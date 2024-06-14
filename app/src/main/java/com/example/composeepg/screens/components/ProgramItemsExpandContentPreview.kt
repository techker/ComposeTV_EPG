package com.example.composeepg.screens.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.composeepg.R
import com.example.composeepg.data.ProgramRowItems
import com.example.composeepg.screens.components.dialogs.ProgramDetailsPopup
import com.example.composeepg.screens.components.dialogs.RecordDialogContent

@Composable
fun ProgramItemsExpandContentPreview (
    program: ProgramRowItems,
    cellHeight: Dp,
    onFocusChange: (Boolean) -> Unit,
    focusRequester: FocusRequester,
    positionX: Dp,
    i: Int,
    showInfoPop: Boolean,
) {
    val borderWidth = 1.dp
    var showDialog by remember { mutableStateOf(false) }
    var expand by remember { mutableStateOf(false) }
    val bgwColor = Color.Black
    var isFocused by remember { mutableStateOf(false) }
    var focusedProgram by remember { mutableStateOf<ProgramRowItems?>(null) }
    var showPopup by remember { mutableStateOf(false) }
    val borderColor = if (isFocused) Color.White else Color.Transparent // Change to your preferred colors

    val cellHeightVisible = if(expand) 100.dp else { cellHeight}
    Row(
        modifier = Modifier
            .height(cellHeightVisible)
            .width(positionX)
            .padding(start = 5.dp,bottom = 5.dp, end = 2.dp)
            .clip(shape = RoundedCornerShape(15.dp, 15.dp, 15.dp, 15.dp))
            .border(2.dp, borderColor, RoundedCornerShape(15.dp))
            .background(bgwColor)
            .onFocusChanged { focusState ->
                onFocusChange(focusState.isFocused)
                isFocused = focusState.isFocused
                focusedProgram = program
                showPopup = isFocused
                expand = focusState.isFocused
            }
            .focusRequester(focusRequester)
            .clickable(onClick = {
                showPopup = false
            })
            .focusable(true)
    ) {
        Column(
            modifier = Modifier
                .padding(top = 10.dp)
        ) {
            if(!expand){
                Text(
                    text = program.programName,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth().padding(start = 10.dp),
                )


                Row(modifier = Modifier.height(20.dp)) {
                    Image(
                        painterResource(R.drawable.reset),
                        contentDescription = "",
                        modifier = Modifier
                            .width(30.dp)
                            .padding(start = 20.dp, top = 5.dp)
                    )
                    Image(
                        painterResource(R.drawable.ic_record),
                        contentDescription = "",
                        modifier = Modifier
                            .width(30.dp)
                            .padding(start = 20.dp, top = 5.dp)
                    )
                }
            }else{
                Row(modifier = Modifier) {
                    if (program.programImage.isNotEmpty()) {
                        AsyncImage(
                            model = program.programImage,
                            contentDescription = "",
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .align(Alignment.CenterVertically)
                                .padding(5.dp),
                            contentScale = ContentScale.Inside
                        )
                    }
                    Text(
                        text = program.programDescription,
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth().padding(start = 10.dp)
                            .widthIn(0.dp, positionX),
                    )

                }
                Text(
                    text = program.quality,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth().padding(start = 10.dp)
                )
            }
        }
    }
}