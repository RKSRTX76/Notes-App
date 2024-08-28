package com.rksrtx76.notesapp.data.model

data class Note(
    val userId : String = "",
    var documentId : String = "",
    val title : String = "",
    val description : String = "",
)
