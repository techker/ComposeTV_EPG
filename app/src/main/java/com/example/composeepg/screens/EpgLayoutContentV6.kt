package com.example.composeepg.screens

import android.view.KeyEvent.ACTION_DOWN
import android.view.KeyEvent.KEYCODE_DPAD_UP
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.composeepg.R
import com.example.composeepg.data.ChannelRowItems
import com.example.composeepg.data.MockData
import com.example.composeepg.data.ProgramRowItems
import timber.log.Timber
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun EpgLayoutContentV6() {
    val channels = MockData().createChannels()
    val programs = channels.map { channel ->
        MockData().createProgramsV2().filter { it.channelId == channel.channelID }
    }
    val reducedHours = mutableListOf<String>()
    val startedAt = "01:00" //Set Start time
    val startHour = startedAt.substringBefore(":").toInt()

    // Create list of hours according to requested start time in 24h Format
    for (hour in startHour until startHour + 24) {
        val currentHour = hour % 24
        val hourString = String.format("%02d:00", currentHour)
        val halfHourString = String.format("%02d:30", currentHour)

        reducedHours.add(hourString)
        // If we hit midnight (00:00), stop the loop
        if (hourString == "00:00") {
            break
        }
        reducedHours.add(halfHourString)
    }

    var focusedProgram by remember { mutableStateOf<ProgramRowItems?>(null) }

    if (focusedProgram != null) {
        EpgLayoutContentInfoBox(focusedProgram)
    }

    Box(
        modifier = Modifier
            .padding(top = 250.dp)
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.DarkGray)
    ) {
        EpgWithTimeBarContent(channels, programs, reducedHours) {
            focusedProgram = it
        }
    }
}

@Composable
fun EpgLayoutContentInfoBox(programSelected: ProgramRowItems?) {
    var focusedProgram by remember { mutableStateOf("-1") }
    val channels = MockData().createChannels()
    val bgwColor = MaterialTheme.colorScheme.background
    val channelInfo = channels.filter { it.channelID == programSelected!!.channelId }
    val channelDetails = channelInfo.first()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(start = 0.dp, top = 45.dp)
            .background(Color.DarkGray)
    ) {
        Column {
            if (focusedProgram.isNotEmpty() || focusedProgram.isNotBlank()) {
                if (programSelected?.programImage?.isNotEmpty() == true) {
                    if (!programSelected.isAdult) {
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
                        Box(
                            modifier = Modifier
                                .width(250.dp)
                                .padding(start = 20.dp)
                                .height(200.dp)
                                .padding(start = 20.dp, top = 10.dp, bottom = 10.dp, end = 10.dp)
                                .clip(RoundedCornerShape(topEnd = 8.dp, topStart = 8.dp))
                                .background(Color.Black.copy(alpha = 0.5f)) // Semi-transparent overlay
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = Color.White,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
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
        }
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 230.dp)

        ) {
            if (focusedProgram.isNotEmpty() || focusedProgram.isNotBlank()) {
                Timber.tag("TAG").d("selected = $programSelected")
                Text(
                    text = if (programSelected?.isAdult == true) "Adult Content" else programSelected?.programName.toString(),
                    modifier = Modifier.padding(20.dp, 10.dp, 0.dp, 0.dp),
                    color = Color.White
                )
                Text(
                    text = "Today ${
                        programSelected?.programStart.plus(" - ").plus(programSelected?.programEnd)
                    } - ${channelDetails.channelName}",
                    modifier = Modifier.padding(20.dp, 20.dp, 0.dp, 0.dp),
                    color = Color.White

                )
                Text(
                    text = if (programSelected?.isAdult == true) "R" else "TV-G,G,PG-13",
                    modifier = Modifier.padding(20.dp, 20.dp, 0.dp, 0.dp),
                    color = Color.White
                )
                //TODO if locked show text if unlock show real description
                Text(
                    text = if (programSelected?.isAdult == true) "Adult Content" else programSelected?.programDescription.toString(),
                    modifier = Modifier.padding(20.dp, 10.dp, 20.dp, 0.dp),
                    color = Color.White
                )
            }
        }
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
        ) {
            if (focusedProgram.isNotEmpty() || focusedProgram.isNotBlank()) {
                when (programSelected?.quality) {
                    "HD" -> {
                        Image(
                            painterResource(R.drawable.baseline_hd_24),
                            contentDescription = "",
                            modifier = Modifier
                                .width(48.dp)
                                .height(48.dp)
                        )
                    }

                    "SD" -> {
                        Image(
                            painterResource(R.drawable.baseline_sd_24),
                            contentDescription = "",
                            modifier = Modifier
                                .width(48.dp)
                                .height(48.dp)
                        )
                    }

                    "4K" -> {
                        Image(
                            painterResource(R.drawable.baseline_4k_24),
                            contentDescription = "",
                            modifier = Modifier
                                .width(48.dp)
                                .height(48.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun EpgWithTimeBarContent(
    channels: List<ChannelRowItems>,
    programs: List<List<ProgramRowItems>>,
    reducedHours: MutableList<String>,
    onFocusedProgram: (ProgramRowItems) -> Unit
) {
    val baseWidthPerHour = 200.dp // Define the base width for a 1-hour slot

    val channelCellContent: @Composable (Int) -> Unit = { index ->
        if (channels[index].channelLogo.isNotEmpty()) {
            AsyncImage(
                model = channels[index].channelLogo,
                contentDescription = "",
                modifier = Modifier
                    .size(40.dp),
                contentScale = ContentScale.Inside
            )
        } else {
            Text(text = channels[index].channelName)
        }
    }

    EpgWithTimeBarV2(
        channels = channels,
        programs = programs,
        baseWidthPerHour = baseWidthPerHour,
        timeSlots = reducedHours,
        channelCellContent = channelCellContent,
    ) { selected ->
        onFocusedProgram(selected)
    }
}

@Composable
fun EpgWithTimeBarV2(
    channels: List<ChannelRowItems>,
    programs: List<List<ProgramRowItems>>,
    baseWidthPerHour: Dp,
    timeSlots: List<String>,
    channelCellContent: @Composable (Int) -> Unit,
    onFocusedProgram: (ProgramRowItems) -> Unit
) {
    // Create a shared ScrollState to synchronize horizontal scrolling
    val scrollState = rememberScrollState()
    val currentTime = LocalTime.now() // Get the current time
    val timeFormatter = DateTimeFormatter.ofPattern("H:mm")
    // Remember the index of the "on now" time slot
    val onNowIndex = remember { mutableStateOf(-1) }
    // Find the index of the current "on now" time slot
    timeSlots.forEachIndexed { index, time ->
        val slotStartTime = LocalTime.parse(time, timeFormatter)
        val slotEndTime = slotStartTime.plusMinutes(30) // 30-minute increments
        // Check if current time is within this time slot
        if (currentTime.isAfter(slotStartTime) && currentTime.isBefore(slotEndTime)) {
            onNowIndex.value = index
        }
    }
    val baseWidthPx = with(LocalDensity.current) { (baseWidthPerHour / 2).toPx() }
    // Automatically scroll to the "on now" position when the composable is first loaded
    LaunchedEffect(onNowIndex.value) {
        if (onNowIndex.value != -1) {
            val scrollPosition =
                onNowIndex.value * baseWidthPx - 400    //-400 is to get the show before time
            scrollState.animateScrollTo(scrollPosition.toInt())
        }
    }
    Column(
        modifier = Modifier
            .background(Color.DarkGray)
            .fillMaxWidth()
    ) {
        // Time Bar Row
        Row(
            modifier = Modifier
        ) {
            Surface(
                color = Color.DarkGray,
                modifier = Modifier
                    .width(160.dp)
                    .background(Color.DarkGray)
            ) {
                Text(
                    text = "Today",
                    modifier = Modifier.padding(start = 30.dp),
                    color = Color.White
                )
            }

            // Time slots start after the channel column
            Row(
                modifier = Modifier
                    .background(Color.DarkGray)
                    .horizontalScroll(scrollState) // Synchronize with program grid scroll state
            ) {
                timeSlots.forEach { time ->
                    /**
                     * Time handling
                     * Check if current time is within this time slot
                     * 30-minute increments
                     */
                    val slotStartTime = LocalTime.parse(time, timeFormatter)
                    val slotEndTime = slotStartTime.plusMinutes(30)
                    val isOnNow =
                        currentTime.isAfter(slotStartTime) && currentTime.isBefore(slotEndTime)

                    // Calculate the progress if it is the current time slot
                    val progress = if (isOnNow) {
                        val totalMinutesInSlot =
                            Duration.between(slotStartTime, slotEndTime).toMinutes().toFloat()
                        val minutesPassed =
                            Duration.between(slotStartTime, currentTime).toMinutes().toFloat()
                        minutesPassed / totalMinutesInSlot
                    } else {
                        0f
                    }

                    Surface(
                        color = Color.DarkGray, // Set the background color for the surface
                        modifier = Modifier
                            .width(baseWidthPerHour / 2)// Width corresponds to 1 hour
                            .background(Color.DarkGray)
                    ) {
                        /**
                         * Line in front of the time
                         */
                        Canvas(
                            modifier = Modifier
                                .padding(top = 12.dp, start = 3.dp)
                                .height(10.dp)
                                .width(2.dp)

                        ) {
                            drawLine(
                                color = if (isOnNow) Color.Blue else Color.White,
                                start = Offset(0f, 0f),
                                end = Offset(0f, size.height),
                                strokeWidth = 5f
                            )
                            // If it's the current time slot, draw the progress line/indicator
                            if (isOnNow) {
                                // Draw a progress line or rectangle indicating time passed
                                val progressBarWidth = size.width * progress
                                drawRect(
                                    color = Color.Blue,
                                    topLeft = Offset(0f, 0f),
                                    size = Size(
                                        progressBarWidth,
                                        size.height
                                    )
                                )
                            }
                        }
                        Text(
                            text = time,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Start,
                            color = Color.White,
                            modifier = Modifier.padding(start = 5.dp)
                        )
                    }
                }
            }
        }

        // Draw the line under the entire row
        // Under the Time indicator
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
        ) {
            drawLine(
                color = Color.LightGray,
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = 2f
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        EpgTableTimeV2(
            channels = channels,
            programs = programs,
            baseWidthPerHour = baseWidthPerHour,
            modifier = Modifier,
            channelCellContent = channelCellContent,
            scrollState = scrollState,
        ) { selected ->
            onFocusedProgram(selected)
        }
    }
}

@Composable
fun EpgTableTimeV2(
    channels: List<ChannelRowItems>,
    programs: List<List<ProgramRowItems>>,
    baseWidthPerHour: Dp,
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    channelCellContent: @Composable (Int) -> Unit,
    onFocusedProgram: (ProgramRowItems) -> Unit,
    ) {
    // Create a vertical scroll state for the channels
    val verticalScrollState = rememberScrollState()
    val bgwColor = Color.DarkGray
    val currentTime = LocalTime.now() // Current time
    Surface(
        color = Color.DarkGray, // Set the background color for the surface
        modifier = modifier
            .verticalScroll(verticalScrollState),
    ) {
        Column(
            modifier = Modifier
                .padding(5.dp)
                .background(Color.DarkGray)
        ) {
            val focusRequesters = remember { mutableStateListOf<FocusRequester>() }
            channels.forEachIndexed { channelIndex, _ ->
                val rowFocusRequester = remember { FocusRequester() }
                focusRequesters.add(rowFocusRequester)
                // Handle DPAD since Surface is not focusable by default
                Row(
                    modifier = Modifier
                        .focusRequester(rowFocusRequester)
                        .onKeyEvent { keyEvent ->
                            if (keyEvent.nativeKeyEvent.action == ACTION_DOWN) {
                                return@onKeyEvent KeyEventPropagation.ContinuePropagation
                            }
                            if (keyEvent.nativeKeyEvent.keyCode == KEYCODE_DPAD_UP && channelIndex > 0) {
                                val previousRowFocusRequester = focusRequesters[channelIndex - 1]
                                previousRowFocusRequester.requestFocus()
                                return@onKeyEvent KeyEventPropagation.StopPropagation
                            }
                            KeyEventPropagation.ContinuePropagation
                        }
                ) {
                    // Display the channel name
                    Surface(
                        color = Color.DarkGray,
                        contentColor = Color.Transparent,
                        modifier = Modifier
                            .width(150.dp) //Channel Column Width
                            .background(bgwColor)

                    ) {
                        channelCellContent(channelIndex)
                    }

                    // Display programs for this channel
                    val channelPrograms = programs[channelIndex]
                    Row(
                        modifier = Modifier
                            .horizontalScroll(scrollState) // Synchronize scrolling with time bar
                    ) {
                        /**
                         * Track focus state for each program cell
                         * Parse the start and end time from strings
                         * Calculate the duration in minutes between start and end times
                         * Calculate width based on duration in hours
                         * Focus management inside Box
                         */
                        channelPrograms.forEachIndexed { programIndex, program ->
                            val focusRequester = remember { FocusRequester() }
                            var isFocused by remember { mutableStateOf(false) }
                            val formatter = DateTimeFormatter.ofPattern("HH:mm")
                            val startTime = LocalTime.parse(program.programStart, formatter)
                            val endTime = LocalTime.parse(program.programEnd, formatter)
                            var lastFocusedProgram by remember {
                                mutableStateOf<ProgramRowItems?>(
                                    null
                                )
                            }
                            val durationMinutes = Duration.between(startTime, endTime).toMinutes()
                            val calculatedWidth =
                                baseWidthPerHour * (durationMinutes / 60f)
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(calculatedWidth)
                                    .onFocusChanged { focusState ->
                                        isFocused = focusState.isFocused
                                        if (focusState.isFocused && lastFocusedProgram != program) {
                                            lastFocusedProgram = program
                                            onFocusedProgram(program)
                                        }
                                    }
                                    .focusRequester(focusRequester)
                                    .clickable(onClick = {
                                        // Handle click event
                                    })
                                    .focusable(true), // Only make the Box focusable, not Surface
                                contentAlignment = Alignment.Center
                            ) {

                                CustomProgramCellV2(
                                    program = program,
                                    isFocused = isFocused,
                                    calculatedWidth = calculatedWidth,
                                )
                            }
                            // Automatically request focus for the "on now" program
                            // Only request focus for the onNowIndex program in row 0
                            // If the current time is within the program's time range and row 0
                            if (channelIndex == 0 && startTime.isBefore(currentTime) && endTime.isAfter(
                                    currentTime
                                )
                            ) {
                                LaunchedEffect(key1 = programIndex) {
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

internal object KeyEventPropagation {
    const val StopPropagation = true
    const val ContinuePropagation = false
}

/**
 * Params
 * Program Index
 * Program Object
 * Focus handling
 * Pass the width of the cell
 * Pass the onNowIndex to check which program is "on now"
 * FocusRequester to request focus
 */

@Composable
fun CustomProgramCellV2(
    program: ProgramRowItems,
    isFocused: Boolean,
    calculatedWidth: Dp,
) {
    val textColor = if (isFocused) Color.Black else Color.White
    val textAlignment = if (calculatedWidth > 200.dp) Alignment.Center else Alignment.CenterStart
    var hasOptions by remember { mutableStateOf(false) }

    // Calculate the progress if the program is currently airing
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val currentTime = LocalTime.now()
    val startTime = LocalTime.parse(program.programStart, formatter)
    val endTime = LocalTime.parse(program.programEnd, formatter)

    val isOnNowProgram = startTime.isBefore(currentTime) && endTime.isAfter(currentTime)

    val progress =
        if (isOnNowProgram && currentTime.isAfter(startTime) && currentTime.isBefore(endTime)) {
            val totalMinutes = Duration.between(startTime, endTime).toMinutes().toFloat()
            val minutesPassed = Duration.between(startTime, currentTime).toMinutes().toFloat()
            minutesPassed / totalMinutes
        } else {
            0f
        }
    // Convert Dp to Px
    val density = LocalDensity.current
    val calculatedWidthPx = with(density) { calculatedWidth.toPx() }

    // Create the gradient color for the background
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(
            Color.Blue,    // Progress part
            Color.Gray     // Remaining part
        ),
        startX = 0f,
        endX = calculatedWidthPx * progress // Adjust the gradient to show progress
    )

    Surface(
        modifier = Modifier
            .background(Color.DarkGray)
            .padding(3.dp)
            .focusable(true),
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(
                modifier = if (isFocused && isOnNowProgram) {
                    Modifier.background(brush = gradientBrush) // Apply gradient if it's the current program and focused
                } else {
                    Modifier.background(Color.LightGray) // Default background if not the current program
                }
                    .weight(1f)
                    .padding(start = 8.dp) // Padding between the icon and text
                    .onFocusChanged {
                        //Handle or do nothing
                    }
                    .focusable(true)

            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Box(
                        modifier = Modifier
                            .padding(8.dp),
                        contentAlignment = textAlignment
                    ) {

                        Text(
                            text = program.programName,
                            fontSize = 16.sp,
                            color = textColor,
                            maxLines = 1,
                            textAlign = TextAlign.Start,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 5.dp)
                        ) {

                            Row(modifier = Modifier.align(Alignment.TopEnd)) {
                                if (program.isLookBack) {
                                    hasOptions = true
                                    Image(
                                        painterResource(R.drawable.reset),
                                        contentDescription = "",
                                        modifier = Modifier
                                            .width(10.dp)
                                    )

                                }
                                if (program.isRecording) {
                                    hasOptions = true
                                    Image(
                                        painterResource(R.drawable.ic_record),
                                        contentDescription = "",
                                        modifier = Modifier
                                            .width(10.dp)
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

@Preview(device = Devices.TV_720p)
@Composable
fun ShowEpgLayoutPrev() {
    EpgLayoutContentV6()
}