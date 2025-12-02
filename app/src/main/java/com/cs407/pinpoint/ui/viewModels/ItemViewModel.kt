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

    // Handles the logic when a user deletes a post or marks it as found.
    fun markAsFound(itemId: String) {
        // Instantly removes the item from the screen so the app feels responsive,
        // even before the database finishes processing.
        _uiState.value = _uiState.value.filter { it.id != itemId }

        viewModelScope.launch {
            // Future TODO: Will add the specific repository call here to delete
            // the item from Firebase permanently once the function is available in the repo.
        }
    }

    // Reuses logic since deleting and marking as found currently have the same
    // visual result on the user profile.
    fun deletePost(itemId: String) {
        markAsFound(itemId)
    }
}