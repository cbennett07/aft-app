package com.aftcalculator.scoring

import com.aftcalculator.models.AgeBracket
import com.aftcalculator.models.Gender
import com.aftcalculator.models.MosCategory

/**
 * Official AFT Score Tables
 * Source: HQDA EXORD 218-25, Annex B - Scoring Tables
 * Approved: 1 May 2025, Effective: 1 June 2025
 *
 * M|C = Male or Combat MOS (sex-neutral scoring)
 * F = Female (for combat-enabling MOS only)
 */
object OfficialScoreTables {

    /**
     * Returns the scoring category based on gender and MOS
     * Combat MOS: Everyone uses M|C (sex-neutral) tables - males AND females
     * Non-Combat MOS: Males use M|C tables, Females use F tables
     */
    fun getScoringCategory(gender: Gender, mosCategory: MosCategory): ScoringCategory {
        return when (mosCategory) {
            MosCategory.COMBAT -> ScoringCategory.MALE_COMBAT // Sex-neutral for all
            MosCategory.COMBAT_ENABLING -> when (gender) {
                Gender.MALE -> ScoringCategory.MALE_COMBAT
                Gender.FEMALE -> ScoringCategory.FEMALE
            }
        }
    }

    enum class ScoringCategory {
        MALE_COMBAT, // M|C column - used for all Combat MOS and Male Combat-Enabling
        FEMALE       // F column - used only for Female Combat-Enabling
    }

    // Key score points from official tables (points -> raw value)
    // For full interpolation between these anchor points

    // ==================== DEADLIFT (MDL) ====================
    // Values in LBS, higher is better
    val deadliftMaleCombat: Map<AgeBracket, List<ScoreEntry>> = mapOf(
        AgeBracket.AGE_17_21 to listOf(
            ScoreEntry(340.0, 100), ScoreEntry(330.0, 98), ScoreEntry(320.0, 96),
            ScoreEntry(310.0, 94), ScoreEntry(300.0, 92), ScoreEntry(290.0, 89),
            ScoreEntry(280.0, 87), ScoreEntry(270.0, 85), ScoreEntry(260.0, 83),
            ScoreEntry(250.0, 81), ScoreEntry(240.0, 79), ScoreEntry(230.0, 77),
            ScoreEntry(220.0, 75), ScoreEntry(210.0, 73), ScoreEntry(200.0, 70),
            ScoreEntry(190.0, 69), ScoreEntry(180.0, 67), ScoreEntry(170.0, 65),
            ScoreEntry(160.0, 63), ScoreEntry(150.0, 60),
            ScoreEntry(130.0, 50), ScoreEntry(120.0, 40), ScoreEntry(110.0, 30),
            ScoreEntry(100.0, 20), ScoreEntry(90.0, 10), ScoreEntry(80.0, 0)
        ),
        AgeBracket.AGE_22_26 to listOf(
            ScoreEntry(350.0, 100), ScoreEntry(340.0, 99), ScoreEntry(330.0, 97),
            ScoreEntry(320.0, 95), ScoreEntry(310.0, 93), ScoreEntry(300.0, 91),
            ScoreEntry(290.0, 89), ScoreEntry(280.0, 87), ScoreEntry(270.0, 85),
            ScoreEntry(260.0, 83), ScoreEntry(250.0, 81), ScoreEntry(240.0, 79),
            ScoreEntry(230.0, 77), ScoreEntry(220.0, 75), ScoreEntry(210.0, 73),
            ScoreEntry(200.0, 71), ScoreEntry(190.0, 70), ScoreEntry(180.0, 67),
            ScoreEntry(170.0, 65), ScoreEntry(160.0, 63), ScoreEntry(150.0, 60),
            ScoreEntry(130.0, 50), ScoreEntry(120.0, 40), ScoreEntry(110.0, 30),
            ScoreEntry(100.0, 20), ScoreEntry(90.0, 10), ScoreEntry(80.0, 0)
        ),
        AgeBracket.AGE_27_31 to listOf(
            ScoreEntry(350.0, 100), ScoreEntry(340.0, 98), ScoreEntry(330.0, 97),
            ScoreEntry(320.0, 95), ScoreEntry(310.0, 93), ScoreEntry(300.0, 91),
            ScoreEntry(290.0, 89), ScoreEntry(280.0, 87), ScoreEntry(270.0, 85),
            ScoreEntry(260.0, 83), ScoreEntry(250.0, 81), ScoreEntry(240.0, 79),
            ScoreEntry(230.0, 77), ScoreEntry(220.0, 75), ScoreEntry(210.0, 73),
            ScoreEntry(200.0, 71), ScoreEntry(190.0, 70), ScoreEntry(180.0, 67),
            ScoreEntry(170.0, 65), ScoreEntry(160.0, 63), ScoreEntry(150.0, 60),
            ScoreEntry(130.0, 50), ScoreEntry(120.0, 40), ScoreEntry(110.0, 30),
            ScoreEntry(100.0, 20), ScoreEntry(90.0, 10), ScoreEntry(80.0, 0)
        ),
        AgeBracket.AGE_32_36 to listOf(
            ScoreEntry(350.0, 100), ScoreEntry(340.0, 99), ScoreEntry(330.0, 97),
            ScoreEntry(320.0, 95), ScoreEntry(310.0, 93), ScoreEntry(300.0, 91),
            ScoreEntry(290.0, 89), ScoreEntry(280.0, 87), ScoreEntry(270.0, 85),
            ScoreEntry(260.0, 83), ScoreEntry(250.0, 81), ScoreEntry(240.0, 79),
            ScoreEntry(230.0, 77), ScoreEntry(220.0, 75), ScoreEntry(210.0, 73),
            ScoreEntry(200.0, 70), ScoreEntry(190.0, 69), ScoreEntry(180.0, 67),
            ScoreEntry(170.0, 65), ScoreEntry(160.0, 63), ScoreEntry(150.0, 61),
            ScoreEntry(140.0, 60), ScoreEntry(130.0, 50), ScoreEntry(120.0, 40),
            ScoreEntry(110.0, 30), ScoreEntry(100.0, 20), ScoreEntry(90.0, 10), ScoreEntry(80.0, 0)
        ),
        AgeBracket.AGE_37_41 to listOf(
            ScoreEntry(350.0, 100), ScoreEntry(340.0, 99), ScoreEntry(330.0, 97),
            ScoreEntry(320.0, 95), ScoreEntry(310.0, 93), ScoreEntry(300.0, 91),
            ScoreEntry(290.0, 89), ScoreEntry(280.0, 87), ScoreEntry(270.0, 85),
            ScoreEntry(260.0, 83), ScoreEntry(250.0, 81), ScoreEntry(240.0, 79),
            ScoreEntry(230.0, 77), ScoreEntry(220.0, 75), ScoreEntry(210.0, 73),
            ScoreEntry(200.0, 70), ScoreEntry(190.0, 69), ScoreEntry(180.0, 67),
            ScoreEntry(170.0, 65), ScoreEntry(160.0, 63), ScoreEntry(150.0, 62),
            ScoreEntry(140.0, 60), ScoreEntry(130.0, 50), ScoreEntry(120.0, 40),
            ScoreEntry(110.0, 30), ScoreEntry(100.0, 20), ScoreEntry(90.0, 10), ScoreEntry(80.0, 0)
        ),
        AgeBracket.AGE_42_46 to listOf(
            ScoreEntry(350.0, 100), ScoreEntry(340.0, 99), ScoreEntry(330.0, 97),
            ScoreEntry(320.0, 95), ScoreEntry(310.0, 93), ScoreEntry(300.0, 92),
            ScoreEntry(290.0, 89), ScoreEntry(280.0, 88), ScoreEntry(270.0, 86),
            ScoreEntry(260.0, 84), ScoreEntry(250.0, 82), ScoreEntry(240.0, 79),
            ScoreEntry(230.0, 76), ScoreEntry(220.0, 76), ScoreEntry(210.0, 73),
            ScoreEntry(200.0, 70), ScoreEntry(190.0, 69), ScoreEntry(180.0, 67),
            ScoreEntry(170.0, 66), ScoreEntry(160.0, 64), ScoreEntry(150.0, 62),
            ScoreEntry(140.0, 60), ScoreEntry(130.0, 50), ScoreEntry(120.0, 40),
            ScoreEntry(110.0, 30), ScoreEntry(100.0, 20), ScoreEntry(90.0, 10), ScoreEntry(80.0, 0)
        ),
        AgeBracket.AGE_47_51 to listOf(
            ScoreEntry(340.0, 100), ScoreEntry(330.0, 99), ScoreEntry(320.0, 97),
            ScoreEntry(310.0, 95), ScoreEntry(300.0, 93), ScoreEntry(290.0, 91),
            ScoreEntry(280.0, 89), ScoreEntry(270.0, 87), ScoreEntry(260.0, 84),
            ScoreEntry(250.0, 82), ScoreEntry(240.0, 79), ScoreEntry(230.0, 76),
            ScoreEntry(220.0, 74), ScoreEntry(210.0, 73), ScoreEntry(200.0, 70),
            ScoreEntry(190.0, 69), ScoreEntry(180.0, 67), ScoreEntry(170.0, 66),
            ScoreEntry(160.0, 64), ScoreEntry(150.0, 62), ScoreEntry(140.0, 60),
            ScoreEntry(130.0, 50), ScoreEntry(120.0, 40), ScoreEntry(110.0, 30),
            ScoreEntry(100.0, 20), ScoreEntry(90.0, 10), ScoreEntry(80.0, 0)
        ),
        AgeBracket.AGE_52_56 to listOf(
            ScoreEntry(330.0, 100), ScoreEntry(320.0, 99), ScoreEntry(310.0, 97),
            ScoreEntry(300.0, 95), ScoreEntry(290.0, 93), ScoreEntry(280.0, 91),
            ScoreEntry(270.0, 89), ScoreEntry(260.0, 87), ScoreEntry(250.0, 84),
            ScoreEntry(240.0, 82), ScoreEntry(230.0, 80), ScoreEntry(220.0, 76),
            ScoreEntry(210.0, 73), ScoreEntry(200.0, 70), ScoreEntry(190.0, 68),
            ScoreEntry(180.0, 67), ScoreEntry(170.0, 65), ScoreEntry(160.0, 63),
            ScoreEntry(150.0, 61), ScoreEntry(140.0, 60), ScoreEntry(130.0, 50),
            ScoreEntry(120.0, 40), ScoreEntry(110.0, 30), ScoreEntry(100.0, 20),
            ScoreEntry(90.0, 10), ScoreEntry(80.0, 0)
        ),
        AgeBracket.AGE_57_61 to listOf(
            ScoreEntry(250.0, 100), ScoreEntry(240.0, 99), ScoreEntry(230.0, 98),
            ScoreEntry(220.0, 97), ScoreEntry(210.0, 95), ScoreEntry(200.0, 94),
            ScoreEntry(190.0, 93), ScoreEntry(180.0, 91), ScoreEntry(170.0, 89),
            ScoreEntry(160.0, 85), ScoreEntry(150.0, 80), ScoreEntry(140.0, 72),
            ScoreEntry(130.0, 50), ScoreEntry(120.0, 40), ScoreEntry(110.0, 30),
            ScoreEntry(100.0, 20), ScoreEntry(90.0, 10), ScoreEntry(80.0, 0)
        ),
        AgeBracket.AGE_62_PLUS to listOf(
            ScoreEntry(230.0, 100), ScoreEntry(220.0, 99), ScoreEntry(210.0, 98),
            ScoreEntry(200.0, 95), ScoreEntry(190.0, 94), ScoreEntry(180.0, 92),
            ScoreEntry(170.0, 89), ScoreEntry(160.0, 82), ScoreEntry(150.0, 75),
            ScoreEntry(140.0, 72), ScoreEntry(130.0, 50), ScoreEntry(120.0, 40),
            ScoreEntry(110.0, 30), ScoreEntry(100.0, 20), ScoreEntry(90.0, 10), ScoreEntry(80.0, 0)
        )
    )

    val deadliftFemale: Map<AgeBracket, List<ScoreEntry>> = mapOf(
        AgeBracket.AGE_17_21 to listOf(
            ScoreEntry(220.0, 100), ScoreEntry(210.0, 98), ScoreEntry(200.0, 97),
            ScoreEntry(190.0, 94), ScoreEntry(180.0, 91), ScoreEntry(170.0, 88),
            ScoreEntry(160.0, 84), ScoreEntry(150.0, 80), ScoreEntry(140.0, 75),
            ScoreEntry(130.0, 68), ScoreEntry(120.0, 60), ScoreEntry(110.0, 50),
            ScoreEntry(100.0, 40), ScoreEntry(90.0, 30), ScoreEntry(80.0, 20),
            ScoreEntry(70.0, 10), ScoreEntry(60.0, 0)
        ),
        AgeBracket.AGE_22_26 to listOf(
            ScoreEntry(230.0, 100), ScoreEntry(220.0, 99), ScoreEntry(210.0, 98),
            ScoreEntry(200.0, 97), ScoreEntry(190.0, 94), ScoreEntry(180.0, 91),
            ScoreEntry(170.0, 88), ScoreEntry(160.0, 84), ScoreEntry(150.0, 79),
            ScoreEntry(140.0, 75), ScoreEntry(130.0, 68), ScoreEntry(120.0, 60),
            ScoreEntry(110.0, 50), ScoreEntry(100.0, 40), ScoreEntry(90.0, 30),
            ScoreEntry(80.0, 20), ScoreEntry(70.0, 10), ScoreEntry(60.0, 0)
        ),
        AgeBracket.AGE_27_31 to listOf(
            ScoreEntry(240.0, 100), ScoreEntry(230.0, 99), ScoreEntry(220.0, 98),
            ScoreEntry(210.0, 96), ScoreEntry(200.0, 95), ScoreEntry(190.0, 93),
            ScoreEntry(180.0, 91), ScoreEntry(170.0, 86), ScoreEntry(160.0, 82),
            ScoreEntry(150.0, 78), ScoreEntry(140.0, 73), ScoreEntry(130.0, 67),
            ScoreEntry(120.0, 60), ScoreEntry(110.0, 50), ScoreEntry(100.0, 40),
            ScoreEntry(90.0, 30), ScoreEntry(80.0, 20), ScoreEntry(70.0, 10), ScoreEntry(60.0, 0)
        ),
        AgeBracket.AGE_32_36 to listOf(
            ScoreEntry(230.0, 100), ScoreEntry(220.0, 99), ScoreEntry(210.0, 97),
            ScoreEntry(200.0, 95), ScoreEntry(190.0, 93), ScoreEntry(180.0, 90),
            ScoreEntry(170.0, 87), ScoreEntry(160.0, 83), ScoreEntry(150.0, 79),
            ScoreEntry(140.0, 74), ScoreEntry(130.0, 68), ScoreEntry(120.0, 60),
            ScoreEntry(110.0, 50), ScoreEntry(100.0, 40), ScoreEntry(90.0, 30),
            ScoreEntry(80.0, 20), ScoreEntry(70.0, 10), ScoreEntry(60.0, 0)
        ),
        AgeBracket.AGE_37_41 to listOf(
            ScoreEntry(220.0, 100), ScoreEntry(210.0, 99), ScoreEntry(200.0, 97),
            ScoreEntry(190.0, 95), ScoreEntry(180.0, 92), ScoreEntry(170.0, 89),
            ScoreEntry(160.0, 85), ScoreEntry(150.0, 80), ScoreEntry(140.0, 75),
            ScoreEntry(130.0, 69), ScoreEntry(120.0, 60), ScoreEntry(110.0, 50),
            ScoreEntry(100.0, 40), ScoreEntry(90.0, 30), ScoreEntry(80.0, 20),
            ScoreEntry(70.0, 10), ScoreEntry(60.0, 0)
        ),
        AgeBracket.AGE_42_46 to listOf(
            ScoreEntry(210.0, 100), ScoreEntry(200.0, 98), ScoreEntry(190.0, 96),
            ScoreEntry(180.0, 92), ScoreEntry(170.0, 88), ScoreEntry(160.0, 84),
            ScoreEntry(150.0, 79), ScoreEntry(140.0, 74), ScoreEntry(130.0, 68),
            ScoreEntry(120.0, 60), ScoreEntry(110.0, 50), ScoreEntry(100.0, 40),
            ScoreEntry(90.0, 30), ScoreEntry(80.0, 20), ScoreEntry(70.0, 10), ScoreEntry(60.0, 0)
        ),
        AgeBracket.AGE_47_51 to listOf(
            ScoreEntry(200.0, 100), ScoreEntry(190.0, 98), ScoreEntry(180.0, 93),
            ScoreEntry(170.0, 89), ScoreEntry(160.0, 84), ScoreEntry(150.0, 79),
            ScoreEntry(140.0, 74), ScoreEntry(130.0, 68), ScoreEntry(120.0, 60),
            ScoreEntry(110.0, 50), ScoreEntry(100.0, 40), ScoreEntry(90.0, 30),
            ScoreEntry(80.0, 20), ScoreEntry(70.0, 10), ScoreEntry(60.0, 0)
        ),
        AgeBracket.AGE_52_56 to listOf(
            ScoreEntry(190.0, 100), ScoreEntry(180.0, 98), ScoreEntry(170.0, 95),
            ScoreEntry(160.0, 91), ScoreEntry(150.0, 85), ScoreEntry(140.0, 79),
            ScoreEntry(130.0, 72), ScoreEntry(120.0, 60), ScoreEntry(110.0, 50),
            ScoreEntry(100.0, 40), ScoreEntry(90.0, 30), ScoreEntry(80.0, 20),
            ScoreEntry(70.0, 10), ScoreEntry(60.0, 0)
        ),
        AgeBracket.AGE_57_61 to listOf(
            ScoreEntry(170.0, 100), ScoreEntry(160.0, 99), ScoreEntry(150.0, 90),
            ScoreEntry(140.0, 80), ScoreEntry(130.0, 72), ScoreEntry(120.0, 60),
            ScoreEntry(110.0, 50), ScoreEntry(100.0, 40), ScoreEntry(90.0, 30),
            ScoreEntry(80.0, 20), ScoreEntry(70.0, 10), ScoreEntry(60.0, 0)
        ),
        AgeBracket.AGE_62_PLUS to listOf(
            ScoreEntry(170.0, 100), ScoreEntry(160.0, 99), ScoreEntry(150.0, 90),
            ScoreEntry(140.0, 80), ScoreEntry(130.0, 72), ScoreEntry(120.0, 60),
            ScoreEntry(110.0, 50), ScoreEntry(100.0, 40), ScoreEntry(90.0, 30),
            ScoreEntry(80.0, 20), ScoreEntry(70.0, 10), ScoreEntry(60.0, 0)
        )
    )

    fun getDeadliftTable(category: ScoringCategory, ageBracket: AgeBracket): List<ScoreEntry> {
        return when (category) {
            ScoringCategory.MALE_COMBAT -> deadliftMaleCombat[ageBracket] ?: deadliftMaleCombat[AgeBracket.AGE_17_21]!!
            ScoringCategory.FEMALE -> deadliftFemale[ageBracket] ?: deadliftFemale[AgeBracket.AGE_17_21]!!
        }
    }
}
