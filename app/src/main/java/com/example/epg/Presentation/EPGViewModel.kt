package com.example.epg.Presentation

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.tv.foundation.lazy.list.TvLazyListState
import com.example.epg.Data.repository.EPGRepository.EPGRepository
import com.example.epg.Domain.model.AppChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.epg.Domain.model.AppProgram
import java.util.concurrent.TimeUnit
import androidx.compose.runtime.State
import com.example.epg.Data.local.FavoriteManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import java.util.Calendar


class EPGViewModel(
    private val epgRepository: EPGRepository,
    private val favoriteManager: FavoriteManager
): ViewModel() {


    private val _channelState = MutableStateFlow<Resource<List<AppChannel>>>(Resource.Loading)
    val channelState: StateFlow<Resource<List<AppChannel>>> = _channelState.asStateFlow()

    private val _programState =
        MutableStateFlow<Resource<Map<String, List<AppProgram>>>>(Resource.Loading)
    val programState: StateFlow<Resource<Map<String, List<AppProgram>>>> =
        _programState.asStateFlow()

    // NOVO: Stanje koje čuva program koji se trenutno pušta
    //private val _playingProgram = MutableStateFlow<AppProgram?>(null)
    //val playingProgram: StateFlow<AppProgram?> = _playingProgram.asStateFlow()

    private val _epgWindowStartEpochSeconds = MutableStateFlow<Long?>(null)
    val epgWindowStartEpochSeconds: StateFlow<Long?> = _epgWindowStartEpochSeconds.asStateFlow()

    private val TAG = "EPGViewModel"


    init {
        fetchChannelsAndInitialPrograms()
    }

    /*private fun getSnappedEpgStartTime(): Long {
        val calendar = Calendar.getInstance()


        val minutes = calendar.get(Calendar.MINUTE)
        if (minutes >= 30) {
            calendar.set(Calendar.MINUTE, 30)
        } else {
            calendar.set(Calendar.MINUTE, 0)
        }


        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)


        calendar.add(Calendar.MINUTE, -30)

        return calendar.timeInMillis / 1000
    }*/

    //new -> test

    private fun getSnappedEpgStartTime(): Long {
        val calendar = Calendar.getInstance()


        val minutes = calendar.get(Calendar.MINUTE)
        if (minutes < 30) {
            calendar.add(Calendar.HOUR_OF_DAY, -1)
        }

        calendar.set(Calendar.MINUTE, 30)

        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return calendar.timeInMillis / 1000
    }


    fun fetchChannelsAndInitialPrograms() {
        viewModelScope.launch {
            _channelState.value = Resource.Loading
            _epgWindowStartEpochSeconds.value = null
            Log.d(TAG, "Fetching channels...")
            val channelsResult = epgRepository.getChannels()

            channelsResult.fold(
                onSuccess = { appChannelList ->
                    Log.d(TAG, "Channels fetched successfully: ${appChannelList.size} channels")
                    Log.d("CHANNEL_ID_FINDER", "--- ALL CHANNELS ---")
                    appChannelList.forEach { channel ->
                        Log.d(
                            "CHANNEL_ID_FINDER",
                            "Channel_name: ${channel.name}, ID: ${channel.channelId}"
                        )
                    }
                    Log.d("CHANNEL_ID_FINDER", "--- END OF LIST ---")
                    _channelState.value = Resource.Success(appChannelList)
                    if (appChannelList.isNotEmpty()) {
                        loadProgramsForChannels(appChannelList)
                    } else {
                        _programState.value = Resource.Success(emptyMap())
                        Log.d(TAG, "Channel list is empty, no programs to fetch.")
                    }
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to fetch channels", error)
                    _channelState.value = Resource.Error(
                        message = error.message ?: "Unknown error getting channel.",
                        error = error
                    )

                    _programState.value = Resource.Error(
                        message = "Channels are not available, programs cannot be fetched.",
                        error = error
                    )
                }
            )
        }
    }


    private fun loadProgramsForChannels(channels: List<AppChannel>) {
        viewModelScope.launch {

            Log.d(
                "DISPATCHER_CHECK",
                "viewModelScope.launch START on thread: ${Thread.currentThread().name}"
            )

            _programState.value = Resource.Loading
            _epgWindowStartEpochSeconds.value = null
            Log.d(TAG, "Fetching programs for ${channels.size} channels...")
            val actualCurrentTimeForCalculation = System.currentTimeMillis()
            val currentTimeSec = actualCurrentTimeForCalculation / 1000
            val HoursInSec = TimeUnit.MINUTES.toSeconds(30)
            val startEpoch = getSnappedEpgStartTime()

            val endEpoch: Long? = null

            val programsResult = epgRepository.getPrograms(
                channels = channels,
                startEpoch = startEpoch,
                endEpoch = endEpoch
            )

            programsResult.fold(
                onSuccess = { appProgramList ->
                    // NOVO //
                    Log.d(
                        TAG,
                        "Programs fetched successfully. Original count: ${appProgramList.size}"
                    )

                    val finalProgramMap = withContext(Dispatchers.Default) {
                        Log.d(TAG, "Starting data processing on background thread...")


                        val filteredByDurationList =
                            appProgramList.filter { (it.durationSec ?: 0) >= 60 }


                        sanitizeAndGroupPrograms(filteredByDurationList)
                    }

                    Log.d(TAG, "Processing finished. Final program map created.")

                    _epgWindowStartEpochSeconds.value = startEpoch

                    _programState.value = Resource.Success(finalProgramMap)


                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to fetch programs", error)
                    _programState.value = Resource.Error(
                        message = error.message ?: "Unknown error fetching program.",
                        error = error
                    )
                    _epgWindowStartEpochSeconds.value = null
                }
            )
        }
    }


    private fun sanitizeAndGroupPrograms(programs: List<AppProgram>): Map<String, List<AppProgram>> {
        if (programs.isEmpty()) return emptyMap()

        val currentTimeSec = System.currentTimeMillis() / 1000


        val programsByChannel = programs.groupBy { it.channelId }
        val finalSanitizedMap = mutableMapOf<String, List<AppProgram>>()


        for ((channelId, channelPrograms) in programsByChannel) {
            if (channelPrograms.isEmpty()) continue


            val sortedPrograms = channelPrograms.sortedBy { it.startTimeEpoch }

            val sanitizedChannelList = mutableListOf<AppProgram>()
            sanitizedChannelList.add(sortedPrograms.first()) // Add first program in list

            for (i in 1 until sortedPrograms.size) {
                val currentProgram = sortedPrograms[i]
                val lastAddedProgram = sanitizedChannelList.last()
                val lastAddedProgramEndTime =
                    lastAddedProgram.startTimeEpoch + (lastAddedProgram.durationSec ?: 0)


                if (currentProgram.startTimeEpoch < lastAddedProgramEndTime) {
                    Log.w(
                        "EPG_SANITIZER",
                        "On channel $channelId, removing overlapping program: '${currentProgram.title}' starting at ${currentProgram.startTimeEpoch} because previous ends at $lastAddedProgramEndTime"
                    )
                    continue
                }
                sanitizedChannelList.add(currentProgram)
            }
            // Ovaj deo izračunava 'isLive' status za svaki program
            /*val listWithLiveStatus = sanitizedChannelList.map { program ->
                val programEndTime = program.startTimeEpoch + (program.durationSec ?: 0)
                val isCurrentlyLive = currentTimeSec >= program.startTimeEpoch && currentTimeSec < programEndTime
                program.copy(isLive = isCurrentlyLive)
            }*/

            finalSanitizedMap[channelId] = sanitizedChannelList
        }
        return finalSanitizedMap
    }


    fun refreshAllData() {
        fetchChannelsAndInitialPrograms()
    }


    fun onToggleFavorite(channelId: String) {
        viewModelScope.launch {

            epgRepository.toggleFavoriteStatus(channelId)

            (_channelState.value as? Resource.Success)?.data?.let { currentChannels ->
                val updatedChannels = currentChannels.map {
                    if (it.channelId == channelId) {
                        it.copy(isFavorite = !it.isFavorite)
                    } else {
                        it
                    }
                }
                _channelState.value = Resource.Success(updatedChannels)
            }
        }
    }

    // IZMENA: buildFinalUrl sada prima Context kao parametar
    /*private suspend fun buildFinalUrl(
        templateUrl: String,
        context: Context,
        playerWidth: Int,
        playerHeight: Int
    ): String {
        var finalUrl = templateUrl
        val appBundleId = context.packageName
        //val auid = auidRepository.getAuidString() ?: ""
        val auid = epgRepository.getAuid() ?: ""
        val advertisingId = epgRepository.getAdvertisingId() ?: auid// Privremeno rešenje

        finalUrl = finalUrl.replace("{{APP_BUNDLE_ID}}", URLEncoder.encode(appBundleId, "UTF-8"))
        finalUrl = finalUrl.replace("{{AUID}}", auid)
        finalUrl = finalUrl.replace("{{DID}}", advertisingId)
        finalUrl = finalUrl.replace("{{AAID}}", advertisingId)
        finalUrl = finalUrl.replace("{{DNT}}", "1")
        finalUrl = finalUrl.replace("{{US_PRIVACY}}", "1---")
        finalUrl = finalUrl.replace("{{GDPR}}", "0")
        finalUrl = finalUrl.replace("{{CONSENT}}", "")
        finalUrl = finalUrl.replace("{{PLAYER_WIDTH}}", playerWidth.toString())
        finalUrl = finalUrl.replace("{{PLAYER_HEIGHT}}", playerHeight.toString())
        finalUrl = finalUrl.replace("{{PLATID}}", "android_tv")
        return finalUrl
    }*/

    // IZMENA: onProgramClicked sada prima samo context
    /*fun onProgramClicked(
        program: AppProgram,
        playerWidth: Int,
        playerHeight: Int,
        context: Context
    ) {
        val currentTimeSec = System.currentTimeMillis() / 1000
        val programEndTimeSec = program.startTimeEpoch + (program.durationSec ?: 0)
        val isCurrentlyLive =
            currentTimeSec >= program.startTimeEpoch && currentTimeSec < programEndTimeSec

        if (isCurrentlyLive) {
            viewModelScope.launch {
                val allChannels = (_channelState.value as? Resource.Success)?.data
                val parentChannel = allChannels?.find { it.channelId == program.channelId }
                if (parentChannel != null) {
                    val finalUrl =
                        buildFinalUrl(parentChannel.playbackUrl, context, playerWidth, playerHeight)
                    Log.d("FINAL_URL_TEST", finalUrl)
                    val programForPlayback = program.copy(playbackURL = finalUrl)
                    _playingProgram.value = programForPlayback
                }
            }
        } else {
            Log.d(TAG, "Program '${program.title}' is not live, cannot play.")
            _playingProgram.value = null
        }
    }*/

}


class EPGViewModelFactory(
    private val epgRepository: EPGRepository,
    private val favoriteManager: FavoriteManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EPGViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EPGViewModel(epgRepository, favoriteManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}