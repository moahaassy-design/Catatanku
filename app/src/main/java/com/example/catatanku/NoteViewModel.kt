package com.example.catatanku

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import androidx.lifecycle.asLiveData

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NoteRepository
    private val _allNotes = MutableLiveData<List<Note>>()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedCategory = MutableStateFlow("Semua") // Default category
    val selectedCategory: StateFlow<String> = _selectedCategory

    val notes: LiveData<List<Note>> = combine(
        _allNotes.asFlow(),
        _searchQuery,
        _selectedCategory
    ) { allNotes, query, category ->
        allNotes.filter {
            (query.isBlank() || it.title.contains(query, ignoreCase = true) ||
             it.content.contains(query, ignoreCase = true) ||
             it.category.contains(query, ignoreCase = true))
            &&
            (category == "Semua" || it.category == category)
        }
    }.asLiveData(viewModelScope.coroutineContext)

    init {
        repository = NoteRepository(application)
        loadNotes()
    }

    fun loadNotes() {
        viewModelScope.launch(Dispatchers.IO) {
            _allNotes.postValue(repository.getNotes())
        }
    }

    fun addNote(note: Note) {
        val currentNotes = _allNotes.value.orEmpty().toMutableList()
        currentNotes.add(0, note) // Add new note to the top
        saveAndLoadNotes(currentNotes)
    }

    fun updateNote(updatedNote: Note) {
        val currentNotes = _allNotes.value.orEmpty().toMutableList()
        val index = currentNotes.indexOfFirst { it.id == updatedNote.id }
        if (index != -1) {
            currentNotes[index] = updatedNote
            saveAndLoadNotes(currentNotes)
        }
    }

    fun deleteNote(note: Note) {
        val currentNotes = _allNotes.value.orEmpty().toMutableList()
        currentNotes.remove(note)
        saveAndLoadNotes(currentNotes)
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
    }

    private fun saveAndLoadNotes(notes: List<Note>) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveNotes(notes)
            _allNotes.postValue(notes) // Update LiveData after saving
        }
    }
}

