package com.cs407.pinpoint.data.repository

import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class LostItemStorageManager {

    private val storage = Firebase.storage
    private val firestore = Firebase.firestore
    private val auth = Firebase.auth

    /**
     * Upload image and create Firestore document
     */
    suspend fun createLostItem(
        imageUri: Uri,
        itemName: String,
        description: String,
        city: String,
        state: String,
        location: String,
        latitude: Double,
        longitude: Double,
        contactInfo: String
    ): Result<String> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.failure(Exception("User not authenticated"))

            val itemId = UUID.randomUUID().toString()
            val timestamp = System.currentTimeMillis()
            val filename = "image_$timestamp.jpg"

            // Upload to Storage: /users/{userId}/lost-items/{itemId}/{filename}
            val storageRef = storage.reference
                .child("users/$userId/lost-items/$itemId/$filename")

            val uploadTask = storageRef.putFile(imageUri).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()

            // Create Firestore document
            val lostItemData = hashMapOf(
                "itemName" to itemName,
                "description" to description,
                "city" to city,
                "state" to state,
                "location" to location,
                "latitude" to latitude,
                "longitude" to longitude,
                "imageUrl" to downloadUrl,
                "storagePath" to storageRef.path, // Store path for deletion
                "ownerId" to userId,
                "userName" to (auth.currentUser?.displayName ?: "Unknown"),
                "datePosted" to Timestamp.now(),
                "additionalInfo" to "",
                "status" to "Lost",
                "contactInfo" to contactInfo
            )

            firestore.collection("lost-items")
                .document(itemId)
                .set(lostItemData)
                .await()

            Result.success(itemId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Update item image (deletes old, uploads new)
     */
    suspend fun updateItemImage(
        itemId: String,
        newImageUri: Uri
    ): Result<String> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.failure(Exception("User not authenticated"))

            // Get existing document to find old image
            val doc = firestore.collection("lost-items")
                .document(itemId)
                .get()
                .await()

            // Verify ownership
            if (doc.getString("ownerId") != userId) {
                return Result.failure(Exception("Unauthorized"))
            }

            // Delete old image if exists
            val oldStoragePath = doc.getString("storagePath")
            if (oldStoragePath != null) {
                storage.reference.child(oldStoragePath).delete().await()
            }

            // Upload new image
            val timestamp = System.currentTimeMillis()
            val filename = "image_$timestamp.jpg"
            val storageRef = storage.reference
                .child("users/$userId/lost-items/$itemId/$filename")

            storageRef.putFile(newImageUri).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()

            // Update Firestore
            firestore.collection("lost-items")
                .document(itemId)
                .update(
                    mapOf(
                        "imageUrl" to downloadUrl,
                        "storagePath" to storageRef.path
                    )
                )
                .await()

            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Delete item and its image
     */
    suspend fun deleteItem(itemId: String): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.failure(Exception("User not authenticated"))

            // Get document
            val doc = firestore.collection("lost-items")
                .document(itemId)
                .get()
                .await()

            // Verify ownership
            if (doc.getString("ownerId") != userId) {
                return Result.failure(Exception("Unauthorized"))
            }

            // Delete image from Storage
            val storagePath = doc.getString("storagePath")
            if (storagePath != null) {
                storage.reference.child(storagePath).delete().await()
            }

            // Delete Firestore document
            firestore.collection("lost-items")
                .document(itemId)
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get all items for current user
     */
    suspend fun getUserItems(): Result<List<Map<String, Any>>> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.failure(Exception("User not authenticated"))

            val snapshot = firestore.collection("lost-items")
                .whereEqualTo("ownerId", userId)
                .get()
                .await()

            val items = snapshot.documents.map { doc ->
                doc.data?.plus("id" to doc.id) ?: emptyMap()
            }

            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

