package com.example.noteapp

import android.graphics.Color
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class NoteAdapter(private val onClick: (Note) -> Unit) :
    ListAdapter<Note, NoteAdapter.NoteViewHolder>(NoteDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = getItem(position)
        holder.bind(note)
    }

    class NoteViewHolder(itemView: View, val onClick: (Note) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.textViewTitle)
        private val contentTextView: TextView = itemView.findViewById(R.id.textViewContent)
        private val tagsTextView: TextView = itemView.findViewById(R.id.textViewTags)
        private val noteBackgroundLayout: LinearLayout = itemView.findViewById(R.id.note_background_layout)
        private val imageViewShare: ImageView = itemView.findViewById(R.id.imageViewShare)

        fun bind(note: Note) {
            titleTextView.text = note.title
            contentTextView.text = note.content
            tagsTextView.text = note.tags

            try {
                noteBackgroundLayout.setBackgroundColor(Color.parseColor(note.color))
            } catch (e: IllegalArgumentException) {
                noteBackgroundLayout.setBackgroundColor(Color.parseColor("#FFFFFF"))
            }

            itemView.setOnClickListener {
                onClick(note)
            }

            imageViewShare.setOnClickListener {
                val shareText = StringBuilder()
                if (note.title.isNotEmpty()) {
                    shareText.append(note.title).append("\n")
                }
                if (note.content.isNotEmpty()) {
                    shareText.append(note.content).append("\n")
                }
                if (note.tags.isNotEmpty()) {
                    shareText.append("\nEtiketler: ").append(note.tags)
                }

                if (shareText.isNotEmpty()) {
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareText.toString())
                        type = "text/plain"
                    }
                    itemView.context.startActivity(Intent.createChooser(shareIntent, itemView.context.getString(R.string.share)))
                } else {
                    android.widget.Toast.makeText(itemView.context, itemView.context.getString(R.string.no_content_to_share), android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    object NoteDiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }
}