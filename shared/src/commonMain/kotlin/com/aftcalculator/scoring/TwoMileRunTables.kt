package com.aftcalculator.scoring

import com.aftcalculator.models.AgeBracket

/**
 * Two-Mile Run (2MR) Scoring Tables
 * Overall time in minutes and seconds
 * Lower time is better
 */
object TwoMileRunTables {

    // Helper to convert mm:ss to seconds
    private fun t(min: Int, sec: Int): Double = (min * 60 + sec).toDouble()

    val maleCombat: Map<AgeBracket, List<ScoreEntry>> = mapOf(
        AgeBracket.AGE_17_21 to listOf(
            ScoreEntry(t(13, 22), 100), ScoreEntry(t(13, 47), 99), ScoreEntry(t(14, 4), 98),
            ScoreEntry(t(14, 19), 97), ScoreEntry(t(14, 32), 96), ScoreEntry(t(14, 45), 95),
            ScoreEntry(t(14, 56), 94), ScoreEntry(t(15, 7), 93), ScoreEntry(t(15, 18), 92),
            ScoreEntry(t(15, 29), 91), ScoreEntry(t(15, 39), 90), ScoreEntry(t(15, 49), 89),
            ScoreEntry(t(15, 59), 88), ScoreEntry(t(16, 9), 87), ScoreEntry(t(16, 19), 86),
            ScoreEntry(t(16, 28), 85), ScoreEntry(t(16, 38), 84), ScoreEntry(t(16, 48), 83),
            ScoreEntry(t(16, 57), 82), ScoreEntry(t(17, 7), 81), ScoreEntry(t(17, 13), 80),
            ScoreEntry(t(17, 17), 79), ScoreEntry(t(17, 25), 78), ScoreEntry(t(17, 34), 77),
            ScoreEntry(t(17, 43), 76), ScoreEntry(t(17, 52), 75), ScoreEntry(t(18, 0), 74),
            ScoreEntry(t(18, 9), 73), ScoreEntry(t(18, 18), 72), ScoreEntry(t(18, 27), 71),
            ScoreEntry(t(18, 35), 70), ScoreEntry(t(18, 45), 69), ScoreEntry(t(18, 54), 68),
            ScoreEntry(t(19, 3), 67), ScoreEntry(t(19, 13), 66), ScoreEntry(t(19, 23), 65),
            ScoreEntry(t(19, 33), 64), ScoreEntry(t(19, 43), 63), ScoreEntry(t(19, 54), 62),
            ScoreEntry(t(19, 54), 61), ScoreEntry(t(19, 57), 60),
            ScoreEntry(t(20, 25), 50), ScoreEntry(t(20, 53), 40), ScoreEntry(t(21, 21), 30),
            ScoreEntry(t(21, 49), 20), ScoreEntry(t(22, 17), 10), ScoreEntry(t(22, 45), 0)
        ),
        AgeBracket.AGE_22_26 to listOf(
            ScoreEntry(t(13, 25), 100), ScoreEntry(t(13, 47), 99), ScoreEntry(t(13, 55), 98),
            ScoreEntry(t(14, 12), 97), ScoreEntry(t(14, 27), 96), ScoreEntry(t(14, 41), 95),
            ScoreEntry(t(14, 54), 94), ScoreEntry(t(15, 5), 93), ScoreEntry(t(15, 17), 92),
            ScoreEntry(t(15, 28), 91), ScoreEntry(t(15, 38), 90), ScoreEntry(t(15, 49), 89),
            ScoreEntry(t(15, 59), 88), ScoreEntry(t(16, 9), 87), ScoreEntry(t(16, 19), 86),
            ScoreEntry(t(16, 29), 85), ScoreEntry(t(16, 39), 84), ScoreEntry(t(16, 49), 83),
            ScoreEntry(t(16, 59), 82), ScoreEntry(t(17, 8), 81), ScoreEntry(t(17, 18), 79),
            ScoreEntry(t(17, 28), 78), ScoreEntry(t(17, 37), 77), ScoreEntry(t(17, 46), 76),
            ScoreEntry(t(17, 55), 75), ScoreEntry(t(18, 3), 74), ScoreEntry(t(18, 12), 73),
            ScoreEntry(t(18, 21), 72), ScoreEntry(t(18, 30), 70), ScoreEntry(t(18, 39), 69),
            ScoreEntry(t(18, 48), 68), ScoreEntry(t(18, 57), 67), ScoreEntry(t(19, 7), 66),
            ScoreEntry(t(19, 16), 65), ScoreEntry(t(19, 26), 64), ScoreEntry(t(19, 36), 63),
            ScoreEntry(t(19, 45), 60),
            ScoreEntry(t(20, 13), 50), ScoreEntry(t(20, 41), 40), ScoreEntry(t(21, 9), 30),
            ScoreEntry(t(21, 37), 20), ScoreEntry(t(22, 5), 10), ScoreEntry(t(22, 33), 0)
        ),
        AgeBracket.AGE_27_31 to listOf(
            ScoreEntry(t(13, 25), 100), ScoreEntry(t(13, 47), 99), ScoreEntry(t(13, 55), 98),
            ScoreEntry(t(14, 12), 97), ScoreEntry(t(14, 27), 96), ScoreEntry(t(14, 41), 95),
            ScoreEntry(t(14, 54), 94), ScoreEntry(t(15, 5), 93), ScoreEntry(t(15, 17), 92),
            ScoreEntry(t(15, 28), 91), ScoreEntry(t(15, 38), 90), ScoreEntry(t(15, 55), 89),
            ScoreEntry(t(16, 5), 88), ScoreEntry(t(16, 14), 87), ScoreEntry(t(16, 24), 86),
            ScoreEntry(t(16, 33), 85), ScoreEntry(t(16, 43), 84), ScoreEntry(t(16, 52), 83),
            ScoreEntry(t(17, 2), 82), ScoreEntry(t(17, 12), 81), ScoreEntry(t(17, 21), 80),
            ScoreEntry(t(17, 30), 79), ScoreEntry(t(17, 38), 78), ScoreEntry(t(17, 47), 77),
            ScoreEntry(t(17, 55), 76), ScoreEntry(t(18, 4), 75), ScoreEntry(t(18, 13), 74),
            ScoreEntry(t(18, 21), 73), ScoreEntry(t(18, 23), 70), ScoreEntry(t(18, 30), 69),
            ScoreEntry(t(18, 39), 68), ScoreEntry(t(18, 57), 67), ScoreEntry(t(19, 6), 66),
            ScoreEntry(t(19, 15), 65), ScoreEntry(t(19, 25), 64), ScoreEntry(t(19, 35), 63),
            ScoreEntry(t(19, 45), 60),
            ScoreEntry(t(20, 13), 50), ScoreEntry(t(20, 41), 40), ScoreEntry(t(21, 9), 30),
            ScoreEntry(t(21, 37), 20), ScoreEntry(t(22, 5), 10), ScoreEntry(t(22, 33), 0)
        ),
        AgeBracket.AGE_32_36 to listOf(
            ScoreEntry(t(13, 42), 100), ScoreEntry(t(14, 6), 99), ScoreEntry(t(14, 23), 98),
            ScoreEntry(t(14, 37), 97), ScoreEntry(t(14, 49), 96), ScoreEntry(t(15, 1), 95),
            ScoreEntry(t(15, 12), 94), ScoreEntry(t(15, 23), 93), ScoreEntry(t(15, 33), 92),
            ScoreEntry(t(15, 43), 91), ScoreEntry(t(15, 50), 90), ScoreEntry(t(15, 53), 89),
            ScoreEntry(t(16, 2), 88), ScoreEntry(t(16, 12), 87), ScoreEntry(t(16, 21), 86),
            ScoreEntry(t(16, 30), 85), ScoreEntry(t(16, 40), 84), ScoreEntry(t(16, 49), 83),
            ScoreEntry(t(16, 58), 82), ScoreEntry(t(17, 7), 81), ScoreEntry(t(17, 16), 80),
            ScoreEntry(t(17, 26), 79), ScoreEntry(t(17, 34), 78), ScoreEntry(t(17, 42), 77),
            ScoreEntry(t(17, 50), 76), ScoreEntry(t(17, 58), 75), ScoreEntry(t(18, 7), 74),
            ScoreEntry(t(18, 15), 73), ScoreEntry(t(18, 23), 72), ScoreEntry(t(18, 30), 70),
            ScoreEntry(t(18, 58), 69), ScoreEntry(t(19, 6), 68), ScoreEntry(t(19, 16), 67),
            ScoreEntry(t(19, 25), 66), ScoreEntry(t(19, 34), 65), ScoreEntry(t(19, 44), 64),
            ScoreEntry(t(19, 55), 63), ScoreEntry(t(20, 6), 62), ScoreEntry(t(20, 18), 61),
            ScoreEntry(t(20, 44), 60),
            ScoreEntry(t(21, 12), 50), ScoreEntry(t(21, 40), 40), ScoreEntry(t(22, 8), 30),
            ScoreEntry(t(22, 36), 20), ScoreEntry(t(23, 4), 10), ScoreEntry(t(23, 32), 0)
        ),
        AgeBracket.AGE_37_41 to listOf(
            ScoreEntry(t(13, 42), 100), ScoreEntry(t(14, 16), 99), ScoreEntry(t(14, 32), 98),
            ScoreEntry(t(14, 46), 97), ScoreEntry(t(14, 59), 96), ScoreEntry(t(15, 10), 95),
            ScoreEntry(t(15, 21), 94), ScoreEntry(t(15, 32), 93), ScoreEntry(t(15, 42), 92),
            ScoreEntry(t(15, 52), 91), ScoreEntry(t(16, 1), 90), ScoreEntry(t(16, 11), 89),
            ScoreEntry(t(16, 20), 88), ScoreEntry(t(16, 29), 87), ScoreEntry(t(16, 39), 86),
            ScoreEntry(t(16, 48), 85), ScoreEntry(t(16, 57), 84), ScoreEntry(t(17, 6), 83),
            ScoreEntry(t(17, 15), 82), ScoreEntry(t(17, 24), 81), ScoreEntry(t(17, 33), 80),
            ScoreEntry(t(17, 41), 79), ScoreEntry(t(17, 50), 78), ScoreEntry(t(17, 58), 77),
            ScoreEntry(t(18, 6), 76), ScoreEntry(t(18, 14), 75), ScoreEntry(t(18, 22), 74),
            ScoreEntry(t(18, 31), 73), ScoreEntry(t(18, 35), 70), ScoreEntry(t(18, 47), 69),
            ScoreEntry(t(19, 4), 68), ScoreEntry(t(19, 13), 67), ScoreEntry(t(19, 22), 66),
            ScoreEntry(t(19, 31), 65), ScoreEntry(t(19, 41), 64), ScoreEntry(t(19, 51), 63),
            ScoreEntry(t(20, 12), 62), ScoreEntry(t(20, 24), 61), ScoreEntry(t(20, 44), 60),
            ScoreEntry(t(21, 12), 50), ScoreEntry(t(21, 40), 40), ScoreEntry(t(22, 8), 30),
            ScoreEntry(t(22, 36), 20), ScoreEntry(t(23, 4), 10), ScoreEntry(t(23, 32), 0)
        ),
        AgeBracket.AGE_42_46 to listOf(
            ScoreEntry(t(14, 5), 100), ScoreEntry(t(14, 29), 99), ScoreEntry(t(14, 45), 98),
            ScoreEntry(t(14, 59), 97), ScoreEntry(t(15, 12), 96), ScoreEntry(t(15, 24), 95),
            ScoreEntry(t(15, 35), 94), ScoreEntry(t(15, 45), 93), ScoreEntry(t(15, 55), 92),
            ScoreEntry(t(16, 5), 91), ScoreEntry(t(16, 15), 90), ScoreEntry(t(16, 24), 89),
            ScoreEntry(t(16, 33), 88), ScoreEntry(t(16, 43), 87), ScoreEntry(t(16, 52), 86),
            ScoreEntry(t(17, 1), 85), ScoreEntry(t(17, 10), 84), ScoreEntry(t(17, 19), 83),
            ScoreEntry(t(17, 28), 82), ScoreEntry(t(17, 37), 81), ScoreEntry(t(17, 47), 80),
            ScoreEntry(t(17, 56), 79), ScoreEntry(t(18, 6), 78), ScoreEntry(t(18, 15), 77),
            ScoreEntry(t(18, 25), 76), ScoreEntry(t(18, 35), 75), ScoreEntry(t(18, 45), 74),
            ScoreEntry(t(18, 54), 73), ScoreEntry(t(18, 55), 70), ScoreEntry(t(19, 15), 69),
            ScoreEntry(t(19, 36), 68), ScoreEntry(t(19, 47), 67), ScoreEntry(t(19, 58), 66),
            ScoreEntry(t(20, 10), 65), ScoreEntry(t(20, 37), 64), ScoreEntry(t(20, 52), 63),
            ScoreEntry(t(21, 9), 62), ScoreEntry(t(21, 31), 61), ScoreEntry(t(22, 4), 60),
            ScoreEntry(t(22, 32), 50), ScoreEntry(t(23, 0), 40), ScoreEntry(t(23, 28), 30),
            ScoreEntry(t(23, 56), 20), ScoreEntry(t(24, 24), 10), ScoreEntry(t(24, 52), 0)
        ),
        AgeBracket.AGE_47_51 to listOf(
            ScoreEntry(t(14, 30), 100), ScoreEntry(t(14, 52), 99), ScoreEntry(t(15, 8), 98),
            ScoreEntry(t(15, 22), 97), ScoreEntry(t(15, 35), 96), ScoreEntry(t(15, 47), 95),
            ScoreEntry(t(15, 58), 94), ScoreEntry(t(16, 9), 93), ScoreEntry(t(16, 19), 92),
            ScoreEntry(t(16, 29), 91), ScoreEntry(t(16, 39), 90), ScoreEntry(t(16, 48), 89),
            ScoreEntry(t(16, 58), 88), ScoreEntry(t(17, 7), 87), ScoreEntry(t(17, 16), 86),
            ScoreEntry(t(17, 25), 85), ScoreEntry(t(17, 35), 84), ScoreEntry(t(17, 44), 83),
            ScoreEntry(t(17, 53), 82), ScoreEntry(t(18, 2), 81), ScoreEntry(t(18, 12), 80),
            ScoreEntry(t(18, 21), 79), ScoreEntry(t(18, 31), 78), ScoreEntry(t(18, 41), 77),
            ScoreEntry(t(18, 51), 76), ScoreEntry(t(19, 0), 75), ScoreEntry(t(19, 10), 74),
            ScoreEntry(t(19, 20), 73), ScoreEntry(t(19, 30), 70), ScoreEntry(t(19, 41), 69),
            ScoreEntry(t(20, 2), 68), ScoreEntry(t(20, 13), 67), ScoreEntry(t(20, 37), 66),
            ScoreEntry(t(20, 50), 65), ScoreEntry(t(21, 4), 64), ScoreEntry(t(21, 19), 63),
            ScoreEntry(t(21, 37), 62), ScoreEntry(t(21, 59), 61), ScoreEntry(t(22, 4), 60),
            ScoreEntry(t(22, 32), 50), ScoreEntry(t(23, 0), 40), ScoreEntry(t(23, 28), 30),
            ScoreEntry(t(23, 56), 20), ScoreEntry(t(24, 24), 10), ScoreEntry(t(24, 52), 0)
        ),
        AgeBracket.AGE_52_56 to listOf(
            ScoreEntry(t(15, 9), 100), ScoreEntry(t(15, 38), 99), ScoreEntry(t(15, 54), 98),
            ScoreEntry(t(16, 8), 97), ScoreEntry(t(16, 21), 96), ScoreEntry(t(16, 33), 95),
            ScoreEntry(t(16, 44), 94), ScoreEntry(t(16, 55), 93), ScoreEntry(t(17, 6), 92),
            ScoreEntry(t(17, 16), 91), ScoreEntry(t(17, 26), 90), ScoreEntry(t(17, 35), 89),
            ScoreEntry(t(17, 45), 88), ScoreEntry(t(17, 54), 87), ScoreEntry(t(18, 4), 86),
            ScoreEntry(t(18, 13), 85), ScoreEntry(t(18, 22), 84), ScoreEntry(t(18, 32), 83),
            ScoreEntry(t(18, 41), 82), ScoreEntry(t(18, 51), 81), ScoreEntry(t(19, 0), 80),
            ScoreEntry(t(19, 10), 79), ScoreEntry(t(19, 20), 78), ScoreEntry(t(19, 30), 77),
            ScoreEntry(t(19, 39), 76), ScoreEntry(t(19, 49), 75), ScoreEntry(t(19, 59), 74),
            ScoreEntry(t(20, 10), 73), ScoreEntry(t(20, 20), 70), ScoreEntry(t(20, 52), 69),
            ScoreEntry(t(21, 3), 68), ScoreEntry(t(21, 15), 67), ScoreEntry(t(21, 27), 66),
            ScoreEntry(t(21, 40), 65), ScoreEntry(t(21, 54), 64), ScoreEntry(t(22, 10), 63),
            ScoreEntry(t(22, 28), 62), ScoreEntry(t(22, 50), 60),
            ScoreEntry(t(23, 18), 50), ScoreEntry(t(23, 46), 40), ScoreEntry(t(24, 14), 30),
            ScoreEntry(t(24, 42), 20), ScoreEntry(t(25, 10), 10), ScoreEntry(t(25, 38), 0)
        ),
        AgeBracket.AGE_57_61 to listOf(
            ScoreEntry(t(15, 28), 100), ScoreEntry(t(15, 55), 99), ScoreEntry(t(16, 22), 98),
            ScoreEntry(t(16, 44), 97), ScoreEntry(t(16, 58), 96), ScoreEntry(t(17, 14), 95),
            ScoreEntry(t(17, 27), 94), ScoreEntry(t(17, 45), 93), ScoreEntry(t(17, 57), 92),
            ScoreEntry(t(18, 7), 91), ScoreEntry(t(18, 17), 90), ScoreEntry(t(18, 25), 89),
            ScoreEntry(t(18, 36), 88), ScoreEntry(t(18, 45), 87), ScoreEntry(t(18, 53), 86),
            ScoreEntry(t(19, 0), 85), ScoreEntry(t(19, 7), 84), ScoreEntry(t(19, 17), 83),
            ScoreEntry(t(19, 27), 82), ScoreEntry(t(19, 36), 81), ScoreEntry(t(19, 45), 80),
            ScoreEntry(t(19, 51), 79), ScoreEntry(t(19, 59), 78), ScoreEntry(t(20, 7), 77),
            ScoreEntry(t(20, 14), 76), ScoreEntry(t(20, 22), 75), ScoreEntry(t(20, 31), 74),
            ScoreEntry(t(20, 41), 73), ScoreEntry(t(20, 46), 72), ScoreEntry(t(20, 54), 71),
            ScoreEntry(t(21, 0), 70), ScoreEntry(t(21, 1), 69), ScoreEntry(t(21, 19), 68),
            ScoreEntry(t(21, 35), 67), ScoreEntry(t(21, 47), 66), ScoreEntry(t(22, 3), 65),
            ScoreEntry(t(22, 21), 64), ScoreEntry(t(22, 39), 63), ScoreEntry(t(22, 58), 62),
            ScoreEntry(t(23, 12), 61), ScoreEntry(t(23, 36), 60),
            ScoreEntry(t(24, 4), 50), ScoreEntry(t(24, 32), 40), ScoreEntry(t(25, 0), 30),
            ScoreEntry(t(25, 28), 20), ScoreEntry(t(25, 56), 10), ScoreEntry(t(26, 24), 0)
        ),
        AgeBracket.AGE_62_PLUS to listOf(
            ScoreEntry(t(15, 28), 100), ScoreEntry(t(15, 55), 99), ScoreEntry(t(16, 22), 98),
            ScoreEntry(t(16, 44), 97), ScoreEntry(t(16, 58), 96), ScoreEntry(t(17, 14), 95),
            ScoreEntry(t(17, 27), 94), ScoreEntry(t(17, 45), 93), ScoreEntry(t(17, 57), 92),
            ScoreEntry(t(18, 7), 91), ScoreEntry(t(18, 17), 90), ScoreEntry(t(18, 25), 89),
            ScoreEntry(t(18, 36), 88), ScoreEntry(t(18, 45), 87), ScoreEntry(t(18, 53), 86),
            ScoreEntry(t(19, 0), 85), ScoreEntry(t(19, 7), 84), ScoreEntry(t(19, 17), 83),
            ScoreEntry(t(19, 27), 82), ScoreEntry(t(19, 36), 81), ScoreEntry(t(19, 45), 80),
            ScoreEntry(t(19, 51), 79), ScoreEntry(t(19, 59), 78), ScoreEntry(t(20, 7), 77),
            ScoreEntry(t(20, 14), 76), ScoreEntry(t(20, 22), 75), ScoreEntry(t(20, 31), 74),
            ScoreEntry(t(20, 41), 73), ScoreEntry(t(20, 46), 72), ScoreEntry(t(20, 54), 71),
            ScoreEntry(t(21, 0), 70), ScoreEntry(t(21, 40), 60),
            ScoreEntry(t(22, 8), 50), ScoreEntry(t(22, 36), 40), ScoreEntry(t(23, 4), 30),
            ScoreEntry(t(23, 32), 20), ScoreEntry(t(24, 0), 10), ScoreEntry(t(24, 28), 0)
        )
    )

    val female: Map<AgeBracket, List<ScoreEntry>> = mapOf(
        AgeBracket.AGE_17_21 to listOf(
            ScoreEntry(t(16, 0), 100), ScoreEntry(t(16, 28), 99), ScoreEntry(t(16, 49), 98),
            ScoreEntry(t(17, 7), 97), ScoreEntry(t(17, 14), 96), ScoreEntry(t(17, 23), 95),
            ScoreEntry(t(17, 31), 94), ScoreEntry(t(17, 37), 93), ScoreEntry(t(17, 44), 92),
            ScoreEntry(t(17, 50), 91), ScoreEntry(t(17, 55), 90), ScoreEntry(t(18, 7), 89),
            ScoreEntry(t(18, 13), 88), ScoreEntry(t(18, 24), 87), ScoreEntry(t(18, 34), 86),
            ScoreEntry(t(18, 44), 85), ScoreEntry(t(18, 54), 84), ScoreEntry(t(19, 3), 83),
            ScoreEntry(t(19, 12), 82), ScoreEntry(t(19, 21), 81), ScoreEntry(t(19, 30), 80),
            ScoreEntry(t(19, 39), 79), ScoreEntry(t(19, 47), 78), ScoreEntry(t(19, 56), 77),
            ScoreEntry(t(20, 5), 76), ScoreEntry(t(20, 13), 75), ScoreEntry(t(20, 24), 74),
            ScoreEntry(t(20, 35), 73), ScoreEntry(t(20, 45), 72), ScoreEntry(t(20, 56), 71),
            ScoreEntry(t(21, 6), 70), ScoreEntry(t(21, 17), 69), ScoreEntry(t(21, 28), 68),
            ScoreEntry(t(21, 49), 67), ScoreEntry(t(22, 1), 66), ScoreEntry(t(22, 12), 65),
            ScoreEntry(t(22, 25), 64), ScoreEntry(t(22, 38), 63), ScoreEntry(t(22, 53), 62),
            ScoreEntry(t(22, 55), 60),
            ScoreEntry(t(23, 24), 50), ScoreEntry(t(23, 53), 40), ScoreEntry(t(24, 22), 30),
            ScoreEntry(t(24, 51), 20), ScoreEntry(t(25, 20), 10), ScoreEntry(t(25, 50), 0)
        ),
        AgeBracket.AGE_22_26 to listOf(
            ScoreEntry(t(15, 30), 100), ScoreEntry(t(15, 44), 99), ScoreEntry(t(15, 55), 98),
            ScoreEntry(t(16, 0), 97), ScoreEntry(t(16, 4), 96), ScoreEntry(t(16, 27), 95),
            ScoreEntry(t(16, 46), 94), ScoreEntry(t(17, 3), 93), ScoreEntry(t(17, 17), 92),
            ScoreEntry(t(17, 31), 91), ScoreEntry(t(17, 44), 90), ScoreEntry(t(17, 55), 89),
            ScoreEntry(t(18, 7), 88), ScoreEntry(t(18, 18), 87), ScoreEntry(t(18, 28), 86),
            ScoreEntry(t(18, 38), 85), ScoreEntry(t(18, 48), 84), ScoreEntry(t(18, 58), 83),
            ScoreEntry(t(19, 7), 82), ScoreEntry(t(19, 16), 81), ScoreEntry(t(19, 25), 80),
            ScoreEntry(t(19, 34), 79), ScoreEntry(t(19, 43), 78), ScoreEntry(t(19, 52), 77),
            ScoreEntry(t(20, 1), 76), ScoreEntry(t(20, 12), 75), ScoreEntry(t(20, 24), 74),
            ScoreEntry(t(20, 35), 73), ScoreEntry(t(20, 46), 72), ScoreEntry(t(20, 57), 71),
            ScoreEntry(t(21, 0), 70), ScoreEntry(t(21, 32), 69), ScoreEntry(t(21, 40), 68),
            ScoreEntry(t(21, 49), 67), ScoreEntry(t(21, 58), 66), ScoreEntry(t(22, 7), 65),
            ScoreEntry(t(22, 16), 64), ScoreEntry(t(22, 26), 63), ScoreEntry(t(22, 37), 62),
            ScoreEntry(t(22, 45), 60),
            ScoreEntry(t(23, 14), 50), ScoreEntry(t(23, 43), 40), ScoreEntry(t(24, 12), 30),
            ScoreEntry(t(24, 41), 20), ScoreEntry(t(25, 10), 10), ScoreEntry(t(25, 40), 0)
        ),
        // Using similar pattern for remaining age brackets
        AgeBracket.AGE_27_31 to listOf(
            ScoreEntry(t(15, 30), 100), ScoreEntry(t(16, 0), 98), ScoreEntry(t(16, 30), 96),
            ScoreEntry(t(17, 0), 94), ScoreEntry(t(17, 30), 92), ScoreEntry(t(18, 0), 88),
            ScoreEntry(t(18, 30), 84), ScoreEntry(t(19, 0), 80), ScoreEntry(t(19, 30), 76),
            ScoreEntry(t(20, 0), 72), ScoreEntry(t(20, 30), 68), ScoreEntry(t(21, 0), 64),
            ScoreEntry(t(21, 30), 62), ScoreEntry(t(22, 45), 60),
            ScoreEntry(t(23, 14), 50), ScoreEntry(t(23, 43), 40), ScoreEntry(t(24, 12), 30),
            ScoreEntry(t(24, 41), 20), ScoreEntry(t(25, 10), 10), ScoreEntry(t(25, 40), 0)
        ),
        AgeBracket.AGE_32_36 to listOf(
            ScoreEntry(t(15, 48), 100), ScoreEntry(t(16, 15), 98), ScoreEntry(t(16, 45), 96),
            ScoreEntry(t(17, 15), 94), ScoreEntry(t(17, 45), 92), ScoreEntry(t(18, 15), 88),
            ScoreEntry(t(18, 45), 84), ScoreEntry(t(19, 15), 80), ScoreEntry(t(19, 45), 76),
            ScoreEntry(t(20, 15), 72), ScoreEntry(t(20, 45), 68), ScoreEntry(t(21, 15), 64),
            ScoreEntry(t(21, 45), 62), ScoreEntry(t(22, 50), 60),
            ScoreEntry(t(23, 19), 50), ScoreEntry(t(23, 48), 40), ScoreEntry(t(24, 17), 30),
            ScoreEntry(t(24, 46), 20), ScoreEntry(t(25, 15), 10), ScoreEntry(t(25, 45), 0)
        ),
        AgeBracket.AGE_37_41 to listOf(
            ScoreEntry(t(15, 51), 100), ScoreEntry(t(16, 21), 98), ScoreEntry(t(16, 51), 96),
            ScoreEntry(t(17, 21), 94), ScoreEntry(t(17, 51), 92), ScoreEntry(t(18, 21), 88),
            ScoreEntry(t(18, 51), 84), ScoreEntry(t(19, 21), 80), ScoreEntry(t(19, 51), 76),
            ScoreEntry(t(20, 21), 72), ScoreEntry(t(20, 51), 68), ScoreEntry(t(21, 21), 64),
            ScoreEntry(t(21, 51), 62), ScoreEntry(t(22, 59), 60),
            ScoreEntry(t(23, 28), 50), ScoreEntry(t(23, 57), 40), ScoreEntry(t(24, 26), 30),
            ScoreEntry(t(24, 55), 20), ScoreEntry(t(25, 24), 10), ScoreEntry(t(25, 54), 0)
        ),
        AgeBracket.AGE_42_46 to listOf(
            ScoreEntry(t(16, 0), 100), ScoreEntry(t(16, 31), 98), ScoreEntry(t(17, 2), 96),
            ScoreEntry(t(17, 33), 94), ScoreEntry(t(18, 4), 92), ScoreEntry(t(18, 35), 88),
            ScoreEntry(t(19, 6), 84), ScoreEntry(t(19, 37), 80), ScoreEntry(t(20, 8), 76),
            ScoreEntry(t(20, 39), 72), ScoreEntry(t(21, 10), 68), ScoreEntry(t(21, 41), 64),
            ScoreEntry(t(22, 12), 62), ScoreEntry(t(23, 15), 60),
            ScoreEntry(t(23, 44), 50), ScoreEntry(t(24, 13), 40), ScoreEntry(t(24, 42), 30),
            ScoreEntry(t(25, 11), 20), ScoreEntry(t(25, 40), 10), ScoreEntry(t(26, 10), 0)
        ),
        AgeBracket.AGE_47_51 to listOf(
            ScoreEntry(t(16, 30), 100), ScoreEntry(t(17, 0), 98), ScoreEntry(t(17, 30), 96),
            ScoreEntry(t(18, 0), 94), ScoreEntry(t(18, 30), 92), ScoreEntry(t(19, 0), 88),
            ScoreEntry(t(19, 30), 84), ScoreEntry(t(20, 0), 80), ScoreEntry(t(20, 30), 76),
            ScoreEntry(t(21, 0), 72), ScoreEntry(t(21, 30), 68), ScoreEntry(t(22, 0), 64),
            ScoreEntry(t(22, 30), 62), ScoreEntry(t(23, 30), 60),
            ScoreEntry(t(24, 0), 50), ScoreEntry(t(24, 30), 40), ScoreEntry(t(25, 0), 30),
            ScoreEntry(t(25, 30), 20), ScoreEntry(t(26, 0), 10), ScoreEntry(t(26, 25), 0)
        ),
        AgeBracket.AGE_52_56 to listOf(
            ScoreEntry(t(16, 59), 100), ScoreEntry(t(17, 44), 98), ScoreEntry(t(18, 5), 96),
            ScoreEntry(t(18, 22), 94), ScoreEntry(t(18, 50), 92), ScoreEntry(t(19, 15), 88),
            ScoreEntry(t(19, 47), 84), ScoreEntry(t(20, 16), 80), ScoreEntry(t(20, 44), 76),
            ScoreEntry(t(21, 10), 72), ScoreEntry(t(21, 35), 68), ScoreEntry(t(22, 7), 64),
            ScoreEntry(t(22, 38), 62), ScoreEntry(t(24, 0), 60),
            ScoreEntry(t(24, 29), 50), ScoreEntry(t(24, 58), 40), ScoreEntry(t(25, 27), 30),
            ScoreEntry(t(25, 56), 20), ScoreEntry(t(26, 25), 10), ScoreEntry(t(26, 55), 0)
        ),
        AgeBracket.AGE_57_61 to listOf(
            ScoreEntry(t(17, 18), 100), ScoreEntry(t(17, 56), 98), ScoreEntry(t(18, 25), 96),
            ScoreEntry(t(18, 46), 94), ScoreEntry(t(19, 4), 92), ScoreEntry(t(19, 45), 88),
            ScoreEntry(t(20, 17), 84), ScoreEntry(t(20, 44), 80), ScoreEntry(t(21, 15), 76),
            ScoreEntry(t(21, 40), 72), ScoreEntry(t(22, 9), 68), ScoreEntry(t(22, 43), 64),
            ScoreEntry(t(23, 22), 62), ScoreEntry(t(24, 48), 60),
            ScoreEntry(t(25, 17), 50), ScoreEntry(t(25, 46), 40), ScoreEntry(t(26, 15), 30),
            ScoreEntry(t(26, 44), 20), ScoreEntry(t(27, 13), 10), ScoreEntry(t(27, 43), 0)
        ),
        AgeBracket.AGE_62_PLUS to listOf(
            ScoreEntry(t(17, 18), 100), ScoreEntry(t(17, 56), 98), ScoreEntry(t(18, 25), 96),
            ScoreEntry(t(18, 46), 94), ScoreEntry(t(19, 4), 92), ScoreEntry(t(19, 45), 88),
            ScoreEntry(t(20, 17), 84), ScoreEntry(t(20, 44), 80), ScoreEntry(t(21, 15), 76),
            ScoreEntry(t(21, 40), 72), ScoreEntry(t(22, 9), 68), ScoreEntry(t(22, 43), 64),
            ScoreEntry(t(23, 22), 62), ScoreEntry(t(25, 0), 60),
            ScoreEntry(t(25, 29), 50), ScoreEntry(t(25, 58), 40), ScoreEntry(t(26, 27), 30),
            ScoreEntry(t(26, 56), 20), ScoreEntry(t(27, 25), 10), ScoreEntry(t(27, 55), 0)
        )
    )

    fun getTable(category: OfficialScoreTables.ScoringCategory, ageBracket: AgeBracket): List<ScoreEntry> {
        return when (category) {
            OfficialScoreTables.ScoringCategory.MALE_COMBAT -> maleCombat[ageBracket] ?: maleCombat[AgeBracket.AGE_17_21]!!
            OfficialScoreTables.ScoringCategory.FEMALE -> female[ageBracket] ?: female[AgeBracket.AGE_17_21]!!
        }
    }
}
