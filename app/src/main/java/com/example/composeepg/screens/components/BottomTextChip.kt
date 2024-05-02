package com.example.composeepg.screens.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.FilterChip
import androidx.tv.material3.FilterChipDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.ProvideTextStyle
import androidx.tv.material3.Text
import com.example.composeepg.ui.theme.JetStreamCardShape

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun BottomTextChip(
    label: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester
) {
    var isFocused by remember { mutableStateOf(false) }
    FilterChip(
        modifier = modifier
            .padding(start = 25.dp,top = 10.dp)
            .focusRequester(focusRequester)
            .onFocusChanged {
                isFocused = it.isFocused || it.hasFocus
            }
            .then(
                if (isFocused)
                    Modifier.border(
                        ChipFocusedBorder.border.width,
                        ChipFocusedBorder.border.brush,
                        ChipFocusedBorder.shape
                    )
                else
                    Modifier
            ),
        onClick = { onCheckedChange(!isChecked) },
        selected = isChecked,
        leadingIcon = {
            AnimatedVisibility(visible = isChecked) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "",
                    modifier = Modifier.size(16.dp)
                )
            }
        },
        shape = FilterChipDefaults.shape(shape = JetStreamCardShape),
        scale = FilterChipDefaults.scale(focusedScale = 1f),
        colors = FilterChipDefaults.colors(
            focusedContainerColor = ChipColor,
            selectedContainerColor = ChipColor,
            focusedSelectedContainerColor = ChipColor,
            focusedContentColor = ChipContentColor,
            focusedSelectedContentColor = ChipContentColor
        )
    ) {
        ProvideTextStyle(value = MaterialTheme.typography.bodyMedium) {
            Text(text = label,color = Color.White)
        }
    }
}

private val ChipColor @Composable get() = Color.White.copy(alpha = 0.1f)

@OptIn(ExperimentalTvMaterial3Api::class)
private val ChipContentColor @Composable get() = MaterialTheme.colorScheme.inverseSurface

@OptIn(ExperimentalTvMaterial3Api::class)
private val ChipFocusedBorder
    @Composable get() = Border(
        border = BorderStroke(
            width = 1.5.dp,
            color = MaterialTheme.colorScheme.onSurface,
        ), shape = JetStreamCardShape
    )
