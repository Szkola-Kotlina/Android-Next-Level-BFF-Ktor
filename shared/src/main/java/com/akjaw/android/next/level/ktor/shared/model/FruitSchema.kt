package com.akjaw.android.next.level.ktor.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class FruitSchema(
    val name: String = "",
    val id: Int = -1,
    val nutritions: NutritionsSchema = NutritionsSchema()
)

@Serializable
data class NutritionsSchema(
    val carbohydrates: Float = 0.0f,
    val protein: Float = 0.0f,
    val fat: Float = 0.0f,
    val calories: Float = 0.0f,
    val sugar: Float = 0.0f,
)