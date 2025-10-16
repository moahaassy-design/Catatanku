package com.example.catatanku

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class NoteRepository(private val context: Context) {

    private val PREFS_NAME = "catatanku_prefs"
    private val NOTES_KEY = "notes_list"
    private val gson = Gson()

    fun saveNotes(notes: List<Note>) {
        val json = gson.toJson(notes)
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(NOTES_KEY, json)
            .apply()
    }

    fun getNotes(): List<Note> {
        val json = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(NOTES_KEY, null)
        return if (json != null) {
            gson.fromJson(json, object : TypeToken<List<Note>>() {}.type)
        } else {
            emptyList()
        }
    }
}

