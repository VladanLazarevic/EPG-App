package com.example.epg.Domain.model

data class AppProgram(
    val channelId: String,
    val programId: String,
    val title: String,
    val description: String?,
    val startTimeEpoch: Long,
    val durationSec: Int,
    val thumbnail: String?,
    val genre: String?,
    val startTime: String,
    val language: String?,
    //val isLive: Boolean = false
    val playbackURL: String = ""
)


