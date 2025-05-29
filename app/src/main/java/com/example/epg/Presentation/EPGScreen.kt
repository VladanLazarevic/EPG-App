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
import androidx.tv.foundation.ExperimentalTvFoundationApi
import androidx.tv.foundation.lazy.list.TvLazyListState
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.items
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

///////////////////////NOVO////////////////////////////////////////

/*@Composable
fun EPGScreen(viewModel: EPGViewModel) {
    val channelState by viewModel.channelState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (val state = channelState) {
            is Resource.Loading -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color.Yellow)
                    Text(
                        text = "Loading channels...",
                        style = TextStyle(
                            color = Color.Yellow,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = (-0.5).sp
                        )
                    )
                }
                Log.d("EPGScreen", "Stanje: Učitavanje...")
            }
            is Resource.Success -> {
                val channels = state.data
                Log.d("EPGScreen", "Stanje: Uspeh, broj kanala: ${channels.size}")
                if (channels.isEmpty()) {
                    Text("Nema dostupnih kanala.")
                } else {
                    Column(modifier = Modifier.fillMaxSize()) {
                        TopHeader()
                        Spacer(modifier = Modifier.height(60.dp))
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                        ) {
                            ChannelList(
                                channels = channels,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(0.4f)
                                    .padding(start = 35.dp)
                            )
                            Spacer(modifier = Modifier.weight(0.6f))
                        }
                    }
                }
            }
            is Resource.Error -> {
                Log.e("EPGScreen", "Stanje: Greška - ${state.message}", state.error)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text("Došlo je do greške: ${state.message}")
                    Button(
                        onClick = { viewModel.fetchChannels() },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("Pokušaj ponovo")
                    }
                }
            }
        }
    }
}

@Composable
fun TopHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 39.dp,
                start = 65.8.dp,
                end = 46.75.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.iwedia_logo_white_02),
            contentDescription = "TV Guide Logo",
            modifier = Modifier.height(37.5.dp).width(45.dp)
        )
        Spacer(modifier = Modifier.width(62.8.dp))
        Text(
            text = "TV Guide",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
            fontSize = 17.sp
        )
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
    Text(
        text = currentTime,
        style = MaterialTheme.typography.titleLarge,
        color = Color.White,
        fontSize = 25.sp
    )
}

private fun getCurrentFormattedTime(): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Calendar.getInstance().time)
}

@OptIn(ExperimentalTvFoundationApi::class)
@Composable
fun ChannelList(channels: List<AppChannel>, modifier: Modifier = Modifier) {
    val focusRequester = remember { FocusRequester() }
    val tvListState = rememberTvLazyListState()

    TvLazyColumn(
        state = tvListState,
        modifier = modifier
            .focusRequester(focusRequester)
            .focusRestorer(),
        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
        //horizontalAlignment = Alignment.Start
    ) {
        items(channels, key = { channel -> channel.channelId }) { channel ->
            ChannelItem(channel = channel)
        }
    }

    LaunchedEffect(channels.isNotEmpty(), tvListState.layoutInfo.visibleItemsInfo.isNotEmpty()) {
        if (channels.isNotEmpty() && tvListState.layoutInfo.visibleItemsInfo.isNotEmpty()) {
            //delay(100)
            try {
                Log.d("ChannelList", "Requesting focus for TvLazyColumn (items visible)")
                focusRequester.requestFocus()
            } catch (e: Exception) {
                Log.e("ChannelList", "Error requesting focus: ${e.message}", e)
            }
        }
    }
}

@Composable
fun ChannelItem(channel: AppChannel) {
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.03f else 1.0f,
        animationSpec = tween(durationMillis = 150), label = "ChannelItemScale"
    )
    val animatedBorderColor by animateColorAsState(
        targetValue = if (isFocused) Color.White else Color.Transparent,
        animationSpec = tween(durationMillis = 150), label = "ChannelItemBorderColor"
    )
    val unfocusedCardColor = Color(0xFF323333)
    val focusedCardColor = Color(0xFFCEC7D1)
    val animatedContainerColor by animateColorAsState(
        targetValue = if (isFocused) focusedCardColor else unfocusedCardColor,
        animationSpec = tween(durationMillis = 150), label = "ChannelItemContainerColor"
    )
    val cardShape = RoundedCornerShape(11.dp)

    Card(
        modifier = Modifier
            .width(180.dp)
            .height(58.dp)
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
                if (isFocused) Log.d("ChannelItem", "Fokusiran: ${channel.name}")
            }
            .focusable(true)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .border(
                border = BorderStroke(if (isFocused) 1.5.dp else 0.dp, animatedBorderColor),
                shape = cardShape
            ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = if (isFocused) 8.dp else 2.dp),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = animatedContainerColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = channel.name,
                style = MaterialTheme.typography.bodySmall,
                color = if (isFocused) Color.White else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.width(2.dp))
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(channel.logo)
                    .crossfade(true)
                    //.error(R.drawable.ic_launcher_foreground)
                    .placeholder(R.drawable.ic_launcher_background)
                    .build(),
                contentDescription = "Logo kanala ${channel.name}",
                modifier = Modifier
                    .width(100.dp)
                    .height(50.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.FillBounds
            )
        }
    }
}*/





/////////////////////////////XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX////////////////////
//****************************************************************************************************************************///////////////////////////////

/*private val EPG_CHANNEL_ITEM_WIDTH = 220.dp
private val EPG_PROGRAM_ROW_HEIGHT = 50.dp
private val DP_PER_MINUTE = 1.8.dp
private val EPG_TIMELINE_HEIGHT = 40.dp
private val EPG_WINDOW_DURATION_HOURS = 28
private val EPG_START_OFFSET_HOURS = 4
private const val TIMELINE_SEGMENT_MINUTES = 30

@Composable
fun EPGScreen(viewModel: EPGViewModel) {
    val channelState by viewModel.channelState.collectAsState()
    val programState by viewModel.programState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFF121212)),
        contentAlignment = Alignment.Center
    ) {
        val isLoadingChannels = channelState is Resource.Loading
        val isLoadingPrograms = channelState is Resource.Success && programState is Resource.Loading


        if (isLoadingChannels || isLoadingPrograms) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = Color.Yellow)
                Text(
                    text = if (isLoadingChannels) "Loading channels..." else "Loading programs...",
                    style = TextStyle(
                        color = Color.Yellow, fontSize = 16.sp, fontWeight = FontWeight.Medium,
                        letterSpacing = (-0.5).sp
                    )
                )
            }
        } else if (channelState is Resource.Success) {
            val channels = (channelState as Resource.Success<List<AppChannel>>).data
            if (channels.isEmpty()) {
                Text("Nema dostupnih kanala.", color = Color.White)
            } else {
                val programs = if (programState is Resource.Success) {
                    (programState as Resource.Success<List<AppProgram>>).data
                } else {
                    emptyList()
                }
                EpgContent(channels = channels, programs = programs, viewModel = viewModel)
            }
        } else if (channelState is Resource.Error) {
            val errorState = channelState as Resource.Error
            Log.e("EPGScreen", "Stanje kanala: Greška - ${errorState.message}", errorState.error)
            ErrorStateDisplay(message = "Greška pri učitavanju kanala: ${errorState.message}") {
                viewModel.fetchChannelsAndInitialPrograms()
            }
        } else if (programState is Resource.Error && channelState !is Resource.Loading) {
            val errorState = programState as Resource.Error
            Log.e("EPGScreen", "Stanje programa: Greška - ${errorState.message}", errorState.error)
            ErrorStateDisplay(message = "Greška pri učitavanju programa: ${errorState.message}") {
                if (channelState is Resource.Success) {
                    val channels = (channelState as Resource.Success<List<AppChannel>>).data
                    if (channels.isNotEmpty()) {
                        viewModel.loadProgramsForChannels(channels)
                    } else {
                        viewModel.fetchChannelsAndInitialPrograms()
                    }
                } else {
                    viewModel.fetchChannelsAndInitialPrograms()
                }
            }
        }
    }
}

@Composable
fun ErrorStateDisplay(message: String?, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(message ?: "Nepoznata greška.", color = Color.Red, textAlign = TextAlign.Center)
        Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Pokušaj ponovo")
        }
    }
}

@OptIn(ExperimentalTvFoundationApi::class)
@Composable
fun EpgContent(
    channels: List<AppChannel>,
    programs: List<AppProgram>,
    viewModel: EPGViewModel
) {
    val epgWindowStartEpoch = remember {
        (System.currentTimeMillis() / 1000) - TimeUnit.HOURS.toSeconds(EPG_START_OFFSET_HOURS.toLong())
    }
    val epgWindowDurationSec = remember {
        TimeUnit.HOURS.toSeconds(EPG_WINDOW_DURATION_HOURS.toLong())
    }
    val sharedHorizontalScrollState = rememberTvLazyListState()
    val programsByChannelId = remember(programs) {
        programs.groupBy { it.channelId }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopHeader()
        TimelineHeader(
            scrollState = sharedHorizontalScrollState,
            epgWindowStartEpoch = epgWindowStartEpoch,
            totalDurationSec = epgWindowDurationSec,
            dpPerMinute = DP_PER_MINUTE,
            segmentMinutes = TIMELINE_SEGMENT_MINUTES
        )
        TvLazyColumn(
            modifier = Modifier.weight(1f).padding(top = 1.dp),
            verticalArrangement = Arrangement.spacedBy(1.2.dp)
        ) {
            items(channels, key = { it.channelId }) { channel ->
                EpgChannelRow(
                    channel = channel,
                    programsForThisChannel = programsByChannelId[channel.channelId] ?: emptyList(),
                    sharedHorizontalScrollState = sharedHorizontalScrollState,
                    epgWindowStartEpoch = epgWindowStartEpoch,
                    dpPerMinute = DP_PER_MINUTE,
                    rowHeight = EPG_PROGRAM_ROW_HEIGHT
                )
            }
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
        Image(
            painter = painterResource(id = R.drawable.iwedia_logo_white_02),
            contentDescription = "TV Guide Logo",
            modifier = Modifier.height(37.5.dp).width(45.91.dp)
        )
        Spacer(modifier = Modifier.width(20.dp))
        Text(text = "TV Guide", style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 26.sp, color = Color.White))
        Spacer(modifier = Modifier.weight(1f))
        CurrentTimeText()
    }
}

@Composable
fun CurrentTimeText() {
    var currentTime by remember { mutableStateOf(getCurrentFormattedTime()) }
    LaunchedEffect(Unit) { while (true) { delay(1000); currentTime = getCurrentFormattedTime() } }
    Text(text = currentTime, style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 26.sp, color = Color.White))
}

private fun getCurrentFormattedTime(): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Calendar.getInstance().time)

@OptIn(ExperimentalTvFoundationApi::class)
@Composable
fun TimelineHeader(
    scrollState: TvLazyListState,
    epgWindowStartEpoch: Long,
    totalDurationSec: Long,
    dpPerMinute: Dp,
    segmentMinutes: Int
) {
    val segmentDurationSec = TimeUnit.MINUTES.toSeconds(segmentMinutes.toLong())
    val segmentWidth = (segmentMinutes * dpPerMinute.value).dp

    TvLazyRow(
        state = scrollState,
        modifier = Modifier
            .fillMaxWidth()
            .height(EPG_TIMELINE_HEIGHT)
            .background(Color.Black.copy(alpha = 0.2f))
            .padding(start = EPG_CHANNEL_ITEM_WIDTH + 35.dp),
        horizontalArrangement = Arrangement.Absolute.Left
    ) {
        val numberOfSegments = (totalDurationSec / segmentDurationSec).toInt() + 1
        items(numberOfSegments) { segmentIndex ->
            val currentSegmentStartEpoch = epgWindowStartEpoch + (segmentIndex * segmentDurationSec)
            val displayCalendar = Calendar.getInstance().apply {
                timeInMillis = currentSegmentStartEpoch * 1000
            }
            val timeText = String.format(Locale.getDefault(), "%02d:%02d",
                displayCalendar.get(Calendar.HOUR_OF_DAY),
                displayCalendar.get(Calendar.MINUTE)
            )

            Box(
                modifier = Modifier
                    .width(segmentWidth)
                    .fillMaxHeight()
                    .border(BorderStroke(0.5.dp, Color.Gray.copy(alpha = 0.5f))),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = timeText,
                    color = Color.LightGray,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 6.dp, end = 6.dp),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}


@Composable
fun EpgChannelRow(
    channel: AppChannel,
    programsForThisChannel: List<AppProgram>,
    sharedHorizontalScrollState: TvLazyListState,
    epgWindowStartEpoch: Long,
    dpPerMinute: Dp,
    rowHeight: Dp
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(rowHeight)
            .background(Color.Transparent),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ChannelItem(
            channel = channel,
            modifier = Modifier
                .width(EPG_CHANNEL_ITEM_WIDTH)
                .fillMaxHeight()
                .padding(start = 35.dp, end = 8.dp, bottom = 8.dp)
        )
        ProgramsTvLazyRow(
            programs = programsForThisChannel,
            scrollState = sharedHorizontalScrollState,
            epgWindowStartEpoch = epgWindowStartEpoch,
            dpPerMinute = dpPerMinute,
            rowHeight = rowHeight,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )
    }
}

@Composable
fun ChannelItem(channel: AppChannel, modifier: Modifier = Modifier) {
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isFocused) 1f else 1f, tween(100), label = "ChannelItemScaleFocus")
    val containerColor by animateColorAsState(
        if (isFocused) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f) else Color.Transparent,
        tween(100), label = "ChannelItemContainerColorFocus"
    )
    val borderColor by animateColorAsState(
        if (isFocused) Color.White else Color.Transparent, tween(100), label = "ChannelItemBorderColorFocus"
    )

    Card(
        modifier = modifier
            .fillMaxSize()
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .border(BorderStroke(if (isFocused) 0.dp else 0.dp, borderColor), RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 1.dp, vertical = 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = channel.name,
                style = MaterialTheme.typography.bodySmall,
                color = if (isFocused) Color.White else Color.LightGray,
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            //Spacer(modifier = Modifier.width(4.dp))
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(channel.logo)
                    .crossfade(true)
                    //.error(R.drawable.ic_launcher_foreground)
                    .placeholder(R.drawable.ic_launcher_background)
                    .build(),
                contentDescription = channel.name,
                modifier = Modifier
                    .height(64.dp)
                    .width(95.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.FillBounds
            )
        }
    }
}

@OptIn(ExperimentalTvFoundationApi::class)
@Composable
fun ProgramsTvLazyRow(
    programs: List<AppProgram>,
    scrollState: TvLazyListState,
    epgWindowStartEpoch: Long,
    dpPerMinute: Dp,
    rowHeight: Dp,
    modifier: Modifier = Modifier
) {
    TvLazyRow(
        state = scrollState,
        modifier = modifier.fillMaxHeight(),
        contentPadding = PaddingValues(horizontal = 8.dp)/*,
        horizontalArrangement = Arrangement.Absolute.Left*/
    ) {
        /*val firstProgram = programs.minByOrNull { it.startTimeEpoch }
        if (firstProgram != null && firstProgram.startTimeEpoch > epgWindowStartEpoch) {
            val offsetSec = firstProgram.startTimeEpoch - epgWindowStartEpoch
            val offsetDp = ((offsetSec / 60f) * dpPerMinute.value).dp
            if (offsetDp > 0.dp) {
                item { Spacer(Modifier.width(offsetDp)) }
            }
        }*/

        items(programs, key = { program -> "${program.channelId}_${program.programId}_${program.startTimeEpoch}" }) { program ->
            ProgramCard(
                program = program,
                dpPerMinute = dpPerMinute,
                height = rowHeight
            )
        }
    }
}

@Composable
fun ProgramCard(program: AppProgram, dpPerMinute: Dp, height: Dp) {
    val programWidth = ((program.durationSec / 60f) * dpPerMinute.value).dp
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isFocused) 1.05f else 1f, tween(100), label = "ProgramCardScaleFocus")
    val containerColor by animateColorAsState(
        if (isFocused) MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        tween(100), label = "ProgramCardContainerColorFocus"
    )

    Card(
        modifier = Modifier
            .width(programWidth)
            .height(height - 4.dp)
            .padding(horizontal = 1.dp)
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = if (isFocused) BorderStroke(2.dp, Color.White) else null
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 4.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = program.title,
                color = if (isFocused) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}*/

//OFAHODFAOFAIDHOD***********************************33333333333333/////////////////////////////////////////////




// Konstante za EPG
/*private val EPG_SIDE_PADDING = 35.dp // Glavni levi padding za EPG sadržaj
private val EPG_CHANNEL_ITEM_WIDTH = 250.dp // Širina za ChannelItem kolonu (ime + logo)
private val EPG_PROGRAM_ROW_HEIGHT = 60.dp  // Visina svakog reda (ChannelItem i ProgramCard)
private val DP_PER_MINUTE = 2.5.dp // Koliko dp zauzima jedan minut programa
private val SPACE_BETWEEN_CHANNEL_AND_PROGRAMS = 8.dp // Razmak između ChannelItem i ProgramsTvLazyRow

private const val EPG_START_OFFSET_HOURS = 4L // Koliko sati unazad od trenutnog vremena počinje EPG (kao Long)

@Composable
fun EPGScreen(viewModel: EPGViewModel) {
    val channelState by viewModel.channelState.collectAsState()
    val programState by viewModel.programState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFF121212)),
        contentAlignment = Alignment.Center
    ) {
        val isLoadingChannels = channelState is Resource.Loading
        val isLoadingPrograms = (channelState is Resource.Success && programState is Resource.Loading)

        if (isLoadingChannels || isLoadingPrograms) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = Color.Yellow)
                Text(
                    text = if (isLoadingChannels) "Loading channels..." else "Loading programs...",
                    style = TextStyle(
                        color = Color.Yellow, fontSize = 16.sp, fontWeight = FontWeight.Medium,
                        letterSpacing = (-0.5).sp
                    )
                )
            }
        } else if (channelState is Resource.Success) {
            val channels = (channelState as Resource.Success<List<AppChannel>>).data
            if (channels.isEmpty()) {
                Text("Nema dostupnih kanala.", color = Color.White)
            } else {
                val programs = if (programState is Resource.Success) {
                    (programState as Resource.Success<List<AppProgram>>).data
                } else {
                    emptyList()
                }
                EpgContent(channels = channels, programs = programs, viewModel = viewModel)
            }
        } else if (channelState is Resource.Error) {
            val errorState = channelState as Resource.Error
            ErrorStateDisplay(message = "Greška pri učitavanju kanala: ${errorState.message}") {
                viewModel.fetchChannelsAndInitialPrograms()
            }
        } else if (programState is Resource.Error && channelState !is Resource.Loading) {
            val errorState = programState as Resource.Error
            ErrorStateDisplay(message = "Greška pri učitavanju programa: ${errorState.message}") {
                if (channelState is Resource.Success) {
                    val channels = (channelState as Resource.Success<List<AppChannel>>).data
                    if (channels.isNotEmpty()) {
                        viewModel.loadProgramsForChannels(channels)
                    } else {
                        viewModel.fetchChannelsAndInitialPrograms()
                    }
                } else {
                    viewModel.fetchChannelsAndInitialPrograms()
                }
            }
        }
    }
}

@Composable
fun ErrorStateDisplay(message: String?, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(message ?: "Nepoznata greška.", color = Color.Red, textAlign = TextAlign.Center)
        Button(onClick = onRetry, modifier = Modifier.padding(top = 16.dp)) {
            Text("Pokušaj ponovo")
        }
    }
}

@OptIn(ExperimentalTvFoundationApi::class)
@Composable
fun EpgContent(
    channels: List<AppChannel>,
    programs: List<AppProgram>,
    viewModel: EPGViewModel
) {
    val epgWindowStartEpoch = remember { (System.currentTimeMillis() / 1000) - TimeUnit.HOURS.toSeconds(EPG_START_OFFSET_HOURS.toLong()) }
    // Deljeno stanje za horizontalno skrolovanje (za sada se ne koristi jer nema TimelineHeader-a)
    // val sharedHorizontalScrollState = rememberTvLazyListState()
    val programsByChannelId = remember(programs) { programs.groupBy { it.channelId } }

    Column(modifier = Modifier.fillMaxSize()) {
        TopHeader()
        // TimelineHeader() // UKLONJENO

        TvLazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(top = 24.dp) // Razmak od TopHeader-a
                .padding(start = EPG_SIDE_PADDING), // Glavni levi padding za celu listu
            verticalArrangement = Arrangement.spacedBy(1.2.dp) // Razmak između redova kanala
        ) {
            items(channels, key = { it.channelId }) { channel ->
                EpgChannelRow(
                    channel = channel,
                    programsForThisChannel = programsByChannelId[channel.channelId] ?: emptyList(),
                    // sharedHorizontalScrollState = sharedHorizontalScrollState, // Ne treba za sada
                    epgWindowStartEpoch = epgWindowStartEpoch,
                    dpPerMinute = DP_PER_MINUTE,
                    rowHeight = EPG_PROGRAM_ROW_HEIGHT
                )
            }
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
        Image(
            painter = painterResource(id = R.drawable.iwedia_logo_white_02), // Tvoj logo
            contentDescription = "TV Guide Logo",
            modifier = Modifier.height(37.5.dp).width(45.91.dp)
        )
        Spacer(modifier = Modifier.width(20.dp))
        Text(text = "TV Guide", style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 26.sp, color = Color.White))
        Spacer(modifier = Modifier.weight(1f))
        CurrentTimeText()
    }
}

@Composable
fun CurrentTimeText() {
    var currentTime by remember { mutableStateOf(getCurrentFormattedTime()) }
    LaunchedEffect(Unit) { while (true) { delay(1000); currentTime = getCurrentFormattedTime() } }
    Text(text = currentTime, style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 26.sp, color = Color.White))
}

private fun getCurrentFormattedTime(): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Calendar.getInstance().time)

// TimelineHeader je uklonjen

@OptIn(ExperimentalTvFoundationApi::class)
@Composable
fun EpgChannelRow(
    channel: AppChannel,
    programsForThisChannel: List<AppProgram>,
    // sharedHorizontalScrollState: TvLazyListState, // Ne treba za sada
    epgWindowStartEpoch: Long,
    dpPerMinute: Dp,
    rowHeight: Dp
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(rowHeight)
            .background(Color.Transparent),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ChannelItem(
            channel = channel,
            modifier = Modifier
                .width(EPG_CHANNEL_ITEM_WIDTH) // Fiksna širina za ChannelItem kolonu
                .fillMaxHeight()
            // .padding(end = SPACE_BETWEEN_CHANNEL_AND_PROGRAMS) // Razmak će biti deo ProgramsTvLazyRow paddinga
        )
        ProgramsTvLazyRow(
            programs = programsForThisChannel,
            // scrollState = sharedHorizontalScrollState, // Ne treba za sada
            epgWindowStartEpoch = epgWindowStartEpoch,
            dpPerMinute = dpPerMinute,
            rowHeight = rowHeight,
            modifier = Modifier
                .weight(1f) // Zauzima preostalu širinu
                .fillMaxHeight()
                .padding(start = SPACE_BETWEEN_CHANNEL_AND_PROGRAMS) // Padding na početku programskog reda
                .focusRestorer()
        )
    }
}

@Composable
fun ChannelItem(channel: AppChannel, modifier: Modifier = Modifier) {
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isFocused) 1f else 1f, tween(100), label = "ChannelItemScaleFocus")
    val containerColor by animateColorAsState(
        if (isFocused) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f) else Color.Transparent,
        tween(100), label = "ChannelItemContainerColorFocus"
    )
    val borderColor by animateColorAsState(
        if (isFocused) Color.White else Color.Transparent, tween(100), label = "ChannelItemBorderColorFocus"
    )

    Card(
        modifier = modifier
            .fillMaxSize()
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .border(BorderStroke(if (isFocused) 1.5.dp else 0.dp, borderColor), RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 6.dp, vertical = 2.dp), // Smanjen padding
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = channel.name,
                style = MaterialTheme.typography.bodySmall, // Manji font
                color = if (isFocused) Color.White else Color.LightGray,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.width(4.dp)) // Manji razmak
            AsyncImage(
                model = channel.logo,
                    //.placeholder(R.drawable.ic_launcher_background),
                    //.error(R.drawable.ic_launcher_foreground).build(),
                contentDescription = channel.name,
                modifier = Modifier
                    .height(EPG_PROGRAM_ROW_HEIGHT - 8.dp) // Prilagođeno visini reda i paddingu
                    .width(70.dp)  // Prilagođena širina logoa
                    .clip(RoundedCornerShape(6.dp)),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@OptIn(ExperimentalTvFoundationApi::class)
@Composable
fun ProgramsTvLazyRow(
    programs: List<AppProgram>,
    // scrollState: TvLazyListState, // Ne treba za sada, svaki red ima svoj scroll
    epgWindowStartEpoch: Long,
    dpPerMinute: Dp,
    rowHeight: Dp,
    modifier: Modifier = Modifier
) {
    val rowScrollState = rememberTvLazyListState() // Svaki red sada ima svoje stanje skrolovanja

    TvLazyRow(
        state = rowScrollState,
        modifier = modifier.fillMaxHeight().focusRestorer(),
        contentPadding = PaddingValues(horizontal = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp) // Mali razmak između programskih kartica
    ) {
        val firstProgram = programs.minByOrNull { it.startTimeEpoch }
        if (firstProgram != null && firstProgram.startTimeEpoch > epgWindowStartEpoch) {
            val offsetSec = firstProgram.startTimeEpoch - epgWindowStartEpoch
            val offsetDp = ((offsetSec / 60f) * dpPerMinute.value).dp
            if (offsetDp > 0.dp) {
                item(key = "initial_spacer_${firstProgram.channelId}_${firstProgram.startTimeEpoch}") { Spacer(Modifier.width(offsetDp)) }
            }
        }

        items(programs, key = { program -> "program_${program.channelId}_${program.programId}_${program.startTimeEpoch}" }) { program ->
            ProgramCard(
                program = program,
                dpPerMinute = dpPerMinute,
                height = rowHeight
            )
        }
    }
}

@Composable
fun ProgramCard(program: AppProgram, dpPerMinute: Dp, height: Dp) {
    val programWidth = ((program.durationSec / 60f) * dpPerMinute.value).dp
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isFocused) 1.05f else 1f, tween(100), label = "ProgramCardScaleFocus")
    val containerColor by animateColorAsState(
        if (isFocused) MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        tween(100), label = "ProgramCardContainerColorFocus"
    )

    Card(
        modifier = Modifier
            .width(programWidth)
            .height(height - 4.dp) // Malo manje od visine reda
            // .padding(horizontal = 0.5.dp) // Uklonjen horizontalni padding ovde, rešeno sa spacedBy u TvLazyRow
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .graphicsLayer { scaleX = scale; scaleY = scale },
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = if (isFocused) BorderStroke(2.dp, Color.White) else null
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(horizontal = 6.dp, vertical = 4.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = program.title,
                color = if (isFocused) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                maxLines = 2, // Ostavljamo 2 linije za naslov programa
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}*/

////////////////////////////////*****************************BITAN KOD*******************************************///

// import java.util.TimeZone //
//import java.util.concurrent.TimeUnit
// NOVO: Lottie importi
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import androidx.compose.ui.graphics.Brush // gradijent linerni
import androidx.compose.ui.graphics.TileMode

// Konstante za EPG
/*private val EPG_SIDE_PADDING = 35.dp
private val EPG_CHANNEL_ITEM_WIDTH = 177.dp // 250
private val EPG_PROGRAM_ROW_HEIGHT = 60.dp
private val DP_PER_MINUTE = 6.dp
private val SPACE_BETWEEN_CHANNEL_AND_PROGRAMS = 8.dp

// Definicija boja za gradijent
val gradientStartColor = Color(0xFF1A1C1E)
val gradientMidColor = Color(0xFF1A1C1E)
val gradientEndColor = Color(0x001A1C1E) // #1A1C1E sa 00 alfa (transparentno)


@Composable
fun EPGScreen(viewModel: EPGViewModel) {
    val channelState by viewModel.channelState.collectAsState()
    val programState by viewModel.programState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Transparent // IZMENA: Surface je sada transparentan
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colorStops = arrayOf(
                            0.0f to gradientStartColor,  // Donji-levi
                            0.62f to gradientMidColor,   // Na 62% dijagonale
                            1.0f to gradientEndColor     // Gornji-desni
                        ),
                        start = Offset(0f, Float.POSITIVE_INFINITY), // Počinje od donjeg levog ugla
                        end = Offset(Float.POSITIVE_INFINITY, 0f),   // Završava u gornjem desnom uglu
                        tileMode = TileMode.Clamp // Sprečava ponavljanje gradijenta ako je Composable veći
                    )
                )
        ) {
            val isLoadingChannels = channelState is Resource.Loading
            val isLoadingPrograms = (channelState is Resource.Success && programState is Resource.Loading)

            if (isLoadingChannels || isLoadingPrograms) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    val composition by rememberLottieComposition(
                        LottieCompositionSpec.RawRes(R.raw.tv_lottie_animation)
                    )
                    LottieAnimation(
                        composition = composition,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier.size(270.dp)
                    )
                    Text(
                        text = if (isLoadingChannels) "Loading..." else "Loading...",
                        style = TextStyle(
                            color = Color.LightGray, fontSize = 16.sp, fontWeight = FontWeight.Medium,
                            letterSpacing = (-0.5).sp
                        )
                    )
                }
            } else if (channelState is Resource.Success) {
                val channels = (channelState as Resource.Success<List<AppChannel>>).data
                if (channels.isEmpty()) {
                    Text("Nema dostupnih kanala.", color = Color.White)
                } else {
                    val programs = if (programState is Resource.Success) {
                        (programState as Resource.Success<List<AppProgram>>).data
                    } else {
                        emptyList()
                    }
                    EpgContent(channels = channels, programs = programs, viewModel = viewModel)
                }
            } else if (channelState is Resource.Error) {
                val errorState = channelState as Resource.Error
                ErrorStateDisplay(message = "Greška pri učitavanju kanala: ${errorState.message}") {
                    viewModel.fetchChannelsAndInitialPrograms()
                }
            } else if (programState is Resource.Error && channelState !is Resource.Loading) {
                val errorState = programState as Resource.Error
                ErrorStateDisplay(message = "Greška pri učitavanju programa: ${errorState.message}") {
                    if (channelState is Resource.Success) {
                        val channels = (channelState as Resource.Success<List<AppChannel>>).data
                        if (channels.isNotEmpty()) {
                            viewModel.loadProgramsForChannels(channels)
                        } else {
                            viewModel.fetchChannelsAndInitialPrograms()
                        }
                    } else {
                        viewModel.fetchChannelsAndInitialPrograms()
                    }
                }
            }
        }
    }

}

@Composable
fun ErrorStateDisplay(message: String?, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(message ?: "Nepoznata greška.", color = Color.Red, textAlign = TextAlign.Center)
        Button(onClick = onRetry, modifier = Modifier.padding(top = 16.dp)) {
            Text("Pokušaj ponovo")
        }
    }
}


@Composable
fun EpgContent(
    channels: List<AppChannel>,
    programs: List<AppProgram>,
    viewModel: EPGViewModel
) {
    val programsByChannelId = remember(programs) { programs.groupBy { it.channelId } }
    val sharedProgramScrollState = viewModel.programScrollState.value // Dohvati deljeno stanje skrolovanja

    Column(modifier = Modifier.fillMaxSize()) {
        TopHeader()
        TvLazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(top = 24.dp)
                .padding(start = EPG_SIDE_PADDING),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            items(channels, key = { it.channelId }) { channel ->
                EpgChannelRow(
                    channel = channel,
                    programsForThisChannel = programsByChannelId[channel.channelId] ?: emptyList(),
                    dpPerMinute = DP_PER_MINUTE,
                    rowHeight = EPG_PROGRAM_ROW_HEIGHT,
                    sharedProgramScrollState =  sharedProgramScrollState// Dodaj parametar za deljeno stanje
                )
            }
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
        Image(
            painter = painterResource(id = R.drawable.iwedia_logo_white_02),
            contentDescription = "TV Guide Logo",
            modifier = Modifier.height(37.5.dp).width(45.91.dp)
        )
        Spacer(modifier = Modifier.width(40.dp))
        Text(text = "TV Guide", style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 20.sp, color = Color.White))
        Spacer(modifier = Modifier.weight(1f))
        CurrentTimeText()
    }
}

@Composable
fun CurrentTimeText() {
    var currentTime by remember { mutableStateOf(getCurrentFormattedTime()) }
    LaunchedEffect(Unit) { while (true) { delay(1000); currentTime = getCurrentFormattedTime() } }
    Text(text = currentTime, style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 24.sp, color = Color.White))
}

private fun getCurrentFormattedTime(): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Calendar.getInstance().time)


@Composable
fun EpgChannelRow(
    channel: AppChannel,
    programsForThisChannel: List<AppProgram>,
    dpPerMinute: Dp,
    rowHeight: Dp,
    sharedProgramScrollState: TvLazyListState // Dodaj parametar za deljeno stanje
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(rowHeight)
            .background(Color.Transparent),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ChannelItem(
            channel = channel,
            modifier = Modifier
                .width(EPG_CHANNEL_ITEM_WIDTH)
                .fillMaxHeight()
        )
        ProgramsTvLazyRow(
            programs = programsForThisChannel,
            dpPerMinute = dpPerMinute,
            rowHeight = rowHeight,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(start = SPACE_BETWEEN_CHANNEL_AND_PROGRAMS)
                .focusRestorer(),
            sharedProgramScrollState = sharedProgramScrollState // Prosledi deljeno stanje
        )
    }
}

@Composable
fun ChannelItem(channel: AppChannel, modifier: Modifier = Modifier) {
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isFocused) 1f else 1f, tween(100), label = "ChannelItemScaleFocus")
    val containerColor by animateColorAsState(
        if (isFocused) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f) else Color.Transparent,
        tween(100), label = "ChannelItemContainerColorFocus"
    )
    val borderColor by animateColorAsState(
        if (isFocused) Color.Gray else Color.Transparent, tween(100), label = "ChannelItemBorderColorFocus"
    )

    Card(
        modifier = modifier
            .fillMaxSize()
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .border(BorderStroke(if (isFocused) 0.05.dp else 0.dp, borderColor), RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp, vertical = 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = channel.name,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 10.sp,
                color = if (isFocused) Color.White else Color.LightGray,
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.StartEllipsis
            )
            Spacer(modifier = Modifier.width(1.5.dp))
            AsyncImage(
                model = channel.logo,
                contentDescription = channel.name,
                modifier = Modifier
                    .height(54.dp)
                    .width(88.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.FillBounds
            )
        }
    }
}


@Composable
fun ProgramsTvLazyRow(
    programs: List<AppProgram>,
    dpPerMinute: Dp,
    rowHeight: Dp,
    modifier: Modifier = Modifier,
    sharedProgramScrollState: TvLazyListState // Prihvati deljeno stanje
) {
    // Koristi prosleđeno stanje umesto da kreiraš novo
    val rowScrollState = sharedProgramScrollState


    TvLazyRow(
        state = rowScrollState,
        modifier = modifier.fillMaxHeight().focusRestorer(),
        //contentPadding = PaddingValues(horizontal = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {


        items(programs, key = { program -> "program_${program.channelId}_${program.programId}_${program.startTimeEpoch}" }) { program ->
            ProgramCard(
                program = program,
                dpPerMinute = dpPerMinute,
                height = rowHeight
            )
        }
    }
}

@Composable
fun ProgramCard(program: AppProgram, dpPerMinute: Dp, height: Dp) {
    val programWidth = ((program.durationSec / 60f) * dpPerMinute.value).dp
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isFocused) 1f else 1f, tween(100), label = "ProgramCardScaleFocus")
    val focuced_white = Color(0xFFB9B7B7)
    val unfocused_gray = Color(0xFF262525)
    val containerColor by animateColorAsState(
        if (isFocused)  focuced_white
        else unfocused_gray,
        tween(100), label = "ProgramCardContainerColorFocus"
    )

    Card(
        modifier = Modifier
            .width(programWidth)
            .height(height - 4.dp)
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .graphicsLayer { scaleX = scale; scaleY = scale },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
        //border = if (isFocused) BorderStroke(0.1.dp, Color.White) else null
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(horizontal = 6.dp, vertical = 4.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = program.title,
                style = MaterialTheme.typography.bodySmall,
                color = if (isFocused) Color.Black else Color.LightGray,
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}*/


///->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>DIJELJENI tvlazyliststate>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>//

/*private val EPG_SIDE_PADDING = 35.dp
private val EPG_CHANNEL_ITEM_WIDTH = 177.dp // 250
private val EPG_PROGRAM_ROW_HEIGHT = 60.dp
private val DP_PER_MINUTE = 6.dp
private val SPACE_BETWEEN_CHANNEL_AND_PROGRAMS = 8.dp

// Definicija boja za gradijent
val gradientStartColor = Color(0xFF1A1C1E)
val gradientMidColor = Color(0xFF1A1C1E)
val gradientEndColor = Color(0x001A1C1E)


@Composable
fun EPGScreen(viewModel: EPGViewModel) {
    val channelState by viewModel.channelState.collectAsState()
    val programState by viewModel.programState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colorStops = arrayOf(
                            0.0f to gradientStartColor,
                            0.62f to gradientMidColor,
                            1.0f to gradientEndColor
                        ),
                        start = Offset(0f, Float.POSITIVE_INFINITY),
                        end = Offset(Float.POSITIVE_INFINITY, 0f),
                        tileMode = TileMode.Clamp
                    )
                )
        ) {
            val isLoadingChannels = channelState is Resource.Loading
            val isLoadingPrograms = (channelState is Resource.Success && programState is Resource.Loading)

            if (isLoadingChannels || isLoadingPrograms) {
                Column(
                    modifier = Modifier.fillMaxSize(), // Dodato da se centira u celom Box-u
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center // Dodato da se centira vertikalno
                ) {
                    val composition by rememberLottieComposition(
                        LottieCompositionSpec.RawRes(R.raw.tv_lottie_animation)
                    )

                    LottieAnimation(
                        composition = composition,
                        iterations = LottieConstants.IterateForever
                        //modifier = Modifier.size(300.dp)
                    )
                    /*Text(
                        text = if (isLoadingChannels) "Loading channels..." else "Loading programs...",
                        style = TextStyle(
                            color = Color.LightGray, fontSize = 16.sp, fontWeight = FontWeight.Medium,
                            letterSpacing = (-0.5).sp
                        )
                    )*/
                }
            } else if (channelState is Resource.Success) {
                val channels = (channelState as Resource.Success<List<AppChannel>>).data
                if (channels.isEmpty()) {
                    Box( // Omotano u Box i centrirano
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Nema dostupnih kanala.", color = Color.White)
                    }
                } else {
                    val programs = if (programState is Resource.Success) {
                        (programState as Resource.Success<List<AppProgram>>).data
                    } else {
                        emptyList()
                    }
                    EpgContent(channels = channels, programs = programs, viewModel = viewModel)
                }
            } else if (channelState is Resource.Error) {
                val errorState = channelState as Resource.Error
                ErrorStateDisplay(message = "Greška pri učitavanju kanala: ${errorState.message}") {
                    viewModel.fetchChannelsAndInitialPrograms()
                }
            } else if (programState is Resource.Error && channelState !is Resource.Loading) {
                val errorState = programState as Resource.Error
                ErrorStateDisplay(message = "Greška pri učitavanju programa: ${errorState.message}") {
                    if (channelState is Resource.Success) {
                        val channels = (channelState as Resource.Success<List<AppChannel>>).data
                        if (channels.isNotEmpty()) {
                            viewModel.loadProgramsForChannels(channels)
                        } else {
                            viewModel.fetchChannelsAndInitialPrograms()
                        }
                    } else {
                        viewModel.fetchChannelsAndInitialPrograms()
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorStateDisplay(message: String?, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(message ?: "Nepoznata greška.", color = Color.Red, textAlign = TextAlign.Center)
        Button(onClick = onRetry, modifier = Modifier.padding(top = 16.dp)) {
            Text("Pokušaj ponovo")
        }
    }
}


@Composable
fun EpgContent(
    channels: List<AppChannel>,
    programs: List<AppProgram>,
    viewModel: EPGViewModel // Zadržavamo ViewModel za ostale logike, npr. ucitavanje programa
) {
    val programsByChannelId = remember(programs) { programs.groupBy { it.channelId } }
    // KREIRAJ JEDAN SHARED TvLazyListState KOJI SE KORISTI ZA SVE PROGRAM LAZY ROW-ove
    val sharedProgramScrollState = rememberTvLazyListState() // <-- Ovo je ključna promena

    Column(modifier = Modifier.fillMaxSize()) {
        TopHeader()
        TvLazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(top = 24.dp)
                .padding(start = EPG_SIDE_PADDING),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            items(channels, key = { it.channelId }) { channel ->
                EpgChannelRow(
                    channel = channel,
                    programsForThisChannel = programsByChannelId[channel.channelId] ?: emptyList(),
                    dpPerMinute = DP_PER_MINUTE,
                    rowHeight = EPG_PROGRAM_ROW_HEIGHT,
                    sharedProgramScrollState = sharedProgramScrollState // Prosledi deljeno stanje
                )
            }
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
        // PAZNJA: Proverite da li imate R.drawable.iwedia_logo_white_02 u svom projektu
        Image(
            painter = painterResource(id = R.drawable.iwedia_logo_white_02),
            contentDescription = "TV Guide Logo",
            modifier = Modifier.height(37.5.dp).width(45.91.dp)
        )
        Spacer(modifier = Modifier.width(40.dp))
        Text(text = "TV Guide", style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 20.sp, color = Color.White))
        Spacer(modifier = Modifier.weight(1f))
        CurrentTimeText()
    }
}

@Composable
fun CurrentTimeText() {
    var currentTime by remember { mutableStateOf(getCurrentFormattedTime()) }
    LaunchedEffect(Unit) { while (true) { delay(1000); currentTime = getCurrentFormattedTime() } }
    Text(text = currentTime, style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 24.sp, color = Color.White))
}

private fun getCurrentFormattedTime(): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Calendar.getInstance().time)



@Composable
fun EpgChannelRow(
    channel: AppChannel,
    programsForThisChannel: List<AppProgram>,
    dpPerMinute: Dp,
    rowHeight: Dp,
    sharedProgramScrollState: TvLazyListState // Prihvati deljeno stanje
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(rowHeight)
            .background(Color.Transparent),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ChannelItem(
            channel = channel,
            modifier = Modifier
                .width(EPG_CHANNEL_ITEM_WIDTH)
                .fillMaxHeight()
        )
        ProgramsTvLazyRow(
            programs = programsForThisChannel,
            dpPerMinute = dpPerMinute,
            rowHeight = rowHeight,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(start = SPACE_BETWEEN_CHANNEL_AND_PROGRAMS),
                //.focusRestorer(),
            sharedProgramScrollState = sharedProgramScrollState // Prosledi deljeno stanje
        )
    }
}

@Composable
fun ChannelItem(channel: AppChannel, modifier: Modifier = Modifier) {
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isFocused) 1f else 1f, tween(100), label = "ChannelItemScaleFocus")
    val containerColor by animateColorAsState(
        if (isFocused) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f) else Color.Transparent,
        tween(100), label = "ChannelItemContainerColorFocus"
    )
    val borderColor by animateColorAsState(
        if (isFocused) Color.Gray else Color.Transparent, tween(100), label = "ChannelItemBorderColorFocus"
    )

    Card(
        modifier = modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            //.fillMaxSize()
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .border(BorderStroke(if (isFocused) 0.05.dp else 0.dp, borderColor), RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp, vertical = 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = channel.name,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 10.sp,
                color = if (isFocused) Color.White else Color.LightGray,
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.StartEllipsis
            )
            Spacer(modifier = Modifier.width(1.5.dp))
            AsyncImage(
                model = channel.logo,
                contentDescription = channel.name,
                modifier = Modifier
                    .height(54.dp)
                    .width(88.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.FillBounds
            )
        }
    }
}



@Composable
fun ProgramsTvLazyRow(
    programs: List<AppProgram>,
    dpPerMinute: Dp,
    rowHeight: Dp,
    modifier: Modifier = Modifier,
    sharedProgramScrollState: TvLazyListState // Prihvati deljeno stanje
) {
    // KORISTI PROSLEĐENO STANJE UMESTO DA KREIRAŠ NOVO
    // val rowScrollState = rememberTvLazyListState() // <-- UKLONJENO
    val rowScrollState = sharedProgramScrollState // <-- KORIŠĆENO PROSLEĐENO STANJE

    TvLazyRow(
        state = rowScrollState, // Koristi deljeno stanje
        modifier = modifier.fillMaxHeight()//.focusRestorer(),
        //contentPadding = PaddingValues(horizontal = 2.dp),
        ,horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(programs, key = { program -> "program_${program.channelId}_${program.programId}_${program.startTimeEpoch}" }) { program ->
            ProgramCard(
                program = program,
                dpPerMinute = dpPerMinute,
                height = rowHeight
            )
        }
    }
}

@Composable
fun ProgramCard(program: AppProgram, dpPerMinute: Dp, height: Dp) {
    val programWidth = ((program.durationSec / 60f) * dpPerMinute.value).dp
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isFocused) 1f else 1f, tween(100), label = "ProgramCardScaleFocus")
    val focuced_white = Color(0xFFB9B7B7)
    //val unfocused_gray = Color(0xFF262525)
    val unfocused_gray = Color(0xFF1E2329)
    val containerColor by animateColorAsState(
        if (isFocused)  focuced_white
        else unfocused_gray,
        tween(100), label = "ProgramCardContainerColorFocus"
    )

    Card(
        modifier = Modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .width(programWidth)
            .height(height - 4.dp)

            .onFocusChanged { isFocused = it.isFocused }
            .focusable(),

        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
        //border = if (isFocused) BorderStroke(0.1.dp, Color.White) else null
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(horizontal = 6.dp, vertical = 4.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = program.title,
                style = MaterialTheme.typography.bodySmall,
                color = if (isFocused) Color.Black else Color.LightGray,
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}*/

// RUCNI SCROLL OFFSET-----------------------------------------------------------------------------------------------------------------//
/*private val EPG_SIDE_PADDING = 35.dp
private val EPG_CHANNEL_ITEM_WIDTH = 177.dp // 250
private val EPG_PROGRAM_ROW_HEIGHT = 60.dp
private val DP_PER_MINUTE = 6.dp
private val SPACE_BETWEEN_CHANNEL_AND_PROGRAMS = 8.dp

// Definicija boja za gradijent
val gradientStartColor = Color(0xFF1A1C1E)
val gradientMidColor = Color(0xFF1A1C1E)
val gradientEndColor = Color(0x001A1C1E)


@Composable
fun EPGScreen(viewModel: EPGViewModel) {
    val channelState by viewModel.channelState.collectAsState()
    val programState by viewModel.programState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colorStops = arrayOf(
                            0.0f to gradientStartColor,
                            0.62f to gradientMidColor,
                            1.0f to gradientEndColor
                        ),
                        start = Offset(0f, Float.POSITIVE_INFINITY),
                        end = Offset(Float.POSITIVE_INFINITY, 0f),
                        tileMode = TileMode.Clamp
                    )
                )
        ) {
            val isLoadingChannels = channelState is Resource.Loading
            val isLoadingPrograms = (channelState is Resource.Success && programState is Resource.Loading)

            if (isLoadingChannels || isLoadingPrograms) {
                Column(
                    modifier = Modifier.fillMaxSize(), // Dodato da se centira u celom Box-u
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center // Dodato da se centira vertikalno
                ) {
                    val composition by rememberLottieComposition(
                        LottieCompositionSpec.RawRes(R.raw.tv_lottie_animation)
                    )

                    LottieAnimation(
                        composition = composition,
                        iterations = LottieConstants.IterateForever
                        //modifier = Modifier.size(300.dp)
                    )
                    /*Text(
                        text = if (isLoadingChannels) "Loading channels..." else "Loading programs...",
                        style = TextStyle(
                            color = Color.LightGray, fontSize = 16.sp, fontWeight = FontWeight.Medium,
                            letterSpacing = (-0.5).sp
                        )
                    )*/
                }
            } else if (channelState is Resource.Success) {
                val channels = (channelState as Resource.Success<List<AppChannel>>).data
                if (channels.isEmpty()) {
                    Box( // Omotano u Box i centrirano
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Nema dostupnih kanala.", color = Color.White)
                    }
                } else {
                    val programs = if (programState is Resource.Success) {
                        (programState as Resource.Success<List<AppProgram>>).data
                    } else {
                        emptyList()
                    }
                    EpgContent(channels = channels, programs = programs, viewModel = viewModel)
                }
            } else if (channelState is Resource.Error) {
                val errorState = channelState as Resource.Error
                ErrorStateDisplay(message = "Greška pri učitavanju kanala: ${errorState.message}") {
                    viewModel.fetchChannelsAndInitialPrograms()
                }
            } else if (programState is Resource.Error && channelState !is Resource.Loading) {
                val errorState = programState as Resource.Error
                ErrorStateDisplay(message = "Greška pri učitavanju programa: ${errorState.message}") {
                    if (channelState is Resource.Success) {
                        val channels = (channelState as Resource.Success<List<AppChannel>>).data
                        if (channels.isNotEmpty()) {
                            viewModel.loadProgramsForChannels(channels)
                        } else {
                            viewModel.fetchChannelsAndInitialPrograms()
                        }
                    } else {
                        viewModel.fetchChannelsAndInitialPrograms()
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorStateDisplay(message: String?, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(message ?: "Nepoznata greška.", color = Color.Red, textAlign = TextAlign.Center)
        Button(onClick = onRetry, modifier = Modifier.padding(top = 16.dp)) {
            Text("Pokušaj ponovo")
        }
    }
}


@Composable
fun EpgContent(
    channels: List<AppChannel>,
    programs: List<AppProgram>,
    viewModel: EPGViewModel
) {
    val programsByChannelId = remember(programs) { programs.groupBy { it.channelId } }
    // KREIRAJ JEDAN SHARED TvLazyListState KOJI SE KORISTI ZA SVE PROGRAM LAZY ROW-ove
    // Čuvamo stanje svih redova
    val rowStates = remember { mutableStateMapOf<Int, TvLazyListState>() }
    // Globalna referenca na lider red
    var leaderIndex by remember { mutableStateOf(-1) }
    var currentScrollIndex by remember { mutableStateOf(0) }
    var currentScrollOffset by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopHeader()
        TvLazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(top = 24.dp)
                .padding(start = EPG_SIDE_PADDING),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            itemsIndexed(channels, key = {_, channel -> channel.channelId }) {index,  channel ->

                // Zapamti scroll state za ovaj red
                val state = rowStates.getOrPut(index) { rememberTvLazyListState() }
                // Kada se lider pomera – ažuriraj poziciju
                LaunchedEffect(state.firstVisibleItemIndex, state.firstVisibleItemScrollOffset) {
                    if (leaderIndex == index && !state.isScrollInProgress) {
                        currentScrollIndex = state.firstVisibleItemIndex
                        currentScrollOffset = state.firstVisibleItemScrollOffset
                    }
                }

                // Sinhronizuj ostale redove
                LaunchedEffect(currentScrollIndex, currentScrollOffset) {
                    if (leaderIndex != index && !state.isScrollInProgress) {
                        state.animateScrollToItem(currentScrollIndex, currentScrollOffset)
                    }
                }

                EpgChannelRow(
                    channel = channel,
                    programsForThisChannel = programsByChannelId[channel.channelId] ?: emptyList(),
                    dpPerMinute = DP_PER_MINUTE,
                    rowHeight = EPG_PROGRAM_ROW_HEIGHT,
                    rowScrollState = state,
                    onScrollInteraction = {
                        leaderIndex = index
                    }

                )
            }
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

        Image(
            painter = painterResource(id = R.drawable.iwedia_logo_white_02),
            contentDescription = "TV Guide Logo",
            modifier = Modifier.height(37.5.dp).width(45.91.dp)
        )
        Spacer(modifier = Modifier.width(40.dp))
        Text(text = "TV Guide", style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 20.sp, color = Color.White))
        Spacer(modifier = Modifier.weight(1f))
        CurrentTimeText()
    }
}

@Composable
fun CurrentTimeText() {
    var currentTime by remember { mutableStateOf(getCurrentFormattedTime()) }
    LaunchedEffect(Unit) { while (true) { delay(1000); currentTime = getCurrentFormattedTime() } }
    Text(text = currentTime, style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 24.sp, color = Color.White))
}

private fun getCurrentFormattedTime(): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Calendar.getInstance().time)



@Composable
fun EpgChannelRow(
    channel: AppChannel,
    programsForThisChannel: List<AppProgram>,
    dpPerMinute: Dp,
    rowHeight: Dp,
    rowScrollState : TvLazyListState,
    onScrollInteraction : () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(rowHeight)
            .background(Color.Transparent),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ChannelItem(
            channel = channel,
            modifier = Modifier
                .width(EPG_CHANNEL_ITEM_WIDTH)
                .fillMaxHeight()
        )
        ProgramsTvLazyRow(
            programs = programsForThisChannel,
            dpPerMinute = dpPerMinute,
            rowHeight = rowHeight,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(start = SPACE_BETWEEN_CHANNEL_AND_PROGRAMS),
            rowScrollState = rowScrollState,
            onScrollInteraction = onScrollInteraction
        )
    }
}

@Composable
fun ChannelItem(channel: AppChannel, modifier: Modifier = Modifier) {
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isFocused) 1f else 1f, tween(100), label = "ChannelItemScaleFocus")
    val containerColor by animateColorAsState(
        if (isFocused) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f) else Color.Transparent,
        tween(100), label = "ChannelItemContainerColorFocus"
    )
    val borderColor by animateColorAsState(
        if (isFocused) Color.Gray else Color.Transparent, tween(100), label = "ChannelItemBorderColorFocus"
    )

    Card(
        modifier = modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            //.fillMaxSize()
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .border(BorderStroke(if (isFocused) 0.05.dp else 0.dp, borderColor), RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp, vertical = 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = channel.name,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 10.sp,
                color = if (isFocused) Color.White else Color.LightGray,
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.StartEllipsis
            )
            Spacer(modifier = Modifier.width(1.5.dp))
            AsyncImage(
                model = channel.logo,
                contentDescription = channel.name,
                modifier = Modifier
                    .height(54.dp)
                    .width(88.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.FillBounds
            )
        }
    }
}



@Composable
fun ProgramsTvLazyRow(
    programs: List<AppProgram>,
    dpPerMinute: Dp,
    rowHeight: Dp,
    modifier: Modifier = Modifier,
    rowScrollState: TvLazyListState,
    onScrollInteraction: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    // Detekcija korisničkog pomeranja
    LaunchedEffect(rowScrollState.isScrollInProgress) {
        if (rowScrollState.isScrollInProgress) {
            onScrollInteraction()
        }
    }


    TvLazyRow(
        state = rowScrollState, // Koristi deljeno stanje
        modifier = modifier.fillMaxHeight()//.focusRestorer(),
        //contentPadding = PaddingValues(horizontal = 2.dp),
        ,horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(programs, key = { program -> "program_${program.channelId}_${program.programId}_${program.startTimeEpoch}" }) { program ->
            ProgramCard(
                program = program,
                dpPerMinute = dpPerMinute,
                height = rowHeight
            )
        }
    }
}

@Composable
fun ProgramCard(program: AppProgram, dpPerMinute: Dp, height: Dp) {
    val programWidth = ((program.durationSec / 60f) * dpPerMinute.value).dp
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isFocused) 1f else 1f, tween(100), label = "ProgramCardScaleFocus")
    val focuced_white = Color(0xFFB9B7B7)
    //val unfocused_gray = Color(0xFF262525)
    val unfocused_gray = Color(0xFF1E2329)
    val containerColor by animateColorAsState(
        if (isFocused)  focuced_white
        else unfocused_gray,
        tween(100), label = "ProgramCardContainerColorFocus"
    )

    Card(
        modifier = Modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .width(programWidth)
            .height(height - 4.dp)

            .onFocusChanged { isFocused = it.isFocused }
            .focusable(),

        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
        //border = if (isFocused) BorderStroke(0.1.dp, Color.White) else null
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(horizontal = 6.dp, vertical = 4.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = program.title,
                style = MaterialTheme.typography.bodySmall,
                color = if (isFocused) Color.Black else Color.LightGray,
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}*/

// RUCNI SCROLL OFFSET-----------------------------------------------------------------------------------------------------------------//
/*private val EPG_SIDE_PADDING = 35.dp
private val EPG_CHANNEL_ITEM_WIDTH = 177.dp // 250
private val EPG_PROGRAM_ROW_HEIGHT = 60.dp
private val DP_PER_MINUTE = 6.dp
private val SPACE_BETWEEN_CHANNEL_AND_PROGRAMS = 8.dp


val gradientStartColor = Color(0xFF1A1C1E)
val gradientMidColor = Color(0xFF1A1C1E)
val gradientEndColor = Color(0x001A1C1E)


@Composable
fun EPGScreen(viewModel: EPGViewModel) {
    val channelState by viewModel.channelState.collectAsState()
    val programState by viewModel.programState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colorStops = arrayOf(
                            0.0f to gradientStartColor,
                            0.62f to gradientMidColor,
                            1.0f to gradientEndColor
                        ),
                        start = Offset(0f, Float.POSITIVE_INFINITY),
                        end = Offset(Float.POSITIVE_INFINITY, 0f),
                        tileMode = TileMode.Clamp
                    )
                )
        ) {
            val isLoadingChannels = channelState is Resource.Loading
            val isLoadingPrograms = (channelState is Resource.Success && programState is Resource.Loading)

            if (isLoadingChannels || isLoadingPrograms) {
                Column(
                    modifier = Modifier.fillMaxSize(), // Dodato da se centira u celom Box-u
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center // Dodato da se centira vertikalno
                ) {
                    val composition by rememberLottieComposition(
                        LottieCompositionSpec.RawRes(R.raw.tv_lottie_animation)
                    )

                    LottieAnimation(
                        composition = composition,
                        iterations = LottieConstants.IterateForever
                        //modifier = Modifier.size(300.dp)
                    )
                    /*Text(
                        text = if (isLoadingChannels) "Loading channels..." else "Loading programs...",
                        style = TextStyle(
                            color = Color.LightGray, fontSize = 16.sp, fontWeight = FontWeight.Medium,
                            letterSpacing = (-0.5).sp
                        )
                    )*/
                }
            } else if (channelState is Resource.Success) {
                val channels = (channelState as Resource.Success<List<AppChannel>>).data
                if (channels.isEmpty()) {
                    Box( // Omotano u Box i centrirano
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Nema dostupnih kanala.", color = Color.White)
                    }
                } else {
                    val programs = if (programState is Resource.Success) {
                        (programState as Resource.Success<List<AppProgram>>).data
                    } else {
                        emptyList()
                    }
                    EpgContent(channels = channels, programs = programs, viewModel = viewModel)
                }
            } else if (channelState is Resource.Error) {
                val errorState = channelState as Resource.Error
                ErrorStateDisplay(message = "Greška pri učitavanju kanala: ${errorState.message}") {
                    viewModel.fetchChannelsAndInitialPrograms()
                }
            } else if (programState is Resource.Error && channelState !is Resource.Loading) {
                val errorState = programState as Resource.Error
                ErrorStateDisplay(message = "Greška pri učitavanju programa: ${errorState.message}") {
                    if (channelState is Resource.Success) {
                        val channels = (channelState as Resource.Success<List<AppChannel>>).data
                        if (channels.isNotEmpty()) {
                            viewModel.loadProgramsForChannels(channels)
                        } else {
                            viewModel.fetchChannelsAndInitialPrograms()
                        }
                    } else {
                        viewModel.fetchChannelsAndInitialPrograms()
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorStateDisplay(message: String?, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(message ?: "Nepoznata greška.", color = Color.Red, textAlign = TextAlign.Center)
        Button(onClick = onRetry, modifier = Modifier.padding(top = 16.dp)) {
            Text("Pokušaj ponovo")
        }
    }
}


@Composable
fun EpgContent(
    channels: List<AppChannel>,
    programs: List<AppProgram>,
    viewModel: EPGViewModel
) {
    val programsByChannelId = remember(programs) { programs.groupBy { it.channelId } }
    // KREIRAJ JEDAN SHARED TvLazyListState KOJI SE KORISTI ZA SVE PROGRAM LAZY ROW-ove
    // Čuvamo stanje svih redova
    val rowStates = remember { mutableStateMapOf<Int, TvLazyListState>() }
    // Globalna referenca na lider red
    var leaderIndex by remember { mutableStateOf(-1) }
    var currentScrollIndex by remember { mutableStateOf(0) }
    var currentScrollOffset by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopHeader()
        TvLazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(top = 24.dp)
                .padding(start = EPG_SIDE_PADDING),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            itemsIndexed(channels, key = {_, channel -> channel.channelId }) {index,  channel ->

                // Zapamti scroll state za ovaj red
                val state = rowStates.getOrPut(index) { rememberTvLazyListState() }
                // Kada se lider pomera – ažuriraj poziciju
                LaunchedEffect(state.firstVisibleItemIndex, state.firstVisibleItemScrollOffset) {
                    if (leaderIndex == index && !state.isScrollInProgress) {
                        currentScrollIndex = state.firstVisibleItemIndex
                        currentScrollOffset = state.firstVisibleItemScrollOffset
                    }
                }

                // Sinhronizuj ostale redove
                LaunchedEffect(currentScrollIndex, currentScrollOffset) {
                    if (leaderIndex != index && !state.isScrollInProgress) {
                        state.animateScrollToItem(currentScrollIndex, currentScrollOffset)
                    }
                }

                EpgChannelRow(
                    channel = channel,
                    programsForThisChannel = programsByChannelId[channel.channelId] ?: emptyList(),
                    dpPerMinute = DP_PER_MINUTE,
                    rowHeight = EPG_PROGRAM_ROW_HEIGHT,
                    rowScrollState = state,
                    onScrollInteraction = {
                        leaderIndex = index
                    }

                )
            }
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

        Image(
            painter = painterResource(id = R.drawable.iwedia_logo_white_02),
            contentDescription = "TV Guide Logo",
            modifier = Modifier.height(37.5.dp).width(45.91.dp)
        )
        Spacer(modifier = Modifier.width(40.dp))
        Text(text = "TV Guide", style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 20.sp, color = Color.White))
        Spacer(modifier = Modifier.weight(1f))
        CurrentTimeText()
    }
}

@Composable
fun CurrentTimeText() {
    var currentTime by remember { mutableStateOf(getCurrentFormattedTime()) }
    LaunchedEffect(Unit) { while (true) { delay(1000); currentTime = getCurrentFormattedTime() } }
    Text(text = currentTime, style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 24.sp, color = Color.White))
}

private fun getCurrentFormattedTime(): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Calendar.getInstance().time)



@Composable
fun EpgChannelRow(
    channel: AppChannel,
    programsForThisChannel: List<AppProgram>,
    dpPerMinute: Dp,
    rowHeight: Dp,
    rowScrollState : TvLazyListState,
    onScrollInteraction : () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(rowHeight)
            .background(Color.Transparent),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ChannelItem(
            channel = channel,
            modifier = Modifier
                .width(EPG_CHANNEL_ITEM_WIDTH)
                .fillMaxHeight()
        )
        ProgramsTvLazyRow(
            programs = programsForThisChannel,
            dpPerMinute = dpPerMinute,
            rowHeight = rowHeight,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(start = SPACE_BETWEEN_CHANNEL_AND_PROGRAMS),
            rowScrollState = rowScrollState,
            onScrollInteraction = onScrollInteraction
        )
    }
}

@Composable
fun ChannelItem(channel: AppChannel, modifier: Modifier = Modifier) {
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isFocused) 1f else 1f, tween(100), label = "ChannelItemScaleFocus")
    val containerColor by animateColorAsState(
        if (isFocused) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f) else Color.Transparent,
        tween(100), label = "ChannelItemContainerColorFocus"
    )
    val borderColor by animateColorAsState(
        if (isFocused) Color.Gray else Color.Transparent, tween(100), label = "ChannelItemBorderColorFocus"
    )

    Card(
        modifier = modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            //.fillMaxSize()
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .border(BorderStroke(if (isFocused) 0.05.dp else 0.dp, borderColor), RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp, vertical = 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = channel.name,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 10.sp,
                color = if (isFocused) Color.White else Color.LightGray,
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.StartEllipsis
            )
            Spacer(modifier = Modifier.width(1.5.dp))
            AsyncImage(
                model = channel.logo,
                contentDescription = channel.name,
                modifier = Modifier
                    .height(54.dp)
                    .width(88.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.FillBounds
            )
        }
    }
}



@Composable
fun ProgramsTvLazyRow(
    programs: List<AppProgram>,
    dpPerMinute: Dp,
    rowHeight: Dp,
    modifier: Modifier = Modifier,
    rowScrollState: TvLazyListState,
    onScrollInteraction: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    // Detekcija korisničkog pomeranja
    LaunchedEffect(rowScrollState.isScrollInProgress) {
        if (rowScrollState.isScrollInProgress) {
            onScrollInteraction()
        }
    }


    TvLazyRow(
        state = rowScrollState, // Koristi deljeno stanje
        modifier = modifier.fillMaxHeight()//.focusRestorer(),
        //contentPadding = PaddingValues(horizontal = 2.dp),
        ,horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(programs, key = { program -> "program_${program.channelId}_${program.programId}_${program.startTimeEpoch}" }) { program ->
            ProgramCard(
                program = program,
                dpPerMinute = dpPerMinute,
                height = rowHeight
            )
        }
    }
}

@Composable
fun ProgramCard(program: AppProgram, dpPerMinute: Dp, height: Dp) {
    val programWidth = ((program.durationSec / 60f) * dpPerMinute.value).dp
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        if (isFocused) 1f else 1f,
        tween(100),
        label = "ProgramCardScaleFocus"
    )
    val focuced_white = Color(0xFFB9B7B7)
    //val unfocused_gray = Color(0xFF262525)
    val unfocused_gray = Color(0xFF1E2329)
    val containerColor by animateColorAsState(
        if (isFocused) focuced_white
        else unfocused_gray,
        tween(100), label = "ProgramCardContainerColorFocus"
    )

    Card(
        modifier = Modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .width(programWidth)
            .height(height - 4.dp)

            .onFocusChanged { isFocused = it.isFocused }
            .focusable(),

        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
        //border = if (isFocused) BorderStroke(0.1.dp, Color.White) else null
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(horizontal = 6.dp, vertical = 4.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = program.title,
                style = MaterialTheme.typography.bodySmall,
                color = if (isFocused) Color.Black else Color.LightGray,
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}*/





// POSLEDNJA VERZIJA*******************************************************//




/*private val EPG_SIDE_PADDING = 35.dp
private val EPG_CHANNEL_ITEM_WIDTH = 177.dp
private val EPG_PROGRAM_ROW_HEIGHT = 60.dp
private val DP_PER_MINUTE = 6.dp
private val SPACE_BETWEEN_CHANNEL_AND_PROGRAMS = 4.dp //8


val gradientStartColor = Color(0xFF1A1C1E)



@Composable
fun EPGScreen(viewModel: EPGViewModel) {

    val channelState by viewModel.channelState.collectAsState()
    val programState by viewModel.programState.collectAsState()
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A1C1E)),

        ) {
            val isLoadingChannels = channelState is Resource.Loading
            val isLoadingPrograms = (channelState is Resource.Success && programState is Resource.Loading)

            if (isLoadingChannels || isLoadingPrograms) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val composition by rememberLottieComposition(
                    LottieCompositionSpec.RawRes(R.raw.tv_lottie_animation)
                    )
                    LottieAnimation(
                        composition = composition,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier.size(380.dp)
                    )

                }
            } else if (channelState is Resource.Success) {
                val channels = (channelState as Resource.Success<List<AppChannel>>).data
                if (channels.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Nema dostupnih kanala.", color = Color.White)
                    }
                } else {
                    val programs = if (programState is Resource.Success) {
                        (programState as Resource.Success<List<AppProgram>>).data
                    } else {
                        emptyList()
                    }
                    EpgContent(channels = channels, programs = programs, viewModel = viewModel)
                }
            } else if (channelState is Resource.Error) {
                val errorState = channelState as Resource.Error
                ErrorStateDisplay(message = "Greška pri učitavanju kanala: ${errorState.message}") {
                    viewModel.fetchChannelsAndInitialPrograms()
                }
            } else if (programState is Resource.Error && channelState !is Resource.Loading) {
                val errorState = programState as Resource.Error
                ErrorStateDisplay(message = "Greška pri učitavanju programa: ${errorState.message}") {
                    if (channelState is Resource.Success) {
                        val currentChannels = (channelState as Resource.Success<List<AppChannel>>).data
                        if (currentChannels?.isNotEmpty() == true) {
                            viewModel.loadProgramsForChannels(currentChannels)
                        } else {
                            viewModel.fetchChannelsAndInitialPrograms()
                        }
                    } else {
                        viewModel.fetchChannelsAndInitialPrograms()
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorStateDisplay(message: String?, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(message ?: "Nepoznata greška.", color = Color.Red, textAlign = TextAlign.Center)
        Button(onClick = onRetry, modifier = Modifier.padding(top = 16.dp)) {
            Text("Pokušaj ponovo")
        }
    }
}




@Composable
fun EpgContent(
    channels: List<AppChannel>,
    programs: List<AppProgram>,
    viewModel: EPGViewModel
) {
    val programsByChannelId = remember(programs) {
        programs.groupBy { it.channelId }
            .mapValues { entry -> // Za svaku listu programa unutar mape...
                entry.value.sortedBy { it.startTimeEpoch } // ...sortiraj je po vremenu početka
            }
    }
    //val programsByChannelId = remember(programs) { programs.groupBy { it.channelId } }
    val sharedHorizontalScrollState = rememberScrollState()
    // Stanje za čuvanje URL-a logoa trenutno fokusiranog kanala
    var focusedChannelLogoUrl by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {

        val imageOverallAlpha = 0.21f
        val imageFadeEdgeLength = 70.dp
        val imageFadeToColor = gradientStartColor

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 0.dp, end = 0.dp)
                .width(500.dp)
                .height(320.dp)
        ) {

            Image(
                painter = painterResource(id = R.drawable.picturee),
                contentDescription = "Pozadinska slika EPG-a",
                modifier = Modifier
                    .matchParentSize()
                    .alpha(imageOverallAlpha),
                contentScale = ContentScale.Crop
            )


            Box(
                Modifier
                    .align(Alignment.CenterStart)
                    .width(imageFadeEdgeLength)
                    .fillMaxHeight()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(imageFadeToColor, Color.Transparent)
                        )
                    )
            )


            Box(
                Modifier
                    .align(Alignment.TopCenter)
                    .height(imageFadeEdgeLength)
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(imageFadeToColor, Color.Transparent)
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
                            colors = listOf(Color.Transparent, imageFadeToColor)
                        )
                    )
            )



            Box(
                Modifier
                    .align(Alignment.CenterEnd)
                    .width(imageFadeEdgeLength)
                    .fillMaxHeight()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color.Transparent, imageFadeToColor)
                        )
                    )
            )

        }

        Column(modifier = Modifier.fillMaxSize()) {
            TopHeader()
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                TvLazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 24.dp)
                        .padding(start = EPG_SIDE_PADDING),
                    state = rememberTvLazyListState(),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    itemsIndexed(channels, key = { _, channel -> channel.channelId }) { _, channel ->
                        EpgChannelRow(
                            channel = channel,
                            programsForThisChannel = programsByChannelId[channel.channelId] ?: emptyList(),
                            dpPerMinute = DP_PER_MINUTE,
                            rowHeight = EPG_PROGRAM_ROW_HEIGHT,
                            externalHorizontalScrollState = sharedHorizontalScrollState
                        )
                    }
                }
            }
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

        Image(
            painter = painterResource(id = R.drawable.iwedia_logo_white_02), // Proveri da li postoji ovaj resurs
            contentDescription = "TV Guide Logo",
            modifier = Modifier.height(37.5.dp).width(45.91.dp)
        )
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
            delay(1000) // Za HH:mm dovoljno je i 60000ms (1 minut)
            currentTime = getCurrentFormattedTime()
        }
    }
    Text(text = currentTime, style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 24.sp, color = Color.White))
}

private fun getCurrentFormattedTime(): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Calendar.getInstance().time)


@Composable
fun EpgChannelRow(
    channel: AppChannel,
    programsForThisChannel: List<AppProgram>,
    dpPerMinute: Dp,
    rowHeight: Dp,
    externalHorizontalScrollState: ScrollState
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(rowHeight)
            .background(Color.Transparent),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ChannelItem(
            channel = channel,
            modifier = Modifier
                .width(EPG_CHANNEL_ITEM_WIDTH)
                .fillMaxHeight()
        )
        ProgramsNonLazyRow(
            programs = programsForThisChannel,
            dpPerMinute = dpPerMinute,
            rowHeight = rowHeight,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(start = SPACE_BETWEEN_CHANNEL_AND_PROGRAMS),
            scrollState = externalHorizontalScrollState
        )
    }
}

@Composable
fun ChannelItem(channel: AppChannel, modifier: Modifier = Modifier) {

    var isFocused by remember { mutableStateOf(false) }
    //val scale by animateFloatAsState(if (isFocused) 1f else 1f, tween(100), label = "ChannelItemScaleFocus")
    val containerColor by animateColorAsState(

        if (isFocused) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f) else Color.Transparent,
        tween(100), label = "ChannelItemContainerColorFocus"
    )
    val borderColor by animateColorAsState(
        if (isFocused) Color.White else Color.Transparent, tween(100), label = "ChannelItemBorderColorFocus"
    )

    Card(
        modifier = modifier
            //.graphicsLayer { scaleX = scale; scaleY = scale }
            .onFocusChanged { isFocused = it.isFocused }
            .focusable(true)
            .border(BorderStroke(if (isFocused) 0.05.dp else 0.dp, borderColor), RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)

    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 1.7.dp, vertical = 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = channel.name,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 10.sp,
                color = if (isFocused) Color.White else Color.LightGray,
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.StartEllipsis
            )
            Spacer(modifier = Modifier.width(1.2.dp))
            AsyncImage(
                model = channel.logo,
                contentDescription = channel.name,
                modifier = Modifier
                    .height(54.dp)
                    .width(88.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.FillBounds
            )
        }
    }
}


@Composable
fun ProgramsNonLazyRow(
    programs: List<AppProgram>,
    dpPerMinute: Dp,
    rowHeight: Dp,
    modifier: Modifier = Modifier,
    scrollState: ScrollState
) {
    Box(
        modifier = modifier.horizontalScroll(scrollState)
    ) {
        Row(
            modifier = Modifier.fillMaxHeight().padding(end = 200.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            programs.forEach { program ->
                ProgramCard(
                    program = program,
                    dpPerMinute = dpPerMinute,
                    height = rowHeight
                )
            }
        }
    }
}

@Composable
fun ProgramCard(program: AppProgram, dpPerMinute: Dp, height: Dp) {

    val programWidth = ((program.durationSec / 60f) * dpPerMinute.value).dp
    var isFocused by remember { mutableStateOf(false) }
    /*val scale by animateFloatAsState(
        if (isFocused) 1f else 1f,
        tween(100),
        label = "ProgramCardScaleFocus"
    )*/
    val baseFocusedColor = Color(0xFFB9B7B7)
    val baseUnfocusedColor = Color(0xFF1E2329)

    val containerColor by animateColorAsState(
        targetValue = if (isFocused) {
            baseFocusedColor.copy(alpha = 0.65f)
        }
        else {
            baseUnfocusedColor.copy(alpha = 0.65f)
        },
        animationSpec = tween(100), label = "ProgramCardContainerColorFocus"
    )

    Card(
        modifier = Modifier
            //.graphicsLayer { scaleX = scale; scaleY = scale }
            .width(programWidth)
            .height(height - 4.dp)
            .onFocusChanged { isFocused = it.isFocused }
            .focusable(true),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)

    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(horizontal = 6.dp, vertical = 4.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = program.title,
                style = MaterialTheme.typography.bodySmall,
                color = if (isFocused) Color.Black else Color.LightGray,
                fontSize = 11.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

*/




        // manuelni skrol//

private val EPG_SIDE_PADDING = 35.dp
private val EPG_CHANNEL_ITEM_WIDTH = 177.dp
private val EPG_PROGRAM_ROW_HEIGHT = 60.dp
private val DP_PER_MINUTE = 6.dp
private val SPACE_BETWEEN_CHANNEL_AND_PROGRAMS = 4.dp

val gradientStartColor = Color(0xFF1A1C1E)


@Composable
fun EPGScreen(viewModel: EPGViewModel) {

    val channelState by viewModel.channelState.collectAsState()
    val programState by viewModel.programState.collectAsState()
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A1C1E)),

            ) {
            val isLoadingChannels = channelState is Resource.Loading
            val isLoadingPrograms = (channelState is Resource.Success && programState is Resource.Loading)

            if (isLoadingChannels || isLoadingPrograms) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val composition by rememberLottieComposition(
                        LottieCompositionSpec.RawRes(R.raw.unknown)
                    )
                    LottieAnimation(
                        composition = composition,
                        iterations = LottieConstants.IterateForever
                        //modifier = Modifier.size(380.dp)
                    )

                }
            } else if (channelState is Resource.Success) {
                val channels = (channelState as Resource.Success<List<AppChannel>>).data
                if (channels.isNullOrEmpty()) { // Tvoja provera
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Nema dostupnih kanala.", color = Color.White)
                    }
                } else {
                    val programs = if (programState is Resource.Success) {
                        (programState as Resource.Success<List<AppProgram>>).data ?: emptyList() // Tvoja logika
                    } else {
                        emptyList()
                    }

                    EpgContent(channels = channels, programs = programs, viewModel = viewModel)
                }
            } else if (channelState is Resource.Error) {
                val errorState = channelState as Resource.Error
                ErrorStateDisplay(message = "Greška pri učitavanju kanala: ${errorState.message}") {
                    viewModel.fetchChannelsAndInitialPrograms()
                }
            } else if (programState is Resource.Error && channelState !is Resource.Loading) {
                val errorState = programState as Resource.Error
                ErrorStateDisplay(message = "Greška pri učitavanju programa: ${errorState.message}") {
                    if (channelState is Resource.Success) {
                        val currentChannels = (channelState as Resource.Success<List<AppChannel>>).data
                        if (currentChannels?.isNotEmpty() == true) {
                            viewModel.loadProgramsForChannels(currentChannels)
                        } else {
                            viewModel.fetchChannelsAndInitialPrograms()
                        }
                    } else {
                        viewModel.fetchChannelsAndInitialPrograms()
                    }
                }
            }
        }
    }
}


@Composable
fun ErrorStateDisplay(message: String?, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(message ?: "Nepoznata greška.", color = Color.Red, textAlign = TextAlign.Center)
        Button(onClick = onRetry, modifier = Modifier.padding(top = 16.dp)) {
            Text("Pokušaj ponovo")
        }
    }
}



@Composable
fun EpgContent(
    channels: List<AppChannel>,
    programs: List<AppProgram>,
    viewModel: EPGViewModel
) {
    val programsByChannelId = remember(programs, channels) {
        programs.groupBy { it.channelId }
            .mapValues { entry ->
                entry.value.sortedBy { it.startTimeEpoch }
            }
    }
    val sharedHorizontalScrollState = rememberScrollState()
    var focusedChannelLogoUrl by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {

        val imageOverallAlpha = 0.42f
        val imageFadeEdgeLength = 50.dp
        val imageFadeToColor = gradientStartColor
        val imageBoxHeight = 243.dp
        val imageBoxWidth = remember(imageBoxHeight) {
            (imageBoxHeight.value * 16 / 9).dp
        }


        focusedChannelLogoUrl?.let { logoUrl ->
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 0.dp, end = 0.dp)
                    .width(imageBoxWidth)
                    .height(imageBoxHeight)
            ) {
                AsyncImage(
                    model = logoUrl,
                    contentDescription = "Pozadinska slika fokusiranog kanala",
                    modifier = Modifier
                        .matchParentSize()
                        .alpha(imageOverallAlpha),
                    contentScale = ContentScale.FillBounds
                )
                // soft edges
                Box(
                    Modifier.align(Alignment.CenterStart).width(imageFadeEdgeLength).fillMaxHeight()
                        .background(brush = Brush.horizontalGradient(listOf(imageFadeToColor, Color.Transparent)))
                )
                /*Box(
                    Modifier.align(Alignment.TopCenter).height(imageFadeEdgeLength).fillMaxWidth()
                        .background(brush = Brush.verticalGradient(listOf(imageFadeToColor, Color.Transparent)))
                )*/
                Box(
                    Modifier.align(Alignment.BottomCenter).height(imageFadeEdgeLength).fillMaxWidth()
                        .background(brush = Brush.verticalGradient(listOf(Color.Transparent, imageFadeToColor)))
                )
                /*Box(
                    Modifier.align(Alignment.CenterEnd).width(imageFadeEdgeLength).fillMaxHeight()
                        .background(brush = Brush.horizontalGradient(listOf(Color.Transparent, imageFadeToColor)))
                )*/
            }
        }


        Column(modifier = Modifier.fillMaxSize()) {
            TopHeader()
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                TvLazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 24.dp)
                        .padding(start = EPG_SIDE_PADDING),
                    state = rememberTvLazyListState(),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    itemsIndexed(channels, key = { _, channel -> channel.channelId }) { _, channel ->
                        EpgChannelRow(
                            channel = channel,
                            programsForThisChannel = programsByChannelId[channel.channelId] ?: emptyList(),
                            dpPerMinute = DP_PER_MINUTE,
                            rowHeight = EPG_PROGRAM_ROW_HEIGHT,
                            externalHorizontalScrollState = sharedHorizontalScrollState,

                            onChannelFocusStateChanged = { isFocused, logo ->
                                if (isFocused) {
                                    focusedChannelLogoUrl = logo
                                } else {
                                    if (focusedChannelLogoUrl == logo) {
                                        focusedChannelLogoUrl = null
                                    }

                                }
                            }
                        )
                    }
                }
            }
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
        Image(
            painter = painterResource(id = R.drawable.iwedia_logo_white_02),
            contentDescription = "TV Guide Logo",
            modifier = Modifier.height(37.5.dp).width(45.91.dp)
        )
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

private fun getCurrentFormattedTime(): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())


@Composable
fun EpgChannelRow(
    channel: AppChannel,
    programsForThisChannel: List<AppProgram>,
    dpPerMinute: Dp,
    rowHeight: Dp,
    externalHorizontalScrollState: ScrollState,
    onChannelFocusStateChanged: (isFocused: Boolean, logoUrl: String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(rowHeight)
            .background(Color.Transparent),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ChannelItem(
            channel = channel,
            modifier = Modifier
                .width(EPG_CHANNEL_ITEM_WIDTH)
                .fillMaxHeight(),
            onFocusChangedCallback = { isFocused ->
                onChannelFocusStateChanged(isFocused, channel.logo)
            }
        )
        ProgramsNonLazyRow(
            programs = programsForThisChannel,
            dpPerMinute = dpPerMinute,
            rowHeight = rowHeight,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(start = SPACE_BETWEEN_CHANNEL_AND_PROGRAMS),
            scrollState = externalHorizontalScrollState
        )
    }
}

@Composable
fun ChannelItem(
    channel: AppChannel,
    modifier: Modifier = Modifier,
    onFocusChangedCallback: (isFocused: Boolean) -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    // val scale by animateFloatAsState...
    val containerColor by animateColorAsState(
        if (isFocused) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f) else Color.Transparent,
        tween(100), label = "ChannelItemContainerColorFocus"
    )
    val borderColor by animateColorAsState(
        if (isFocused) Color.White else Color.Transparent, tween(100), label = "ChannelItemBorderColorFocus"
    )

    Card(
        modifier = modifier
            // .graphicsLayer { scaleX = scale; scaleY = scale }
            .onFocusChanged { focusState ->
                val currentlyFocused = focusState.isFocused
                isFocused = currentlyFocused
                onFocusChangedCallback(currentlyFocused)
            }
            .focusable(true),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(if (isFocused) 0.05.dp else 0.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 1.7.dp, vertical = 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = channel.name,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 10.sp,
                color = if (isFocused) Color.White else Color.LightGray,
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.StartEllipsis
            )
            Spacer(modifier = Modifier.width(1.2.dp))
            AsyncImage(
                model = channel.logo,
                contentDescription = channel.name,
                modifier = Modifier
                    .height(54.dp)
                    .width(88.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.FillBounds
            )
        }
    }
}


@Composable
fun ProgramsNonLazyRow(
    programs: List<AppProgram>,
    dpPerMinute: Dp,
    rowHeight: Dp,
    modifier: Modifier = Modifier,
    scrollState: ScrollState
) {
    Box(
        modifier = modifier.horizontalScroll(scrollState)
    ) {
        Row(
            modifier = Modifier.fillMaxHeight().padding(end = 200.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            programs.forEach { program ->
                ProgramCard(
                    program = program,
                    dpPerMinute = dpPerMinute,
                    height = rowHeight
                )
            }
        }
    }
}

@Composable
fun ProgramCard(program: AppProgram, dpPerMinute: Dp, height: Dp) {
    val programWidth = ((program.durationSec / 60f) * dpPerMinute.value).dp
    var isFocused by remember { mutableStateOf(false) }
    // val scale by animateFloatAsState...
    val baseFocusedColor = Color(0xFFB9B7B7)
    val baseUnfocusedColor = Color(0xFF1E2329)
    val cardAlpha = 0.53f

    val containerColor by animateColorAsState(
        targetValue = if (isFocused) {
            baseFocusedColor.copy(alpha = 1f)
        } else {
            baseUnfocusedColor.copy(alpha = cardAlpha)
        },
        animationSpec = tween(100), label = "ProgramCardContainerColorFocus"
    )

    Card(
        modifier = Modifier
            // .graphicsLayer { scaleX = scale; scaleY = scale }
            .width(programWidth)
            .height(height - 4.dp)
            .onFocusChanged { isFocused = it.isFocused }
            .focusable(true),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(horizontal = 6.dp, vertical = 4.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = program.title,
                style = MaterialTheme.typography.bodySmall,
                color = if (isFocused) Color.Black else Color.LightGray,
                fontSize = 10.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}