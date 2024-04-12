package com.example.composeepg.screens

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import coil.compose.AsyncImage
import com.example.composeepg.R
import com.example.composeepg.data.ChannelRowItems
import com.example.composeepg.data.MockData
import com.example.composeepg.screens.components.ChannelItemsContentV2
import com.example.composeepg.screens.components.EpgFilterChip
import com.example.composeepg.screens.components.HoursItemsContent
import com.example.composeepg.screens.components.ProgramItemsContent
import com.example.composeepg.screens.components.dialogs.FilterDialog
import com.example.composeepg.view.HomeScreenUiState
import com.example.composeepg.view.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun EpgLayoutContentV1(mainViewModel: MainViewModel = viewModel()) {
    val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()

    when (val s = uiState) {
        is HomeScreenUiState.Ready -> {
            MaterialTheme {
                CreateViewV1(s.channelList, mainViewModel,backgroundColor = MaterialTheme.colorScheme.onSurface)
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
fun CreateViewV1(
    channelsList: MutableList<ChannelRowItems>,
    mainViewModel: MainViewModel,
    backgroundColor: Color = MaterialTheme.colorScheme.onSurface
) {
    var channelList by remember { mutableStateOf(channelsList) }
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
    val scope = rememberCoroutineScope()
    var userHasInteracted by remember { mutableStateOf(false) }
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
    val program = MockData().getProgramOnNow(currentTimeNowString)
    val adjustScrollState = remember { mutableStateOf(true) }
    val onNowProgram = (program?.programID)?.plus(1) //Calculate that items start at 0 add plus 1 to get the correct ID
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
    val lastFocusedItemId = remember { mutableStateOf<Int?>(null) }
    /**
     * Opens Favorite Dialog
     */
    var isOpen by remember { mutableStateOf(false) }
    var filterWasActive by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(300)
        focusRequester.requestFocus()
    }

    /**
     * Opens Filter Dialog
     * Receives Item selected
     * Handle Selection -> Filter programs and channels
     */
    if (isOpen) {
        FilterDialog(onCardClicked = { received ->
            when(received){
                "Favorites"->{
                    scope.launch {
                        val filtered = withContext(Dispatchers.Default) {
                            channelList.filter { it.isFavorite }.toMutableList()
                        }
                        withContext(Dispatchers.Main) {
                            channelList = filtered
                            delay(300)
                            focusRequester.requestFocus()

                        }
                    }
                }
                "HD"->{
                    scope.launch {
                        val filtered = withContext(Dispatchers.Default) {
                            channelList.filter { it.quality == "HD"}.toMutableList()
                        }
                        withContext(Dispatchers.Main) {
                            channelList = filtered
                            delay(300)
                            focusRequester.requestFocus()
                        }
                    }
                }
                "4K"->{
                    scope.launch {
                        val filtered = withContext(Dispatchers.Default) {
                            channelList.filter { it.quality == "4K"}.toMutableList()
                        }
                        withContext(Dispatchers.Main) {
                            channelList = filtered
                            delay(300)
                            focusRequester.requestFocus()
                        }
                    }
                }
                "Music"->{
                    scope.launch {
                        val filtered = withContext(Dispatchers.Default) {
                            channelList.filter { it.genre == "Music"}.toMutableList()
                        }
                        withContext(Dispatchers.Main) {
                            channelList = filtered
                            delay(300)
                            focusRequester.requestFocus()
                        }
                    }
                }
                "Reset"-> {
                    scope.launch {
                        val originalList = MockData().createChannels()
                        withContext(Dispatchers.Main) {
                            channelList.clear()
                            channelList = originalList
                            delay(300)
                            focusRequester.requestFocus()
                        }
                    }
                }
            }
            isOpen = false
        }, onDismiss = {
            isOpen = false
        })
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(backgroundColor, shape = shape)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)

        ) {
            Text(
                text = clock,
                modifier = Modifier.padding(0.dp, 10.dp, 30.dp, 0.dp),
                color = Color.White, fontSize = 30.sp
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(start = 10.dp, top = 45.dp, end = 50.dp)
            .background(backgroundColor, shape = shape)
    ) {
        Column {
            if (focusedProgram != "-1") {
                val programSelected = MockData().getProgramData(focusedIndexP, focusedIndexCh)
                AsyncImage(
                    model = programSelected.programImage,
                    contentDescription = "image",
                    modifier = Modifier
                        .width(250.dp)
                        .padding(start = 20.dp)
                        .height(200.dp)
                        .padding(start = 20.dp, top = 10.dp, bottom = 10.dp, end = 10.dp)
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
                        .width(250.dp)
                        .padding(start = 20.dp, top = 10.dp, bottom = 10.dp, end = 10.dp)
                        .height(200.dp)
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
                .align(Alignment.TopStart)
                .padding(start = 230.dp)

        ) {
            if (focusedProgram != "-1") {
                val programSelected = MockData().getProgramData(focusedIndexP, focusedIndexCh)
                Timber.tag("TAG").d("selected = $programSelected")
                Text(
                    text = programSelected.programName,
                    modifier = Modifier.padding(20.dp, 10.dp, 0.dp, 0.dp),
                    color = Color.White
                )
                Text(
                    text = "TV-G,G,PG-13",
                    modifier = Modifier.padding(20.dp, 20.dp, 0.dp, 0.dp),
                    color = Color.White
                )
                Text(
                    text = "Program Description Goes Here",
                    modifier = Modifier.padding(20.dp, 30.dp, 20.dp, 0.dp),
                    color = Color.White
                )
            }
        }
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
        ) {
            if (focusedProgram != "-1") {
                val channelData = MockData().getChannelData(focusedIndexCh)
                val programSelected = MockData().getProgramData(focusedIndexP, focusedIndexCh)
                Text(
                    text = programSelected.programStart.plus(" - ")
                        .plus(programSelected.programEnd),
                    modifier = Modifier.padding(0.dp, 10.dp, 200.dp, 0.dp),
                    color = Color.White
                )
                Text(
                    text = channelData.channelID.toString().plus(" - ")
                        .plus(channelData.channelName),
                    modifier = Modifier.padding(0.dp, 20.dp, 200.dp, 0.dp),
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
    /**
     * Entire EPG
     */
    Box(
        modifier = Modifier
            .wrapContentSize()
            .padding(top = 200.dp)
    ) {
        EpgFilterChip(
            label = "Filter",
            isChecked = false,
            onCheckedChange = {
                isOpen = true
            }, modifier = Modifier.onFocusChanged {
                if(it.hasFocus){
                    filterWasActive = true
                }
                if(!it.hasFocus && filterWasActive) {
                    scope.launch {
                        withContext(Dispatchers.Main) {
                            focusRequester.requestFocus()
                        }
                    }
                }
            })
        Row {
            LazyColumn(
                modifier = Modifier
                    .width(firstColumnWidth)
                    .padding(start = 10.dp, top = 40.dp)
                    .background(backgroundColor),
                state = sharedLazyListState,
            ) {
                /**
                 * Channels
                 */
                itemsIndexed(items = channelList, key = { _, itemB -> itemB.channelID })
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
                Column(modifier = Modifier.fillMaxWidth().background(backgroundColor))
                {
                    // Hours Row
                    Row(
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .fillMaxWidth()

                    ) {
                        reducedHours.forEachIndexed { index, hour ->
                            HoursItemsContent(hour = hour, index = index, mainViewModel, hoursIndex)
                        }

                    }
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth().background(backgroundColor)
                            .onGloballyPositioned { }
                            .pointerInput(Unit) {
                                detectTapGestures(onPress = { userHasInteracted = true })
                            },
                        contentPadding = PaddingValues(start = 10.dp, bottom = 20.dp, top = 5.dp),
                        state = sharedLazyListState
                    ) {
                        itemsIndexed(items = channelList) { index, item ->
                            if (index == channelList.size) {
                                listCompleted = true
                            }
                            val programs = MockData().getAllProgramsForChannel(item.channelID)
                            var indexOfPrg=0
                            Row(modifier = Modifier.fillMaxWidth().background(backgroundColor)) {
                                programs.forEachIndexed { i, program ->

                                    if(program.programID == onNowProgram){
                                        indexOfPrg = i+1
                                    }
                                    /**
                                     * Used in Mock 1.00 replace . with :
                                     */
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
                                    ProgramItemsContent(
                                        program,
                                        cellHeight,
                                        onFocusChange = { isFocused ->
                                            if (isFocused) {
                                                focusedIndex = index + 1
                                                focusedIndexP = program.programID
                                                focusedIndexCh = program.channelId
                                                hasFocusP = true
                                                focusedProgram = program.programID.toString()
                                                lastFocusedItemId.value = program.programID
                                            }
                                        },
                                       focusRequester = if (index == indexOfPrg) focusRequester else FocusRequester(),
                                        widthDp,
                                        i
                                    )
                                    LaunchedEffect(key1 = program.programID, key2 = lastFocusedItemId.value) {
                                        if (programs[index].programID == lastFocusedItemId.value && !userHasInteracted) {
                                            focusRequester.requestFocus()
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}
@Composable
fun VerticalTimeLine(xPosition: Dp) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(2.dp) // Adjust the line's thickness
            .background(Color.Blue) // Adjust the line's color
            .offset(x = xPosition)
    ) {
        // No content needed inside the box, it's just the line
    }
}
@Composable
@Preview(device = Devices.TV_1080p)
fun EpgLayoutContentPreviewV1() {
    EpgLayoutContentV1()
}