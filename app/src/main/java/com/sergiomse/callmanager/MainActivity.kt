package com.sergiomse.callmanager

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.sergiomse.callmanager.database.Database
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.simpleName

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
        numberRV.adapter = adapter
        db.cleanup()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "onOptionsItemSelected: " + item.itemId)
        return when (item.itemId) {
            R.id.action_add_number -> {
                val intent = Intent(this, AddNumberActivity::class.java)
                startActivity(intent)
                true
            }

            else ->
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                super.onOptionsItemSelected(item)
        }
    }
}
