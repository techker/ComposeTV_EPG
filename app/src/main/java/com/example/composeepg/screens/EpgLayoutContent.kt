package com.example.composeepg.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
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
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun EpgLayoutContent(mainViewModel: MainViewModel = viewModel()) {
    val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()
    var isOpen by remember { mutableStateOf(true) }

    /**
     * Create Hours with Half Hour
     */
    val hoursList = mutableListOf<String>()
    for (hour in 0 until 24) {
        hoursList.add("$hour:00")
        hoursList.add("$hour:30")
    }

    val programsList = MockData().createPrograms()
    val channelList = MockData().createChannels()
    val numberOfPrg = MockData().returnProgramRows(programsList)
    val numberOfChannels = MockData().returnChannelRows(channelList)

    when (val s = uiState) {
        is HomeScreenUiState.Ready -> {
//            if (isOpen) {
//                // Call CardDialog and provide a lambda to dismiss the dialog
//                CardDialog(onDismiss = { isOpen = false })
//            }
            CreateViewV3({ scrollable, scrollToFirst ->
                Log.d("TAG", "onScroll : scrollable = $scrollable, scrollToFirst = $scrollToFirst")
            }, s.channelList, programsList, hoursList)
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
fun CreateViewV3(
    onVerticalScroll: (Boolean, Boolean) -> Unit,
    channelsList: MutableList<ChannelRowItems>,
    programsList: MutableList<ProgramRowItems>,
    hoursList: MutableList<String>
) {
    /**
     * HoursList can be used if you want to see all Hours
     * In this case i start at the current time
     */
    val lazyListStateMainTable = rememberLazyListState()
    val lazyListStateFirstColumn = rememberLazyListState()
    val horizontalScrollState = rememberScrollState()
    val lazyListStatePrograms = rememberLazyListState()
    val focusRequester = remember { FocusRequester() }
    val focusRequesterPrg = remember { FocusRequester() }
    var hasFocus by remember { mutableStateOf(false) }
    val firstColumnWidth = 130.dp
    val cellHeight = 50.dp
    val verticalScrollState = rememberScrollState()
    var focusedIndex by remember { mutableStateOf(-1) }
    var focusedIndexP by remember { mutableStateOf(-1) }
    var focusedIndexCh by remember { mutableStateOf(-1) }
    var hasFocusP by remember { mutableStateOf(false) }
    var listCompleted by remember { mutableStateOf(false) }
    var componentWidth by remember { mutableStateOf(0.dp) }
    var focusedProgram by remember { mutableStateOf("-1") }
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val context = LocalContext.current
    val borderWidth = 1.dp
    var pxHour = 0
    val sharedLazyListState = rememberLazyListState()
    val gradientColors = listOf(
        Color.DarkGray,
        Color.Blue,
        Color.Transparent
    )
    //Demo how to calculate TimeBar Start
    val calendar = Calendar.getInstance()
    var startTime = EpgData.getStartTimeLookBack(1)
    val endTime = EpgData.getEndTime()
    val halfHour = EpgData.getHalfHour()
    val date = Date(startTime)
    calendar.time = date
    val minutes = calendar[Calendar.MINUTE]
    val diff = 60 - minutes
    //have only precise time from half and half an hour
    startTime += diff * 60000

    val hoursOffset = EpgData.calculateOffset(context)

    val timeList = EpgData.generateTimeList(startTime, endTime, halfHour)


    //End

    val reducedHours = mutableListOf<String>()
    val startedAt = "01:00"
    val startHour =
        startedAt.substringBefore(":").toInt()

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
        println("Current time is at index: $indexReduced time ${reducedHours.get(indexReduced)} offset $hoursOffset")
        hoursIndex = indexReduced
    } else {
        println("Current time not found in the list")
    }
    val hour = TimeUnit.HOURS.toMillis(1)
    pxHour = EpgData.convertMillisecondsToPx((hour / 2).toDouble(), context).toInt()

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

    //Debug logs for Offset
    //Channels Size: 220 x 720, Position: Offset(40.0, 360.0)
    //First Vissible View Programs Size : 10080 x 720, Position: Offset(260.0, 360.0)
    //Scrolling right  Programs Size : 10080 x 720, Position: Offset(0.0, 360.0)
    //Scrolling down Programs Size : 10080 x 720, Position: Offset(-240.0, 360.0)
    val shape = RoundedCornerShape(8.dp)
    val bgwColor = MaterialTheme.colorScheme.background

    val scrollPosition = (hoursOffset - 30) * hoursIndex
        Log.d("TAG","Epg Time List is ${timeList.size} - ${timeList.first()} offset $hoursOffset scrollPosition $scrollPosition")
    /**
     * Opens Favorite Dialog
     */
    var isOpen by remember { mutableStateOf(false) }
    LaunchedEffect(true) {
        coroutineScope.launch {
            horizontalScrollState.scrollTo(scrollPosition.toInt())
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
        /**
         * Hours
         */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(state = horizontalScrollState)
                .padding(start = 90.dp, bottom = 20.dp)
                .onGloballyPositioned {
                    componentWidth = with(density) {
                        it.size.width.toDp()
                    }
                }
                .background(Color.Transparent)
                .clip(shape = RoundedCornerShape(15.dp, 0.dp, 0.dp, 0.dp))

        ) {
            reducedHours.forEachIndexed { i, item ->
                val showTime =
                    if (i == hoursIndex) {  // If time equals roundTimeMs then show 'ON NOW' text on time bar.
                        "ON NOW"
                    } else {  // else show only time.
                        item
                    }

                Text(
                    text = showTime,
                    modifier = Modifier
                        .padding(horizontal = 40.dp, vertical = 4.dp)
                        .onGloballyPositioned {
                        },
                    color = Color.White
                )
            }
        }
        Row(
            modifier = Modifier
                .wrapContentSize()

        ) {
            LazyColumn(
                modifier = Modifier
                    .width(firstColumnWidth)
                    .padding(start = 20.dp, top = 30.dp)
                    .wrapContentSize(),
                contentPadding = PaddingValues(bottom = 8.dp),
                state = sharedLazyListState,
            ) {
                /**
                 * Channels
                 */
                itemsIndexed(items = channelsList, key = { _, itemB -> itemB.channelID!! })

                { index, itemC ->
                    Box(
                        modifier = Modifier
                            .width(firstColumnWidth)
                            .height(cellHeight)

                    ) {
                        Column(
                            modifier = Modifier
                                .width(firstColumnWidth)
                                .height(cellHeight)
                        ) {
                            Column(
                                modifier = Modifier
                                    .width(firstColumnWidth)
                                    .height(cellHeight)
                                    .clip(shape = RoundedCornerShape(15.dp, 0.dp, 0.dp, 15.dp))
                                    .clickable(onClick = {
                                        isOpen = true
                                    })
                                    .scrollable(
                                        state = verticalScrollState,
                                        orientation = Orientation.Vertical
                                    )
                                    .border(1.dp, Color.White)
                                    .background(Color.LightGray)
                                    .padding(4.dp),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 5.dp)
                                ) {

                                        if (itemC.channelLogo.isNotEmpty()) {
                                            AsyncImage(
                                                model = itemC.channelLogo,
                                                contentDescription = "",
                                                modifier = Modifier
                                                    .width(50.dp)
                                                    .height(50.dp)
                                                    .clip(
                                                        RoundedCornerShape(
                                                            topStart = 8.dp,
                                                            bottomStart = 8.dp
                                                        )
                                                    )
                                                    .drawWithContent {
                                                        drawContent()
                                                        drawRect(
                                                            Brush.verticalGradient(
                                                                colors = listOf(
                                                                    Color.Transparent,
                                                                    Color.DarkGray.copy(alpha = 0.5f)
                                                                )
                                                            )
                                                        )
                                                    },
                                                contentScale = ContentScale.Inside
                                            )
                                        } else {
                                            Text(text = itemC.channelName)
                                        }
                                    Spacer(modifier = Modifier.width(10.dp)) // Add spacing between texts
                                    Text(
                                        text = index.plus(1).toString(),
                                        modifier = Modifier.padding(0.dp, 10.dp, 0.dp, 5.dp),
                                        color = Color.Black
                                    )
                                    if(itemC.isLocked) {
                                        Image(
                                            painterResource(R.drawable.baseline_lock_outline_24),
                                            contentDescription = "",
                                            modifier = Modifier
                                                .padding(start = 5.dp, top = 5.dp)
                                                .width(25.dp)
                                                .height(25.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            /**
             * Programs
             */
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .background(Color.Transparent)
                    .horizontalScroll(state = horizontalScrollState)
                    .padding(top = 30.dp)
            ) {
                    LazyColumn(
                        modifier = Modifier.wrapContentSize(),
                        contentPadding = PaddingValues(bottom = 8.dp),
                        state = sharedLazyListState
                    ) {
                        itemsIndexed(items = channelsList) { index, item ->
                            if (index == channelsList.size) {
                                listCompleted = true
                            }
                            val channelID = item.channelID

                            Row {
                                val allPrograms = MockData().getAllProgramsForChannel(channelID)
                                allPrograms.forEachIndexed { _, itemPrg ->
                                    val convertedStart = convertStartToTime(itemPrg.programStart)
                                    val convertedEnd = convertEndToTime(itemPrg.programEnd)
                                    val roundedStartTime = EpgData.getNearestHalfHour(convertedStart)
                                    val roundedEndTime = EpgData.getRoundedTimetoNearestPastHalfHour(convertedEnd)
                                    val pgmTimes = (roundedEndTime - roundedStartTime)
                                    val pxx = EpgData.convertMillisecondsToPx(pgmTimes.toDouble(),context
                                    ).toInt() / 2

                                    /**
                                     * 3600000 1h current- 480  2x   should be-240
                                     * 1800000 1/2h    -240 1x                -120
                                     * 7200000 2h      960  4x
                                     * 10800000 3h
                                     */
                                    Log.d("TAG","Program  width $pxx "  + itemPrg.programName +  " or pgmTimes $pgmTimes  prgStart " + convertedStart + " prgEnd " + convertedEnd)
                                    Column(
                                        modifier = Modifier
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .height(cellHeight)
                                                .width(pxx.dp)  ///individual with per time
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
                                                .onFocusChanged { isFocused ->
                                                    if (isFocused.isFocused) {
                                                        focusedIndex = index + 1
                                                        focusedIndexP = itemPrg.programID
                                                        focusedIndexCh = itemPrg.channelId
                                                        hasFocusP = true
                                                        focusedProgram =
                                                            itemPrg.programID.toString()
                                                    }
                                                }
                                                .clickable(onClick = { /* Handle click event */ })
                                                .focusable(true)
                                                .focusRequester(focusRequesterPrg),
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
                                                text = itemPrg.programName,
                                                color = Color.White
                                            )
                                            if (itemPrg.isLookBack) {
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
                                                if (itemPrg.isRecording) {
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
                                }
                            }
                        }
                    }
                }
            }
        }
}

fun convertStartToTime(start:String):Long{
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return try {
        val datePart = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(today.time)
        val formattedTimeStart = formatTime(start).replace(".", ":")
        val startTime = format.parse("$datePart $formattedTimeStart")
        startTime?.time ?: -1L
    } catch (e: Exception) {
        Log.d("TAG", "Converted start Error ${e.message}")
        -1L // Return a default value indicating failure
    }
}
fun convertEndToTime(end:String):Long{
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return try {
        val datePart = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(today.time)
        val formattedTimeEnd = formatTime(end).replace(".", ":")
        val endTime = format.parse("$datePart $formattedTimeEnd")
        endTime?.time ?: -1L
    } catch (e: Exception) {
        Log.d("TAG","Converted start Error ${e.message}")
        -1L // Return a default value indicating failure
    }
}

/**
 * Using this since the Mock is set as 1.00
 */
fun formatTime(time: String): String {
    // Split the time string by the dot to separate hours and minutes
    val parts = time.split(".")
    // Check if the hour part (before the dot) consists of a single digit
    val hours = parts[0]
    val formattedHours = if (hours.length == 1) "0$hours" else hours
    // Reconstruct the time string with the formatted hours
    return "$formattedHours.${parts[1]}"
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

@Composable
@Preview(device = Devices.TV_1080p)
fun EpgLayoutContentPreview() {
    EpgLayoutContent()
}