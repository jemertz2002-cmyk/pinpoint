package com.cs407.pinpoint.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs407.pinpoint.data.repository.LostItemRepository
import com.cs407.pinpoint.domain.models.LostItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ItemState(
    val error: String? = null,
    val successMsg: String? = null,
    val eventId: Int = 0,
)

class LostItemsViewModel : ViewModel() {

    private val repository = LostItemRepository()

    private val _uiState = MutableStateFlow(ItemState())
    val uiState = _uiState.asStateFlow()

    private val _items = MutableStateFlow<List<LostItem>>(emptyList())
    val items: StateFlow<List<LostItem>> = _items.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadAllItems()
    }

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

    private fun loadAllItems() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                repository.getAllItems().collect { itemsList ->
                    _items.value = itemsList
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load items"
                _isLoading.value = false
            }
        }
    }

    fun filterByLocation(city: String, state: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                repository.getItemsByState(state).collect { itemsList ->
                    val filtered = if (city.isNotBlank()) {
                        itemsList.filter {
                            it.city.trim().equals(city.trim(), ignoreCase = true)
                        }
                    } else {
                        itemsList
                    }
                    _items.value = filtered
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to filter items"
                _isLoading.value = false
            }
        }
    }

    fun refresh() {
        loadAllItems()
    }
}