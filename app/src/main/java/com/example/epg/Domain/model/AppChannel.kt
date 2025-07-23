package com.example.epg.Domain.model

data class AppChannel(
    val channelId: String,
    val name: String,
    val playbackUrl: String,
    val logo: String,
    val isFavorite: Boolean = false
)
