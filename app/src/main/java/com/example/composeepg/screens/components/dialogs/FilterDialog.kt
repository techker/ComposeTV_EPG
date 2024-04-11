package com.example.composeepg.screens.components.dialogs

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.composeepg.R

@Composable
fun FilterDialog(onCardClicked: (String) -> Unit,onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.height(100.dp).width(350.dp),
            colors = CardDefaults.cardColors(containerColor = Color.DarkGray, contentColor = Color.White)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ClickableImage(
                    resourceId = R.drawable.baseline_favorite_24_white,
                    onClick = { onCardClicked("Favorites") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                ClickableImage(
                    resourceId = R.drawable.baseline_hd_24,
                    onClick = { onCardClicked("HD") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                ClickableImage(
                    resourceId = R.drawable.baseline_4k_24,
                    onClick = { onCardClicked("4K") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                ClickableImage(
                    resourceId = R.drawable.baseline_music_video_24,
                    onClick = { onCardClicked("Music") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                ClickableImage(
                    resourceId = R.drawable.baseline_reset_filter_24,
                    onClick = { onCardClicked("reset") }
                )
            }
        }
    }
}
@Composable
@Preview(device = Devices.TV_1080p)
fun FilterDialogPreview() {
    FilterDialog({},{})
}