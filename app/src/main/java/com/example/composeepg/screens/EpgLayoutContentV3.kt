package com.example.composeepg.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.example.composeepg.R
import com.example.composeepg.data.ChannelRowItems
import com.example.composeepg.data.MockData
import com.example.composeepg.screens.components.ChannelItemsContent
import com.example.composeepg.screens.components.ChannelItemsContentV2
import com.example.composeepg.screens.components.HoursItemsContent
import com.example.composeepg.screens.components.ProgramItemsContent
import com.example.composeepg.screens.components.dialogs.CardDialog
import com.example.composeepg.view.HomeScreenUiState
import com.example.composeepg.view.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun EpgLayoutContentV3 (mainViewModel: MainViewModel = viewModel()) {
    val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()

    when (val s = uiState) {
        is HomeScreenUiState.Ready -> {
            CreateViewV3(s.channelList, mainViewModel)
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
        Text(text = "Whoops, something went wrong.", modifier = modifier)
    }
}

@Composable
fun CreateViewV3(
    channelsList: MutableList<ChannelRowItems>,
    mainViewModel: MainViewModel,
) {

    val horizontalScrollState = rememberScrollState()
    val focusRequester = remember { FocusRequester() }
    val firstColumnWidth = 120.dp
    val cellHeight = 60.dp
    var focusedIndex by remember { mutableStateOf(-1) }
    var focusedIndexP by remember { mutableStateOf(-1) }
    var focusedIndexCh by remember { mutableStateOf(-1) }
    var hasFocusP by remember { mutableStateOf(false) }
    var listCompleted by remember { mutableStateOf(false) }
    var focusedProgram by remember { mutableStateOf("-1") }
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val context = LocalContext.current
    val sharedLazyListState = rememberLazyListState()

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
    val currentTimeString = roundedTime.format(formatter) // Format rounded time as string

    val indexReduced = reducedHours.indexOf(currentTimeString)

    var hoursIndex = 0
    if (indexReduced != -1) {
        println("Current time is at index: $indexReduced time ${reducedHours[indexReduced]}")
        hoursIndex = indexReduced
    }
    val adjustScrollState = remember { mutableStateOf(true) }
    /**
     * Channels rows
     * This logic initiates a scroll adjustment when the snapshot flow detects that the last visible item index is within a certain range of the total item count,
     * suggesting that the user is near or at the end of the list but the last item might not be fully visible.
     * What i can find to fix the issue that when you scroll down to last program the channel row fallows.
     */

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
    val shape = RoundedCornerShape(8.dp)
    val bgwColor = MaterialTheme.colorScheme.background

    /**
     * Opens Favorite Dialog
     */
    var isOpen by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        focusRequester.requestFocus()
    }


    if (isOpen) {
        CardDialog(onDismiss = { isOpen = false })
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(Color.Transparent, shape = shape)

    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)

        ) {
            androidx.compose.material3.Text(
                text = currentTime.hour.toString().plus(":").plus(currentTime.minute.toString()),
                modifier = Modifier.padding(10.dp, 10.dp, 30.dp, 0.dp),
                color = Color.White,fontSize = 30.sp
            )
        }
    }

    /**
     * Entire EPG
     */
    Box(
        modifier = Modifier
            .wrapContentSize()
            .padding(top = 160.dp, bottom = 120.dp)

    ) {
        Row {
            LazyColumn(
                modifier = Modifier
                    .width(firstColumnWidth)
                    .padding(start = 10.dp, top = 30.dp),
                state = sharedLazyListState,
            ) {
                /**
                 * Channels
                 */
                itemsIndexed(items = channelsList, key = { _, itemB -> itemB.channelID })

                { index, itemC ->
                    ChannelItemsContentV2(item = itemC, index = index)
                }
            }

            /**
             * Programs
             */
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(state = horizontalScrollState)
            ) {
                Column(modifier = Modifier.fillMaxWidth())
                {
                    // Hours Row
                    Row(
                        modifier = Modifier
                            .padding(bottom = 5.dp)
                            .fillMaxWidth()

                    ) {
                        reducedHours.forEachIndexed { index, hour ->
                            HoursItemsContent(hour = hour, index = index, mainViewModel, hoursIndex)
                        }
                    }
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().background(Color.DarkGray),
                        contentPadding = PaddingValues(start = 10.dp),

                        state = sharedLazyListState
                    ) {
                        itemsIndexed(items = channelsList) { index, item ->
                            if (index == channelsList.size) {
                                listCompleted = true
                            }
                            val programs = MockData().getAllProgramsForChannel(item.channelID)
                            Row(modifier = Modifier.fillMaxWidth()) {
                                programs.forEachIndexed { i, program ->
                                    val convertedStartHours = program.programStart.replace(".", ":")
                                    val convertedEndHours = program.programEnd.replace(".", ":")
                                    /**
                                     * Check in HashMap for location
                                     */
                                    val positionX =
                                        mainViewModel.startTimePositions[convertedStartHours]
                                            ?: 0f // Default to 0 if not found
                                    val positionXEnd =
                                        mainViewModel.startTimePositions[convertedEndHours]
                                            ?: 0f // Default to 0 if not found
                                    val durationInHours = positionXEnd - positionX
                                    val widthDp =
                                        with(LocalDensity.current) { durationInHours.toDp() }

                                    ProgramItemsContent(program, cellHeight, onFocusChange = { isFocused ->
                                        if (isFocused) {
                                            focusedIndex = index + 1
                                            focusedIndexP = program.programID
                                            focusedIndexCh = program.channelId
                                            hasFocusP = true
                                            focusedProgram = program.programID.toString()
                                        }
                                    }, focusRequester, widthDp,i)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    //Bottom
    Box(modifier = Modifier.fillMaxSize()
        .padding(start = 10.dp, end = 50.dp).height(120.dp)
        .background(Color.Transparent, shape = shape)
    ) {
        // add your column here (with align modifier)
        Column(modifier = Modifier.align(Alignment.BottomStart)) {
            if (focusedProgram != "-1") {
                val programSelected = MockData().getProgramData(focusedIndexP, focusedIndexCh)
                AsyncImage(
                    model = programSelected.programImage,
                    contentDescription = "image",
                    modifier = Modifier
                        .width(200.dp)
                        .height(100.dp)
                        .padding(start = 10.dp, top = 10.dp, bottom = 10.dp, end = 10.dp)
                        .clip(RoundedCornerShape(topEnd = 8.dp, topStart = 8.dp))
                        .drawWithContent {
                            drawContent()
                            drawRect(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        bgwColor.copy(alpha = 1.0f)
                                    )
                                )
                            )
                        },
                    contentScale = ContentScale.Inside
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.default_card),
                    contentDescription = "image:",
                    modifier = Modifier
                        .align(Alignment.Start)
                        .width(200.dp)
                        .padding(start = 10.dp, top = 10.dp, bottom = 10.dp, end = 10.dp)
                        .height(100.dp)
                        .clip(RoundedCornerShape(topEnd = 8.dp, topStart = 8.dp))
                        .drawWithCache {
                            onDrawWithContent {
                                drawContent()
                                drawRect(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            bgwColor.copy(alpha = 1.0f)
                                        )
                                    )
                                )
                            }
                        },
                    contentScale = ContentScale.Crop,
                )
            }
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 230.dp)
        ) {
            if (focusedProgram != "-1") {
                val programSelected = MockData().getProgramData(focusedIndexP, focusedIndexCh)
                Timber.tag("TAG").d("selected = $programSelected")
                androidx.compose.material3.Text(
                    text = programSelected.programName,
                    modifier = Modifier.padding(20.dp, 5.dp, 0.dp, 0.dp),
                    color = Color.White
                )
                androidx.compose.material3.Text(
                    text = "TV-G,G,PG-13",
                    modifier = Modifier.padding(20.dp, 10.dp, 0.dp, 0.dp),
                    color = Color.White
                )
                androidx.compose.material3.Text(
                    text = "Program Description Goes Here",
                    modifier = Modifier.padding(20.dp, 15.dp, 0.dp, 10.dp),
                    color = Color.White
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 200.dp)
        ) {
            Divider(
                color = Color.Black,
                thickness = 1.dp,
                modifier = Modifier
                    .height(100.dp)
                    .width(2.dp)
                    .background(Color.White)
                    .align(Alignment.CenterHorizontally),

                )

        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)

        ) {
            if (focusedProgram != "-1") {
                val channelData = MockData().getChannelData(focusedIndexCh)
                val programSelected = MockData().getProgramData(focusedIndexP, focusedIndexCh)
                androidx.compose.material3.Text(
                    text = programSelected.programStart.plus(" - ")
                        .plus(programSelected.programEnd),
                    modifier = Modifier.padding(0.dp, 5.dp, 100.dp, 0.dp),
                    color = Color.White
                )
                androidx.compose.material3.Text(
                    text = channelData.channelID.toString().plus(" - ")
                        .plus(channelData.channelName),
                    modifier = Modifier.padding(0.dp, 10.dp, 100.dp, 0.dp),
                    color = Color.White
                )
                Image(
                    painterResource(R.drawable.baseline_hd_24),
                    contentDescription = "",
                    modifier = Modifier
                        .width(48.dp)
                        .height(48.dp)
                )

            }
        }

    }


 }



@Composable
@Preview(device = Devices.TV_1080p)
fun EpgLayoutContentPreviewV3() {
    EpgLayoutContentV3()
}