package com.example.noteapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class AddNoteActivity : AppCompatActivity() {

    private lateinit var editTextTitle: EditText
    private lateinit var editTextContent: EditText
    private lateinit var buttonSave: Button
    private lateinit var noteDao: NoteDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        editTextTitle = findViewById(R.id.editTextTitle)
        editTextContent = findViewById(R.id.editTextContent)
        buttonSave = findViewById(R.id.buttonSave)

        val database = NoteDatabase.getDatabase(application)
        noteDao = database.noteDao()

        buttonSave.setOnClickListener {
            val title = editTextTitle.text.toString().trim()
            val content = editTextContent.text.toString().trim()

            if (title.isNotEmpty() && content.isNotEmpty()) {
                val note = Note(title = title, content = content)
                saveNote(note)
            } else {
                // Kullanıcıya başlık ve içeriğin boş olmaması gerektiğini bildirebilirsiniz (isteğe bağlı)
            }
        }
    }

    private fun saveNote(note: Note) {
        lifecycleScope.launch {
            noteDao.insert(note)
            finish() // Kaydetme işleminden sonra AddNoteActivity'yi kapat ve MainActivity'ye geri dön
        }
    }
}