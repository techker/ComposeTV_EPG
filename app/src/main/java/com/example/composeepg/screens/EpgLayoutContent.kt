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
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.composeepg.R
import com.example.composeepg.data.ChannelRowItems
import com.example.composeepg.data.MockData
import com.example.composeepg.data.ProgramRowItems
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun EpgLayoutContent() {

    /**
     * Create Hours with Half Hour
     */
    val hoursList = mutableListOf<String>()
        for (hour in 0 until 24) {
        hoursList.add("$hour:00")
        hoursList.add("$hour:30")
    }

    val programsList= MockData().createPrograms()
    val channelList = MockData().createChannels()
    CreateViewV3( { scrollable, scrollToFirst ->
        Log.d("TAG","onScroll : scrollable = $scrollable, scrollToFirst = $scrollToFirst")
    }, channelList, programsList, hoursList)

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
    val focusRequester = remember { FocusRequester() }
    val focusRequesterPrg = remember { FocusRequester() }
    var hasFocus by remember { mutableStateOf(false) }
    val firstColumnWidth = 100.dp
    val cellHeight = 50.dp
    val verticalScrollState = rememberScrollState()
    var focusedIndex by remember { mutableStateOf(-1) }
    var focusedIndexP by remember { mutableStateOf(-1) }
    var hasFocusP by remember { mutableStateOf(false) }
    var listCompleted by remember { mutableStateOf(false) }
    var componentWidth by remember { mutableStateOf(0.dp) }
    var focusedProgram by remember { mutableStateOf("-1") }

    val density = LocalDensity.current
    val borderWidth = 1.dp
    val gradientColors = listOf(
        Color.DarkGray,
        Color.Blue,
        Color.Transparent
    )
    val reducedHours = mutableListOf<String>()
    val startedAt= "07:00"
    val startHour = startedAt.substringBefore(":").toInt() - 5    //put -5 to get the time in East, need adjust
    // Create list of hours according to requested start time
    for (hour in startHour until startHour + 24) {
        val currentHour = hour % 24
        reducedHours.add("$currentHour:00")
        reducedHours.add("$currentHour:30")
    }

    val currentTime = LocalTime.now()
    val roundedTime = LocalTime.of(currentTime.hour, if (currentTime.minute < 30) 0 else 30) // Round time to nearest half hour
    val formatter = DateTimeFormatter.ofPattern("H:mm") // Format as "hour:minute"
    val currentTimeString = roundedTime.format(formatter) // Format rounded time as string

    val indexReduced = reducedHours.indexOf(currentTimeString)

    var hoursIndex =0
    if (indexReduced != -1) {
        println("Current time is at index: $indexReduced time ${reducedHours.get(indexReduced)}")
        hoursIndex = indexReduced
    } else {
        println("Current time not found in the list")
    }

    /**
     *   .focusable(true) can only be in one part at a time.
     */
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
    val shape =  RoundedCornerShape(8.dp)

    val bgwColor = MaterialTheme.colorScheme.background
    val recordColor = MaterialTheme.colorScheme.errorContainer



    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(start = 10.dp,top = 10.dp,bottom = 10.dp)
            .background(Color.Transparent, shape = shape)

    ){
        Column {
            val selected =   programsList.filter { it.programID.toString() == focusedProgram }
            if(focusedProgram != "-1") {

                AsyncImage(
                    model = selected[0].programImage,
                    contentDescription = "image",
                    modifier = Modifier
                        .width(250.dp)
                        .padding(start = 20.dp)
                        .height(200.dp)
                        .padding(start = 20.dp, top = 10.dp, bottom = 10.dp, end = 10.dp)
                        .clip(RoundedCornerShape(topEnd = 8.dp , topStart = 8.dp))
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

            }else{
                Image(
                    painter = painterResource(R.drawable.default_card),
                    contentDescription = "image:",
                    modifier = Modifier
                        .align(Alignment.Start)
                        .width(250.dp)
                        .padding(start = 20.dp, top = 10.dp, bottom = 10.dp, end = 10.dp)
                        .height(200.dp)
                        .clip(RoundedCornerShape(topEnd = 8.dp , topStart = 8.dp))
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
            val selected =   programsList.filter { it.programID.toString()== focusedProgram }
            if(focusedProgram != "-1"){
                Log.d("TAG","selected = $selected")
                Text(
                    text = selected[0].programName,
                    modifier = Modifier.padding(20.dp,10.dp,0.dp,0.dp),
                    color = Color.White
                )
                Text(
                    text = "TV-G,G,PG-13",
                    modifier = Modifier.padding(20.dp,20.dp,0.dp,0.dp),
                    color = Color.White
                )
                Text(
                    text = "Program Description Goes Here",
                    modifier = Modifier.padding(20.dp,30.dp,20.dp,0.dp),
                    color = Color.White
                )
            }
        }
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .width(400.dp)
                .padding(end = 50.dp)
        ) {


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
                .padding(start = 40.dp, bottom = 20.dp)
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

                Text(text = showTime,
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
                    val color = if(itemC.isFavorite) Color.Red else Color.LightGray
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
//                                    .onFocusChanged { isFocused ->
//                                        if (isFocused.isFocused) {
//                                            focusedIndex = index
//                                            hasFocus = true
//                                        }
//                                    }
                                    .clip(shape = RoundedCornerShape(15.dp, 0.dp, 0.dp, 15.dp))
                                    .clickable(onClick = { /* Handle click event */ })
//                                    .focusable(true)
                                    //.focusRequester(focusRequester)
//                                    .scrollable(
//                                        state = verticalScrollState,
//                                        orientation = Orientation.Vertical
//                                    )
                                    .border(1.dp, color)
                                    .background(Color.LightGray)
                                    .padding(4.dp),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 5.dp)
                                ) {
                                    if(itemC.isLocked){
                                        Image(
                                            painterResource(R.drawable.baseline_lock_outline_24),
                                            contentDescription = "",
                                            modifier = Modifier
                                                .width(50.dp)
                                                .height(50.dp)
                                        )
                                    }else {
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
                                        text = index.toString(),
                                        modifier = Modifier.padding(0.dp,10.dp,0.dp,5.dp),
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
                        //Number of channels
                        //TODO fill Gaps with empty programs and cell Size.
                        itemsIndexed(items = channelsList) { index, item ->
                            if(index == channelsList.size){
                                listCompleted = true
                            }
                            val channelID = item.channelID

                            Row {
                                val fileteredPrg = programsList.filter { it.programID == channelID }
                                fileteredPrg.forEachIndexed { i, itemPrg ->
                                    val prgStart = itemPrg.programStart
                                    val prgEnd = itemPrg.programEnd

                                    /**
                                     * Need to fix according to program time
                                     * Need the width of the hours cell
                                     */
                                    val cellWidth = 320 //60min

                                    Column(
                                        modifier = Modifier
                                            .height(cellHeight)
                                            .width(cellWidth.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .height(cellHeight)
                                                .width(cellWidth.dp)  ///individual with per time
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
                                                        focusedIndexP = index
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
                                            Text(text = "${itemPrg.programName}", color = Color.White)
                                            if(itemPrg.isLookBack) {
                                                Column(
                                                    modifier = Modifier.padding(start = 30.dp)
                                                        .align(Alignment.CenterStart)
                                                ) {
                                                    Image(
                                                        painterResource(R.drawable.reset),
                                                        contentDescription = "",
                                                        modifier = Modifier
                                                            //.width(20.dp)
                                                            //.height(20.dp)
                                                            .width(30.dp)
                                                            .align(Alignment.Start)
                                                            .padding(start = 20.dp)
                                                    )
                                                }
                                               }
                                            Column(modifier = Modifier
                                                .padding(end = 0.dp)
                                                .align(Alignment.CenterEnd)){
                                                //Draws circle
//                                                Box(
//                                                    modifier = Modifier
//                                                        .size(10.dp)
//                                                        .clip(CircleShape)
//                                                        .background(Color.Red)
//                                                )
                                                if(itemPrg.isRecording) {
                                                    Divider(
                                                        color = Color.Red,
                                                        thickness = 3.dp,
                                                        modifier = Modifier
                                                            .fillMaxHeight()
                                                            .width(3.dp)
                                                            .align(Alignment.End)
                                                            .drawWithContent {
                                                                drawContent()
                                                                drawRect(
                                                                    Brush.verticalGradient(
                                                                        colors = listOf(
                                                                            Color.Transparent,
                                                                            recordColor.copy(alpha = 0.5f)
                                                                        )
                                                                    )
                                                                )
                                                            },
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

@Composable
@Preview(device = Devices.TV_1080p)
fun EpgLayoutContentPreview() {
    EpgLayoutContent()
}