package com.example.composeepg.screens.libtvguide.data

import com.example.composeepg.jctvguide.data.Event


data class Channel(val id: Int, val title: String, val icon: String, var events: List<Event> = emptyList())