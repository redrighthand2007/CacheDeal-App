package com.kush.cachedeal.core.model

import com.google.firebase.Timestamp

data class Deal(
    val id: String = "",
    val itemId: String = "",
    val itemTitle: String = "",
    val itemPhotoUrl: String = "",
    val sellerId: String = "",
    val sellerName: String = "",
    val sellerPhone: String = "",
    val buyerId: String = "",
    val buyerName: String = "",
    val buyerPhone: String = "",
    val finalPrice: Double = 0.0,
    val lockedAt: Timestamp = Timestamp.now(),
    val completionDeadline: Timestamp = Timestamp.now(),
    val status: String = "locked", // locked / completed / expired
    val completedAt: Timestamp? = null
)
