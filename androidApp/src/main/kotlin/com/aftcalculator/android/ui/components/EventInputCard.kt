package com.aftcalculator.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aftcalculator.android.ui.theme.ArmyGold
import com.aftcalculator.android.ui.theme.PassGreen
import com.aftcalculator.android.ui.theme.FailRed
import com.aftcalculator.android.ui.theme.WarningAmber
import com.aftcalculator.models.AftEvent
import com.aftcalculator.models.EventScore

enum class ScoreStatus {
    NONE,
    PASS,
    WARNING,
    FAIL,
    EXEMPT
}

fun getScoreStatus(score: EventScore?, minRequired: Int, isExempt: Boolean): ScoreStatus {
    if (isExempt) return ScoreStatus.EXEMPT
    if (score == null) return ScoreStatus.NONE

    return when {
        minRequired > 0 && score.points < minRequired -> ScoreStatus.FAIL
        minRequired > 0 && score.points < (minRequired * 1.1).toInt() -> ScoreStatus.WARNING
        else -> ScoreStatus.PASS
    }
}

fun getScoreColor(status: ScoreStatus): Color {
    return when (status) {
        ScoreStatus.NONE -> Color.White.copy(alpha = 0.4f)
        ScoreStatus.PASS -> PassGreen
        ScoreStatus.WARNING -> WarningAmber
        ScoreStatus.FAIL -> FailRed
        ScoreStatus.EXEMPT -> Color.White.copy(alpha = 0.3f)
    }
}

fun getScoreBackgroundColor(status: ScoreStatus): Color {
    return when (status) {
        ScoreStatus.NONE -> Color.White.copy(alpha = 0.1f)
        ScoreStatus.PASS -> PassGreen.copy(alpha = 0.2f)
        ScoreStatus.WARNING -> WarningAmber.copy(alpha = 0.2f)
        ScoreStatus.FAIL -> FailRed.copy(alpha = 0.3f)
        ScoreStatus.EXEMPT -> Color.White.copy(alpha = 0.05f)
    }
}

@Composable
fun EventInputCard(
    event: AftEvent,
    value: String,
    onValueChange: (String) -> Unit,
    liveScore: EventScore?,
    minRequired: Int = 60,
    minPassingRaw: String = "",
    isExempt: Boolean = false,
    onExemptToggle: () -> Unit = {},
    showAlternateOption: Boolean = false,
    onSwitchToAlternate: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scoreStatus = getScoreStatus(liveScore, minRequired, isExempt)
    val scoreColor = getScoreColor(scoreStatus)
    val scoreBgColor = getScoreBackgroundColor(scoreStatus)

    // Input validation based on event type
    val filteredOnValueChange: (String) -> Unit = { newValue ->
        when (event) {
            AftEvent.DEADLIFT, AftEvent.PUSH_UP -> {
                // Only allow digits for numeric fields
                val filtered = newValue.filter { it.isDigit() }
                onValueChange(filtered)
            }
            AftEvent.SPRINT_DRAG_CARRY, AftEvent.PLANK -> {
                // SDC/Plank can be M:SS or MM:SS format
                val digitsOnly = newValue.filter { it.isDigit() }.take(4)
                val formatted = when (digitsOnly.length) {
                    0 -> ""
                    1, 2 -> digitsOnly // Wait for more input
                    3 -> {
                        // M:SS format (e.g., "145" -> "1:45")
                        val seconds = digitsOnly.substring(1).toIntOrNull() ?: 0
                        if (seconds <= 59) {
                            "${digitsOnly[0]}:${digitsOnly.substring(1)}"
                        } else {
                            digitsOnly // Invalid seconds
                        }
                    }
                    4 -> {
                        // MM:SS format (e.g., "1030" -> "10:30")
                        val seconds = digitsOnly.substring(2).toIntOrNull() ?: 0
                        if (seconds <= 59) {
                            "${digitsOnly.substring(0, 2)}:${digitsOnly.substring(2)}"
                        } else {
                            digitsOnly // Invalid seconds
                        }
                    }
                    else -> digitsOnly.take(4)
                }
                onValueChange(formatted)
            }
            AftEvent.TWO_MILE_RUN, AftEvent.WALK_2_5_MILE, AftEvent.ROW_5K,
            AftEvent.BIKE_12K, AftEvent.SWIM_1K -> {
                // These events are always MM:SS format - only format at 4 digits
                val digitsOnly = newValue.filter { it.isDigit() }.take(4)
                val formatted = when (digitsOnly.length) {
                    0 -> ""
                    1, 2, 3 -> digitsOnly // Wait for 4 digits
                    4 -> {
                        // MM:SS format (e.g., "1415" -> "14:15")
                        val seconds = digitsOnly.substring(2).toIntOrNull() ?: 0
                        if (seconds <= 59) {
                            "${digitsOnly.substring(0, 2)}:${digitsOnly.substring(2)}"
                        } else {
                            digitsOnly // Invalid seconds
                        }
                    }
                    else -> digitsOnly.take(4)
                }
                onValueChange(formatted)
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header row: event name + exempt toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = event.displayName.uppercase(),
                        style = MaterialTheme.typography.titleSmall,
                        color = if (isExempt) Color.White.copy(alpha = 0.5f) else ArmyGold,
                        fontWeight = FontWeight.Bold
                    )

                    // Switch to alternate option (only for 2-mile run)
                    if (showAlternateOption && !isExempt) {
                        Spacer(modifier = Modifier.height(4.dp))
                        TextButton(
                            onClick = onSwitchToAlternate,
                            modifier = Modifier.align(Alignment.Start),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = "ALTERNATE AEROBIC EVENT",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = ArmyGold,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(80.dp)
                ) {
                    Switch(
                        checked = isExempt,
                        onCheckedChange = { onExemptToggle() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ArmyGold,
                            checkedTrackColor = ArmyGold.copy(alpha = 0.5f),
                            uncheckedThumbColor = Color.White.copy(alpha = 0.6f),
                            uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier
                            .height(24.dp)
                            .scale(0.875f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "EXEMPT",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = Color.White.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Input + Score row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = value,
                        onValueChange = filteredOnValueChange,
                        enabled = !isExempt,
                        placeholder = {
                            Text(
                                text = if (minPassingRaw.isNotEmpty()) "Min: $minPassingRaw" else "",
                                color = Color.White.copy(alpha = 0.4f)
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = getKeyboardType(event)),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ArmyGold,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            disabledBorderColor = Color.White.copy(alpha = 0.1f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            disabledTextColor = Color.White.copy(alpha = 0.5f),
                            cursorColor = ArmyGold
                        ),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = getInputHint(event),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Score display
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(80.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(64.dp)
                            .height(56.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(scoreBgColor)
                            .border(
                                width = 1.dp,
                                color = scoreColor,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isExempt) "60" else (liveScore?.points?.toString() ?: "--"),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = scoreColor,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "POINTS",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

private fun getInputHint(event: AftEvent): String = when (event) {
    AftEvent.DEADLIFT -> "Weight in pounds (lbs)"
    AftEvent.PUSH_UP -> "Number of repetitions"
    AftEvent.SPRINT_DRAG_CARRY -> "Time (m:ss or mm:ss)"
    AftEvent.PLANK -> "Time (m:ss or mm:ss)"
    AftEvent.TWO_MILE_RUN -> "Time (mm:ss)"
    AftEvent.WALK_2_5_MILE -> "Time (mm:ss)"
    AftEvent.ROW_5K -> "Time (mm:ss)"
    AftEvent.BIKE_12K -> "Time (mm:ss)"
    AftEvent.SWIM_1K -> "Time (mm:ss)"
}

private fun getKeyboardType(event: AftEvent): KeyboardType = when (event) {
    AftEvent.DEADLIFT, AftEvent.PUSH_UP -> KeyboardType.Number
    else -> KeyboardType.Number // Use number keyboard for easier time entry
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlternateAerobicCard(
    selectedEvent: AftEvent,
    onEventChange: (AftEvent) -> Unit,
    timeValue: String,
    onTimeChange: (String) -> Unit,
    maxPassingTime: String,
    liveScore: EventScore?,
    onSwitchToStandard: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Determine pass/fail based on live score (which now uses official time standards)
    val hasTime = timeValue.isNotBlank() && liveScore != null
    val passed = liveScore?.points == 60
    val scoreStatus = when {
        !hasTime -> ScoreStatus.NONE
        passed -> ScoreStatus.PASS
        else -> ScoreStatus.FAIL
    }
    val scoreColor = getScoreColor(scoreStatus)
    val scoreBgColor = getScoreBackgroundColor(scoreStatus)

    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header row: title + disabled exempt toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ALTERNATE AEROBIC EVENT",
                        style = MaterialTheme.typography.titleSmall,
                        color = ArmyGold,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    TextButton(
                        onClick = onSwitchToStandard,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "2-MILE RUN",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = ArmyGold,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(80.dp)
                ) {
                    Switch(
                        checked = false,
                        onCheckedChange = {},
                        enabled = false,
                        colors = SwitchDefaults.colors(
                            disabledUncheckedThumbColor = Color.White.copy(alpha = 0.3f),
                            disabledUncheckedTrackColor = Color.White.copy(alpha = 0.1f)
                        ),
                        modifier = Modifier
                            .height(24.dp)
                            .scale(0.875f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "EXEMPT",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = Color.White.copy(alpha = 0.3f),
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Event selector dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = selectedEvent.displayName,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ArmyGold,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = ArmyGold
                    ),
                    label = { Text("Select Event", color = Color.White.copy(alpha = 0.6f)) }
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    AftEvent.alternateAerobicEvents.forEach { event ->
                        DropdownMenuItem(
                            text = { Text(event.displayName) },
                            onClick = {
                                onEventChange(event)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Input + Score row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = timeValue,
                        onValueChange = { newValue ->
                            // Alternate events are always MM:SS format - only format at 4 digits
                            val digitsOnly = newValue.filter { it.isDigit() }.take(4)
                            val formatted = when (digitsOnly.length) {
                                0 -> ""
                                1, 2, 3 -> digitsOnly // Wait for 4 digits
                                4 -> {
                                    val seconds = digitsOnly.substring(2).toIntOrNull() ?: 0
                                    if (seconds <= 59) {
                                        "${digitsOnly.substring(0, 2)}:${digitsOnly.substring(2)}"
                                    } else {
                                        digitsOnly
                                    }
                                }
                                else -> digitsOnly.take(4)
                            }
                            onTimeChange(formatted)
                        },
                        placeholder = { Text("Max: $maxPassingTime", color = Color.White.copy(alpha = 0.4f)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ArmyGold,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = ArmyGold
                        ),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Time (mm:ss) - Max to pass: $maxPassingTime",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.5f)
                    )

                    // Show pass/fail status based on entered time
                    if (hasTime) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (passed) "PASS - Time meets standard" else "FAIL - Exceeds max time",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (passed) PassGreen else FailRed,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Score display
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(80.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(64.dp)
                            .height(56.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(scoreBgColor)
                            .border(
                                width = 1.dp,
                                color = scoreColor,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = liveScore?.points?.toString() ?: "--",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = scoreColor,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "POINTS",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
