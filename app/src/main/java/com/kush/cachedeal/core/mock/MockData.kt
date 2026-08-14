package com.kush.cachedeal.core.mock

import com.google.firebase.Timestamp
import com.kush.cachedeal.core.model.Category
import com.kush.cachedeal.core.model.Deal
import com.kush.cachedeal.core.model.Item
import com.kush.cachedeal.core.model.Offer
import com.kush.cachedeal.core.model.User
import java.util.Date

object MockData {

    val currentUser = User(
        uid = "user_kush",
        phone = "+919876543210",
        name = "Kush Agheera",
        block = "Men's Hostel G",
        greenDots = 5,
        redDots = 1
    )

    val users = listOf(
        currentUser,
        User(uid = "u2", phone = "+919876500001", name = "Arjun Mehta", block = "Men's Hostel A", greenDots = 8, redDots = 0),
        User(uid = "u3", phone = "+919876500002", name = "Priya Sharma", block = "Ladies' Hostel C", greenDots = 3, redDots = 2),
        User(uid = "u4", phone = "+919876500003", name = "Rahul Nair", block = "Men's Hostel B", greenDots = 12, redDots = 1),
        User(uid = "u5", phone = "+919876500004", name = "Sneha Patel", block = "Ladies' Hostel A", greenDots = 6, redDots = 0),
        User(uid = "u6", phone = "+919876500005", name = "Dev Kapoor", block = "Men's Hostel H", greenDots = 2, redDots = 3),
    )

    val items = listOf(
        Item(
            id = "item1",
            sellerId = "u2",
            sellerName = "Arjun Mehta",
            sellerBlock = "Men's Hostel A",
            sellerGreenDots = 8,
            sellerRedDots = 0,
            category = Category.CYCLES.displayName,
            title = "Hero Sprint Pro Cycle — Great Condition",
            description = "Used for 1 year, tyres recently changed. Works perfectly fine. Selling because I'm graduating. Come check it out at Hostel A.",
            price = 2800.0,
            photoUrl = "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400",
            status = "open"
        ),
        Item(
            id = "item2",
            sellerId = "u3",
            sellerName = "Priya Sharma",
            sellerBlock = "Ladies' Hostel C",
            sellerGreenDots = 3,
            sellerRedDots = 2,
            category = Category.CALCULATORS.displayName,
            title = "Casio FX-991EX Classwiz",
            description = "Bought last semester, barely used. All functions work. Solar + battery powered. Perfect for exams.",
            price = 750.0,
            photoUrl = "https://images.unsplash.com/photo-1611532736597-de2d4265fba3?w=400",
            status = "open"
        ),
        Item(
            id = "item3",
            sellerId = "u4",
            sellerName = "Rahul Nair",
            sellerBlock = "Men's Hostel B",
            sellerGreenDots = 12,
            sellerRedDots = 1,
            category = Category.SUBSCRIPTIONS.displayName,
            title = "Spotify Premium — 3 Months Left",
            description = "Transferring my Spotify account with 3 months premium remaining. Family plan slot available.",
            price = 180.0,
            photoUrl = "https://images.unsplash.com/photo-1614680376573-df3480f0c6ff?w=400",
            status = "open"
        ),
        Item(
            id = "item4",
            sellerId = "u5",
            sellerName = "Sneha Patel",
            sellerBlock = "Ladies' Hostel A",
            sellerGreenDots = 6,
            sellerRedDots = 0,
            category = Category.LAB_COATS.displayName,
            title = "White Lab Coat — Size M",
            description = "Used for 2 semesters, washed and clean. Size M. Perfect for biotech/chemistry labs.",
            price = 200.0,
            photoUrl = "https://images.unsplash.com/photo-1576091160550-2173dba999ef?w=400",
            status = "open"
        ),
        Item(
            id = "item5",
            sellerId = "u6",
            sellerName = "Dev Kapoor",
            sellerBlock = "Men's Hostel H",
            sellerGreenDots = 2,
            sellerRedDots = 3,
            category = Category.STUDY_NOTES.displayName,
            title = "Data Structures Handwritten Notes",
            description = "Complete DSA notes covering arrays, trees, graphs, DP. Written by 9.1 CGPA student. Very organized.",
            price = 120.0,
            photoUrl = "https://images.unsplash.com/photo-1456735190827-d1262f71b8a3?w=400",
            status = "open"
        ),
        Item(
            id = "item6",
            sellerId = "u2",
            sellerName = "Arjun Mehta",
            sellerBlock = "Men's Hostel A",
            sellerGreenDots = 8,
            sellerRedDots = 0,
            category = Category.GAME_ACCOUNTS.displayName,
            title = "BGMI Account — Ace Tier",
            description = "Ace tier BGMI account with rare skins. 3000+ matches played. Selling because I'm done gaming.",
            price = 500.0,
            photoUrl = "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=400",
            status = "locked"
        ),
        Item(
            id = "item7",
            sellerId = "u3",
            sellerName = "Priya Sharma",
            sellerBlock = "Ladies' Hostel C",
            sellerGreenDots = 3,
            sellerRedDots = 2,
            category = Category.EATABLES.displayName,
            title = "Maggi Masala Box — 12 Packets",
            description = "Bought in bulk, leaving campus early. Full sealed box. Best before 6 months from now.",
            price = 110.0,
            photoUrl = "https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=400",
            status = "open"
        ),
        Item(
            id = "item8",
            sellerId = "u4",
            sellerName = "Rahul Nair",
            sellerBlock = "Men's Hostel B",
            sellerGreenDots = 12,
            sellerRedDots = 1,
            category = Category.WEARABLES.displayName,
            title = "Campus Backpack — Wildcraft",
            description = "Wildcraft 45L backpack. Used 2 semesters. All zips work. Spacious laptop compartment. Minor scuff on bottom.",
            price = 650.0,
            photoUrl = "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=400",
            status = "open"
        ),
        Item(
            id = "item9",
            sellerId = "user_kush",
            sellerName = "Kush Agheera",
            sellerBlock = "Men's Hostel G",
            sellerGreenDots = 5,
            sellerRedDots = 1,
            category = Category.CALCULATORS.displayName,
            title = "Casio FX-82MS Scientific",
            description = "My old calculator. Works perfectly. Selling since I upgraded to 991EX.",
            price = 350.0,
            photoUrl = "https://images.unsplash.com/photo-1611532736597-de2d4265fba3?w=400",
            status = "open"
        ),
        Item(
            id = "item10",
            sellerId = "user_kush",
            sellerName = "Kush Agheera",
            sellerBlock = "Men's Hostel G",
            sellerGreenDots = 5,
            sellerRedDots = 1,
            category = Category.STUDY_NOTES.displayName,
            title = "Operating Systems Notes — Complete",
            description = "Detailed OS notes with diagrams. Covers scheduling, memory management, file systems. Useful for GATE prep too.",
            price = 80.0,
            photoUrl = "https://images.unsplash.com/photo-1456735190827-d1262f71b8a3?w=400",
            status = "sold"
        ),
    )

    val myItems = items.filter { it.sellerId == "user_kush" }

    val offersForItem1 = listOf(
        Offer(
            id = "offer1",
            buyerId = "u3",
            buyerName = "Priya Sharma",
            buyerGreenDots = 3,
            buyerRedDots = 2,
            buyerPhone = "+919876500002",
            amount = 2600.0,
            note = "Can pay by evening. I'm in Ladies C.",
            status = "pending"
        ),
        Offer(
            id = "offer2",
            buyerId = "u4",
            buyerName = "Rahul Nair",
            buyerGreenDots = 12,
            buyerRedDots = 1,
            buyerPhone = "+919876500003",
            amount = 2500.0,
            note = "Ready right now. Cash in hand.",
            status = "pending"
        ),
        Offer(
            id = "offer3",
            buyerId = "u6",
            buyerName = "Dev Kapoor",
            buyerGreenDots = 2,
            buyerRedDots = 3,
            buyerPhone = "+919876500005",
            amount = 2200.0,
            note = null,
            status = "pending"
        ),
    )

    val activeDeals = listOf(
        Deal(
            id = "deal1",
            itemId = "item6",
            itemTitle = "BGMI Account — Ace Tier",
            itemPhotoUrl = "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=400",
            sellerId = "u2",
            sellerName = "Arjun Mehta",
            sellerPhone = "+919876500001",
            buyerId = "user_kush",
            buyerName = "Kush Agheera",
            buyerPhone = "+919876543210",
            finalPrice = 480.0,
            lockedAt = Timestamp(Date(System.currentTimeMillis() - 86400000L)),
            completionDeadline = Timestamp(Date(System.currentTimeMillis() + 2 * 86400000L)),
            status = "locked"
        )
    )
}
