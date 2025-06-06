package com.example.noteapp

import android.os.Bundle
import android.view.View
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
    private lateinit var buttonArchive: Button
    private lateinit var btnBack: ImageView
    private lateinit var noteDao: NoteDao
    private var noteId: Int = -1

    private var selectedColor: String = "#FFFFFF"
    private lateinit var colorWhite: View
    private lateinit var colorRed: View
    private lateinit var colorGreen: View
    private lateinit var colorBlue: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

        editTextTitle = findViewById(R.id.editTextTitle)
        editTextContent = findViewById(R.id.editTextContent)
        editTextTags = findViewById(R.id.editTextTags)
        buttonSave = findViewById(R.id.buttonSave)
        buttonDelete = findViewById(R.id.buttonDelete)
        btnBack = findViewById(R.id.btnBack)
        buttonArchive = findViewById(R.id.buttonArchive)

        colorWhite = findViewById(R.id.color_white)
        colorRed = findViewById(R.id.color_red)
        colorGreen = findViewById(R.id.color_green)
        colorBlue = findViewById(R.id.color_blue)


        colorWhite.setOnClickListener { selectColor("#FFFFFF") }
        colorRed.setOnClickListener { selectColor("#FF0000") }
        colorGreen.setOnClickListener { selectColor("#00FF00") }
        colorBlue.setOnClickListener { selectColor("#0000FF") }

        val database = NoteDatabase.getDatabase(application)
        noteDao = database.noteDao()

        noteId = intent.getIntExtra("NOTE_ID", -1)

        if (noteId != -1) {
            loadNote()
        } else {
            buttonSave.text = getString(R.string.save)
            buttonDelete.visibility = View.GONE
            buttonArchive.visibility = View.GONE
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

        buttonArchive.setOnClickListener {
            lifecycleScope.launch {
                val note = noteDao.getNoteById(noteId)
                note?.let {
                    noteDao.archiveNote(it.copy(isArchived = true))
                    finish()
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                }
            }
        }
    }



    private fun selectColor(colorHex: String) {
        selectedColor = colorHex
        updateColorSelectionView(selectedColor)
    }

    private fun updateColorSelectionView(currentColor: String) {
        colorWhite.setBackgroundResource(if (currentColor == "#FFFFFF") R.drawable.circle_white_selected else R.drawable.circle_white)
        colorRed.setBackgroundResource(if (currentColor == "#FF0000") R.drawable.circle_red_selected else R.drawable.circle_red)
        colorGreen.setBackgroundResource(if (currentColor == "#00FF00") R.drawable.circle_green_selected else R.drawable.circle_green)
        colorBlue.setBackgroundResource(if (currentColor == "#0000FF") R.drawable.circle_blue_selected else R.drawable.circle_blue)
    }

    private fun loadNote() {
        lifecycleScope.launch {
            val note = noteDao.getNoteById(noteId)
            note?.let {
                editTextTitle.setText(it.title)
                editTextContent.setText(it.content)
                editTextTags.setText(it.tags)
                selectedColor = it.color
                updateColorSelectionView(selectedColor)
            }
        }
    }

    private fun updateNote(note: Note) {
        lifecycleScope.launch {
            noteDao.update(note.copy(color = selectedColor))
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }
}