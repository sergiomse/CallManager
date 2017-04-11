package com.sergiomse.callmanager

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.media.AudioManager
import android.content.Context.AUDIO_SERVICE
import android.util.Log


class RingService : Service() {

    private val TAG = RingService::class.simpleName

    private var currentVolume: Int = 0
    private var alreadyStarted: Boolean = false

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!alreadyStarted) {
            val audio = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            currentVolume = audio.getStreamVolume(AudioManager.STREAM_RING)
            audio.setStreamVolume(AudioManager.STREAM_RING, 0, 0)
            alreadyStarted = true
        }
        return START_STICKY
    }

    override fun onDestroy() {
        Log.d(TAG, "Service onDestroy")
        val audio = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audio.setStreamVolume(AudioManager.STREAM_RING, currentVolume, 0)
        alreadyStarted = false
    }
}
