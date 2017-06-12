package com.sergiomse.callmanager.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by sergiomse@gmail.com.
 */
@Entity
data class NumberEntry(@PrimaryKey(autoGenerate = true) var id: Long = 0,
                       @ColumnInfo var type: NumberType,
                       @ColumnInfo var name: String = "",
                       @ColumnInfo var number: String = "",
                       @ColumnInfo var contactId: Long = 0)