package com.cs407.pinpoint.ui.viewModels

import androidx.lifecycle.ViewModel
import com.cs407.pinpoint.data.repository.LostItemRepository
import com.cs407.pinpoint.domain.models.LostItem
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ItemState(
    val error: String? = null,
    val successMsg: String? = null,
    val eventId: Int = 0,
)

class UploadViewModel : ViewModel() {

    private val repository = LostItemRepository()

    private val _uiState = MutableStateFlow(ItemState())
    val uiState = _uiState.asStateFlow()

    private val _selectedLocation = MutableStateFlow<LatLng?>(null)
    val selectedLocation = _selectedLocation.asStateFlow()

    fun submitLostItem(lostItem: LostItem) {
        if (lostItem.itemName.isBlank() ||
            lostItem.location.isBlank() ||
            lostItem.description.isBlank() ||
            lostItem.city.isBlank() ||
            lostItem.state.isBlank()
        ) {
            _uiState.value = _uiState.value.copy(successMsg = null)
            _uiState.value = _uiState.value.copy(error = "Make sure all fields are filled out!")
            _uiState.value = _uiState.value.copy(eventId = _uiState.value.eventId + 1)
        } else {
            _uiState.value = _uiState.value.copy(successMsg = null)
            _uiState.value = _uiState.value.copy(error = repository.submitLostItem(lostItem))
            if (_uiState.value.error == null) {
                _uiState.value = _uiState.value.copy(successMsg = "Successfully created lost item!")
            }
            _uiState.value = _uiState.value.copy(eventId = _uiState.value.eventId + 1)
        }
    }

    fun onMapClick(latLng: LatLng) {
        _selectedLocation.value = latLng
    }
}