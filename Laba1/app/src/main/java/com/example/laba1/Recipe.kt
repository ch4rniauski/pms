package com.example.laba1

data class Recipe(
    val id: Int,
    val title: String,
    val steps: List<RecipeStep> = emptyList(),
    var isFavorite: Boolean = false
)
