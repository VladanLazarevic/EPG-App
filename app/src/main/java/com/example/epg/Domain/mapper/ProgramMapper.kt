package com.example.epg.Domain.mapper

import androidx.core.text.HtmlCompat
import com.example.epg.Data.model.ServerProgram // Tvoj ServerProgram model (ProgramItem.kt)
import com.example.epg.Domain.model.AppProgram    // Tvoj AppProgram model

object ProgramMapper {

    private fun String.decodeHtml(): String =
        HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
    /**
     * Mapira jedan ProgramItem (ServerProgram) objekat u AppProgram objekat.
     * Zahteva channelId jer ProgramItem sam po sebi ne sadrži channelId.
     */
    fun mapServerToAppProgram(serverProgram: ServerProgram, channelId: String): AppProgram {
        return AppProgram(
            programId = serverProgram.contentId,
            channelId = channelId, // Prosleđujemo channelId
            title = serverProgram.title.decodeHtml(),
            description = serverProgram.description?.decodeHtml() ?: " ",
            startTime = serverProgram.startTime, // Mapiranje originalnog startTime stringa
            startTimeEpoch = serverProgram.startTimeEpoch,
            durationSec = serverProgram.durationSec,
            thumbnail = serverProgram.thumbnail,
            genre = serverProgram.genre?.decodeHtml(),
            language = serverProgram.language
            // Mapiraj ostala polja ako ih AppProgram bude imao
        )
    }

    /**
     * Mapira listu ProgramItem (ServerProgram) objekata u listu AppProgram objekata za dati channelId.
     */
    fun mapServerListToAppList(
        serverPrograms: List<ServerProgram>,
        channelId: String
    ): List<AppProgram> {
        return serverPrograms.map { mapServerToAppProgram(it, channelId) }
    }
}