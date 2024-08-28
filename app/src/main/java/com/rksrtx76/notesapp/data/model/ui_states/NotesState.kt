package com.rksrtx76.notesapp.data.model.ui_states

import com.rksrtx76.notesapp.data.model.Note

data class NotesState(
    val notes : List<Note> = emptyList(),
    val isLoading : Boolean = false,
    val error : String? = null
)
