package com.example.noteapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerViewNotes: RecyclerView
    private lateinit var noteAdapter: NoteAdapter
    private lateinit var noteDao: NoteDao
    private lateinit var fabAddNote: FloatingActionButton
    private lateinit var emptyNotesView: View
    private lateinit var searchViewNotes: SearchView
    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val database = NoteDatabase.getDatabase(application)
        noteDao = database.noteDao()

        recyclerViewNotes = findViewById(R.id.recyclerViewNotes)
        emptyNotesView = findViewById(R.id.emptyNotesView)
        searchViewNotes = findViewById(R.id.searchViewNotes)
        recyclerViewNotes.layoutManager = LinearLayoutManager(this)
        noteAdapter = NoteAdapter { note ->
            val intent = Intent(this, EditNoteActivity::class.java).apply {
                putExtra("NOTE_ID", note.id)
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


        searchViewNotes.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    newText?.let { query ->
                        if (query.isNotEmpty()) {
                            noteDao.searchNotes(query).collectLatest { searchedNotes ->
                                if (searchedNotes.isEmpty()) {
                                    recyclerViewNotes.visibility = View.GONE
                                    emptyNotesView.visibility = View.VISIBLE
                                } else {
                                    recyclerViewNotes.visibility = View.VISIBLE
                                    emptyNotesView.visibility = View.GONE
                                    noteAdapter.submitList(searchedNotes)
                                }
                            }
                        } else {
                            noteDao.getAllNotes().collectLatest { allNotes ->
                                if (allNotes.isEmpty()) {
                                    recyclerViewNotes.visibility = View.GONE
                                    emptyNotesView.visibility = View.VISIBLE
                                } else {
                                    recyclerViewNotes.visibility = View.VISIBLE
                                    emptyNotesView.visibility = View.GONE
                                    noteAdapter.submitList(allNotes)
                                }
                            }
                        }
                    }
                }
                return true
            }
        })
    }

    private fun observeNotes() {
        lifecycleScope.launch {
            noteDao.getAllNotes().collectLatest { notes ->
                if (notes.isEmpty()) {
                    recyclerViewNotes.visibility = View.GONE
                    emptyNotesView.visibility = View.VISIBLE
                } else {
                    recyclerViewNotes.visibility = View.VISIBLE
                    emptyNotesView.visibility = View.GONE
                    noteAdapter.submitList(notes)
                }
            }
        }
    }
}