package com.example.noteapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ArchiveActivity : AppCompatActivity() {

    private lateinit var recyclerViewArchivedNotes: RecyclerView
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var noteDao: NoteDao
    private lateinit var emptyArchivedNotesView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_archive)

        val database = NoteDatabase.getDatabase(application)
        noteDao = database.noteDao()

        recyclerViewArchivedNotes = findViewById(R.id.recyclerViewArchivedNotes)
        emptyArchivedNotesView = findViewById(R.id.emptyArchivedNotesView)
        recyclerViewArchivedNotes.layoutManager = LinearLayoutManager(this)
        noteAdapter = NoteAdapter { note ->
            val intent = android.content.Intent(this, EditNoteActivity::class.java).apply {
                putExtra("NOTE_ID", note.id)
            }
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
        recyclerViewArchivedNotes.adapter = noteAdapter

        observeArchivedNotes()
    }

    private fun observeArchivedNotes() {
        lifecycleScope.launch {
            noteDao.getAllArchivedNotes().collectLatest { archivedNotes ->
                if (archivedNotes.isEmpty()) {
                    recyclerViewArchivedNotes.visibility = View.GONE
                    emptyArchivedNotesView.visibility = View.VISIBLE
                } else {
                    recyclerViewArchivedNotes.visibility = View.VISIBLE
                    emptyArchivedNotesView.visibility = View.GONE
                    noteAdapter.submitList(archivedNotes)
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }
}