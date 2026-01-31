package com.aftcalculator.models

import kotlinx.serialization.Serializable

@Serializable
enum class AftEvent(val displayName: String, val unit: String) {
    DEADLIFT("3-Rep Max Deadlift", "lbs"),
    PUSH_UP("Hand-Release Push-Up", "reps"),
    SPRINT_DRAG_CARRY("Sprint-Drag-Carry", "time"),
    PLANK("Plank", "time"),
    TWO_MILE_RUN("2-Mile Run", "time")
}

@Serializable
enum class MosCategory(val displayName: String, val minimumPerEvent: Int, val minimumTotal: Int) {
    COMBAT("Combat MOS", 60, 350),
    COMBAT_ENABLING("Combat-Enabling MOS", 0, 300)
}

@Serializable
enum class Gender {
    MALE,
    FEMALE
}

@Serializable
enum class AgeBracket(val minAge: Int, val maxAge: Int, val displayName: String) {
    AGE_17_21(17, 21, "17-21"),
    AGE_22_26(22, 26, "22-26"),
    AGE_27_31(27, 31, "27-31"),
    AGE_32_36(32, 36, "32-36"),
    AGE_37_41(37, 41, "37-41"),
    AGE_42_46(42, 46, "42-46"),
    AGE_47_51(47, 51, "47-51"),
    AGE_52_56(52, 56, "52-56"),
    AGE_57_61(57, 61, "57-61"),
    AGE_62_PLUS(62, 999, "62+");

    companion object {
        fun fromAge(age: Int): AgeBracket {
            return entries.first { age in it.minAge..it.maxAge }
        }
    }
}
