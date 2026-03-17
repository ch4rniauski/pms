package com.example.laba1

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.laba1.auth.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CookbookScreen(
    viewModel: CookbookViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel(),
    onOpenRecipe: (Recipe) -> Unit,
    onOpenIngredients: () -> Unit,
    onSignOut: () -> Unit
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var text by remember { mutableStateOf("") }
    val recipes = viewModel.recipes

    var newSteps by remember { mutableStateOf(listOf<RecipeStep>()) }
    var stepTitle by remember { mutableStateOf("") }
    var stepDuration by remember { mutableStateOf("") }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Кулинарная книга") },
                actions = {
                    IconButton(onClick = {
                        authViewModel.signOut()
                        onSignOut()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Sign Out")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Название рецепта") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Text("Добавить шаг")

            OutlinedTextField(
                value = stepTitle,
                onValueChange = { stepTitle = it },
                label = { Text("Название шага") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = stepDuration,
                onValueChange = { stepDuration = it },
                label = { Text("Длительность (сек)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (stepTitle.isNotBlank() && stepDuration.toIntOrNull() != null) {
                        newSteps = newSteps + RecipeStep(stepTitle, stepDuration.toInt())
                        stepTitle = ""
                        stepDuration = ""
                    }
                }
            ) {
                Text("Добавить шаг")
            }

            Spacer(Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.heightIn(max = 150.dp)) {
                items(newSteps) { s ->
                    Text("• ${s.title} (${s.duration} сек)")
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (text.isNotBlank() && newSteps.isNotEmpty()) {
                        viewModel.addRecipe(text, newSteps)
                        text = ""
                        newSteps = emptyList()
                        scope.launch {
                            snackbarHostState.showSnackbar("Рецепт добавлен")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Создать рецепт")
            }

            Spacer(Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(recipes) { recipe ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                onOpenRecipe(recipe)

                                showNotification(
                                    context,
                                    "Открыт рецепт",
                                    recipe.title
                                )

                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "Переход к рецепту ${recipe.title}"
                                    )
                                }
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(recipe.title, Modifier.weight(1f))

                        IconButton(onClick = {
                            viewModel.toggleFavorite(recipe.id)
                        }) {
                            Text(if (recipe.isFavorite) "★" else "☆")
                        }

                        IconButton(onClick = {
                            viewModel.deleteRecipe(recipe.id)
                            scope.launch {
                                snackbarHostState.showSnackbar("Рецепт удалён")
                            }
                        }) {
                            Icon(Icons.Default.Delete, null)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    showNotification(
                        context,
                        "Открыт список ингредиентов",
                        "Вы перешли к списку покупок"
                    )
                    onOpenIngredients()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Список ингредиентов")
            }
        }
    }
}
