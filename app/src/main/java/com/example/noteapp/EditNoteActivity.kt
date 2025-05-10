package com.example.noteapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class EditNoteActivity : AppCompatActivity() {

    private lateinit var editTextTitle: EditText
    private lateinit var editTextContent: EditText
    private lateinit var editTextTags: EditText
    private lateinit var buttonSave: Button
    private lateinit var buttonDelete: Button
    private lateinit var btnBack: ImageView
    private lateinit var noteDao: NoteDao
    private var noteId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

        editTextTitle = findViewById(R.id.editTextTitle)
        editTextContent = findViewById(R.id.editTextContent)
        editTextTags = findViewById(R.id.editTextTags)
        buttonSave = findViewById(R.id.buttonSave)
        buttonDelete = findViewById(R.id.buttonDelete)
        btnBack = findViewById(R.id.btnBack)

        val database = NoteDatabase.getDatabase(application)
        noteDao = database.noteDao()

        noteId = intent.getIntExtra("NOTE_ID", -1)

        if (noteId != -1) {
            loadNote()
        }

        btnBack.setOnClickListener{
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        buttonSave.setOnClickListener {
            val title = editTextTitle.text.toString().trim()
            val content = editTextContent.text.toString().trim()
            val tags = editTextTags.text.toString().trim()

            if (title.isNotEmpty() && content.isNotEmpty()) {
                val updatedNote = Note(id = noteId, title = title, content = content, tags = tags)
                updateNote(updatedNote)
            } else {
                Toast.makeText(application, "başlık ve içerik boş olamaz!!!", Toast.LENGTH_LONG).show()
            }
        }

        buttonDelete.setOnClickListener {
            lifecycleScope.launch {
                noteDao.delete(Note(id = noteId, title = "", content = "", tags = ""))
                finish()
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }
        }
    }

    private fun loadNote() {
        lifecycleScope.launch {
            val note = noteDao.getNoteById(noteId)
            note?.let {
                editTextTitle.setText(it.title)
                editTextContent.setText(it.content)
                editTextTags.setText(it.tags)
            }
        }
    }

    private fun updateNote(note: Note) {
        lifecycleScope.launch {
            noteDao.update(note)
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }
}