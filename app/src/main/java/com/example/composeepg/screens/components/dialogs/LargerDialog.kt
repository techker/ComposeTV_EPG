package com.example.composeepg.screens.components.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.composeepg.R

@Composable
fun LargerDialog(onCardClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .size(400.dp, 100.dp)
                .clickable(onClick = onCardClicked),

            colors = CardDefaults.cardColors(
                containerColor = Color.DarkGray, //Card background color
                contentColor = Color.White  //Card content color,e.g.text
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painterResource(R.drawable.baseline_favorite_24),
                    contentDescription = "",
                    modifier = Modifier
                        .padding(start = 40.dp)
                        .focusable(true)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painterResource(R.drawable.baseline_favorite_24),
                    contentDescription = "",
                    modifier = Modifier
                        .padding(start = 40.dp)
                        .focusable(true)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painterResource(R.drawable.baseline_favorite_24),
                    contentDescription = "",
                    modifier = Modifier
                        .padding(start = 40.dp)
                        .focusable(true)
                )
                Spacer(modifier = Modifier.width(8.dp))
                ClickableImage(
                    resourceId = R.drawable.baseline_favorite_24,
                    onClick = { /* Handle click event for this image */ }
                )
            }
        }
    }
}

@Composable
fun ClickableImage(resourceId: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painterResource(resourceId),
            contentDescription = "",
            modifier = Modifier
                .fillMaxSize()
                .focusable(true)
        )
    }
}
