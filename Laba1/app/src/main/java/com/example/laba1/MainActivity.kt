package com.example.laba1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show()

        setContent {
            CookbookApp()
        }
    }

    override fun onStart() {
        super.onStart()
        Toast.makeText(this, "onStart", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show()
    }

    override fun onPause() {
        super.onPause()
        Toast.makeText(this, "onPause", Toast.LENGTH_SHORT).show()
    }

    override fun onStop() {
        super.onStop()
        Toast.makeText(this, "onStop", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun CookbookApp() {
    var recipeText by remember { mutableStateOf("") }
    var recipes by remember { mutableStateOf(listOf<Recipe>()) }
    var nextId by remember { mutableIntStateOf(1) }

    val configuration = LocalConfiguration.current
    val isLandscape =
        configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            AddRecipeSection(
                recipeText,
                onTextChange = { recipeText = it },
                onAdd = {
                    if (recipeText.isNotBlank()) {
                        recipes = recipes + Recipe(nextId++, recipeText)
                        recipeText = ""
                    }
                },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            RecipeList(
                recipes,
                onDelete = { recipe ->
                    recipes = recipes.filter { it.id != recipe.id }
                },
                onFavorite = { recipe ->
                    recipes = recipes.map {
                        if (it.id == recipe.id)
                            it.copy(isFavorite = !it.isFavorite)
                        else it
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }

    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            AddRecipeSection(
                recipeText,
                onTextChange = { recipeText = it },
                onAdd = {
                    if (recipeText.isNotBlank()) {
                        recipes = recipes + Recipe(nextId++, recipeText)
                        recipeText = ""
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            RecipeList(
                recipes,
                onDelete = { recipe ->
                    recipes = recipes.filter { it.id != recipe.id }
                },
                onFavorite = { recipe ->
                    recipes = recipes.map {
                        if (it.id == recipe.id)
                            it.copy(isFavorite = !it.isFavorite)
                        else it
                    }
                }
            )
        }
    }
}

@Composable
fun AddRecipeSection(
    text: String,
    onTextChange: (String) -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(text = "Кулинарная книга", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            label = { Text("Название рецепта") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onAdd,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Добавить рецепт")
        }
    }
}

@Composable
fun RecipeList(
    recipes: List<Recipe>,
    onDelete: (Recipe) -> Unit,
    onFavorite: (Recipe) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(recipes) { recipe ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = recipe.title,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = { onFavorite(recipe) }) {
                    Text(
                        text = if (recipe.isFavorite) "★" else "☆"
                    )
                }

                IconButton(onClick = { onDelete(recipe) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Удалить"
                    )
                }
            }
        }
    }
}
