package com.kush.cachedeal.core.util

import android.content.Context
import android.content.Intent
import android.net.Uri

object WhatsAppHelper {

    /**
     * Opens WhatsApp with a pre-filled message to the given phone number.
     * Falls back to browser if WhatsApp is not installed.
     *
     * @param context Android context
     * @param phone   E.164 format phone number (e.g. +919876543210)
     * @param message Pre-filled message text
     */
    fun openChat(context: Context, phone: String, message: String) {
        val sanitized = phone.replace(Regex("[^\\d+]"), "")
        val encoded = Uri.encode(message)
        val waUri = Uri.parse("https://wa.me/$sanitized?text=$encoded")

        val intent = Intent(Intent.ACTION_VIEW, waUri).apply {
            setPackage("com.whatsapp")
        }

        // Try WhatsApp directly first, fall back to browser
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            context.startActivity(Intent(Intent.ACTION_VIEW, waUri))
        }
    }

    /**
     * Builds the standard post-deal message for CacheDeal.
     */
    fun buildDealMessage(
        itemTitle: String,
        finalPrice: Double,
        otherPartyName: String
    ): String {
        return "Hey $otherPartyName! 👋 I'm contacting you through CacheDeal about our locked deal for \"$itemTitle\" at ₹${"%.0f".format(finalPrice)}. Let's meet up to complete the handoff! 🤝"
    }
}
