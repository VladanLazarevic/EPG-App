package com.example.epg.Presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
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


private val EPG_SIDE_PADDING = 35.dp
private val EPG_CHANNEL_ITEM_WIDTH = 177.dp
private val EPG_PROGRAM_ROW_HEIGHT = 60.dp
private val DP_PER_MINUTE = 6.dp
private val SPACE_BETWEEN_CHANNEL_AND_PROGRAMS = 2.dp
private val FIXED_CARD_SPACING_DP = 0.dp
private val PROGRAM_DETAILS_HEIGHT = 200.dp



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
                            modifier = Modifier.size(499.dp)
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
    onProgramFocused: (program: AppProgram?) -> Unit
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
            },
            onFavoriteClick = { viewModel.onToggleFavorite(channel.channelId) }

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
                        if (event.type != KeyEventType.KeyDown) return@onKeyEvent false
                        when (event.key) {
                            Key.DirectionLeft -> {
                                if (focusedProgramId == firstProgramId && focusedProgramStartTimeEpoch == firstProgramStartTimeEpoch) {
                                    focusRequesterForChannel.requestFocus()
                                    return@onKeyEvent true
                                }
                            }

                        }
                        false
                    }
            ) {
                var lastVisualElementEndTimeSeconds = globalTimelineStartEpochSeconds
                val epgWindowEndEpochSeconds = globalTimelineStartEpochSeconds + (27 * 60 * 60)

                for (program in programsForThisChannel) {
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
                            EmptyProgramCard(width = gapWidth, height = rowHeight)
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
                        }
                    )
                    lastVisualElementEndTimeSeconds = programEndTimeSeconds
                }



                if (lastVisualElementEndTimeSeconds < epgWindowEndEpochSeconds) {
                    var finalGapSec = epgWindowEndEpochSeconds - lastVisualElementEndTimeSeconds


                    while (finalGapSec > 0) {
                        val chunkDurationSec =
                            minOf(finalGapSec, MAX_EMPTY_CHUNK_DURATION_SEC.toLong())
                        val chunkWidthDp = ((chunkDurationSec / 60f) * dpPerMinute.value).dp

                        EmptyProgramCard(width = chunkWidthDp, height = rowHeight)

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

    val cardAlpha = 0.53f
    val containerColor by animateColorAsState(
        targetValue = if (isFocused) FocusedProgramCardColor.copy(alpha = 1f) else UnfocusedProgramCardColor.copy(alpha = cardAlpha),
        animationSpec = tween(100),
        label = "ProgramCardContainerColorFocus"
    )

    val spacingWidth = 3.dp


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
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = spacingWidth)
                .background(color = containerColor, shape = shape)
                .padding(horizontal = 6.dp, vertical = 4.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = program.title,
                style = MaterialTheme.typography.bodySmall,
                color = if (isFocused) Color.Black else Color.LightGray,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/*@Composable
fun EmptyProgramCard(width: Dp, height: Dp) {
    Card(
        modifier = Modifier
            .width(width)
            .height(height - 4.dp)
            // VAŽNO: Ova kartica ne sme biti fokusabilna
            .focusable(false),
        shape = RoundedCornerShape(9.dp),
        // Koristimo tamniju, poluprovidnu boju da se razlikuje
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.2f)),
        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.2f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
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

//testiranje
@Composable
fun EmptyProgramCard(width: Dp, height: Dp) {
    // Definišemo razmak da bude isti kao kod ProgramCard
    val spacingWidth = 3.dp

    // Spoljna kartica je sada providna i zauzima punu širinu praznine
    Card(
        modifier = Modifier
            .width(width)
            .height(height - 4.dp)
            .focusable(false),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent), // Providna pozadina
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), // Gasimo senku
        border = null // Uklanjamo stari border jer ćemo ga dodati na Box unutra
    ) {
        // Unutrašnji Box nosi boju i sadržaj, i malo je uži
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = spacingWidth) // Ostavljamo 3dp prostora sa desne strane
                .background(
                    color = Color.Black.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(9.dp)
                )
                .border( // Dodajemo border ovde da prati obojeni deo
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
}


@Composable
fun TopHeader() {
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
fun ChannelItem(
    channel: AppChannel,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester,
    onFocusChangedAndIdCallback: (isFocused: Boolean, channelId: String) -> Unit,
    onFavoriteClick: () -> Unit
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
                        .padding(bottom = 4.dp, end = 4.dp)
                        .size(24.dp),
                    tint = Color.Red
                )
            }
        }
    }
}





@Composable
fun EpgContent(
    viewModel: EPGViewModel,
    channels: List<AppChannel>,
    programsByChannelId: Map<String,List<AppProgram>>,
    epgWindowStartEpochSeconds: Long
) {


    var imageUrlForTopRight by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val initialLastFocusedId = remember(channels) { if (channels.isNotEmpty()) context.getLastFocusedChannelId() else null }
    val targetChannelGlobalIndex = remember(channels, initialLastFocusedId) { if (initialLastFocusedId != null) channels.indexOfFirst { it.channelId == initialLastFocusedId }.takeIf { it != -1 } ?: 0 else 0 }
    val itemsAboveFocused = 2
    val indexForListTop = remember(targetChannelGlobalIndex) { (targetChannelGlobalIndex - itemsAboveFocused).coerceAtLeast(0) }
    val listState = rememberTvLazyListState(initialFirstVisibleItemIndex = indexForListTop)
    val focusRequesters = remember(channels) { channels.associateWith { FocusRequester() } }
    var initialFocusRequestedForId by remember { mutableStateOf<String?>(null) }
    val sharedHorizontalScrollState = rememberScrollState()
    val totalEpgWidth = remember(DP_PER_MINUTE) { (24 * 60 * DP_PER_MINUTE.value).dp }
    var focusedProgram by remember { mutableStateOf<AppProgram?>(null) }

    var animatedProgramState by remember { mutableStateOf<AppProgram?>(null) }

    LaunchedEffect(focusedProgram) {
        delay(100L)
        animatedProgramState = focusedProgram
    }


    BackHandler(enabled = focusedProgram != null) {

        val targetRequester = channels
            .find { it.channelId == focusedProgram?.channelId }
            ?.let { focusRequesters[it] }

        targetRequester?.requestFocus()

    }


    Box(modifier = Modifier.fillMaxSize()) {
        val imageOverallAlpha = 0.42f
        val imageFadeEdgeLength = 50.dp
        val imageFadeToColor = BackgroundColor
        val imageBoxHeight = 243.dp
        val imageBoxWidth = remember(imageBoxHeight) { (imageBoxHeight.value * 16 / 9).dp }


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
                Box(Modifier
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
                    ))
                Box(Modifier
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
                    ))
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            /*Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(
                        animationSpec = tween(
                            durationMillis = 750,
                            easing = LinearOutSlowInEasing
                        )
                    )
            ) {

                if (animatedProgramState != null) {
                    ProgramDetailsView(targetProgram = animatedProgramState!!)
                } else {
                    TopHeader()
                }
            }*/
            AnimatedContent(
                targetState = animatedProgramState,
                transitionSpec = {
                    if (targetState != null && initialState == null) {
                        //  TopHeader -> ProgramDetailsView
                        (slideInVertically(animationSpec = tween(1000)) { height -> -height / 2 } + fadeIn(animationSpec = tween(1000)))
                            .togetherWith(slideOutVertically(animationSpec = tween(1000)) { height -> -height / 2 } + fadeOut(animationSpec = tween(1000)))
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
                    TopHeader()
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
                itemsIndexed(channels, key = { _, channel -> channel.channelId }) { index, channel ->
                    val requester = focusRequesters[channel] ?: remember { FocusRequester() }
                    LaunchedEffect(initialLastFocusedId, channel.channelId, requester, listState.isScrollInProgress, initialFocusRequestedForId) {
                        if (channel.channelId == initialLastFocusedId && initialLastFocusedId != null && initialFocusRequestedForId != initialLastFocusedId && !listState.isScrollInProgress && index == targetChannelGlobalIndex) {
                            requester.requestFocus()
                            initialFocusRequestedForId = initialLastFocusedId
                        }
                    }

                    EpgChannelRow(
                        viewModel = viewModel,
                        channel = channel,
                        programsForThisChannel = programsByChannelId[channel.channelId] ?: emptyList(),
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
                            val channelForProgram = channels.find { it.channelId == program?.channelId }
                            imageUrlForTopRight = program?.thumbnail ?: channelForProgram?.logo
                        }
                    )
                }
            }
        }
    }
}


















// JOS JEDNO TESTIRANJE //




















