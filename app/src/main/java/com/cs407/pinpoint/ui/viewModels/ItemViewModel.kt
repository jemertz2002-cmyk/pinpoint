package com.cs407.pinpoint.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs407.pinpoint.data.repository.LostItemRepository
import com.cs407.pinpoint.domain.models.LostItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ItemViewModel : ViewModel() {

    // Initialized the repository here. This is the bridge to the Firebase database
    // set up to fetch actual data.
    private val repository = LostItemRepository()

    // Uses StateFlow to hold the list of items.
    private val _uiState = MutableStateFlow<List<LostItem>>(emptyList())
    val uiState: StateFlow<List<LostItem>> = _uiState.asStateFlow()

    // Loads real data from the database.
    // Accepts userId to filter the list and only show items belonging to the current user.
    fun loadItemsForUser(userId: String?) {
        if (userId == null) return

        viewModelScope.launch {
            // Calls the repository to get the stream of items.
            // Uses .collect because it's a Flow—if the database changes in real-time,
            // this block triggers automatically to update the app.
            repository.getAllItems().collect { allItems ->

                // Filters logic used here to ensure My Profile page
                // only displays items that match the logged-in user's ID.
                val userItems = allItems.filter { it.ownerId == userId }

                // Updates the state, instantly refreshing the screen.
                _uiState.value = userItems
            }
        }
    }


    fun markAsFound(itemId: String) {
        //  Remove it from the screen immediately
        val currentList = _uiState.value
        _uiState.value = currentList.filter { it.id != itemId }

        // Permanently delete it from Firebase
        // Claim action—it removes the lost item record.
        repository.deleteItem(
            itemId = itemId,
            onSuccess = {
                // It's gone from the database.
            },
            onFailure = { e ->
                // If it fails, put the item back in the list
                _uiState.value = currentList
            }
        )
    }

    // Reuses logic since deleting and marking as found currently have the same
    // visual result on the user profile.
    fun deletePost(itemId: String) {
        markAsFound(itemId)
    }
}