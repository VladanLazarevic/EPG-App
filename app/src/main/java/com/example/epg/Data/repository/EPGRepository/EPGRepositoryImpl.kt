package com.example.epg.Data.repository.EPGRepository

import android.util.Log
import com.example.epg.Data.model.ServerChannel
import com.example.epg.Data.network.IpAddressHelper
import com.example.epg.Data.network.RetrofitInstance
import com.example.epg.Data.repository.AUIDRepository.AUIDRepository
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import android.content.Context


import com.example.epg.Domain.model.AppChannel
import com.example.epg.Domain.mapper.ChannelMapper
import com.example.epg.Domain.mapper.ProgramMapper
import com.example.epg.Domain.model.AppProgram


///////////////////////////****************************************************************************************************////////////////////////////////
/*class EPGRepositoryImpl(
    private val auidRepository: AUIDRepository,
    private val ipAddressHelper: IpAddressHelper,
    private val context: Context
) : EPGRepository {

    private val TAG = "EPGRepositoryImpl"

    override suspend fun getChannels(): Result<List<AppChannel>> {
        val auid = auidRepository.getAuidString()
        if (auid == null) {
            Log.e(TAG, "AUID is null, cannot fetch channels.")
            return Result.failure(Exception("AUID could not be retrieved."))
        }
        Log.d(TAG, "Retrieved AUID: $auid")


        val ipAddress = ipAddressHelper.getIpAddress(context)
        if (ipAddress == null || ipAddress.isEmpty()) {
            Log.e(TAG, "IP Address is null or empty, cannot fetch channels.")
            return Result.failure(Exception("IP Address could not be retrieved."))
        }
        Log.d(TAG, "Retrieved IP Address: $ipAddress")

        return try {

            val response = RetrofitInstance.api.getChannels(auidd = auid, ip = ipAddress, country = "USA")

            if (response.isSuccessful) {
                val serverChannels = response.body()
                if (serverChannels != null) {
                    Log.d(TAG, "Successfully fetched ${serverChannels.size} server channels.")
                    Log.d(TAG, "Fetched server channels: $serverChannels")
                    Log.d(TAG, "USPIJEH: ${response.code()}")

                    // Mapping//
                    val appChannels = ChannelMapper.mapServerListToAppList(serverChannels)
                    Log.d(TAG, "Mapped to ${appChannels.size} app channels.")
                    Result.success(appChannels)
                } else {
                    Log.e(TAG, "Server channel list is null even though response was successful.")
                    Result.failure(Exception("Channel list is null in server response body."))
                }
            } else {
                val errorCode = response.code()
                val errorMsg = response.message()
                val errorBody = try { response.errorBody()?.string() } catch (e: Exception) { "Error reading error body." }
                Log.e(TAG, "Failed to fetch channels. Code: $errorCode, Message: $errorMsg, ErrorBody: $errorBody")
                Result.failure(Exception("Failed to fetch channels. HTTP $errorCode: $errorMsg"))
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network IOException when fetching channels: ${e.message}", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Generic Exception when fetching channels: ${e.message}", e)
            Result.failure(e)
        }
    }
}*/


/////////////////////////****************************************************************////////////////////////////////////////////////

class EPGRepositoryImpl(
    private val auidRepository: AUIDRepository,
    private val ipAddressHelper: IpAddressHelper,
    private val context: Context
) : EPGRepository {

    private val TAG = "EPGRepositoryImpl"
    //private val apiService: ApiService = RetrofitInstance.api

    override suspend fun getChannels(): Result<List<AppChannel>> {
        val auid = auidRepository.getAuidString()
        if (auid == null) {
            Log.e(TAG, "AUID is null, cannot fetch channels.")
            return Result.failure(Exception("AUID could not be retrieved."))
        }
        Log.d(TAG, "Retrieved AUID: $auid")

        val ipAddress = ipAddressHelper.getIpAddress(context)
        if (ipAddress == null || ipAddress.isEmpty()) {
            Log.e(TAG, "IP Address is null or empty, cannot fetch channels.")
            return Result.failure(Exception("IP Address could not be retrieved."))
        }
        Log.d(TAG, "Retrieved IP Address: $ipAddress")

        return try {
            val response = RetrofitInstance.api.getChannels(auidd = auid, ip = ipAddress, country = "USA")

            if (response.isSuccessful) {
                val serverChannels = response.body()
                if (serverChannels != null) {
                    Log.d(TAG, "Successfully fetched ${serverChannels.size} server channels.")
                    Log.d(TAG, "Channel fetch success code: ${response.code()}")

                    val appChannels = ChannelMapper.mapServerListToAppList(serverChannels)
                    Log.d(TAG, "Mapped to ${appChannels.size} app channels.")
                    Result.success(appChannels)
                } else {
                    Log.e(TAG, "Server channel list is null even though response was successful.")
                    Result.failure(Exception("Channel list is null in server response body."))
                }
            } else {
                val errorCode = response.code()
                val errorMsg = response.message()
                val errorBody = try { response.errorBody()?.string() } catch (e: Exception) { "Error reading error body." }
                Log.e(TAG, "Failed to fetch channels. Code: $errorCode, Message: $errorMsg, ErrorBody: $errorBody")
                Result.failure(Exception("Failed to fetch channels. HTTP $errorCode: $errorMsg"))
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network IOException when fetching channels: ${e.message}", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Generic Exception when fetching channels: ${e.message}", e)
            Result.failure(e)
        }
    }


    override suspend fun getPrograms(
        channels: List<AppChannel>,
        startEpoch: Long,
        endEpoch: Long?
    ): Result<List<AppProgram>> {

        val auid = auidRepository.getAuidString()
        if (auid == null) {
            Log.e(TAG, "AUID is null, cannot fetch programs.")
            return Result.failure(Exception("AUID could not be retrieved for programs."))
        }

        if (channels.isEmpty()) {
            Log.d(TAG, "Channel list for programs is empty.")
            return Result.success(emptyList())
        }

        val allChannelIds = channels.map { it.channelId }
        val allFetchedPrograms = mutableListOf<AppProgram>()
        var anyBatchSucceeded = false
        val batchSize = 20

        allChannelIds.chunked(batchSize).forEachIndexed { batchIndex, channelIdChunk ->
            val channelIdsString = channelIdChunk.joinToString(separator = ",")
            Log.d(TAG, "Fetching programs for batch ${batchIndex + 1}: $channelIdsString, startEpoch: $startEpoch, endEpoch: $endEpoch")

            try {

                val response = RetrofitInstance.api.getPrograms(
                    auidd = auid,
                    startEpoch = startEpoch,
                    channelIDs = channelIdsString,
                    endEpoch = endEpoch
                )

                if (response.isSuccessful) {
                    val channelsWithProgramsList = response.body()
                    if (channelsWithProgramsList != null) {
                        anyBatchSucceeded = true
                        for (channelData in channelsWithProgramsList) {
                            if (channelIdChunk.contains(channelData.channelId)) {
                                val appProgramsForChannel = ProgramMapper.mapServerListToAppList(
                                    serverPrograms = channelData.items,
                                    channelId = channelData.channelId
                                )
                                allFetchedPrograms.addAll(appProgramsForChannel)
                            }
                        }
                        Log.d(TAG, "Batch ${batchIndex + 1} success. Fetched programs for ${channelsWithProgramsList.size} channels in this batch.")
                    } else {
                        Log.w(TAG, "Program list body is null for batch ${batchIndex + 1} even though response was successful.")
                    }
                } else {
                    val errorCode = response.code()
                    val errorMsg = response.message()
                    val errorBody = try { response.errorBody()?.string() } catch (e: Exception) { "Error reading error body." }
                    Log.e(TAG, "Failed to fetch programs for batch ${batchIndex + 1}. Code: $errorCode, Message: $errorMsg, ErrorBody: $errorBody")
                }
            } catch (e: IOException) {
                Log.e(TAG, "Network IOException for batch ${batchIndex + 1} when fetching programs: ${e.message}", e)
            } catch (e: Exception) {
                Log.e(TAG, "Generic Exception for batch ${batchIndex + 1} when fetching programs: ${e.message}", e)
            }
        }

        return if (anyBatchSucceeded || allFetchedPrograms.isNotEmpty()) {
            Log.d(TAG, "Total programs fetched across all batches: ${allFetchedPrograms.size}")
            Result.success(allFetchedPrograms)
        } else {
            Log.e(TAG, "Failed to fetch programs for any batch.")
            Result.failure(Exception("Failed to fetch programs for all requested channels."))
        }
    }
}

