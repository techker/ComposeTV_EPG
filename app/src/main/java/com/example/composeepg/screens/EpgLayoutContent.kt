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
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date

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
        is HomeScreenUiState.Loading ->  IndeterminateCircularProgressBarDemo()
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

    val timeList = EpgData.generateTimeList(startTime,endTime,halfHour)
    Timber.tag("TAG").d("Epg Time List is ${timeList.size} - ${timeList.first()} offset $hoursOffset")

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

    LaunchedEffect(lazyListStateMainTable.firstVisibleItemScrollOffset) {
        if (!lazyListStateFirstColumn.isScrollInProgress) {
            lazyListStateFirstColumn.scrollToItem(
                lazyListStateMainTable.firstVisibleItemIndex,
                lazyListStateMainTable.firstVisibleItemScrollOffset
            )
        }
    }

    LaunchedEffect(lazyListStateFirstColumn.firstVisibleItemScrollOffset) {
        if (!lazyListStateMainTable.isScrollInProgress) {
            lazyListStateMainTable.scrollToItem(
                lazyListStateFirstColumn.firstVisibleItemIndex,
                lazyListStateFirstColumn.firstVisibleItemScrollOffset
            )
        }
        onVerticalScroll(
            lazyListStateFirstColumn.isScrollInProgress,
            lazyListStateFirstColumn.firstVisibleItemIndex <= 1
        )
    }
    val shape = RoundedCornerShape(8.dp)

    val bgwColor = MaterialTheme.colorScheme.background
    val recordColor = MaterialTheme.colorScheme.errorContainer

    /**
     * Opens Favorite Dialog
     */
    var isOpen by remember { mutableStateOf(false) }
    LaunchedEffect(true) {
        coroutineScope.launch {
            //testing Scroll to position
            //val scrollPosition = 15500 // Adjust itemWidth as needed
            //horizontalScrollState.scrollTo(scrollPosition)
            // Animate scroll to the 5th item
            //lazyListStatePrograms.animateScrollToItem(index = 5)
            //horizontalScrollState.scrollTo(hoursIndex)
            //horizontalScrollState.animateScrollTo(scrollPosition)
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
                val programSelected = MockData().getProgramData(focusedIndexP,focusedIndexCh)
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
                val programSelected = MockData().getProgramData(focusedIndexP,focusedIndexCh)
                Text(
                    text = programSelected.programStart.plus(" - ").plus(programSelected.programEnd),
                    modifier = Modifier.padding(0.dp, 10.dp, 200.dp, 0.dp),
                    color = Color.White
                )
                Text(
                    text = channelData.channelID.toString().plus(" - ").plus(channelData.channelName),
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
                .padding(start = 60.dp, bottom = 20.dp)
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
                        .padding(horizontal = 60.dp, vertical = 4.dp)
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
                state = lazyListStateFirstColumn,
            ) {
                /**
                 * Channels
                 */
                itemsIndexed(items = channelsList, key = { _, itemB -> itemB.channelID!! })

                { index, itemC ->
                    val color = if (itemC.isFavorite) Color.Red else Color.LightGray
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
                                    if (itemC.isLocked) {
                                        Image(
                                            painterResource(R.drawable.baseline_lock_outline_24),
                                            contentDescription = "",
                                            modifier = Modifier
                                                .width(50.dp)
                                                .height(50.dp)
                                        )
                                    } else {
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
                                    }
                                    Spacer(modifier = Modifier.width(10.dp)) // Add spacing between texts
                                    Text(
                                        text = index.plus(1).toString(),
                                        modifier = Modifier.padding(0.dp, 10.dp, 0.dp, 5.dp),
                                        color = Color.Black
                                    )
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
                    .padding(top = 30.dp)
            ) {
                Box(
                    modifier = Modifier
                        .wrapContentSize()
                        .horizontalScroll(state = horizontalScrollState)
                ) {
                    LazyColumn(
                        modifier = Modifier.wrapContentSize(),
                        contentPadding = PaddingValues(bottom = 8.dp),
                        state = lazyListStateMainTable
                    ) {
                        //TODO fill Gaps with empty programs and cell Size.
                        itemsIndexed(items = channelsList) { index, item ->
                            if (index == channelsList.size) {
                                listCompleted = true
                            }
                            val channelID = item.channelID

                            Row {
                                val allPrograms = MockData().getAllProgramsForChannel(channelID)
                                allPrograms.forEachIndexed { i, itemPrg ->

                                            val prgStart = itemPrg.programStart.toDouble()
                                            val prgEnd = itemPrg.programEnd.toDouble()
                                            val pgmTime = (prgEnd - prgStart)
                                            //get program duration to milliseconds and convert to pixels
                                            val px = EpgData.convertMillisecondsToPx(
                                            pgmTime,context).toInt()
                                            Timber.tag("TAG").d("Program cell PX $px")

                                             val halfHourWidth = 50.dp.value
                                             val programWidthDp = (pgmTime * 2 * halfHourWidth).dp
                                             Log.d("TAG","Program cell PX $px plus half hour $halfHour pgTime $pgmTime programWidthDp $programWidthDp prgStart $prgStart prgEnd $prgEnd")
                                            /**
                                             * Need to fix according to program time
                                             * Need the width of the hours cell
                                             */
                                            val cellWidth = 320 //60min

                                            Column(
                                                modifier = Modifier
                                                    //.height(cellHeight)
//                                                    .width(programWidthDp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .height(cellHeight)
                                                        .width(programWidthDp)  ///individual with per time
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
                                                                Timber
                                                                    .tag("TAG")
                                                                    .d("isFocused = $index program id = ${itemPrg.programID} channel id $channelID or program ${itemPrg.channelId}")
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
                                                                .padding(start = 30.dp)
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
                                                            .padding(end = 0.dp)
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

}

private fun printDetails(px: Int, programWidthDp: Dp) {
    Timber.tag("TAG").d("PRINT = $px and $programWidthDp" )
    Log.d("TAG", "PRINT = $px and $programWidthDp")
}

private fun fillGaps(hoursList: MutableList<String>, programsList: Int) {
    /**
     * Need to fill the gaps on left over hours that have no programs
     * hours list - programs list
     * need programs id and add left over.
     * divide hours / 2 since you have half hours
     * Number of channels
     * number of shows per channel
     */
    val availableHoursLeft = (hoursList.size / 2) //Divide by 2 to remove the half hours
    val toFill = availableHoursLeft - programsList //Number of programs to fill

    /**
     * Need to fill the gaps on left over hours that have no programs
     * and associate channel to programs
     * ProgramRowItems(programID = add, programName = "Gaps i", "",programStart = "1.00", programEnd = "2.00", channelId = 1,false,false,false)    }
     */

    Timber.tag("TAG").d("availableHoursLeft = $availableHoursLeft from ${hoursList.size} number of prg $programsList toFill $toFill")
    for (i in programsList until availableHoursLeft) {
        //ProgramRowItems(programID = 1, programName = "Gaps i", "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "1.00", programEnd = "2.00", channelId = 1,true,true,false)    }
        Timber.tag("TAG").d( "programRowItems  index = $i")
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
        ){
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