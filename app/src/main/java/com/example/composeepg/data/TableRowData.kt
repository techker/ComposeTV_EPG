package com.example.composeepg.data

data class ProgramRowItems(
    val programID: Int,
    val programName: String,
    val programImage: String,
    val programStart: String,
    val programEnd: String,
    val channelId: Int,
    val isRecording: Boolean,
    val isLookBack: Boolean,
    val isLocked: Boolean,
)

data class ChannelRowItems(
    val channelID: Int,
    val channelName: String,
    val channelLogo: String,
    val isFavorite: Boolean,
    val isLocked: Boolean,
    val isAdult: Boolean,
)