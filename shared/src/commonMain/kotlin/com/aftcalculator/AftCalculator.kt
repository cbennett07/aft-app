package com.aftcalculator

import com.aftcalculator.models.*
import com.aftcalculator.scoring.*

class AftCalculator {

    private val scorers = mapOf(
        AftEvent.DEADLIFT to DeadliftScorer,
        AftEvent.PUSH_UP to PushUpScorer,
        AftEvent.SPRINT_DRAG_CARRY to SprintDragCarryScorer,
        AftEvent.PLANK to PlankScorer,
        AftEvent.TWO_MILE_RUN to TwoMileRunScorer
        // Alternate aerobic events handled specially (time-based pass/fail)
    )

    fun calculateScore(soldier: Soldier, inputs: List<EventInput>): AftScore {
        val isCombatMos = soldier.mosCategory == MosCategory.COMBAT
        val eventScores = mutableListOf<EventScore>()
        val failureReasons = mutableListOf<String>()

        // Minimum is 60 points per event taken
        val minPerEvent = 60

        for (input in inputs) {
            // Calculate points - handle alternate aerobic events specially
            val points = if (input.event.isAlternateAerobic) {
                AlternateAerobicScorer.calculateScore(
                    event = input.event,
                    timeInSeconds = input.value,
                    ageBracket = soldier.ageBracket,
                    gender = soldier.gender,
                    isCombatMos = isCombatMos
                )
            } else {
                val scorer = scorers[input.event]
                    ?: throw IllegalArgumentException("Unknown event: ${input.event}")
                scorer.calculateScore(
                    rawValue = input.value,
                    ageBracket = soldier.ageBracket,
                    gender = soldier.gender,
                    isCombatMos = isCombatMos
                )
            }

            // Each event must score at least 60 points
            val eventPassed = points >= minPerEvent

            if (!eventPassed) {
                failureReasons.add("${input.event.displayName}: scored $points, minimum is $minPerEvent")
            }

            eventScores.add(
                EventScore(
                    event = input.event,
                    rawValue = input.value,
                    points = points,
                    passed = eventPassed
                )
            )
        }

        val totalPoints = eventScores.sumOf { it.points }
        // Use MOS-specific minimum totals (350 for Combat, 300 for Combat-Enabling)
        // Pro-rate if fewer than 5 events taken due to exemptions
        val fullMinimum = soldier.mosCategory.minimumTotal
        val minimumTotal = if (inputs.size < 5) {
            // Pro-rate: Combat needs 70 per event (350/5), Combat-Enabling needs 60 (300/5)
            (fullMinimum * inputs.size) / 5
        } else {
            fullMinimum
        }
        val totalPassed = totalPoints >= minimumTotal

        if (!totalPassed) {
            failureReasons.add("Total score: $totalPoints, minimum required is $minimumTotal")
        }

        val allEventsPassed = eventScores.all { it.passed }
        val overallPassed = allEventsPassed && totalPassed

        return AftScore(
            soldier = soldier,
            eventScores = eventScores,
            totalPoints = totalPoints,
            passed = overallPassed,
            failureReasons = failureReasons
        )
    }

    fun calculateSingleEvent(
        event: AftEvent,
        rawValue: Double,
        soldier: Soldier
    ): EventScore {
        val isCombatMos = soldier.mosCategory == MosCategory.COMBAT

        // Calculate points - handle alternate aerobic events specially
        val points = if (event.isAlternateAerobic) {
            AlternateAerobicScorer.calculateScore(
                event = event,
                timeInSeconds = rawValue,
                ageBracket = soldier.ageBracket,
                gender = soldier.gender,
                isCombatMos = isCombatMos
            )
        } else {
            val scorer = scorers[event]
                ?: throw IllegalArgumentException("Unknown event: $event")
            scorer.calculateScore(
                rawValue = rawValue,
                ageBracket = soldier.ageBracket,
                gender = soldier.gender,
                isCombatMos = isCombatMos
            )
        }

        // Each event must score at least 60 points
        val passed = points >= 60

        return EventScore(
            event = event,
            rawValue = rawValue,
            points = points,
            passed = passed
        )
    }

    /**
     * iOS-friendly method to calculate score from up to 5 individual event scores.
     * Pass null for events that weren't taken.
     */
    fun calculateScoreFromEvents(
        soldier: Soldier,
        deadliftScore: EventScore?,
        pushUpScore: EventScore?,
        sdcScore: EventScore?,
        plankScore: EventScore?,
        runScore: EventScore?
    ): AftScore {
        val eventScores = listOfNotNull(deadliftScore, pushUpScore, sdcScore, plankScore, runScore)
        val failureReasons = mutableListOf<String>()

        // Check each event for minimum 60 points
        for (score in eventScores) {
            if (score.points < 60) {
                failureReasons.add("${score.event.displayName}: scored ${score.points}, minimum is 60")
            }
        }

        val totalPoints = eventScores.sumOf { it.points }

        // Calculate minimum total based on events taken
        val fullMinimum = soldier.mosCategory.minimumTotal
        val minimumTotal = if (eventScores.size < 5) {
            (fullMinimum * eventScores.size) / 5
        } else {
            fullMinimum
        }

        val totalPassed = totalPoints >= minimumTotal
        if (!totalPassed) {
            failureReasons.add("Total score: $totalPoints, minimum required is $minimumTotal")
        }

        val allEventsPassed = eventScores.all { it.passed }
        val overallPassed = allEventsPassed && totalPassed

        return AftScore(
            soldier = soldier,
            eventScores = eventScores,
            totalPoints = totalPoints,
            passed = overallPassed,
            failureReasons = failureReasons
        )
    }

    companion object {
        fun formatTime(seconds: Double): String {
            val totalSeconds = seconds.toInt()
            val minutes = totalSeconds / 60
            val secs = totalSeconds % 60
            return "$minutes:${secs.toString().padStart(2, '0')}"
        }

        fun parseTime(timeString: String): Double {
            val parts = timeString.split(":")
            return when (parts.size) {
                2 -> {
                    val minutes = parts[0].toIntOrNull() ?: 0
                    val seconds = parts[1].toIntOrNull() ?: 0
                    (minutes * 60 + seconds).toDouble()
                }
                1 -> parts[0].toDoubleOrNull() ?: 0.0
                else -> 0.0
            }
        }
    }
}
