package com.example.epg.Data.repository.AUIDRepository

import android.util.Log
import android.content.Context
import com.example.epg.Data.network.ApiService
import com.example.epg.Data.network.RetrofitInstance
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AUIDRepositoryImpl(
    private val context: Context
) : AUIDRepository {

    private suspend fun getAdvertisingId(): String? {
        return try {
            withContext(Dispatchers.IO) {
                val adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context)
                adInfo?.id
            }
        } catch (e: Exception) {
            Log.e("AUIDRepositoryImpl", "Failed to get Advertising ID", e)
            null
        }
    }

    override suspend fun getAuidString(): String? {
        val deviceId = getAdvertisingId()

        if (deviceId == null) {
            Log.e("AUIDRepositoryImpl", "Device ID (AAID) could not be retrieved.")
            return null
        }

        Log.d("AUIDRepositoryImpl", "Retrieved AAID: $deviceId")

        return try {
            val response = RetrofitInstance.api.getAuid(deviceId)
            if (response.isSuccessful) {
                val auid = response.body()
                Log.d("AUIDRepositoryImpl", "Successfully retrieved AUID: $auid")
                auid
            } else {
                Log.e("AUIDRepositoryImpl", "Failed to get AUID. Code: ${response.code()}, Message: ${response.message()}")
                Log.e("AUIDRepositoryImpl", "Error body: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("AUIDRepositoryImpl", "Exception when trying to get AUID", e)
            null
        }
    }
}