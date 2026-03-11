package com.example.laba1

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RecipeRepository(
    private val dao: RecipeDao
) {

    val recipes: Flow<List<Recipe>> =
        dao.getAllRecipesWithSteps().map { list ->
            list.map { it.toDomain() }
        }

    suspend fun addRecipe(recipe: Recipe) {
        val recipeId = dao.insertRecipe(
            RecipeEntity(
                title = recipe.title,
                isFavorite = recipe.isFavorite
            )
        ).toInt()

        if (recipe.steps.isNotEmpty()) {
            val stepsEntities = recipe.steps.map {
                RecipeStepEntity(
                    recipeId = recipeId,
                    title = it.title,
                    duration = it.duration
                )
            }
            dao.insertSteps(stepsEntities)
        }
    }

    suspend fun toggleFavorite(recipe: Recipe) {
        dao.updateRecipe(
            RecipeEntity(
                id = recipe.id,
                title = recipe.title,
                isFavorite = !recipe.isFavorite
            )
        )
    }

    suspend fun deleteRecipe(recipe: Recipe) {
        dao.deleteStepsForRecipe(recipe.id)
        dao.deleteRecipe(
            RecipeEntity(
                id = recipe.id,
                title = recipe.title,
                isFavorite = recipe.isFavorite
            )
        )
    }
}

private fun RecipeWithSteps.toDomain(): Recipe =
    Recipe(
        id = recipe.id,
        title = recipe.title,
        steps = steps.map { RecipeStep(it.title, it.duration) },
        isFavorite = recipe.isFavorite
    )