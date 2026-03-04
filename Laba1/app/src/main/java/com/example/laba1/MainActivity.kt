package com.example.laba1

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.launch

const val CHANNEL_ID = "cookbook_channel"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }

        createNotificationChannel()

        setContent {
            CookbookApp()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Cookbook Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )

            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }
}

@Composable
fun CookbookApp() {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var text by remember { mutableStateOf("") }
    var recipes by remember { mutableStateOf(listOf<Recipe>()) }
    var nextId by remember { mutableIntStateOf(1) }

    var newSteps by remember { mutableStateOf(listOf<RecipeStep>()) }
    var stepTitle by remember { mutableStateOf("") }
    var stepDuration by remember { mutableStateOf("") }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Кулинарная книга",
                style = MaterialTheme.typography.headlineMedium)

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

            LazyColumn {
                items(newSteps) { s ->
                    Text("• ${s.title} (${s.duration} сек)")
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (text.isNotBlank() && newSteps.isNotEmpty()) {
                        recipes = recipes + Recipe(nextId++, text, newSteps)
                        text = ""
                        newSteps = emptyList()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Создать рецепт")
            }

            Spacer(Modifier.height(16.dp))

            LazyColumn {
                items(recipes) { recipe ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                val intent = Intent(context, CookingActivity::class.java)
                                intent.putExtra("title", recipe.title)
                                intent.putExtra("steps", ArrayList(recipe.steps))
                                context.startActivity(intent)

                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "Переход к рецепту ${recipe.title}"
                                    )
                                }

                                showNotification(
                                    context,
                                    "Открыт рецепт",
                                    recipe.title
                                )
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(recipe.title, Modifier.weight(1f))

                        IconButton(onClick = {
                            recipes = recipes.map {
                                if (it.id == recipe.id) {
                                    it.copy(isFavorite = !it.isFavorite)
                                }
                                else it
                            }
                        }) {
                            Text(if (recipe.isFavorite) "★" else "☆")
                        }

                        IconButton(onClick = {
                            recipes = recipes.filter { it.id != recipe.id }
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

                    context.startActivity(
                        Intent(context, IngredientsActivity::class.java)
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Список ингредиентов")
            }
        }
    }
}

fun showNotification(context: Context, title: String, text: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
    }

    val intent = Intent(context, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notification = androidx.core.app.NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle(title)
        .setContentText(text)
        .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .build()

    NotificationManagerCompat.from(context)
        .notify(System.currentTimeMillis().toInt(), notification)
}
