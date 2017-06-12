package com.sergiomse.callmanager.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.sergiomse.callmanager.model.NumberEntry

/**
 * Created by sergiomse@gmail.com.
 */
@Dao
interface NumberDao {
    @Query("SELECT * FROM NumberEntry")
    fun getAll(): List<NumberEntry>

    @Query("SELECT * FROM NumberEntry WHERE type = 'NUMBER'")
    fun getAllNumbers(): MutableList<NumberEntry>

    @Query("SELECT * FROM NumberEntry WHERE type = 'CONTACT'")
    fun getAllContacts(): List<NumberEntry>

    @Insert
    fun insert(numberEntry: NumberEntry)
}