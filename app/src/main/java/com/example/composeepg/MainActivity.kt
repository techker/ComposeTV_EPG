package com.example.composeepg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.composeepg.screens.EPGScreen
import com.example.composeepg.ui.theme.ComposeEpgTheme
import com.kevinnzou.compose.progressindicator.BuildConfig
import timber.log.Timber.DebugTree
import timber.log.Timber.Forest.plant


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.DEBUG) {
            plant(DebugTree())
        }
        enableEdgeToEdge()
        setContent {
            ComposeEpgTheme {
                Box(
                    modifier = Modifier
                        .wrapContentSize()
                        .background(Color.DarkGray)

                ) {
                    //EpgLayoutContentV6()
                    EPGScreen()
                    /**
                     *
                     * V1  EpgLayoutContentV1()
                     * V2  EpgLayoutContentV2()
                     * V3  EpgLayoutContentV3()
                     * V4 EpgLayoutContentV4() Test
                     * V5 EpgLayoutContentV5(viewModel,false,true) : Set one true or false ShowPopup Info or bottom info on guide
                     * V6 EpgLayoutContentV6()
                     * V7 EpgLayoutContentV7() LibtvGuide test from Github
                     * V8 EPGScreen() New version
                     */

                }
            }
        }
    }
}
