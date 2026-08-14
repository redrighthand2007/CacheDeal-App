package com.kush.cachedeal.core.util

object Constants {

    // Firestore Collections
    const val COLLECTION_USERS = "users"
    const val COLLECTION_ITEMS = "items"
    const val COLLECTION_OFFERS = "offers"
    const val COLLECTION_DEALS = "deals"

    // Item Statuses
    const val STATUS_OPEN = "open"
    const val STATUS_LOCKED = "locked"
    const val STATUS_SOLD = "sold"
    const val STATUS_EXPIRED = "expired"

    // Offer Statuses
    const val STATUS_PENDING = "pending"
    const val STATUS_ACCEPTED = "accepted"
    const val STATUS_REJECTED = "rejected"

    // Deal Window (in days)
    const val DEAL_COMPLETION_DAYS = 3L

    // VIT Vellore Hostel Blocks
    val HOSTEL_BLOCKS = listOf(
        // Men's Hostels
        "Men's Hostel A", "Men's Hostel B", "Men's Hostel C", "Men's Hostel D",
        "Men's Hostel E", "Men's Hostel F", "Men's Hostel G", "Men's Hostel H",
        "Men's Hostel I", "Men's Hostel J", "Men's Hostel K", "Men's Hostel L",
        "Men's Hostel M", "Men's Hostel N", "Men's Hostel O", "Men's Hostel P",
        "Men's Hostel Q", "Men's Hostel R",
        // Ladies' Hostels
        "Ladies' Hostel A", "Ladies' Hostel B", "Ladies' Hostel C", "Ladies' Hostel D",
        "Ladies' Hostel E", "Ladies' Hostel F", "Ladies' Hostel G", "Ladies' Hostel H",
        "Ladies' Hostel I", "Ladies' Hostel J",
        // Other
        "Day Scholar", "Off Campus"
    )
}
