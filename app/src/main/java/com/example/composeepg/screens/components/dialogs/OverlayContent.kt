package com.example.composeepg.screens.components.dialogs

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.ProvideTextStyle
import coil.compose.AsyncImage
import com.example.composeepg.R
import com.example.composeepg.data.MockData
import com.example.composeepg.data.handleDPadKeyEvents
import com.example.composeepg.screens.components.BottomTextChip
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalComposeUiApi::class, ExperimentalTvMaterial3Api::class)
@Composable
fun OverlayContent(onDismissRequest: () -> Unit) {
    val (settingsFocusRequester, appsFocusRequester, recordingsFocusRequester, onDemandFocusRequester) = remember { FocusRequester.createRefs() }
    val currentTime = LocalTime.now()
    val formatter = DateTimeFormatter.ofPattern("H:mm") // Format as "hour:minute"
    val focusRequesterOverlay = remember { FocusRequester() }
    val clock = currentTime.format(formatter)
    var isExpanded by remember { mutableStateOf(false) }
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    val channelList = MockData().createChannels().take(5)
    Box(
        modifier = Modifier
            .focusRequester(focusRequesterOverlay)
            .fillMaxSize()
            .focusTarget()
            .onGloballyPositioned {
                focusRequesterOverlay.requestFocus()
            }
            .handleDPadKeyEvents(
                onDown = {
                    Log.d("TAG", "OverlayContent handleDPadKeyEvents onDown")
                    isExpanded = true
                },
                onUp = {
                    Log.d("TAG", "OverlayContent handleDPadKeyEvents onDown")
                    isExpanded = false
                    settingsFocusRequester.requestFocus()
                }
            )
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(width = screenWidth, height = if (isExpanded) 400.dp else 170.dp)
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.DarkGray.copy(alpha = 0.9f),
                            Color.DarkGray
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Box(
                contentAlignment = Alignment.TopEnd,
                modifier = Modifier.fillMaxWidth()
            ){
                Text(
                    modifier = Modifier.padding(end = 80.dp),
                    text = "20º",
                    textAlign = TextAlign.Right,
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color =  Color.White
                )
                Text(
                    modifier = Modifier.padding(end = 10.dp),
                    text = clock,
                    textAlign = TextAlign.Right,
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color =  Color.White
                )
            }
            Row(modifier = Modifier.padding(top = 30.dp)) {
                Image(
                    painterResource(R.drawable.baseline_search_48),
                    contentDescription = "",
                    modifier = Modifier
                        .focusable(true)
                        .size(60.dp)
                        .padding(start = 20.dp)
                )
                FocusableTextButtonD("Settings", onDismissRequest,settingsFocusRequester)
                FocusableTextButtonD("Apps", onDismissRequest,appsFocusRequester)
                FocusableTextButtonD("Recordings", onDismissRequest,recordingsFocusRequester)
                FocusableTextButtonD("OnDemand", onDismissRequest,onDemandFocusRequester)
            }
        }
        if(isExpanded){
            ProvideTextStyle(value = MaterialTheme.typography.labelMedium) {
                androidx.tv.material3.Text(text = "Recent Channels",modifier = Modifier.padding(top = 110.dp, start = 20.dp), color = Color.White)
            }
            LazyRow(modifier = Modifier.padding(top = 120.dp, start = 30.dp)) {
                itemsIndexed(items = channelList) { index, item ->
                    AsyncImage(
                        model = item.channelLogo,
                        contentDescription = "",
                        modifier = Modifier
                            .size(75.dp)
                            .focusable()
                            .padding(5.dp),
                        contentScale = ContentScale.Inside
                    )
                }
            }
            ProvideTextStyle(value = MaterialTheme.typography.labelMedium) {
                androidx.tv.material3.Text(text = "Recent Movies",modifier = Modifier.padding(top = 200.dp, start = 20.dp), color = Color.White)
            }
            LazyRow(modifier = Modifier.padding(top = 210.dp, start = 30.dp)) {
                itemsIndexed(items = channelList) { index, item ->
                    AsyncImage(
                        model = item.channelLogo,
                        contentDescription = "",
                        modifier = Modifier
                            .size(75.dp)
                            .focusable()
                            .padding(5.dp),
                        contentScale = ContentScale.Inside
                    )
                }
            }
        }
        LaunchedEffect(Unit) {
            settingsFocusRequester.requestFocus()
        }
    }

}
@Composable
fun FocusableTextButtonD(text: String, onClick: () -> Unit, focusRequester: FocusRequester) {
    val isFocused = remember { mutableStateOf(false) }
    BottomTextChip(
    label = text,
    isChecked = false,
    onCheckedChange = { onClick() },
        modifier = Modifier.onFocusChanged {
            isFocused.value = it.hasFocus
    },focusRequester)

}
@Composable
@Preview(device = Devices.TV_1080p)
fun OverlayContentPreview() {
    OverlayContent {

    }
}