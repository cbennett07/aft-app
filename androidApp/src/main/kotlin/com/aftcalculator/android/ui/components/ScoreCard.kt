package com.aftcalculator.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aftcalculator.android.ui.theme.ArmyGold
import com.aftcalculator.android.ui.theme.PassGreen
import com.aftcalculator.android.ui.theme.FailRed
import com.aftcalculator.android.ui.theme.WarningAmber
import com.aftcalculator.android.viewmodels.CalculatorViewModel
import com.aftcalculator.models.AftEvent
import com.aftcalculator.models.AftScore
import com.aftcalculator.models.EventScore

@Composable
fun ScoreResultCard(
    score: AftScore,
    modifier: Modifier = Modifier
) {
    // Check if any event is below 60 (standard passing threshold)
    val anyEventFailed = score.eventScores.any { it.points < 60 }
    // Minimum stays at full value; exempt events contribute 60 pts to total
    val minimumTotal = score.soldier.mosCategory.minimumTotal
    val totalFailed = score.totalPoints < minimumTotal
    val overallPassed = !anyEventFailed && !totalFailed

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = if (overallPassed) PassGreen else FailRed,
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Pass/Fail Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (overallPassed) PassGreen else FailRed)
                    .padding(horizontal = 32.dp, vertical = 12.dp)
            ) {
                Text(
                    text = if (overallPassed) "PASS" else "FAIL",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = 2.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Total Score Box
            val totalScoreColor = if (overallPassed) PassGreen else FailRed
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(totalScoreColor.copy(alpha = 0.2f))
                    .border(2.dp, totalScoreColor, RoundedCornerShape(12.dp))
                    .padding(horizontal = 32.dp, vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = score.totalPoints.toString(),
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Black,
                        color = totalScoreColor
                    )
                    Text(
                        text = "TOTAL POINTS",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (score.eventScores.size < 5) {
                    "Minimum required: $minimumTotal (${5 - score.eventScores.size} exempt @ 60 pts)"
                } else {
                    "Minimum required: $minimumTotal"
                },
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.5f)
            )

            // ABC Exemption
            val abcExempt = score.totalPoints > 465 && score.eventScores.all { it.points >= 80 }
            if (abcExempt) {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(PassGreen.copy(alpha = 0.2f))
                        .border(1.dp, PassGreen, RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Soldier exempt from ABC standards!",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = PassGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

            Spacer(modifier = Modifier.height(16.dp))

            // Event Breakdown
            Text(
                text = "EVENT BREAKDOWN",
                style = MaterialTheme.typography.labelMedium,
                color = ArmyGold,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Show all 5 standard events - exempt ones greyed out
            val standardEvents = listOf(
                AftEvent.DEADLIFT,
                AftEvent.PUSH_UP,
                AftEvent.SPRINT_DRAG_CARRY,
                AftEvent.PLANK,
                AftEvent.TWO_MILE_RUN
            )
            val scoresByEvent = score.eventScores.associateBy { it.event }

            standardEvents.forEach { event ->
                val eventScore = scoresByEvent[event]
                if (eventScore != null) {
                    EventScoreRow(eventScore = eventScore, minRequired = 60)
                } else {
                    // Check if an alternate aerobic event was used instead of 2MR
                    val alternateScore = if (event == AftEvent.TWO_MILE_RUN) {
                        score.eventScores.firstOrNull { it.event.isAlternateAerobic }
                    } else null

                    if (alternateScore != null) {
                        EventScoreRow(eventScore = alternateScore, minRequired = 60)
                    } else {
                        ExemptEventScoreRow(eventName = event.displayName)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Failure Reasons
            if (score.failureReasons.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "AREAS FOR IMPROVEMENT",
                    style = MaterialTheme.typography.labelMedium,
                    color = FailRed,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                score.failureReasons.forEach { reason ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.bodySmall,
                            color = FailRed,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = reason,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
private fun EventScoreRow(
    eventScore: EventScore,
    minRequired: Int,
    modifier: Modifier = Modifier
) {
    val scoreStatus = getScoreStatus(eventScore, minRequired, false)
    val scoreColor = getScoreColor(scoreStatus)
    val scoreBgColor = getScoreBackgroundColor(scoreStatus)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.03f))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = eventScore.event.displayName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            Text(
                text = formatRawValue(eventScore),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.5f)
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (minRequired > 0) {
                Text(
                    text = "min $minRequired",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.4f),
                    modifier = Modifier.padding(end = 12.dp)
                )
            }

            Box(
                modifier = Modifier
                    .width(64.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(scoreBgColor)
                    .border(1.dp, scoreColor, RoundedCornerShape(6.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${eventScore.points}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = scoreColor,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}

private fun formatRawValue(eventScore: EventScore): String {
    return when (eventScore.event) {
        AftEvent.DEADLIFT -> "${eventScore.rawValue.toInt()} lbs"
        AftEvent.PUSH_UP -> "${eventScore.rawValue.toInt()} reps"
        AftEvent.SPRINT_DRAG_CARRY,
        AftEvent.PLANK,
        AftEvent.TWO_MILE_RUN -> CalculatorViewModel.formatTimeForDisplay(eventScore.rawValue)
        // Alternate aerobic events - show pass/fail or time
        AftEvent.WALK_2_5_MILE,
        AftEvent.ROW_5K,
        AftEvent.BIKE_12K,
        AftEvent.SWIM_1K -> if (eventScore.rawValue > 0) {
            CalculatorViewModel.formatTimeForDisplay(eventScore.rawValue)
        } else {
            "N/A"
        }
    }
}

@Composable
private fun ExemptEventScoreRow(
    eventName: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.03f))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = eventName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            Text(
                text = "Exempt",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.5f)
            )
        }

        Box(
            modifier = Modifier
                .width(64.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.White.copy(alpha = 0.05f))
                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "60",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}
