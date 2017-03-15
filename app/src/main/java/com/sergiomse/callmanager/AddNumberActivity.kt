package com.sergiomse.callmanager

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.sergiomse.callmanager.database.Database
import kotlinx.android.synthetic.main.activity_add_number.*

class AddNumberActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_number)
    }

    fun onSave(view: View) {
        val db = Database(this)
        db.insertNumber(numberInput.text.toString())
        db.cleanup()
        finish()
    }
}
