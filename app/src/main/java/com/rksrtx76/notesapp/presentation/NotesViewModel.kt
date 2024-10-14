package com.rksrtx76.notesapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rksrtx76.notesapp.data.model.Note
import com.rksrtx76.notesapp.data.model.ui_states.NotesState
import com.rksrtx76.notesapp.domain.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val notesRepository: NotesRepository
): ViewModel(){

    private val _notesState = MutableStateFlow(NotesState())
    val notesState = _notesState.asStateFlow()

    fun getNotes(userId : String){
        viewModelScope.launch {
            _notesState.update {
                it.copy(isLoading = true)
            }
            try {
                val notes = notesRepository.getNotes(userId).getOrThrow()
                _notesState.update {
                    it.copy(
                        isLoading = false,
                        notes = notes
                    )
                }
            }catch (e : Exception){
                _notesState.update {
                    it.copy(isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun addNote(note : Note){
        viewModelScope.launch {
            _notesState.update {
                it.copy(isLoading = true)
            }
            // add note
            try {
//                val newNote = note.copy(userId = userId)
                notesRepository.addNote(note)
                _notesState.update {
                    it.copy(isLoading = false)
                }
            }catch(e: Exception){
                _notesState.update {
                    it.copy(isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun updateNote(note : Note){
        viewModelScope.launch {
            _notesState.update {
                it.copy(isLoading = true)
            }
            try {
                notesRepository.updateNote(note)
                _notesState.update {
                    it.copy(
                        isLoading = false
                    )
                }
            }catch (e: Exception){
                _notesState.update {
                    it.copy(isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun deleteNote(documentId: String){
        viewModelScope.launch {
            _notesState.update {
                it.copy(isLoading = true)
            }
            try {
                notesRepository.deleteNote(documentId)
                _notesState.update {
                    it.copy(
                        isLoading = false,
                        notes = it.notes.filterNot {
                            it.documentId == documentId
                        }
                    )
                }
            }catch (e: Exception){
                _notesState.update {
                    it.copy(isLoading = false,
                        error = e.message
                    )
                }
            }

        }
    }


    // reset error
    fun clearError(){
        _notesState.update {
            it.copy(error = null)
        }
    }
}