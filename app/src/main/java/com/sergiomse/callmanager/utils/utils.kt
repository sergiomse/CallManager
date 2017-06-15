package com.sergiomse.callmanager.utils

import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity

/**
 * Created by sergiomse@gmail.com.
 */

/**
 * @return True if the permission is granted, false if not
 */
fun isPermissionGranted(context: Context, permission: String): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        return true
    }
    return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}

fun isNotificationManagerPermissionGranted(context: Context): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        return true
    }
    val notificationManager = context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
    return notificationManager.isNotificationPolicyAccessGranted
}