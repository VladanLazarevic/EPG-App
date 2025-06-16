package com.example.epg.Presentation

//import androidx.compose.foundation.shape.RoundedCornerShape // Potreban za RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.itemsIndexed
import androidx.tv.foundation.lazy.list.rememberTvLazyListState
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import android.util.Log
import kotlinx.coroutines.delay
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable // Import za focusable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.times
/*import androidx.tv.foundation.ExperimentalTvFoundationApi
import androidx.tv.foundation.lazy.list.TvLazyListState
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.items*/
import coil.request.ImageRequest

import com.example.epg.Domain.model.AppChannel
import com.example.epg.Domain.model.AppProgram
import com.example.epg.R
import com.example.epg.ui.theme.my_purple
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit



import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.draw.alpha


import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import com.example.epg.Data.local.getLastFocusedChannelId
import com.example.epg.Data.local.saveLastFocusedChannelId

// LAST //


private val EPG_SIDE_PADDING = 35.dp
private val EPG_CHANNEL_ITEM_WIDTH = 177.dp
private val EPG_PROGRAM_ROW_HEIGHT = 60.dp
private val DP_PER_MINUTE = 6.dp
private val SPACE_BETWEEN_CHANNEL_AND_PROGRAMS = 2.dp
private val FIXED_CARD_SPACING_DP = 0.dp

val gradientStartColor = Color(0xFF1A1C1E)


@Composable
fun EPGScreen(viewModel: EPGViewModel) {
    val channelState by viewModel.channelState.collectAsState()
    val programState by viewModel.programState.collectAsState()
    val epgWindowStartEpochSecondsFromVM by viewModel.epgWindowStartEpochSeconds.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color(0xFF1A1C1E)),
        ) {
            val isLoadingChannels = channelState is Resource.Loading
            val isLoadingProgramsOrEpoch = (channelState is Resource.Success && programState is Resource.Loading) ||
                    (channelState is Resource.Success && programState is Resource.Success && epgWindowStartEpochSecondsFromVM == null && !(channelState as Resource.Success<List<AppChannel>>).data.isNullOrEmpty())

            if (isLoadingChannels || isLoadingProgramsOrEpoch) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.tv_lottie_animation))
                    LottieAnimation(composition = composition, iterations = LottieConstants.IterateForever, modifier = Modifier.size(380.dp))
                    Spacer(modifier = Modifier.height(4.dp))
                    if (isLoadingChannels) Text("Loading channels...", color = Color.White)
                    else if (programState is Resource.Loading) Text("Loading programs...", color = Color.White)
                    else Text("Preparing EPG data...", color = Color.White)
                }
            } else if (channelState is Resource.Success && programState is Resource.Success && epgWindowStartEpochSecondsFromVM != null) {
                val channels = (channelState as Resource.Success<List<AppChannel>>).data
                val programs = (programState as Resource.Success<List<AppProgram>>).data
                val currentEpgWindowStartEpochSeconds = epgWindowStartEpochSecondsFromVM!!

                if (channels.isNullOrEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Nema dostupnih kanala.", color = Color.White)
                    }
                } else {
                    EpgContent(
                        channels = channels,
                        programs = programs,
                        viewModel = viewModel,
                        epgWindowStartEpochSeconds = currentEpgWindowStartEpochSeconds
                    )
                }
            } else if (channelState is Resource.Error) {
                val errorState = channelState as Resource.Error
                ErrorStateDisplay(message = "Greška pri učitavanju kanala: ${errorState.message}") { viewModel.fetchChannelsAndInitialPrograms() }
            } else if (programState is Resource.Error && channelState !is Resource.Loading) {
                val errorState = programState as Resource.Error
                ErrorStateDisplay(message = "Greška pri učitavanju programa: ${errorState.message}") {
                    if (channelState is Resource.Success) {
                        val currentChannels = (channelState as Resource.Success<List<AppChannel>>).data
                        if (!currentChannels.isNullOrEmpty()) viewModel.loadProgramsForChannels(currentChannels)
                        else viewModel.fetchChannelsAndInitialPrograms()
                    } else {
                        viewModel.fetchChannelsAndInitialPrograms()
                    }
                }
            } else if (channelState is Resource.Success && programState is Resource.Success && epgWindowStartEpochSecondsFromVM == null && !(channelState as Resource.Success<List<AppChannel>>).data.isNullOrEmpty()) {
                Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text("Waiting for EPG start time...", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun ErrorStateDisplay(message: String?, onRetry: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(message ?: "Nepoznata greška.", color = Color.Red, textAlign = TextAlign.Center)
        Button(onClick = onRetry, modifier = Modifier.padding(top = 16.dp)) { Text("Pokušaj ponovo") }
    }
}



@Composable
fun EpgContent(
    channels: List<AppChannel>,
    programs: List<AppProgram>,
    viewModel: EPGViewModel,
    epgWindowStartEpochSeconds: Long
) {
    val programsByChannelId = remember(programs, channels) {
        programs.groupBy { it.channelId }
            .mapValues { entry -> entry.value.sortedBy { it.startTimeEpoch } }
    }

    var focusedChannelLogoUrl by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val initialLastFocusedId = remember(channels) { if (channels.isNotEmpty()) context.getLastFocusedChannelId() else null }
    val targetChannelGlobalIndex = remember(channels, initialLastFocusedId) { if (initialLastFocusedId != null) channels.indexOfFirst { it.channelId == initialLastFocusedId }.takeIf { it != -1 } ?: 0 else 0 }
    val itemsAboveFocused = 2
    val indexForListTop = remember(targetChannelGlobalIndex) { (targetChannelGlobalIndex - itemsAboveFocused).coerceAtLeast(0) }
    val listState = rememberTvLazyListState(initialFirstVisibleItemIndex = indexForListTop)
    val focusRequesters = remember(channels) { channels.associateWith { FocusRequester() } }
    var initialFocusRequestedForId by remember { mutableStateOf<String?>(null) }


    val sharedHorizontalScrollState = rememberScrollState()

    val totalEpgWidth = remember(DP_PER_MINUTE) {
        (24 * 60 * DP_PER_MINUTE.value).dp
    }

    Box(modifier = Modifier.fillMaxSize()) {

        val imageOverallAlpha = 0.42f
        val imageFadeEdgeLength = 50.dp
        val imageFadeToColor = gradientStartColor
        val imageBoxHeight = 243.dp
        val imageBoxWidth = remember(imageBoxHeight) { (imageBoxHeight.value * 16 / 9).dp }

        focusedChannelLogoUrl?.let { logoUrl ->
            Box(
                modifier = Modifier.align(Alignment.TopEnd).width(imageBoxWidth).height(imageBoxHeight)
            ) {
                AsyncImage(model = logoUrl, contentDescription = "Pozadinska slika fokusiranog kanala", modifier = Modifier.matchParentSize().alpha(imageOverallAlpha), contentScale = ContentScale.FillBounds)
                Box(Modifier.align(Alignment.CenterStart).width(imageFadeEdgeLength).fillMaxHeight().background(brush = Brush.horizontalGradient(listOf(imageFadeToColor, Color.Transparent))))
                Box(Modifier.align(Alignment.BottomCenter).height(imageFadeEdgeLength).fillMaxWidth().background(brush = Brush.verticalGradient(listOf(Color.Transparent, imageFadeToColor))))
            }
        }


        Column(modifier = Modifier.fillMaxSize()) {
            TopHeader()
            TimelineHeader(
                globalTimelineStartEpochSeconds = epgWindowStartEpochSeconds,
                dpPerMinute = DP_PER_MINUTE,
                timelineHeight = 25.dp,
                horizontalScrollState = sharedHorizontalScrollState
            )


            TvLazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = EPG_SIDE_PADDING),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                itemsIndexed(channels, key = { _, channel -> channel.channelId }) { index, channel ->
                    val requester = focusRequesters[channel] ?: remember { FocusRequester() }
                    LaunchedEffect(initialLastFocusedId, channel.channelId, requester, listState.isScrollInProgress, initialFocusRequestedForId) {
                        if (channel.channelId == initialLastFocusedId && initialLastFocusedId != null && initialFocusRequestedForId != initialLastFocusedId && !listState.isScrollInProgress && index == targetChannelGlobalIndex) {
                            Log.d("EpgContent", "Requesting initial focus for channel: ${channel.name} (ID: ${channel.channelId}) at index $index")
                            requester.requestFocus()
                            initialFocusRequestedForId = initialLastFocusedId
                        }
                    }

                    EpgChannelRow(
                        channel = channel,
                        programsForThisChannel = programsByChannelId[channel.channelId] ?: emptyList(),
                        dpPerMinute = DP_PER_MINUTE,
                        rowHeight = EPG_PROGRAM_ROW_HEIGHT,
                        focusRequesterForChannel = requester,
                        onChannelFocusAndIdChanged = { isFocused, focusedChannelId, logoUrl ->
                            if (isFocused) { focusedChannelLogoUrl = logoUrl; context.saveLastFocusedChannelId(focusedChannelId) }
                            else { if (focusedChannelLogoUrl == logoUrl) focusedChannelLogoUrl = null }
                        },
                        globalTimelineStartEpochSeconds = epgWindowStartEpochSeconds,
                        horizontalScrollState = sharedHorizontalScrollState,
                        totalWidth = totalEpgWidth
                    )
                }
            }
        }
    }
}

@Composable
fun TimelineHeader(
    globalTimelineStartEpochSeconds: Long,
    dpPerMinute: Dp,
    timelineHeight: Dp,
    horizontalScrollState: ScrollState
) {
    val totalDurationMinutes = 24 * 60f
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(timelineHeight)
            .padding(start = EPG_SIDE_PADDING),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Spacer(modifier = Modifier.width(EPG_CHANNEL_ITEM_WIDTH + SPACE_BETWEEN_CHANNEL_AND_PROGRAMS))


        Row(
            modifier = Modifier.horizontalScroll(horizontalScrollState).width(8640.dp)
        ) {
            val minutesPerTick = 30
            val numberOfTicks = (totalDurationMinutes / minutesPerTick).toInt()
            val tickWidthDp = (minutesPerTick * dpPerMinute.value).dp
            val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

            for (i in 0..numberOfTicks - 1) {
                val currentTickTimeEpochSeconds = globalTimelineStartEpochSeconds + (i * minutesPerTick * 60L)
                val timeString = timeFormatter.format(Date(currentTickTimeEpochSeconds * 1000L))
                Box(modifier = Modifier.width(tickWidthDp).fillMaxHeight(), contentAlignment = Alignment.CenterStart) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        drawLine(color = Color.Gray.copy(alpha = 0.5f), start = Offset(0f, size.height * 0.5f), end = Offset(0f, size.height), strokeWidth = 1.dp.toPx())
                    }
                    Text(text = timeString, color = Color.White, fontSize = 10.sp, modifier = Modifier.padding(start = 4.dp))
                }
            }
        }
    }
}









@Composable
fun TopHeader() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 39.dp, start = 65.8.dp, end = 46.75.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painter = painterResource(id = R.drawable.iwedia_logo_white_02), contentDescription = "TV Guide Logo", modifier = Modifier.height(37.5.dp).width(45.91.dp))
        Spacer(modifier = Modifier.width(40.dp))
        Text(text = "TV Guide", style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 20.sp, color = Color.White))
        Spacer(modifier = Modifier.weight(1f))
        CurrentTimeText()
    }
}

@Composable
fun CurrentTimeText() {
    var currentTime by remember { mutableStateOf(getCurrentFormattedTime()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentTime = getCurrentFormattedTime()
        }
    }
    Text(text = currentTime, style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 24.sp, color = Color.White))
}

private fun getCurrentFormattedTime(): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

@Composable
fun ChannelItem( channel: AppChannel, modifier: Modifier = Modifier, focusRequester: FocusRequester, onFocusChangedAndIdCallback: (isFocused: Boolean, channelId: String) -> Unit) {
    var isFocusedState by remember { mutableStateOf(false) }
    val containerColor by animateColorAsState(if (isFocusedState) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f) else Color.Transparent, tween(100), label = "ChannelItemContainerColorFocus")
    val borderColor by animateColorAsState(if (isFocusedState) Color.White else Color.Transparent, tween(100), label = "ChannelItemBorderColorFocus")
    Card(
        modifier = modifier.focusRequester(focusRequester).onFocusChanged { focusState -> //
            isFocusedState = focusState.isFocused
            onFocusChangedAndIdCallback(focusState.isFocused, channel.channelId)
        }.focusable(true),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(if (isFocusedState) 0.05.dp else 0.dp, borderColor)
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(horizontal = 1.7.dp, vertical = 0.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = channel.name, style = MaterialTheme.typography.bodySmall, fontSize = 10.sp, color = if (isFocusedState) Color.White else Color.LightGray, modifier = Modifier.weight(1f), maxLines = 2, overflow = TextOverflow.StartEllipsis)
            Spacer(modifier = Modifier.width(1.2.dp))
            AsyncImage(model = channel.logo, contentDescription = channel.name, modifier = Modifier.height(55.dp).width(88.dp).clip(RoundedCornerShape(9.dp)), contentScale = ContentScale.FillBounds)
        }
    } // 54 je bilo
}




fun InProgressCardShape() = RoundedCornerShape(
    topStart = 0.dp,
    bottomStart = 0.dp,
    topEnd = 9.dp,
    bottomEnd = 9.dp
)

@Composable
fun EpgChannelRow(
    channel: AppChannel,
    programsForThisChannel: List<AppProgram>,
    dpPerMinute: Dp,
    rowHeight: Dp,
    focusRequesterForChannel: FocusRequester,
    onChannelFocusAndIdChanged: (isFocused: Boolean, channelId: String, logoUrl: String) -> Unit,
    globalTimelineStartEpochSeconds: Long,
    horizontalScrollState: ScrollState,
    totalWidth: Dp
) {

    var focusedProgramId by remember { mutableStateOf<String?>(null) }


    val firstProgramId = remember(programsForThisChannel, globalTimelineStartEpochSeconds) {
        programsForThisChannel.firstOrNull { (it.startTimeEpoch + (it.durationSec.toLong() ?: 0)) > globalTimelineStartEpochSeconds }?.programId
    }

    // ID poslednjeg programa koji počinje pre kraja prozora
    /*val lastProgramId = remember(programsForThisChannel, globalTimelineStartEpochSeconds) {
        val epgWindowEndEpochSeconds = globalTimelineStartEpochSeconds + (24 * 60 * 60)
        programsForThisChannel.lastOrNull { it.startTimeEpoch < epgWindowEndEpochSeconds }?.programId
    }*/

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(rowHeight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ChannelItem(
            channel = channel,
            modifier = Modifier.width(EPG_CHANNEL_ITEM_WIDTH).fillMaxHeight(),
            focusRequester = focusRequesterForChannel,
            onFocusChangedAndIdCallback = { isFocused, focusedChannelId ->
                onChannelFocusAndIdChanged(isFocused, focusedChannelId, channel.logo)
            }
        )

        Spacer(modifier = Modifier.width(SPACE_BETWEEN_CHANNEL_AND_PROGRAMS))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {

            Row(
                modifier = Modifier
                    .width(totalWidth)
                    .horizontalScroll(horizontalScrollState)
                    .onKeyEvent { event ->
                        if (event.type != KeyEventType.KeyDown) return@onKeyEvent false

                        when (event.key) {
                            Key.DirectionLeft -> {
                                if (focusedProgramId == firstProgramId) {
                                    focusRequesterForChannel.requestFocus()
                                    return@onKeyEvent true
                                }
                            }

                        }
                        false
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                var lastVisualElementEndTimeSeconds = globalTimelineStartEpochSeconds
                val epgWindowEndEpochSeconds = globalTimelineStartEpochSeconds + (24 * 60 * 60)

                for (program in programsForThisChannel) {
                    val programStartTimeSeconds = program.startTimeEpoch
                    val programDuration = program.durationSec ?: 0
                    val programEndTimeSeconds = programStartTimeSeconds + programDuration


                    if (programEndTimeSeconds <= globalTimelineStartEpochSeconds) {
                        continue
                    }


                    if (lastVisualElementEndTimeSeconds >= epgWindowEndEpochSeconds) {
                        break
                    }


                    val isInProgress = programStartTimeSeconds < globalTimelineStartEpochSeconds

                    if (isInProgress) {

                    } else {
                        val timeGapBeforeProgramSeconds = programStartTimeSeconds - lastVisualElementEndTimeSeconds
                        if (timeGapBeforeProgramSeconds > 0) {
                            val gapWidth =
                                ((timeGapBeforeProgramSeconds / 60f) * dpPerMinute.value).dp
                            Spacer(modifier = Modifier.width(gapWidth))
                        } else {
                            Spacer(modifier = Modifier.width(FIXED_CARD_SPACING_DP))
                        }
                    }


                    val startPoint = maxOf(programStartTimeSeconds, globalTimelineStartEpochSeconds)
                    val endPoint = minOf(programEndTimeSeconds, epgWindowEndEpochSeconds)
                    val durationToUse = (endPoint - startPoint).coerceAtLeast(0)

                    if (durationToUse <= 0) continue

                    val shapeToUse =
                        if (isInProgress) InProgressCardShape() else RoundedCornerShape(9.dp)


                    ConfigurableProgramCard(
                        program = program,
                        dpPerMinute = dpPerMinute,
                        height = rowHeight,
                        durationSec = durationToUse,
                        shape = shapeToUse,
                        onFocusChanged = { isFocused ->
                            if (isFocused) {
                                focusedProgramId = program.programId
                            }
                        }
                    )

                    lastVisualElementEndTimeSeconds = programEndTimeSeconds
                }
            }
        }

    }
}









@Composable
fun ConfigurableProgramCard(
    program: AppProgram,
    dpPerMinute: Dp,
    height: Dp,
    durationSec: Long,
    shape: Shape,
    onFocusChanged: (isFocused: Boolean) -> Unit
) {
    val programDurationMinutes = durationSec / 60f
    val programWidth = (programDurationMinutes * dpPerMinute.value).dp
    var isFocused by remember { mutableStateOf(false) }
    val baseFocusedColor = Color(0xFFB9B7B7)
    val baseUnfocusedColor = Color(0xFF1E2329)
    val cardAlpha = 0.53f
    val containerColor by animateColorAsState(targetValue = if (isFocused) baseFocusedColor.copy(alpha = 1f) else baseUnfocusedColor.copy(alpha = cardAlpha), animationSpec = tween(100), label = "ProgramCardContainerColorFocus")

    Card(
        modifier = Modifier
            .width(programWidth)
            .height(height - 4.dp)
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
                onFocusChanged(isFocused)
            }
            .focusable(true),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp, vertical = 2.dp), // Malo smanjen vertikalni padding
            verticalArrangement = Arrangement.Center
        ) {
            // 1. Tekst za naslov programa
            Text(
                text = program.title,
                style = MaterialTheme.typography.bodySmall,
                color = if (isFocused) Color.Black else Color.LightGray,
                fontSize = 10.sp,
                maxLines = 1, // Vraćeno na 1 da stane sve
                overflow = TextOverflow.Ellipsis
            )

            // NOVO: Prikaz trajanja u minutima
            val originalDurationInMinutes = (program.durationSec ?: 0) / 60
            val durationString = "$originalDurationInMinutes min"

            Text(
                text = durationString,
                style = MaterialTheme.typography.bodySmall,
                color = if (isFocused) Color.Black.copy(alpha = 0.7f) else Color.LightGray.copy(alpha = 0.8f),
                fontSize = 9.sp,
                maxLines = 1
            )

            // 3. Prikaz vremenskog opsega
            val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
            val startTimeString = remember(program.startTimeEpoch) {
                timeFormatter.format(Date(program.startTimeEpoch * 1000L))
            }
            val endTimeEpoch = program.startTimeEpoch + (program.durationSec ?: 0)
            val endTimeString = remember(endTimeEpoch) {
                timeFormatter.format(Date(endTimeEpoch * 1000L))
            }
            val timeRangeString = "$startTimeString - $endTimeString"

            Text(
                text = timeRangeString,
                style = MaterialTheme.typography.bodySmall,
                color = if (isFocused) Color.Black.copy(alpha = 0.7f) else Color.LightGray.copy(alpha = 0.8f),
                fontSize = 9.sp,
                maxLines = 1
            )
        }
    }
}




















