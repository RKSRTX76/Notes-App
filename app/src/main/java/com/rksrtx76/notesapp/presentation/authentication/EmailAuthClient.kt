package com.rksrtx76.notesapp.presentation.authentication

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.rksrtx76.notesapp.data.model.ui_states.SignInResult
import com.rksrtx76.notesapp.data.model.ui_states.UserData
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

class EmailAuthClient(
    private val context : Context
) {
    private val auth = Firebase.auth

    suspend fun signUpWithEmailAndPassword(displayName : String ,email : String, password : String) : SignInResult{
        return try{
            val user = auth.createUserWithEmailAndPassword(email,password).await().user
            // update user name
            user?.updateProfile(userProfileChangeRequest {
                this.displayName = displayName
            })?.await()

            SignInResult(
                data = user?.run {
                    UserData(
                        userId = uid,
                        userName = displayName,
                        profilePictureUrl = photoUrl?.toString()
                    )
                },
                errorMessage = null
            )
        }catch (e : Exception){
            e.printStackTrace()
            if(e is CancellationException) throw e
            SignInResult(
                data = null,
                errorMessage = e.message
            )
        }
    }

    suspend fun signInWithEmailAndPassword(email : String, password : String) : SignInResult{
        return try{
            val user = auth.signInWithEmailAndPassword(email,password).await().user
            SignInResult(
                data = user?.run {
                    UserData(
                        userId = uid,
                        userName = displayName,
                        profilePictureUrl = photoUrl?.toString()
                    )
                },
                errorMessage = null
            )
        }catch (e : Exception){
            e.printStackTrace()
            if(e is CancellationException) throw e
            SignInResult(
                data = null,
                errorMessage = e.message
            )
        }
    }

}