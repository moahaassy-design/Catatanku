package com.example.catatanku

data class Note(
    val id: String = java.util.UUID.randomUUID().toString(),
    var title: String,
    var content: String,
    var category: String = "Umum",
    val timestamp: Long = System.currentTimeMillis()
)

