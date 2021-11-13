package com.manuel.mynotes

interface OnClickListener {
    fun onChecked(note: Note)
    fun onClick(note: Note, currentAdapter: NoteAdapter)
    fun onLongClick(note: Note, currentAdapter: NoteAdapter)
}