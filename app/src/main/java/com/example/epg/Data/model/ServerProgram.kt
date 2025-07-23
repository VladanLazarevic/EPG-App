package com.example.epg.Data.model

data class ServerProgram(

    val startTime: String,
    val startTimeEpoch: Long,
    val durationSec: Int,
    val contentId: String,
    val title: String,
    val description: String?,
    val thumbnail: String?,
    val rating: String?,
    val origRating: String?,
    val genre: String?,
    val runtime: String?,
    val language: String?
)
