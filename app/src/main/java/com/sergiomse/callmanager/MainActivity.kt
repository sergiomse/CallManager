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
import com.sergiomse.callmanager.model.NumberEntry
import com.sergiomse.callmanager.model.NumberType
import com.sergiomse.callmanager.utils.isNotificationManagerPermissionGranted
import com.sergiomse.callmanager.utils.isPermissionGranted
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.simpleName
    private val REQUEST_STANDARD_PERMISSIONS = 100
    private val PICK_CONTACT_REQUEST_CODE = 1000

    private var fabSelected = false

    private var snackbar1: Snackbar? = null
    private var snackbar2: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val layoutManager = LinearLayoutManager(this)
        numberRV.layoutManager = layoutManager

        createSnackbars()

        startPermissionsRequest()

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

        updateRecycler()
    }

    private fun startPermissionsRequest() {

        val permissions = mutableListOf<String>()
        if ( !isPermissionGranted(this, Manifest.permission.READ_PHONE_STATE) ) {
            permissions.add(Manifest.permission.READ_PHONE_STATE)
        }
        if ( !isPermissionGranted(this, Manifest.permission.READ_CONTACTS) ) {
            permissions.add(Manifest.permission.READ_CONTACTS)
        }

        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(),
                    REQUEST_STANDARD_PERMISSIONS)

        } else {
            requestNotificationManagerPermission()
        }
    }

    /**
     * Request for Notification Policy (for volume muting) permission
     */
    private fun requestNotificationManagerPermission() {
        if ( !isNotificationManagerPermissionGranted(this) ) {
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_STANDARD_PERMISSIONS -> {
                if (permissions.isNotEmpty()) {
                    for (i in permissions.indices) {
                        when (permissions[i]) {
                            Manifest.permission.READ_PHONE_STATE -> {
                                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) snackbar1?.show()
                            }

                            Manifest.permission.READ_CONTACTS -> {
                                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) snackbar2?.show()
                            }
                        }
                    }
                }
            }
        }

        requestNotificationManagerPermission()
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

                        //TODO check if the contact already exists
                        val contact = NumberEntry(type = NumberType.CONTACT, contactId = id.toLong())
                        AppDatabase.getInstance(this).numberDao().insert(contact)
                    }
                }
            }
        }
    }

    private fun createSnackbars() {
        snackbar1 = Snackbar.make(mainLayout,
                "Call Manager needs access to the phone state permission in order to detect incoming calls.",
                Snackbar.LENGTH_INDEFINITE)
                .setAction("Configure", {v ->
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                })

        snackbar2 = Snackbar.make(mainLayout,
                "Call Manager needs access to the contacts to work properly.",
                Snackbar.LENGTH_INDEFINITE)
                .setAction("Configure", {v ->
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                })
    }

    private fun updateRecycler() {

        val listOfNumbers = AppDatabase.getInstance(this).numberDao().getAllNumbers()
        val listOfContacts = AppDatabase.getInstance(this).numberDao().getAllContacts()

        if ( isPermissionGranted(this, Manifest.permission.READ_CONTACTS)) {
            val contactIds = listOfContacts.map { e -> e.contactId.toString() }

            val selection = MutableList(contactIds.size) { _ -> ContactsContract.Contacts._ID + " = ?"}
                    .joinToString(separator = " OR ")
            val cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                    arrayOf(ContactsContract.Contacts._ID,
                            ContactsContract.Contacts.DISPLAY_NAME),
                    selection,
                    contactIds.toTypedArray(),
                    null)

            while (cursor!!.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))

                val contacts = listOfContacts.filter { e -> e.contactId == id }
                if (contacts.isNotEmpty()) {
                    contacts[0].name = name
                }
            }
            cursor.close()

        } else {
            snackbar2?.show()
        }

        listOfNumbers.addAll(listOfContacts)
        val adapter = NumbersAdapter(this, listOfNumbers)
        numberRV.adapter = adapter
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
