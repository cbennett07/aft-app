package com.aftcalculator.models

import kotlinx.serialization.Serializable

@Serializable
data class EventScore(
    val event: AftEvent,
    val rawValue: Double,
    val points: Int,
    val passed: Boolean
)

@Serializable
data class AftScore(
    val soldier: Soldier,
    val eventScores: List<EventScore>,
    val totalPoints: Int,
    val passed: Boolean,
    val failureReasons: List<String>
) {
    val deadliftScore: EventScore? get() = eventScores.find { it.event == AftEvent.DEADLIFT }
    val pushUpScore: EventScore? get() = eventScores.find { it.event == AftEvent.PUSH_UP }
    val sprintDragCarryScore: EventScore? get() = eventScores.find { it.event == AftEvent.SPRINT_DRAG_CARRY }
    val plankScore: EventScore? get() = eventScores.find { it.event == AftEvent.PLANK }
    val twoMileRunScore: EventScore? get() = eventScores.find { it.event == AftEvent.TWO_MILE_RUN }
}

@Serializable
data class EventInput(
    val event: AftEvent,
    val value: Double // lbs for deadlift, reps for push-up, seconds for timed events
)
