package com.example.noteapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class EditNoteActivity : AppCompatActivity() {

    private lateinit var editTextTitle: EditText
    private lateinit var editTextContent: EditText
    private lateinit var buttonSave: Button
    private lateinit var buttonDelete: Button
    private lateinit var noteDao: NoteDao
    private var noteId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

        editTextTitle = findViewById(R.id.editTextTitle)
        editTextContent = findViewById(R.id.editTextContent)
        buttonSave = findViewById(R.id.buttonSave)
        buttonDelete = findViewById(R.id.buttonDelete) // Sil butonuna referans alıyoruz

        val database = NoteDatabase.getDatabase(application)
        noteDao = database.noteDao()

        noteId = intent.getIntExtra("NOTE_ID", -1)

        if (noteId != -1) {
            loadNote()
        }

        buttonSave.setOnClickListener {
            val title = editTextTitle.text.toString().trim()
            val content = editTextContent.text.toString().trim()

            if (title.isNotEmpty() && content.isNotEmpty()) {
                val updatedNote = Note(id = noteId, title = title, content = content)
                updateNote(updatedNote)
            } else {
                // Kullanıcıya başlık ve içeriğin boş olmaması gerektiğini bildirebilirsiniz
            }
        }

        buttonDelete.setOnClickListener { // Sil butonuna tıklama listener'ı ekliyoruz
            lifecycleScope.launch {
                noteDao.delete(Note(id = noteId, title = "", content = "")) // Sadece ID'si yeterli
                finish() // Silme işleminden sonra EditNoteActivity'yi kapat
            }
        }
    }

    private fun loadNote() {
        lifecycleScope.launch {
            val note = noteDao.getNoteById(noteId)
            note?.let {
                editTextTitle.setText(it.title)
                editTextContent.setText(it.content)
            }
        }
    }

    private fun updateNote(note: Note) {
        lifecycleScope.launch {
            noteDao.update(note)
            finish()
        }
    }
}