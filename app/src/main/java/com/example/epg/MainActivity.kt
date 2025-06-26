package com.example.epg

//import androidx.tv.material3.Surface
///////////////////////////////////////////////

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.epg.Data.network.IpAddressHelper
import com.example.epg.Data.network.RetrofitInstance
import com.example.epg.Data.repository.AUIDRepository.AUIDRepository
import com.example.epg.Data.repository.AUIDRepository.AUIDRepositoryImpl
import com.example.epg.Data.repository.EPGRepository.EPGRepository
import com.example.epg.Data.repository.EPGRepository.EPGRepositoryImpl
import com.example.epg.Presentation.EPGViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import com.example.epg.Presentation.EPGScreen
import com.example.epg.Presentation.EPGViewModelFactory

class MainActivity : ComponentActivity() {

    private val epgViewModel: EPGViewModel by viewModels {
        val appContext = applicationContext
        val auidRepository = AUIDRepositoryImpl(context = appContext)
        val epgRepository = EPGRepositoryImpl(
            auidRepository = auidRepository,
            ipAddressHelper = IpAddressHelper,
            context = appContext
        )
        EPGViewModelFactory(epgRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //EPGScreen(viewModel = epgViewModel)
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.Magenta) // Koristimo jarku boju da bude očigledno
            ) {
                EPGScreen(viewModel = epgViewModel)
            }
        }
        }
    }

    /*override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event!!.repeatCount >0 && event!!.repeatCount % 3 == 0) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyLongPress(keyCode: Int, event: KeyEvent?): Boolean {
        return super.onKeyLongPress(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        return super.onKeyUp(keyCode, event)
    }*/


