package com.cs407.pinpoint.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs407.pinpoint.data.repository.LostItemRepository
import com.cs407.pinpoint.domain.models.LostItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ItemPageViewModel : ViewModel() {

    private val repository = LostItemRepository()

    private val _item = MutableStateFlow<LostItem?>(null)
    val item: StateFlow<LostItem?> = _item.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadItem(itemId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val loadedItem = repository.getItemById(itemId)
                if (loadedItem != null) {
                    _item.value = loadedItem
                } else {
                    _error.value = "Item not found"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load item"
            } finally {
                _isLoading.value = false
            }
        }
    }
}

