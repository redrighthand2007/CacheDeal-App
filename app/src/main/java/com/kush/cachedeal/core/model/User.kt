package com.kush.cachedeal.core.model

import com.google.firebase.Timestamp

data class User(
    val uid: String = "",
    val phone: String = "",
    val name: String = "",
    val block: String = "",
    val greenDots: Int = 0,
    val redDots: Int = 0,
    val createdAt: Timestamp = Timestamp.now()
)
