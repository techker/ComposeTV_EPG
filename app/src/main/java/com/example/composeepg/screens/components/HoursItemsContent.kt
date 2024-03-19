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

@Composable
fun HoursItemsContent (hour: String, index: Int, mainViewModel: MainViewModel, hoursIndex: Int) {
    val density = LocalDensity.current
    var now = false
    var offsetFloat =0f
    val currentTime = LocalTime.now()
    val minuteOffset = currentTime.minute
    val hourSlotWidthDp = 80.dp // Assuming each hour slot is 120.dp wide

    // Use the density to convert DP to pixel values
    val slotWidthPx = with(density) { hourSlotWidthDp.toPx() }
    val strokeWidthPx = with(density) { 2.dp.toPx() }
    // Calculate the fraction of the hour that has passed
    // Calculate the x position of the line in pixels
    val proportionOfHourPassed = minuteOffset / 60f
    val linePosition = slotWidthPx * proportionOfHourPassed

    val textHeightApprox = with(density) { 20.dp.toPx() }
    val linePositionY =80 - textHeightApprox + with(density) { 4.dp.toPx() } // Adjust the 4.dp offset as needed

    if (index == hoursIndex) { now = true }
    if (index == 0) {
        // Applying a negative offset to the first hour item
        Text(
            text =if(index == hoursIndex) "ON NOW" else hour,
            modifier = Modifier
                .offset(x = (-30).dp)
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
                    offsetFloat =positionInRoot.x - 80
                },
            // Testing to draw a line with time elapsed
//                .drawBehind {
//                    if (now) {
//                        drawLine(
//                        color = Color.Blue,
//                        start = Offset(x = offsetFloat, y = linePositionY),
//                        end = Offset(x = linePosition, y = linePositionY),
//                        strokeWidth = strokeWidthPx,
//                        )
//                    }
//                },
            color = Color.White
        )
    }
}