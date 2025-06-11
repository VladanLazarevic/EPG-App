package com.example.epg.Data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.DisplayMetrics
import com.example.epg.Data.local.Constants.DEFAULT_IP
import com.example.epg.Data.local.Constants.PREFS_KEY_IP_ADDRESS
import com.example.epg.Data.local.Constants.SHARED_PREFS_NAME


fun Context.getPrefs(): SharedPreferences {
    return getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
}

fun Context.savePublicIpAddress(IP: String) {
    getPrefs().edit().putString(PREFS_KEY_IP_ADDRESS, IP).apply()
}


fun Context.getPublicIpAddress(): String? {
    return getPrefs().getString(PREFS_KEY_IP_ADDRESS, DEFAULT_IP)
}


fun Context.saveLastFocusedChannelId(channelId: String) {
    getPrefs().edit().putString(Constants.PREFS_KEY_LAST_FOCUSED_CHANNEL_ID, channelId).apply()
}


fun Context.getLastFocusedChannelId(): String? {
    return getPrefs().getString(Constants.PREFS_KEY_LAST_FOCUSED_CHANNEL_ID, null)
}