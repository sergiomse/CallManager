package com.sergiomse.callmanager

import android.Manifest
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.sergiomse.callmanager.database.Database
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import android.net.Uri.fromParts
import android.util.Log


class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.simpleName
    private val PERMISSION_REQUEST_READ_PHONE_STATE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val layoutManager = LinearLayoutManager(this)
        numberRV.layoutManager = layoutManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissionReadPhoneState()
        }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted) {
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            startActivity(intent)
        }

        fab.setOnClickListener { _ ->
            val intent = Intent(this, AddNumberActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        val db = Database(this)
        val numberList = db.getAllNumbers()
        val adapter = NumbersAdapter(this, numberList)
        numberRV.adapter = adapter
        db.cleanup()

    }

    private fun requestPermissionReadPhoneState() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_PHONE_STATE),
                    PERMISSION_REQUEST_READ_PHONE_STATE);

        }
    }

    //TODO show alert when the permission is not granted
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_READ_PHONE_STATE -> {

                if (!grantResults.isNotEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(mainLayout,
                            "Call Manager needs access to the phone state permission in order to detect incoming calls.",
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction("Configure", {v ->
                                val intent = Intent()
                                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                val uri = Uri.fromParts("package", packageName, null)
                                intent.data = uri
                                startActivity(intent)
                            })
                            .show()

                }
                return
            }
        }
    }

}
