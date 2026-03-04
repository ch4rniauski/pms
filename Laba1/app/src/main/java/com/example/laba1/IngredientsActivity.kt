package com.example.laba1

import androidx.activity.ComponentActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class IngredientsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            IngredientsScreen(this)
        }
    }
}

@Composable
fun IngredientsScreen(context: android.content.Context) {
    Column(Modifier.padding(16.dp)) {
        Text("Необходимо купить",
            style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(16.dp))

        Text("• Молоко")
        Text("• Яйца")
        Text("• Мука")

        Spacer(Modifier.height(24.dp))

        Button(onClick = {
            showNotification(
                context,
                "Пора купить ингредиенты",
                "Не забудьте приобрести продукты!"
            )
        }) {
            Text("Отправить уведомление")
        }

        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            showNotification(
                context,
                "Возврат на главный экран",
                "Вы вернулись в кулинарную книгу"
            )
            (context as ComponentActivity).finish()
        }) {
            Text("Назад")
        }
    }
}
