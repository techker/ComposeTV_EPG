package com.example.composeepg.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.composeepg.R

data class TimeSlot(
    val time: String,
    val startHour: Float
)

data class Program(
    val title: String,
    val startTime: Float, // Changed from Int to Float to handle half hours
    val duration: Float, // Duration in hours (can be 0.5, 1, 1.5, etc.)
    val isRecording: Boolean = false,  // Add this property
    val description: String = "Program Short Description", // Add default description
    val programPoster: String = "Program Poster" // Add default poster
)

data class Channel(
    val id: String,
    val name: String,
    val logo: String,
    val programs: List<Program>
)

// utility functions at the top level
fun timeToFloat(hour: Int, minute: Int): Float {
    return hour + (minute / 60f)
}

fun calculateProgramValues(startHour: Int, startMinute: Int, endHour: Int, endMinute: Int): Pair<Float, Float> {
    val startTimeFloat = timeToFloat(startHour, startMinute)
    val endTimeFloat = timeToFloat(endHour, endMinute)
    
    // Handle cases where program ends next day
    val duration = if (endTimeFloat <= startTimeFloat) {
        (24f - startTimeFloat) + endTimeFloat
    } else {
        endTimeFloat - startTimeFloat
    }
    
    return Pair(startTimeFloat, duration)
}

// Example usage:
fun createProgram(
    title: String, 
    startHour: Int, 
    startMinute: Int, 
    endHour: Int, 
    endMinute: Int,
    isRecording: Boolean = false
): Program {
    val (startTime, duration) = calculateProgramValues(startHour, startMinute, endHour, endMinute)
    return Program(title, startTime, duration, isRecording)
}

// function to create empty program slots
fun createEmptyProgram(startTime: Float, duration: Float): Program {
    return Program("No Program", startTime, duration)
}

// function to fill gaps in program schedule
fun fillProgramGaps(programs: List<Program>): List<Program> {
    val sortedPrograms = programs.sortedBy { it.startTime }
    val filledPrograms = mutableListOf<Program>()
    var currentTime = 0f

    sortedPrograms.forEach { program ->
        // If there's a gap before the program, fill it
        if (program.startTime > currentTime) {
            filledPrograms.add(createEmptyProgram(currentTime, program.startTime - currentTime))
        }
        filledPrograms.add(program)
        currentTime = program.startTime + program.duration
    }

    // Fill gap at the end if needed (up to 24 hours)
    if (currentTime < 24f) {
        filledPrograms.add(createEmptyProgram(currentTime, 24f - currentTime))
    }

    return filledPrograms
}

@SuppressLint("DefaultLocale")
@Composable
fun EPGScreen() {
    // Get current hour and minutes
    val currentHour = remember {
        java.time.LocalTime.now().hour
    }
    val currentMinute = remember {
        java.time.LocalTime.now().minute
    }
    val currentTimeFloat = remember {
        currentHour + (currentMinute / 60f)
    }
    // used if you want to add a top info box
    val clickedPrograms = remember { mutableStateOf<Set<String>>(setOf()) }

    //Program data
    //createProgram("Early Show", 0, 0, 6, 0),
    //Show title
    //Start time 0-1-2-3-4-5..to 24   1 is 1AM 13 is 1PM
    //Start Minutes 0 or 30
    //End Hour 0-1-2-3-4-5..to 24
    //End Minutes 0 or 30
    //Program Description
    //Program Poster

    val channels = remember {
        listOf(
            Channel(
                id = "1",
                name = "Channel 1",
                logo = "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/ESPN%20HD.png",
                programs = fillProgramGaps(listOf(
                    createProgram("Early Show", 0, 0, 6, 0),
                    createProgram("Morning News", 7, 0, 8, 30),
                    createProgram("Talk Show", 9, 0, 10, 30),
                    createProgram("Movie", 11, 0, 13, 30, isRecording = true),
                    createProgram("News", 14, 0, 15, 30),
                    createProgram("Series", 16, 0, 18, 0),
                    createProgram("Evening News", 19, 0, 20, 0),
                    createProgram("Prime Time", 20, 30, 23, 0),
                    createProgram("Late Show", 23, 30, 0, 30),
                ))
            ),
            Channel(
                id = "2",
                name = "Channel 2",
                logo = "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/MTV.png",
                programs = listOf(
                    createProgram("Early Show", 0, 0, 2, 30),
                    createProgram("Morning News", 2, 30, 3, 30),
                    createProgram("Talk Show", 3, 30, 4, 0),
                    createProgram("Movie", 4, 0, 5, 30, isRecording = true),
                    createProgram("News", 5, 30, 16, 0),
                    createProgram("Series", 16, 0, 18, 0),
                    createProgram("Evening News", 18, 0, 20, 0),
                    createProgram("Prime Time", 20, 0, 23, 0),
                    createProgram("Late Show", 23, 30, 0, 30),
                )
            ),
            Channel(
                id = "3",
                name = "Channel 3",
                logo = "https://ott-logos.s3.us-east-1.amazonaws.com/WGNHD.png",
                programs = listOf(
                    createProgram("Early Show", 0, 0, 1, 30),
                    createProgram("Morning News", 1, 30, 3, 30),
                    createProgram("Talk Show", 3, 30, 4, 0),
                    createProgram("Movie", 4, 0, 5, 30, isRecording = true),
                    createProgram("News", 5, 30, 16, 0),
                    createProgram("Series", 16, 0, 18, 0),
                    createProgram("Evening News", 18, 0, 20, 0),
                    createProgram("Prime Time", 20, 0, 23, 0),
                    createProgram("Late Show", 23, 30, 0, 0),
                )
            ),
            Channel(
                id = "4",
                name = "Channel 4",
                logo = "https://ott-logos.s3.us-east-1.amazonaws.com/BBCHD.png",
                programs = listOf(
                    createProgram("Early Show", 0, 0, 1, 0),
                    createProgram("Morning News", 1, 0, 2, 0),
                    createProgram("Talk Show", 2, 0, 3, 0),
                    createProgram("Movie", 3, 0, 4, 0, isRecording = true),
                    createProgram("News", 4, 0, 5, 0),
                    createProgram("Series", 5, 0, 8, 0),
                    createProgram("Evening News", 8, 0, 10, 0),
                    createProgram("Prime Time", 10, 0, 11, 0),
                    createProgram("Late Show", 11, 0, 12, 30),
                    createProgram("Chicago PD", 12, 30, 13, 30),
                    createProgram("DOC", 13, 30, 14, 30),
                    createProgram("STAT", 14, 30, 15, 30),
                    createProgram("CROWN", 15, 30, 16, 0),
                    createProgram("True Crime", 16, 0, 17, 0),
                    createProgram("Paid Programing", 17, 0, 18, 0),
                    createProgram("Local News", 18, 0, 19, 0),
                    createProgram("Show", 19, 0, 20, 0),
                    createProgram("Show", 20, 0, 21, 0),
                    createProgram("Chicago PD", 21, 0, 22, 0),
                    createProgram("Chicago Fire", 22, 0, 23, 0),
                    createProgram("Matlock", 23, 0, 0, 0),
                )
            ),
            Channel(
                id = "5",
                name = "Channel 5",
                logo = "https://ott-logos.s3.us-east-1.amazonaws.com/BETHD.png",
                programs = listOf(
                    createProgram("Early Show", 0, 0, 2, 30),
                    createProgram("Morning News", 2, 30, 3, 30),
                    createProgram("Talk Show", 3, 30, 4, 0),
                    createProgram("Movie", 4, 0, 5, 30, isRecording = true),
                    createProgram("News", 5, 30, 16, 0),
                    createProgram("Series", 16, 0, 18, 0),
                    createProgram("Evening News", 18, 0, 20, 0),
                    createProgram("Prime Time", 20, 0, 23, 0),
                    createProgram("Late Show", 23, 30, 0, 30),
                )
            ),
            Channel(
                id = "6",
                name = "Channel 6",
                logo = "https://github.com/Jasmeet181/mediaportal-us-logos/blob/master/TV/.Light/CHCH%20TV.png?raw=true",
                programs = listOf(
                    createProgram("Early Show", 0, 0, 2, 30),
                    createProgram("Morning News", 2, 30, 3, 30),
                    createProgram("Talk Show", 3, 30, 4, 0),
                    createProgram("Movie", 4, 0, 5, 30, isRecording = true),
                    createProgram("News", 5, 30, 16, 0),
                    createProgram("Series", 16, 0, 18, 0),
                    createProgram("Evening News", 18, 0, 20, 0),
                    createProgram("Prime Time", 20, 0, 23, 0),
                    createProgram("Late Show", 23, 30, 0, 30),
                )
            ),
            Channel(
                id = "7",
                name = "Channel 7",
                logo = "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/Discovery.png",
                programs = fillProgramGaps(listOf(
                    createProgram("Early Show", 0, 0, 2, 30),
                    createProgram("Morning News", 2, 30, 3, 30),
                    createProgram("Talk Show", 3, 30, 4, 0),
                    createProgram("Movie", 4, 0, 5, 30, isRecording = true),
                    createProgram("News", 5, 30, 16, 0),
                    createProgram("Series", 16, 0, 18, 0),
                    createProgram("Evening News", 18, 0, 20, 0),
                    createProgram("Prime Time", 20, 0, 23, 0),
                    createProgram("Late Show", 23, 30, 0, 30),
                ))
            ),
            Channel(
                id = "8",
                name = "Channel 8",
                logo = "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/History%20HD.png",
                programs = fillProgramGaps(listOf(
                    createProgram("Early Show", 0, 0, 2, 30),
                    createProgram("Morning News", 2, 30, 3, 30),
                    createProgram("Talk Show", 3, 30, 4, 0),
                    createProgram("Movie", 4, 0, 5, 30, isRecording = true),
                    createProgram("News", 5, 30, 16, 0),
                    createProgram("Series", 16, 0, 18, 0),
                    createProgram("Evening News", 18, 0, 20, 0),
                    createProgram("Prime Time", 20, 0, 23, 0),
                    createProgram("Late Show", 23, 30, 0, 30),
                ))
            ),
            Channel(
                id = "9",
                name = "Channel 9",
                logo = "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/Discovery%20Life.png",
                programs = fillProgramGaps(listOf(
                    createProgram("Early Show", 0, 0, 2, 30),
                    createProgram("Morning News", 2, 30, 3, 30),
                    createProgram("Talk Show", 3, 30, 4, 0),
                    createProgram("Movie", 4, 0, 5, 30, isRecording = true),
                    createProgram("News", 5, 30, 16, 0),
                    createProgram("Series", 16, 0, 18, 0),
                    createProgram("Evening News", 18, 0, 20, 0),
                    createProgram("Prime Time", 20, 0, 23, 0),
                    createProgram("Late Show", 23, 30, 0, 30),
                ))
            )
        )
    }

    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberScrollState()

    // Calculate initial scroll position (current time)
    LaunchedEffect(Unit) {
        horizontalScrollState.scrollTo((currentHour * 100 * 2).toInt()) // 100dp per half hour
    }

    // Calculate the last program end time
    val lastProgramEndHour = remember(channels) {
        channels.maxOf { channel ->
            channel.programs.maxOf { program ->
                program.startTime + program.duration
            }
        }
    }

    // Modify time slots to show half hours
    val timeSlots = remember(lastProgramEndHour) {
        (0..(lastProgramEndHour * 2).toInt()).map { halfHour ->
            val hour = halfHour / 2f
            TimeSlot(
                time = String.format("%02d:%02d", hour.toInt(), (hour % 1 * 60).toInt()),
                startHour = hour
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Fixed channel column with vertical scroll
        Box(
            modifier = Modifier
                .width(100.dp)
                .fillMaxHeight()
        ) {
            // Fixed header space
            Spacer(
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth()
                    .background(Color.Black)
            )
            
            // Scrollable channel names
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 50.dp)  // Same as header space
                    .verticalScroll(verticalScrollState)  // Use same scroll state as program grid
                    .background(Color.Black)
                    .border(
                        width = 1.dp,
                        color = Color.DarkGray,
                        shape = RoundedCornerShape(0.dp)
                    )
            ) {
                channels.forEach { channel ->
                    Box(
                        modifier = Modifier
                            .height(80.dp)
                            .fillMaxWidth()
                            .border(1.dp, Color.DarkGray),
                        contentAlignment = Alignment.Center
                    ) {
                        if(channel.logo.isNotEmpty()){
                            AsyncImage(
                                model = channel.logo,
                                contentDescription = "image",
                                contentScale = ContentScale.Inside,
                                modifier = Modifier.width(60.dp)
                            )
                        }else{
                            Text(
                                text = channel.name,
                                color = Color.White
                            )
                        }

                    }
                }
            }
        }

        // Content area with fixed time header
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 100.dp) // Offset for channel column
        ) {
            // Fixed time header
            Row(
                modifier = Modifier
                    .height(50.dp)
                    .horizontalScroll(horizontalScrollState)
                    .background(Color.Black)
            ) {
                timeSlots.forEach { slot ->
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .fillMaxHeight()
                            .background(Color.Black)
                            .border(
                                width = 1.dp,
                                color = Color.DarkGray,
                                shape = RoundedCornerShape(bottomStart = 2.dp, bottomEnd = 2.dp)
                            )
                    ) {
                        // Time text at the top
                        Text(
                            text = slot.time,
                            color = Color.White,
                            modifier = Modifier.align(Alignment.TopCenter).padding(top = 4.dp)
                        )
                        
                        // Current time indicator at the bottom
                        if (currentTimeFloat >= slot.startHour && currentTimeFloat < slot.startHour + 0.5f) {
                            val offsetPercentage = (currentMinute % 30) / 30f
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 2.dp)
                                    .offset(x = (offsetPercentage * 100).dp)
                            ) {
                                // Vertical line
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(12.dp)
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                                // Arrow
                                Icon(
                                    imageVector = Icons.Filled.ArrowDropUp,
                                    contentDescription = "Current time",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .offset(y = (-6).dp)
                                )
                            }
                        }
                    }
                }
            }

            // vertical line that spans the entire EPG
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(2.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                    .offset(x = (((currentMinute % 30) / 30f) * 100).dp)
            )

            // Scrollable program grid
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 50.dp) // Offset for time header
            ) {
                Column(
                    modifier = Modifier
                        .horizontalScroll(horizontalScrollState)
                        .verticalScroll(verticalScrollState)
                        .width(100.dp * (lastProgramEndHour * 2))
                ) {
                    // Programs grid
                    channels.forEach { channel ->
                        Row(
                            modifier = Modifier
                                .height(80.dp)
                        ) {
                            channel.programs.forEach { program ->
                                val programEndTime = program.startTime + program.duration
                                val isPastProgram = programEndTime < currentHour
                                val isCurrentProgram = program.startTime <= currentHour && programEndTime > currentHour
                                var isFocused by remember { mutableStateOf(false) }
                                var isClicked by remember { mutableStateOf(false) }
                                Box(
                                    modifier = Modifier
                                        .width(100.dp * (program.duration * 2))
                                        .fillMaxHeight()
                                        .padding(1.dp)
                                        .onFocusChanged {
                                            isFocused = it.isFocused
                                            if(!it.isFocused){
                                                isClicked = false
                                            }
                                        }
                                        .border( width = 1.dp, color =  if (isFocused || isClicked) Color.White else Color.Transparent )
                                        .focusable()
                                        .background(
                                            when {
                                                program.title == "No Program" -> Color.DarkGray.copy(alpha = 0.3f)
                                                isPastProgram -> Color.DarkGray.copy(alpha = 0.7f)
                                                isCurrentProgram -> MaterialTheme.colorScheme.secondary
                                                else -> MaterialTheme.colorScheme.primary
                                            },
                                            RoundedCornerShape(4.dp)
                                        )
                                        .clickable {
                                            isClicked = true
                                            val programId = "${channel.id}_${program.title}_${program.startTime}"
                                            if (clickedPrograms.value.contains(programId)) {
                                                // Second click - navigate to player
                                               // navController.navigate("player/${channel.id}/${program.startTime}")
                                            } else {
                                                // First click - show details
                                                clickedPrograms.value += programId
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = if(isClicked) Arrangement.Start else Arrangement.SpaceBetween
                                    ) {
                                        if(isClicked){
                                            Image(
                                                painterResource(R.drawable.default_card),
                                                contentDescription = "",
                                                contentScale = ContentScale.Fit,
                                                modifier = Modifier
                                                    .width(60.dp)
                                                    .fillMaxHeight()
                                            )

                                            Spacer(modifier = Modifier.width(8.dp))

                                            Text(
                                                text = "Program Short Description",
                                                color = Color.White,
                                                textAlign = TextAlign.Start,
                                                overflow = TextOverflow.Ellipsis,
                                                maxLines = 1,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }else {
                                            // Program title and time
                                            Text(
                                                text = if (program.title == "No Program") {
                                                    "No Program"
                                                } else {
                                                    "${program.title} (${
                                                        String.format(
                                                            "%02d:%02d",
                                                            program.startTime.toInt(),
                                                            (program.startTime % 1 * 60).toInt()
                                                        )
                                                    }-${
                                                        String.format(
                                                            "%02d:%02d",
                                                            (program.startTime + program.duration).toInt(),
                                                            ((program.startTime + program.duration) % 1 * 60).toInt()
                                                        )
                                                    })"
                                                },
                                                color = when {
                                                    program.title == "No Program" -> Color.Gray.copy(
                                                        alpha = 0.7f
                                                    )

                                                    isPastProgram -> Color.Gray
                                                    else -> Color.White
                                                },
                                                textAlign = TextAlign.Start,
                                                overflow = TextOverflow.Ellipsis,
                                                maxLines = 1,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .padding(end = if (program.isRecording || isPastProgram) 8.dp else 0.dp)
                                            )

                                            // Icons in a separate Row
                                            if (program.title != "No Program") {
                                                Row(
                                                    horizontalArrangement = Arrangement.End,
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(start = 4.dp)
                                                ) {
                                                    if (isPastProgram) {
                                                        Icon(
                                                            imageVector = Icons.Filled.Refresh,
                                                            contentDescription = "Restart program",
                                                            modifier = Modifier.size(20.dp),
                                                            tint = Color.White
                                                        )
                                                    }

                                                    if (program.isRecording) {
                                                        if (isPastProgram) {
                                                            Spacer(modifier = Modifier.width(4.dp))
                                                        }
                                                        Icon(
                                                            imageVector = Icons.Filled.FiberManualRecord,
                                                            contentDescription = "Recording",
                                                            modifier = Modifier.size(20.dp),
                                                            tint = Color.Red.copy(alpha = 0.9f)
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
}

@Preview(
    showBackground = true,
    widthDp = 1280,
    heightDp = 720
)
@Composable
fun EPGScreenPreview() {
    EPGScreen()
} 