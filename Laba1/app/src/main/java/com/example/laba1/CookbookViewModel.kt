package com.example.laba1

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.laba1.data.AppDatabase
import com.example.laba1.data.RecipeRepository
import com.google.firebase.Firebase
import com.google.firebase.functions.functions
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CookbookViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: RecipeRepository
    private val functions = Firebase.functions

    val recipes = mutableStateListOf<Recipe>()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = RecipeRepository(db.recipeDao())

        viewModelScope.launch {
            repository.recipes.collectLatest { list ->
                recipes.clear()
                recipes.addAll(list)
            }
        }
        
        FirebaseMessaging.getInstance().subscribeToTopic("cooking")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) Log.d("Cookbook", "Subscribed to 'cooking' topic")
            }
    }

    fun addRecipe(title: String, steps: List<RecipeStep>) {
        viewModelScope.launch {
            try {
                repository.addRecipe(
                    Recipe(id = 0, title = title, steps = steps, isFavorite = false)
                )
                
                // 1. Показываем ЛОКАЛЬНОЕ уведомление сразу (для текущего пользователя)
                showNotification(getApplication(), "Успех!", "Рецепт '$title' добавлен в вашу книгу")
                
                // 2. Вызываем Cloud Function для уведомления ОСТАЛЬНЫХ
                notifyNewRecipe(title)
                
                withContext(Dispatchers.Main) {
                    Toast.makeText(getApplication(), "Рецепт сохранен", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("Cookbook", "Save error", e)
            }
        }
    }

    private fun notifyNewRecipe(recipeTitle: String) {
        viewModelScope.launch {
            try {
                val data = hashMapOf("recipeTitle" to recipeTitle)
                functions.getHttpsCallable("sendNewRecipeNotification").call(data).await()
                Log.d("Cookbook", "Cloud Function success")
            } catch (e: Exception) {
                Log.e("Cookbook", "Cloud Function failed: ${e.message}. Проверьте, развернута ли функция в Firebase Console.")
            }
        }
    }

    fun toggleFavorite(id: Int) {
        val recipe = recipes.find { it.id == id } ?: return
        viewModelScope.launch { repository.toggleFavorite(recipe) }
    }

    fun deleteRecipe(id: Int) {
        val recipe = recipes.find { it.id == id } ?: return
        viewModelScope.launch { repository.deleteRecipe(recipe) }
    }
}
