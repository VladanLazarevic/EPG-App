package com.example.epg.Data.local

import android.content.Context

class FavoriteManager(context: Context) {


    private val prefs = context.getSharedPreferences("epg_favorites_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_FAVORITE_IDS = "favorite_channel_ids"
    }


    fun getFavoriteIds(): Set<String> {
        return prefs.getStringSet(KEY_FAVORITE_IDS, emptySet()) ?: emptySet()
    }


    fun toggleFavorite(channelId: String) {

        val currentFavorites = getFavoriteIds().toMutableSet()

        if (channelId in currentFavorites) {
            currentFavorites.remove(channelId)
        } else {
            currentFavorites.add(channelId)
        }

        prefs.edit().putStringSet(KEY_FAVORITE_IDS, currentFavorites).apply()
    }
}