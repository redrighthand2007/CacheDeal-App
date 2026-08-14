package com.kush.cachedeal.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kush.cachedeal.core.model.Offer

@Composable
fun OfferCard(
    offer: Offer,
    onAccept: (() -> Unit)? = null,
    isAcceptLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = when (offer.status) {
                "accepted" -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                "rejected" -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Buyer info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = offer.buyerName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(2.dp))
                    DotBadge(
                        greenDots = offer.buyerGreenDots,
                        redDots = offer.buyerRedDots
                    )
                }
                Spacer(Modifier.width(12.dp))
                // Offer amount
                Text(
                    text = "₹${"%.0f".format(offer.amount)}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (!offer.note.isNullOrBlank()) {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "\"${offer.note}\"",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (onAccept != null && offer.status == "pending") {
                Spacer(Modifier.height(12.dp))
                DealButton(
                    text = "Accept This Offer",
                    onClick = onAccept,
                    isLoading = isAcceptLoading
                )
            }

            if (offer.status == "accepted") {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "✅ Accepted",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
