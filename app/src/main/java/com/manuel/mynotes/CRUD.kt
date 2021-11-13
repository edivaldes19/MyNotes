package com.manuel.mynotes

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class CRUD(context: Context) :
    SQLiteOpenHelper(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION) {
    override fun onCreate(sqLiteDatabase: SQLiteDatabase?) {
        val createTable =
            "CREATE TABLE ${Constants.TABLE_NAME} " +
                    "(${Constants.PROPERTY_ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "${Constants.PROPERTY_DESCRIPTION} VARCHAR(50), " +
                    "${Constants.PROPERTY_IS_DONE} BOOLEAN)"
        sqLiteDatabase?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}
    fun create(note: Note): Long {
        val sqLiteDatabase = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(Constants.PROPERTY_DESCRIPTION, note.description)
            put(Constants.PROPERTY_IS_DONE, note.isDone)
        }
        return sqLiteDatabase.insert(Constants.TABLE_NAME, null, contentValues)
    }

    @SuppressLint("Recycle", "Range")
    fun read(): MutableList<Note> {
        val notesList = mutableListOf<Note>()
        val sqLiteDatabase = this.readableDatabase
        val query = "SELECT * FROM ${Constants.TABLE_NAME}"
        val result = sqLiteDatabase.rawQuery(query, null)
        if (result.moveToFirst()) {
            do {
                val note = Note()
                note.id = result.getLong(result.getColumnIndex(Constants.PROPERTY_ID))
                note.description =
                    result.getString(result.getColumnIndex(Constants.PROPERTY_DESCRIPTION))
                note.isDone =
                    result.getInt(result.getColumnIndex(Constants.PROPERTY_IS_DONE)) == Constants.TRUE
                notesList.add(note)
            } while (result.moveToNext())
        }
        return notesList
    }

    fun update(note: Note): Boolean {
        val sqLiteDatabase = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(Constants.PROPERTY_DESCRIPTION, note.description)
            put(Constants.PROPERTY_IS_DONE, note.isDone)
        }
        val result = sqLiteDatabase.update(
            Constants.TABLE_NAME,
            contentValues,
            "${Constants.PROPERTY_ID} = ${note.id}",
            null
        )
        return result == Constants.TRUE
    }

    fun delete(note: Note): Boolean {
        val sqLiteDatabase = this.writableDatabase
        val result = sqLiteDatabase.delete(
            Constants.TABLE_NAME,
            "${Constants.PROPERTY_ID} = ${note.id}",
            null
        )
        return result == Constants.TRUE
    }
}