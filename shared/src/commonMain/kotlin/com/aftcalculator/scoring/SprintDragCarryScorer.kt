package com.aftcalculator.scoring

import com.aftcalculator.models.AgeBracket
import com.aftcalculator.models.Gender
import com.aftcalculator.models.MosCategory

object SprintDragCarryScorer : EventScorer {

    override fun calculateScore(
        rawValue: Double,
        ageBracket: AgeBracket,
        gender: Gender,
        isCombatMos: Boolean
    ): Int {
        val mosCategory = if (isCombatMos) MosCategory.COMBAT else MosCategory.COMBAT_ENABLING
        val scoringCategory = OfficialScoreTables.getScoringCategory(gender, mosCategory)
        val table = TimedEventTables.getSdcTable(scoringCategory, ageBracket)

        return lookupScore(rawValue, table, higherIsBetter = false)
    }
}
