package com.example.composeepg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.ui.Modifier
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import com.example.composeepg.screens.EpgLayoutContent
import com.example.composeepg.ui.theme.ComposeEpgTheme
import com.kevinnzou.compose.progressindicator.BuildConfig
import timber.log.Timber.*
import timber.log.Timber.Forest.plant


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalTvMaterial3Api::class)
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
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    EpgLayoutContent()
                }
            }
        }
    }
}
