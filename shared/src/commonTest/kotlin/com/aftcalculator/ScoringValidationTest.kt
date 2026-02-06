package com.aftcalculator

import com.aftcalculator.models.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

/**
 * Comprehensive scoring validation tests against official HQDA EXORD 218-25 tables.
 * Tests cover exact thresholds, between-threshold step function behavior, female scoring,
 * age bracket variations, edge cases, full score calculations, exemptions, and
 * alternate aerobic events.
 */
class ScoringValidationTest {

    private val calculator = AftCalculator()

    // Helper to create a soldier and calculate a single event score
    private fun score(event: AftEvent, rawValue: Double, age: Int, gender: Gender, mos: MosCategory): Int {
        val soldier = Soldier(age, gender, mos)
        return calculator.calculateSingleEvent(event, rawValue, soldier).points
    }

    // ==================== 1. EXACT THRESHOLD TESTS (~20 cases) ====================

    @Test
    fun deadlift_maleCombat_age2226_350lbs_equals100() {
        assertEquals(100, score(AftEvent.DEADLIFT, 350.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun deadlift_maleCombat_age2226_200lbs_equals71() {
        assertEquals(71, score(AftEvent.DEADLIFT, 200.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun deadlift_maleCombat_age2226_150lbs_equals60() {
        assertEquals(60, score(AftEvent.DEADLIFT, 150.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun deadlift_maleCombat_age2226_80lbs_equals0() {
        assertEquals(0, score(AftEvent.DEADLIFT, 80.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun pushUp_maleCombat_age2226_61reps_equals100() {
        assertEquals(100, score(AftEvent.PUSH_UP, 61.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun pushUp_maleCombat_age2226_14reps_equals60() {
        assertEquals(60, score(AftEvent.PUSH_UP, 14.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun pushUp_maleCombat_age2226_4reps_equals0() {
        assertEquals(0, score(AftEvent.PUSH_UP, 4.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun sdc_maleCombat_age2226_90sec_equals100() {
        assertEquals(100, score(AftEvent.SPRINT_DRAG_CARRY, 90.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun sdc_maleCombat_age2226_151sec_equals60() {
        assertEquals(60, score(AftEvent.SPRINT_DRAG_CARRY, 151.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun sdc_maleCombat_age2226_211sec_equals0() {
        assertEquals(0, score(AftEvent.SPRINT_DRAG_CARRY, 211.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun plank_age2226_215sec_equals100() {
        assertEquals(100, score(AftEvent.PLANK, 215.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun plank_age2226_85sec_equals60() {
        assertEquals(60, score(AftEvent.PLANK, 85.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun plank_age2226_55sec_equals0() {
        assertEquals(0, score(AftEvent.PLANK, 55.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun twoMileRun_maleCombat_age2226_805sec_equals100() {
        assertEquals(100, score(AftEvent.TWO_MILE_RUN, 805.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun twoMileRun_maleCombat_age2226_1185sec_equals60() {
        assertEquals(60, score(AftEvent.TWO_MILE_RUN, 1185.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun twoMileRun_maleCombat_age2226_1353sec_equals0() {
        assertEquals(0, score(AftEvent.TWO_MILE_RUN, 1353.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun deadlift_maleCombat_age1721_340lbs_equals100() {
        assertEquals(100, score(AftEvent.DEADLIFT, 340.0, 20, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun deadlift_maleCombat_age1721_150lbs_equals60() {
        assertEquals(60, score(AftEvent.DEADLIFT, 150.0, 20, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun pushUp_maleCombat_age1721_58reps_equals100() {
        assertEquals(100, score(AftEvent.PUSH_UP, 58.0, 20, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun pushUp_maleCombat_age1721_15reps_equals60() {
        assertEquals(60, score(AftEvent.PUSH_UP, 15.0, 20, Gender.MALE, MosCategory.COMBAT))
    }

    // ==================== 2. BETWEEN-THRESHOLD TESTS (~20 cases) ====================

    @Test
    fun deadlift_between_195lbs_getsLowerThreshold() {
        // 195 is between 200=71 and 190=70, should get 70 (step function: not yet at 200)
        assertEquals(70, score(AftEvent.DEADLIFT, 195.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun deadlift_between_145lbs_getsLowerThreshold() {
        // 145 between 150=60 and 130=50 → 50 (not yet at 150)
        assertEquals(50, score(AftEvent.DEADLIFT, 145.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun deadlift_between_345lbs_gets99() {
        // 345 between 350=100 and 340=99 → 99
        assertEquals(99, score(AftEvent.DEADLIFT, 345.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun deadlift_between_305lbs_gets91() {
        // 305 between 310=93 and 300=91 → 91
        assertEquals(91, score(AftEvent.DEADLIFT, 305.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun pushUp_between_16reps_gets61() {
        // 16 between 17=62 and 15=61 → 61
        assertEquals(61, score(AftEvent.PUSH_UP, 16.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun pushUp_between_20reps_gets64() {
        // 20 between 21=65 and 19=64, should get 64
        assertEquals(64, score(AftEvent.PUSH_UP, 20.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun pushUp_between_60reps_gets99() {
        // 60 between 61=100 and 59=99 → 99
        assertEquals(99, score(AftEvent.PUSH_UP, 60.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun sdc_between_91sec_gets99() {
        // SDC lower is better. 91 between 90=100 and 92=99 → 99 (not fast enough for 100)
        assertEquals(99, score(AftEvent.SPRINT_DRAG_CARRY, 91.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun sdc_between_100sec_gets93() {
        // 100 exactly matches entry 100=93 in AGE_22_26 table
        assertEquals(93, score(AftEvent.SPRINT_DRAG_CARRY, 100.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun sdc_between_152sec_gets50() {
        // 152 between 151=60 and 161=50 → 50 (slower than 151)
        assertEquals(50, score(AftEvent.SPRINT_DRAG_CARRY, 152.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun plank_between_100sec_gets64() {
        // Plank higher is better. 100 between 108=68 and 97=66... check table
        // Age 22-26: 111=68, 108=67, 105=66... actually let's look at exact table entries
        // t(1,48)=108 → 67, t(1,45)=105 → 66; 100 sec = t(1,40)=100 → 65... wait
        // Actually: t(1,51)=111→68, t(1,48)=108→67, t(1,45)=105→66, t(1,41)=101→65, t(1,38)=98→64
        // 100 sec is between 101=65 and 98=64 → 64 (hasn't reached 101)
        assertEquals(64, score(AftEvent.PLANK, 100.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun plank_between_200sec_gets95() {
        // Age 22-26 plank: t(3,9)=189→92, t(3,12)=192→93... wait, descending
        // Actually plank 22-26: t(3,12)=192→93, t(3,9)=189→92
        // 200 sec >= 189 so meets 92, and >= 192 meets 93, check if >= 192
        // 200 >= 192 = true, so 93. Check 195: t(3,16)=196→94? No, let's check.
        // t(3,19)=199→95, t(3,16)=196→94, t(3,12)=192→93, t(3,9)=189→92
        // 200 >= 199 → 95. Wait that doesn't seem right either.
        // Actually reading: ScoreEntry(t(3, 19), 95), so 3:19 = 199 sec = 95 pts
        // ScoreEntry(t(3, 16), 94) = 196 sec = 94 pts
        // 200 sec >= 199 sec → meets 95 pts. And >= 196 → meets 94. And >= 199 → 95.
        // But also check: t(3,22)=202→96. 200 < 202, so doesn't meet 96.
        // So 200 sec → 95 pts
        assertEquals(95, score(AftEvent.PLANK, 200.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun twoMileRun_between_1000sec_gets83() {
        // 2MR lower is better. Age 22-26:
        // t(16,49)=1009→83, t(16,39)=999→84
        // 1000 sec: is it <= 1009? Yes → 83. Is it <= 999? No. So 83.
        assertEquals(83, score(AftEvent.TWO_MILE_RUN, 1000.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun twoMileRun_between_810sec_gets99() {
        // t(13,25)=805→100, t(13,47)=827→99
        // 810 <= 827 → 99. 810 <= 805? No. So 99.
        assertEquals(99, score(AftEvent.TWO_MILE_RUN, 810.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun twoMileRun_between_1186sec_gets50() {
        // t(19,45)=1185→60, t(20,13)=1213→50
        // 1186 <= 1213 → 50. 1186 <= 1185? No. So 50.
        assertEquals(50, score(AftEvent.TWO_MILE_RUN, 1186.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun deadlift_between_85lbs_gets0() {
        // 85 between 90=10 and 80=0 → 0
        assertEquals(0, score(AftEvent.DEADLIFT, 85.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun pushUp_between_10reps_gets50() {
        // 10 between 14=60 and 9=50 → 50
        assertEquals(50, score(AftEvent.PUSH_UP, 10.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun pushUp_between_13reps_gets50() {
        // 13 between 14=60 and 9=50 → 50 (hasn't reached 14)
        assertEquals(50, score(AftEvent.PUSH_UP, 13.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun deadlift_between_125lbs_gets40() {
        // 125 between 130=50 and 120=40 → 40
        assertEquals(40, score(AftEvent.DEADLIFT, 125.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    // ==================== 3. FEMALE SCORING TESTS (~15 cases) ====================

    @Test
    fun deadlift_femaleCombatEnabling_age2226_230lbs_equals100() {
        assertEquals(100, score(AftEvent.DEADLIFT, 230.0, 25, Gender.FEMALE, MosCategory.COMBAT_ENABLING))
    }

    @Test
    fun deadlift_femaleCombatEnabling_age2226_120lbs_equals60() {
        assertEquals(60, score(AftEvent.DEADLIFT, 120.0, 25, Gender.FEMALE, MosCategory.COMBAT_ENABLING))
    }

    @Test
    fun deadlift_femaleCombat_age2226_usesMaleTable_150lbs_equals60() {
        // Female in Combat MOS uses male/combat tables (sex-neutral)
        assertEquals(60, score(AftEvent.DEADLIFT, 150.0, 25, Gender.FEMALE, MosCategory.COMBAT))
    }

    @Test
    fun deadlift_femaleCombat_sameAsMale() {
        // Combat MOS is sex-neutral - female should get same score as male
        val femaleScore = score(AftEvent.DEADLIFT, 280.0, 25, Gender.FEMALE, MosCategory.COMBAT)
        val maleScore = score(AftEvent.DEADLIFT, 280.0, 25, Gender.MALE, MosCategory.COMBAT)
        assertEquals(maleScore, femaleScore)
    }

    @Test
    fun pushUp_femaleCombatEnabling_age2226_50reps_equals100() {
        assertEquals(100, score(AftEvent.PUSH_UP, 50.0, 25, Gender.FEMALE, MosCategory.COMBAT_ENABLING))
    }

    @Test
    fun pushUp_femaleCombatEnabling_age2226_11reps_equals60() {
        assertEquals(60, score(AftEvent.PUSH_UP, 11.0, 25, Gender.FEMALE, MosCategory.COMBAT_ENABLING))
    }

    @Test
    fun twoMileRun_femaleCombatEnabling_age2226_930sec_equals100() {
        assertEquals(100, score(AftEvent.TWO_MILE_RUN, 930.0, 25, Gender.FEMALE, MosCategory.COMBAT_ENABLING))
    }

    @Test
    fun twoMileRun_femaleCombatEnabling_age2226_1365sec_equals60() {
        assertEquals(60, score(AftEvent.TWO_MILE_RUN, 1365.0, 25, Gender.FEMALE, MosCategory.COMBAT_ENABLING))
    }

    @Test
    fun twoMileRun_femaleCombat_usesMaleTable() {
        // Female in Combat MOS uses male tables
        val femaleScore = score(AftEvent.TWO_MILE_RUN, 1000.0, 25, Gender.FEMALE, MosCategory.COMBAT)
        val maleScore = score(AftEvent.TWO_MILE_RUN, 1000.0, 25, Gender.MALE, MosCategory.COMBAT)
        assertEquals(maleScore, femaleScore)
    }

    @Test
    fun sdc_femaleCombatEnabling_age1721_195sec_equals60() {
        // Female SDC age 17-21: t(3,15)=195→60
        assertEquals(60, score(AftEvent.SPRINT_DRAG_CARRY, 195.0, 20, Gender.FEMALE, MosCategory.COMBAT_ENABLING))
    }

    @Test
    fun sdc_femaleCombatEnabling_age1721_115sec_equals100() {
        assertEquals(100, score(AftEvent.SPRINT_DRAG_CARRY, 115.0, 20, Gender.FEMALE, MosCategory.COMBAT_ENABLING))
    }

    @Test
    fun plank_genderNeutral() {
        // Plank is gender-neutral - male and female should score the same
        val maleScore = score(AftEvent.PLANK, 150.0, 25, Gender.MALE, MosCategory.COMBAT)
        val femaleScore = score(AftEvent.PLANK, 150.0, 25, Gender.FEMALE, MosCategory.COMBAT_ENABLING)
        assertEquals(maleScore, femaleScore)
    }

    @Test
    fun deadlift_femaleCombatEnabling_age3741_220lbs_equals100() {
        // Female age 37-41: 220=100
        assertEquals(100, score(AftEvent.DEADLIFT, 220.0, 40, Gender.FEMALE, MosCategory.COMBAT_ENABLING))
    }

    @Test
    fun deadlift_femaleCombatEnabling_age3741_120lbs_equals60() {
        // Female age 37-41: 120=60
        assertEquals(60, score(AftEvent.DEADLIFT, 120.0, 40, Gender.FEMALE, MosCategory.COMBAT_ENABLING))
    }

    @Test
    fun pushUp_femaleCombatEnabling_age4246_40reps_equals100() {
        // Female pushups age 42-46: 40=100
        assertEquals(100, score(AftEvent.PUSH_UP, 40.0, 45, Gender.FEMALE, MosCategory.COMBAT_ENABLING))
    }

    // ==================== 4. AGE BRACKET TESTS (~15 cases) ====================

    @Test
    fun deadlift_200lbs_age1721_equals70() {
        assertEquals(70, score(AftEvent.DEADLIFT, 200.0, 20, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun deadlift_200lbs_age2226_equals71() {
        assertEquals(71, score(AftEvent.DEADLIFT, 200.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun deadlift_200lbs_age4246_equals70() {
        assertEquals(70, score(AftEvent.DEADLIFT, 200.0, 45, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun deadlift_200lbs_age62plus_equals95() {
        // Age 62+: 200=95
        assertEquals(95, score(AftEvent.DEADLIFT, 200.0, 65, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun deadlift_200lbs_age5761_equals94() {
        // Age 57-61: 200=94
        assertEquals(94, score(AftEvent.DEADLIFT, 200.0, 60, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun twoMileRun_1080sec_age1721_vs_age5256() {
        // Older soldiers should score higher with the same time
        val youngScore = score(AftEvent.TWO_MILE_RUN, 1080.0, 20, Gender.MALE, MosCategory.COMBAT)
        val olderScore = score(AftEvent.TWO_MILE_RUN, 1080.0, 55, Gender.MALE, MosCategory.COMBAT)
        assertTrue(olderScore > youngScore, "Age 52-56 ($olderScore) should score higher than 17-21 ($youngScore) at 18:00")
    }

    @Test
    fun plank_120sec_age1721_equals69() {
        // Age 17-21: t(2,9)=129→72? No. Let me check: t(2,2)=122→70, t(2,6)=126→71, t(2,9)=129→72
        // 120 sec = 2:00. t(1,59)=119→69, t(2,2)=122→70
        // 120 >= 119 → 69, 120 >= 122? No. So 69.
        assertEquals(69, score(AftEvent.PLANK, 120.0, 20, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun plank_120sec_age2226_equals70() {
        // Age 22-26: t(2,7)=127→73, t(2,4)=124→72, t(2,1)=121→71
        // 120 >= 121? No. 120 >= 118 (=t(1,58)=118→70)? Yes → 70
        assertEquals(70, score(AftEvent.PLANK, 120.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun pushUp_30reps_age2226_equals74() {
        assertEquals(74, score(AftEvent.PUSH_UP, 30.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun pushUp_30reps_age4246_equals76() {
        assertEquals(76, score(AftEvent.PUSH_UP, 30.0, 45, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun pushUp_30reps_age5761_equals91() {
        assertEquals(91, score(AftEvent.PUSH_UP, 30.0, 60, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun pushUp_30reps_age62plus_equals92() {
        assertEquals(92, score(AftEvent.PUSH_UP, 30.0, 65, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun sdc_120sec_age3236_equals78() {
        // Age 32-36: t(2,0)=120→78 exactly matches
        assertEquals(78, score(AftEvent.SPRINT_DRAG_CARRY, 120.0, 35, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun sdc_120sec_age4751_equals92() {
        // Age 47-51: t(2,13)=133→81, t(2,12)=132→82...
        // 120 <= 120=t(2,0)? t(2,0)=120. Check: t(2,0)=120→92, t(2,1)=121→91
        // 120 <= 120 → 92
        assertEquals(92, score(AftEvent.SPRINT_DRAG_CARRY, 120.0, 50, Gender.MALE, MosCategory.COMBAT))
    }

    // ==================== 5. EDGE CASES (~10 cases) ====================

    @Test
    fun deadlift_aboveMax_400lbs_equals100() {
        // 400 lbs > 350 max → should still get 100
        assertEquals(100, score(AftEvent.DEADLIFT, 400.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun deadlift_belowMin_50lbs_equals0() {
        // 50 lbs < 80 min → 0
        assertEquals(0, score(AftEvent.DEADLIFT, 50.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun pushUp_aboveMax_100reps_equals100() {
        assertEquals(100, score(AftEvent.PUSH_UP, 100.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun pushUp_belowMin_3reps_equals0() {
        assertEquals(0, score(AftEvent.PUSH_UP, 3.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun twoMileRun_veryFastTime_600sec_equals100() {
        assertEquals(100, score(AftEvent.TWO_MILE_RUN, 600.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun twoMileRun_verySlowTime_1500sec_equals0() {
        assertEquals(0, score(AftEvent.TWO_MILE_RUN, 1500.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun plank_aboveMax_300sec_equals100() {
        assertEquals(100, score(AftEvent.PLANK, 300.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun sdc_veryFastTime_60sec_equals100() {
        assertEquals(100, score(AftEvent.SPRINT_DRAG_CARRY, 60.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun deadlift_exactlyAtPassingBoundary_150lbs_passes() {
        val soldier = Soldier(25, Gender.MALE, MosCategory.COMBAT)
        val result = calculator.calculateSingleEvent(AftEvent.DEADLIFT, 150.0, soldier)
        assertTrue(result.passed, "150 lbs should pass with exactly 60 pts")
        assertEquals(60, result.points)
    }

    @Test
    fun deadlift_oneBelow_149lbs_fails() {
        val soldier = Soldier(25, Gender.MALE, MosCategory.COMBAT)
        val result = calculator.calculateSingleEvent(AftEvent.DEADLIFT, 149.0, soldier)
        assertFalse(result.passed, "149 lbs should fail (below 60 pts)")
    }

    // ==================== 6. FULL SCORE CALCULATION TESTS (~10 cases) ====================

    @Test
    fun fullScore_allMaxScores_combat_passes() {
        val soldier = Soldier(25, Gender.MALE, MosCategory.COMBAT)
        val inputs = listOf(
            EventInput(AftEvent.DEADLIFT, 350.0),
            EventInput(AftEvent.PUSH_UP, 61.0),
            EventInput(AftEvent.SPRINT_DRAG_CARRY, 90.0),
            EventInput(AftEvent.PLANK, 215.0),
            EventInput(AftEvent.TWO_MILE_RUN, 805.0)
        )
        val result = calculator.calculateScore(soldier, inputs)
        assertTrue(result.passed)
        assertEquals(500, result.totalPoints)
        assertTrue(result.failureReasons.isEmpty())
    }

    @Test
    fun fullScore_allAt60_combat_fails() {
        // Combat needs 350 total; 5 * 60 = 300 < 350
        val soldier = Soldier(25, Gender.MALE, MosCategory.COMBAT)
        val inputs = listOf(
            EventInput(AftEvent.DEADLIFT, 150.0),      // 60
            EventInput(AftEvent.PUSH_UP, 14.0),         // 60
            EventInput(AftEvent.SPRINT_DRAG_CARRY, 151.0), // 60
            EventInput(AftEvent.PLANK, 85.0),            // 60
            EventInput(AftEvent.TWO_MILE_RUN, 1185.0)   // 60
        )
        val result = calculator.calculateScore(soldier, inputs)
        assertFalse(result.passed)
        assertEquals(300, result.totalPoints)
    }

    @Test
    fun fullScore_allAt60_combatEnabling_passes() {
        // Combat-Enabling needs 300 total; 5 * 60 = 300 = 300
        val soldier = Soldier(25, Gender.MALE, MosCategory.COMBAT_ENABLING)
        val inputs = listOf(
            EventInput(AftEvent.DEADLIFT, 150.0),      // 60
            EventInput(AftEvent.PUSH_UP, 14.0),         // 60
            EventInput(AftEvent.SPRINT_DRAG_CARRY, 151.0), // 60
            EventInput(AftEvent.PLANK, 85.0),            // 60
            EventInput(AftEvent.TWO_MILE_RUN, 1185.0)   // 60
        )
        val result = calculator.calculateScore(soldier, inputs)
        assertTrue(result.passed, "300 pts should pass for Combat-Enabling")
        assertEquals(300, result.totalPoints)
    }

    @Test
    fun fullScore_oneEventFails_overallFails() {
        val soldier = Soldier(25, Gender.MALE, MosCategory.COMBAT_ENABLING)
        val inputs = listOf(
            EventInput(AftEvent.DEADLIFT, 100.0),       // 20 - FAIL
            EventInput(AftEvent.PUSH_UP, 61.0),          // 100
            EventInput(AftEvent.SPRINT_DRAG_CARRY, 90.0), // 100
            EventInput(AftEvent.PLANK, 215.0),            // 100
            EventInput(AftEvent.TWO_MILE_RUN, 805.0)     // 100
        )
        val result = calculator.calculateScore(soldier, inputs)
        assertFalse(result.passed, "Should fail because deadlift < 60")
        assertTrue(result.failureReasons.any { it.contains("Deadlift") || it.contains("deadlift") })
    }

    @Test
    fun fullScore_highTotal_butOneBelow60_fails() {
        // Total is above 350, but one event below 60 should still fail
        val soldier = Soldier(25, Gender.MALE, MosCategory.COMBAT)
        val inputs = listOf(
            EventInput(AftEvent.DEADLIFT, 130.0),       // 50 - FAIL
            EventInput(AftEvent.PUSH_UP, 61.0),          // 100
            EventInput(AftEvent.SPRINT_DRAG_CARRY, 90.0), // 100
            EventInput(AftEvent.PLANK, 215.0),            // 100
            EventInput(AftEvent.TWO_MILE_RUN, 805.0)     // 100
        )
        val result = calculator.calculateScore(soldier, inputs)
        assertFalse(result.passed)
        assertEquals(450, result.totalPoints)
    }

    @Test
    fun fullScore_combatMinimum350_passes() {
        // Need exactly 350 for combat
        val soldier = Soldier(25, Gender.MALE, MosCategory.COMBAT)
        val inputs = listOf(
            EventInput(AftEvent.DEADLIFT, 150.0),        // 60
            EventInput(AftEvent.PUSH_UP, 61.0),           // 100
            EventInput(AftEvent.SPRINT_DRAG_CARRY, 90.0),  // 100
            EventInput(AftEvent.PLANK, 85.0),              // 60
            EventInput(AftEvent.TWO_MILE_RUN, 1185.0)     // 60
        )
        val result = calculator.calculateScore(soldier, inputs)
        assertEquals(380, result.totalPoints)
        assertTrue(result.passed)
    }

    @Test
    fun fullScore_femaleComEnabling_getsFemaleTables() {
        val soldier = Soldier(25, Gender.FEMALE, MosCategory.COMBAT_ENABLING)
        // Female deadlift at 230 lbs should get 100 on female table
        val inputs = listOf(
            EventInput(AftEvent.DEADLIFT, 230.0),        // 100 (female)
            EventInput(AftEvent.PUSH_UP, 50.0),           // 100 (female)
            EventInput(AftEvent.SPRINT_DRAG_CARRY, 115.0), // 100 (female)
            EventInput(AftEvent.PLANK, 215.0),             // 100 (plank is neutral)
            EventInput(AftEvent.TWO_MILE_RUN, 930.0)      // 100 (female)
        )
        val result = calculator.calculateScore(soldier, inputs)
        assertTrue(result.passed)
        assertEquals(500, result.totalPoints)
    }

    @Test
    fun fullScore_femaleCombat_usesMaleTables() {
        val soldier = Soldier(25, Gender.FEMALE, MosCategory.COMBAT)
        // Female in Combat MOS at 350 lbs deadlift should get 100 on MALE table
        val dlScore = calculator.calculateSingleEvent(AftEvent.DEADLIFT, 350.0, soldier)
        assertEquals(100, dlScore.points)
        // And at 150 lbs should get 60 (male table)
        val dlScore2 = calculator.calculateSingleEvent(AftEvent.DEADLIFT, 150.0, soldier)
        assertEquals(60, dlScore2.points)
    }

    @Test
    fun fullScore_totalBelow350_allAbove60_failsCombat() {
        val soldier = Soldier(25, Gender.MALE, MosCategory.COMBAT)
        val inputs = listOf(
            EventInput(AftEvent.DEADLIFT, 150.0),        // 60
            EventInput(AftEvent.PUSH_UP, 14.0),           // 60
            EventInput(AftEvent.SPRINT_DRAG_CARRY, 151.0), // 60
            EventInput(AftEvent.PLANK, 85.0),              // 60
            EventInput(AftEvent.TWO_MILE_RUN, 1185.0)     // 60
        )
        val result = calculator.calculateScore(soldier, inputs)
        assertFalse(result.passed, "300 total < 350 required for Combat")
        assertTrue(result.failureReasons.any { it.contains("Total") || it.contains("total") })
    }

    // ==================== 7. EXEMPTION TESTS (~10 cases) ====================

    @Test
    fun exemption_4events_combat_exemptGets60pts() {
        // 4 events taken, 1 exempt = 60 pts automatic. Min stays 350.
        val soldier = Soldier(25, Gender.MALE, MosCategory.COMBAT)
        val inputs = listOf(
            EventInput(AftEvent.DEADLIFT, 350.0),        // 100
            EventInput(AftEvent.PUSH_UP, 61.0),           // 100
            EventInput(AftEvent.SPRINT_DRAG_CARRY, 90.0),  // 100
            EventInput(AftEvent.PLANK, 215.0)              // 100
            // 2MR exempt → 60 pts
        )
        val result = calculator.calculateScore(soldier, inputs)
        assertEquals(460, result.totalPoints) // 400 scored + 60 exempt
        assertTrue(result.passed)
    }

    @Test
    fun exemption_4events_combatEnabling_passes() {
        val soldier = Soldier(25, Gender.MALE, MosCategory.COMBAT_ENABLING)
        val inputs = listOf(
            EventInput(AftEvent.DEADLIFT, 150.0),        // 60
            EventInput(AftEvent.PUSH_UP, 14.0),           // 60
            EventInput(AftEvent.SPRINT_DRAG_CARRY, 151.0), // 60
            EventInput(AftEvent.PLANK, 85.0)               // 60
            // 2MR exempt → 60 pts
        )
        val result = calculator.calculateScore(soldier, inputs)
        assertEquals(300, result.totalPoints) // 240 scored + 60 exempt
        assertTrue(result.passed, "300 pts meets 300 minimum for Combat-Enabling")
    }

    @Test
    fun exemption_4events_combat_borderlineFails() {
        // All at minimum 60, plus 1 exempt = 60. Total = 300. Combat needs 350.
        val soldier = Soldier(25, Gender.MALE, MosCategory.COMBAT)
        val inputs = listOf(
            EventInput(AftEvent.DEADLIFT, 150.0),        // 60
            EventInput(AftEvent.PUSH_UP, 14.0),           // 60
            EventInput(AftEvent.SPRINT_DRAG_CARRY, 151.0), // 60
            EventInput(AftEvent.PLANK, 85.0)               // 60
            // 1 exempt → 60
        )
        val result = calculator.calculateScore(soldier, inputs)
        assertEquals(300, result.totalPoints) // 240 + 60
        assertFalse(result.passed, "300 < 350 required for Combat")
    }

    @Test
    fun exemption_3events_combat_passes() {
        // 3 events taken, 2 exempt = 120 pts automatic. Min stays 350.
        val soldier = Soldier(25, Gender.MALE, MosCategory.COMBAT)
        val inputs = listOf(
            EventInput(AftEvent.DEADLIFT, 350.0),        // 100
            EventInput(AftEvent.PUSH_UP, 61.0),           // 100
            EventInput(AftEvent.SPRINT_DRAG_CARRY, 90.0)   // 100
            // 2 exempt → 120 pts
        )
        val result = calculator.calculateScore(soldier, inputs)
        assertEquals(420, result.totalPoints) // 300 + 120
        assertTrue(result.passed)
    }

    @Test
    fun exemption_3events_combatEnabling_passes() {
        val soldier = Soldier(25, Gender.MALE, MosCategory.COMBAT_ENABLING)
        val inputs = listOf(
            EventInput(AftEvent.DEADLIFT, 150.0),        // 60
            EventInput(AftEvent.PUSH_UP, 14.0),           // 60
            EventInput(AftEvent.SPRINT_DRAG_CARRY, 151.0)  // 60
            // 2 exempt → 120 pts
        )
        val result = calculator.calculateScore(soldier, inputs)
        assertEquals(300, result.totalPoints) // 180 + 120
        assertTrue(result.passed, "300 meets 300 for Combat-Enabling")
    }

    @Test
    fun exemption_3events_combat_borderlineFails() {
        // All at 60, 2 exempt = 120. Total = 300. Combat needs 350.
        val soldier = Soldier(25, Gender.MALE, MosCategory.COMBAT)
        val inputs = listOf(
            EventInput(AftEvent.DEADLIFT, 150.0),        // 60
            EventInput(AftEvent.PUSH_UP, 14.0),           // 60
            EventInput(AftEvent.SPRINT_DRAG_CARRY, 151.0)  // 60
        )
        val result = calculator.calculateScore(soldier, inputs)
        assertEquals(300, result.totalPoints)
        assertFalse(result.passed, "300 < 350 for Combat")
    }

    @Test
    fun exemption_1event_gets240exemptPoints() {
        // 1 event taken, 4 exempt = 240 pts automatic
        val soldier = Soldier(25, Gender.MALE, MosCategory.COMBAT)
        val inputs = listOf(
            EventInput(AftEvent.DEADLIFT, 350.0)  // 100
        )
        val result = calculator.calculateScore(soldier, inputs)
        assertEquals(340, result.totalPoints) // 100 + 240
    }

    @Test
    fun exemption_failingEvent_stillFails() {
        // Even with exempt bonus, a failing event (<60) causes overall fail
        val soldier = Soldier(25, Gender.MALE, MosCategory.COMBAT_ENABLING)
        val inputs = listOf(
            EventInput(AftEvent.DEADLIFT, 100.0),  // 20 - FAIL
            EventInput(AftEvent.PUSH_UP, 61.0),     // 100
            EventInput(AftEvent.PLANK, 215.0)        // 100
            // 2 exempt → 120
        )
        val result = calculator.calculateScore(soldier, inputs)
        assertEquals(340, result.totalPoints) // 220 + 120
        assertFalse(result.passed, "Should fail because deadlift < 60")
    }

    @Test
    fun exemption_5events_noExemptBonus() {
        // All 5 events taken, no exempt bonus
        val soldier = Soldier(25, Gender.MALE, MosCategory.COMBAT)
        val inputs = listOf(
            EventInput(AftEvent.DEADLIFT, 280.0),         // 87
            EventInput(AftEvent.PUSH_UP, 40.0),            // 83
            EventInput(AftEvent.SPRINT_DRAG_CARRY, 110.0),  // 82 (t(1,50)=110→83? check)
            EventInput(AftEvent.PLANK, 180.0),              // 83 (t(3,0)=180→... check)
            EventInput(AftEvent.TWO_MILE_RUN, 900.0)       // check
        )
        val result = calculator.calculateScore(soldier, inputs)
        val expectedTotal = result.eventScores.sumOf { it.points }
        assertEquals(expectedTotal, result.totalPoints, "No exempt bonus with 5 events")
    }

    // ==================== 8. ALTERNATE AEROBIC EVENT TESTS (~10 cases) ====================

    @Test
    fun walk_maleCombat_age2226_atLimit_passes() {
        // Walk male age 22-26: max passing time = 30:45 = 1845 sec
        assertEquals(60, score(AftEvent.WALK_2_5_MILE, 1845.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun walk_maleCombat_age2226_overLimit_fails() {
        assertEquals(0, score(AftEvent.WALK_2_5_MILE, 1846.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun bike_femaleCombatEnabling_age2731_atLimit_passes() {
        // Bike female age 27-31: max = 28:07 = 1687 sec
        assertEquals(60, score(AftEvent.BIKE_12K, 1687.0, 30, Gender.FEMALE, MosCategory.COMBAT_ENABLING))
    }

    @Test
    fun bike_femaleCombatEnabling_age2731_overLimit_fails() {
        assertEquals(0, score(AftEvent.BIKE_12K, 1688.0, 30, Gender.FEMALE, MosCategory.COMBAT_ENABLING))
    }

    @Test
    fun swim_maleCombat_age4751_atLimit_passes() {
        // Swim male age 47-51: max = 31:48 = 1908 sec
        assertEquals(60, score(AftEvent.SWIM_1K, 1908.0, 50, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun swim_maleCombat_age4751_overLimit_fails() {
        assertEquals(0, score(AftEvent.SWIM_1K, 1909.0, 50, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun row_sameAsSwim_standards() {
        // Row uses same standards as swim
        val swimScore = score(AftEvent.SWIM_1K, 1908.0, 50, Gender.MALE, MosCategory.COMBAT)
        val rowScore = score(AftEvent.ROW_5K, 1908.0, 50, Gender.MALE, MosCategory.COMBAT)
        assertEquals(swimScore, rowScore)
    }

    @Test
    fun walk_femaleCombat_usesMaleStandards() {
        // Female in Combat MOS uses male/combat (sex-neutral) standards
        val maleScore = score(AftEvent.WALK_2_5_MILE, 1845.0, 25, Gender.MALE, MosCategory.COMBAT)
        val femaleScore = score(AftEvent.WALK_2_5_MILE, 1845.0, 25, Gender.FEMALE, MosCategory.COMBAT)
        assertEquals(maleScore, femaleScore, "Combat MOS should be sex-neutral for alternate events")
    }

    @Test
    fun alternateAerobic_zeroTime_fails() {
        assertEquals(0, score(AftEvent.WALK_2_5_MILE, 0.0, 25, Gender.MALE, MosCategory.COMBAT))
    }

    @Test
    fun alternateAerobic_negativeTime_fails() {
        assertEquals(0, score(AftEvent.SWIM_1K, -1.0, 25, Gender.MALE, MosCategory.COMBAT))
    }
}
