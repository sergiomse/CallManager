package com.sergiomse.callmanager.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by sergiomse@gmail.com.
 */
@Entity
data class NumberEntry(@PrimaryKey val id: Long = 0,
                       @ColumnInfo val type: NumberType,
                       @ColumnInfo val name: String = "",
                       @ColumnInfo val number: String = "",
                       @ColumnInfo val contactId: Long = 0)