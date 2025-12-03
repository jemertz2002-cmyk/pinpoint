package com.cs407.pinpoint.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs407.pinpoint.data.repository.LostItemRepository
import com.cs407.pinpoint.data.repository.LostItemStorageManager
import com.cs407.pinpoint.domain.models.LostItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Moved this data class here so it can be shared across the Home and User screens
// to avoid duplicate models. Represents a single item post.
data class PinPointItem(
    val id: String,
    val itemName: String,
    val location: String,
    val datePosted: String,
    val user: String, // The username or email of the person who posted it
    val type: String,  // Used to filter between Lost and Found tabs
    val imageUrl: String = "", // Image URL from Firebase Storage
    val city: String = "",
    val state: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

class ItemViewModel : ViewModel() {

    private val storageManager = LostItemStorageManager()
    private val repository = LostItemRepository()

    // Using StateFlow for Reactive UI. The UI observes this list,
    // so whenever _uiState updates, the screen automatically refreshes.
    private val _uiState = MutableStateFlow<List<PinPointItem>>(emptyList())
    val uiState: StateFlow<List<PinPointItem>> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * Load items for the current user by ownerId
     * Converts LostItem to PinPointItem for display
     */
    fun loadItemsForUser(ownerId: String?) {
        if (ownerId == null) return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                repository.getItemsByOwnerId(ownerId).collect { lostItems ->
                    // Convert LostItem to PinPointItem
                    // Note: Currently all items are "Lost" type since we don't have a "Found" status yet
                    val pinPointItems = lostItems.map { lostItem ->
                        PinPointItem(
                            id = lostItem.id,
                            itemName = lostItem.itemName,
                            location = lostItem.location,
                            datePosted = lostItem.datePosted,
                            user = lostItem.userName,
                            type = "Lost", // All items are currently "Lost" type
                            imageUrl = lostItem.imageUrl,
                            city = lostItem.city,
                            state = lostItem.state,
                            latitude = lostItem.latitude,
                            longitude = lostItem.longitude
                        )
                    }
                    _uiState.value = pinPointItems
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load items"
                _isLoading.value = false
            }
        }
    }


    fun markAsFound(itemId: String) {
        //  Remove it from the screen immediately
        val currentList = _uiState.value
        _uiState.value = currentList.filter { it.id != itemId }

        viewModelScope.launch {
            // TODO: Add database call here to update item status in Firestore
        }
    }

    /**
     * Delete item and its image from Firebase Storage and Firestore
     */
    fun deletePost(itemId: String) {
        // Update UI immediately
        _uiState.value = _uiState.value.filter { it.id != itemId }

        viewModelScope.launch {
            val result = storageManager.deleteItem(itemId)
            result.fold(
                onSuccess = {
                    // Success - item already removed from UI
                    _error.value = null
                },
                onFailure = { exception ->
                    // Revert UI change on error
                    // Note: In a real app, you'd want to reload from database
                    _error.value = exception.message ?: "Failed to delete item"
                }
            )
        }
    }
}