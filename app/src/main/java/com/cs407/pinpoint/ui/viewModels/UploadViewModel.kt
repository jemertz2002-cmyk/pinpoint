package com.cs407.pinpoint.ui.viewModels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs407.pinpoint.data.repository.LostItemStorageManager
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ItemState(
    val error: String? = null,
    val successMsg: String? = null,
    val eventId: Int = 0,
    val isUploading: Boolean = false
)

class UploadViewModel : ViewModel() {

    private val storageManager = LostItemStorageManager()

    private val _uiState = MutableStateFlow(ItemState())
    val uiState = _uiState.asStateFlow()

    private val _selectedLocation = MutableStateFlow<LatLng?>(null)
    val selectedLocation = _selectedLocation.asStateFlow()

    /**
     * Submits a lost item, uploading the image first if provided
     * Uses LostItemStorageManager to handle image upload and Firestore document creation
     */
    fun submitLostItem(
        imageUri: Uri?,
        itemName: String,
        description: String,
        city: String,
        state: String,
        location: String,
        latitude: Double,
        longitude: Double,
        contactInfo: String,
    ) {
        // Validate required fields
        if (itemName.isBlank() ||
            location.isBlank() ||
            description.isBlank() ||
            city.isBlank() ||
            state.isBlank()
        ) {
            _uiState.value = _uiState.value.copy(
                successMsg = null,
                error = "Make sure all fields are filled out!",
                isUploading = false,
                eventId = _uiState.value.eventId + 1
            )
            return
        }

        // Require image
        if (imageUri == null) {
            _uiState.value = _uiState.value.copy(
                successMsg = null,
                error = "Please take a photo of the item!",
                isUploading = false,
                eventId = _uiState.value.eventId + 1
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isUploading = true,
                error = null,
                successMsg = null
            )

            try {
                // Use LostItemStorageManager to create item (handles image upload and Firestore)
                val result = storageManager.createLostItem(
                    imageUri = imageUri,
                    itemName = itemName,
                    description = description,
                    city = city,
                    state = state,
                    location = location,
                    latitude = latitude,
                    longitude = longitude,
                    contactInfo = contactInfo
                )

                result.fold(
                    onSuccess = { _ ->
                        _uiState.value = _uiState.value.copy(
                            isUploading = false,
                            successMsg = "Successfully created lost item!",
                            error = null,
                            eventId = _uiState.value.eventId + 1
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isUploading = false,
                            error = exception.message ?: "Failed to create lost item",
                            successMsg = null,
                            eventId = _uiState.value.eventId + 1
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isUploading = false,
                    error = "An error occurred: ${e.message}",
                    successMsg = null,
                    eventId = _uiState.value.eventId + 1
                )
            }
        }
    }

    fun onMapClick(latLng: LatLng) {
        _selectedLocation.value = latLng
    }
}
