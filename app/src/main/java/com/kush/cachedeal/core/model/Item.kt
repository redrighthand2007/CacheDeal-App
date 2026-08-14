package com.kush.cachedeal.core.model

import com.google.firebase.Timestamp

data class Item(
    val id: String = "",
    val sellerId: String = "",
    val sellerName: String = "",
    val sellerBlock: String = "",
    val sellerGreenDots: Int = 0,
    val sellerRedDots: Int = 0,
    val category: String = "",
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val photoUrl: String = "",
    val status: String = "open", // open / locked / sold
    val createdAt: Timestamp = Timestamp.now()
)
