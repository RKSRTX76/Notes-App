package com.rksrtx76.notesapp.data.model.ui_states

data class SignInResult(
    val data  : UserData?,
    val errorMessage: String?
)

data class UserData(
    val userId : String,
    val userName : String?,
    val profilePictureUrl : String?
)
