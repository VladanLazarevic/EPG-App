package com.example.epg.Domain.mapper

import com.example.epg.Data.model.ServerChannel
import com.example.epg.Domain.model.AppChannel

object ChannelMapper {

    fun mapServerToAppChannel(serverChannel: ServerChannel): AppChannel {
        return AppChannel(
            channelId = serverChannel.channelId,
            name = serverChannel.name,
            playbackUrl = serverChannel.playbackUrl,
            logo = serverChannel.logo
        )
    }


    fun mapServerListToAppList(serverChannels: List<ServerChannel>): List<AppChannel> {
        return serverChannels.map { mapServerToAppChannel(it) }
    }
}