package com.cs407.pinpoint.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
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
    val type: String  // Used to filter between Lost and Found tabs
)

class ItemViewModel : ViewModel() {

    // Using StateFlow for Reactive UI. The UI observes this list,
    // so whenever _uiState updates, the screen automatically refreshes.
    private val _uiState = MutableStateFlow<List<PinPointItem>>(emptyList())
    val uiState: StateFlow<List<PinPointItem>> = _uiState.asStateFlow()

    // Simulates fetching data for the specific logged-in user.
    // Uses userEmail to only show posts belonging to the profile being viewed.
    fun loadItemsForUser(userEmail: String?) {
        if (userEmail == null) return

        viewModelScope.launch {
            // TODO: Replace with Supabase API call.
            // Using hardcoded list for now to test UI and Navigation logic.
            delay(500) // Simulating network latency

            val fetchedItems = listOf(
                PinPointItem("1", "Blue HydroFlask", "College Library", "Nov 12, 2025", userEmail, "Lost"),
                PinPointItem("2", "AirPods Pro Case", "Union South", "Nov 11, 2025", userEmail, "Found"),
                PinPointItem("3", "WiscCard", "Bascom Hill", "Nov 10, 2025", userEmail, "Lost"),
                PinPointItem("4", "Calculator", "Engineering Hall", "Nov 18, 2025", userEmail, "Found")
            )

            _uiState.value = fetchedItems
        }
    }

    // Logic for marking an item as found.
    // Updates local state to remove it from the list instantly.
    fun markAsFound(itemId: String) {
        // Filtering the list triggers a UI recomposition
        _uiState.value = _uiState.value.filter { it.id != itemId }

        viewModelScope.launch {
            // TODO: Add database call here to delete/update item in Supabase
        }
    }

    // Logic to delete a post entirely.
    fun deletePost(itemId: String) {
        // Reusing markAsFound logic for the prototype phase
        markAsFound(itemId)
    }
}