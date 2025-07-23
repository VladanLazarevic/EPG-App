package com.example.epg.Data.model

data class ServerChannel(
    val channelId: String,
    val name: String,
    val playbackUrl: String,
    val logo: String,
    val rating: String,
    val genre: List<String>,
    val resolution: List<String>,
    val isAnokiChannel: Boolean,
    val createNewDrmSession: Boolean
)
