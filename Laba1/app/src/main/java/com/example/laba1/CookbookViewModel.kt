package com.example.laba1

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf

class CookbookViewModel : ViewModel() {
    val recipes = mutableStateListOf<Recipe>()
    private var nextId = 1

    fun addRecipe(title: String, steps: List<RecipeStep>) {
        recipes.add(Recipe(nextId++, title, steps))
    }

    fun toggleFavorite(id: Int) {
        recipes.replaceAll {
            if (it.id == id) it.copy(isFavorite = !it.isFavorite)
            else it
        }
    }

    fun deleteRecipe(id: Int) {
        recipes.removeAll { it.id == id }
    }
}
