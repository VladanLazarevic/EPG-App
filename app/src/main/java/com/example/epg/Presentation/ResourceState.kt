package com.example.epg.Presentation

    sealed class Resource<out T> {
        data class Success<out T>(val data: T) : Resource<T>()
        data class Error(val message: String, val error: Throwable? = null) : Resource<Nothing>()
        object Loading : Resource<Nothing>()
    }
