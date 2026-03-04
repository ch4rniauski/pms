package com.example.laba1

import java.io.Serializable

data class RecipeStep(
    val title: String,
    val duration: Int
) : Serializable
