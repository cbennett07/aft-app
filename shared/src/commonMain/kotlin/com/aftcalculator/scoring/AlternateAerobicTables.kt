package com.aftcalculator.scoring

import com.aftcalculator.models.AgeBracket
import com.aftcalculator.models.AftEvent

/**
 * Official Alternate Aerobic Event Time Standards
 * Source: HQDA EXORD 218-25, Annex B - Scoring Tables (Page 9)
 * Approved: 1 May 2025, Effective: 1 June 2025
 *
 * These are Go/No-Go events - pass if time is at or under the standard.
 * Pass = 60 points, Fail = 0 points
 *
 * Times are stored in seconds for easy comparison.
 */
object AlternateAerobicTables {

    /**
     * Returns the maximum time (in seconds) to pass for the given alternate event.
     * If the soldier's time is <= this value, they pass (60 points).
     */
    fun getMaxPassingTime(
        event: AftEvent,
        ageBracket: AgeBracket,
        isMaleOrCombat: Boolean
    ): Int {
        return when (event) {
            AftEvent.WALK_2_5_MILE -> getWalkStandard(ageBracket, isMaleOrCombat)
            AftEvent.BIKE_12K -> getBikeStandard(ageBracket, isMaleOrCombat)
            AftEvent.SWIM_1K -> getSwimStandard(ageBracket, isMaleOrCombat)
            AftEvent.ROW_5K -> getRowStandard(ageBracket, isMaleOrCombat)
            else -> Int.MAX_VALUE // Non-alternate events always "pass" this check
        }
    }

    // 2.5-Mile Walk standards (mm:ss converted to seconds)
    private fun getWalkStandard(ageBracket: AgeBracket, isMaleOrCombat: Boolean): Int {
        return if (isMaleOrCombat) {
            when (ageBracket) {
                AgeBracket.AGE_17_21 -> 31 * 60 + 0   // 31:00
                AgeBracket.AGE_22_26 -> 30 * 60 + 45  // 30:45
                AgeBracket.AGE_27_31 -> 30 * 60 + 30  // 30:30
                AgeBracket.AGE_32_36 -> 30 * 60 + 45  // 30:45
                AgeBracket.AGE_37_41 -> 31 * 60 + 0   // 31:00
                AgeBracket.AGE_42_46 -> 31 * 60 + 0   // 31:00
                AgeBracket.AGE_47_51 -> 32 * 60 + 0   // 32:00
                AgeBracket.AGE_52_56 -> 32 * 60 + 0   // 32:00
                AgeBracket.AGE_57_61 -> 33 * 60 + 0   // 33:00
                AgeBracket.AGE_62_PLUS -> 33 * 60 + 0 // 33:00
            }
        } else {
            // Female (Non-Combat)
            when (ageBracket) {
                AgeBracket.AGE_17_21 -> 34 * 60 + 0   // 34:00
                AgeBracket.AGE_22_26 -> 33 * 60 + 30  // 33:30
                AgeBracket.AGE_27_31 -> 33 * 60 + 0   // 33:00
                AgeBracket.AGE_32_36 -> 33 * 60 + 30  // 33:30
                AgeBracket.AGE_37_41 -> 34 * 60 + 0   // 34:00
                AgeBracket.AGE_42_46 -> 34 * 60 + 0   // 34:00
                AgeBracket.AGE_47_51 -> 35 * 60 + 0   // 35:00
                AgeBracket.AGE_52_56 -> 35 * 60 + 0   // 35:00
                AgeBracket.AGE_57_61 -> 36 * 60 + 0   // 36:00
                AgeBracket.AGE_62_PLUS -> 36 * 60 + 0 // 36:00
            }
        }
    }

    // 12km Bike standards (mm:ss converted to seconds)
    private fun getBikeStandard(ageBracket: AgeBracket, isMaleOrCombat: Boolean): Int {
        return if (isMaleOrCombat) {
            when (ageBracket) {
                AgeBracket.AGE_17_21 -> 26 * 60 + 25  // 26:25
                AgeBracket.AGE_22_26 -> 26 * 60 + 12  // 26:12
                AgeBracket.AGE_27_31 -> 26 * 60 + 0   // 26:00
                AgeBracket.AGE_32_36 -> 26 * 60 + 12  // 26:12
                AgeBracket.AGE_37_41 -> 26 * 60 + 25  // 26:25
                AgeBracket.AGE_42_46 -> 26 * 60 + 25  // 26:25
                AgeBracket.AGE_47_51 -> 27 * 60 + 16  // 27:16
                AgeBracket.AGE_52_56 -> 27 * 60 + 16  // 27:16
                AgeBracket.AGE_57_61 -> 28 * 60 + 7   // 28:07
                AgeBracket.AGE_62_PLUS -> 28 * 60 + 7 // 28:07
            }
        } else {
            // Female (Non-Combat)
            when (ageBracket) {
                AgeBracket.AGE_17_21 -> 28 * 60 + 58  // 28:58
                AgeBracket.AGE_22_26 -> 28 * 60 + 31  // 28:31
                AgeBracket.AGE_27_31 -> 28 * 60 + 7   // 28:07
                AgeBracket.AGE_32_36 -> 28 * 60 + 31  // 28:31
                AgeBracket.AGE_37_41 -> 28 * 60 + 58  // 28:58
                AgeBracket.AGE_42_46 -> 28 * 60 + 58  // 28:58
                AgeBracket.AGE_47_51 -> 29 * 60 + 50  // 29:50
                AgeBracket.AGE_52_56 -> 29 * 60 + 50  // 29:50
                AgeBracket.AGE_57_61 -> 30 * 60 + 41  // 30:41
                AgeBracket.AGE_62_PLUS -> 30 * 60 + 41 // 30:41
            }
        }
    }

    // 1km Swim standards (mm:ss converted to seconds)
    private fun getSwimStandard(ageBracket: AgeBracket, isMaleOrCombat: Boolean): Int {
        return if (isMaleOrCombat) {
            when (ageBracket) {
                AgeBracket.AGE_17_21 -> 30 * 60 + 48  // 30:48
                AgeBracket.AGE_22_26 -> 30 * 60 + 30  // 30:30
                AgeBracket.AGE_27_31 -> 30 * 60 + 20  // 30:20
                AgeBracket.AGE_32_36 -> 30 * 60 + 30  // 30:30
                AgeBracket.AGE_37_41 -> 30 * 60 + 48  // 30:48
                AgeBracket.AGE_42_46 -> 30 * 60 + 48  // 30:48
                AgeBracket.AGE_47_51 -> 31 * 60 + 48  // 31:48
                AgeBracket.AGE_52_56 -> 31 * 60 + 48  // 31:48
                AgeBracket.AGE_57_61 -> 32 * 60 + 50  // 32:50
                AgeBracket.AGE_62_PLUS -> 32 * 60 + 50 // 32:50
            }
        } else {
            // Female (Non-Combat)
            when (ageBracket) {
                AgeBracket.AGE_17_21 -> 33 * 60 + 48  // 33:48
                AgeBracket.AGE_22_26 -> 33 * 60 + 18  // 33:18
                AgeBracket.AGE_27_31 -> 32 * 60 + 48  // 32:48
                AgeBracket.AGE_32_36 -> 33 * 60 + 18  // 33:18
                AgeBracket.AGE_37_41 -> 33 * 60 + 48  // 33:48
                AgeBracket.AGE_42_46 -> 33 * 60 + 48  // 33:48
                AgeBracket.AGE_47_51 -> 34 * 60 + 48  // 34:48
                AgeBracket.AGE_52_56 -> 34 * 60 + 48  // 34:48
                AgeBracket.AGE_57_61 -> 35 * 60 + 48  // 35:48
                AgeBracket.AGE_62_PLUS -> 35 * 60 + 48 // 35:48
            }
        }
    }

    // 5km Row standards (same as 1km Swim per the official tables)
    private fun getRowStandard(ageBracket: AgeBracket, isMaleOrCombat: Boolean): Int {
        return getSwimStandard(ageBracket, isMaleOrCombat)
    }

    /**
     * Format the max passing time for display (mm:ss)
     */
    fun formatMaxPassingTime(
        event: AftEvent,
        ageBracket: AgeBracket,
        isMaleOrCombat: Boolean
    ): String {
        val seconds = getMaxPassingTime(event, ageBracket, isMaleOrCombat)
        val mins = seconds / 60
        val secs = seconds % 60
        return "%d:%02d".format(mins, secs)
    }
}
