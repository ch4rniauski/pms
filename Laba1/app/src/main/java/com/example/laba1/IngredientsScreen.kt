package com.example.laba1

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun IngredientsScreen(onBack: () -> Unit) {
    val context = LocalContext.current

    Column(Modifier.padding(16.dp)) {

        Text("Необходимо купить", style = MaterialTheme.typography.headlineMedium)

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

        Button(onClick = onBack) {
            Text("Назад")
        }
    }
}
