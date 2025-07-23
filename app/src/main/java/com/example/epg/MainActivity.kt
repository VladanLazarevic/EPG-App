package com.example.epg

import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.epg.Data.local.FavoriteManager
import com.example.epg.Data.network.IpAddressHelper
import com.example.epg.Data.repository.AUIDRepository.AUIDRepositoryImpl
import com.example.epg.Data.repository.EPGRepository.EPGRepositoryImpl
import com.example.epg.Presentation.EPGViewModel

import com.example.epg.Presentation.EPGScreen
import com.example.epg.Presentation.EPGViewModelFactory

class MainActivity : ComponentActivity() {

    private val epgViewModel: EPGViewModel by viewModels {
        val appContext = applicationContext
        val auidRepository = AUIDRepositoryImpl(context = appContext)

        val favoriteManager = FavoriteManager(context = appContext)
        val epgRepository = EPGRepositoryImpl(
            auidRepository = auidRepository,
            ipAddressHelper = IpAddressHelper,
            context = appContext,
            favoriteManager = favoriteManager
        )
        EPGViewModelFactory(epgRepository, favoriteManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EPGScreen(viewModel = epgViewModel)
        }
    }
}




