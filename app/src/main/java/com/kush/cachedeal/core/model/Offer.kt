package com.kush.cachedeal.core.model

import com.google.firebase.Timestamp

data class Offer(
    val id: String = "",
    val buyerId: String = "",
    val buyerName: String = "",
    val buyerGreenDots: Int = 0,
    val buyerRedDots: Int = 0,
    val buyerPhone: String = "",
    val amount: Double = 0.0,
    val note: String? = null,
    val status: String = "pending", // pending / accepted / rejected
    val createdAt: Timestamp = Timestamp.now()
)
