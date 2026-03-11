package com.example.laba1

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            CookbookScreen(
                onOpenRecipe = { recipe ->
                    navController.navigate("cooking/${recipe.title}") {
                        launchSingleTop = true
                    }

                    navController.getBackStackEntry("cooking/${recipe.title}")
                        .savedStateHandle["steps"] = recipe.steps
                },
                onOpenIngredients = {
                    navController.navigate("ingredients")
                }
            )
        }

        composable("cooking/{title}") { entry ->
            val title = entry.arguments?.getString("title") ?: ""

            val steps = entry.savedStateHandle
                .get<List<RecipeStep>>("steps")
                ?: emptyList()

            CookingScreen(
                title = title,
                steps = steps,
                onBack = { navController.popBackStack() }
            )
        }

        composable("ingredients") {
            IngredientsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
