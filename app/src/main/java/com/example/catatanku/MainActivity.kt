package com.example.catatanku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import android.app.Application

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CatatankuApp(application = application)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatatankuApp(application: Application) {
    val noteViewModel: NoteViewModel = viewModel(factory = NoteViewModelFactory(application = application))
    val notes by noteViewModel.notes.observeAsState(emptyList())
    var showAddEditDialog by remember { mutableStateOf(false) }
    var selectedNote by remember { mutableStateOf<Note?>(null) }
    val searchQuery by noteViewModel.searchQuery.collectAsState()
    val selectedCategory by noteViewModel.selectedCategory.collectAsState()
    val allCategories = remember { mutableStateListOf<String>("Semua", "Pribadi", "Pekerjaan", "Belajar") }

    // Update allCategories based on existing notes
    LaunchedEffect(notes) {
        val categoriesFromNotes = notes.map { it.category }.distinct().filter { it.isNotBlank() }
        val newCategories = (categoriesFromNotes - allCategories.toSet()).sorted()
        allCategories.addAll(newCategories)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catatanku") },
                actions = {
                    // Category filter dropdown
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        TextButton(onClick = { expanded = true }) {
                            Text(selectedCategory)
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            allCategories.forEach { category ->
                                DropdownMenuItem(onClick = {
                                    noteViewModel.onCategorySelected(category)
                                    expanded = false
                                }) { Text(category) }
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddEditDialog = true; selectedNote = null }) {
                Icon(Icons.Filled.Add, "Add new note")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { noteViewModel.onSearchQueryChanged(it) },
                label = { Text("Cari Catatan") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search Icon") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { noteViewModel.onSearchQueryChanged("") }) {
                            Icon(Icons.Filled.Close, contentDescription = "Clear Search")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(notes) {
                    note ->
                    NoteItem(
                        note = note,
                        onEditClick = { selectedNote = it; showAddEditDialog = true },
                        onDeleteClick = { noteViewModel.deleteNote(it) }
                    )
                }
            }
        }

        if (showAddEditDialog) {
            AddEditNoteDialog(
                note = selectedNote,
                onDismiss = { showAddEditDialog = false },
                onConfirm = {
                    if (selectedNote == null) {
                        noteViewModel.addNote(it)
                    } else {
                        noteViewModel.updateNote(it)
                    }
                    showAddEditDialog = false
                }
            )
        }
    }
}

@Composable
fun NoteItem(note: Note, onEditClick: (Note) -> Unit, onDeleteClick: (Note) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onEditClick(note) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = note.title, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = note.content, style = MaterialTheme.typography.bodyMedium)
            Text(text = "Kategori: ${note.category}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = { onEditClick(note) }) {
                    Icon(Icons.Filled.Edit, "Edit note")
                }
                IconButton(onClick = { onDeleteClick(note) }) {
                    Icon(Icons.Filled.Delete, "Delete note")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteDialog(note: Note?, onDismiss: () -> Unit, onConfirm: (Note) -> Unit) {
    var title by remember { mutableStateOf(note?.title ?: "") }
    var content by remember { mutableStateOf(note?.content ?: "") }
    var category by remember { mutableStateOf(note?.category ?: "Umum") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (note == null) "Tambah Catatan Baru" else "Edit Catatan") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Konten") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Kategori") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(note?.copy(title = title, content = content, category = category) ?: Note(title = title, content = content, category = category)) }) {
                Text(if (note == null) "Tambah" else "Simpan")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CatatankuApp(application = Application())
}

