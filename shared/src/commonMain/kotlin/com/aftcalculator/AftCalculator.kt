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
    )

    fun calculateScore(soldier: Soldier, inputs: List<EventInput>): AftScore {
        val isCombatMos = soldier.mosCategory == MosCategory.COMBAT
        val eventScores = mutableListOf<EventScore>()
        val failureReasons = mutableListOf<String>()

        // Minimum is 60 points per event taken
        val minPerEvent = 60

        for (input in inputs) {
            val scorer = scorers[input.event]
                ?: throw IllegalArgumentException("Unknown event: ${input.event}")

            val points = scorer.calculateScore(
                rawValue = input.value,
                ageBracket = soldier.ageBracket,
                gender = soldier.gender,
                isCombatMos = isCombatMos
            )

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
        val scorer = scorers[event]
            ?: throw IllegalArgumentException("Unknown event: $event")

        val isCombatMos = soldier.mosCategory == MosCategory.COMBAT
        val points = scorer.calculateScore(
            rawValue = rawValue,
            ageBracket = soldier.ageBracket,
            gender = soldier.gender,
            isCombatMos = isCombatMos
        )

        // Each event must score at least 60 points
        val passed = points >= 60

        return EventScore(
            event = event,
            rawValue = rawValue,
            points = points,
            passed = passed
        )
    }

    companion object {
        fun formatTime(seconds: Double): String {
            val totalSeconds = seconds.toInt()
            val minutes = totalSeconds / 60
            val secs = totalSeconds % 60
            return "%d:%02d".format(minutes, secs)
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
