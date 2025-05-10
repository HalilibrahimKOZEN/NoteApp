package com.example.noteapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class AddNoteActivity : AppCompatActivity() {

    private lateinit var editTextTitle: EditText
    private lateinit var editTextContent: EditText
    private lateinit var editTextTags: EditText
    private lateinit var buttonSave: Button
    private lateinit var btnBack: ImageView
    private lateinit var noteDao: NoteDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        editTextTitle = findViewById(R.id.editTextTitle)
        editTextContent = findViewById(R.id.editTextContent)
        buttonSave = findViewById(R.id.buttonSave)
        btnBack = findViewById(R.id.btnBack)
        editTextTags = findViewById(R.id.editTextTags)

        val database = NoteDatabase.getDatabase(application)
        noteDao = database.noteDao()

        btnBack.setOnClickListener{
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        buttonSave.setOnClickListener {
            val title = editTextTitle.text.toString().trim()
            val content = editTextContent.text.toString().trim()
            val tags = editTextTags.text.toString().trim()

            if (title.isNotEmpty() && content.isNotEmpty()) {
                val note = Note(title = title, content = content, tags = tags)
                saveNote(note)
            } else {
                Toast.makeText(application, "başlık ve içerik boş olamaz!!!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun saveNote(note: Note) {
        lifecycleScope.launch {
            noteDao.insert(note)
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }
}