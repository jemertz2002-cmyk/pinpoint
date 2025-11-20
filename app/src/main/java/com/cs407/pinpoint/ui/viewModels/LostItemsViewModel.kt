package com.cs407.pinpoint.ui.viewModels

import androidx.lifecycle.ViewModel
import com.cs407.pinpoint.data.repository.LostItemsRepository
import com.cs407.pinpoint.domain.models.LostItem
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ItemState(
    val error: String? = null,
    val successMsg: String? = null,
    val eventId: Int = 0,
)

class LostItemsViewModel: ViewModel() {

    private val repository = LostItemsRepository()
    private val _uiState = MutableStateFlow(ItemState())
    val uiState = _uiState.asStateFlow()

    fun submitLostItem(lostItem: LostItem) {
        if (lostItem.itemName == null ||
            lostItem.location == null ||
            lostItem.description == null ||
            lostItem.additionalInfo == null) {
            _uiState.value = _uiState.value.copy(successMsg = null)
            _uiState.value = _uiState.value.copy(error = "Make sure all fields are filled out!")
            _uiState.value = _uiState.value.copy(eventId = _uiState.value.eventId + 1)
        }
        else {
            _uiState.value = _uiState.value.copy(successMsg = null)
            _uiState.value = _uiState.value.copy(error = repository.submitLostItem(lostItem))
            if (_uiState.value.error == null) {
                _uiState.value = _uiState.value.copy(successMsg = "Successfully created lost item!")
            }
            _uiState.value = _uiState.value.copy(eventId = _uiState.value.eventId + 1)
        }
    }
}