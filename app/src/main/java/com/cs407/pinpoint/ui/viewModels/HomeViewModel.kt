package com.cs407.pinpoint.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs407.pinpoint.data.repository.LostItemRepository
import com.cs407.pinpoint.domain.models.LostItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repository = LostItemRepository()

    private val _items = MutableStateFlow<List<LostItem>>(emptyList())
    val items: StateFlow<List<LostItem>> = _items.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadAllItems()
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