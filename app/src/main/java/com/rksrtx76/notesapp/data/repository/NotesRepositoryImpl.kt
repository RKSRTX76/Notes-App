package com.rksrtx76.notesapp.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObjects
import com.rksrtx76.notesapp.data.model.Note
import com.rksrtx76.notesapp.domain.repository.NotesRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotesRepositoryImpl @Inject constructor(
    private val firestore : FirebaseFirestore
) : NotesRepository {

    private val notesCollection = firestore.collection("notes")

    override suspend fun addNote(note: Note) {
        try{
            val newDocument = notesCollection.document()
            note.documentId = newDocument.id
            // save to fire store
            newDocument.set(note).await()

        }catch (e: Exception){
            throw e
        }
    }

    override suspend fun updateNote(note: Note) {
        try{
            notesCollection.document(note.documentId).set(note).await()
            Result.success(Unit)
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun deleteNote(documentId: String) {
        try{
            notesCollection.document(documentId).delete().await()
            Result.success(Unit)
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun getNotes(userId: String): Result<List<Note>> {
       return  try{
            val query = notesCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()
            // convert
            val notesList = query.documents.map { document ->
                document.toObject(Note::class.java)?.copy(documentId = document.id)
            }.filterNotNull()

            Result.success(notesList)

        }catch (e: Exception){
            Result.failure(e)
        }

    }

}