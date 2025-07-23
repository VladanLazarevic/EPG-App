package com.example.epg.Data.model

data class ChannelWithProgram(
    val channelId: String,
    val items: List<ServerProgram>
)
