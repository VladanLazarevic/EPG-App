package com.example.epg.Domain.model

data class AppProgram(
    val channelId: String, // Da znamo kom kanalu pripada
    val programId: String, // Koristićemo contentId sa servera
    val title: String,
    val description: String?,
    val startTimeEpoch: Long, // Vreme početka kao Unix timestamp (sekunde)
    val durationSec: Int,     // Trajanje u sekundama
    val thumbnail: String?,   // URL do thumbnail-a
    val genre: String?,        // Žanr programa
    val startTime: String,    // Originalni startTime string sa servera
    val language: String?
) {
    // Izračunato polje za vreme završetka programa u sekundama od Unix epohe.
    val endTimeEpoch: Long
        get() = startTimeEpoch + durationSec
}