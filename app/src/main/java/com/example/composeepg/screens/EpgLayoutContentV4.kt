package com.example.composeepg.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import coil.compose.AsyncImage
import com.example.composeepg.R
import com.example.composeepg.data.ChannelRowItems
import com.example.composeepg.data.ProgramRowItems
import com.example.composeepg.screens.components.dialogs.ProgramDetailsPopup
import com.example.composeepg.view.HomeScreenUiState
import com.example.composeepg.view.MainViewModel
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import androidx.compose.runtime.remember as remember1

/**
 * Testing ways to reduce the complex nesting
 * having issues with time bar and navigation in the epg
 */

@Composable
fun EpgLayoutContentV4(mainViewModel: MainViewModel = viewModel()) {
    val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()

    when (val s = uiState) {
        is HomeScreenUiState.Ready -> {

            MaterialTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Background image live Tv
                    Image(
                        painter = painterResource(id = R.drawable.movie_scenes_that_make_you_emotional),
                        contentDescription = "Background Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(0.5f), // Adjust alpha here, 0.5f for 50% transparency,
                        contentScale = ContentScale.Fit,

                        )
                    // EPGView( mainViewModel, s.channelList)
                    CreateViewV5(
                        s.channelList,
                        mainViewModel,
                        backgroundColor = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        is HomeScreenUiState.Loading -> IndeterminateCircularProgressBarDemo()
        is HomeScreenUiState.Error -> Error()
    }
}


@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun Error(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        androidx.tv.material3.Text(text = "Whoops, something went wrong.", modifier = modifier)
    }
}

@Composable
fun EPGView(mainViewModel: MainViewModel, channelList: MutableList<ChannelRowItems>) {
    val lazyListState = rememberLazyListState()
    val focusRequester = remember1 { FocusRequester() }
    val sharedHorizontalScrollState = rememberLazyListState()
    //End
    val reducedHours = mutableListOf<String>()
    val startedAt = "01:00"
    val startHour = startedAt.substringBefore(":").toInt()

    // Create list of hours according to requested start time
    for (hour in startHour until startHour + 24) {
        val currentHour = hour % 24
        reducedHours.add("$currentHour:00")
        reducedHours.add("$currentHour:30")
    }

    val currentTime = LocalTime.now()
    val roundedTime = LocalTime.of(
        currentTime.hour,
        if (currentTime.minute < 30) 0 else 30
    ) // Round time to nearest half hour

    val formatter = DateTimeFormatter.ofPattern("H:mm") // Format as "hour:minute"
    val formatterNow = DateTimeFormatter.ofPattern("H") // Format as "hour"
    val currentTimeString = roundedTime.format(formatter) // Format rounded time as string

    val indexReduced = reducedHours.indexOf(currentTimeString)
    val hoursScrollState = rememberLazyListState()
    var hoursIndex = 0
    if (indexReduced != -1) {
        hoursIndex = indexReduced
    }

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 175.dp, start = 55.dp, bottom = 5.dp)) {
        HoursRow(reducedHours, sharedHorizontalScrollState, hoursIndex, mainViewModel)
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 200.dp, start = 15.dp, bottom = 5.dp),
        state = lazyListState
    ) {
        itemsIndexed(channelList) { index, channel ->
            val programs = mainViewModel.getProgramsForChannel(channel.channelID)
            Row(verticalAlignment = Alignment.CenterVertically) {
                ChannelItem(channel)
                ProgramsRow(
                    programs,
                    mainViewModel,
                    focusRequester,
                    lazyListState,
                    hoursScrollState
                )
            }
        }
    }
}

@Composable
fun ProgramsRow(
    programs: List<ProgramRowItems>,
    mainViewModel: MainViewModel,
    focusRequester: FocusRequester,
    lazyListState: LazyListState,
    hoursScrollState: LazyListState
) {
    LazyRow(modifier = Modifier.padding(start = 15.dp), state = lazyListState) {
        itemsIndexed(programs) { index, program ->
            val convertedStartHours = program.programStart.replace(".", ":")
            val convertedEndHours = program.programEnd.replace(".", ":")
            val positionX = mainViewModel.startTimePositions[convertedStartHours] ?: 0f
            val positionXEnd = mainViewModel.startTimePositions[convertedEndHours] ?: 0f
            val durationInHours = positionXEnd - positionX
            val widthDp = with(LocalDensity.current) { durationInHours.toDp() }
            ProgramItems(program, index, focusRequester, 100.dp, hoursScrollState, mainViewModel)
        }
    }
}

@Composable
fun CreateViewV5(
    channelsList: MutableList<ChannelRowItems>,
    mainViewModel: MainViewModel,
    backgroundColor: Color = MaterialTheme.colorScheme.onSurface
) {
    var channelList by remember1 { mutableStateOf(channelsList) }
    val horizontalScrollState = rememberScrollState()
    val focusRequester = remember1 { FocusRequester() }
    val firstColumnWidth = 140.dp
    val cellHeight = 60.dp
    var focusedIndex by remember1 { mutableStateOf(-1) }
    var focusedIndexP by remember1 { mutableStateOf(-1) }
    var focusedIndexCh by remember1 { mutableStateOf(-1) }
    var hasFocusP by remember1 { mutableStateOf(false) }
    var listCompleted by remember1 { mutableStateOf(false) }
    var focusedProgram by remember1 { mutableStateOf("-1") }
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val context = LocalContext.current
    val sharedLazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var userHasInteracted by remember1 { mutableStateOf(false) }
    var isVisible by remember1 { mutableStateOf(false) }

    // Scroll state only for programs, controlling vertical scroll
    val programScrollState = rememberLazyListState()

    // Disable direct user interaction with the channel list
    val channelScrollState = rememberLazyListState()

    val sharedHorizontalScrollState = rememberLazyListState()
    val hoursScrollState = rememberLazyListState()
    val gradientColors = listOf(
        Color.DarkGray,
        Color.Blue,
        Color.Transparent
    )

    //End
    val reducedHours = mutableListOf<String>()
    val startedAt = "01:00"
    val startHour = startedAt.substringBefore(":").toInt()

    // Create list of hours according to requested start time
    for (hour in startHour until startHour + 24) {
        val currentHour = hour % 24
        reducedHours.add("$currentHour:00")
        reducedHours.add("$currentHour:30")
    }

    val currentTime = LocalTime.now()
    val roundedTime = LocalTime.of(
        currentTime.hour,
        if (currentTime.minute < 30) 0 else 30
    ) // Round time to nearest half hour

    val formatter = DateTimeFormatter.ofPattern("H:mm") // Format as "hour:minute"
    val formatterNow = DateTimeFormatter.ofPattern("H") // Format as "hour"
    val currentTimeString = roundedTime.format(formatter) // Format rounded time as string
    val currentTimeNowString = roundedTime.format(formatterNow) // Format rounded time as string
    val clock = currentTime.format(formatter)

    val indexReduced = reducedHours.indexOf(currentTimeString)

    var hoursIndex = 0
    if (indexReduced != -1) {
        hoursIndex = indexReduced
    }

    val adjustScrollState = remember1 { mutableStateOf(true) }
    val lazyListState = rememberLazyListState()

    LaunchedEffect(sharedLazyListState.isScrollInProgress) {
        if (sharedLazyListState.isScrollInProgress) {
            adjustScrollState.value = true
        }
    }

    LaunchedEffect(key1 = sharedLazyListState) {
        snapshotFlow { sharedLazyListState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->

                val lastVisibleItemIndex = visibleItems.lastOrNull()?.index ?: return@collect
                val totalItemCount = sharedLazyListState.layoutInfo.totalItemsCount

                // Determine if we are close to the end and need to adjust.
                val nearEnd = lastVisibleItemIndex >= totalItemCount - 2

                if (nearEnd && adjustScrollState.value && !sharedLazyListState.isScrollInProgress) {
                    coroutineScope.launch {
                        sharedLazyListState.scrollToItem(index = totalItemCount - 1)
                        adjustScrollState.value = false // Prevent further adjustments until reset.
                    }
                }
            }
    }

//    LaunchedEffect(sharedHorizontalScrollState.isScrollInProgress) {
//        if (sharedHorizontalScrollState.isScrollInProgress) {
//            adjustScrollState.value = true
//        }
//    }

//    LaunchedEffect(key1 = sharedHorizontalScrollState) {
//        snapshotFlow { sharedHorizontalScrollState.layoutInfo.visibleItemsInfo }
//            .collect { visibleItems ->
//
//                val lastVisibleItemIndex = visibleItems.lastOrNull()?.index ?: return@collect
//                val totalItemCount = sharedHorizontalScrollState.layoutInfo.totalItemsCount
//
//                // Determine if we are close to the end and need to adjust.
//                val nearEnd = lastVisibleItemIndex >= totalItemCount - 1
//
//                if (nearEnd && adjustScrollState.value && !sharedHorizontalScrollState.isScrollInProgress) {
//                    coroutineScope.launch {
//                        sharedHorizontalScrollState.scrollToItem(index = totalItemCount - 1)
//                        adjustScrollState.value = false // Prevent further adjustments until reset.
//                    }
//                }
//            }
//    }

    val focusRequesters = remember1 { mutableStateMapOf<Int, FocusRequester>() }

    /**
     * Entire EPG
     */
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 175.dp, start = 55.dp, bottom = 5.dp)) {
        HoursRow(reducedHours, hoursScrollState, hoursIndex, mainViewModel)
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 200.dp, start = 15.dp, bottom = 5.dp),
        state = lazyListState
    ) {
        item {
            channelList.forEachIndexed { _, item ->
                val programs = mainViewModel.getProgramsForChannel(item.channelID)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ChannelItem(item)
                    LazyRow(
                        modifier = Modifier.padding(start = 15.dp),
                        state = sharedHorizontalScrollState
                    ) {
                        items(programs.size) {
                            programs.forEachIndexed { index, prg ->
                                val widthDp =
                                    calculateWidth(prg, mainViewModel, LocalDensity.current)
                                val focusRequesterN =
                                    focusRequesters.getOrPut(index) { FocusRequester() }
                                ProgramItems(
                                    prg,
                                    index,
                                    focusRequesterN,
                                    widthDp,
                                    hoursScrollState,
                                    mainViewModel
                                )

                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HoursRow(
    hours: List<String>,
    scrollState: LazyListState,
    hoursIndex: Int,
    mainViewModel: MainViewModel
) {
    var now = false

    LazyRow(state = scrollState, modifier = Modifier.padding(start = 80.dp)) {
        items(hours.size) { hour ->
            hours.forEachIndexed() { index, h ->
                if (index == hoursIndex) {
                    now = true
                }
                Text(text = h,
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 70.dp)
                        .onGloballyPositioned { layoutCoordinates ->
                            val positionInRoot = layoutCoordinates.positionInRoot()
                            if (now) {
                                mainViewModel.timeNowPosition = positionInRoot.x - 60
                            }
                            mainViewModel.startTimePositions[h] = positionInRoot.x
                        })
            }
        }
    }
}

@Composable
fun ChannelItem(item: ChannelRowItems) {
    Row(
        modifier = Modifier
            .width(120.dp)
            .padding(start = 5.dp, bottom = 8.dp)
            .clip(shape = RoundedCornerShape(15.dp, 15.dp, 15.dp, 15.dp))
            .background(Color.Black)
            .padding(4.dp)
    ) {
        if (item.channelLogo.isNotEmpty()) {
            AsyncImage(
                model = item.channelLogo,
                contentDescription = "",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)

                    .padding(5.dp),
                contentScale = ContentScale.Inside
            )
        } else {
            Text(text = item.channelName)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = item.channelNumber.toString(),
            modifier = Modifier.padding(5.dp, 15.dp, 0.dp, 0.dp),
            color = Color.White
        )
    }
}

@Composable
fun ProgramItems(
    prg: ProgramRowItems,
    index: Int,
    focusRequester: FocusRequester,
    widthDp: Dp,
    hoursScrollState: LazyListState,
    mainViewModel: MainViewModel
) {
    var isFocused by remember1 { mutableStateOf(false) }
    val borderColor = if (isFocused) Color.White else Color.Transparent
    val cellHeight = 60.dp
    var focusedProgram by remember1 { mutableStateOf<ProgramRowItems?>(null) }
    var showPopup by remember1 { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .width(widthDp)
            .padding(bottom = 5.dp, end = 5.dp)
            .height(cellHeight)
            .clip(shape = RoundedCornerShape(15.dp, 15.dp, 15.dp, 15.dp))
            .border(2.dp, borderColor, RoundedCornerShape(15.dp))
            .background(Color.Black)
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
                focusedProgram = prg
                // showPopup = isFocused
            }
            .focusRequester(focusRequester)
            .clickable(onClick = {
                showPopup = false
                focusRequester.requestFocus()
            })
            .focusable(true)

    ) {
        Text(
            text = prg.programName,
            color = Color.White,
            modifier = Modifier.padding(5.dp, 15.dp, 0.dp, 0.dp),
        )
        if (prg.isLookBack) {
            Column(
                modifier = Modifier
                    .padding(top = 30.dp)
                    .align(Alignment.CenterStart)
            ) {
                Image(
                    painterResource(R.drawable.reset),
                    contentDescription = "",
                    modifier = Modifier
                        .width(30.dp)
                        .align(Alignment.Start)
                        .padding(start = 20.dp)
                )
            }
        }
        Column(
            modifier = Modifier
                .padding(end = 0.dp, top = 30.dp)
                .align(Alignment.CenterEnd)
        ) {
            if (prg.isRecording) {
                Image(
                    painterResource(R.drawable.ic_record),
                    contentDescription = "",
                    modifier = Modifier
                        .width(30.dp)
                        .align(Alignment.Start)
                        .padding(end = 20.dp)
                )
            }
        }
        // Shows in the selected view
//        if (showPopup && focusedProgram != null) {
//            ProgramDetailsPopup(program = focusedProgram!!)
//        }
    }
    //Shows in a static location
    if (showPopup && focusedProgram != null) {
        ProgramDetailsPopup(program = focusedProgram!!)
    }

}

@Composable
@Preview(device = Devices.TV_1080p)
fun EpgLayoutContentPreviewV4() {
    EpgLayoutContentV4()
}