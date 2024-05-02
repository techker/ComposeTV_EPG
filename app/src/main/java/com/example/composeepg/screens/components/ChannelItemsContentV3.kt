package com.example.composeepg.screens.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.composeepg.R
import com.example.composeepg.data.ChannelRowItems

@Composable
fun ChannelItemsContentV3(item: ChannelRowItems, index: Int) {
    val bgwColor = Color.Black
    Row(
        modifier = Modifier
            .width(120.dp)
            .padding(start = 5.dp, bottom = 5.dp,end=10.dp )
            .clip(shape = RoundedCornerShape(15.dp, 15.dp, 15.dp, 15.dp))
            .background(bgwColor)
            .padding(4.dp)
    ) {
        if (item.channelLogo.isNotEmpty()) {
            AsyncImage(
                model = item.channelLogo,
                contentDescription = "",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterVertically)
                    .padding(5.dp),
                contentScale = ContentScale.Inside
            )
        } else {
            Text(text = item.channelName)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = item.channelNumber.toString(),
            modifier = Modifier.padding(0.dp, 15.dp, 0.dp, 5.dp),
            color = Color.White
        )
        if (item.isLocked) {
            Image(
                painter = painterResource(R.drawable.baseline_lock_outline_24),
                contentDescription = "",
                modifier = Modifier
                    .padding(start = 5.dp, top = 10.dp)
                    .size(25.dp)
            )
        }
    }
}

@Composable
@Preview(device = Devices.TV_1080p)
fun ChannelItemsContentV3Preview() {
    val item = ChannelRowItems(channelID = 1, channelName = "ESPN", channelLogo = "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/ESPN%20HD.png",true,false,false,"HD","Sports",true,1)
    ChannelItemsContentV3(item,1)
}