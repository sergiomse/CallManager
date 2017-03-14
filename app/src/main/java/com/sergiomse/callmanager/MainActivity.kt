package com.sergiomse.callmanager

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.sergiomse.callmanager.database.Database
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(mainToolbar)
        mainToolbar.title = "Call Manager"

        val layoutManager = LinearLayoutManager(this)
        numberRV.layoutManager = layoutManager
    }

    override fun onResume() {
        super.onResume()

        val db = Database(this)
        val numberList = db.getAllNumbers()
        val adapter = NumbersAdapter(this, numberList)
        numberRV.setAdapter(adapter)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add_number ->
                //TODO Show activity for adding the number
                return true

            else ->
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item)
        }
    }
}
