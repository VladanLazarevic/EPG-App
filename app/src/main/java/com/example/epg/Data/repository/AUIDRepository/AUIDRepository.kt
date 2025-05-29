package com.example.epg.Data.repository.AUIDRepository

interface AUIDRepository {
    suspend fun getAuidString(): String?
}