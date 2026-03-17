package com.example.laba1

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

// 5. FCM Service для обработки входящих уведомлений
class MyFCMService : FirebaseMessagingService() {
    
    override fun onMessageReceived(message: RemoteMessage) {
        Log.d("CookbookFCM", "Получено сообщение от: ${message.from}")

        // 1. Обработка уведомления (если отправлено через Firebase Console)
        message.notification?.let {
            Log.d("CookbookFCM", "Уведомление: ${it.title} - ${it.body}")
            showNotification(this, it.title ?: "Новый рецепт", it.body ?: "")
        }

        // 2. Обработка данных (если отправлено через Cloud Functions)
        if (message.data.isNotEmpty()) {
            Log.d("CookbookFCM", "Данные: ${message.data}")
            val recipeTitle = message.data["recipeTitle"]
            if (recipeTitle != null) {
                showNotification(
                    this, 
                    "Новый рецепт в приложении!", 
                    "Кто-то добавил: $recipeTitle"
                )
            }
        }
    }

    override fun onNewToken(token: String) {
        Log.d("CookbookFCM", "Новый токен FCM: $token")
        // Здесь можно отправить токен на сервер, если это необходимо
    }
}
