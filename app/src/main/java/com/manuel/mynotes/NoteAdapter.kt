package com.manuel.mynotes

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import com.manuel.mynotes.databinding.ItemNoteBinding

class NoteAdapter(private var noteList: MutableList<Note>, private val listener: OnClickListener) :
    RecyclerView.Adapter<NoteAdapter.ViewHolder>() {
    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_note, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = noteList[position]
        holder.setListener(note)
        holder.binding.root.animation =
            AnimationUtils.loadAnimation(context, R.anim.fade_transition)
        holder.binding.tvDescription.text = note.description
        holder.binding.cbFinish.isChecked = note.isDone
        if (note.isDone) {
            holder.binding.tvDescription.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                context.resources.getInteger(R.integer.description_size_done).toFloat()
            )
        } else {
            holder.binding.tvDescription.setTextSize(
                TypedValue.COMPLEX_UNIT_SP,
                context.resources.getInteger(R.integer.description_size_default).toFloat()
            )
        }
    }

    override fun getItemCount() = noteList.size
    fun add(note: Note) {
        if (!noteList.contains(note)) {
            noteList.add(note)
            notifyItemInserted(noteList.size - 1)
        } else {
            update(note)
        }
    }

    fun update(note: Note) {
        val index = noteList.indexOf(note)
        if (index != -1) {
            noteList[index] = note
            notifyItemChanged(index)
        }
    }

    fun delete(note: Note) {
        val index = noteList.indexOf(note)
        if (index != -1) {
            noteList.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemNoteBinding.bind(view)
        fun setListener(note: Note) {
            binding.cbFinish.setOnClickListener { view ->
                note.isDone = (view as MaterialCheckBox).isChecked
                listener.onChecked(note)
            }
            binding.root.setOnClickListener {
                listener.onClick(note, this@NoteAdapter)
            }
            binding.root.setOnLongClickListener {
                listener.onLongClick(note, this@NoteAdapter)
                true
            }
        }
    }
}