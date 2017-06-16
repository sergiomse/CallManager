package com.sergiomse.callmanager

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.provider.ContactsContract
import android.telephony.PhoneNumberUtils
import android.telephony.TelephonyManager
import android.util.Log
import com.sergiomse.callmanager.database.AppDatabase
import com.sergiomse.callmanager.utils.isNotificationManagerPermissionGranted
import com.sergiomse.callmanager.utils.isPermissionGranted


class CallReceiver : BroadcastReceiver() {

    private val TAG = CallReceiver::class.simpleName

    @SuppressLint("ApplySharedPref")
    override fun onReceive(context: Context, intent: Intent) {
        // If we don't have "don't disturbe" permission granted we don't continue
        if (!isNotificationManagerPermissionGranted(context)) return

        val pref = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)

        Log.d(TAG, "incoming call: " + intent.action)
        if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            val number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
            Log.d(TAG, "state: $state, number: $number")

            if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                // Always mute for a quick response when a call is received. Later we'll proceed to check if should unmute
                val bannedNumbers = getAllBannedPhoneNumbers(context)
                val isBanned = bannedNumbers.any { PhoneNumberUtils.compare(number, it) }
                Log.d(TAG, "Is banned: $isBanned")
                if (isBanned) {
                    // The phone number is in the black list, so mute it
                    val audio = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                    val volume = audio.getStreamVolume(AudioManager.STREAM_RING)
                    val editor = pref.edit()
                    with(editor) {
                        putInt("volume", volume)
                        commit()
                        Log.d(TAG, "Volume saved")
                    }
                    audio.setStreamVolume(AudioManager.STREAM_RING, 0, 0)
                    Log.d(TAG, "Volume = " + volume)
                }

            } else if (state == TelephonyManager.EXTRA_STATE_IDLE) {
                val audio = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                val volume = pref.getInt("volume", audio.getStreamMaxVolume(AudioManager.STREAM_RING) / 2)
                audio.setStreamVolume(AudioManager.STREAM_RING, volume, 0)
            }

        }
    }

    private fun getAllBannedPhoneNumbers(context: Context): List<String> {
        val listOfNumbers = AppDatabase.getInstance(context).numberDao().getAllNumbers()

        if (isPermissionGranted(context, Manifest.permission.READ_CONTACTS)) {
            val listOfContacts = AppDatabase.getInstance(context).numberDao().getAllContacts()
            val contactIds = listOfContacts.map { e -> e.contactId.toString() }

            val selection = MutableList(contactIds.size) { _ -> ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?" }
                    .joinToString(separator = " OR ")
            val cursor = context.contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    arrayOf(ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                            ContactsContract.CommonDataKinds.Phone.NUMBER),
                    selection,
                    contactIds.toTypedArray(),
                    null)

            while (cursor!!.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
                val number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                val contacts = listOfContacts.filter { e -> e.contactId == id }
                if (contacts.isNotEmpty()) {
                    contacts[0].number = number
                }
            }
            cursor.close()

            listOfNumbers.addAll(listOfContacts)
        }

        return listOfNumbers.map { e -> e.number }
    }
}

