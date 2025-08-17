package com.example.epg.Data.network

import android.util.Log
import androidx.media3.common.util.UnstableApi
import okhttp3.Interceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


object RetrofitInstance {
    //private const val BASE_URL = "https://stream-stage.aistrm.net:5200"
    private const val BASE_URL = "https://stream.aistrm.net"

    // Logging interceptor za ispis svih HTTP request/response informacija
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Prikazuje ceo response body
    }

    // OkHttp klijent sa interceptorom
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(KeyApiInterceptor())
        .build()

    // Retrofit instanca
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Dodaj klijent sa interceptorom
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

}






