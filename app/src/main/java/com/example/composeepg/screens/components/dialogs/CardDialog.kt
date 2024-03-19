package com.example.composeepg.screens.components.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.composeepg.R


@Composable
fun CardDialog(onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            modifier = Modifier
                .width(60.dp)
                .height(60.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.DarkGray,
                contentColor = Color.White
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .clickable { onDismiss() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                ClickableImage(
                    resourceId = R.drawable.baseline_favorite_24,
                    onClick = { onDismiss() }
                )
            }
        }
    }
}