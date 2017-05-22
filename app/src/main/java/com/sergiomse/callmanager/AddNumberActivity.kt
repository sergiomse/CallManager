package com.sergiomse.callmanager

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.sergiomse.callmanager.database.AppDatabase
import com.sergiomse.callmanager.database.NumbersDB
import com.sergiomse.callmanager.model.NumberEntry
import com.sergiomse.callmanager.model.NumberType
import kotlinx.android.synthetic.main.activity_add_number.*

class AddNumberActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_number)
    }

    fun onSave(view: View) {
        val numberEntry = NumberEntry(type = NumberType.NUMBER, number = numberInput.text.toString())
        AppDatabase.getInstance(this).numberDao().insert(numberEntry)
        finish()
    }
}
