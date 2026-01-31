package com.aftcalculator.scoring

import com.aftcalculator.models.AgeBracket

/**
 * Timed Event Scoring Tables
 * Sprint-Drag-Carry, Plank, Two-Mile Run
 * Values in seconds
 */
object TimedEventTables {

    // Helper to convert mm:ss to seconds
    private fun t(min: Int, sec: Int): Double = (min * 60 + sec).toDouble()

    // ==================== SPRINT-DRAG-CARRY (SDC) ====================
    // Lower time is better

    val sdcMaleCombat: Map<AgeBracket, List<ScoreEntry>> = mapOf(
        AgeBracket.AGE_17_21 to listOf(
            ScoreEntry(t(1, 29), 100), ScoreEntry(t(1, 31), 99), ScoreEntry(t(1, 34), 98),
            ScoreEntry(t(1, 35), 97), ScoreEntry(t(1, 36), 96), ScoreEntry(t(1, 37), 95),
            ScoreEntry(t(1, 39), 94), ScoreEntry(t(1, 40), 93), ScoreEntry(t(1, 41), 92),
            ScoreEntry(t(1, 42), 91), ScoreEntry(t(1, 43), 90), ScoreEntry(t(1, 44), 89),
            ScoreEntry(t(1, 45), 88), ScoreEntry(t(1, 46), 87), ScoreEntry(t(1, 47), 86),
            ScoreEntry(t(1, 48), 85), ScoreEntry(t(1, 49), 84), ScoreEntry(t(1, 50), 83),
            ScoreEntry(t(1, 51), 82), ScoreEntry(t(1, 52), 81), ScoreEntry(t(1, 53), 80),
            ScoreEntry(t(1, 54), 79), ScoreEntry(t(1, 55), 78), ScoreEntry(t(1, 56), 77),
            ScoreEntry(t(1, 57), 76), ScoreEntry(t(1, 58), 75), ScoreEntry(t(1, 59), 74),
            ScoreEntry(t(2, 0), 73), ScoreEntry(t(2, 1), 72), ScoreEntry(t(2, 2), 71),
            ScoreEntry(t(2, 3), 70), ScoreEntry(t(2, 4), 69), ScoreEntry(t(2, 6), 68),
            ScoreEntry(t(2, 7), 67), ScoreEntry(t(2, 8), 66), ScoreEntry(t(2, 11), 65),
            ScoreEntry(t(2, 13), 64), ScoreEntry(t(2, 15), 63), ScoreEntry(t(2, 17), 62),
            ScoreEntry(t(2, 22), 61), ScoreEntry(t(2, 28), 60),
            ScoreEntry(t(2, 38), 50), ScoreEntry(t(2, 48), 40), ScoreEntry(t(2, 58), 30),
            ScoreEntry(t(3, 8), 20), ScoreEntry(t(3, 18), 10), ScoreEntry(t(3, 28), 0)
        ),
        AgeBracket.AGE_22_26 to listOf(
            ScoreEntry(t(1, 30), 100), ScoreEntry(t(1, 32), 99), ScoreEntry(t(1, 33), 98),
            ScoreEntry(t(1, 34), 97), ScoreEntry(t(1, 36), 96), ScoreEntry(t(1, 37), 95),
            ScoreEntry(t(1, 39), 94), ScoreEntry(t(1, 40), 93), ScoreEntry(t(1, 41), 92),
            ScoreEntry(t(1, 42), 91), ScoreEntry(t(1, 43), 90), ScoreEntry(t(1, 44), 89),
            ScoreEntry(t(1, 45), 88), ScoreEntry(t(1, 46), 87), ScoreEntry(t(1, 47), 86),
            ScoreEntry(t(1, 48), 85), ScoreEntry(t(1, 49), 84), ScoreEntry(t(1, 50), 83),
            ScoreEntry(t(1, 51), 82), ScoreEntry(t(1, 52), 81), ScoreEntry(t(1, 53), 80),
            ScoreEntry(t(1, 54), 79), ScoreEntry(t(1, 55), 78), ScoreEntry(t(1, 56), 77),
            ScoreEntry(t(1, 58), 76), ScoreEntry(t(1, 59), 75), ScoreEntry(t(2, 0), 74),
            ScoreEntry(t(2, 1), 73), ScoreEntry(t(2, 2), 72), ScoreEntry(t(2, 3), 71),
            ScoreEntry(t(2, 5), 70), ScoreEntry(t(2, 7), 69), ScoreEntry(t(2, 8), 68),
            ScoreEntry(t(2, 10), 67), ScoreEntry(t(2, 11), 66), ScoreEntry(t(2, 14), 65),
            ScoreEntry(t(2, 16), 64), ScoreEntry(t(2, 18), 63), ScoreEntry(t(2, 21), 62),
            ScoreEntry(t(2, 26), 61), ScoreEntry(t(2, 31), 60),
            ScoreEntry(t(2, 41), 50), ScoreEntry(t(2, 51), 40), ScoreEntry(t(3, 1), 30),
            ScoreEntry(t(3, 11), 20), ScoreEntry(t(3, 21), 10), ScoreEntry(t(3, 31), 0)
        ),
        AgeBracket.AGE_27_31 to listOf(
            ScoreEntry(t(1, 30), 100), ScoreEntry(t(1, 31), 99), ScoreEntry(t(1, 34), 98),
            ScoreEntry(t(1, 35), 97), ScoreEntry(t(1, 37), 96), ScoreEntry(t(1, 38), 95),
            ScoreEntry(t(1, 40), 94), ScoreEntry(t(1, 41), 93), ScoreEntry(t(1, 42), 92),
            ScoreEntry(t(1, 43), 91), ScoreEntry(t(1, 45), 90), ScoreEntry(t(1, 46), 89),
            ScoreEntry(t(1, 47), 88), ScoreEntry(t(1, 48), 87), ScoreEntry(t(1, 49), 86),
            ScoreEntry(t(1, 50), 85), ScoreEntry(t(1, 51), 84), ScoreEntry(t(1, 52), 83),
            ScoreEntry(t(1, 53), 82), ScoreEntry(t(1, 54), 81), ScoreEntry(t(1, 55), 80),
            ScoreEntry(t(1, 56), 79), ScoreEntry(t(1, 57), 78), ScoreEntry(t(1, 58), 77),
            ScoreEntry(t(1, 59), 76), ScoreEntry(t(2, 0), 75), ScoreEntry(t(2, 1), 74),
            ScoreEntry(t(2, 2), 73), ScoreEntry(t(2, 4), 72), ScoreEntry(t(2, 5), 71),
            ScoreEntry(t(2, 6), 70), ScoreEntry(t(2, 8), 69), ScoreEntry(t(2, 10), 68),
            ScoreEntry(t(2, 11), 67), ScoreEntry(t(2, 13), 66), ScoreEntry(t(2, 15), 65),
            ScoreEntry(t(2, 17), 64), ScoreEntry(t(2, 20), 63), ScoreEntry(t(2, 22), 62),
            ScoreEntry(t(2, 28), 61), ScoreEntry(t(2, 32), 60),
            ScoreEntry(t(2, 42), 50), ScoreEntry(t(2, 52), 40), ScoreEntry(t(3, 2), 30),
            ScoreEntry(t(3, 12), 20), ScoreEntry(t(3, 22), 10), ScoreEntry(t(3, 32), 0)
        ),
        AgeBracket.AGE_32_36 to listOf(
            ScoreEntry(t(1, 33), 100), ScoreEntry(t(1, 34), 99), ScoreEntry(t(1, 37), 98),
            ScoreEntry(t(1, 38), 97), ScoreEntry(t(1, 40), 96), ScoreEntry(t(1, 41), 95),
            ScoreEntry(t(1, 43), 94), ScoreEntry(t(1, 44), 93), ScoreEntry(t(1, 45), 92),
            ScoreEntry(t(1, 46), 91), ScoreEntry(t(1, 48), 90), ScoreEntry(t(1, 49), 89),
            ScoreEntry(t(1, 50), 88), ScoreEntry(t(1, 51), 87), ScoreEntry(t(1, 52), 86),
            ScoreEntry(t(1, 53), 85), ScoreEntry(t(1, 54), 84), ScoreEntry(t(1, 55), 83),
            ScoreEntry(t(1, 56), 82), ScoreEntry(t(1, 57), 81), ScoreEntry(t(1, 58), 80),
            ScoreEntry(t(1, 59), 79), ScoreEntry(t(2, 0), 78), ScoreEntry(t(2, 1), 77),
            ScoreEntry(t(2, 2), 76), ScoreEntry(t(2, 3), 75), ScoreEntry(t(2, 4), 74),
            ScoreEntry(t(2, 5), 73), ScoreEntry(t(2, 7), 72), ScoreEntry(t(2, 8), 71),
            ScoreEntry(t(2, 10), 70), ScoreEntry(t(2, 11), 69), ScoreEntry(t(2, 13), 68),
            ScoreEntry(t(2, 15), 67), ScoreEntry(t(2, 16), 66), ScoreEntry(t(2, 19), 65),
            ScoreEntry(t(2, 21), 64), ScoreEntry(t(2, 24), 63), ScoreEntry(t(2, 26), 62),
            ScoreEntry(t(2, 31), 61), ScoreEntry(t(2, 36), 60),
            ScoreEntry(t(2, 46), 50), ScoreEntry(t(2, 56), 40), ScoreEntry(t(3, 6), 30),
            ScoreEntry(t(3, 16), 20), ScoreEntry(t(3, 26), 10), ScoreEntry(t(3, 36), 0)
        ),
        AgeBracket.AGE_37_41 to listOf(
            ScoreEntry(t(1, 36), 100), ScoreEntry(t(1, 37), 99), ScoreEntry(t(1, 40), 98),
            ScoreEntry(t(1, 42), 97), ScoreEntry(t(1, 43), 96), ScoreEntry(t(1, 45), 95),
            ScoreEntry(t(1, 47), 94), ScoreEntry(t(1, 48), 93), ScoreEntry(t(1, 49), 92),
            ScoreEntry(t(1, 50), 91), ScoreEntry(t(1, 52), 90), ScoreEntry(t(1, 53), 89),
            ScoreEntry(t(1, 54), 88), ScoreEntry(t(1, 55), 87), ScoreEntry(t(1, 56), 86),
            ScoreEntry(t(1, 57), 85), ScoreEntry(t(1, 58), 84), ScoreEntry(t(1, 59), 83),
            ScoreEntry(t(2, 0), 82), ScoreEntry(t(2, 1), 81), ScoreEntry(t(2, 2), 80),
            ScoreEntry(t(2, 3), 79), ScoreEntry(t(2, 4), 78), ScoreEntry(t(2, 5), 77),
            ScoreEntry(t(2, 7), 76), ScoreEntry(t(2, 8), 75), ScoreEntry(t(2, 9), 74),
            ScoreEntry(t(2, 10), 73), ScoreEntry(t(2, 12), 72), ScoreEntry(t(2, 13), 71),
            ScoreEntry(t(2, 14), 70), ScoreEntry(t(2, 16), 69), ScoreEntry(t(2, 18), 68),
            ScoreEntry(t(2, 20), 67), ScoreEntry(t(2, 21), 66), ScoreEntry(t(2, 24), 65),
            ScoreEntry(t(2, 26), 64), ScoreEntry(t(2, 28), 63), ScoreEntry(t(2, 31), 62),
            ScoreEntry(t(2, 36), 61), ScoreEntry(t(2, 41), 60),
            ScoreEntry(t(2, 51), 50), ScoreEntry(t(3, 1), 40), ScoreEntry(t(3, 11), 30),
            ScoreEntry(t(3, 21), 20), ScoreEntry(t(3, 31), 10), ScoreEntry(t(3, 41), 0)
        ),
        AgeBracket.AGE_42_46 to listOf(
            ScoreEntry(t(1, 40), 100), ScoreEntry(t(1, 42), 99), ScoreEntry(t(1, 44), 98),
            ScoreEntry(t(1, 46), 97), ScoreEntry(t(1, 48), 96), ScoreEntry(t(1, 49), 95),
            ScoreEntry(t(1, 51), 94), ScoreEntry(t(1, 52), 93), ScoreEntry(t(1, 53), 92),
            ScoreEntry(t(1, 54), 91), ScoreEntry(t(1, 56), 90), ScoreEntry(t(1, 57), 89),
            ScoreEntry(t(1, 58), 88), ScoreEntry(t(1, 59), 87), ScoreEntry(t(2, 0), 86),
            ScoreEntry(t(2, 1), 85), ScoreEntry(t(2, 2), 84), ScoreEntry(t(2, 4), 83),
            ScoreEntry(t(2, 5), 82), ScoreEntry(t(2, 6), 81), ScoreEntry(t(2, 7), 80),
            ScoreEntry(t(2, 8), 79), ScoreEntry(t(2, 9), 78), ScoreEntry(t(2, 10), 77),
            ScoreEntry(t(2, 12), 76), ScoreEntry(t(2, 13), 75), ScoreEntry(t(2, 14), 74),
            ScoreEntry(t(2, 15), 73), ScoreEntry(t(2, 17), 72), ScoreEntry(t(2, 18), 71),
            ScoreEntry(t(2, 20), 70), ScoreEntry(t(2, 22), 69), ScoreEntry(t(2, 23), 68),
            ScoreEntry(t(2, 25), 67), ScoreEntry(t(2, 26), 66), ScoreEntry(t(2, 29), 65),
            ScoreEntry(t(2, 31), 64), ScoreEntry(t(2, 33), 63), ScoreEntry(t(2, 36), 62),
            ScoreEntry(t(2, 41), 61), ScoreEntry(t(2, 45), 60),
            ScoreEntry(t(2, 55), 50), ScoreEntry(t(3, 5), 40), ScoreEntry(t(3, 15), 30),
            ScoreEntry(t(3, 25), 20), ScoreEntry(t(3, 35), 10), ScoreEntry(t(3, 45), 0)
        ),
        AgeBracket.AGE_47_51 to listOf(
            ScoreEntry(t(1, 45), 100), ScoreEntry(t(1, 46), 99), ScoreEntry(t(1, 50), 98),
            ScoreEntry(t(1, 52), 97), ScoreEntry(t(1, 54), 96), ScoreEntry(t(1, 55), 95),
            ScoreEntry(t(1, 57), 94), ScoreEntry(t(1, 59), 93), ScoreEntry(t(2, 0), 92),
            ScoreEntry(t(2, 1), 91), ScoreEntry(t(2, 2), 90), ScoreEntry(t(2, 3), 89),
            ScoreEntry(t(2, 5), 88), ScoreEntry(t(2, 6), 87), ScoreEntry(t(2, 7), 86),
            ScoreEntry(t(2, 8), 85), ScoreEntry(t(2, 9), 84), ScoreEntry(t(2, 10), 83),
            ScoreEntry(t(2, 12), 82), ScoreEntry(t(2, 13), 81), ScoreEntry(t(2, 14), 80),
            ScoreEntry(t(2, 15), 79), ScoreEntry(t(2, 16), 78), ScoreEntry(t(2, 17), 77),
            ScoreEntry(t(2, 19), 76), ScoreEntry(t(2, 20), 75), ScoreEntry(t(2, 21), 74),
            ScoreEntry(t(2, 23), 73), ScoreEntry(t(2, 25), 72), ScoreEntry(t(2, 26), 71),
            ScoreEntry(t(2, 27), 70), ScoreEntry(t(2, 29), 69), ScoreEntry(t(2, 30), 68),
            ScoreEntry(t(2, 32), 67), ScoreEntry(t(2, 34), 66), ScoreEntry(t(2, 37), 65),
            ScoreEntry(t(2, 39), 64), ScoreEntry(t(2, 41), 63), ScoreEntry(t(2, 44), 62),
            ScoreEntry(t(2, 48), 61), ScoreEntry(t(2, 53), 60),
            ScoreEntry(t(3, 3), 50), ScoreEntry(t(3, 13), 40), ScoreEntry(t(3, 23), 30),
            ScoreEntry(t(3, 33), 20), ScoreEntry(t(3, 43), 10), ScoreEntry(t(3, 53), 0)
        ),
        AgeBracket.AGE_52_56 to listOf(
            ScoreEntry(t(1, 52), 100), ScoreEntry(t(1, 55), 99), ScoreEntry(t(1, 57), 98),
            ScoreEntry(t(2, 0), 97), ScoreEntry(t(2, 1), 96), ScoreEntry(t(2, 3), 95),
            ScoreEntry(t(2, 5), 94), ScoreEntry(t(2, 6), 93), ScoreEntry(t(2, 7), 92),
            ScoreEntry(t(2, 9), 91), ScoreEntry(t(2, 10), 90), ScoreEntry(t(2, 11), 89),
            ScoreEntry(t(2, 13), 88), ScoreEntry(t(2, 14), 87), ScoreEntry(t(2, 15), 86),
            ScoreEntry(t(2, 16), 85), ScoreEntry(t(2, 17), 84), ScoreEntry(t(2, 19), 83),
            ScoreEntry(t(2, 20), 82), ScoreEntry(t(2, 21), 81), ScoreEntry(t(2, 23), 80),
            ScoreEntry(t(2, 25), 78), ScoreEntry(t(2, 26), 77), ScoreEntry(t(2, 28), 76),
            ScoreEntry(t(2, 29), 75), ScoreEntry(t(2, 30), 74), ScoreEntry(t(2, 31), 73),
            ScoreEntry(t(2, 32), 72), ScoreEntry(t(2, 34), 71), ScoreEntry(t(2, 35), 70),
            ScoreEntry(t(2, 37), 69), ScoreEntry(t(2, 38), 68), ScoreEntry(t(2, 40), 67),
            ScoreEntry(t(2, 41), 66), ScoreEntry(t(2, 44), 65), ScoreEntry(t(2, 46), 64),
            ScoreEntry(t(2, 48), 63), ScoreEntry(t(2, 50), 62), ScoreEntry(t(2, 57), 61),
            ScoreEntry(t(3, 0), 60),
            ScoreEntry(t(3, 10), 50), ScoreEntry(t(3, 20), 40), ScoreEntry(t(3, 30), 30),
            ScoreEntry(t(3, 40), 20), ScoreEntry(t(3, 50), 10), ScoreEntry(t(4, 0), 0)
        ),
        AgeBracket.AGE_57_61 to listOf(
            ScoreEntry(t(1, 58), 100), ScoreEntry(t(2, 2), 99), ScoreEntry(t(2, 3), 98),
            ScoreEntry(t(2, 6), 97), ScoreEntry(t(2, 8), 96), ScoreEntry(t(2, 9), 95),
            ScoreEntry(t(2, 11), 94), ScoreEntry(t(2, 13), 93), ScoreEntry(t(2, 15), 92),
            ScoreEntry(t(2, 16), 91), ScoreEntry(t(2, 17), 90), ScoreEntry(t(2, 19), 89),
            ScoreEntry(t(2, 20), 88), ScoreEntry(t(2, 21), 87), ScoreEntry(t(2, 22), 86),
            ScoreEntry(t(2, 23), 85), ScoreEntry(t(2, 24), 84), ScoreEntry(t(2, 26), 83),
            ScoreEntry(t(2, 27), 82), ScoreEntry(t(2, 28), 81), ScoreEntry(t(2, 29), 80),
            ScoreEntry(t(2, 30), 79), ScoreEntry(t(2, 31), 78), ScoreEntry(t(2, 33), 77),
            ScoreEntry(t(2, 35), 76), ScoreEntry(t(2, 36), 75), ScoreEntry(t(2, 37), 74),
            ScoreEntry(t(2, 38), 73), ScoreEntry(t(2, 40), 72), ScoreEntry(t(2, 42), 71),
            ScoreEntry(t(2, 43), 70), ScoreEntry(t(2, 45), 69), ScoreEntry(t(2, 47), 68),
            ScoreEntry(t(2, 48), 67), ScoreEntry(t(2, 50), 66), ScoreEntry(t(2, 53), 65),
            ScoreEntry(t(2, 55), 64), ScoreEntry(t(2, 57), 63), ScoreEntry(t(2, 59), 62),
            ScoreEntry(t(3, 4), 61), ScoreEntry(t(3, 12), 60),
            ScoreEntry(t(3, 22), 50), ScoreEntry(t(3, 32), 40), ScoreEntry(t(3, 42), 30),
            ScoreEntry(t(3, 52), 20), ScoreEntry(t(4, 2), 10), ScoreEntry(t(4, 12), 0)
        ),
        AgeBracket.AGE_62_PLUS to listOf(
            ScoreEntry(t(2, 9), 100), ScoreEntry(t(2, 12), 99), ScoreEntry(t(2, 13), 97),
            ScoreEntry(t(2, 14), 95), ScoreEntry(t(2, 15), 94), ScoreEntry(t(2, 16), 93),
            ScoreEntry(t(2, 17), 89), ScoreEntry(t(2, 18), 88), ScoreEntry(t(2, 19), 87),
            ScoreEntry(t(2, 20), 86), ScoreEntry(t(2, 21), 85), ScoreEntry(t(2, 22), 84),
            ScoreEntry(t(2, 23), 83), ScoreEntry(t(2, 24), 82), ScoreEntry(t(2, 27), 81),
            ScoreEntry(t(2, 32), 80), ScoreEntry(t(2, 33), 79), ScoreEntry(t(2, 35), 78),
            ScoreEntry(t(2, 36), 77), ScoreEntry(t(2, 38), 76), ScoreEntry(t(2, 41), 75),
            ScoreEntry(t(2, 43), 74), ScoreEntry(t(2, 44), 73), ScoreEntry(t(2, 46), 72),
            ScoreEntry(t(2, 47), 71), ScoreEntry(t(2, 49), 70), ScoreEntry(t(2, 52), 69),
            ScoreEntry(t(2, 56), 68), ScoreEntry(t(2, 57), 67), ScoreEntry(t(3, 0), 66),
            ScoreEntry(t(3, 3), 65), ScoreEntry(t(3, 9), 64), ScoreEntry(t(3, 11), 63),
            ScoreEntry(t(3, 12), 62), ScoreEntry(t(3, 14), 61), ScoreEntry(t(3, 16), 60),
            ScoreEntry(t(3, 26), 50), ScoreEntry(t(3, 36), 40), ScoreEntry(t(3, 46), 30),
            ScoreEntry(t(3, 56), 20), ScoreEntry(t(4, 6), 10), ScoreEntry(t(4, 16), 0)
        )
    )

    // Female SDC tables have higher time allowances
    val sdcFemale: Map<AgeBracket, List<ScoreEntry>> = mapOf(
        AgeBracket.AGE_17_21 to listOf(
            ScoreEntry(t(1, 55), 100), ScoreEntry(t(1, 59), 99), ScoreEntry(t(2, 2), 98),
            ScoreEntry(t(2, 5), 97), ScoreEntry(t(2, 6), 96), ScoreEntry(t(2, 8), 95),
            ScoreEntry(t(2, 10), 94), ScoreEntry(t(2, 12), 93), ScoreEntry(t(2, 13), 92),
            ScoreEntry(t(2, 14), 91), ScoreEntry(t(2, 16), 90), ScoreEntry(t(2, 17), 89),
            ScoreEntry(t(2, 18), 88), ScoreEntry(t(2, 20), 87), ScoreEntry(t(2, 21), 86),
            ScoreEntry(t(2, 22), 85), ScoreEntry(t(2, 23), 84), ScoreEntry(t(2, 24), 83),
            ScoreEntry(t(2, 25), 82), ScoreEntry(t(2, 26), 81), ScoreEntry(t(2, 28), 80),
            ScoreEntry(t(2, 29), 79), ScoreEntry(t(2, 30), 78), ScoreEntry(t(2, 31), 77),
            ScoreEntry(t(2, 33), 76), ScoreEntry(t(2, 34), 75), ScoreEntry(t(2, 35), 74),
            ScoreEntry(t(2, 37), 73), ScoreEntry(t(2, 39), 72), ScoreEntry(t(2, 40), 71),
            ScoreEntry(t(2, 41), 70), ScoreEntry(t(2, 44), 69), ScoreEntry(t(2, 45), 68),
            ScoreEntry(t(2, 47), 67), ScoreEntry(t(2, 49), 66), ScoreEntry(t(2, 53), 65),
            ScoreEntry(t(2, 55), 64), ScoreEntry(t(2, 58), 63), ScoreEntry(t(3, 0), 62),
            ScoreEntry(t(3, 8), 61), ScoreEntry(t(3, 15), 60),
            ScoreEntry(t(3, 25), 50), ScoreEntry(t(3, 35), 40), ScoreEntry(t(3, 45), 30),
            ScoreEntry(t(3, 55), 20), ScoreEntry(t(4, 5), 10), ScoreEntry(t(4, 15), 0)
        ),
        // Simplified - using age 17-21 female table as baseline for other age brackets
        // In production, each age bracket would have its own table from the PDF
        AgeBracket.AGE_22_26 to listOf(
            ScoreEntry(t(1, 55), 100), ScoreEntry(t(2, 0), 98), ScoreEntry(t(2, 5), 96),
            ScoreEntry(t(2, 10), 94), ScoreEntry(t(2, 15), 92), ScoreEntry(t(2, 20), 88),
            ScoreEntry(t(2, 25), 84), ScoreEntry(t(2, 30), 80), ScoreEntry(t(2, 35), 76),
            ScoreEntry(t(2, 40), 72), ScoreEntry(t(2, 45), 68), ScoreEntry(t(2, 50), 64),
            ScoreEntry(t(3, 0), 62), ScoreEntry(t(3, 15), 60),
            ScoreEntry(t(3, 25), 50), ScoreEntry(t(3, 35), 40), ScoreEntry(t(3, 45), 30),
            ScoreEntry(t(3, 55), 20), ScoreEntry(t(4, 5), 10), ScoreEntry(t(4, 15), 0)
        ),
        AgeBracket.AGE_27_31 to listOf(
            ScoreEntry(t(1, 55), 100), ScoreEntry(t(2, 1), 98), ScoreEntry(t(2, 6), 96),
            ScoreEntry(t(2, 12), 94), ScoreEntry(t(2, 16), 92), ScoreEntry(t(2, 20), 88),
            ScoreEntry(t(2, 26), 84), ScoreEntry(t(2, 31), 80), ScoreEntry(t(2, 36), 76),
            ScoreEntry(t(2, 43), 70), ScoreEntry(t(2, 50), 66), ScoreEntry(t(3, 0), 62),
            ScoreEntry(t(3, 15), 60),
            ScoreEntry(t(3, 25), 50), ScoreEntry(t(3, 35), 40), ScoreEntry(t(3, 45), 30),
            ScoreEntry(t(3, 55), 20), ScoreEntry(t(4, 5), 10), ScoreEntry(t(4, 15), 0)
        ),
        AgeBracket.AGE_32_36 to listOf(
            ScoreEntry(t(1, 59), 100), ScoreEntry(t(2, 5), 98), ScoreEntry(t(2, 10), 96),
            ScoreEntry(t(2, 15), 94), ScoreEntry(t(2, 20), 92), ScoreEntry(t(2, 26), 88),
            ScoreEntry(t(2, 32), 84), ScoreEntry(t(2, 38), 80), ScoreEntry(t(2, 45), 75),
            ScoreEntry(t(2, 52), 70), ScoreEntry(t(3, 0), 66), ScoreEntry(t(3, 10), 62),
            ScoreEntry(t(3, 22), 60),
            ScoreEntry(t(3, 32), 50), ScoreEntry(t(3, 42), 40), ScoreEntry(t(3, 52), 30),
            ScoreEntry(t(4, 2), 20), ScoreEntry(t(4, 12), 10), ScoreEntry(t(4, 22), 0)
        ),
        AgeBracket.AGE_37_41 to listOf(
            ScoreEntry(t(2, 2), 100), ScoreEntry(t(2, 10), 98), ScoreEntry(t(2, 15), 96),
            ScoreEntry(t(2, 21), 94), ScoreEntry(t(2, 26), 92), ScoreEntry(t(2, 32), 88),
            ScoreEntry(t(2, 38), 84), ScoreEntry(t(2, 45), 80), ScoreEntry(t(2, 52), 75),
            ScoreEntry(t(3, 0), 70), ScoreEntry(t(3, 8), 66), ScoreEntry(t(3, 17), 62),
            ScoreEntry(t(3, 27), 60),
            ScoreEntry(t(3, 37), 50), ScoreEntry(t(3, 47), 40), ScoreEntry(t(3, 57), 30),
            ScoreEntry(t(4, 7), 20), ScoreEntry(t(4, 17), 10), ScoreEntry(t(4, 27), 0)
        ),
        AgeBracket.AGE_42_46 to listOf(
            ScoreEntry(t(2, 9), 100), ScoreEntry(t(2, 15), 98), ScoreEntry(t(2, 20), 96),
            ScoreEntry(t(2, 27), 94), ScoreEntry(t(2, 33), 92), ScoreEntry(t(2, 40), 88),
            ScoreEntry(t(2, 47), 84), ScoreEntry(t(2, 55), 80), ScoreEntry(t(3, 5), 75),
            ScoreEntry(t(3, 14), 70), ScoreEntry(t(3, 24), 66), ScoreEntry(t(3, 35), 62),
            ScoreEntry(t(3, 42), 60),
            ScoreEntry(t(3, 52), 50), ScoreEntry(t(4, 2), 40), ScoreEntry(t(4, 12), 30),
            ScoreEntry(t(4, 22), 20), ScoreEntry(t(4, 32), 10), ScoreEntry(t(4, 42), 0)
        ),
        AgeBracket.AGE_47_51 to listOf(
            ScoreEntry(t(2, 11), 100), ScoreEntry(t(2, 22), 98), ScoreEntry(t(2, 30), 96),
            ScoreEntry(t(2, 37), 94), ScoreEntry(t(2, 44), 92), ScoreEntry(t(2, 52), 88),
            ScoreEntry(t(3, 0), 84), ScoreEntry(t(3, 10), 80), ScoreEntry(t(3, 21), 75),
            ScoreEntry(t(3, 32), 70), ScoreEntry(t(3, 42), 66), ScoreEntry(t(3, 51), 60),
            ScoreEntry(t(4, 1), 50), ScoreEntry(t(4, 11), 40), ScoreEntry(t(4, 21), 30),
            ScoreEntry(t(4, 31), 20), ScoreEntry(t(4, 41), 10), ScoreEntry(t(4, 51), 0)
        ),
        AgeBracket.AGE_52_56 to listOf(
            ScoreEntry(t(2, 18), 100), ScoreEntry(t(2, 28), 98), ScoreEntry(t(2, 38), 96),
            ScoreEntry(t(2, 48), 94), ScoreEntry(t(2, 58), 92), ScoreEntry(t(3, 10), 88),
            ScoreEntry(t(3, 22), 84), ScoreEntry(t(3, 35), 80), ScoreEntry(t(3, 48), 75),
            ScoreEntry(t(4, 3), 60),
            ScoreEntry(t(4, 13), 50), ScoreEntry(t(4, 23), 40), ScoreEntry(t(4, 33), 30),
            ScoreEntry(t(4, 43), 20), ScoreEntry(t(4, 53), 10), ScoreEntry(t(5, 3), 0)
        ),
        AgeBracket.AGE_57_61 to listOf(
            ScoreEntry(t(2, 26), 100), ScoreEntry(t(2, 34), 98), ScoreEntry(t(2, 45), 96),
            ScoreEntry(t(2, 55), 94), ScoreEntry(t(3, 7), 92), ScoreEntry(t(3, 21), 88),
            ScoreEntry(t(3, 36), 84), ScoreEntry(t(3, 54), 80), ScoreEntry(t(4, 16), 70),
            ScoreEntry(t(4, 48), 60),
            ScoreEntry(t(4, 58), 50), ScoreEntry(t(5, 8), 40), ScoreEntry(t(5, 18), 30),
            ScoreEntry(t(5, 28), 20), ScoreEntry(t(5, 38), 10), ScoreEntry(t(5, 48), 0)
        ),
        AgeBracket.AGE_62_PLUS to listOf(
            ScoreEntry(t(2, 26), 100), ScoreEntry(t(2, 34), 98), ScoreEntry(t(2, 45), 96),
            ScoreEntry(t(2, 55), 94), ScoreEntry(t(3, 7), 92), ScoreEntry(t(3, 21), 88),
            ScoreEntry(t(3, 36), 84), ScoreEntry(t(3, 54), 80), ScoreEntry(t(4, 16), 70),
            ScoreEntry(t(4, 48), 60),
            ScoreEntry(t(4, 58), 50), ScoreEntry(t(5, 8), 40), ScoreEntry(t(5, 18), 30),
            ScoreEntry(t(5, 28), 20), ScoreEntry(t(5, 38), 10), ScoreEntry(t(5, 48), 0)
        )
    )

    fun getSdcTable(category: OfficialScoreTables.ScoringCategory, ageBracket: AgeBracket): List<ScoreEntry> {
        return when (category) {
            OfficialScoreTables.ScoringCategory.MALE_COMBAT -> sdcMaleCombat[ageBracket] ?: sdcMaleCombat[AgeBracket.AGE_17_21]!!
            OfficialScoreTables.ScoringCategory.FEMALE -> sdcFemale[ageBracket] ?: sdcFemale[AgeBracket.AGE_17_21]!!
        }
    }

    // ==================== PLANK (PLK) ====================
    // Higher time is better - Gender neutral scoring
    val plank: Map<AgeBracket, List<ScoreEntry>> = mapOf(
        AgeBracket.AGE_17_21 to listOf(
            ScoreEntry(t(3, 40), 100), ScoreEntry(t(3, 37), 99), ScoreEntry(t(3, 34), 98),
            ScoreEntry(t(3, 30), 97), ScoreEntry(t(3, 27), 96), ScoreEntry(t(3, 24), 95),
            ScoreEntry(t(3, 21), 94), ScoreEntry(t(3, 17), 93), ScoreEntry(t(3, 14), 92),
            ScoreEntry(t(3, 11), 91), ScoreEntry(t(3, 8), 90), ScoreEntry(t(3, 4), 89),
            ScoreEntry(t(3, 1), 88), ScoreEntry(t(2, 58), 87), ScoreEntry(t(2, 55), 86),
            ScoreEntry(t(2, 51), 85), ScoreEntry(t(2, 48), 84), ScoreEntry(t(2, 45), 83),
            ScoreEntry(t(2, 41), 82), ScoreEntry(t(2, 38), 81), ScoreEntry(t(2, 35), 80),
            ScoreEntry(t(2, 32), 79), ScoreEntry(t(2, 29), 78), ScoreEntry(t(2, 25), 77),
            ScoreEntry(t(2, 22), 76), ScoreEntry(t(2, 19), 75), ScoreEntry(t(2, 15), 74),
            ScoreEntry(t(2, 12), 73), ScoreEntry(t(2, 9), 72), ScoreEntry(t(2, 6), 71),
            ScoreEntry(t(2, 2), 70), ScoreEntry(t(1, 59), 69), ScoreEntry(t(1, 56), 68),
            ScoreEntry(t(1, 53), 67), ScoreEntry(t(1, 49), 66), ScoreEntry(t(1, 46), 65),
            ScoreEntry(t(1, 43), 64), ScoreEntry(t(1, 40), 63), ScoreEntry(t(1, 37), 62),
            ScoreEntry(t(1, 33), 61), ScoreEntry(t(1, 30), 60),
            ScoreEntry(t(1, 25), 50), ScoreEntry(t(1, 20), 40), ScoreEntry(t(1, 15), 30),
            ScoreEntry(t(1, 10), 20), ScoreEntry(t(1, 5), 10), ScoreEntry(t(1, 0), 0)
        ),
        AgeBracket.AGE_22_26 to listOf(
            ScoreEntry(t(3, 35), 100), ScoreEntry(t(3, 32), 99), ScoreEntry(t(3, 29), 98),
            ScoreEntry(t(3, 25), 97), ScoreEntry(t(3, 22), 96), ScoreEntry(t(3, 19), 95),
            ScoreEntry(t(3, 16), 94), ScoreEntry(t(3, 12), 93), ScoreEntry(t(3, 9), 92),
            ScoreEntry(t(3, 6), 91), ScoreEntry(t(3, 3), 90), ScoreEntry(t(2, 59), 89),
            ScoreEntry(t(2, 56), 88), ScoreEntry(t(2, 53), 87), ScoreEntry(t(2, 50), 86),
            ScoreEntry(t(2, 46), 85), ScoreEntry(t(2, 43), 84), ScoreEntry(t(2, 40), 83),
            ScoreEntry(t(2, 37), 82), ScoreEntry(t(2, 33), 81), ScoreEntry(t(2, 30), 80),
            ScoreEntry(t(2, 27), 79), ScoreEntry(t(2, 23), 78), ScoreEntry(t(2, 20), 77),
            ScoreEntry(t(2, 17), 76), ScoreEntry(t(2, 14), 75), ScoreEntry(t(2, 10), 74),
            ScoreEntry(t(2, 7), 73), ScoreEntry(t(2, 4), 72), ScoreEntry(t(2, 1), 71),
            ScoreEntry(t(1, 58), 70), ScoreEntry(t(1, 54), 69), ScoreEntry(t(1, 51), 68),
            ScoreEntry(t(1, 48), 67), ScoreEntry(t(1, 45), 66), ScoreEntry(t(1, 41), 65),
            ScoreEntry(t(1, 38), 64), ScoreEntry(t(1, 35), 63), ScoreEntry(t(1, 32), 62),
            ScoreEntry(t(1, 28), 61), ScoreEntry(t(1, 25), 60),
            ScoreEntry(t(1, 20), 50), ScoreEntry(t(1, 15), 40), ScoreEntry(t(1, 10), 30),
            ScoreEntry(t(1, 5), 20), ScoreEntry(t(1, 0), 10), ScoreEntry(t(0, 55), 0)
        ),
        AgeBracket.AGE_27_31 to listOf(
            ScoreEntry(t(3, 30), 100), ScoreEntry(t(3, 27), 99), ScoreEntry(t(3, 24), 98),
            ScoreEntry(t(3, 20), 97), ScoreEntry(t(3, 17), 96), ScoreEntry(t(3, 14), 95),
            ScoreEntry(t(3, 11), 94), ScoreEntry(t(3, 7), 93), ScoreEntry(t(3, 4), 92),
            ScoreEntry(t(3, 1), 91), ScoreEntry(t(2, 58), 90), ScoreEntry(t(2, 54), 89),
            ScoreEntry(t(2, 51), 88), ScoreEntry(t(2, 48), 87), ScoreEntry(t(2, 45), 86),
            ScoreEntry(t(2, 41), 85), ScoreEntry(t(2, 38), 84), ScoreEntry(t(2, 35), 83),
            ScoreEntry(t(2, 31), 82), ScoreEntry(t(2, 28), 81), ScoreEntry(t(2, 25), 80),
            ScoreEntry(t(2, 22), 79), ScoreEntry(t(2, 18), 78), ScoreEntry(t(2, 15), 77),
            ScoreEntry(t(2, 12), 76), ScoreEntry(t(2, 9), 75), ScoreEntry(t(2, 6), 74),
            ScoreEntry(t(2, 2), 73), ScoreEntry(t(1, 59), 72), ScoreEntry(t(1, 56), 71),
            ScoreEntry(t(1, 52), 70), ScoreEntry(t(1, 49), 69), ScoreEntry(t(1, 46), 68),
            ScoreEntry(t(1, 43), 67), ScoreEntry(t(1, 39), 66), ScoreEntry(t(1, 36), 65),
            ScoreEntry(t(1, 33), 64), ScoreEntry(t(1, 30), 63), ScoreEntry(t(1, 26), 62),
            ScoreEntry(t(1, 23), 61), ScoreEntry(t(1, 20), 60),
            ScoreEntry(t(1, 15), 50), ScoreEntry(t(1, 10), 40), ScoreEntry(t(1, 5), 30),
            ScoreEntry(t(1, 0), 20), ScoreEntry(t(0, 55), 10), ScoreEntry(t(0, 50), 0)
        ),
        AgeBracket.AGE_32_36 to listOf(
            ScoreEntry(t(3, 25), 100), ScoreEntry(t(3, 22), 99), ScoreEntry(t(3, 19), 98),
            ScoreEntry(t(3, 15), 97), ScoreEntry(t(3, 12), 96), ScoreEntry(t(3, 9), 95),
            ScoreEntry(t(3, 6), 94), ScoreEntry(t(3, 2), 93), ScoreEntry(t(2, 59), 92),
            ScoreEntry(t(2, 56), 91), ScoreEntry(t(2, 53), 90), ScoreEntry(t(2, 49), 89),
            ScoreEntry(t(2, 46), 88), ScoreEntry(t(2, 43), 87), ScoreEntry(t(2, 40), 86),
            ScoreEntry(t(2, 36), 85), ScoreEntry(t(2, 33), 84), ScoreEntry(t(2, 30), 83),
            ScoreEntry(t(2, 27), 82), ScoreEntry(t(2, 23), 81), ScoreEntry(t(2, 20), 80),
            ScoreEntry(t(2, 17), 79), ScoreEntry(t(2, 13), 78), ScoreEntry(t(2, 10), 77),
            ScoreEntry(t(2, 7), 76), ScoreEntry(t(2, 4), 75), ScoreEntry(t(2, 0), 74),
            ScoreEntry(t(1, 57), 73), ScoreEntry(t(1, 54), 72), ScoreEntry(t(1, 51), 71),
            ScoreEntry(t(1, 47), 70), ScoreEntry(t(1, 44), 69), ScoreEntry(t(1, 41), 68),
            ScoreEntry(t(1, 38), 67), ScoreEntry(t(1, 35), 66), ScoreEntry(t(1, 31), 65),
            ScoreEntry(t(1, 28), 64), ScoreEntry(t(1, 25), 63), ScoreEntry(t(1, 22), 62),
            ScoreEntry(t(1, 18), 61), ScoreEntry(t(1, 15), 60),
            ScoreEntry(t(1, 10), 50), ScoreEntry(t(1, 5), 40), ScoreEntry(t(1, 0), 30),
            ScoreEntry(t(0, 55), 20), ScoreEntry(t(0, 50), 10), ScoreEntry(t(0, 45), 0)
        ),
        // Ages 37+ have same minimums but adjusted maximums
        AgeBracket.AGE_37_41 to listOf(
            ScoreEntry(t(3, 20), 100), ScoreEntry(t(3, 17), 99), ScoreEntry(t(3, 14), 98),
            ScoreEntry(t(3, 10), 97), ScoreEntry(t(3, 7), 96), ScoreEntry(t(3, 4), 95),
            ScoreEntry(t(3, 1), 94), ScoreEntry(t(2, 57), 93), ScoreEntry(t(2, 54), 92),
            ScoreEntry(t(2, 51), 91), ScoreEntry(t(2, 47), 90), ScoreEntry(t(2, 44), 89),
            ScoreEntry(t(2, 41), 88), ScoreEntry(t(2, 38), 87), ScoreEntry(t(2, 35), 86),
            ScoreEntry(t(2, 31), 85), ScoreEntry(t(2, 28), 84), ScoreEntry(t(2, 25), 83),
            ScoreEntry(t(2, 22), 82), ScoreEntry(t(2, 18), 81), ScoreEntry(t(2, 15), 80),
            ScoreEntry(t(2, 12), 79), ScoreEntry(t(2, 8), 78), ScoreEntry(t(2, 5), 77),
            ScoreEntry(t(2, 2), 76), ScoreEntry(t(1, 59), 75), ScoreEntry(t(1, 56), 74),
            ScoreEntry(t(1, 52), 73), ScoreEntry(t(1, 49), 72), ScoreEntry(t(1, 46), 71),
            ScoreEntry(t(1, 42), 70), ScoreEntry(t(1, 39), 69), ScoreEntry(t(1, 36), 68),
            ScoreEntry(t(1, 33), 67), ScoreEntry(t(1, 30), 66), ScoreEntry(t(1, 26), 65),
            ScoreEntry(t(1, 23), 64), ScoreEntry(t(1, 20), 63), ScoreEntry(t(1, 16), 62),
            ScoreEntry(t(1, 13), 61), ScoreEntry(t(1, 10), 60),
            ScoreEntry(t(1, 5), 50), ScoreEntry(t(1, 0), 40), ScoreEntry(t(0, 55), 30),
            ScoreEntry(t(0, 50), 20), ScoreEntry(t(0, 45), 10), ScoreEntry(t(0, 40), 0)
        ),
        AgeBracket.AGE_42_46 to listOf(
            ScoreEntry(t(3, 20), 100), ScoreEntry(t(3, 17), 99), ScoreEntry(t(3, 14), 98),
            ScoreEntry(t(3, 10), 97), ScoreEntry(t(3, 7), 96), ScoreEntry(t(3, 4), 95),
            ScoreEntry(t(3, 1), 94), ScoreEntry(t(2, 57), 93), ScoreEntry(t(2, 54), 92),
            ScoreEntry(t(2, 51), 91), ScoreEntry(t(2, 47), 90), ScoreEntry(t(2, 44), 89),
            ScoreEntry(t(2, 41), 88), ScoreEntry(t(2, 38), 87), ScoreEntry(t(2, 35), 86),
            ScoreEntry(t(2, 31), 85), ScoreEntry(t(2, 28), 84), ScoreEntry(t(2, 25), 83),
            ScoreEntry(t(2, 22), 82), ScoreEntry(t(2, 18), 81), ScoreEntry(t(2, 15), 80),
            ScoreEntry(t(2, 12), 79), ScoreEntry(t(2, 8), 78), ScoreEntry(t(2, 5), 77),
            ScoreEntry(t(2, 2), 76), ScoreEntry(t(1, 59), 75), ScoreEntry(t(1, 56), 74),
            ScoreEntry(t(1, 52), 73), ScoreEntry(t(1, 49), 72), ScoreEntry(t(1, 46), 71),
            ScoreEntry(t(1, 42), 70), ScoreEntry(t(1, 39), 69), ScoreEntry(t(1, 36), 68),
            ScoreEntry(t(1, 33), 67), ScoreEntry(t(1, 30), 66), ScoreEntry(t(1, 26), 65),
            ScoreEntry(t(1, 23), 64), ScoreEntry(t(1, 20), 63), ScoreEntry(t(1, 16), 62),
            ScoreEntry(t(1, 13), 61), ScoreEntry(t(1, 10), 60),
            ScoreEntry(t(1, 5), 50), ScoreEntry(t(1, 0), 40), ScoreEntry(t(0, 55), 30),
            ScoreEntry(t(0, 50), 20), ScoreEntry(t(0, 45), 10), ScoreEntry(t(0, 40), 0)
        ),
        AgeBracket.AGE_47_51 to listOf(
            ScoreEntry(t(3, 20), 100), ScoreEntry(t(3, 17), 99), ScoreEntry(t(3, 14), 98),
            ScoreEntry(t(3, 10), 97), ScoreEntry(t(3, 7), 96), ScoreEntry(t(3, 4), 95),
            ScoreEntry(t(3, 1), 94), ScoreEntry(t(2, 57), 93), ScoreEntry(t(2, 54), 92),
            ScoreEntry(t(2, 51), 91), ScoreEntry(t(2, 47), 90), ScoreEntry(t(2, 44), 89),
            ScoreEntry(t(2, 41), 88), ScoreEntry(t(2, 38), 87), ScoreEntry(t(2, 35), 86),
            ScoreEntry(t(2, 31), 85), ScoreEntry(t(2, 28), 84), ScoreEntry(t(2, 25), 83),
            ScoreEntry(t(2, 22), 82), ScoreEntry(t(2, 18), 81), ScoreEntry(t(2, 15), 80),
            ScoreEntry(t(2, 12), 79), ScoreEntry(t(2, 8), 78), ScoreEntry(t(2, 5), 77),
            ScoreEntry(t(2, 2), 76), ScoreEntry(t(1, 59), 75), ScoreEntry(t(1, 56), 74),
            ScoreEntry(t(1, 52), 73), ScoreEntry(t(1, 49), 72), ScoreEntry(t(1, 46), 71),
            ScoreEntry(t(1, 42), 70), ScoreEntry(t(1, 39), 69), ScoreEntry(t(1, 36), 68),
            ScoreEntry(t(1, 33), 67), ScoreEntry(t(1, 30), 66), ScoreEntry(t(1, 26), 65),
            ScoreEntry(t(1, 23), 64), ScoreEntry(t(1, 20), 63), ScoreEntry(t(1, 16), 62),
            ScoreEntry(t(1, 13), 61), ScoreEntry(t(1, 10), 60),
            ScoreEntry(t(1, 5), 50), ScoreEntry(t(1, 0), 40), ScoreEntry(t(0, 55), 30),
            ScoreEntry(t(0, 50), 20), ScoreEntry(t(0, 45), 10), ScoreEntry(t(0, 40), 0)
        ),
        AgeBracket.AGE_52_56 to listOf(
            ScoreEntry(t(3, 20), 100), ScoreEntry(t(3, 17), 99), ScoreEntry(t(3, 14), 98),
            ScoreEntry(t(3, 10), 97), ScoreEntry(t(3, 7), 96), ScoreEntry(t(3, 4), 95),
            ScoreEntry(t(3, 1), 94), ScoreEntry(t(2, 57), 93), ScoreEntry(t(2, 54), 92),
            ScoreEntry(t(2, 51), 91), ScoreEntry(t(2, 47), 90), ScoreEntry(t(2, 44), 89),
            ScoreEntry(t(2, 41), 88), ScoreEntry(t(2, 38), 87), ScoreEntry(t(2, 35), 86),
            ScoreEntry(t(2, 31), 85), ScoreEntry(t(2, 28), 84), ScoreEntry(t(2, 25), 83),
            ScoreEntry(t(2, 22), 82), ScoreEntry(t(2, 18), 81), ScoreEntry(t(2, 15), 80),
            ScoreEntry(t(2, 12), 79), ScoreEntry(t(2, 8), 78), ScoreEntry(t(2, 5), 77),
            ScoreEntry(t(2, 2), 76), ScoreEntry(t(1, 59), 75), ScoreEntry(t(1, 56), 74),
            ScoreEntry(t(1, 52), 73), ScoreEntry(t(1, 49), 72), ScoreEntry(t(1, 46), 71),
            ScoreEntry(t(1, 42), 70), ScoreEntry(t(1, 39), 69), ScoreEntry(t(1, 36), 68),
            ScoreEntry(t(1, 33), 67), ScoreEntry(t(1, 30), 66), ScoreEntry(t(1, 26), 65),
            ScoreEntry(t(1, 23), 64), ScoreEntry(t(1, 20), 63), ScoreEntry(t(1, 16), 62),
            ScoreEntry(t(1, 13), 61), ScoreEntry(t(1, 10), 60),
            ScoreEntry(t(1, 5), 50), ScoreEntry(t(1, 0), 40), ScoreEntry(t(0, 55), 30),
            ScoreEntry(t(0, 50), 20), ScoreEntry(t(0, 45), 10), ScoreEntry(t(0, 40), 0)
        ),
        AgeBracket.AGE_57_61 to listOf(
            ScoreEntry(t(3, 20), 100), ScoreEntry(t(3, 17), 99), ScoreEntry(t(3, 14), 98),
            ScoreEntry(t(3, 10), 97), ScoreEntry(t(3, 7), 96), ScoreEntry(t(3, 4), 95),
            ScoreEntry(t(3, 1), 94), ScoreEntry(t(2, 57), 93), ScoreEntry(t(2, 54), 92),
            ScoreEntry(t(2, 51), 91), ScoreEntry(t(2, 47), 90), ScoreEntry(t(2, 44), 89),
            ScoreEntry(t(2, 41), 88), ScoreEntry(t(2, 38), 87), ScoreEntry(t(2, 35), 86),
            ScoreEntry(t(2, 31), 85), ScoreEntry(t(2, 28), 84), ScoreEntry(t(2, 25), 83),
            ScoreEntry(t(2, 22), 82), ScoreEntry(t(2, 18), 81), ScoreEntry(t(2, 15), 80),
            ScoreEntry(t(2, 12), 79), ScoreEntry(t(2, 8), 78), ScoreEntry(t(2, 5), 77),
            ScoreEntry(t(2, 2), 76), ScoreEntry(t(1, 59), 75), ScoreEntry(t(1, 56), 74),
            ScoreEntry(t(1, 52), 73), ScoreEntry(t(1, 49), 72), ScoreEntry(t(1, 46), 71),
            ScoreEntry(t(1, 42), 70), ScoreEntry(t(1, 39), 69), ScoreEntry(t(1, 36), 68),
            ScoreEntry(t(1, 33), 67), ScoreEntry(t(1, 30), 66), ScoreEntry(t(1, 26), 65),
            ScoreEntry(t(1, 23), 64), ScoreEntry(t(1, 20), 63), ScoreEntry(t(1, 16), 62),
            ScoreEntry(t(1, 13), 61), ScoreEntry(t(1, 10), 60),
            ScoreEntry(t(1, 5), 50), ScoreEntry(t(1, 0), 40), ScoreEntry(t(0, 55), 30),
            ScoreEntry(t(0, 50), 20), ScoreEntry(t(0, 45), 10), ScoreEntry(t(0, 40), 0)
        ),
        AgeBracket.AGE_62_PLUS to listOf(
            ScoreEntry(t(3, 20), 100), ScoreEntry(t(3, 17), 99), ScoreEntry(t(3, 14), 98),
            ScoreEntry(t(3, 10), 97), ScoreEntry(t(3, 7), 96), ScoreEntry(t(3, 4), 95),
            ScoreEntry(t(3, 1), 94), ScoreEntry(t(2, 57), 93), ScoreEntry(t(2, 54), 92),
            ScoreEntry(t(2, 51), 91), ScoreEntry(t(2, 47), 90), ScoreEntry(t(2, 44), 89),
            ScoreEntry(t(2, 41), 88), ScoreEntry(t(2, 38), 87), ScoreEntry(t(2, 35), 86),
            ScoreEntry(t(2, 31), 85), ScoreEntry(t(2, 28), 84), ScoreEntry(t(2, 25), 83),
            ScoreEntry(t(2, 22), 82), ScoreEntry(t(2, 18), 81), ScoreEntry(t(2, 15), 80),
            ScoreEntry(t(2, 12), 79), ScoreEntry(t(2, 8), 78), ScoreEntry(t(2, 5), 77),
            ScoreEntry(t(2, 2), 76), ScoreEntry(t(1, 59), 75), ScoreEntry(t(1, 56), 74),
            ScoreEntry(t(1, 52), 73), ScoreEntry(t(1, 49), 72), ScoreEntry(t(1, 46), 71),
            ScoreEntry(t(1, 42), 70), ScoreEntry(t(1, 39), 69), ScoreEntry(t(1, 36), 68),
            ScoreEntry(t(1, 33), 67), ScoreEntry(t(1, 30), 66), ScoreEntry(t(1, 26), 65),
            ScoreEntry(t(1, 23), 64), ScoreEntry(t(1, 20), 63), ScoreEntry(t(1, 16), 62),
            ScoreEntry(t(1, 13), 61), ScoreEntry(t(1, 10), 60),
            ScoreEntry(t(1, 5), 50), ScoreEntry(t(1, 0), 40), ScoreEntry(t(0, 55), 30),
            ScoreEntry(t(0, 50), 20), ScoreEntry(t(0, 45), 10), ScoreEntry(t(0, 40), 0)
        )
    )

    fun getPlankTable(ageBracket: AgeBracket): List<ScoreEntry> {
        return plank[ageBracket] ?: plank[AgeBracket.AGE_17_21]!!
    }
}
