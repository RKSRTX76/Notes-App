package com.rksrtx76.notesapp.domain.repository

import com.rksrtx76.notesapp.data.model.Note

interface NotesRepository {
    suspend fun addNote(note : Note)
    suspend fun updateNote(note : Note)
    suspend fun deleteNote(documentId : String)
    suspend fun getNotes(userId : String) : Result<List<Note>>
}