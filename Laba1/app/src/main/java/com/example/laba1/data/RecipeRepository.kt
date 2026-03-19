package com.example.laba1.data

import com.example.laba1.Recipe
import com.example.laba1.RecipeStep
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RecipeRepository(
    private val dao: RecipeDao
) {

    fun getRecipes(userId: String): Flow<List<Recipe>> =
        dao.getAllRecipesWithSteps(userId).map { list ->
            list.map { it.toDomain() }
        }

    suspend fun addRecipe(recipe: Recipe, userId: String) {
        val recipeId = dao.insertRecipe(
            RecipeEntity(
                userId = userId,
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

    suspend fun toggleFavorite(recipe: Recipe, userId: String) {
        dao.updateRecipe(
            RecipeEntity(
                id = recipe.id,
                userId = userId,
                title = recipe.title,
                isFavorite = !recipe.isFavorite
            )
        )
    }

    suspend fun deleteRecipe(recipe: Recipe, userId: String) {
        dao.deleteStepsForRecipe(recipe.id)
        dao.deleteRecipe(
            RecipeEntity(
                id = recipe.id,
                userId = userId,
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
