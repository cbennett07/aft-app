package com.aftcalculator.scoring

import com.aftcalculator.models.AgeBracket

/**
 * Hand-Release Push-Up (HRP) Scoring Tables
 * Number of correctly performed repetitions in 2 minutes
 * Higher is better
 */
object PushUpTables {

    val maleCombat: Map<AgeBracket, List<ScoreEntry>> = mapOf(
        AgeBracket.AGE_17_21 to listOf(
            ScoreEntry(58.0, 100), ScoreEntry(57.0, 99), ScoreEntry(55.0, 98),
            ScoreEntry(54.0, 97), ScoreEntry(53.0, 96), ScoreEntry(52.0, 95),
            ScoreEntry(51.0, 94), ScoreEntry(49.0, 93), ScoreEntry(48.0, 92),
            ScoreEntry(47.0, 91), ScoreEntry(46.0, 90), ScoreEntry(45.0, 89),
            ScoreEntry(44.0, 88), ScoreEntry(43.0, 87), ScoreEntry(42.0, 86),
            ScoreEntry(41.0, 85), ScoreEntry(40.0, 84), ScoreEntry(39.0, 82),
            ScoreEntry(38.0, 81), ScoreEntry(37.0, 80), ScoreEntry(36.0, 79),
            ScoreEntry(35.0, 78), ScoreEntry(34.0, 77), ScoreEntry(33.0, 76),
            ScoreEntry(32.0, 75), ScoreEntry(31.0, 74), ScoreEntry(30.0, 73),
            ScoreEntry(29.0, 72), ScoreEntry(28.0, 70), ScoreEntry(26.0, 69),
            ScoreEntry(25.0, 68), ScoreEntry(24.0, 67), ScoreEntry(23.0, 66),
            ScoreEntry(22.0, 65), ScoreEntry(21.0, 64), ScoreEntry(19.0, 63),
            ScoreEntry(18.0, 62), ScoreEntry(17.0, 61), ScoreEntry(15.0, 60),
            ScoreEntry(9.0, 50), ScoreEntry(8.0, 40), ScoreEntry(7.0, 30),
            ScoreEntry(6.0, 20), ScoreEntry(5.0, 10), ScoreEntry(4.0, 0)
        ),
        AgeBracket.AGE_22_26 to listOf(
            ScoreEntry(61.0, 100), ScoreEntry(59.0, 99), ScoreEntry(57.0, 98),
            ScoreEntry(56.0, 97), ScoreEntry(55.0, 96), ScoreEntry(53.0, 95),
            ScoreEntry(52.0, 94), ScoreEntry(51.0, 93), ScoreEntry(50.0, 92),
            ScoreEntry(49.0, 91), ScoreEntry(48.0, 90), ScoreEntry(46.0, 89),
            ScoreEntry(45.0, 88), ScoreEntry(44.0, 87), ScoreEntry(43.0, 86),
            ScoreEntry(42.0, 85), ScoreEntry(41.0, 84), ScoreEntry(40.0, 83),
            ScoreEntry(39.0, 82), ScoreEntry(38.0, 81), ScoreEntry(37.0, 80),
            ScoreEntry(36.0, 79), ScoreEntry(35.0, 78), ScoreEntry(34.0, 77),
            ScoreEntry(32.0, 76), ScoreEntry(31.0, 75), ScoreEntry(30.0, 74),
            ScoreEntry(29.0, 73), ScoreEntry(28.0, 72), ScoreEntry(27.0, 71),
            ScoreEntry(26.0, 70), ScoreEntry(25.0, 69), ScoreEntry(24.0, 68),
            ScoreEntry(23.0, 67), ScoreEntry(22.0, 66), ScoreEntry(21.0, 65),
            ScoreEntry(19.0, 64), ScoreEntry(18.0, 63), ScoreEntry(17.0, 62),
            ScoreEntry(15.0, 61), ScoreEntry(14.0, 60),
            ScoreEntry(9.0, 50), ScoreEntry(8.0, 40), ScoreEntry(7.0, 30),
            ScoreEntry(6.0, 20), ScoreEntry(5.0, 10), ScoreEntry(4.0, 0)
        ),
        AgeBracket.AGE_27_31 to listOf(
            ScoreEntry(62.0, 100), ScoreEntry(60.0, 99), ScoreEntry(58.0, 98),
            ScoreEntry(57.0, 97), ScoreEntry(55.0, 96), ScoreEntry(54.0, 95),
            ScoreEntry(53.0, 94), ScoreEntry(52.0, 93), ScoreEntry(51.0, 92),
            ScoreEntry(49.0, 91), ScoreEntry(48.0, 90), ScoreEntry(47.0, 89),
            ScoreEntry(46.0, 88), ScoreEntry(45.0, 87), ScoreEntry(44.0, 86),
            ScoreEntry(43.0, 85), ScoreEntry(42.0, 84), ScoreEntry(41.0, 83),
            ScoreEntry(39.0, 82), ScoreEntry(38.0, 81), ScoreEntry(37.0, 80),
            ScoreEntry(36.0, 79), ScoreEntry(35.0, 78), ScoreEntry(34.0, 77),
            ScoreEntry(33.0, 76), ScoreEntry(32.0, 75), ScoreEntry(31.0, 74),
            ScoreEntry(30.0, 73), ScoreEntry(29.0, 72), ScoreEntry(28.0, 71),
            ScoreEntry(26.0, 70), ScoreEntry(25.0, 69), ScoreEntry(24.0, 68),
            ScoreEntry(23.0, 67), ScoreEntry(22.0, 66), ScoreEntry(21.0, 65),
            ScoreEntry(20.0, 64), ScoreEntry(18.0, 63), ScoreEntry(17.0, 62),
            ScoreEntry(15.0, 61), ScoreEntry(14.0, 60),
            ScoreEntry(9.0, 50), ScoreEntry(8.0, 40), ScoreEntry(7.0, 30),
            ScoreEntry(6.0, 20), ScoreEntry(5.0, 10), ScoreEntry(4.0, 0)
        ),
        AgeBracket.AGE_32_36 to listOf(
            ScoreEntry(60.0, 100), ScoreEntry(58.0, 99), ScoreEntry(57.0, 98),
            ScoreEntry(55.0, 97), ScoreEntry(54.0, 96), ScoreEntry(53.0, 95),
            ScoreEntry(52.0, 94), ScoreEntry(51.0, 93), ScoreEntry(49.0, 92),
            ScoreEntry(48.0, 91), ScoreEntry(47.0, 90), ScoreEntry(46.0, 89),
            ScoreEntry(45.0, 88), ScoreEntry(44.0, 87), ScoreEntry(43.0, 86),
            ScoreEntry(42.0, 85), ScoreEntry(41.0, 84), ScoreEntry(40.0, 83),
            ScoreEntry(39.0, 82), ScoreEntry(37.0, 81), ScoreEntry(36.0, 80),
            ScoreEntry(35.0, 79), ScoreEntry(34.0, 78), ScoreEntry(33.0, 77),
            ScoreEntry(32.0, 76), ScoreEntry(31.0, 75), ScoreEntry(30.0, 74),
            ScoreEntry(29.0, 73), ScoreEntry(28.0, 72), ScoreEntry(27.0, 71),
            ScoreEntry(26.0, 70), ScoreEntry(25.0, 69), ScoreEntry(24.0, 68),
            ScoreEntry(22.0, 67), ScoreEntry(21.0, 66), ScoreEntry(20.0, 65),
            ScoreEntry(19.0, 64), ScoreEntry(18.0, 63), ScoreEntry(16.0, 62),
            ScoreEntry(15.0, 61), ScoreEntry(13.0, 60),
            ScoreEntry(9.0, 50), ScoreEntry(8.0, 40), ScoreEntry(7.0, 30),
            ScoreEntry(6.0, 20), ScoreEntry(5.0, 10), ScoreEntry(4.0, 0)
        ),
        AgeBracket.AGE_37_41 to listOf(
            ScoreEntry(59.0, 100), ScoreEntry(57.0, 99), ScoreEntry(55.0, 98),
            ScoreEntry(54.0, 97), ScoreEntry(53.0, 96), ScoreEntry(51.0, 95),
            ScoreEntry(50.0, 94), ScoreEntry(49.0, 93), ScoreEntry(48.0, 92),
            ScoreEntry(47.0, 91), ScoreEntry(46.0, 90), ScoreEntry(45.0, 89),
            ScoreEntry(44.0, 88), ScoreEntry(42.0, 87), ScoreEntry(41.0, 86),
            ScoreEntry(40.0, 85), ScoreEntry(39.0, 84), ScoreEntry(38.0, 83),
            ScoreEntry(37.0, 82), ScoreEntry(36.0, 81), ScoreEntry(35.0, 80),
            ScoreEntry(34.0, 79), ScoreEntry(33.0, 78), ScoreEntry(32.0, 77),
            ScoreEntry(31.0, 76), ScoreEntry(30.0, 75), ScoreEntry(29.0, 74),
            ScoreEntry(28.0, 73), ScoreEntry(27.0, 72), ScoreEntry(25.0, 71),
            ScoreEntry(24.0, 70), ScoreEntry(23.0, 69), ScoreEntry(22.0, 68),
            ScoreEntry(21.0, 67), ScoreEntry(20.0, 66), ScoreEntry(19.0, 65),
            ScoreEntry(18.0, 64), ScoreEntry(17.0, 63), ScoreEntry(15.0, 62),
            ScoreEntry(14.0, 61), ScoreEntry(12.0, 60),
            ScoreEntry(9.0, 50), ScoreEntry(8.0, 40), ScoreEntry(7.0, 30),
            ScoreEntry(6.0, 20), ScoreEntry(5.0, 10), ScoreEntry(4.0, 0)
        ),
        AgeBracket.AGE_42_46 to listOf(
            ScoreEntry(57.0, 100), ScoreEntry(55.0, 99), ScoreEntry(53.0, 98),
            ScoreEntry(52.0, 97), ScoreEntry(51.0, 96), ScoreEntry(49.0, 95),
            ScoreEntry(48.0, 94), ScoreEntry(47.0, 93), ScoreEntry(46.0, 92),
            ScoreEntry(45.0, 91), ScoreEntry(44.0, 90), ScoreEntry(43.0, 89),
            ScoreEntry(42.0, 88), ScoreEntry(41.0, 87), ScoreEntry(40.0, 86),
            ScoreEntry(39.0, 85), ScoreEntry(38.0, 84), ScoreEntry(37.0, 83),
            ScoreEntry(36.0, 82), ScoreEntry(35.0, 81), ScoreEntry(34.0, 80),
            ScoreEntry(33.0, 79), ScoreEntry(32.0, 78), ScoreEntry(31.0, 77),
            ScoreEntry(30.0, 76), ScoreEntry(29.0, 75), ScoreEntry(28.0, 74),
            ScoreEntry(26.0, 73), ScoreEntry(25.0, 72), ScoreEntry(24.0, 71),
            ScoreEntry(23.0, 70), ScoreEntry(22.0, 69), ScoreEntry(21.0, 68),
            ScoreEntry(20.0, 67), ScoreEntry(19.0, 66), ScoreEntry(18.0, 65),
            ScoreEntry(17.0, 64), ScoreEntry(16.0, 63), ScoreEntry(15.0, 62),
            ScoreEntry(13.0, 61), ScoreEntry(11.0, 60),
            ScoreEntry(9.0, 50), ScoreEntry(8.0, 40), ScoreEntry(7.0, 30),
            ScoreEntry(6.0, 20), ScoreEntry(5.0, 10), ScoreEntry(4.0, 0)
        ),
        AgeBracket.AGE_47_51 to listOf(
            ScoreEntry(55.0, 100), ScoreEntry(53.0, 99), ScoreEntry(51.0, 98),
            ScoreEntry(50.0, 97), ScoreEntry(49.0, 96), ScoreEntry(48.0, 95),
            ScoreEntry(46.0, 94), ScoreEntry(45.0, 93), ScoreEntry(44.0, 92),
            ScoreEntry(43.0, 91), ScoreEntry(42.0, 90), ScoreEntry(41.0, 89),
            ScoreEntry(40.0, 88), ScoreEntry(39.0, 87), ScoreEntry(38.0, 86),
            ScoreEntry(37.0, 85), ScoreEntry(36.0, 84), ScoreEntry(35.0, 83),
            ScoreEntry(34.0, 82), ScoreEntry(33.0, 81), ScoreEntry(32.0, 80),
            ScoreEntry(31.0, 79), ScoreEntry(30.0, 78), ScoreEntry(29.0, 77),
            ScoreEntry(28.0, 76), ScoreEntry(27.0, 75), ScoreEntry(26.0, 74),
            ScoreEntry(25.0, 73), ScoreEntry(24.0, 72), ScoreEntry(23.0, 71),
            ScoreEntry(22.0, 70), ScoreEntry(21.0, 69), ScoreEntry(20.0, 68),
            ScoreEntry(19.0, 67), ScoreEntry(18.0, 66), ScoreEntry(17.0, 65),
            ScoreEntry(16.0, 64), ScoreEntry(15.0, 63), ScoreEntry(14.0, 62),
            ScoreEntry(12.0, 61), ScoreEntry(11.0, 60),
            ScoreEntry(9.0, 50), ScoreEntry(8.0, 40), ScoreEntry(7.0, 30),
            ScoreEntry(6.0, 20), ScoreEntry(5.0, 10), ScoreEntry(4.0, 0)
        ),
        AgeBracket.AGE_52_56 to listOf(
            ScoreEntry(51.0, 100), ScoreEntry(50.0, 99), ScoreEntry(48.0, 98),
            ScoreEntry(47.0, 97), ScoreEntry(46.0, 96), ScoreEntry(45.0, 95),
            ScoreEntry(44.0, 94), ScoreEntry(43.0, 93), ScoreEntry(42.0, 92),
            ScoreEntry(41.0, 91), ScoreEntry(40.0, 90), ScoreEntry(39.0, 89),
            ScoreEntry(38.0, 88), ScoreEntry(37.0, 87), ScoreEntry(36.0, 86),
            ScoreEntry(35.0, 85), ScoreEntry(34.0, 84), ScoreEntry(33.0, 83),
            ScoreEntry(32.0, 82), ScoreEntry(31.0, 81), ScoreEntry(30.0, 80),
            ScoreEntry(29.0, 79), ScoreEntry(28.0, 78), ScoreEntry(27.0, 77),
            ScoreEntry(26.0, 76), ScoreEntry(25.0, 74), ScoreEntry(24.0, 73),
            ScoreEntry(23.0, 72), ScoreEntry(22.0, 71), ScoreEntry(21.0, 70),
            ScoreEntry(20.0, 69), ScoreEntry(19.0, 68), ScoreEntry(18.0, 67),
            ScoreEntry(17.0, 66), ScoreEntry(16.0, 65), ScoreEntry(15.0, 64),
            ScoreEntry(14.0, 63), ScoreEntry(13.0, 62), ScoreEntry(11.0, 61),
            ScoreEntry(10.0, 60),
            ScoreEntry(9.0, 50), ScoreEntry(8.0, 40), ScoreEntry(7.0, 30),
            ScoreEntry(6.0, 20), ScoreEntry(5.0, 10), ScoreEntry(4.0, 0)
        ),
        AgeBracket.AGE_57_61 to listOf(
            ScoreEntry(46.0, 100), ScoreEntry(43.0, 99), ScoreEntry(40.0, 98),
            ScoreEntry(38.0, 97), ScoreEntry(37.0, 96), ScoreEntry(35.0, 95),
            ScoreEntry(34.0, 94), ScoreEntry(33.0, 93), ScoreEntry(31.0, 92),
            ScoreEntry(30.0, 91), ScoreEntry(29.0, 90), ScoreEntry(26.0, 89),
            ScoreEntry(25.0, 88), ScoreEntry(24.0, 87), ScoreEntry(23.0, 86),
            ScoreEntry(22.0, 84), ScoreEntry(21.0, 83), ScoreEntry(20.0, 82),
            ScoreEntry(19.0, 80), ScoreEntry(18.0, 79), ScoreEntry(17.0, 77),
            ScoreEntry(16.0, 76), ScoreEntry(15.0, 75), ScoreEntry(14.0, 73),
            ScoreEntry(13.0, 72), ScoreEntry(12.0, 68), ScoreEntry(11.0, 65),
            ScoreEntry(10.0, 60),
            ScoreEntry(9.0, 50), ScoreEntry(8.0, 40), ScoreEntry(7.0, 30),
            ScoreEntry(6.0, 20), ScoreEntry(5.0, 10), ScoreEntry(4.0, 0)
        ),
        AgeBracket.AGE_62_PLUS to listOf(
            ScoreEntry(43.0, 100), ScoreEntry(41.0, 99), ScoreEntry(39.0, 98),
            ScoreEntry(37.0, 97), ScoreEntry(35.0, 96), ScoreEntry(34.0, 95),
            ScoreEntry(33.0, 94), ScoreEntry(31.0, 93), ScoreEntry(30.0, 92),
            ScoreEntry(29.0, 91), ScoreEntry(26.0, 90), ScoreEntry(24.0, 89),
            ScoreEntry(23.0, 87), ScoreEntry(22.0, 86), ScoreEntry(21.0, 85),
            ScoreEntry(20.0, 84), ScoreEntry(19.0, 82), ScoreEntry(18.0, 81),
            ScoreEntry(17.0, 80), ScoreEntry(16.0, 79), ScoreEntry(15.0, 77),
            ScoreEntry(14.0, 76), ScoreEntry(13.0, 72), ScoreEntry(12.0, 70),
            ScoreEntry(11.0, 68), ScoreEntry(10.0, 60),
            ScoreEntry(9.0, 50), ScoreEntry(8.0, 40), ScoreEntry(7.0, 30),
            ScoreEntry(6.0, 20), ScoreEntry(5.0, 10), ScoreEntry(4.0, 0)
        )
    )

    val female: Map<AgeBracket, List<ScoreEntry>> = mapOf(
        AgeBracket.AGE_17_21 to listOf(
            ScoreEntry(53.0, 100), ScoreEntry(48.0, 99), ScoreEntry(44.0, 98),
            ScoreEntry(42.0, 97), ScoreEntry(40.0, 96), ScoreEntry(38.0, 95),
            ScoreEntry(36.0, 94), ScoreEntry(35.0, 93), ScoreEntry(34.0, 92),
            ScoreEntry(33.0, 91), ScoreEntry(32.0, 90), ScoreEntry(31.0, 89),
            ScoreEntry(30.0, 88), ScoreEntry(29.0, 87), ScoreEntry(28.0, 86),
            ScoreEntry(27.0, 85), ScoreEntry(26.0, 84), ScoreEntry(25.0, 83),
            ScoreEntry(24.0, 81), ScoreEntry(23.0, 80), ScoreEntry(22.0, 79),
            ScoreEntry(21.0, 78), ScoreEntry(20.0, 76), ScoreEntry(19.0, 73),
            ScoreEntry(18.0, 70), ScoreEntry(15.0, 68), ScoreEntry(14.0, 66),
            ScoreEntry(13.0, 64), ScoreEntry(12.0, 62), ScoreEntry(11.0, 60),
            ScoreEntry(9.0, 50), ScoreEntry(8.0, 40), ScoreEntry(7.0, 30),
            ScoreEntry(6.0, 20), ScoreEntry(5.0, 10), ScoreEntry(4.0, 0)
        ),
        AgeBracket.AGE_22_26 to listOf(
            ScoreEntry(50.0, 100), ScoreEntry(45.0, 99), ScoreEntry(44.0, 98),
            ScoreEntry(42.0, 97), ScoreEntry(40.0, 96), ScoreEntry(39.0, 95),
            ScoreEntry(38.0, 94), ScoreEntry(36.0, 93), ScoreEntry(35.0, 92),
            ScoreEntry(34.0, 91), ScoreEntry(33.0, 90), ScoreEntry(32.0, 89),
            ScoreEntry(31.0, 88), ScoreEntry(30.0, 87), ScoreEntry(29.0, 86),
            ScoreEntry(28.0, 85), ScoreEntry(27.0, 84), ScoreEntry(26.0, 83),
            ScoreEntry(25.0, 82), ScoreEntry(24.0, 81), ScoreEntry(23.0, 80),
            ScoreEntry(22.0, 78), ScoreEntry(21.0, 77), ScoreEntry(20.0, 76),
            ScoreEntry(19.0, 74), ScoreEntry(18.0, 73), ScoreEntry(17.0, 71),
            ScoreEntry(16.0, 70), ScoreEntry(15.0, 68), ScoreEntry(14.0, 66),
            ScoreEntry(13.0, 64), ScoreEntry(12.0, 62), ScoreEntry(11.0, 60),
            ScoreEntry(9.0, 50), ScoreEntry(8.0, 40), ScoreEntry(7.0, 30),
            ScoreEntry(6.0, 20), ScoreEntry(5.0, 10), ScoreEntry(4.0, 0)
        ),
        AgeBracket.AGE_27_31 to listOf(
            ScoreEntry(48.0, 100), ScoreEntry(45.0, 99), ScoreEntry(43.0, 98),
            ScoreEntry(42.0, 97), ScoreEntry(40.0, 96), ScoreEntry(39.0, 95),
            ScoreEntry(37.0, 94), ScoreEntry(36.0, 93), ScoreEntry(35.0, 92),
            ScoreEntry(34.0, 91), ScoreEntry(33.0, 90), ScoreEntry(32.0, 89),
            ScoreEntry(31.0, 88), ScoreEntry(30.0, 87), ScoreEntry(29.0, 86),
            ScoreEntry(28.0, 85), ScoreEntry(27.0, 84), ScoreEntry(26.0, 83),
            ScoreEntry(25.0, 82), ScoreEntry(24.0, 81), ScoreEntry(23.0, 80),
            ScoreEntry(22.0, 78), ScoreEntry(21.0, 77), ScoreEntry(20.0, 76),
            ScoreEntry(19.0, 74), ScoreEntry(18.0, 73), ScoreEntry(17.0, 71),
            ScoreEntry(16.0, 70), ScoreEntry(15.0, 68), ScoreEntry(14.0, 66),
            ScoreEntry(13.0, 63), ScoreEntry(12.0, 62), ScoreEntry(11.0, 60),
            ScoreEntry(9.0, 50), ScoreEntry(8.0, 40), ScoreEntry(7.0, 30),
            ScoreEntry(6.0, 20), ScoreEntry(5.0, 10), ScoreEntry(4.0, 0)
        ),
        AgeBracket.AGE_32_36 to listOf(
            ScoreEntry(47.0, 100), ScoreEntry(44.0, 99), ScoreEntry(42.0, 98),
            ScoreEntry(40.0, 97), ScoreEntry(39.0, 96), ScoreEntry(38.0, 95),
            ScoreEntry(36.0, 94), ScoreEntry(35.0, 93), ScoreEntry(34.0, 92),
            ScoreEntry(33.0, 91), ScoreEntry(32.0, 90), ScoreEntry(31.0, 89),
            ScoreEntry(30.0, 88), ScoreEntry(29.0, 87), ScoreEntry(28.0, 86),
            ScoreEntry(27.0, 85), ScoreEntry(26.0, 84), ScoreEntry(25.0, 83),
            ScoreEntry(24.0, 82), ScoreEntry(23.0, 80), ScoreEntry(22.0, 79),
            ScoreEntry(21.0, 78), ScoreEntry(20.0, 76), ScoreEntry(19.0, 74),
            ScoreEntry(18.0, 73), ScoreEntry(17.0, 72), ScoreEntry(16.0, 70),
            ScoreEntry(15.0, 68), ScoreEntry(14.0, 67), ScoreEntry(13.0, 65),
            ScoreEntry(12.0, 62), ScoreEntry(11.0, 60),
            ScoreEntry(9.0, 50), ScoreEntry(8.0, 40), ScoreEntry(7.0, 30),
            ScoreEntry(6.0, 20), ScoreEntry(5.0, 10), ScoreEntry(4.0, 0)
        ),
        AgeBracket.AGE_37_41 to listOf(
            ScoreEntry(43.0, 100), ScoreEntry(41.0, 99), ScoreEntry(39.0, 98),
            ScoreEntry(38.0, 97), ScoreEntry(37.0, 96), ScoreEntry(35.0, 95),
            ScoreEntry(34.0, 94), ScoreEntry(33.0, 93), ScoreEntry(32.0, 92),
            ScoreEntry(31.0, 91), ScoreEntry(30.0, 90), ScoreEntry(29.0, 89),
            ScoreEntry(28.0, 88), ScoreEntry(27.0, 87), ScoreEntry(26.0, 85),
            ScoreEntry(25.0, 84), ScoreEntry(24.0, 83), ScoreEntry(23.0, 82),
            ScoreEntry(22.0, 80), ScoreEntry(21.0, 79), ScoreEntry(20.0, 78),
            ScoreEntry(19.0, 76), ScoreEntry(18.0, 74), ScoreEntry(17.0, 73),
            ScoreEntry(16.0, 72), ScoreEntry(15.0, 70), ScoreEntry(14.0, 68),
            ScoreEntry(13.0, 66), ScoreEntry(12.0, 64), ScoreEntry(11.0, 61),
            ScoreEntry(10.0, 60),
            ScoreEntry(9.0, 50), ScoreEntry(8.0, 40), ScoreEntry(7.0, 30),
            ScoreEntry(6.0, 20), ScoreEntry(5.0, 10), ScoreEntry(4.0, 0)
        ),
        AgeBracket.AGE_42_46 to listOf(
            ScoreEntry(40.0, 100), ScoreEntry(38.0, 99), ScoreEntry(37.0, 98),
            ScoreEntry(36.0, 97), ScoreEntry(35.0, 96), ScoreEntry(33.0, 95),
            ScoreEntry(32.0, 94), ScoreEntry(31.0, 93), ScoreEntry(30.0, 92),
            ScoreEntry(29.0, 90), ScoreEntry(28.0, 89), ScoreEntry(27.0, 88),
            ScoreEntry(26.0, 86), ScoreEntry(25.0, 85), ScoreEntry(24.0, 84),
            ScoreEntry(23.0, 83), ScoreEntry(22.0, 82), ScoreEntry(21.0, 80),
            ScoreEntry(20.0, 79), ScoreEntry(19.0, 77), ScoreEntry(18.0, 76),
            ScoreEntry(17.0, 74), ScoreEntry(16.0, 72), ScoreEntry(15.0, 70),
            ScoreEntry(14.0, 68), ScoreEntry(13.0, 66), ScoreEntry(12.0, 64),
            ScoreEntry(11.0, 62), ScoreEntry(10.0, 60),
            ScoreEntry(9.0, 50), ScoreEntry(8.0, 40), ScoreEntry(7.0, 30),
            ScoreEntry(6.0, 20), ScoreEntry(5.0, 10), ScoreEntry(4.0, 0)
        ),
        AgeBracket.AGE_47_51 to listOf(
            ScoreEntry(38.0, 100), ScoreEntry(37.0, 99), ScoreEntry(35.0, 98),
            ScoreEntry(34.0, 97), ScoreEntry(33.0, 96), ScoreEntry(32.0, 95),
            ScoreEntry(31.0, 94), ScoreEntry(30.0, 93), ScoreEntry(29.0, 92),
            ScoreEntry(28.0, 91), ScoreEntry(27.0, 89), ScoreEntry(26.0, 88),
            ScoreEntry(25.0, 87), ScoreEntry(24.0, 86), ScoreEntry(23.0, 84),
            ScoreEntry(22.0, 83), ScoreEntry(21.0, 81), ScoreEntry(20.0, 80),
            ScoreEntry(19.0, 78), ScoreEntry(18.0, 76), ScoreEntry(17.0, 75),
            ScoreEntry(16.0, 73), ScoreEntry(15.0, 71), ScoreEntry(14.0, 69),
            ScoreEntry(13.0, 67), ScoreEntry(12.0, 64), ScoreEntry(11.0, 62),
            ScoreEntry(10.0, 60),
            ScoreEntry(9.0, 50), ScoreEntry(8.0, 40), ScoreEntry(7.0, 30),
            ScoreEntry(6.0, 20), ScoreEntry(5.0, 10), ScoreEntry(4.0, 0)
        ),
        AgeBracket.AGE_52_56 to listOf(
            ScoreEntry(36.0, 100), ScoreEntry(34.0, 99), ScoreEntry(33.0, 98),
            ScoreEntry(32.0, 97), ScoreEntry(31.0, 96), ScoreEntry(30.0, 95),
            ScoreEntry(29.0, 94), ScoreEntry(28.0, 93), ScoreEntry(27.0, 92),
            ScoreEntry(26.0, 90), ScoreEntry(25.0, 89), ScoreEntry(24.0, 88),
            ScoreEntry(23.0, 86), ScoreEntry(22.0, 85), ScoreEntry(21.0, 83),
            ScoreEntry(20.0, 82), ScoreEntry(19.0, 80), ScoreEntry(18.0, 78),
            ScoreEntry(17.0, 76), ScoreEntry(16.0, 74), ScoreEntry(15.0, 72),
            ScoreEntry(14.0, 70), ScoreEntry(13.0, 68), ScoreEntry(12.0, 65),
            ScoreEntry(11.0, 62), ScoreEntry(10.0, 60),
            ScoreEntry(9.0, 50), ScoreEntry(8.0, 40), ScoreEntry(7.0, 30),
            ScoreEntry(6.0, 20), ScoreEntry(5.0, 10), ScoreEntry(4.0, 0)
        ),
        AgeBracket.AGE_57_61 to listOf(
            ScoreEntry(24.0, 100), ScoreEntry(23.0, 99), ScoreEntry(22.0, 98),
            ScoreEntry(21.0, 97), ScoreEntry(20.0, 96), ScoreEntry(19.0, 95),
            ScoreEntry(18.0, 94), ScoreEntry(17.0, 92), ScoreEntry(16.0, 91),
            ScoreEntry(15.0, 90), ScoreEntry(14.0, 89), ScoreEntry(13.0, 84),
            ScoreEntry(12.0, 80), ScoreEntry(11.0, 68), ScoreEntry(10.0, 60),
            ScoreEntry(9.0, 50), ScoreEntry(8.0, 40), ScoreEntry(7.0, 30),
            ScoreEntry(6.0, 20), ScoreEntry(5.0, 10), ScoreEntry(4.0, 0)
        ),
        AgeBracket.AGE_62_PLUS to listOf(
            ScoreEntry(24.0, 100), ScoreEntry(23.0, 99), ScoreEntry(22.0, 98),
            ScoreEntry(21.0, 97), ScoreEntry(20.0, 96), ScoreEntry(19.0, 95),
            ScoreEntry(18.0, 94), ScoreEntry(17.0, 92), ScoreEntry(16.0, 91),
            ScoreEntry(15.0, 90), ScoreEntry(14.0, 89), ScoreEntry(13.0, 86),
            ScoreEntry(12.0, 79), ScoreEntry(11.0, 68), ScoreEntry(10.0, 60),
            ScoreEntry(9.0, 50), ScoreEntry(8.0, 40), ScoreEntry(7.0, 30),
            ScoreEntry(6.0, 20), ScoreEntry(5.0, 10), ScoreEntry(4.0, 0)
        )
    )

    fun getTable(category: OfficialScoreTables.ScoringCategory, ageBracket: AgeBracket): List<ScoreEntry> {
        return when (category) {
            OfficialScoreTables.ScoringCategory.MALE_COMBAT -> maleCombat[ageBracket] ?: maleCombat[AgeBracket.AGE_17_21]!!
            OfficialScoreTables.ScoringCategory.FEMALE -> female[ageBracket] ?: female[AgeBracket.AGE_17_21]!!
        }
    }
}
