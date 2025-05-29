package com.example.epg.Data.repository.EPGRepository

import com.example.epg.Data.model.ChannelWithProgram
import com.example.epg.Data.model.ServerChannel
import com.example.epg.Domain.model.AppChannel
import com.example.epg.Domain.model.AppProgram
import org.json.JSONObject
import retrofit2.Response

interface EPGRepository {
    suspend fun getChannels(): Result<List<AppChannel>>
    suspend fun getPrograms(channels: List<AppChannel>, startEpoch: Long, endEpoch: Long? = null): Result<List<AppProgram>>
}