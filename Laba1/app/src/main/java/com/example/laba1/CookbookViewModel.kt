package com.example.laba1

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CookbookViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RecipeRepository

    val recipes = mutableStateListOf<Recipe>()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = RecipeRepository(db.recipeDao())

        viewModelScope.launch {
            repository.recipes.collectLatest { list ->
                recipes.clear()
                recipes.addAll(list)
            }
        }
    }

    fun addRecipe(title: String, steps: List<RecipeStep>) {
        viewModelScope.launch {
            repository.addRecipe(
                Recipe(
                    id = 0,
                    title = title,
                    steps = steps,
                    isFavorite = false
                )
            )
        }
    }

    fun toggleFavorite(id: Int) {
        val recipe = recipes.find { it.id == id } ?: return
        viewModelScope.launch {
            repository.toggleFavorite(recipe)
        }
    }

    fun deleteRecipe(id: Int) {
        val recipe = recipes.find { it.id == id } ?: return
        viewModelScope.launch {
            repository.deleteRecipe(recipe)
        }
    }
}
