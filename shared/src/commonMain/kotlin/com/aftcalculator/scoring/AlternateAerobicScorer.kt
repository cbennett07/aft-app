package com.aftcalculator.scoring

import com.aftcalculator.models.AftEvent
import com.aftcalculator.models.AgeBracket
import com.aftcalculator.models.Gender

/**
 * Scorer for alternate aerobic events (Walk, Row, Bike, Swim).
 * These are Go/No-Go events based on official time standards.
 * Pass = 60 points, Fail = 0 points.
 *
 * Time standards are from HQDA EXORD 218-25, Annex B - Scoring Tables (Page 9).
 * Soldier passes if their time is at or under the maximum for their age/gender/MOS.
 */
object AlternateAerobicScorer {

    const val PASS_POINTS = 60
    const val FAIL_POINTS = 0

    /**
     * Calculate score for an alternate aerobic event.
     * @param event The specific alternate event (WALK_2_5_MILE, ROW_5K, BIKE_12K, SWIM_1K)
     * @param timeInSeconds The soldier's completion time in seconds
     * @param ageBracket The soldier's age bracket
     * @param gender The soldier's gender
     * @param isCombatMos Whether the soldier is in a Combat MOS
     * @return 60 if passed (time <= standard), 0 if failed
     */
    fun calculateScore(
        event: AftEvent,
        timeInSeconds: Double,
        ageBracket: AgeBracket,
        gender: Gender,
        isCombatMos: Boolean
    ): Int {
        if (timeInSeconds <= 0) return FAIL_POINTS

        // Combat MOS: Everyone uses Male/Combat standards (sex-neutral)
        // Non-Combat MOS: Males use Male standards, Females use Female standards
        val useMaleStandard = isCombatMos || gender == Gender.MALE

        val maxPassingTime = AlternateAerobicTables.getMaxPassingTime(
            event = event,
            ageBracket = ageBracket,
            isMaleOrCombat = useMaleStandard
        )

        // Pass if time is at or under the standard
        return if (timeInSeconds <= maxPassingTime) PASS_POINTS else FAIL_POINTS
    }

    /**
     * Check if a given time passes the standard.
     */
    fun isPassing(
        event: AftEvent,
        timeInSeconds: Double,
        ageBracket: AgeBracket,
        gender: Gender,
        isCombatMos: Boolean
    ): Boolean {
        return calculateScore(event, timeInSeconds, ageBracket, gender, isCombatMos) == PASS_POINTS
    }
}
