package com.aftcalculator.scoring

import com.aftcalculator.models.AgeBracket
import com.aftcalculator.models.Gender

interface EventScorer {
    fun calculateScore(
        rawValue: Double,
        ageBracket: AgeBracket,
        gender: Gender,
        isCombatMos: Boolean
    ): Int
}

data class ScoreEntry(
    val rawValue: Double,
    val points: Int
)

fun lookupScore(
    rawValue: Double,
    table: List<ScoreEntry>,
    higherIsBetter: Boolean
): Int {
    if (table.isEmpty()) return 0

    // Sort best-to-worst: descending for higher-is-better, ascending for lower-is-better
    val sortedTable = if (higherIsBetter) {
        table.sortedByDescending { it.rawValue }
    } else {
        table.sortedBy { it.rawValue }
    }

    // Step function: find the first (best) threshold the soldier met
    for (entry in sortedTable) {
        val met = if (higherIsBetter) {
            rawValue >= entry.rawValue
        } else {
            rawValue <= entry.rawValue
        }
        if (met) return entry.points
    }

    return 0
}
