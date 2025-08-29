package com.example.epg.Presentation

import android.app.Activity
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
//import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.focusable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.itemsIndexed
import androidx.tv.foundation.lazy.list.rememberTvLazyListState
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.epg.Data.local.getLastFocusedChannelId
import com.example.epg.Data.local.saveLastFocusedChannelId
import com.example.epg.Domain.model.AppChannel
import com.example.epg.Domain.model.AppProgram
import com.example.epg.R
import com.example.epg.ui.theme.BackgroundColor
import com.example.epg.ui.theme.FocusedProgramCardColor
import com.example.epg.ui.theme.UnfocusedProgramCardColor
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.animation.core.animateDpAsState
//import androidx.compose.foundation.layout.BoxScopeInstance.matchParentSize
//import androidx.compose.ui.draw.EmptyBuildDrawCacheParams.density
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.times
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import androidx.media3.ui.PlayerView
//import androidx.media3.ui.compose.PlayerSurface
//import androidx.media3.ui.PlayerView
//import androidx.media3.ui.PlayerView
//import androidx.media3.ui.PlayerView
// Potrebni importi za ovo rešenje
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.WindowManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.Player



import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.platform.LocalDensity



// U EPGScreen.kt, na vrhu
private val PlayingProgramCardColor = Color(0xFFA269FF)
private val EPG_SIDE_PADDING = 35.dp
private val EPG_CHANNEL_ITEM_WIDTH = 177.dp
private val EPG_PROGRAM_ROW_HEIGHT = 60.dp
private val DP_PER_MINUTE = 6.dp
private val SPACE_BETWEEN_CHANNEL_AND_PROGRAMS = 2.dp
private val FIXED_CARD_SPACING_DP = 0.dp
private val PROGRAM_DETAILS_HEIGHT = 200.dp
private val TOP_HEADER_HEIGHT = 76.5.dp
private val FILTER_MENU_WIDTH = 120.dp


fun inProgressCardShape() = RoundedCornerShape(
    topStart = 0.dp, bottomStart = 0.dp, topEnd = 9.dp, bottomEnd = 9.dp
)
fun cutOffEndCardShape() = RoundedCornerShape(
    topStart = 9.dp, bottomStart = 9.dp, topEnd = 0.dp, bottomEnd = 0.dp
)

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
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundColor),
        ) {
            val isLoading = channelState is Resource.Loading || (channelState is Resource.Success && programState is Resource.Loading)
            val isSuccess = channelState is Resource.Success && programState is Resource.Success && epgWindowStartEpochSecondsFromVM != null
            val isError = channelState is Resource.Error || programState is Resource.Error

            when {
                isLoading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.tv_lottie_animation))
                        LottieAnimation(
                            composition = composition,
                            iterations = LottieConstants.IterateForever,
                            modifier = Modifier.size(550.dp)
                        )
                    }
                }
                isSuccess -> {
                    val channels = (channelState as Resource.Success<List<AppChannel>>).data
                    val programsByChannelId = (programState as Resource.Success<Map<String, List<AppProgram>>>).data
                    val currentEpgWindowStartEpochSeconds = epgWindowStartEpochSecondsFromVM!!

                    if (channels.isNullOrEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No channels available.", color = Color.White)
                        }
                    } else {
                        EpgContent(
                            viewModel = viewModel,
                            channels = channels,
                            programsByChannelId = programsByChannelId,
                            epgWindowStartEpochSeconds = currentEpgWindowStartEpochSeconds
                        )
                    }
                }
                isError -> {
                    val errorMessage = (channelState as? Resource.Error)?.message ?: (programState as? Resource.Error)?.message
                    ErrorStateDisplay(message = "Error: $errorMessage") { viewModel.fetchChannelsAndInitialPrograms() }
                }
                else -> {
                    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text("Waiting for EPG data...", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorStateDisplay(message: String?, onRetry: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Text(message ?: "Unknown error.", color = Color.Red, textAlign = TextAlign.Center)
        Button(onClick = onRetry, modifier = Modifier.padding(top = 16.dp)) { Text("Pokušaj ponovo") }
    }
}

@Composable
fun TimelineHeader(
    globalTimelineStartEpochSeconds: Long,
    dpPerMinute: Dp,
    timelineHeight: Dp,
    horizontalScrollState: ScrollState,
    totalWidth: Dp
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(timelineHeight)
            .padding(start = EPG_SIDE_PADDING),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(EPG_CHANNEL_ITEM_WIDTH + SPACE_BETWEEN_CHANNEL_AND_PROGRAMS))
        Box(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier
                    .width(totalWidth)
                    .horizontalScroll(horizontalScrollState)
            ) {
                val totalDurationMinutes = 24 * 60f
                val minutesPerTick = 30
                val numberOfTicks = (totalDurationMinutes / minutesPerTick).toInt()
                val tickWidthDp = (minutesPerTick * dpPerMinute.value).dp
                val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

                for (i in 0 until numberOfTicks) {
                    val currentTickTimeEpochSeconds = globalTimelineStartEpochSeconds + (i * minutesPerTick * 60L)
                    val timeString = timeFormatter.format(Date(currentTickTimeEpochSeconds * 1000L))
                    Box(modifier = Modifier
                        .width(tickWidthDp)
                        .fillMaxHeight(), contentAlignment = Alignment.CenterStart) {
                        Canvas(modifier = Modifier.matchParentSize()) {
                            drawLine(color = Color.Gray.copy(alpha = 0.5f), start = Offset(0f, size.height * 0.5f), end = Offset(0f, size.height), strokeWidth = 1.dp.toPx())
                        }
                        Text(text = timeString, color = Color.White, fontSize = 10.sp, modifier = Modifier.padding(start = 4.dp))
                    }
                }
            }
        }
    }
}




@Composable
fun EpgChannelRow(
    viewModel: EPGViewModel,
    channel: AppChannel,
    programsForThisChannel: List<AppProgram>,
    dpPerMinute: Dp,
    rowHeight: Dp,
    focusRequesterForChannel: FocusRequester,
    onChannelFocusAndIdChanged: (isFocused: Boolean, channelId: String, logoUrl: String) -> Unit,
    globalTimelineStartEpochSeconds: Long,
    horizontalScrollState: ScrollState,
    totalWidth: Dp,
    onProgramFocused: (program: AppProgram?) -> Unit,
    currentTimeInEpochSeconds: Long,
    // NOVO: EpgChannelRow sada prima onKeyEvent
    onKeyEvent: (KeyEvent) -> Boolean
    //onProgramClicked: (AppProgram) -> Unit,
    //playingProgram: AppProgram?

) {

    val MAX_EMPTY_CHUNK_DURATION_SEC = 2 * 3600

    var focusedProgramId by remember { mutableStateOf<String?>(null) }
    var focusedProgramStartTimeEpoch by remember { mutableStateOf<Long?>(null)  }
    val firstVisibleProgram = remember(programsForThisChannel, globalTimelineStartEpochSeconds) {
        programsForThisChannel.firstOrNull { (it.startTimeEpoch + (it.durationSec ?: 0)) > globalTimelineStartEpochSeconds }
    }
    val firstProgramId = firstVisibleProgram?.programId
    val firstProgramStartTimeEpoch = firstVisibleProgram?.startTimeEpoch


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(rowHeight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ChannelItem(
            channel = channel,
            modifier = Modifier
                .width(EPG_CHANNEL_ITEM_WIDTH)
                .fillMaxHeight(),
            focusRequester = focusRequesterForChannel,
            onFocusChangedAndIdCallback = { isFocused, channelId ->
                onChannelFocusAndIdChanged(isFocused, channelId, channel.logo)
                // NOVO: Prosleđujemo fokus nazad u ViewModel
                //viewModel.onChannelItemFocusChanged(isFocused)
            },
            onFavoriteClick = { viewModel.onToggleFavorite(channel.channelId) },
            // NOVO: Prosleđujemo onKeyEvent parametar
            onKeyEvent = onKeyEvent

        )

        Spacer(modifier = Modifier.width(SPACE_BETWEEN_CHANNEL_AND_PROGRAMS))
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Row(
                modifier = Modifier
                    .focusGroup()
                    .width(totalWidth)
                    .horizontalScroll(horizontalScrollState)
                    .onKeyEvent { event ->
                        if (event.type != KeyEventType.KeyDown || event.key != Key.DirectionLeft) {
                            return@onKeyEvent false
                        }
                        // Proveravamo da li je prva stvar u redu "praznina" (No Info kartica)
                        val isFirstElementAGap = firstVisibleProgram != null &&
                                firstVisibleProgram.startTimeEpoch > globalTimelineStartEpochSeconds

                        // Proveravamo da li je fokus na prvom pravom programu
                        val isFocusOnFirstRealProgram = focusedProgramId == firstVisibleProgram?.programId && focusedProgramStartTimeEpoch == firstVisibleProgram?.startTimeEpoch

                        // Proveravamo da li je fokus na prvoj "No Info" kartici
                        val isFocusOnFirstGapCard = focusedProgramId == "gap_${globalTimelineStartEpochSeconds}"
                        if ((!isFirstElementAGap && isFocusOnFirstRealProgram) || (isFirstElementAGap && isFocusOnFirstGapCard)) {
                            focusRequesterForChannel.requestFocus()
                            return@onKeyEvent true
                        }
                        false

                        /*when (event.key) {
                            Key.DirectionLeft -> {
                                if (focusedProgramId == firstProgramId && focusedProgramStartTimeEpoch == firstProgramStartTimeEpoch) {
                                    focusRequesterForChannel.requestFocus()
                                    return@onKeyEvent true
                                }
                            }

                        }*/
                        //false
                    }
            ) {
                var lastVisualElementEndTimeSeconds = globalTimelineStartEpochSeconds
                val epgWindowEndEpochSeconds = globalTimelineStartEpochSeconds + (24 * 60 * 60)

                for (program in programsForThisChannel) {
                    /*Button(
                        onClick = { },
                        modifier = Modifier.onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                Log.d("FocusTest", "DUGME 1 JE DOBILO PUNI FOKUS!")
                            } else {
                                Log.d("FocusTest", "DUGME 1 JE IZGUBILO FOKUS!")
                            }
                        }

                        ) { Text("Test Dugme 1") }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { }) { Text("Test Dugme 2") }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { }) { Text("Test Dugme 3") }*/
                    val programStartTimeSeconds = program.startTimeEpoch
                    val programDuration = program.durationSec ?: 0
                    val programEndTimeSeconds = programStartTimeSeconds + programDuration
                    if (programEndTimeSeconds <= globalTimelineStartEpochSeconds) continue
                    if (lastVisualElementEndTimeSeconds >= epgWindowEndEpochSeconds) break

                    val isInProgress = programStartTimeSeconds < globalTimelineStartEpochSeconds
                    val endsAfterWindow = programEndTimeSeconds > epgWindowEndEpochSeconds

                    if (!isInProgress) {
                        val timeGapBeforeProgramSeconds =
                            programStartTimeSeconds - lastVisualElementEndTimeSeconds
                        if (timeGapBeforeProgramSeconds > 0) {
                            val gapWidth =
                                ((timeGapBeforeProgramSeconds / 60f) * dpPerMinute.value).dp
                            EmptyProgramCard(
                                width = gapWidth,
                                height = rowHeight,
                                onFocus = {onProgramFocused(null)}
                                )
                        } else {
                            Spacer(modifier = Modifier.width(FIXED_CARD_SPACING_DP))
                        }
                    }

                    val startPoint = maxOf(programStartTimeSeconds, globalTimelineStartEpochSeconds)
                    val endPoint = minOf(programEndTimeSeconds, epgWindowEndEpochSeconds)
                    val durationToUse = (endPoint - startPoint).coerceAtLeast(0)

                    if (durationToUse <= 0) continue

                    if (isInProgress && durationToUse < 60) {
                        continue
                    }
                    val shapeToUse = when {
                        isInProgress -> inProgressCardShape()
                        endsAfterWindow -> cutOffEndCardShape()
                        else -> RoundedCornerShape(9.dp)
                    }


                    ProgramCard(
                        program = program,
                        dpPerMinute = dpPerMinute,
                        height = rowHeight,
                        durationSec = durationToUse,
                        shape = shapeToUse,
                        onFocusChanged = { isFocused ->
                            if (isFocused) {
                                focusedProgramId = program.programId
                                focusedProgramStartTimeEpoch = program.startTimeEpoch
                                onProgramFocused(program)
                            }
                        },
                        currentTimeInEpochSeconds = currentTimeInEpochSeconds
                        //playingProgram = playingProgram,
                        //onClick = { onProgramClicked(program) }
                    )
                    lastVisualElementEndTimeSeconds = programEndTimeSeconds
                }



                if (lastVisualElementEndTimeSeconds < epgWindowEndEpochSeconds) {
                    var finalGapSec = epgWindowEndEpochSeconds - lastVisualElementEndTimeSeconds


                    while (finalGapSec > 0) {
                        val chunkDurationSec =
                            minOf(finalGapSec, MAX_EMPTY_CHUNK_DURATION_SEC.toLong())
                        val chunkWidthDp = ((chunkDurationSec / 60f) * dpPerMinute.value).dp

                        EmptyProgramCard(
                            width = chunkWidthDp,
                            height = rowHeight,
                            onFocus = { onProgramFocused(null)}
                        )

                        finalGapSec -= chunkDurationSec
                    }
                }
            }
        }
    }
}


@Composable
fun ProgramDetailsView(targetProgram: AppProgram) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(PROGRAM_DETAILS_HEIGHT)
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 33.dp, end = 46.dp, top = 24.dp, bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = targetProgram.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Bold
                )


                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    val dateFormatter = remember { SimpleDateFormat("EEE, dd/MM/yyyy", Locale.getDefault()) }
                    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
                    val dateString = remember(targetProgram.startTimeEpoch) { dateFormatter.format(Date(targetProgram.startTimeEpoch * 1000L)) }
                    val startTimeString = remember(targetProgram.startTimeEpoch) { timeFormatter.format(Date(targetProgram.startTimeEpoch * 1000L)) }
                    val endTimeEpoch = targetProgram.startTimeEpoch + (targetProgram.durationSec ?: 0)
                    val endTimeString = remember(endTimeEpoch) { timeFormatter.format(Date(endTimeEpoch * 1000L)) }
                    val timeRangeString = "$startTimeString - $endTimeString"
                    val originalDurationInMinutes = (targetProgram.durationSec ?: 0) / 60
                    val durationString = remember(originalDurationInMinutes) {
                        if (originalDurationInMinutes < 60) "$originalDurationInMinutes m"
                        else {
                            val hours = originalDurationInMinutes / 60
                            val minutes = originalDurationInMinutes % 60
                            if (minutes == 0) "${hours}h" else "${hours}h ${minutes}m"
                        }
                    }
                    val genreText = targetProgram.genre ?: ""
                    val metadataTextPart1 = "$dateString  $timeRangeString"
                    val metadataTextPart2 = "$durationString   |   $genreText"

                    Text(text = metadataTextPart1, style = MaterialTheme.typography.bodyMedium, color = Color.LightGray)
                    Text(text = "|", style = MaterialTheme.typography.bodyMedium, color = Color.LightGray)
                    Text(text = metadataTextPart2, style = MaterialTheme.typography.bodyMedium, color = Color.LightGray)
                    Spacer(modifier = Modifier.width(22.dp))
                    Image(painter = painterResource(id = R.drawable.icon_dolby), contentDescription = "dolby", modifier = Modifier.height(14.dp), alpha = 1f)
                    Image(painter = painterResource(id = R.drawable.icon_ad), contentDescription = "ad", modifier = Modifier.height(12.5.dp), alpha = 1f)
                    Image(painter = painterResource(id = R.drawable.icon_hd), contentDescription = "hd", modifier = Modifier.height(12.5.dp), alpha = 1f)
                    Spacer(modifier = Modifier.width(22.dp))
                    Image(painter = painterResource(id = R.drawable.icon_subtitles), contentDescription = "subtitles", modifier = Modifier.size(17.dp), alpha = 1f)
                    Text(text = "TRACK 1", style = MaterialTheme.typography.bodyMedium, color = Color.LightGray.copy(alpha = 1f), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(22.dp))
                    Image(painter = painterResource(id = R.drawable.icon_audio), contentDescription = "audio", modifier = Modifier.height(14.5.dp), alpha = 1f)

                    if (!targetProgram.language.isNullOrBlank()) {
                        Text(
                            text = targetProgram.language,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.LightGray.copy(alpha = 1f),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }


                Text(
                    text = targetProgram.description ?: "No description available.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis
                    //modifier = Modifier.alpha(0.25f)
                )
            }
        }
    }

}

@Composable
fun ProgramCard(
    //modifier: Modifier = Modifier,
    program: AppProgram,
    dpPerMinute: Dp,
    height: Dp,
    durationSec: Long,
    shape: Shape,
    onFocusChanged: (isFocused: Boolean) -> Unit,
    currentTimeInEpochSeconds: Long
    //playingProgram: AppProgram?,
    //onClick: () -> Unit,
) {
    val programDurationMinutes = durationSec / 60f
    val programWidth = (programDurationMinutes * dpPerMinute.value).dp

    var isFocused by remember { mutableStateOf(false) }
    // NOVO: Dinamička provera da li je program "live"
    val isCurrentlyLive = remember(program, currentTimeInEpochSeconds) {
        val programEndTime = program.startTimeEpoch + (program.durationSec ?: 0)
        currentTimeInEpochSeconds >= program.startTimeEpoch && currentTimeInEpochSeconds < programEndTime
    }

    // NOVO: Proveravamo da li je BAŠ OVA kartica ona koja se pušta
    //val isPlaying = playingProgram?.programId == program.programId && playingProgram?.channelId == program.channelId


    val cardAlpha = 0.53f
    /*val containerrColor by animateColorAsState(
        targetValue = when {
            isPlaying -> PlayingProgramCardColor.copy(alpha = 0.8f) // Ljubičasta ako se pušta
            isFocused -> FocusedProgramCardColor.copy(alpha = 1f)   // Bela/Siva ako je fokusirana
            else -> UnfocusedProgramCardColor.copy(alpha = cardAlpha) // Tamna ako nije
        },
        animationSpec = tween(200),
        label = "ProgramCardContainerColorFocus"
    )*/

    val containerrColor by animateColorAsState(
        targetValue = if (isFocused) FocusedProgramCardColor.copy(alpha = 1f) else UnfocusedProgramCardColor.copy(alpha = cardAlpha),
        animationSpec = tween(100),
        label = "ProgramCardContainerColorFocus"
    )

    val spacingWidth = 4.5.dp
    // Icon + Space
    val iconAreaWidth = 24.dp // 18icon + 6dp Space

    //slide right
    val textOffset by animateDpAsState(
        targetValue = if (isFocused && isCurrentlyLive) iconAreaWidth else 0.dp,
        ///targetValue = if (isPlaying) iconAreaWidth else 0.dp,
        animationSpec = tween(durationMillis = 200),
        label = "TextOffsetAnimation"
    )

    Card(
        modifier = Modifier
            //.clickable { onClick() }
            .width(programWidth)
            .height(height - 4.dp)
            .onFocusChanged { focusState ->
                // --- DODAJEMO LOG ISPIS OVDE ---
                Log.d("FocusTest", "ProgramCard '${program.title}' Focus State: ${focusState.isFocused}")
                // --- KRAJ DODATKA ---

                isFocused = focusState.isFocused
                onFocusChanged(isFocused)
            }
            .focusable(true),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = spacingWidth)
                .clip(shape)
                .background(color = containerrColor, shape = shape)
                .padding(horizontal = 6.dp, vertical = 4.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            androidx.compose.animation.AnimatedVisibility(
                //visible = isPlaying, //isFocused && isCurrentlyLive,
                visible = isFocused && isCurrentlyLive,
                enter = fadeIn(animationSpec = tween(150)),
                exit = fadeOut(animationSpec = tween(150))
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_play),
                    contentDescription = "Play",
                    tint = Color.Black,
                    //tint = if (isFocused) Color.Black else Color.LightGray,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = program.title,
                style = MaterialTheme.typography.bodySmall,
                color = if (isFocused) Color.Black else Color.LightGray,
                fontSize = 10.sp,
                fontWeight = if (isFocused && isCurrentlyLive) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.offset(x = textOffset)

            )
        }
    }
}



/*@Composable
fun EmptyProgramCard(width: Dp, height: Dp) {
    val spacingWidth = 4.dp


    Card(
        modifier = Modifier
            .width(width)
            .height(height - 4.dp)
            .focusable(true),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = null
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = spacingWidth)
                .background(
                    color = Color.Black.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(9.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color.Gray.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(9.dp)
                )
                .padding(horizontal = 6.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "No Information Available",
                style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                color = Color.Gray,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}*/
@Composable
fun EmptyProgramCard(width: Dp, height: Dp, onFocus: () -> Unit) {
    val spacingWidth = 4.5.dp


    var isFocused by remember { mutableStateOf(false) }


    val focusedColor = FocusedProgramCardColor.copy(alpha = 1f)//Color.DarkGray.copy(alpha = 0.6f)
    val unfocusedColor = Color.Black.copy(alpha = 0.2f) //UnfocusedProgramCardColor.copy(alpha = 0.53f)


    val animatedColor by animateColorAsState(
        targetValue = if (isFocused) focusedColor else unfocusedColor,
        animationSpec = tween(100)
    )

    /*val cardAlpha = 0.53f
    val containerrColor by animateColorAsState(
        targetValue = if (isFocused) FocusedProgramCardColor.copy(alpha = 1f) else UnfocusedProgramCardColor.copy(alpha = cardAlpha),
        animationSpec = tween(100),
        label = "ProgramCardContainerColorFocus"
    )*/

    Card(
        modifier = Modifier
            .width(width)
            .height(height - 4.dp)
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
                if(focusState.isFocused) {
                    onFocus()
                }
            }
            .focusable(false),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = null
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = spacingWidth)
                .background(
                    color = animatedColor,
                    shape = RoundedCornerShape(9.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color.Gray.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(9.dp)
                )
                .padding(horizontal = 6.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = "No Information Available",
                style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                //color = if (isFocused) Color.LightGray else Color.Gray,
                color = if (isFocused) Color.Black else Color.LightGray,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}



@Composable
fun TopHeader(currentTime: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 39.dp, start = 65.8.dp, end = 46.75.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painter = painterResource(id = R.drawable.iwedia_logo_white_02), contentDescription = "TV Guide Logo", modifier = Modifier
            .height(37.5.dp)
            .width(45.91.dp))
        Spacer(modifier = Modifier.width(40.dp))
        Text(text = "TV Guide", style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 20.sp, color = Color.White))
        Spacer(modifier = Modifier.weight(1f))
        CurrentTimeText(currentTime = currentTime)
    }
}

@Composable
fun CurrentTimeText(currentTime: String) {
    /*var currentTime by remember { mutableStateOf(getCurrentFormattedTime()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentTime = getCurrentFormattedTime()
        }
    }*/
    Text(text = currentTime, style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 24.sp, color = Color.White))
}

//private fun getCurrentFormattedTime(): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())




@Composable
fun ChannelItem(
    channel: AppChannel,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester,
    onFocusChangedAndIdCallback: (isFocused: Boolean, channelId: String) -> Unit,
    onFavoriteClick: () -> Unit,
    // NOVO: Dodajemo onKeyEvent za rad sa filterima
    onKeyEvent: (KeyEvent) -> Boolean,
) {
    var isFocusedState by remember { mutableStateOf(false) }
    val containerColor by animateColorAsState(if (isFocusedState) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f) else Color.Transparent, tween(100), label = "ChannelItemContainerColorFocus")
    val borderColor by animateColorAsState(if (isFocusedState) Color.White else Color.Transparent, tween(100), label = "ChannelItemBorderColorFocus")

    Card(
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged { focusState ->
                isFocusedState = focusState.isFocused
                onFocusChangedAndIdCallback(focusState.isFocused, channel.channelId)
            }
            .clickable { onFavoriteClick() }
            .onKeyEvent(onKeyEvent)
            .focusable(true),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(if (isFocusedState) 0.05.dp else 0.dp, borderColor)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 1.7.dp, vertical = 0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = channel.name,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 10.sp,
                    color = if (isFocusedState) Color.White else Color.LightGray,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.StartEllipsis
                )
                Spacer(modifier = Modifier.width(1.2.dp))
                AsyncImage(
                    model = channel.logo,
                    contentDescription = channel.name,
                    modifier = Modifier
                        .height(55.dp)
                        .width(88.dp)
                        .clip(RoundedCornerShape(9.dp)),
                    contentScale = ContentScale.FillBounds
                )
            }


            if (channel.isFavorite) {
                Icon(
                    painter = painterResource(id = R.drawable.heart_filled),
                    contentDescription = "Favorite",
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 0.dp, end = 0.dp)
                        .size(26.dp),
                    tint = Color(0xFFA269FF)

                )
            }
        }
    }
}




///////////////////////// TEST/////////////////////////////
@Composable
fun EpgContent(
    viewModel: EPGViewModel,
    channels: List<AppChannel>,
    programsByChannelId: Map<String, List<AppProgram>>,
    epgWindowStartEpochSeconds: Long
) {

    // NOVO //

    //val focusRequesters = remember(channels) { channels.associateWith { FocusRequester() } }
    // IZMENJENO: Mapiranje FocusRequestera po channelId (stabilan ključ)
    val focusRequesters = remember(channels) {
        channels.associateBy({ it.channelId }, { FocusRequester() })
    }

    val allFilterRequester = remember { FocusRequester() }
    val favoritesFilterRequester = remember { FocusRequester() }
    val channelListRequester = remember { FocusRequester() }
    val isInitialFocusRequested = remember { mutableStateOf(false) }
    var isTransitioning by remember { mutableStateOf(false) }



    val context = LocalContext.current
    var currentTimeInEpochSeconds by remember { mutableStateOf(System.currentTimeMillis() / 1000) }
    LaunchedEffect(Unit) {
        while (true) {
            currentTimeInEpochSeconds = System.currentTimeMillis() / 1000
            delay(1000L)
        }
    }
    val formattedCurrentTime = remember(currentTimeInEpochSeconds) {
        SimpleDateFormat(
            "HH:mm",
            Locale.getDefault()
        ).format(Date(currentTimeInEpochSeconds * 1000L))
    }
    var imageUrlForTopRight by remember { mutableStateOf<String?>(null) }
    val initialLastFocusedId =
        remember(channels) { if (channels.isNotEmpty()) context.getLastFocusedChannelId() else null }
    val targetChannelGlobalIndex = remember(channels, initialLastFocusedId) {
        if (initialLastFocusedId != null) channels.indexOfFirst { it.channelId == initialLastFocusedId }
            .takeIf { it != -1 } ?: 0 else 0
    }
    val itemsAboveFocused = 2
    val indexForListTop = remember(targetChannelGlobalIndex) {
        (targetChannelGlobalIndex - itemsAboveFocused).coerceAtLeast(0)
    }
    val listState = rememberTvLazyListState(initialFirstVisibleItemIndex = indexForListTop)
    //val focusRequesters = remember(channels) { channels.associateWith { FocusRequester() } }
    var initialFocusRequestedForId by remember { mutableStateOf<String?>(null) }
    val sharedHorizontalScrollState = rememberScrollState()
    val totalEpgWidth = remember(DP_PER_MINUTE) { (24 * 60 * DP_PER_MINUTE.value).dp }
    var focusedProgram by remember { mutableStateOf<AppProgram?>(null) }
    var animatedProgramState by remember { mutableStateOf<AppProgram?>(null) }


    val isFilterMenuVisible by viewModel.isFilterMenuVisible.collectAsState()
    val isChannelItemFocused by viewModel.isChannelItemFocused.collectAsState()
    val currentFilter by viewModel.currentFilter.collectAsState()

    val filteredChannels by remember(channels, currentFilter) {
        derivedStateOf {
            when (currentFilter) {
                FilterType.ALL -> channels
                FilterType.FAVORITES -> channels.filter { it.isFavorite }
            }
        }
    }


    //NEW

    val epgContentOffsetX by animateDpAsState(
        targetValue = if (isFilterMenuVisible) FILTER_MENU_WIDTH else 0.dp,
        animationSpec = tween(300),
        label = "epgContentOffset"
    )

    var isListReady by remember { mutableStateOf(true) }
    var isInitialFilterFocusDone by remember { mutableStateOf(false) }




    // CHANGE FILTER //
    LaunchedEffect(currentFilter) {
        if (!isInitialFilterFocusDone) {
            return@LaunchedEffect
        }

        isListReady = false
        delay(1)
        val lastFocusedId = context.getLastFocusedChannelId()
        if (lastFocusedId != null) {
            val targetIndex = filteredChannels.indexOfFirst { it.channelId == lastFocusedId }
            val itemsAboveFocused = 2
            val indexToScrollTo = (targetIndex - itemsAboveFocused).coerceAtLeast(0)


            if (targetIndex != -1) {
                listState.scrollToItem(indexToScrollTo)

                //delay(500)
                //delay(100)
                // Opciono: Vratite fokus na kanal nakon skrolovanja
                /*val targetChannel = filteredChannels[targetIndex]
                focusRequesters[targetChannel.channelId]?.requestFocus()*/
            } else {
                listState.scrollToItem(0)
            }
        }
        isListReady = true
    }

    // VISIBLE MENU //
    LaunchedEffect(isFilterMenuVisible) {
        if (isFilterMenuVisible) {
            delay(100)
            if (currentFilter == FilterType.ALL) {
                allFilterRequester.requestFocus()
            } else if (currentFilter == FilterType.FAVORITES) {
                favoritesFilterRequester.requestFocus()
            }
        }
    }



    var isFirstFocusFromMenu by remember { mutableStateOf(true) }

    // INVISIBLE MENU //
    LaunchedEffect(isFilterMenuVisible) {
        if (!isFilterMenuVisible && isInitialFilterFocusDone) {

            delay(100)
            val lastFocusedChannelId = context.getLastFocusedChannelId()
            val targetChannel = filteredChannels.find { it.channelId == lastFocusedChannelId }

            val channelToFocus = targetChannel ?: filteredChannels.firstOrNull()
            if (channelToFocus != null) {
                val targetIndex = filteredChannels.indexOf(channelToFocus)
                if (targetIndex != -1) {
                    focusRequesters[channelToFocus.channelId]?.requestFocus()
                }
            }

            isFirstFocusFromMenu = false
        }

        if (!isFilterMenuVisible) {
            isInitialFilterFocusDone = true
        }
    }



    val filterRequesters = mapOf(
        FilterType.ALL to allFilterRequester,
        FilterType.FAVORITES to favoritesFilterRequester
    )

    LaunchedEffect(focusedProgram) {
        delay(100L)
        animatedProgramState = focusedProgram
    }

    BackHandler(enabled = focusedProgram != null || isFilterMenuVisible) {
        if (isFilterMenuVisible) {
            viewModel.toggleFilterMenu(false)
            //channelListRequester.requestFocus()
        } else {
            val targetRequester = channels
                .find { it.channelId == focusedProgram?.channelId }
                ?.let { focusRequesters[it.channelId] } // DODATO
            targetRequester?.requestFocus()
        }
    }

    val topContentHeight by animateDpAsState(
        targetValue = if (animatedProgramState != null) PROGRAM_DETAILS_HEIGHT else TOP_HEADER_HEIGHT,
        animationSpec = tween(1000),
        label = "TopContentHeightAnimation"
    )
    Box(modifier = Modifier.fillMaxSize()) {
        val imageOverallAlpha = 0.42f
        val imageFadeEdgeLength = 50.dp
        val imageFadeToColor = BackgroundColor
        val imageBoxHeight = 280.dp
        val imageBoxWidth = remember(imageBoxHeight) { (imageBoxHeight.value * 16 / 9).dp }

        imageUrlForTopRight?.let { imageUrl ->
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .width(imageBoxWidth)
                    .height(imageBoxHeight)
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Pozadinska slika",
                    modifier = Modifier
                        .matchParentSize()
                        .alpha(imageOverallAlpha),
                    contentScale = ContentScale.Fit
                )
                Box(
                    Modifier
                        .align(Alignment.CenterStart)
                        .width(imageFadeEdgeLength)
                        .fillMaxHeight()
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(
                                    imageFadeToColor,
                                    Color.Transparent
                                )
                            )
                        )
                )
                Box(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .height(imageFadeEdgeLength)
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    imageFadeToColor
                                )
                            )
                        )
                )
            }
        }

        AnimatedVisibility(
            visible = isFilterMenuVisible,
            enter = fadeIn(tween(300)),
            exit = fadeOut(tween(300)),
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            FilterMenu(
                currentFilter = currentFilter,
                onFilterSelected = { filter ->
                    viewModel.onFilterSelected(filter)
                },
                onFocused = {
                    viewModel.onChannelItemFocusChanged(false)
                },
                /*onKeyEvent = { event ->
                    if (event.type == KeyEventType.KeyDown && event.key == Key.DirectionRight) {
                        viewModel.toggleFilterMenu(false)
                        //channelListRequester.requestFocus()
                        return@onKeyEvent true
                    }
                    return@onKeyEvent false
                },*/
                onKeyEvent = { event ->
                    when (event.key) {
                        Key.DirectionRight -> {
                            if (event.type == KeyEventType.KeyDown) {
                                val channeltofocus = filteredChannels.firstOrNull()
                                if (channeltofocus != null) {
                                    viewModel.toggleFilterMenu(false)
                                    true
                                } else {
                                    false
                                }

                                //viewModel.toggleFilterMenu(false)
                                //true
                            } else {
                                false
                            }
                        }
                        else -> false
                    }
                }
                /*onKeyEvent = { event ->
                    when (event.key) {
                        Key.DirectionRight -> {
                            if (event.type == KeyEventType.KeyDown) {
                                viewModel.toggleFilterMenu(false)
                                // ODMAH POSTAVI FOKUS NA KANAL NAKON ZATVARANJA MENIJA
                                val lastFocusedChannelId = context.getLastFocusedChannelId()
                                val channelToFocus = filteredChannels.find { it.channelId == lastFocusedChannelId } ?: filteredChannels.firstOrNull()
                                if (channelToFocus != null) {
                                    focusRequesters[channelToFocus.channelId]?.requestFocus()
                                }
                                true
                            } else {
                                false
                            }
                        }
                        else -> false
                    }
                }*/,
                allFilterRequester = allFilterRequester,
                favoritesFilterRequester = favoritesFilterRequester
            )
        }
/*hocu da mi napravi jednu izmenu, kada udjem u FAVORITES i kada nemam nijednog kanala tamo , da kada kliknem DESNO sa filter Menu-ja, da mi ne nestane filter menu, jer nemam sta da fokusiram, zellim da se nista ne desi kada kliknem DESNO u tom scenariju*/
        //val isInitialFocusRequested = remember { mutableStateOf(false) }
        val isLeftArrowVisible = isChannelItemFocused && !isFilterMenuVisible
        /*val isLeftArrowVisible by remember(isChannelItemFocused, isFilterMenuVisible, isInitialFocusRequested.value) {
            derivedStateOf {
                // Prikazuje se ako je kanal fokusiran I ako meni nije vidljiv.
                // Ažurira se i kada se početni fokus postavi.
                isChannelItemFocused && !isFilterMenuVisible
            }
        }*/

        val arrowOffset by animateDpAsState(
            targetValue = if (isFilterMenuVisible) FILTER_MENU_WIDTH else 0.dp,
            animationSpec = tween(300),
            label = "arrowOffset"
        )
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(tween(150)),
            exit = fadeOut(tween(150)),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = arrowOffset)
                .graphicsLayer {
                    rotationY = if (isFilterMenuVisible) 180f else 0f // Rotiraj za 180 stepeni kada je meni otvoren
                }
        ) {
            /*Text(
                text = "<<",
                fontSize = 30.sp,
                color = Color(0xFFA269FF).copy(alpha = 0.75f),
                modifier = Modifier.padding(start = 0.dp)
            )*/
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Open Filter Menu",
                tint = Color(0xFFA269FF).copy(alpha = 0.75f),
                modifier = Modifier
                    .size(47.dp)
                    .padding(start = 0.dp)
            )
        }


        ///TESTIRANJE//
        AnimatedContent(
            targetState = animatedProgramState,
            transitionSpec = {
                if (targetState != null && initialState == null) {
                    (slideInVertically(animationSpec = tween(1000)) { height -> -height / 2 } + fadeIn(
                        animationSpec = tween(1000)
                    ))
                        .togetherWith(slideOutVertically(animationSpec = tween(1000)) { height -> -height / 2 } + fadeOut(
                            animationSpec = tween(1000)
                        ))
                } else if (targetState == null && initialState != null) {
                    fadeIn(animationSpec = tween(1000))
                        .togetherWith(fadeOut(animationSpec = tween(1000)))
                } else {
                    fadeIn(animationSpec = tween(1))
                        .togetherWith(fadeOut(animationSpec = tween(1)))
                }.using(
                    SizeTransform(clip = false)
                )
            },
            label = "HeaderDetailsTransition"
        ) { animatedProgramState ->
            if (animatedProgramState != null) {
                ProgramDetailsView(targetProgram = animatedProgramState)
            } else {
                TopHeader(currentTime = formattedCurrentTime)
            }
        }
        // ISPRAVLJENO: Glavni kontejner za animirani sadržaj
        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = epgContentOffsetX)
                .padding(top = topContentHeight)
        ) {
            /*AnimatedContent(
                targetState = animatedProgramState,
                transitionSpec = {
                    if (targetState != null && initialState == null) {
                        (slideInVertically(animationSpec = tween(1000)) { height -> -height / 2 } + fadeIn(
                            animationSpec = tween(1000)
                        ))
                            .togetherWith(slideOutVertically(animationSpec = tween(1000)) { height -> -height / 2 } + fadeOut(
                                animationSpec = tween(1000)
                            ))
                    } else if (targetState == null && initialState != null) {
                        fadeIn(animationSpec = tween(1000))
                            .togetherWith(fadeOut(animationSpec = tween(1000)))
                    } else {
                        fadeIn(animationSpec = tween(1))
                            .togetherWith(fadeOut(animationSpec = tween(1)))
                    }.using(
                        SizeTransform(clip = false)
                    )
                },
                label = "HeaderDetailsTransition"
            ) { animatedProgramState ->
                if (animatedProgramState != null) {
                    ProgramDetailsView(targetProgram = animatedProgramState)
                } else {
                    TopHeader(currentTime = formattedCurrentTime)
                }
            }*/

            TimelineHeader(
                globalTimelineStartEpochSeconds = epgWindowStartEpochSeconds,
                dpPerMinute = DP_PER_MINUTE,
                timelineHeight = 25.dp,
                horizontalScrollState = sharedHorizontalScrollState,
                totalWidth = totalEpgWidth
            )

            // TESTIRANJE //
            AnimatedVisibility(
                visible = isListReady,
                enter = fadeIn(animationSpec = tween(1)),
                exit = fadeOut(animationSpec = tween(1))
            ) {
                TvLazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = EPG_SIDE_PADDING),
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    itemsIndexed(
                        filteredChannels,
                        key = { _, channel -> channel.channelId }) { index, channel ->
                        /*DODATO*/   val requester = focusRequesters[channel.channelId] ?: remember { FocusRequester() }
                        LaunchedEffect(
                            initialLastFocusedId,
                            channel.channelId,
                            requester,
                            listState.isScrollInProgress,
                            initialFocusRequestedForId
                        ) {
                            if (channel.channelId == initialLastFocusedId && initialLastFocusedId != null && initialFocusRequestedForId != initialLastFocusedId && !listState.isScrollInProgress && index == targetChannelGlobalIndex) {
                                requester.requestFocus()
                                initialFocusRequestedForId = initialLastFocusedId
                                //isInitialFocusRequested.value = true
                                // DODAJTE OVE DVE LINIJE
                                delay(50)
                                viewModel.onChannelItemFocusChanged(true)

                            }
                        }
                        EpgChannelRow(
                            viewModel = viewModel,
                            channel = channel,
                            programsForThisChannel = programsByChannelId[channel.channelId]
                                ?: emptyList(),
                            dpPerMinute = DP_PER_MINUTE,
                            rowHeight = EPG_PROGRAM_ROW_HEIGHT,
                            focusRequesterForChannel = requester,
                            onChannelFocusAndIdChanged = { isFocused, focusedChannelId, logoUrl ->
                                if (isFocused) {
                                    imageUrlForTopRight = logoUrl
                                    context.saveLastFocusedChannelId(focusedChannelId)
                                    focusedProgram = null
                                }
                                // POZOVITE viewModel.onChannelItemFocusChanged OVDE
                                viewModel.onChannelItemFocusChanged(isFocused)
                            },
                            globalTimelineStartEpochSeconds = epgWindowStartEpochSeconds,
                            horizontalScrollState = sharedHorizontalScrollState,
                            totalWidth = totalEpgWidth,
                            onProgramFocused = { program ->
                                focusedProgram = program
                                val channelForProgram =
                                    channels.find { it.channelId == program?.channelId }
                                imageUrlForTopRight = program?.thumbnail ?: channelForProgram?.logo
                            },
                            currentTimeInEpochSeconds = currentTimeInEpochSeconds,
                            /*onKeyEvent = { event ->
                                if (isChannelItemFocused && event.type == KeyEventType.KeyDown && event.key == Key.DirectionLeft) {
                                    viewModel.toggleFilterMenu(true)
                                    //allFilterRequester.requestFocus()
                                    true
                                }
                                false
                            }*/
                            onKeyEvent = { event ->
                                if (event.type == KeyEventType.KeyDown && event.key == Key.DirectionLeft) {
                                    if (isChannelItemFocused) {
                                        viewModel.toggleFilterMenu(true)
                                        true
                                    }
                                    else{
                                        false
                                    }
                                }else{
                                    false
                                }

                            }

                        )
                    }
                }
            }

            //----------------------------------------------------------------------//

            //----------------------------------------------------------------------//

            /*TvLazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = EPG_SIDE_PADDING),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                itemsIndexed(
                    filteredChannels,
                    key = { _, channel -> channel.channelId }) { index, channel ->
       /*DODATO*/   val requester = focusRequesters[channel.channelId] ?: remember { FocusRequester() }
                    LaunchedEffect(
                        initialLastFocusedId,
                        channel.channelId,
                        requester,
                        listState.isScrollInProgress,
                        initialFocusRequestedForId
                    ) {
                        if (channel.channelId == initialLastFocusedId && initialLastFocusedId != null && initialFocusRequestedForId != initialLastFocusedId && !listState.isScrollInProgress && index == targetChannelGlobalIndex) {
                            requester.requestFocus()
                            initialFocusRequestedForId = initialLastFocusedId
                            //isInitialFocusRequested.value = true
                            // DODAJTE OVE DVE LINIJE
                            delay(50)
                            viewModel.onChannelItemFocusChanged(true)

                        }
                    }
                    EpgChannelRow(
                        viewModel = viewModel,
                        channel = channel,
                        programsForThisChannel = programsByChannelId[channel.channelId]
                            ?: emptyList(),
                        dpPerMinute = DP_PER_MINUTE,
                        rowHeight = EPG_PROGRAM_ROW_HEIGHT,
                        focusRequesterForChannel = requester,
                        onChannelFocusAndIdChanged = { isFocused, focusedChannelId, logoUrl ->
                            if (isFocused) {
                                imageUrlForTopRight = logoUrl
                                context.saveLastFocusedChannelId(focusedChannelId)
                                focusedProgram = null
                            }
                            // POZOVITE viewModel.onChannelItemFocusChanged OVDE
                            viewModel.onChannelItemFocusChanged(isFocused)
                        },
                        globalTimelineStartEpochSeconds = epgWindowStartEpochSeconds,
                        horizontalScrollState = sharedHorizontalScrollState,
                        totalWidth = totalEpgWidth,
                        onProgramFocused = { program ->
                            focusedProgram = program
                            val channelForProgram =
                                channels.find { it.channelId == program?.channelId }
                            imageUrlForTopRight = program?.thumbnail ?: channelForProgram?.logo
                        },
                        currentTimeInEpochSeconds = currentTimeInEpochSeconds,
                        /*onKeyEvent = { event ->
                            if (isChannelItemFocused && event.type == KeyEventType.KeyDown && event.key == Key.DirectionLeft) {
                                viewModel.toggleFilterMenu(true)
                                //allFilterRequester.requestFocus()
                                true
                            }
                            false
                        }*/
                        onKeyEvent = { event ->
                            if (event.type == KeyEventType.KeyDown && event.key == Key.DirectionLeft) {
                                if (isChannelItemFocused) {
                                    viewModel.toggleFilterMenu(true)
                                    true
                                }
                                else{
                                    false
                                }
                            }else{
                                false
                            }

                        }

                    )
                }
            }*/
        }
        TimeLineOverlay(
            epgWindowStartEpochSeconds = epgWindowStartEpochSeconds,
            dpPerMinute = DP_PER_MINUTE,
            horizontalScrollState = sharedHorizontalScrollState,
            channels = channels,
            isProgramDetailsVisible = (animatedProgramState != null),
            modifier = Modifier.offset(x = epgContentOffsetX)
        )

    }
}
////////////////////////TEST///////////////////////////////
/*@Composable
fun EpgContent(
    viewModel: EPGViewModel,
    channels: List<AppChannel>,
    programsByChannelId: Map<String, List<AppProgram>>,
    epgWindowStartEpochSeconds: Long
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    var currentTimeInEpochSeconds by remember { mutableStateOf(System.currentTimeMillis() / 1000) }
    LaunchedEffect(Unit) {
        while (true) {
            currentTimeInEpochSeconds = System.currentTimeMillis() / 1000
            delay(1000L)
        }
    }
    val formattedCurrentTime = remember(currentTimeInEpochSeconds) {
        SimpleDateFormat(
            "HH:mm",
            Locale.getDefault()
        ).format(Date(currentTimeInEpochSeconds * 1000L))
    }
    var imageUrlForTopRight by remember { mutableStateOf<String?>(null) }
    val initialLastFocusedId =
        remember(channels) { if (channels.isNotEmpty()) context.getLastFocusedChannelId() else null }
    val targetChannelGlobalIndex = remember(channels, initialLastFocusedId) {
        if (initialLastFocusedId != null) channels.indexOfFirst { it.channelId == initialLastFocusedId }
            .takeIf { it != -1 } ?: 0 else 0
    }
    val itemsAboveFocused = 2
    val indexForListTop = remember(targetChannelGlobalIndex) {
        (targetChannelGlobalIndex - itemsAboveFocused).coerceAtLeast(0)
    }
    val listState = rememberTvLazyListState(initialFirstVisibleItemIndex = indexForListTop)
    val focusRequesters = remember(channels) { channels.associateWith { FocusRequester() } }
    var initialFocusRequestedForId by remember { mutableStateOf<String?>(null) }
    val sharedHorizontalScrollState = rememberScrollState()
    val totalEpgWidth = remember(DP_PER_MINUTE) { (24 * 60 * DP_PER_MINUTE.value).dp }
    var focusedProgram by remember { mutableStateOf<AppProgram?>(null) }
    var animatedProgramState by remember { mutableStateOf<AppProgram?>(null) }


    // NOVO: Stanje filtera i animacije iz ViewModel-a
    val isFilterMenuVisible by viewModel.isFilterMenuVisible.collectAsState()
    val isChannelItemFocused by viewModel.isChannelItemFocused.collectAsState()
    val currentFilter by viewModel.currentFilter.collectAsState()
    val filteredChannels by remember(channels, currentFilter) {
        derivedStateOf {
            when (currentFilter) {
                FilterType.ALL -> channels
                FilterType.FAVORITES -> channels.filter { it.isFavorite }
            }
        }
    }
    val epgContentOffsetX by animateDpAsState(
        targetValue = if (isFilterMenuVisible) FILTER_MENU_WIDTH else 0.dp,
        animationSpec = tween(300),
        label = "epgContentOffset"
    )

    // NOVO: FocusRequester za filtere
    val allFilterRequester = remember { FocusRequester() }
    val favoritesFilterRequester = remember { FocusRequester() }
    val filterRequesters = mapOf(
        FilterType.ALL to allFilterRequester,
        FilterType.FAVORITES to favoritesFilterRequester
    )

    LaunchedEffect(focusedProgram) {
        delay(100L)
        animatedProgramState = focusedProgram
    }

    BackHandler(enabled = focusedProgram != null || isFilterMenuVisible) {
        if (isFilterMenuVisible) {
            viewModel.toggleFilterMenu(false)
            // NOVO: Vraćamo fokus na trenutni kanal nakon zatvaranja menija
            val targetChannelId = channels.find { it.channelId == focusedProgram?.channelId }?.channelId ?: initialLastFocusedId
            channels.find { it.channelId == targetChannelId }?.let { channel ->
                focusRequesters[channel]?.requestFocus()
            }
        } else {
            val targetRequester = channels
                .find { it.channelId == focusedProgram?.channelId }
                ?.let { focusRequesters[it] }
            targetRequester?.requestFocus()
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        val imageOverallAlpha = 0.42f
        val imageFadeEdgeLength = 50.dp
        val imageFadeToColor = BackgroundColor
        val imageBoxHeight = 280.dp
        val imageBoxWidth = remember(imageBoxHeight) { (imageBoxHeight.value * 16 / 9).dp }

        imageUrlForTopRight?.let { imageUrl ->
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .width(imageBoxWidth)
                    .height(imageBoxHeight)
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Pozadinska slika",
                    modifier = Modifier
                        .matchParentSize()
                        .alpha(imageOverallAlpha),
                    contentScale = ContentScale.Fit
                )
                Box(
                    Modifier
                        .align(Alignment.CenterStart)
                        .width(imageFadeEdgeLength)
                        .fillMaxHeight()
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(
                                    imageFadeToColor,
                                    Color.Transparent
                                )
                            )
                        )
                )
                Box(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .height(imageFadeEdgeLength)
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    imageFadeToColor
                                )
                            )
                        )
                )
            }
        }

        // NOVO: Filter meni (prikazuje se samo kada je vidljivost true)
        AnimatedVisibility(
            visible = isFilterMenuVisible,
            enter = fadeIn(tween(300)),
            exit = fadeOut(tween(300)),
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            FilterMenu(
                currentFilter = currentFilter,
                onFilterSelected = { filter ->
                    viewModel.onFilterSelected(filter)
                },
                onFocused = {
                    // Kada se fokus vrati na meni, sakrivamo simbol '<'
                    viewModel.onChannelItemFocusChanged(false)
                },
                onKeyEvent = { event ->
                    if (event.type == KeyEventType.KeyDown && event.key == Key.DirectionRight) {
                        viewModel.toggleFilterMenu(false)
                        // Vraćamo fokus na trenutni kanal
                        val targetChannelId = channels.find { it.channelId == focusedProgram?.channelId }?.channelId ?: initialLastFocusedId
                        channels.find { it.channelId == targetChannelId }?.let { channel ->
                            focusRequesters[channel]?.requestFocus()
                        }
                        true
                    }
                    false
                },
                allFilterRequester = allFilterRequester,
                favoritesFilterRequester = favoritesFilterRequester
            )
        }




        // NOVO: Simbol '<'
        // Prikazuje se samo ako je kanal fokusiran I ako meni sa filterima nije otvoren
        val isLeftArrowVisible = isChannelItemFocused && !isFilterMenuVisible
        AnimatedVisibility(
            visible = isLeftArrowVisible,
            enter = fadeIn(tween(150)),
            exit = fadeOut(tween(150)),
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 22.dp)
        ) {
            /*Icon(
                painter = painterResource(id = R.drawable.icon_left_arrow), // Pretpostavljam da imate ovu sliku
                contentDescription = "Filteri",
                tint = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.size(40.dp)
            )*/
            Text(
                text = "<",
                fontSize = 30.sp,
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.padding(start = 12.dp)
            )
        }

        // ISPRAVLJENO: TopHeader je sada izvan animiranog Column-a
        AnimatedContent(
            targetState = animatedProgramState,
            transitionSpec = {
                if (targetState != null && initialState == null) {
                    (slideInVertically(animationSpec = tween(1000)) { height -> -height / 2 } + fadeIn(
                        animationSpec = tween(1000)
                    ))
                        .togetherWith(slideOutVertically(animationSpec = tween(1000)) { height -> -height / 2 } + fadeOut(
                            animationSpec = tween(1000)
                        ))
                } else if (targetState == null && initialState != null) {
                    fadeIn(animationSpec = tween(1000))
                        .togetherWith(fadeOut(animationSpec = tween(1000)))
                } else {
                    fadeIn(animationSpec = tween(1))
                        .togetherWith(fadeOut(animationSpec = tween(1)))
                }.using(
                    SizeTransform(clip = false)
                )
            },
            label = "HeaderDetailsTransition"
        ) { animatedProgramState ->
            if (animatedProgramState != null) {
                ProgramDetailsView(targetProgram = animatedProgramState)
            } else {
                TopHeader(currentTime = formattedCurrentTime)
            }
        }


        // ISPRAVLJENO: Novi Column koji se animira i sadrži samo EPG listu
        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset(x = epgContentOffsetX)
        ) {
            TimelineHeader(
                globalTimelineStartEpochSeconds = epgWindowStartEpochSeconds,
                dpPerMinute = DP_PER_MINUTE,
                timelineHeight = 25.dp,
                horizontalScrollState = sharedHorizontalScrollState,
                totalWidth = totalEpgWidth
            )

            TvLazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = EPG_SIDE_PADDING),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                itemsIndexed(
                    filteredChannels,
                    key = { _, channel -> channel.channelId }) { index, channel ->
                    val requester = focusRequesters[channel] ?: remember { FocusRequester() }
                    LaunchedEffect(
                        initialLastFocusedId,
                        channel.channelId,
                        requester,
                        listState.isScrollInProgress,
                        initialFocusRequestedForId
                    ) {
                        if (channel.channelId == initialLastFocusedId && initialLastFocusedId != null && initialFocusRequestedForId != initialLastFocusedId && !listState.isScrollInProgress && index == targetChannelGlobalIndex) {
                            requester.requestFocus()
                            initialFocusRequestedForId = initialLastFocusedId
                        }
                    }
                    EpgChannelRow(
                        viewModel = viewModel,
                        channel = channel,
                        programsForThisChannel = programsByChannelId[channel.channelId]
                            ?: emptyList(),
                        dpPerMinute = DP_PER_MINUTE,
                        rowHeight = EPG_PROGRAM_ROW_HEIGHT,
                        focusRequesterForChannel = requester,
                        onChannelFocusAndIdChanged = { isFocused, focusedChannelId, logoUrl ->
                            if (isFocused) {
                                imageUrlForTopRight = logoUrl
                                context.saveLastFocusedChannelId(focusedChannelId)
                                focusedProgram = null
                            }
                        },
                        globalTimelineStartEpochSeconds = epgWindowStartEpochSeconds,
                        horizontalScrollState = sharedHorizontalScrollState,
                        totalWidth = totalEpgWidth,
                        onProgramFocused = { program ->
                            focusedProgram = program
                            val channelForProgram =
                                channels.find { it.channelId == program?.channelId }
                            imageUrlForTopRight = program?.thumbnail ?: channelForProgram?.logo
                        },
                        currentTimeInEpochSeconds = currentTimeInEpochSeconds,
                        onKeyEvent = { event ->
                            if (isChannelItemFocused && event.type == KeyEventType.KeyDown && event.key == Key.DirectionLeft) {
                                viewModel.toggleFilterMenu(true)
                                allFilterRequester.requestFocus()
                                true
                            }
                            false
                        }
                    )
                }
            }
        }
        TimeLineOverlay(
            epgWindowStartEpochSeconds = epgWindowStartEpochSeconds,
            dpPerMinute = DP_PER_MINUTE,
            horizontalScrollState = sharedHorizontalScrollState,
            channels = channels,
            isProgramDetailsVisible = (animatedProgramState != null)
        )
    }
}*/


@Composable
fun FilterMenu(
    currentFilter: FilterType,
    onFilterSelected: (FilterType) -> Unit,
    onFocused: () -> Unit,
    onKeyEvent: (KeyEvent) -> Boolean,
    allFilterRequester: FocusRequester,
    favoritesFilterRequester: FocusRequester
) {
    Box(
        modifier = Modifier
            .width(FILTER_MENU_WIDTH)
            .fillMaxHeight()
            .background(BackgroundColor)
            .onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    onFocused()
                }
            }
            .onKeyEvent(onKeyEvent)
            /*.onKeyEvent { event ->
                // Ako je pritisnuto desno dugme na D-padu
                if (event.type == KeyEventType.KeyDown && event.key == Key.DirectionRight) {
                    // Obavestimo ViewModel da sakrije meni
                    onKeyEvent(event)
                    return@onKeyEvent true
                }
                return@onKeyEvent false
            }*/
            .padding(start = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .background(BackgroundColor)
                .fillMaxHeight()
                .focusGroup(),
            verticalArrangement = Arrangement.Center
        ) {
            FilterButton(
                text = "All",
                isSelected = currentFilter == FilterType.ALL,
                onClick = { onFilterSelected(FilterType.ALL) },
                modifier = Modifier.focusRequester(allFilterRequester)
            )
            Spacer(modifier = Modifier.height(16.dp))
            FilterButton(
                text = "Favorites",
                isSelected = currentFilter == FilterType.FAVORITES,
                onClick = { onFilterSelected(FilterType.FAVORITES) },
                modifier = Modifier.focusRequester(favoritesFilterRequester)
            )
        }
    }
}


@Composable
fun FilterButton(text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier) {
    /*val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFFFF525763)/*Color(0xFF5B5B5B)*/ else Color.Transparent,
        animationSpec = tween(200)
    )*/
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    //val focusedColor = Color(0xFFC0A0FF)
    val focusedColor = Color(0xFFA269FF).copy(alpha = 0.75f)
    val selectedColor = Color(0xFF525763)

    val backgroundColor by animateColorAsState(
        targetValue = when {
            // isSelected has highest priority
            isSelected -> selectedColor
            // isFocused has lower priority
            isFocused -> focusedColor

            else -> Color.Transparent
        },
        animationSpec = tween(200)
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected || isFocused) Color.White else Color.LightGray,
        animationSpec = tween(200)
    )
    val buttonModifier = modifier
        .fillMaxWidth()
        .height(45.dp)
        .clip(RoundedCornerShape(27.5.dp))
        .background(backgroundColor)
        .clickable(onClick = onClick,
            indication = null,
            interactionSource = interactionSource
        )
        .focusable(interactionSource = interactionSource)
        .padding(horizontal = 16.dp)

    Box(
        modifier = buttonModifier,
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text = text, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}
///////////////////////// TEST/////////////////////////////

/*@Composable
fun EpgContent(
    viewModel: EPGViewModel,
    channels: List<AppChannel>,
    programsByChannelId: Map<String,List<AppProgram>>,
    epgWindowStartEpochSeconds: Long
) {
    val context = LocalContext.current
    val density = LocalDensity.current

    /*val playerBoxHeight = 280.dp
    val playerBoxWidth = remember(playerBoxHeight) { (playerBoxHeight.value * 16 / 9).dp }
    val playerBoxWidthPx = with(density) { playerBoxWidth.toPx().toInt() }
    val playerBoxHeightPx = with(density) { playerBoxHeight.toPx().toInt() }*/

    //val playingProgram by viewModel.playingProgram.collectAsState()


    var currentTimeInEpochSeconds by remember { mutableStateOf(System.currentTimeMillis() / 1000) }
    LaunchedEffect(Unit) {
        while (true) {
            currentTimeInEpochSeconds = System.currentTimeMillis() / 1000
            delay(1000L)
        }
    }

    val formattedCurrentTime = remember(currentTimeInEpochSeconds) {
        SimpleDateFormat(
            "HH:mm",
            Locale.getDefault()
        ).format(Date(currentTimeInEpochSeconds * 1000L))
    }


    var imageUrlForTopRight by remember { mutableStateOf<String?>(null) }
    //val context = LocalContext.current
    val initialLastFocusedId =
        remember(channels) { if (channels.isNotEmpty()) context.getLastFocusedChannelId() else null }
    val targetChannelGlobalIndex = remember(
        channels,
        initialLastFocusedId
    ) {
        if (initialLastFocusedId != null) channels.indexOfFirst { it.channelId == initialLastFocusedId }
            .takeIf { it != -1 } ?: 0 else 0
    }
    val itemsAboveFocused = 2
    val indexForListTop = remember(targetChannelGlobalIndex) {
        (targetChannelGlobalIndex - itemsAboveFocused).coerceAtLeast(0)
    }
    val listState = rememberTvLazyListState(initialFirstVisibleItemIndex = indexForListTop)
    val focusRequesters = remember(channels) { channels.associateWith { FocusRequester() } }
    var initialFocusRequestedForId by remember { mutableStateOf<String?>(null) }
    val sharedHorizontalScrollState = rememberScrollState()
    val totalEpgWidth = remember(DP_PER_MINUTE) { (24 * 60 * DP_PER_MINUTE.value).dp }
    var focusedProgram by remember { mutableStateOf<AppProgram?>(null) }


    var animatedProgramState by remember { mutableStateOf<AppProgram?>(null) }

    // NOVO: Stanje filtera i animacije iz ViewModel-a
    val isFilterMenuVisible by viewModel.isFilterMenuVisible.collectAsState()
    val isChannelItemFocused by viewModel.isChannelItemFocused.collectAsState()
    val currentFilter by viewModel.currentFilter.collectAsState()
    val filteredChannels by remember(channels, currentFilter) {
        derivedStateOf {
            when (currentFilter) {
                FilterType.ALL -> channels
                FilterType.FAVORITES -> channels.filter { it.isFavorite }
            }
        }
    }
    val epgContentOffsetX by animateDpAsState(
        targetValue = if (isFilterMenuVisible) FILTER_MENU_WIDTH else 0.dp,
        animationSpec = tween(300),
        label = "epgContentOffset"
    )

    // NOVO: FocusRequester za filtere
    val allFilterRequester = remember { FocusRequester() }
    val favoritesFilterRequester = remember { FocusRequester() }
    val filterRequesters = mapOf(
        FilterType.ALL to allFilterRequester,
        FilterType.FAVORITES to favoritesFilterRequester
    )

    LaunchedEffect(focusedProgram) {
        delay(100L)
        animatedProgramState = focusedProgram
    }


    /*BackHandler(enabled = focusedProgram != null) {

        val targetRequester = channels
            .find { it.channelId == focusedProgram?.channelId }
            ?.let { focusRequesters[it] }

        targetRequester?.requestFocus()

    }*/
    BackHandler(enabled = focusedProgram != null || isFilterMenuVisible) {
        if (isFilterMenuVisible) {
            viewModel.toggleFilterMenu(false)
            // NOVO: Vraćamo fokus na trenutni kanal nakon zatvaranja menija
            val targetChannelId = (channels as? Resource.Success)?.data?.find { it.channelId == focusedProgram?.channelId }?.channelId ?: initialLastFocusedId
            channels.find { it.channelId == targetChannelId }?.let { channel ->
                focusRequesters[channel]?.requestFocus()
            }
        } else {
            val targetRequester = channels
                .find { it.channelId == focusedProgram?.channelId }
                ?.let { focusRequesters[it] }
            targetRequester?.requestFocus()
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        val imageOverallAlpha = 0.42f
        val imageFadeEdgeLength = 50.dp
        val imageFadeToColor = BackgroundColor
        val imageBoxHeight = 280.dp
        val imageBoxWidth = remember(imageBoxHeight) { (imageBoxHeight.value * 16 / 9).dp }

        /*if (playingProgram != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .width(imageBoxWidth)
                    .height(imageBoxHeight)
            ) {
                VideoPlayer(
                    videoUrl = playingProgram!!.playbackURL,
                    thumbnailUrl = imageUrlForTopRight,
                    modifier = Modifier.matchParentSize()
                )
            }*/


        //} else {
            imageUrlForTopRight?.let { imageUrl ->


                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .width(imageBoxWidth)
                        .height(imageBoxHeight)
                ) {

                    AsyncImage(model = imageUrl, contentDescription = "Pozadinska slika", modifier = Modifier
                        .matchParentSize()
                        .alpha(imageOverallAlpha), contentScale = ContentScale.Fit)
                    Box(Modifier.align(Alignment.CenterStart)
                        .width(imageFadeEdgeLength)
                        .fillMaxHeight()
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(
                                    imageFadeToColor,
                                    Color.Transparent
                                )
                            )
                        ))
                    Box(Modifier.align(Alignment.BottomCenter)
                        .height(imageFadeEdgeLength)
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    imageFadeToColor
                                )
                            )
                        ))
                }
            }
        //}
        /*Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .width(imageBoxWidth)
                .height(imageBoxHeight)
        ) {
            // NOVO: Uslovna logika koja proverava da li se neki program pušta
            if (playingProgram != null) {
                // Ako program za puštanje POSTOJI, pozovi VideoPlayer
                VideoPlayer(
                    videoUrl = playingProgram!!.playbackURL, // Koristimo URL iz programa
                    modifier = Modifier.matchParentSize()
                )
            } else {
                // U suprotnom (ako je playingProgram null), koristi staru logiku za prikaz slike
                imageUrlForTopRight?.let { imageUrl ->
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Pozadinska slika",
                        modifier = Modifier
                            .matchParentSize()
                            .alpha(imageOverallAlpha),
                        contentScale = ContentScale.Fit
                    )
                    Box(
                        Modifier
                            .align(Alignment.CenterStart)
                            .width(imageFadeEdgeLength)
                            .fillMaxHeight()
                            .background(
                                brush = Brush.horizontalGradient(
                                    listOf(
                                        imageFadeToColor,
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    Box(
                        Modifier
                            .align(Alignment.BottomCenter)
                            .height(imageFadeEdgeLength)
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    listOf(
                                        Color.Transparent,
                                        imageFadeToColor
                                    )
                                )
                            )
                    )
                }
            }
        }*/
        //TESTIRANJE STARI NACIN
        /*imageUrlForTopRight?.let { imageUrl ->


            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .width(imageBoxWidth)
                    .height(imageBoxHeight)
            ) {

                AsyncImage(model = imageUrl, contentDescription = "Pozadinska slika", modifier = Modifier
                            .matchParentSize()
                            .alpha(imageOverallAlpha), contentScale = ContentScale.Fit)
                Box(Modifier.align(Alignment.CenterStart)
                            .width(imageFadeEdgeLength)
                            .fillMaxHeight()
                            .background(
                                brush = Brush.horizontalGradient(
                                    listOf(
                                        imageFadeToColor,
                                        Color.Transparent
                                    )
                                )
                            ))
                Box(Modifier.align(Alignment.BottomCenter)
                            .height(imageFadeEdgeLength)
                            .fillMaxWidth()
                            .background(
                                brush = Brush.verticalGradient(
                                    listOf(
                                        Color.Transparent,
                                        imageFadeToColor
                                    )
                                )
                            ))
                    }
                }*/
            }

        Column(modifier = Modifier.fillMaxSize()) {

            AnimatedContent(
                targetState = animatedProgramState,
                transitionSpec = {
                    if (targetState != null && initialState == null) {
                        //  TopHeader -> ProgramDetailsView
                        (slideInVertically(animationSpec = tween(1000)) { height -> -height / 2 } + fadeIn(
                            animationSpec = tween(1000)
                        ))
                            .togetherWith(slideOutVertically(animationSpec = tween(1000)) { height -> -height / 2 } + fadeOut(
                                animationSpec = tween(1000)
                            ))
                    } else if (targetState == null && initialState != null) {
                        //ProgramDetailsView -> TopHeader
                        fadeIn(animationSpec = tween(1000))
                            .togetherWith(fadeOut(animationSpec = tween(1000)))
                        /*(slideInVertically(animationSpec = tween(1000)) { height -> -height / 2 } + fadeIn(animationSpec = tween(1000)))
                            .togetherWith(slideOutVertically(animationSpec = tween(1000)) { height -> -height / 2 } + fadeOut(animationSpec = tween(1000)))*/
                    } else {
                        // Program A -> Program B
                        fadeIn(animationSpec = tween(1))
                            .togetherWith(fadeOut(animationSpec = tween(1)))
                    }.using(
                        SizeTransform(clip = false)
                    )
                },
                label = "HeaderDetailsTransition"
            ) { animatedProgramState ->
                if (animatedProgramState != null) {
                    ProgramDetailsView(targetProgram = animatedProgramState!!)
                } else {
                    TopHeader(currentTime = formattedCurrentTime)
                }
            }

            TimelineHeader(
                globalTimelineStartEpochSeconds = epgWindowStartEpochSeconds,
                dpPerMinute = DP_PER_MINUTE,
                timelineHeight = 25.dp,
                horizontalScrollState = sharedHorizontalScrollState,
                totalWidth = totalEpgWidth

            )

            TvLazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = EPG_SIDE_PADDING),
                state = listState,
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                itemsIndexed(
                    channels,
                    key = { _, channel -> channel.channelId }) { index, channel ->
                    val requester = focusRequesters[channel] ?: remember { FocusRequester() }
                    LaunchedEffect(
                        initialLastFocusedId,
                        channel.channelId,
                        requester,
                        listState.isScrollInProgress,
                        initialFocusRequestedForId
                    ) {
                        if (channel.channelId == initialLastFocusedId && initialLastFocusedId != null && initialFocusRequestedForId != initialLastFocusedId && !listState.isScrollInProgress && index == targetChannelGlobalIndex) {
                            requester.requestFocus()
                            initialFocusRequestedForId = initialLastFocusedId
                        }
                    }

                    EpgChannelRow(
                        viewModel = viewModel,
                        channel = channel,
                        programsForThisChannel = programsByChannelId[channel.channelId]
                            ?: emptyList(),
                        dpPerMinute = DP_PER_MINUTE,
                        rowHeight = EPG_PROGRAM_ROW_HEIGHT,
                        focusRequesterForChannel = requester,
                        onChannelFocusAndIdChanged = { isFocused, focusedChannelId, logoUrl ->
                            if (isFocused) {
                                imageUrlForTopRight = logoUrl
                                //focusedChannelLogoUrl = logoUrl
                                context.saveLastFocusedChannelId(focusedChannelId)
                                focusedProgram = null
                            } //else {
                            //if (focusedChannelLogoUrl == logoUrl) focusedChannelLogoUrl = null
                            //}
                        },
                        globalTimelineStartEpochSeconds = epgWindowStartEpochSeconds,
                        horizontalScrollState = sharedHorizontalScrollState,
                        totalWidth = totalEpgWidth,
                        onProgramFocused = { program ->
                            focusedProgram = program
                            val channelForProgram =
                                channels.find { it.channelId == program?.channelId }
                            imageUrlForTopRight = program?.thumbnail ?: channelForProgram?.logo
                        },
                        // NOVO: Prosleđujemo stanje o vremenu
                        currentTimeInEpochSeconds = currentTimeInEpochSeconds,
                        // NOVO: Prosleđujemo key event
                        onKeyEvent = { event ->
                            if (isChannelItemFocused && event.type == KeyEventType.KeyDown && event.key == Key.DirectionLeft) {
                                viewModel.toggleFilterMenu(true)
                                // NOVO: Tražimo fokus za ALL filter čim se meni otvori
                                allFilterRequester.requestFocus()
                                return@onKeyEvent true
                            }
                            return@onKeyEvent false
                        }
                        //playingProgram = playingProgram,
                        /*onProgramClicked = { program ->
                            viewModel.onProgramClicked(
                                program = program,
                                playerWidth = playerBoxWidthPx,
                                playerHeight = playerBoxHeightPx,
                                context = context
                            )
                        }*/
                    )

                }
            }
        }
        // NOVO: Dodajemo TimeLineOverlay ovde
        // Moramo ga postaviti unutar istog Box-a kao i Column, kako bi se iscrtao preko svega
        TimeLineOverlay(
            epgWindowStartEpochSeconds = epgWindowStartEpochSeconds,
            dpPerMinute = DP_PER_MINUTE,
            horizontalScrollState = sharedHorizontalScrollState,
            channels = channels,
            isProgramDetailsVisible = (animatedProgramState != null)
        )
    }*/



@Composable
fun TimeLineOverlay(
    epgWindowStartEpochSeconds: Long,
    dpPerMinute: Dp,
    horizontalScrollState: ScrollState,
    channels: List<AppChannel>,
    isProgramDetailsVisible: Boolean,
    modifier: Modifier = Modifier // DODATO: Novi parametar

) {
    // Pratimo trenutno vreme
    var currentTimeInEpochSeconds by remember { mutableStateOf(System.currentTimeMillis() / 1000) }
    LaunchedEffect(Unit) {
        while (true) {
            currentTimeInEpochSeconds = System.currentTimeMillis() / 1000
            delay(1000L) // Osvežavaj svakih sekund
        }
    }

    // Računamo poziciju linije
    val minutesSinceEpgStart = (currentTimeInEpochSeconds - epgWindowStartEpochSeconds) / 60
    val timelineOffset = (minutesSinceEpgStart * dpPerMinute.value).dp

    // Ako je horizontalno skrolovanje u toku, prilagođavamo offset
    val scrollOffset = with(LocalDensity.current) { horizontalScrollState.value.toDp() }
    val finalOffset = timelineOffset - scrollOffset

    // NOVO: Dinamički određujemo padding sa vrha
    val topPadding = if (isProgramDetailsVisible) {
        PROGRAM_DETAILS_HEIGHT
    } else {
        TOP_HEADER_HEIGHT
    }

    // Koristimo Box da se iscrta vertikalna linija
    /*Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = EPG_SIDE_PADDING + EPG_CHANNEL_ITEM_WIDTH + SPACE_BETWEEN_CHANNEL_AND_PROGRAMS
            )
    ) {
        Column(
            modifier = Modifier
                .offset(x = finalOffset)
                .height(EPG_PROGRAM_ROW_HEIGHT * channels.size + (channels.size - 1) * 1.dp)
                .width(4.dp) // Širina vaše linije
        ) {
            // SVG linija koju ste spomenuli
            // Pretpostavljam da je ljubičasta boja, u ovom primeru ću koristiti Color.Magenta
            // Umesto Box-a možete koristiti vašu SVG sliku
            /*
            Image(
                painter = painterResource(id = R.drawable.vaša_linija_slika),
                contentDescription = "Trenutno vreme",
                modifier = Modifier
                    .fillMaxHeight()
                    .width(4.dp) // Prilagodite
            )
            */
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp)
                    .background(Color.Magenta.copy(alpha = 0.8f)) // Privremena linija ako nemate SVG
            )
        }*/

    //*****************TESTIRANJE**********************//
    // Koristimo JEDAN Box za sve

    val isVisible = finalOffset >= 0.dp

    val lineColor = if (isVisible) Color(0xFFA269FF).copy(alpha = 0.75f) else Color.Transparent  //Color.Magenta.copy(alpha = 0.55f)
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(top = topPadding + 17.dp)
            .padding(
                start = EPG_SIDE_PADDING + EPG_CHANNEL_ITEM_WIDTH + SPACE_BETWEEN_CHANNEL_AND_PROGRAMS
            )
            .offset(x = finalOffset)
    ) {
        // Current line
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
                .background(lineColor)
        )
    }
}

/*@Composable
fun VideoPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Kreiramo i pamtimo ExoPlayer instancu. Ključ je videoUrl da bi se
    // plejer ponovo kreirao ako se URL promeni.
    val exoPlayer = remember(videoUrl) {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(videoUrl)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true // Pusti video odmah
        }
    }

    // DisposableEffect je ključan! On osigurava da se plejer uništi (release)
    // kada se komponenta ukloni sa ekrana, čime se sprečava curenje memorije.
    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    // Koristimo AndroidView da bismo prikazali ExoPlayer-ov PlayerView
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = false // Isključujemo kontrole da ne bi smetale u malom prozoru
            }
        }
    )
}*/









/*@Composable
fun VideoPlayer(
    videoUrl: String,
    thumbnailUrl: String?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isPlayerReady by remember { mutableStateOf(false) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            playWhenReady = true
        }
    }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                isPlayerReady = playbackState == Player.STATE_READY
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    LaunchedEffect(videoUrl) {
        isPlayerReady = false
        val mediaItem = MediaItem.fromUri(videoUrl)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        // Vraćamo se na AndroidView, ali sada sa "golim" SurfaceView-om
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (isPlayerReady) 1f else 0f),
            factory = { ctx ->
                SurfaceView(ctx).apply {
                    holder.addCallback(object : SurfaceHolder.Callback {
                        override fun surfaceCreated(holder: SurfaceHolder) {
                            // Površina je kreirana, predajemo je plejeru
                            exoPlayer.setVideoSurface(holder.surface)
                        }

                        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                            // Ništa ne radimo ovde
                        }

                        override fun surfaceDestroyed(holder: SurfaceHolder) {
                            // Površina je uništena, oduzimamo je od plejera
                            exoPlayer.clearVideoSurface()
                        }
                    })
                }
            }
        )

        // Logika za thumbnail i spinner ostaje ista
        if (!isPlayerReady) {
            AsyncImage(
                model = thumbnailUrl,
                contentDescription = "Loading thumbnail",
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Fit
            )
            CircularProgressIndicator(color = Color.White)
        }
    }
}*/

// NAJNOVIJE //
/*@Composable
fun VideoPlayer(
    videoUrl: String,
    thumbnailUrl: String?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            playWhenReady = true
        }
    }

    // --- SREĐENA I OBJEDINJENA LOGIKA ZA ŽIVOTNI CIKLUS ---
    DisposableEffect(exoPlayer) {
        // Kreiramo observer za PAUSE/RESUME događaje
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> exoPlayer.pause()
                Lifecycle.Event.ON_RESUME -> exoPlayer.play()
                else -> {}
            }
        }
        // Dodajemo observer na životni ciklus
        lifecycleOwner.lifecycle.addObserver(observer)

        // onDispose blok se sada brine o SVEMU na kraju
        onDispose {
            // Uklanjamo observer
            lifecycleOwner.lifecycle.removeObserver(observer)
            // I oslobađamo plejer
            exoPlayer.release()
        }
    }
    // --- KRAJ SREĐENE LOGIKE ---


    // Efekat za držanje ekrana budnim ostaje isti
    val view = LocalView.current
    DisposableEffect(Unit) {
        val window = (view.context as? Activity)?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    // Efekat za promenu video URL-a ostaje isti
    LaunchedEffect(videoUrl) {
        val mediaItem = MediaItem.fromUri(videoUrl)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
    }

    // AndroidView ostaje isti
    AndroidView(
        modifier = modifier.focusable(false),
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = false
                isFocusable = false
            }
        },
        // --- KLJUČNA ISPRAVKA JE OVDE ---
        update = { view ->
            // Svaki put kad se UI osveži (npr. povratak u app),
            // ponovo dodeli plejer. Ovo natera PlayerView
            // da ponovo uspostavi vezu sa (potencijalno novom) grafičkom površinom.
            view.player = exoPlayer
        }
    )
}*/




@Composable
fun VideoPlayer(
    videoUrl: String,
    thumbnailUrl: String?, // Parametar za sličicu
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Stanje koje prati da li je plejer spreman za prikaz
    var isPlayerReady by remember { mutableStateOf(false) }

    // Kreiramo ExoPlayer instancu i pamtimo je.
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            playWhenReady = true
        }
    }

    // Efekat koji upravlja životnim ciklusom plejera i listener-om
    DisposableEffect(exoPlayer) {
        // Kreiramo listener koji prati stanje plejera
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                // Kada je plejer spreman (završio baferovanje), ažuriramo stanje
                isPlayerReady = playbackState == Player.STATE_READY
            }
        }
        // Dodajemo listener
        exoPlayer.addListener(listener)

        // onDispose se poziva kada se komponenta uništi
        onDispose {
            exoPlayer.removeListener(listener) // Uklanjamo listener
            exoPlayer.release() // Oslobađamo plejer
        }
    }

    // Efekat koji reaguje na promenu video linka
    LaunchedEffect(videoUrl) {
        isPlayerReady = false // Resetujemo stanje svaki put kad se promeni video
        val mediaItem = MediaItem.fromUri(videoUrl)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
    }

    // Koristimo Box za slaganje elemenata (plejer ili sličica/spinner)
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // AndroidView (plejer) je jedan od elemenata u Box-u
        AndroidView(
            // .alpha() čini plejer nevidljivim dok nije spreman
            modifier = Modifier
                .fillMaxSize()
                .focusable(false)
                .alpha(if (isPlayerReady) 1f else 0f),
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                    isFocusable = false
                }
            },
            // Update blok je važan da se video ne zamrzne pri povratku u aplikaciju
            update = { view ->
                view.player = exoPlayer
            }
        )

        // Prikazujemo sličicu i spinner ako plejer NIJE spreman
        if (!isPlayerReady) {
            AsyncImage(
                model = thumbnailUrl,
                contentDescription = "Učitavanje...",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
            CircularProgressIndicator(color = PlayingProgramCardColor)
        }
    }
}




















// JOS JEDNO TESTIRANJE //




















