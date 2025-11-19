package com.cs407.pinpoint.ui.viewModels

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class LostItemsViewModel: ViewModel() {

    private val lostItemsCollection = Firebase.firestore.collection("lost-items")

    fun submitLostItem(itemName: String?, location: String?, description: String?, additionalInfo: String?): String? {
        if (itemName == null || location == null || description == null || additionalInfo == null) {
            return "Make sure to fill out all fields!"
        }
        var error: String? = null
        val ownerId = Firebase.auth.currentUser?.uid
        val newLostItem = hashMapOf(
            "ownerId" to ownerId,
            "itemName" to itemName,
            "location" to location,
            "description" to description,
            "additionalInfo" to additionalInfo
        )
        lostItemsCollection
            .add(newLostItem)
            .addOnFailureListener { e ->
                error = "Failed to upload document!"
            }
        return error
    }
}