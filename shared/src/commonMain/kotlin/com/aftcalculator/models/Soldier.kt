package com.aftcalculator.models

import kotlinx.serialization.Serializable

@Serializable
data class Soldier(
    val age: Int,
    val gender: Gender,
    val mosCategory: MosCategory
) {
    val ageBracket: AgeBracket get() = AgeBracket.fromAge(age)
}
