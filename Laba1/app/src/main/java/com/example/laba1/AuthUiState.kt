package com.example.laba1

import com.google.firebase.auth.FirebaseUser

sealed class AuthUiState {
    object Loading : AuthUiState()
    object NotSignedIn : AuthUiState()
    data class Success(val user: FirebaseUser) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}