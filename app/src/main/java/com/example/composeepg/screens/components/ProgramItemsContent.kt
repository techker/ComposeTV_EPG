package com.example.composeepg.screens.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.composeepg.R
import com.example.composeepg.data.ProgramRowItems

import com.example.composeepg.screens.components.dialogs.RecordDialogContent

@Composable
fun ProgramItemsContent(
    program: ProgramRowItems,
    cellHeight: Dp,
    onFocusChange: (Boolean) -> Unit,
    focusRequester: FocusRequester,
    positionX: Dp,
    i: Int
) {
    val borderWidth = 1.dp
    var showDialog by remember { mutableStateOf(false) }
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
            .focusRequester(if (i == 0) focusRequester else FocusRequester()) // Apply the focusRequester only to the first item

            .clickable(onClick = {
                Log.d("TAG","CLICKED PRG ")
                showDialog = true
            })
            .focusable(true),
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
    /**
     * Recording Dialog
     */
    RecordDialogContent(
        showDialog = showDialog,
        program,
        onDismiss = { showDialog = false },
        onConfirm = {
            Log.d("TAG","Confirmed Recording")
            showDialog = false
        }
    )
}
