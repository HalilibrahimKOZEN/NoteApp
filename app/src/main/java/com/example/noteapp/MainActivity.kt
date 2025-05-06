package com.example.noteapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.lifecycleScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerViewNotes: RecyclerView
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var noteDao: NoteDao
    private lateinit var fabAddNote: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val database = NoteDatabase.getDatabase(application)
        noteDao = database.noteDao()

        recyclerViewNotes = findViewById(R.id.recyclerViewNotes)
        recyclerViewNotes.layoutManager = LinearLayoutManager(this)
        noteAdapter = NoteAdapter { note -> // Tıklama listener'ı burada implemente ediliyor
            val intent = Intent(this, EditNoteActivity::class.java).apply {
                putExtra("NOTE_ID", note.id) // Tıklanan notun ID'sini EditNoteActivity'ye gönderiyoruz
            }
            startActivity(intent)
        }
        recyclerViewNotes.adapter = noteAdapter

        fabAddNote = findViewById(R.id.fabAddNote)
        fabAddNote.setOnClickListener {
            val intent = Intent(this, AddNoteActivity::class.java)
            startActivity(intent)
        }

        observeNotes()
    }

    private fun observeNotes() {
        lifecycleScope.launch {
            noteDao.getAllNotes().collectLatest { notes ->
                noteAdapter.submitList(notes)
            }
        }
    }
}