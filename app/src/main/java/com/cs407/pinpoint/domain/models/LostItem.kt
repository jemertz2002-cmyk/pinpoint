package com.cs407.pinpoint.domain.models

data class LostItem(
    val id: String = "",
    val ownerId: String = "",
    val itemName: String = "",
    val location: String = "",
    val description: String = "",
    val additionalInfo: String = "",
    val city: String = "",
    val state: String = "",
    val datePosted: String = "",
    val userName: String = "",
    val imageUrl: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
) {
    // No-argument constructor for Firebase
    constructor() : this("", "", "", "", "", "", "", "", "", "", "", 0.0, 0.0)
}