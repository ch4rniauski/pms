package com.example.laba1

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (isUserLoggedIn()) "home" else "login"
    ) {
        composable("login") {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

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
                },
                onSignOut = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
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

fun isUserLoggedIn(): Boolean {
    return Firebase.auth.currentUser != null
}
