package com.aftcalculator.android.viewmodels

import androidx.lifecycle.ViewModel
import com.aftcalculator.AftCalculator
import com.aftcalculator.models.*
import com.aftcalculator.scoring.AlternateAerobicTables
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CalculatorUiState(
    val age: Int = 25,
    val gender: Gender = Gender.MALE,
    val mosCategory: MosCategory = MosCategory.COMBAT,

    // Event inputs (raw values)
    val deadliftLbs: String = "",
    val pushUpReps: String = "",
    val sprintDragCarrySeconds: String = "",
    val plankSeconds: String = "",
    val twoMileRunSeconds: String = "",

    // Alternate aerobic event
    val useAlternateAerobic: Boolean = false,
    val alternateAerobicEvent: AftEvent = AftEvent.WALK_2_5_MILE,
    val alternateAerobicTime: String = "",

    // Medical exemptions
    val deadliftExempt: Boolean = false,
    val pushUpExempt: Boolean = false,
    val sprintDragCarryExempt: Boolean = false,
    val plankExempt: Boolean = false,
    val twoMileRunExempt: Boolean = false,

    // Results
    val result: AftScore? = null,
    val showResults: Boolean = false
)

class CalculatorViewModel : ViewModel() {

    private val calculator = AftCalculator()

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    fun updateAge(age: Int) {
        _uiState.value = _uiState.value.copy(age = age.coerceIn(17, 70))
    }

    fun updateGender(gender: Gender) {
        _uiState.value = _uiState.value.copy(gender = gender)
    }

    fun updateMosCategory(category: MosCategory) {
        _uiState.value = _uiState.value.copy(mosCategory = category)
    }

    fun updateDeadlift(value: String) {
        _uiState.value = _uiState.value.copy(deadliftLbs = value)
    }

    fun updatePushUps(value: String) {
        _uiState.value = _uiState.value.copy(pushUpReps = value)
    }

    fun updateSprintDragCarry(value: String) {
        _uiState.value = _uiState.value.copy(sprintDragCarrySeconds = value)
    }

    fun updatePlank(value: String) {
        _uiState.value = _uiState.value.copy(plankSeconds = value)
    }

    fun updateTwoMileRun(value: String) {
        _uiState.value = _uiState.value.copy(twoMileRunSeconds = value)
    }

    // Alternate aerobic event methods
    fun toggleUseAlternateAerobic() {
        _uiState.value = _uiState.value.copy(
            useAlternateAerobic = !_uiState.value.useAlternateAerobic
        )
    }

    fun setAlternateAerobicEvent(event: AftEvent) {
        if (event.isAlternateAerobic) {
            _uiState.value = _uiState.value.copy(alternateAerobicEvent = event)
        }
    }

    fun updateAlternateAerobicTime(value: String) {
        _uiState.value = _uiState.value.copy(alternateAerobicTime = value)
    }

    // Exemption toggles
    fun toggleDeadliftExempt() {
        _uiState.value = _uiState.value.copy(deadliftExempt = !_uiState.value.deadliftExempt)
    }

    fun togglePushUpExempt() {
        _uiState.value = _uiState.value.copy(pushUpExempt = !_uiState.value.pushUpExempt)
    }

    fun toggleSprintDragCarryExempt() {
        _uiState.value = _uiState.value.copy(sprintDragCarryExempt = !_uiState.value.sprintDragCarryExempt)
    }

    fun togglePlankExempt() {
        _uiState.value = _uiState.value.copy(plankExempt = !_uiState.value.plankExempt)
    }

    fun toggleTwoMileRunExempt() {
        _uiState.value = _uiState.value.copy(twoMileRunExempt = !_uiState.value.twoMileRunExempt)
    }

    fun isEventExempt(event: AftEvent): Boolean {
        val state = _uiState.value
        return when (event) {
            AftEvent.DEADLIFT -> state.deadliftExempt
            AftEvent.PUSH_UP -> state.pushUpExempt
            AftEvent.SPRINT_DRAG_CARRY -> state.sprintDragCarryExempt
            AftEvent.PLANK -> state.plankExempt
            AftEvent.TWO_MILE_RUN -> state.twoMileRunExempt || state.useAlternateAerobic
            // Alternate aerobic events are exempt if not using alternate or using a different one
            AftEvent.WALK_2_5_MILE,
            AftEvent.ROW_5K,
            AftEvent.BIKE_12K,
            AftEvent.SWIM_1K -> !state.useAlternateAerobic || state.alternateAerobicEvent != event
        }
    }

    fun calculateScore() {
        val state = _uiState.value
        val soldier = Soldier(state.age, state.gender, state.mosCategory)

        val inputs = mutableListOf<EventInput>()

        // Parse deadlift (if not exempt)
        if (!state.deadliftExempt) {
            state.deadliftLbs.toDoubleOrNull()?.let {
                inputs.add(EventInput(AftEvent.DEADLIFT, it))
            }
        }

        // Parse push-ups (if not exempt)
        if (!state.pushUpExempt) {
            state.pushUpReps.toDoubleOrNull()?.let {
                inputs.add(EventInput(AftEvent.PUSH_UP, it))
            }
        }

        // Parse sprint-drag-carry (if not exempt)
        if (!state.sprintDragCarryExempt) {
            parseTimeInput(state.sprintDragCarrySeconds)?.let {
                inputs.add(EventInput(AftEvent.SPRINT_DRAG_CARRY, it))
            }
        }

        // Parse plank (if not exempt)
        if (!state.plankExempt) {
            parseTimeInput(state.plankSeconds)?.let {
                inputs.add(EventInput(AftEvent.PLANK, it))
            }
        }

        // Parse 2-mile run OR alternate aerobic event
        if (state.useAlternateAerobic) {
            // Alternate aerobic: pass/fail determined by time vs official standards
            val timeValue = parseTimeInput(state.alternateAerobicTime) ?: 0.0
            inputs.add(EventInput(state.alternateAerobicEvent, timeValue))
        } else if (!state.twoMileRunExempt) {
            parseTimeInput(state.twoMileRunSeconds)?.let {
                inputs.add(EventInput(AftEvent.TWO_MILE_RUN, it))
            }
        }

        if (inputs.isNotEmpty()) {
            val result = calculator.calculateScore(soldier, inputs)
            _uiState.value = _uiState.value.copy(result = result, showResults = true)
        }
    }

    fun calculateSingleEvent(event: AftEvent): EventScore? {
        val state = _uiState.value

        // Return null if event is exempt
        if (isEventExempt(event)) return null

        val soldier = Soldier(state.age, state.gender, state.mosCategory)

        val rawValue = when (event) {
            AftEvent.DEADLIFT -> state.deadliftLbs.toDoubleOrNull()
            AftEvent.PUSH_UP -> state.pushUpReps.toDoubleOrNull()
            AftEvent.SPRINT_DRAG_CARRY -> parseTimeInput(state.sprintDragCarrySeconds)
            AftEvent.PLANK -> parseTimeInput(state.plankSeconds)
            AftEvent.TWO_MILE_RUN -> parseTimeInput(state.twoMileRunSeconds)
            // Alternate aerobic events: use actual time (scorer checks vs standards)
            AftEvent.WALK_2_5_MILE,
            AftEvent.ROW_5K,
            AftEvent.BIKE_12K,
            AftEvent.SWIM_1K -> parseTimeInput(state.alternateAerobicTime)
        }

        return rawValue?.let { calculator.calculateSingleEvent(event, it, soldier) }
    }

    fun hideResults() {
        _uiState.value = _uiState.value.copy(showResults = false)
    }

    fun resetCalculator() {
        _uiState.value = CalculatorUiState()
    }

    private fun parseTimeInput(input: String): Double? {
        if (input.isBlank()) return null

        // Try parsing as mm:ss format
        if (input.contains(":")) {
            return AftCalculator.parseTime(input).takeIf { it > 0 }
        }

        // Try parsing as plain seconds
        return input.toDoubleOrNull()
    }

    // Get minimum raw score to achieve 60 points for current soldier
    fun getMinimumPassingRaw(event: AftEvent): String {
        val state = _uiState.value
        val ageBracket = AgeBracket.fromAge(state.age)

        // Combat MOS: Everyone uses Male/Combat tables (sex-neutral)
        // Non-Combat MOS: Males use Male tables, Females use Female tables
        val isCombat = state.mosCategory == MosCategory.COMBAT
        val useMaleTable = isCombat || state.gender == Gender.MALE

        return when (event) {
            AftEvent.DEADLIFT -> {
                if (useMaleTable) {
                    // Male/Combat table: varies by age (from scoring tables)
                    when (ageBracket) {
                        AgeBracket.AGE_17_21 -> "150"
                        AgeBracket.AGE_22_26 -> "150"
                        AgeBracket.AGE_27_31 -> "150"
                        AgeBracket.AGE_32_36 -> "140"
                        AgeBracket.AGE_37_41 -> "140"
                        AgeBracket.AGE_42_46 -> "140"
                        AgeBracket.AGE_47_51 -> "140"
                        AgeBracket.AGE_52_56 -> "140"
                        AgeBracket.AGE_57_61 -> "135"  // interpolated: 140=72, 130=50
                        AgeBracket.AGE_62_PLUS -> "135" // interpolated: 140=72, 130=50
                    }
                } else {
                    // Female table (Non-Combat females only)
                    "120"
                }
            }
            AftEvent.PUSH_UP -> {
                if (useMaleTable) {
                    // Male/Combat standards
                    when (ageBracket) {
                        AgeBracket.AGE_17_21 -> "15"
                        AgeBracket.AGE_22_26 -> "14"
                        AgeBracket.AGE_27_31 -> "14"
                        AgeBracket.AGE_32_36 -> "13"
                        AgeBracket.AGE_37_41 -> "12"
                        AgeBracket.AGE_42_46 -> "11"
                        AgeBracket.AGE_47_51 -> "11"
                        AgeBracket.AGE_52_56 -> "10"
                        AgeBracket.AGE_57_61 -> "10"
                        AgeBracket.AGE_62_PLUS -> "10"
                    }
                } else {
                    // Female standards (Non-Combat females only)
                    when (ageBracket) {
                        AgeBracket.AGE_17_21 -> "11"
                        AgeBracket.AGE_22_26 -> "11"
                        AgeBracket.AGE_27_31 -> "11"
                        AgeBracket.AGE_32_36 -> "11"
                        AgeBracket.AGE_37_41 -> "10"
                        AgeBracket.AGE_42_46 -> "10"
                        AgeBracket.AGE_47_51 -> "10"
                        AgeBracket.AGE_52_56 -> "10"
                        AgeBracket.AGE_57_61 -> "10"
                        AgeBracket.AGE_62_PLUS -> "10"
                    }
                }
            }
            AftEvent.SPRINT_DRAG_CARRY -> {
                // Values from official scoring tables for 60 points (lower time is better)
                if (useMaleTable) {
                    when (ageBracket) {
                        AgeBracket.AGE_17_21 -> "2:28"
                        AgeBracket.AGE_22_26 -> "2:31"
                        AgeBracket.AGE_27_31 -> "2:32"
                        AgeBracket.AGE_32_36 -> "2:36"
                        AgeBracket.AGE_37_41 -> "2:41"
                        AgeBracket.AGE_42_46 -> "2:45"
                        AgeBracket.AGE_47_51 -> "2:53"
                        AgeBracket.AGE_52_56 -> "3:00"
                        AgeBracket.AGE_57_61 -> "3:12"
                        AgeBracket.AGE_62_PLUS -> "3:16"
                    }
                } else {
                    // Female (Non-Combat) - approximate values
                    when (ageBracket) {
                        AgeBracket.AGE_17_21 -> "3:15"
                        AgeBracket.AGE_22_26 -> "3:15"
                        AgeBracket.AGE_27_31 -> "3:15"
                        AgeBracket.AGE_32_36 -> "3:22"
                        AgeBracket.AGE_37_41 -> "3:27"
                        AgeBracket.AGE_42_46 -> "3:42"
                        AgeBracket.AGE_47_51 -> "3:51"
                        AgeBracket.AGE_52_56 -> "4:03"
                        AgeBracket.AGE_57_61 -> "4:48"
                        AgeBracket.AGE_62_PLUS -> "4:48"
                    }
                }
            }
            AftEvent.PLANK -> {
                // Plank is gender-neutral, varies by age (higher time is better)
                when (ageBracket) {
                    AgeBracket.AGE_17_21 -> "1:30"
                    AgeBracket.AGE_22_26 -> "1:25"
                    AgeBracket.AGE_27_31 -> "1:20"
                    AgeBracket.AGE_32_36 -> "1:15"
                    AgeBracket.AGE_37_41 -> "1:10"
                    AgeBracket.AGE_42_46 -> "1:10"
                    AgeBracket.AGE_47_51 -> "1:10"
                    AgeBracket.AGE_52_56 -> "1:10"
                    AgeBracket.AGE_57_61 -> "1:10"
                    AgeBracket.AGE_62_PLUS -> "1:10"
                }
            }
            AftEvent.TWO_MILE_RUN -> {
                // Values from official scoring tables for 60 points (lower time is better)
                if (useMaleTable) {
                    when (ageBracket) {
                        AgeBracket.AGE_17_21 -> "19:57"
                        AgeBracket.AGE_22_26 -> "19:45"
                        AgeBracket.AGE_27_31 -> "19:45"
                        AgeBracket.AGE_32_36 -> "20:44"
                        AgeBracket.AGE_37_41 -> "20:44"
                        AgeBracket.AGE_42_46 -> "22:04"
                        AgeBracket.AGE_47_51 -> "22:04"
                        AgeBracket.AGE_52_56 -> "22:50"
                        AgeBracket.AGE_57_61 -> "23:36"
                        AgeBracket.AGE_62_PLUS -> "21:40"
                    }
                } else {
                    // Female (Non-Combat) - from scoring tables
                    when (ageBracket) {
                        AgeBracket.AGE_17_21 -> "22:55"
                        AgeBracket.AGE_22_26 -> "22:45"
                        AgeBracket.AGE_27_31 -> "22:45"
                        AgeBracket.AGE_32_36 -> "22:50"
                        AgeBracket.AGE_37_41 -> "22:59"
                        AgeBracket.AGE_42_46 -> "23:15"
                        AgeBracket.AGE_47_51 -> "23:30"
                        AgeBracket.AGE_52_56 -> "24:00"
                        AgeBracket.AGE_57_61 -> "24:48"
                        AgeBracket.AGE_62_PLUS -> "25:00"
                    }
                }
            }
            // Alternate aerobic events - show max passing time from official standards
            AftEvent.WALK_2_5_MILE,
            AftEvent.ROW_5K,
            AftEvent.BIKE_12K,
            AftEvent.SWIM_1K -> {
                AlternateAerobicTables.formatMaxPassingTime(
                    event = event,
                    ageBracket = ageBracket,
                    isMaleOrCombat = useMaleTable
                )
            }
        }
    }

    /**
     * Get the max passing time for an alternate aerobic event.
     * Returns formatted time string (mm:ss).
     */
    fun getAlternateMaxPassingTime(event: AftEvent): String {
        val state = _uiState.value
        val ageBracket = AgeBracket.fromAge(state.age)
        val isCombat = state.mosCategory == MosCategory.COMBAT
        val useMaleTable = isCombat || state.gender == Gender.MALE

        return AlternateAerobicTables.formatMaxPassingTime(
            event = event,
            ageBracket = ageBracket,
            isMaleOrCombat = useMaleTable
        )
    }

    companion object {
        fun formatTimeForDisplay(seconds: Double): String {
            return AftCalculator.formatTime(seconds)
        }
    }
}
