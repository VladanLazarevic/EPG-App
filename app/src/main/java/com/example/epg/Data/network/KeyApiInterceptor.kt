package com.example.epg.Data.network

import okhttp3.Interceptor
import okhttp3.Response
import com.example.epg.BuildConfig

class KeyApiInterceptor: Interceptor {

    // The header name for the API key.
    private val KEY_HEADER = "X-API-KEY"

    /**
     * Intercepts the request and adds the API key to the request headers.
     *
     * @param chain The [Interceptor.Chain] that provides access to the request and response.
     * @return The [Response] after adding the API key to the headers and proceeding with the request.
     */
    override fun intercept (chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val response = originalRequest.newBuilder()
            .header(KEY_HEADER, BuildConfig.API_KEY)
            .build()

        return chain.proceed(response)
    }
}