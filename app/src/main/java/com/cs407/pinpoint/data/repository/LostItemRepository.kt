package com.cs407.pinpoint.data.repository

import com.cs407.pinpoint.domain.models.LostItem
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LostItemRepository {

    private val lostItemsCollection = Firebase.firestore.collection("lost-items")
    private val auth = Firebase.auth

    /**
     * Helper function to convert Firestore Timestamp or String to formatted date string
     */
    private fun formatDate(dateValue: Any?): String {
        return when (dateValue) {
            is Timestamp -> {
                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                dateFormat.format(dateValue.toDate())
            }
            is String -> dateValue
            else -> ""
        }
    }

    fun submitLostItem(lostItem: LostItem): String? {
        var error: String? = null
        val ownerId = auth.currentUser?.uid
        val userName = auth.currentUser?.displayName ?: "Anonymous"

        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        val newLostItem = hashMapOf(
            "ownerId" to ownerId,
            "itemName" to lostItem.itemName,
            "location" to lostItem.location,
            "description" to lostItem.description,
            "additionalInfo" to lostItem.additionalInfo,
            "city" to lostItem.city,
            "state" to lostItem.state,
            "status" to (lostItem.status.ifBlank { "Lost" }),
            "datePosted" to currentDate,
            "userName" to userName,
            "imageUrl" to lostItem.imageUrl,
            "latitude" to lostItem.latitude,
            "longitude" to lostItem.longitude
        )

        lostItemsCollection
            .add(newLostItem)
            .addOnFailureListener { e ->
                error = "Failed to upload document: ${e.message}"
            }
        return error
    }

    fun getAllItems(): Flow<List<LostItem>> = callbackFlow {
        val listener = lostItemsCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val items = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        LostItem(
                            id = doc.id,
                            ownerId = doc.getString("ownerId") ?: "",
                            itemName = doc.getString("itemName") ?: "",
                            location = doc.getString("location") ?: "",
                            description = doc.getString("description") ?: "",
                            additionalInfo = doc.getString("additionalInfo") ?: "",
                            city = doc.getString("city") ?: "",
                            state = doc.getString("state") ?: "",
                            status = doc.getString("status") ?: "Lost",
                            datePosted = formatDate(doc.get("datePosted")),
                            userName = doc.getString("userName") ?: "",
                            imageUrl = doc.getString("imageUrl") ?: "",
                            storagePath = doc.getString("storagePath") ?: "",
                            latitude = doc.getDouble("latitude") ?: 0.0,
                            longitude = doc.getDouble("longitude") ?: 0.0
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                val sortedItems = items.sortedByDescending { it.datePosted }
                trySend(sortedItems)
            }

        awaitClose { listener.remove() }
    }

    fun getItemsByState(state: String): Flow<List<LostItem>> = callbackFlow {
        val listener = lostItemsCollection
            .whereEqualTo("state", state)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val items = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        LostItem(
                            id = doc.id,
                            ownerId = doc.getString("ownerId") ?: "",
                            itemName = doc.getString("itemName") ?: "",
                            location = doc.getString("location") ?: "",
                            description = doc.getString("description") ?: "",
                            additionalInfo = doc.getString("additionalInfo") ?: "",
                            city = doc.getString("city") ?: "",
                            state = doc.getString("state") ?: "",
                            status = doc.getString("status") ?: "Lost",
                            datePosted = formatDate(doc.get("datePosted")),
                            userName = doc.getString("userName") ?: "",
                            imageUrl = doc.getString("imageUrl") ?: "",
                            storagePath = doc.getString("storagePath") ?: "",
                            latitude = doc.getDouble("latitude") ?: 0.0,
                            longitude = doc.getDouble("longitude") ?: 0.0
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                val sortedItems = items.sortedByDescending { it.datePosted }
                trySend(sortedItems)
            }

        awaitClose { listener.remove() }
    }

    suspend fun getItemById(itemId: String): LostItem? {
        val doc = lostItemsCollection.document(itemId).get().await()
        return try {
            LostItem(
                id = doc.id,
                ownerId = doc.getString("ownerId") ?: "",
                itemName = doc.getString("itemName") ?: "",
                location = doc.getString("location") ?: "",
                description = doc.getString("description") ?: "",
                additionalInfo = doc.getString("additionalInfo") ?: "",
                city = doc.getString("city") ?: "",
                state = doc.getString("state") ?: "",
                status = doc.getString("status") ?: "Lost",
                datePosted = formatDate(doc.get("datePosted")),
                userName = doc.getString("userName") ?: "",
                imageUrl = doc.getString("imageUrl") ?: "",
                storagePath = doc.getString("storagePath") ?: "",
                latitude = doc.getDouble("latitude") ?: 0.0,
                longitude = doc.getDouble("longitude") ?: 0.0
            )
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Get all items for a specific owner (user)
     */
    fun getItemsByOwnerId(ownerId: String): Flow<List<LostItem>> = callbackFlow {
        val listener = lostItemsCollection
            .whereEqualTo("ownerId", ownerId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val items = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        LostItem(
                            id = doc.id,
                            ownerId = doc.getString("ownerId") ?: "",
                            itemName = doc.getString("itemName") ?: "",
                            location = doc.getString("location") ?: "",
                            description = doc.getString("description") ?: "",
                            additionalInfo = doc.getString("additionalInfo") ?: "",
                            city = doc.getString("city") ?: "",
                            state = doc.getString("state") ?: "",
                            status = doc.getString("status") ?: "Lost",
                            datePosted = formatDate(doc.get("datePosted")),
                            userName = doc.getString("userName") ?: "",
                            imageUrl = doc.getString("imageUrl") ?: "",
                            storagePath = doc.getString("storagePath") ?: "",
                            latitude = doc.getDouble("latitude") ?: 0.0,
                            longitude = doc.getDouble("longitude") ?: 0.0
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                val sortedItems = items.sortedByDescending { it.datePosted }
                trySend(sortedItems)
            }

        awaitClose { listener.remove() }
    }

    //Added this function for the Claim/Mark as Found feature
    fun deleteItem(itemId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        lostItemsCollection.document(itemId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e) }
    }

    suspend fun updateItemStatus(itemId: String, newStatus: String) {
        lostItemsCollection
            .document(itemId)
            .update("status", newStatus)
            .await()
    }
    suspend fun updateStatus(itemId: String, status: String): Result<Unit> {
        return try {
            lostItemsCollection
                .document(itemId)
                .update("status", status)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}
