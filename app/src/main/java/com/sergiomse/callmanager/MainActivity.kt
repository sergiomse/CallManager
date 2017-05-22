package com.sergiomse.callmanager

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import com.sergiomse.callmanager.database.AppDatabase
import com.sergiomse.callmanager.database.NumbersDB
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.simpleName
    private val PERMISSION_REQUEST_READ_PHONE_STATE = 100
    private val PICK_CONTACT_REQUEST_CODE = 1000

    private var fabSelected = false

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
            if (!fabSelected) {
                startFabForwardAnimation()
            } else {
                startFabReverseAnimation()
            }
            fabSelected = !fabSelected

        }

        addNumber.setOnClickListener { _ ->
            val intent = Intent(this, AddNumberActivity::class.java)
            startActivity(intent)
        }

        addContact.setOnClickListener {_ ->
            val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            startActivityForResult(intent, PICK_CONTACT_REQUEST_CODE)
        }
    }

    override fun onResume() {
        super.onResume()

        initFabAnimations()

        val numbers = AppDatabase.getInstance(this).numberDao().getAll()

//        val numbersDB = NumbersDB(this)
//        val list = numbersDB.getAllNumbers()
//        numbersDB.cleanup()

        val adapter = NumbersAdapter(this, numbers)
        numberRV.adapter = adapter

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

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        when (requestCode) {
            PICK_CONTACT_REQUEST_CODE -> {
                if (resultCode == RESULT_OK && intent != null) {
                    val contactData = intent.data
                    val c = contentResolver.query(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        val id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID))
                        val name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))

                    }
                }
            }
        }
    }

    private fun initFabAnimations() {
        fabSelected = false
        fab.animation = null
        fab.rotation = 0f
        addNumber.visibility = View.GONE
        addContact.visibility = View.GONE
    }

    private fun startFabForwardAnimation() {
        var animation = RotateAnimation(0f, 45f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        animation.duration = 200
        animation.fillAfter = true
        fab.startAnimation(animation)

        addNumber.alpha = 0f
        addNumber.visibility = View.VISIBLE
        addNumber.animate()
                .alpha(1f)
                .setDuration(200)
                .setListener(null)

        addContact.alpha = 0f
        addContact.visibility = View.VISIBLE
        addContact.animate()
                .alpha(1f)
                .setDuration(200)
                .setListener(null)
    }

    private fun startFabReverseAnimation() {
        var animation = RotateAnimation(45f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        animation.duration = 200
        animation.fillAfter = true
        fab.startAnimation(animation)

        addNumber.animate()
                .alpha(0f)
                .setDuration(200)
                .setListener(object: AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        addNumber.visibility = View.GONE
                    }
                })

        addContact.animate()
                .alpha(0f)
                .setDuration(200)
                .setListener(object: AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        addContact.visibility = View.GONE
                    }
                })
    }
}
