package com.sergiomse.callmanager.model

import android.arch.persistence.room.TypeConverter

/**
 * Created by sergiomse@gmail.com.
 */
class Converters {

    @TypeConverter
    fun toNumberString(numberType: NumberType): String {
        return numberType.toString()
    }

    @TypeConverter
    fun toNumberType(numberString: String): NumberType {
        return when (numberString) {
            NumberType.NUMBER.toString() -> NumberType.NUMBER
            NumberType.CONTACT.toString() -> NumberType.CONTACT
            else -> NumberType.UNDEFINED
        }
    }
}