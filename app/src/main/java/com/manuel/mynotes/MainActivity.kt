package com.manuel.mynotes

import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.manuel.mynotes.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var pendingNotesAdapter: NoteAdapter
    private lateinit var madeNotesAdapter: NoteAdapter
    private lateinit var crud: CRUD
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_MyNotes)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        crud = CRUD(this)
        setupRecyclerViews()
        setupButton()
    }

    override fun onStart() {
        super.onStart()
        getAllNotes()
    }

    override fun onChecked(note: Note) {
        if (crud.update(note)) {
            deleteNoteOnTheList(note)
            addNoteOnTheList(note)
            Snackbar.make(binding.root, getString(R.string.note_updated), Snackbar.LENGTH_SHORT)
                .show()
        } else {
            Snackbar.make(
                binding.root,
                getString(R.string.failed_to_update_note),
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    override fun onClick(note: Note, currentAdapter: NoteAdapter) {
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle(getString(R.string.edit_text))
        val editText = TextInputEditText(this)
        editText.hint = getString(R.string.hint_description)
        editText.filters = arrayOf<InputFilter>(LengthFilter(50))
        editText.maxLines = 2
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(40, 20, 40, 20)
        editText.layoutParams = params
        val container = RelativeLayout(this)
        val relativeParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        container.layoutParams = relativeParams
        container.addView(editText)
        builder.setView(container)
        builder.setPositiveButton(getString(R.string.edit)) { _, _ ->
            if (editText.text.toString().trim().isNotEmpty()) {
                note.description = editText.text.toString().trim()
                if (crud.update(note)) {
                    currentAdapter.update(note)
                    updateNoteOnTheList(note)
                    Snackbar.make(
                        binding.root,
                        getString(R.string.note_updated),
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.failed_to_update_note),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            } else {
                Snackbar.make(
                    binding.root,
                    getString(R.string.validation_field_required),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
        builder.setNegativeButton(getString(R.string.dialog_cancel), null)
        builder.show()
    }

    override fun onLongClick(note: Note, currentAdapter: NoteAdapter) {
        val builder = MaterialAlertDialogBuilder(this).setTitle(getString(R.string.dialog_title))
            .setPositiveButton(getString(R.string.dialog_delete)) { _, _ ->
                if (crud.delete(note)) {
                    currentAdapter.delete(note)
                    Snackbar.make(
                        binding.root,
                        getString(R.string.note_removed),
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.failed_to_delete_note),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }.setNegativeButton(getString(R.string.dialog_cancel), null)
        builder.create().show()
    }

    private fun setupRecyclerViews() {
        pendingNotesAdapter = NoteAdapter(mutableListOf(), this)
        binding.rvNotes.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = pendingNotesAdapter
        }
        madeNotesAdapter = NoteAdapter(mutableListOf(), this)
        binding.rvNotesFinished.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = madeNotesAdapter
        }
    }

    private fun setupButton() {
        binding.btnAdd.setOnClickListener {
            if (binding.etDescription.text.toString().trim().isEmpty()) {
                binding.tilDescription.run {
                    error = getString(R.string.validation_field_required)
                    requestFocus()
                }
            } else {
                binding.tilDescription.error = null
                val note = Note(description = binding.etDescription.text.toString().trim())
                note.id = crud.create(note)
                if (note.id != Constants.ID_ERROR) {
                    addNoteOnTheList(note)
                    binding.etDescription.text?.clear()
                    Snackbar.make(
                        binding.root,
                        getString(R.string.note_added),
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    Snackbar.make(
                        binding.root,
                        getString(R.string.error_adding_note),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun getAllNotes() {
        val data = crud.read()
        data.forEach { note -> addNoteOnTheList(note) }
    }

    private fun addNoteOnTheList(note: Note) {
        if (note.isDone) {
            madeNotesAdapter.add(note)
        } else {
            pendingNotesAdapter.add(note)
        }
    }

    private fun updateNoteOnTheList(note: Note) {
        if (note.isDone) {
            madeNotesAdapter.update(note)
        } else {
            pendingNotesAdapter.update(note)
        }
    }

    private fun deleteNoteOnTheList(note: Note) {
        if (note.isDone) {
            pendingNotesAdapter.delete(note)
        } else {
            madeNotesAdapter.delete(note)
        }
    }
}