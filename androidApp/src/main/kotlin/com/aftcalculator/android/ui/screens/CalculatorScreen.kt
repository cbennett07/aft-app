package com.aftcalculator.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aftcalculator.android.ui.components.EventInputCard
import com.aftcalculator.android.ui.components.AlternateAerobicCard
import com.aftcalculator.android.ui.theme.ArmyGold
import com.aftcalculator.android.ui.theme.ArmyBlack
import com.aftcalculator.android.ui.theme.ArmyDarkGray
import com.aftcalculator.android.ui.theme.PassGreen
import com.aftcalculator.android.ui.theme.FailRed
import com.aftcalculator.android.viewmodels.CalculatorUiState
import com.aftcalculator.android.viewmodels.CalculatorViewModel
import com.aftcalculator.models.AftEvent
import com.aftcalculator.models.EventScore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    uiState: CalculatorUiState,
    viewModel: CalculatorViewModel,
    onNavigateBack: () -> Unit,
    onCalculate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Calculate live scores for each event
    val deadliftScore = remember(uiState.deadliftLbs, uiState.age, uiState.gender, uiState.mosCategory) {
        viewModel.calculateSingleEvent(AftEvent.DEADLIFT)
    }
    val pushUpScore = remember(uiState.pushUpReps, uiState.age, uiState.gender, uiState.mosCategory) {
        viewModel.calculateSingleEvent(AftEvent.PUSH_UP)
    }
    val sdcScore = remember(uiState.sprintDragCarrySeconds, uiState.age, uiState.gender, uiState.mosCategory) {
        viewModel.calculateSingleEvent(AftEvent.SPRINT_DRAG_CARRY)
    }
    val plankScore = remember(uiState.plankSeconds, uiState.age, uiState.gender, uiState.mosCategory) {
        viewModel.calculateSingleEvent(AftEvent.PLANK)
    }
    val runScore = remember(uiState.twoMileRunSeconds, uiState.age, uiState.gender, uiState.mosCategory, uiState.useAlternateAerobic) {
        if (uiState.useAlternateAerobic) null else viewModel.calculateSingleEvent(AftEvent.TWO_MILE_RUN)
    }

    // Calculate alternate aerobic score
    val alternateScore = remember(
        uiState.useAlternateAerobic,
        uiState.alternateAerobicEvent,
        uiState.alternateAerobicTime,
        uiState.age,
        uiState.gender,
        uiState.mosCategory
    ) {
        if (uiState.useAlternateAerobic) {
            viewModel.calculateSingleEvent(uiState.alternateAerobicEvent)
        } else null
    }

    // Get max passing time for alternate aerobic event
    val alternateMaxPassingTime = remember(
        uiState.alternateAerobicEvent,
        uiState.age,
        uiState.gender,
        uiState.mosCategory
    ) {
        viewModel.getAlternateMaxPassingTime(uiState.alternateAerobicEvent)
    }

    // Calculate running total (use alternate if selected, otherwise 2-mile run)
    val aerobicScore = if (uiState.useAlternateAerobic) alternateScore else runScore
    val eventScores = listOfNotNull(deadliftScore, pushUpScore, sdcScore, plankScore, aerobicScore)
    val runningTotal = eventScores.sumOf { it.points }
    val enteredEvents = eventScores.size
    // Use MOS-specific minimum totals (350 for Combat, 300 for Combat-Enabling)
    // Pro-rate if fewer than 5 events taken
    val fullMinimum = uiState.mosCategory.minimumTotal
    val minimumRequired = if (enteredEvents < 5 && enteredEvents > 0) {
        (fullMinimum * enteredEvents) / 5
    } else {
        fullMinimum
    }
    // Check if any event is failing (below 60) OR total is below minimum
    val anyEventFailing = eventScores.any { it.points < 60 }
    val isOnTrack = enteredEvents == 0 || (!anyEventFailing && runningTotal >= minimumRequired)

    // Calculate minimum passing raw values based on current soldier config
    // All values now vary by age, so include age in remember keys
    val minPassingDeadlift = remember(uiState.mosCategory, uiState.gender, uiState.age) {
        viewModel.getMinimumPassingRaw(AftEvent.DEADLIFT)
    }
    val minPassingPushUp = remember(uiState.mosCategory, uiState.gender, uiState.age) {
        viewModel.getMinimumPassingRaw(AftEvent.PUSH_UP)
    }
    val minPassingSdc = remember(uiState.mosCategory, uiState.gender, uiState.age) {
        viewModel.getMinimumPassingRaw(AftEvent.SPRINT_DRAG_CARRY)
    }
    val minPassingPlank = remember(uiState.mosCategory, uiState.gender, uiState.age) {
        viewModel.getMinimumPassingRaw(AftEvent.PLANK)
    }
    val minPassingRun = remember(uiState.mosCategory, uiState.gender, uiState.age) {
        viewModel.getMinimumPassingRaw(AftEvent.TWO_MILE_RUN)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        ArmyBlack,
                        ArmyDarkGray
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Custom Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ArmyBlack)
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = ArmyGold
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "ENTER EVENT SCORES",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${uiState.mosCategory.displayName} • Age ${uiState.age}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                // Running total card
                if (enteredEvents > 0) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = if (isOnTrack) PassGreen.copy(alpha = 0.5f) else FailRed.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White.copy(alpha = 0.05f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "RUNNING TOTAL",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = ArmyGold,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$enteredEvents of 5 events entered",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isOnTrack) PassGreen.copy(alpha = 0.2f)
                                        else FailRed.copy(alpha = 0.2f)
                                    )
                                    .border(
                                        1.dp,
                                        if (isOnTrack) PassGreen else FailRed,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "$runningTotal",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isOnTrack) PassGreen else FailRed
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Always use 60 as visual threshold (standard passing score)
                val minPerEvent = 60

                // Event Input Cards
                EventInputCard(
                    event = AftEvent.DEADLIFT,
                    value = uiState.deadliftLbs,
                    onValueChange = { viewModel.updateDeadlift(it) },
                    liveScore = deadliftScore,
                    minRequired = minPerEvent,
                    minPassingRaw = minPassingDeadlift,
                    isExempt = uiState.deadliftExempt,
                    onExemptToggle = { viewModel.toggleDeadliftExempt() }
                )

                Spacer(modifier = Modifier.height(12.dp))

                EventInputCard(
                    event = AftEvent.PUSH_UP,
                    value = uiState.pushUpReps,
                    onValueChange = { viewModel.updatePushUps(it) },
                    liveScore = pushUpScore,
                    minRequired = minPerEvent,
                    minPassingRaw = minPassingPushUp,
                    isExempt = uiState.pushUpExempt,
                    onExemptToggle = { viewModel.togglePushUpExempt() }
                )

                Spacer(modifier = Modifier.height(12.dp))

                EventInputCard(
                    event = AftEvent.SPRINT_DRAG_CARRY,
                    value = uiState.sprintDragCarrySeconds,
                    onValueChange = { viewModel.updateSprintDragCarry(it) },
                    liveScore = sdcScore,
                    minRequired = minPerEvent,
                    minPassingRaw = minPassingSdc,
                    isExempt = uiState.sprintDragCarryExempt,
                    onExemptToggle = { viewModel.toggleSprintDragCarryExempt() }
                )

                Spacer(modifier = Modifier.height(12.dp))

                EventInputCard(
                    event = AftEvent.PLANK,
                    value = uiState.plankSeconds,
                    onValueChange = { viewModel.updatePlank(it) },
                    liveScore = plankScore,
                    minRequired = minPerEvent,
                    minPassingRaw = minPassingPlank,
                    isExempt = uiState.plankExempt,
                    onExemptToggle = { viewModel.togglePlankExempt() }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Aerobic Event: Either 2-Mile Run or Alternate
                if (uiState.useAlternateAerobic) {
                    AlternateAerobicCard(
                        selectedEvent = uiState.alternateAerobicEvent,
                        onEventChange = { viewModel.setAlternateAerobicEvent(it) },
                        timeValue = uiState.alternateAerobicTime,
                        onTimeChange = { viewModel.updateAlternateAerobicTime(it) },
                        maxPassingTime = alternateMaxPassingTime,
                        liveScore = alternateScore,
                        onSwitchToStandard = { viewModel.toggleUseAlternateAerobic() }
                    )
                } else {
                    EventInputCard(
                        event = AftEvent.TWO_MILE_RUN,
                        value = uiState.twoMileRunSeconds,
                        onValueChange = { viewModel.updateTwoMileRun(it) },
                        liveScore = runScore,
                        minRequired = minPerEvent,
                        minPassingRaw = minPassingRun,
                        isExempt = uiState.twoMileRunExempt,
                        onExemptToggle = { viewModel.toggleTwoMileRunExempt() },
                        showAlternateOption = true,
                        onSwitchToAlternate = { viewModel.toggleUseAlternateAerobic() }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Calculate Button
                Button(
                    onClick = onCalculate,
                    enabled = enteredEvents > 0,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ArmyGold,
                        contentColor = ArmyBlack,
                        disabledContainerColor = ArmyGold.copy(alpha = 0.3f),
                        disabledContentColor = ArmyBlack.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "CALCULATE FINAL SCORE",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Minimum required: $minimumRequired points",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
