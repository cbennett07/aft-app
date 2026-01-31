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

fun interpolateScore(
    rawValue: Double,
    table: List<ScoreEntry>,
    higherIsBetter: Boolean
): Int {
    if (table.isEmpty()) return 0

    val sortedTable = if (higherIsBetter) {
        table.sortedByDescending { it.rawValue }
    } else {
        table.sortedBy { it.rawValue }
    }

    // Check boundaries
    if (higherIsBetter) {
        if (rawValue >= sortedTable.first().rawValue) return sortedTable.first().points
        if (rawValue <= sortedTable.last().rawValue) return sortedTable.last().points
    } else {
        if (rawValue <= sortedTable.first().rawValue) return sortedTable.first().points
        if (rawValue >= sortedTable.last().rawValue) return sortedTable.last().points
    }

    // Find the two entries to interpolate between
    for (i in 0 until sortedTable.size - 1) {
        val upper = sortedTable[i]
        val lower = sortedTable[i + 1]

        val inRange = if (higherIsBetter) {
            rawValue <= upper.rawValue && rawValue > lower.rawValue
        } else {
            rawValue >= upper.rawValue && rawValue < lower.rawValue
        }

        if (inRange) {
            val ratio = if (higherIsBetter) {
                (rawValue - lower.rawValue) / (upper.rawValue - lower.rawValue)
            } else {
                (lower.rawValue - rawValue) / (lower.rawValue - upper.rawValue)
            }
            return (lower.points + ratio * (upper.points - lower.points)).toInt()
        }
    }

    return 0
}
