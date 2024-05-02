package com.example.composeepg.screens.components.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.example.composeepg.R
import com.example.composeepg.data.ProgramRowItems
import com.example.composeepg.screens.components.EpgFilterChip

@Composable
fun ProgramDetailsPopup(program: ProgramRowItems) {
    Popup(alignment = Alignment.TopStart) {

        /**
         * Pop up Dialog Program info
         * Customize this container as needed
         */
        Card(
            elevation = 10.dp,
            backgroundColor = Color.White,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.padding(16.dp).widthIn(max = 400.dp)
        ) {
            Row(modifier = Modifier.padding(8.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.default_card),
                    contentDescription = "Program Image",
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.CenterVertically)
                )
                Column(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    val convertedStartHours = program.programStart.replace(".", ":")
                    val convertedEndHours = program.programEnd.replace(".", ":")
                    Text(text = program.programName)
                    Text(text = "Start Time: $convertedStartHours to $convertedEndHours", style = MaterialTheme.typography.labelSmall)
                    Text(
                        text = program.programDescription,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    if(program.isRecording){
                        EpgFilterChip(
                            label = "Manage Recording",
                            isChecked = false,
                            onCheckedChange = {
                                //Do something
                            }, modifier = Modifier.padding(top = 10.dp).onFocusChanged {
                                if(it.hasFocus){
                                    //Do something
                                }
                            })
                    }
                }
            }
        }
    }
}

@Composable
@Preview(device = Devices.TV_1080p)
fun ProgramDetailsPopupPreview() {
    val pop =ProgramRowItems(programID = 1, programName = "Program 1A", "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png", programDescription = "Geralt of Rivia, a solitary monster hunter, struggles to find his place in a world where people often prove more wicked than beasts.", programStart = "1.00", programEnd = "2.00", channelId = 1,true,true,false,"Sports","4K",false)
    ProgramDetailsPopup(pop)
}