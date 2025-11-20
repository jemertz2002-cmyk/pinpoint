package com.cs407.pinpoint.data.repository

import com.cs407.pinpoint.domain.models.LostItem
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class LostItemsRepository {

    private val lostItemsCollection = Firebase.firestore.collection("lost-items")

    fun submitLostItem(lostItem: LostItem): String? {
        var error: String? = null
        val ownerId = Firebase.auth.currentUser?.uid
        val newLostItem = hashMapOf(
            "ownerId" to ownerId,
            "itemName" to lostItem.itemName,
            "location" to lostItem.location,
            "description" to lostItem.description,
            "additionalInfo" to lostItem.additionalInfo
        )
        lostItemsCollection
            .add(newLostItem)
            .addOnFailureListener { e ->
                error = "Failed to upload document!"
            }
        return error
    }
}