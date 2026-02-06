package com.aftcalculator.scoring

import com.aftcalculator.models.AgeBracket
import com.aftcalculator.models.Gender

object PlankScorer : EventScorer {

    override fun calculateScore(
        rawValue: Double,
        ageBracket: AgeBracket,
        gender: Gender,
        isCombatMos: Boolean
    ): Int {
        // Plank scoring is gender-neutral per official tables
        val table = TimedEventTables.getPlankTable(ageBracket)
        return lookupScore(rawValue, table, higherIsBetter = true)
    }
}
