package com.example.laba1

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

class CookingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val title = intent.getStringExtra("title") ?: ""
        val steps = intent.getSerializableExtra("steps") as ArrayList<RecipeStep>

        setContent {
            CookingScreen(title, steps, this)
        }
    }
}

@Composable
fun CookingScreen(title: String, steps: List<RecipeStep>, activity: Activity) {
    var step by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(steps[0].duration) }
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
                activity,
                "Возврат на главный экран",
                "Вы вернулись в кулинарную книгу"
            )
            activity.finish()
        }) {
            Text("Назад")
        }
    }
}
