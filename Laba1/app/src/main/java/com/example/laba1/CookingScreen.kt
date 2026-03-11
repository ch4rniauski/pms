package com.example.laba1

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun CookingScreen(
    title: String,
    steps: List<RecipeStep>,
    onBack: () -> Unit
) {
    if (steps.isEmpty()) {
        Text("Ошибка: нет шагов рецепта")
        return
    }

    val context = LocalContext.current

    var step by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(steps.first().duration) }
    var running by remember { mutableStateOf(false) }

    LaunchedEffect(running, timeLeft) {
        if (running && timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
    }

    Column(Modifier.padding(16.dp)) {
        Text(title, style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(16.dp))

        Text(steps[step].title)
        Text("Осталось: $timeLeft сек")

        Spacer(Modifier.height(16.dp))

        Button(onClick = { running = true }) {
            Text("Старт")
        }

        Spacer(Modifier.height(8.dp))

        Button(onClick = {
            if (step < steps.lastIndex) {
                step++
                timeLeft = steps[step].duration
                running = false
            }
        }) {
            Text("Следующий шаг")
        }

        Spacer(Modifier.height(24.dp))

        Button(onClick = {
            showNotification(
                context,
                "Возврат на главный экран",
                "Вы вернулись в кулинарную книгу"
            )
            onBack()
        }) {
            Text("Назад")
        }
    }
}
