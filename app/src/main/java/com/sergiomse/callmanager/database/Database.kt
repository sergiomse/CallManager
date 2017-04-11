package com.sergiomse.callmanager.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log


/**
 * Created by sergiomse@gmail.com.
 */

class Database(private val context: Context) {

    private val DATABASE_NAME = "database.db"
    val DATABASE_TABLE = "phones"
    val DATABASE_VERSION = 1

    val COLS = arrayOf("_id", "number")


    /**
     *
     */
    inner private class DBOpenHelper(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        private val TAG = DBOpenHelper::class.java.name

        private val CREATE_TABLE = "create table " +
                DATABASE_TABLE + " (" +
                "_id integer primary key autoincrement, " +
                "number text);"

        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(CREATE_TABLE)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            Log.d(TAG, "Database upgrade")
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

    fun getAllNumbers(): Array<String> {
        val numbers = mutableListOf<String>()
        val c = db!!.query(DATABASE_TABLE, COLS, null, null, null, null, null)
        while (c!!.moveToNext()) {
            numbers.add(c.getString(1))
        }
        c.close()

        return numbers.toTypedArray()
    }

    fun insertNumber(number: String) {
        val value = ContentValues()
        value.put("number", number)
        db!!.insert(DATABASE_TABLE, null, value)
    }
}