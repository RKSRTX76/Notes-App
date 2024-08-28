package com.rksrtx76.notesapp.presentation

import androidx.lifecycle.ViewModel
import com.rksrtx76.notesapp.data.model.ui_states.SignInResult
import com.rksrtx76.notesapp.data.model.ui_states.SignInState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(

) : ViewModel(){

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun onSignIn(result : SignInResult){
        _state.update {
            it.copy(
                isSignInSuccess = result.data != null,
                signInError = result.errorMessage
            )
        }
    }

    fun resetState(){
        _state.update {
            SignInState()
        }
    }
}