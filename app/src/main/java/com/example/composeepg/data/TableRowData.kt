package com.example.composeepg.data

data class ProgramRowItems(
    val programID: Int,
    val programName: String,
    val programImage: String,
    val programDescription:String,
    val programStart: String,
    val programEnd: String,
    val channelId: Int,
    val isRecording: Boolean,
    val isLookBack: Boolean,
    val isLocked: Boolean,
    val genre:String,
    val quality:String,
    val isAdult:Boolean
)

data class ChannelRowItems(
    val channelID: Int,
    val channelName: String,
    val channelLogo: String,
    val isFavorite: Boolean,
    val isLocked: Boolean,
    val isAdult: Boolean,
    val quality:String,
    val genre:String,
    val isSubscribed:Boolean,
    val channelNumber:Int
)

data class CategoriesItems(
    val categoryId:Int,
    val categoryName:String,
)