package com.sergiomse.callmanager.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import com.sergiomse.callmanager.model.NumberEntry
import com.sergiomse.callmanager.model.Converters
import com.sergiomse.callmanager.utils.SingletonHolder

/**
 * Created by sergiomse@gmail.com.
 */
@Database(entities = [NumberEntry::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun numberDao(): NumberDao

    companion object : SingletonHolder<AppDatabase, Context>({
        Room.databaseBuilder(it.applicationContext, AppDatabase::class.java, "database")
                .allowMainThreadQueries()
                .build()
    })
}