package com.example.epg.Presentation

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


class EPGViewModel(
    private val epgRepository: EPGRepository
) : ViewModel() {





    private val _channelState = MutableStateFlow<Resource<List<AppChannel>>>(Resource.Loading)
    val channelState: StateFlow<Resource<List<AppChannel>>> = _channelState.asStateFlow()


    private val _programState = MutableStateFlow<Resource<List<AppProgram>>>(Resource.Loading)
    val programState: StateFlow<Resource<List<AppProgram>>> = _programState.asStateFlow()

    private val TAG = "EPGViewModel"

    init {
        fetchChannelsAndInitialPrograms()
    }


    fun fetchChannelsAndInitialPrograms() {
        viewModelScope.launch {
            _channelState.value = Resource.Loading
            Log.d(TAG, "Fetching channels...")
            val channelsResult = epgRepository.getChannels()

            channelsResult.fold(
                onSuccess = { appChannelList ->
                    Log.d(TAG, "Channels fetched successfully: ${appChannelList.size} channels")
                    _channelState.value = Resource.Success(appChannelList)
                    if (appChannelList.isNotEmpty()) {
                        loadProgramsForChannels(appChannelList)
                    } else {
                        _programState.value = Resource.Success(emptyList())
                        Log.d(TAG, "Channel list is empty, no programs to fetch.")
                    }
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to fetch channels", error)
                    _channelState.value = Resource.Error(
                        message = error.message ?: "Nepoznata greška pri dobavljanju kanala.",
                        error = error
                    )

                    _programState.value = Resource.Error(
                        message = "Kanali nisu dostupni, ne mogu se dobaviti programi.",
                        error = error
                    )
                }
            )
        }
    }


    fun loadProgramsForChannels(channels: List<AppChannel>) {
        viewModelScope.launch {
            _programState.value = Resource.Loading
            Log.d(TAG, "Fetching programs for ${channels.size} channels...")
            // OBRISATI OVAJ DIO(TESTIRANJE) //
            val actualCurrentTimeForCalculation = System.currentTimeMillis()
            Log.d("EPGViewModel_TimeCheck", "Calculating startEpoch at (device time): ${java.util.Date(actualCurrentTimeForCalculation)}")
            val currentTimeSec = System.currentTimeMillis() / 1000
            val fourHoursInSec = TimeUnit.HOURS.toSeconds(4)
            val startEpoch = currentTimeSec - fourHoursInSec
            Log.d("EPGViewModel", "$startEpoch")
            Log.d("EPGViewModel", "$currentTimeSec")


            val endEpoch: Long? = null

            Log.d(TAG, "Program fetch range: startEpoch=$startEpoch, endEpoch=${endEpoch ?: "default (24h)"}")

            val programsResult = epgRepository.getPrograms(
                channels = channels,
                startEpoch = startEpoch,
                endEpoch = endEpoch
            )

            programsResult.fold(
                onSuccess = { appProgramList ->
                    Log.d(TAG, "Programs fetched successfully: ${appProgramList.size} programs")
                    appProgramList.take(20).forEach { prog ->
                        Log.d(TAG, "ViewModel Program: Title='${prog.title}', ProgramID='${prog.programId}', ChannelID='${prog.channelId}', StartEpoch='${prog.startTimeEpoch}'")
                    }
                    _programState.value = Resource.Success(appProgramList)
                },
                onFailure = { error ->
                    Log.e(TAG, "Failed to fetch programs", error)
                    _programState.value = Resource.Error(
                        message = error.message ?: "Nepoznata greška pri dobavljanju programa.",
                        error = error
                    )
                }
            )
        }
    }


    fun refreshAllData() {
        fetchChannelsAndInitialPrograms()
    }


}


class EPGViewModelFactory(
    private val epgRepository: EPGRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EPGViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EPGViewModel(epgRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}