package ru.mrfix1033.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteCursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseManager(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, TABLE_NAME, factory, DATABASE_VERSION) {
    companion object {
        val TABLE_NAME = "persons"
        val DATABASE_VERSION = 1

        class Key {
            companion object {
                val KEY_ID = "id"
                val KEY_NAME = "name"
                val KEY_PHONE_NUMBER = "phone_number"
                val KEY_POSITION = "position"
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                    "${Key.KEY_ID} INTEGER PRIMARY KEY," +
                    "${Key.KEY_NAME} TEXT," +
                    "${Key.KEY_PHONE_NUMBER} TEXT," +
                    "${Key.KEY_POSITION} TEXT" +
                    ") "
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun insert(person: Person) {
        writableDatabase.use {
            it.insert(TABLE_NAME, null, ContentValues().run {
                person.run {
                    put(Key.KEY_NAME, name)
                    put(Key.KEY_PHONE_NUMBER, phoneNumber)
                    put(Key.KEY_POSITION, position)
                }
                this
            })
        }
    }

    fun select(func: (cursor: Cursor) -> Any) {
        readableDatabase.use { func(it.rawQuery("SELECT * FROM $TABLE_NAME", null)) }
    }

    fun _REMOVE_ALL() {
        writableDatabase.use { it.delete(TABLE_NAME, null, null) }
    }
}