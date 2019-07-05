package com.example.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.model.Note

class MyDataBaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {


    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1
        const val TABLE_NOTE = "note"
        const val COLUMN_NOTE_ID = "id"
        const val COLUMN_NOTE_TITLE = "title"
        const val COLUMN_NOTE_CONTENT = "content"
        const val DATABASE_NAME = "training_sqlite.db"
        const val CREATE_SQL = ("CREATE TABLE " + TABLE_NOTE + "("
                + COLUMN_NOTE_ID + " INTEGER PRIMARY KEY," + COLUMN_NOTE_TITLE + " TEXT,"
                + COLUMN_NOTE_CONTENT + " TEXT" + ")")
        const val SELECT_QUERY = "SELECT * FROM " + TABLE_NOTE

    }

    var listNote: MutableList<Note> = mutableListOf()

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_SQL)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS" + TABLE_NOTE)
        onCreate(db)
    }


    fun createDefaultNoteIfNeed() {
        var count = this.getNoteCount()
        if (count == 0) {
            var note1 = Note(
                "Firstly see Android ListView",
                "See Android ListView Example"
            )
            var note2 = Note(
                "Learning Android SQlite",
                "SQLite is very easy to learn"
            )
            this.addNote(note1)
            this.addNote(note2)

        }
    }

    fun addNote(note: Note) {
        // Using content values
        val values = ContentValues()
        values.put(COLUMN_NOTE_TITLE, note.title)
        values.put(COLUMN_NOTE_CONTENT, note.content)
        this.writableDatabase.insert(TABLE_NOTE, null, values)
        this.writableDatabase.close()
    }

    fun getAllNotes(): List<Note> {
        listNote.clear()
        var cursor: Cursor = this.writableDatabase.rawQuery(SELECT_QUERY, null)
        if (cursor.moveToFirst()) {
            do {
                var note = Note(cursor.getInt(0), cursor.getString(1), cursor.getString(2))
                listNote.add(note)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return listNote
    }

    private fun getNoteCount(): Int {
        var cursor = this.readableDatabase.rawQuery(SELECT_QUERY, null)
        var count = cursor.count
        cursor.close()
        return count
    }

    fun updateNote(note: Note): Int {
        var content = ContentValues()
        content.put(COLUMN_NOTE_TITLE, note.title)
        content.put(COLUMN_NOTE_CONTENT, note.content)
        return this.writableDatabase.update(
            TABLE_NOTE,
            content,
            COLUMN_NOTE_ID + " = ?",
            arrayOf(note.id.toString())
        )
    }

    fun deleteNote(note: Note) {

        val db = this.writableDatabase
        db.delete(
            TABLE_NOTE,
            "$COLUMN_NOTE_ID = ?",
            arrayOf(note.id.toString())
        )
        db.close()
    }
}