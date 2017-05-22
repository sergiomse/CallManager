package com.sergiomse.callmanager.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.sergiomse.callmanager.model.NumberEntry
import com.sergiomse.callmanager.model.NumberType


/**
 * Created by sergiomse@gmail.com.
 */

class NumbersDB(private val context: Context) {

    private val DATABASE_NAME = "database.db"
    val DATABASE_TABLE = "phones"
    val DATABASE_VERSION = 1

    val COLS = arrayOf("_id", "name", "number")


    /**
     *
     */
    inner private class DBOpenHelper(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        private val TAG = DBOpenHelper::class.java.name

        private val CREATE_TABLE = "create table " +
                DATABASE_TABLE + " (" +
                "_id integer primary key autoincrement, " +
                "name text, " +
                "number text);"

        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(CREATE_TABLE)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            Log.d(TAG, "NumbersDB upgrade")
        }
    }


    private var helper: DBOpenHelper? = null
    private var db: SQLiteDatabase? = null

    init {
        helper = DBOpenHelper(context)
        establishDb()
    }

    private fun establishDb() {
        if (db == null) {
            db = helper!!.writableDatabase
        }
    }

    fun cleanup() {
        if (db != null) {
            db!!.close()
            db = null
        }
    }

    fun getAllNumbers(): MutableList<NumberEntry> {
        val numbers = mutableListOf<NumberEntry>()
        val c = db!!.query(DATABASE_TABLE, COLS, null, null, null, null, null)
        while (c!!.moveToNext()) {
            val id = c.getLong(0)

            val name = c.getString(1)
            val numberStr = c.getString(2)
            numbers.add (NumberEntry(id, NumberType.NUMBER, name, numberStr))
        }
        c.close()

        return numbers
    }

    fun insertNumber(numberEntry: NumberEntry) {
        if (numberEntry.type != NumberType.NUMBER) return

        val value = ContentValues()
        value.put(COLS[1], numberEntry.name)
        value.put(COLS[2], numberEntry.number)
        db!!.insert(DATABASE_TABLE, null, value)
    }
}