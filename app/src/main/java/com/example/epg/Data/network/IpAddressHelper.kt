package com.example.epg.Data.network

import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import android.content.Context
import android.util.Log
import com.example.epg.Data.local.getPublicIpAddress
import com.example.epg.Data.local.savePublicIpAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient

object IpAddressHelper {

    // Tvoja postojeća promenljiva za keširanje u memoriji
    var publicIpAddress: String = ""


    /**
     * Privatna funkcija koja dobavlja javnu IP adresu sa udaljenog API-ja
     * i čuva je u SharedPreferences.
     * Sada je 'suspend' i koristi withContext za mrežni poziv.
     *
     * @param context The [Context] used to access shared preferences.
     * @return Fetched public IP address ili prazan string ako zahtev ne uspe (originalna logika).
     */
    private suspend fun fetchPublicIpAddress(context: Context): String { // Dodat 'suspend'
        // Tvoja originalna logika kreiranja klijenta svaki put
        val client = OkHttpClient.Builder().build()
        val request = Request.Builder()
            .url("https://api64.ipify.org?format=json")
            .build()

        return try {

            withContext(Dispatchers.IO) {
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val responseBody = response.body
                        val jsonIpString = responseBody?.string()
                        if (jsonIpString != null) {
                            try {
                                Log.d("IpAddressHelper", "jsonIP address response = $jsonIpString")
                                val jsonObject = JSONObject(jsonIpString)
                                val fetchedIp = jsonObject.getString("ip")

                                publicIpAddress = fetchedIp
                                context.savePublicIpAddress(fetchedIp)
                                Log.d("IpAddressHelper", "Public IP address = $fetchedIp")
                                fetchedIp
                            } catch (e: org.json.JSONException) {
                                Log.e("IpAddressHelper", "JSON parsing error: ${e.message}", e)
                                ""
                            }
                        } else {
                            Log.e("IpAddressHelper", "Response body was null")
                            ""
                        }
                    } else {
                        Log.e("IpAddressHelper", "Fetch IP request not successful: ${response.code} ${response.message}")
                        ""
                    }
                }
            }
        } catch (e: IOException) {
            Log.d( "IpAddressHelper", "Network Exception!!! ${e.message}")
            ""
        } catch (e: Exception) {
            Log.e( "IpAddressHelper", "Generic Exception fetching IP!!! ${e.message}")
            ""
        }

    }

    /**
     * Javna funkcija za dobijanje javne IP adrese.
     * Sada je 'suspend' da bi mogla da pozove suspend verziju fetchPublicIpAddress.
     * Originalna logika provere keša i SharedPreferences je uglavnom zadržana.
     *
     * @param context The [Context] used to access shared preferences.
     * @return Sačuvana ili dobavljena javna IP adresa, ili null ako dobavljanje nije uspelo.
     */
    suspend fun getIpAddress(context: Context): String? {
        if (publicIpAddress.isEmpty()) {
            val fetchedIp = fetchPublicIpAddress(context)
            return if (fetchedIp.isNotEmpty()) fetchedIp else null
        }

        return withContext(Dispatchers.IO) {
            context.getPublicIpAddress()
        }
    }
}













































/*object IpAddressHelper {

    // The public IP address retrieved from the remote API.
    var publicIpAddress: String = ""


    /**
     * Fetches the public IP address from a remote API and stores it in shared preferences.
     *
     * This method performs a network request to retrieve the public IP address from
     * the `https://api64.ipify.org?format=json` endpoint. If the network status is
     * no connection, it returns an empty string. Otherwise, it saves the IP address
     * to shared preferences and returns it.
     *
     * @param context The [Context] used to access shared preferences.
     * @return The fetched public IP address or an empty string if the request fails or there is no network connection.
     */
    private suspend fun fetchPublicIpAddress(context: Context): String {
        val client = OkHttpClient.Builder().build()
        val request = Request.Builder()
            .url("https://api64.ipify.org?format=json")
            .build()
        try {

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val responseBody = response.body
                    var jsonIp = responseBody?.string()
                    try {
                        Log.d("IpAddressHelper", "jsonIP address = $jsonIp")
                        val jsonObject = JSONObject(jsonIp)
                        publicIpAddress = jsonObject.getString("ip")

                        /**
                         * Save IP address in sharedPrefs so that it will be available for anokiOnDemand module
                         */
                        context.savePublicIpAddress( publicIpAddress)
                        Log.d("IpAddressHelper", "Public IP address = $publicIpAddress")
                        return publicIpAddress
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    return responseBody!!.string()
                }
            }
        } catch (e: IOException) {
            Log.d( "IpAddressHelper", "Exception!!! ${e.message} ${e.cause} ${e.toString()}")
            e.printStackTrace()
            return ""
        }
        return ""
    }

    /**
     * Retrieves the currently stored public IP address.
     *
     * @return The stored public IP address.
     */
    suspend fun getIpAddress(context: Context): String? {
        if (publicIpAddress.isEmpty()) {
            return fetchPublicIpAddress(context)
        }
        return context.getPublicIpAddress(context)
    }


}*/





//////**************************************************////
/*if (publicIpAddress.isEmpty()) {
        // Pokušaj prvo da pročitaš iz SharedPreferences ako je prazan memorijski keš
        val prefsIp = context.getPublicIpAddress() // Pretpostavka da ova funkcija postoji
        if (prefsIp != null && prefsIp.isNotEmpty()) {
            publicIpAddress = prefsIp // Ažuriraj memorijski keš
            return publicIpAddress
        }
        // Ako nema ni u SharedPreferences, onda dobavi sa mreže
        return fetchPublicIpAddressSuspend(context)
    }
    return publicIpAddress // Vrati iz memorijskog keša ako postoji*/