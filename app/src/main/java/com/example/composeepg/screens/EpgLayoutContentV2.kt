package com.example.composeepg.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import coil.compose.AsyncImage
import com.example.composeepg.R
import com.example.composeepg.data.ChannelRowItems
import com.example.composeepg.data.EpgData
import com.example.composeepg.data.MockData
import com.example.composeepg.data.ProgramRowItems
import com.example.composeepg.view.HomeScreenUiState
import com.example.composeepg.view.MainViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date

@Composable
fun EpgLayoutContentV2(mainViewModel: MainViewModel = viewModel()) {
    val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()

    /**
     * Create Hours with Half Hour
     */
    val hoursList = mutableListOf<String>()
    for (hour in 0 until 24) {
        hoursList.add("$hour:00")
        hoursList.add("$hour:30")
    }

    when (val s = uiState) {
        is HomeScreenUiState.Ready -> {
            CreateViewV4({ scrollable, scrollToFirst ->
                Log.d("TAG", "onScroll : scrollable = $scrollable, scrollToFirst = $scrollToFirst")
            }, s.channelList, mainViewModel)
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
fun CreateViewV4(
    onVerticalScroll: (Boolean, Boolean) -> Unit,
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

    /**
     * Scroll to location of time Now
     */
    val scrollTo = mainViewModel.timeNowPosition
    val timeWasSetPosition by remember { mutableStateOf(mainViewModel.isPositionSet) }
    if(timeWasSetPosition.value == true){
        Log.d("TAG","Scroll to $scrollTo")
        LaunchedEffect(true) {
            coroutineScope.launch {
               // horizontalScrollState.scrollTo(scrollTo.toInt())
            }
        }
    }

    if (isOpen) {
        CardDialog(onDismiss = { isOpen = false })
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(start = 10.dp, top = 10.dp, bottom = 10.dp)
            .background(Color.Transparent, shape = shape)

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
                .width(400.dp)
                .padding(end = 200.dp)
        ) {
            Divider(
                color = Color.Black,
                thickness = 1.dp,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(2.dp)
                    .background(Color.White)
                    .align(Alignment.CenterHorizontally),

                )

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
            .padding(top = 150.dp)
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
                    ChannelContent(item = itemC, index = index)
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

                            HourItem(hour = hour, index = index, mainViewModel, hoursIndex)
                        }
                    }
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(start = 10.dp, bottom = 8.dp),
                        state = sharedLazyListState
                    ) {
                        itemsIndexed(items = channelsList) { index, item ->
                            if (index == channelsList.size) {
                                listCompleted = true
                            }
                            val programs = MockData().getAllProgramsForChannel(item.channelID)
                            Row(modifier = Modifier.fillMaxWidth()) {
                                programs.forEachIndexed { _, program ->

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

                                    ProgramItem(program, cellHeight, onFocusChange = { isFocused ->
                                        if (isFocused) {
                                            focusedIndex = index + 1
                                            focusedIndexP = program.programID
                                            focusedIndexCh = program.channelId
                                            hasFocusP = true
                                            focusedProgram = program.programID.toString()
                                        }
                                    }, focusRequester, widthDp)
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
fun IndeterminateCircularProgressBarDemo() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .shadow(elevation = 12.dp, shape = CircleShape, clip = true)
                .border(2.dp, Color.White, CircleShape),
            painter = painterResource(id = R.drawable.default_card),
            contentScale = ContentScale.FillBounds,
            contentDescription = "Splash Screen",
        )
    }
}

/**
 * Dialog for Fav
 */
@Composable
fun LargerDialog(onCardClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .size(400.dp, 100.dp)
                .clickable(onClick = onCardClicked),

            colors = CardDefaults.cardColors(
                containerColor = Color.DarkGray, //Card background color
                contentColor = Color.White  //Card content color,e.g.text
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painterResource(R.drawable.baseline_favorite_24),
                    contentDescription = "",
                    modifier = Modifier
                        .padding(start = 40.dp)
                        .focusable(true)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painterResource(R.drawable.baseline_favorite_24),
                    contentDescription = "",
                    modifier = Modifier
                        .padding(start = 40.dp)
                        .focusable(true)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painterResource(R.drawable.baseline_favorite_24),
                    contentDescription = "",
                    modifier = Modifier
                        .padding(start = 40.dp)
                        .focusable(true)
                )
                Spacer(modifier = Modifier.width(8.dp))
                ClickableImage(
                    resourceId = R.drawable.baseline_favorite_24,
                    onClick = { /* Handle click event for this image */ }
                )
            }
        }
    }
}

@Composable
fun ClickableImage(resourceId: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painterResource(resourceId),
            contentDescription = "",
            modifier = Modifier
                .fillMaxSize()
                .focusable(true)
        )
    }
}

@Composable
fun CardDialog(onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            modifier = Modifier
                .width(60.dp)
                .height(60.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.DarkGray,
                contentColor = Color.White
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .clickable { onDismiss() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                ClickableImage(
                    resourceId = R.drawable.baseline_favorite_24,
                    onClick = { onDismiss() }
                )
            }
        }
    }
}
fun convertStringToTime(timeString: String): LocalTime {
    // Replace the dot with a colon to get a standard time format
    val standardTimeString = timeString.replace(".", ":")
    // Parse the string into a LocalTime object
    // Assuming the time string is in a format like "H:mm"
    val formatter = DateTimeFormatter.ofPattern("H:mm")
    return LocalTime.parse(standardTimeString, formatter)
}

fun timeStringToMinutes(time: String): Int {
    val (hours, minutes) = time.split(".").map { it.toInt() }
    return hours * 60 + minutes
}

@Composable
fun HourItem(hour: String, index: Int, mainViewModel: MainViewModel, hoursIndex: Int) {
    var now = false
    var offsetFloat =0f
    val minuteOffset = EpgData.getHalfHour()

    if (index == hoursIndex) { now = true }
    if (index == 0) {
        // Applying a negative offset to the first hour item
        Text(
            text =if(index == hoursIndex) "ON NOW" else hour,
            modifier = Modifier
                .offset(x = (-40).dp)
                .onGloballyPositioned { layoutCoordinates ->
                    val positionInRoot = layoutCoordinates.positionInRoot()
                    if(now){mainViewModel.timeNowPosition =  positionInRoot.x -60}
                    mainViewModel.startTimePositions[hour] = positionInRoot.x
                }
                .padding(horizontal = 50.dp, vertical = 4.dp),
            color = Color.White
        )
    } else {
        Text(
            text = if(index == hoursIndex) "ON NOW" else hour,
            modifier = Modifier
                .padding(horizontal = 80.dp, vertical = 4.dp)
                .onGloballyPositioned { layoutCoordinates ->
                    val positionInRoot = layoutCoordinates.positionInRoot()
                    if(now){
                        mainViewModel.timeNowPosition =  positionInRoot.x -60
                        mainViewModel.isPositionSet.postValue(true)
                    }
                    mainViewModel.startTimePositions[hour] = positionInRoot.x -60
                    offsetFloat =positionInRoot.x
                }
                .drawBehind {
                    if (now) {
                        val slotWidth = size.width
                        val linePosition = slotWidth * (minuteOffset / 60f) // Calculate the position based on the minute offset
                        drawLine(
                            Color.Blue,
                            start = Offset(x = offsetFloat, y = size.height),
                            end = Offset(x = offsetFloat + 10, y = size.height - 4.dp.toPx()),
                            strokeWidth = 2.dp.toPx()
                        )
                    }
                },
            color = Color.White
        )
    }
}

@Composable
fun ProgramItem(
    program: ProgramRowItems,
    cellHeight: Dp,
    onFocusChange: (Boolean) -> Unit,
    focusRequester: FocusRequester,
    positionX: Dp
) {
    val borderWidth = 1.dp
    Box(
        modifier = Modifier
            .height(cellHeight)
            .width(positionX)
            .drawWithContent {
                drawContent()
                drawLine(
                    color = Color.Black,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = borderWidth.toPx(),
                    cap = StrokeCap.Square
                )
            }
            .onFocusChanged { focusState ->
                onFocusChange(focusState.isFocused)
            }
            .clickable(onClick = { /* Handle click event */ })
            .focusable(true)
            .focusRequester(focusRequester),
        contentAlignment = Alignment.Center
    ) {
        Divider(
            color = Color.Black,
            thickness = 1.dp,
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
                .align(Alignment.TopStart)
        )
        Text(
            text = program.programName,
            color = Color.White
        )
        if (program.isLookBack) {
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
            if (program.isRecording) {
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
    }

}

@Composable
fun ChannelContent(item: ChannelRowItems, index: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 5.dp)
            .clip(shape = RoundedCornerShape(15.dp, 0.dp, 0.dp, 15.dp))
            .background(Color.LightGray)
            .padding(4.dp)
    ) {
        if (item.channelLogo.isNotEmpty()) {
            AsyncImage(
                model = item.channelLogo,
                contentDescription = "",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterVertically)
                    .padding(5.dp),
                contentScale = ContentScale.Inside
            )
        } else {
            Text(text = item.channelName)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = (index + 1).toString(),
            modifier = Modifier.padding(0.dp, 15.dp, 0.dp, 5.dp),
            color = Color.Black
        )
        if (item.isLocked) {
            Image(
                painter = painterResource(R.drawable.baseline_lock_outline_24),
                contentDescription = "",
                modifier = Modifier
                    .padding(start = 5.dp, top = 10.dp)
                    .size(25.dp)
            )
        }
    }
}

@Composable
@Preview(device = Devices.TV_1080p)
fun EpgLayoutContentPreviewV2() {
    EpgLayoutContentV2()
}