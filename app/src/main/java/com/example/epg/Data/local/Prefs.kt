package com.example.epg.Data.local

import android.content.Context
import android.content.SharedPreferences


fun Context.getPrefs(): SharedPreferences {
    return getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE)
}

fun Context.savePublicIpAddress(IP: String) {
    getPrefs().edit().putString(Constants.PREFS_KEY_IP_ADDRESS, IP).apply()
}


fun Context.getPublicIpAddress(): String? {
    return getPrefs().getString(Constants.PREFS_KEY_IP_ADDRESS, Constants.DEFAULT_IP)
}


fun Context.saveLastFocusedChannelId(channelId: String) {
    getPrefs().edit().putString(Constants.PREFS_KEY_LAST_FOCUSED_CHANNEL_ID, channelId).apply()
}


fun Context.getLastFocusedChannelId(): String? {
    return getPrefs().getString(Constants.PREFS_KEY_LAST_FOCUSED_CHANNEL_ID, null)
}