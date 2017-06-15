package com.sergiomse.callmanager

import android.content.Context
import android.media.AudioManager
import android.os.AsyncTask

/**
 * Created by sergiomse@gmail.com.
 */
class RingerModeTask: AsyncTask<Pair<Context, Boolean>, Void, Int>() {

    override fun doInBackground(vararg params: Pair<Context, Boolean>?): Int {
        if (params.isNotEmpty()) {
            val audio = params[0]?.first?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            audio.ringerMode = if (params[0]!!.second)
                    AudioManager.RINGER_MODE_NORMAL
                else
                    AudioManager.RINGER_MODE_SILENT
        }
        return 0
    }
}