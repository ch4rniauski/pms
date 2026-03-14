package com.example.laba1.data

import androidx.room.Embedded
import androidx.room.Relation

data class RecipeWithSteps(
    @Embedded
    val recipe: RecipeEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId"
    )
    val steps: List<RecipeStepEntity>
)
