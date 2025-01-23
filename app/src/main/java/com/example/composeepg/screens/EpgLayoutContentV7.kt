package com.example.composeepg.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.composeepg.screens.libtvguide.App

/**
 * libtv testing screen
 */
@Composable
fun  EpgLayoutContentV7(){
    Box(
        modifier = Modifier
            .padding(top = 250.dp)
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.DarkGray)
    ) {

        App()

    }
}