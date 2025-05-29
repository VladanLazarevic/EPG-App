package com.example.epg.Data.network

import com.example.epg.Data.model.ChannelWithProgram
import com.example.epg.Data.model.ServerChannel
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiService {

    @GET("/v3/auid")
    suspend fun getAuid(@Query("deviceId") deviceId: String) : Response<String>

    @GET("v3/channellist")
    suspend fun getChannels(@Query("auid") auidd: String, @Query("ip") ip: String,@Query("country") country: String ): Response<List<ServerChannel>>

    @GET("v3/programlist")
    suspend fun getPrograms(@Query("auid") auidd: String, @Query("startEpoch") startEpoch: Long, @Query("channelIds") channelIDs: String, @Query("endEpoch") endEpoch: Long? = null): Response<List<ChannelWithProgram>>

}