package com.example.composeepg.screens.components

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.composeepg.view.MainViewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun HoursItemsContent (hour: String, index: Int, mainViewModel: MainViewModel, hoursIndex: Int) {
    val density = LocalDensity.current
    var now = false
    var offsetFloat =0f
    val currentTime = LocalTime.now()
    val minuteOffset = currentTime.minute
    val hourSlotWidthDp = 80.dp // Assuming each hour slot is 120.dp wide

    val fullList = mainViewModel.getHoursFullList()
    val timeFormat = DateTimeFormatter.ofPattern("H:mm")
    val segmentWidthDp = 60.dp // Width of each 30-minute segment
    val currentTimeIndex = getCurrentTimeIndex(currentTime, fullList, timeFormat)
    val offsetDp = (currentTimeIndex * segmentWidthDp.value).dp
    mainViewModel.offsetHours = offsetDp

//    val totalWidthPx = with(LocalDensity.current) { fullList.size * segmentWidthDp.toPx() }
//    val currentTimePx = getCurrentTimePositionPx(currentTime, totalWidthPx, fullList.size)
//    // Use the density to convert DP to pixel values
//    val slotWidthPx = with(density) { hourSlotWidthDp.toPx() }
//    // Calculate the x position of the line in pixels
//    val proportionOfHourPassed = minuteOffset / 60f
//    val linePosition = slotWidthPx * proportionOfHourPassed
//    val textHeightApprox = with(density) { 20.dp.toPx() }
//    val linePositionY =80 - textHeightApprox + with(density) { 4.dp.toPx() } // Adjust the 4.dp offset as needed

    if (index == hoursIndex) { now = true }
    if (index == 0) {
        // Applying a negative offset to the first hour item
        Text(
            text =if(index == hoursIndex) "ON NOW" else hour,
            modifier = Modifier
                .offset(x = (-30).dp)
//                Was testing to Draw a line under Now time to current Minutes
//                .drawBehind {
//                    if (now) {
//                        val yPosition = size.height / 2
//                        drawLine(
//                            color = Color.Blue,
//                            start = Offset(x = currentTimePxFinal, y = 0F),
//                            end = Offset(x = currentTimeIndexFinal , y = 0F), // Extend the line 10px to the right
//                            strokeWidth = strokeWidthPx,
//                        )
//                    }
//                    Log.d("TAG","HoursItemsContent currentTimePxFinal $currentTimePxFinal currentTimeIndexFinal $currentTimeIndexFinal linePosition $linePosition strokeWidthPx $strokeWidthPx currentTimeIndex $currentTimeIndex offsetDp $offsetDp currentTimePx $currentTimePx totalWidthPx $totalWidthPx")
//                }
                .onGloballyPositioned { layoutCoordinates ->
                    val positionInRoot = layoutCoordinates.positionInRoot()
                    if (now) {
                        mainViewModel.timeNowPosition = positionInRoot.x - 60
                    }
                    mainViewModel.startTimePositions[hour] = positionInRoot.x
                }
                .padding(horizontal = 50.dp, vertical = 4.dp),
            color =  Color.White
        )

    } else {
        Text(
            text = if(index == hoursIndex) "ON NOW" else hour,
            modifier = Modifier
                .padding(horizontal = 80.dp, vertical = 4.dp)
                .onGloballyPositioned { layoutCoordinates ->
                    val positionInRoot = layoutCoordinates.positionInRoot()
                    if (now) {
                        mainViewModel.timeNowPosition = positionInRoot.x - 60
                        mainViewModel.isPositionSet.postValue(true)
                    }
                    mainViewModel.startTimePositions[hour] = positionInRoot.x - 60
                    offsetFloat = positionInRoot.x - 80
                },
            color = Color.White
        )
    }

}

fun getCurrentTimePositionPx(currentTime: LocalTime, totalWidthPx: Float, numSegments: Int): Float {
    val minutesSinceMidnight = currentTime.hour * 60 + currentTime.minute
    val minutesPerDay = 24 * 60
    return totalWidthPx * (minutesSinceMidnight / minutesPerDay.toFloat())
}
fun getCurrentTimeIndex(currentTime: LocalTime, hourSegments: List<String>, timeFormat: DateTimeFormatter): Float {
    val currentIndex = currentTime.hour * 2 + if (currentTime.minute >= 30) 1 else 0
    return currentIndex + (currentTime.minute % 30) / 30f // Calculates the offset within the 30-minute segment
}