package com.pegasus.fchat.ui.screens.login

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.pegasus.fchat.model.AuthEffect
import com.pegasus.fchat.model.AuthIntent
import com.pegasus.fchat.model.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _state = mutableStateOf(AuthState())
    val state: State<AuthState> = _state

    private val _effect = MutableSharedFlow<AuthEffect>()
    val effect = _effect.asSharedFlow()

    fun onIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.EnterEmail -> {
                _state.value = _state.value.copy(email = intent.value)
            }

            is AuthIntent.EnterPassword -> {
                _state.value = _state.value.copy(password = intent.value)
            }

            is AuthIntent.Submit -> {
                loginOrSignup()
            }
            is AuthIntent.Login -> {
                _state.value =  _state.value.copy(isLoggedIn = true)
            }

        }
    }

    private fun loginOrSignup() {
        val email = state.value.email
        val password = state.value.password

        if (email.isBlank() || password.isBlank()) {
            _state.value = _state.value.copy(isLoading = false)
            emitEffect(AuthEffect.ShowToast("Email and password required"))
            return
        }

        _state.value = _state.value.copy(isLoading = true)

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _state.value = _state.value.copy(isLoading = false)
                emitEffect(AuthEffect.NavigateToChat)
            }
            .addOnFailureListener {
                // Try sign up
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        _state.value = _state.value.copy(isLoading = false)
                        emitEffect(AuthEffect.NavigateToChat)
                    }
                    .addOnFailureListener { e ->
                        _state.value = _state.value.copy(isLoading = false, error = e.message)
                        emitEffect(AuthEffect.ShowToast(e.message ?: "Authentication failed"))
                    }
            }
    }

    private fun emitEffect(effect: AuthEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}

