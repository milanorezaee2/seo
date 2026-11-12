package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.IssueSeverity
import com.example.data.model.MetricRating
import com.example.ui.theme.ScoreAverage
import com.example.ui.theme.ScoreGood
import com.example.ui.theme.ScorePoor

@Composable
fun SeverityBadge(severity: IssueSeverity, modifier: Modifier = Modifier) {
    val (bgColor, textColor, text, icon) = when (severity) {
        IssueSeverity.HIGH -> Quad(ScorePoor.copy(alpha = 0.15f), ScorePoor, "خطای حیاتی", Icons.Default.Error)
        IssueSeverity.MEDIUM -> Quad(ScoreAverage.copy(alpha = 0.15f), ScoreAverage, "هشدار سئو", Icons.Default.Warning)
        IssueSeverity.LOW -> Quad(Color(0xFF60A5FA).copy(alpha = 0.15f), Color(0xFF60A5FA), "بهبود جزئی", Icons.Default.Warning)
        IssueSeverity.GOOD -> Quad(ScoreGood.copy(alpha = 0.15f), ScoreGood, "بهینه و استاندارد", Icons.Default.CheckCircle)
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = textColor, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, style = MaterialTheme.typography.labelSmall.copy(color = textColor, fontWeight = FontWeight.Bold, fontSize = 11.sp))
    }
}

@Composable
fun MetricRatingBadge(rating: MetricRating, modifier: Modifier = Modifier) {
    val (bgColor, textColor, text) = when (rating) {
        MetricRating.GOOD -> Triple(ScoreGood.copy(alpha = 0.15f), ScoreGood, "خوب (Good)")
        MetricRating.NEEDS_IMPROVEMENT -> Triple(ScoreAverage.copy(alpha = 0.15f), ScoreAverage, "نیاز به بهبود")
        MetricRating.POOR -> Triple(ScorePoor.copy(alpha = 0.15f), ScorePoor, "ضعیف (Poor)")
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.labelSmall.copy(color = textColor, fontWeight = FontWeight.Bold, fontSize = 10.sp))
    }
}

@Composable
fun AuditItemCard(
    title: String,
    value: String?,
    severity: IssueSeverity,
    detail: String? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                SeverityBadge(severity = severity)
            }

            if (!value.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    maxLines = 3
                )
            }

            if (!detail.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = detail,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
