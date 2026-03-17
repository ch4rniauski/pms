package com.example.laba1.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.laba1.AuthUiState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// VIEWMODEL ДЛЯ УПРАВЛЕНИЯ АУТЕНТИФИКАЦИЕЙ
/**
 * AuthViewModel - ViewModel для управления состоянием аутентификации и
 * пользовательскими данными
 */
class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Loading)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        val user = auth.currentUser
        _currentUser.value = user
        if (user != null) {
            _uiState.value = AuthUiState.Success(user)
        } else {
            _uiState.value = AuthUiState.NotSignedIn
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                _currentUser.value = result.user
                _uiState.value = AuthUiState.Success(result.user!!)
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Registration failed")
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                _currentUser.value = result.user
                _uiState.value = AuthUiState.Success(result.user!!)
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun signOut() {
        auth.signOut()
        _currentUser.value = null
        _uiState.value = AuthUiState.NotSignedIn
    }
}
