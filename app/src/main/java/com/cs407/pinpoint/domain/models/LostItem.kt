package com.cs407.pinpoint.domain.models

data class LostItem(
    val ownerId: String? = null,
    val itemName: String? = null,
    val location: String? = null,
    val description: String? = null,
    val additionalInfo: String? = null
)