package com.example.composeepg.data

import kotlinx.coroutines.flow.flow

class MockData {

    fun createChannels():MutableList<ChannelRowItems> {
        return mutableListOf(
            ChannelRowItems(channelID = 1, channelName = "ESPN", channelLogo = "https://ott-logos.s3.us-east-1.amazonaws.com/ESPNH.png",true,false,false),
            ChannelRowItems(channelID = 2, channelName = "MTV", channelLogo = "https://ott-logos.s3.us-east-1.amazonaws.com/MTVH.png",false,false,false),
            ChannelRowItems(channelID = 3, channelName = "WGN", channelLogo = "https://ott-logos.s3.us-east-1.amazonaws.com/WGNHD.png",false,false,false),
            ChannelRowItems(channelID = 4, channelName = "BET", channelLogo = "https://ott-logos.s3.us-east-1.amazonaws.com/BETHD.png",false,false,false),
            ChannelRowItems(channelID = 5, channelName = "BBC", channelLogo = "https://ott-logos.s3.us-east-1.amazonaws.com/BBCHD.png",false,false,true),
            ChannelRowItems(channelID = 6, channelName = "ABC", channelLogo = "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",false,false,false),
            ChannelRowItems(channelID = 7, channelName = "Discovery", channelLogo = "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",false,false,false),
            ChannelRowItems(channelID = 8, channelName = "History", channelLogo = "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",false,false,false),
            ChannelRowItems(channelID = 9, channelName = "Velocity", channelLogo = "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",false,true,false),
            ChannelRowItems(channelID = 10, channelName = "Teletoon", channelLogo = "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",false,false,false)
        )
    }

    fun createChannelsFlow()= flow {
         emit(createChannels())
    }
    fun createProgramsFlow()= flow {
        emit(createPrograms())
    }
    fun getAllProgramsForChannel(channelId:Int):MutableList<ProgramRowItems> {
        return createPrograms().filter { it.channelId == channelId }.toMutableList()
    }
    fun getChannelData(channelId:Int):ChannelRowItems {
        return createChannels().filter { it.channelID == channelId }.first()
    }
    fun getProgramData(programId:Int,channelId: Int):ProgramRowItems {
        return createPrograms().filter { it.programID == programId && it.channelId == channelId }.first()
    }

    fun createPrograms(): MutableList<ProgramRowItems> {
        return mutableListOf(
            ProgramRowItems(programID = 1, programName = "Program 1A", "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "1.00", programEnd = "2.00", channelId = 1,true,true,false),
            ProgramRowItems(programID = 2, programName = "Program 2A","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "2.00", programEnd = "3.00", channelId = 1,false,false,false),
            ProgramRowItems(programID = 3, programName = "Program 3A", "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "3.00", programEnd = "4.00", channelId = 1,true,false,false),
            ProgramRowItems(programID = 4, programName = "Program 4A","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "4.00", programEnd = "5.00", channelId = 1,false,true,false),
            ProgramRowItems(programID = 5, programName = "Program 5A", "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "5.00", programEnd = "6.00", channelId = 1,false,false,false),
            ProgramRowItems(programID = 6, programName = "Program 6","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "6.00", programEnd = "8.00", channelId = 1,false,false,false),
            ProgramRowItems(programID = 7, programName = "Program 7","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "8.00", programEnd = "9.00", channelId = 1,false,false,false),
            ProgramRowItems(programID = 8, programName = "Program 8","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "9.00", programEnd = "10.00", channelId = 1,false,false,false),
            ProgramRowItems(programID = 9, programName = "Program 9","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "10.00", programEnd = "11.00", channelId = 1,false,false,false),
            ProgramRowItems(programID = 10, programName = "Program 10","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "11.00", programEnd = "12.00", channelId = 1,false,false,false),
            ProgramRowItems(programID = 11, programName = "Program 11","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "12.00", programEnd = "13.00", channelId = 1,false,false,false),
            ProgramRowItems(programID = 12, programName = "Program 12","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "13.00", programEnd = "15.00", channelId = 1,false,false,false),
            ProgramRowItems(programID = 13, programName = "Program 13","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "15.00", programEnd = "18.00", channelId = 1,false,false,false),
            ProgramRowItems(programID = 14, programName = "Program 14","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "18.00", programEnd = "19.00", channelId = 1,false,false,false),
            ProgramRowItems(programID = 15, programName = "Program 15","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "19.00", programEnd = "22.00", channelId = 1,false,false,false),


            ProgramRowItems(programID = 1, programName = "Program 1B", "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "1.00", programEnd = "1.30", channelId = 2,false,false,false),
            ProgramRowItems(programID = 2, programName = "Program 2B","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "1.30", programEnd = "3.00", channelId = 2,false,false,false),
            ProgramRowItems(programID = 3, programName = "Program 3B", "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "3.00", programEnd = "4.00", channelId = 2,false,false,false),
            ProgramRowItems(programID = 4, programName = "Program 4B","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "4.00", programEnd = "5.00", channelId = 2,false,false,false),
            ProgramRowItems(programID = 5, programName = "Program 5B", "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "5.00", programEnd = "6.00", channelId = 2,false,false,false),
            ProgramRowItems(programID = 6, programName = "Program 6B","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "6.00", programEnd = "8.00", channelId = 2,false,false,false),
            ProgramRowItems(programID = 7, programName = "Program 7B","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "8.00", programEnd = "9.00", channelId = 2,false,false,false),
            ProgramRowItems(programID = 8, programName = "Program 8","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "9.00", programEnd = "10.00", channelId = 2,false,false,false),
            ProgramRowItems(programID = 9, programName = "Program 9","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "10.00", programEnd = "11.00", channelId = 2,false,false,false),
            ProgramRowItems(programID = 10, programName = "Program 10","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "11.00", programEnd = "12.00", channelId = 2,false,false,false),
            ProgramRowItems(programID = 11, programName = "Program 11","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "12.00", programEnd = "13.00", channelId = 2,false,false,false),
            ProgramRowItems(programID = 12, programName = "Program 12","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "13.00", programEnd = "14.00", channelId = 2,false,false,false),
            ProgramRowItems(programID = 13, programName = "Program 13","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "14.00", programEnd = "14.30", channelId = 2,false,false,false),
            ProgramRowItems(programID = 14, programName = "Program 14","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "14.30", programEnd = "16.00", channelId = 2,false,false,false),
            ProgramRowItems(programID = 15, programName = "Program 15","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "16.00", programEnd = "22.00", channelId = 2,false,false,false),


            ProgramRowItems(programID = 1, programName = "Program 1", "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "1.00", programEnd = "2.00", channelId = 3,false,false,false),
            ProgramRowItems(programID = 2, programName = "Program 2","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "2.00", programEnd = "3.00", channelId = 3,false,false,false),
            ProgramRowItems(programID = 3, programName = "Program 3", "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "3.00", programEnd = "4.00", channelId = 3,false,false,false),
            ProgramRowItems(programID = 4, programName = "Program 4","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "4.00", programEnd = "5.00", channelId = 3,false,false,false),
            ProgramRowItems(programID = 5, programName = "Program 5", "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "5.00", programEnd = "5.30", channelId = 3,false,false,false),
            ProgramRowItems(programID = 6, programName = "Program 6","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "5.30", programEnd = "8.00", channelId = 3,false,false,false),
            ProgramRowItems(programID = 7, programName = "Program 7","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "8.00", programEnd = "9.00", channelId = 3,false,false,false),
            ProgramRowItems(programID = 8, programName = "Program 8","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "9.00", programEnd = "10.00", channelId = 3,false,false,false),
            ProgramRowItems(programID = 9, programName = "Program 9","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "10.00", programEnd = "11.00", channelId = 3,false,false,false),
            ProgramRowItems(programID = 10, programName = "Program 10","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "11.00", programEnd = "12.00", channelId = 3,false,false,false),
            ProgramRowItems(programID = 11, programName = "Program 11","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "12.00", programEnd = "13.00", channelId = 3,false,false,false),
            ProgramRowItems(programID = 12, programName = "Program 12","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "13.00", programEnd = "15.00", channelId = 3,false,false,false),
            ProgramRowItems(programID = 13, programName = "Program 13","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "15.00", programEnd = "16.00", channelId = 3,false,false,false),
            ProgramRowItems(programID = 14, programName = "Program 14","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "16.00", programEnd = "19.00", channelId = 3,false,false,false),
            ProgramRowItems(programID = 15, programName = "Program 15","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "19.00", programEnd = "22.00", channelId = 3,false,false,false),


            ProgramRowItems(programID = 1, programName = "Program 1", "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "1.00", programEnd = "2.30", channelId = 4,false,false,false),
            ProgramRowItems(programID = 2, programName = "Program 2","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "2.30", programEnd = "3.00", channelId = 4,false,false,false),
            ProgramRowItems(programID = 3, programName = "Program 3", "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "3.00", programEnd = "4.00", channelId = 4,false,false,false),
            ProgramRowItems(programID = 4, programName = "Program 4","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "4.00", programEnd = "5.00", channelId = 4,false,false,false),
            ProgramRowItems(programID = 5, programName = "Program 5", "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "5.00", programEnd = "5.30", channelId = 4,false,false,false),
            ProgramRowItems(programID = 6, programName = "Program 6","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "5.30", programEnd = "8.00", channelId = 4,false,false,false),
            ProgramRowItems(programID = 7, programName = "Program 7","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "8.00", programEnd = "9.00", channelId = 4,false,false,false),
            ProgramRowItems(programID = 8, programName = "Program 8","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "9.00", programEnd = "10.00", channelId = 4,false,false,false),
            ProgramRowItems(programID = 9, programName = "Program 9","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "10.00", programEnd = "11.00", channelId = 4,false,false,false),
            ProgramRowItems(programID = 10, programName = "Program 10","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "11.00", programEnd = "12.00", channelId = 4,false,false,false),
            ProgramRowItems(programID = 11, programName = "Program 11","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "12.00", programEnd = "13.00", channelId = 4,false,false,false),
            ProgramRowItems(programID = 12, programName = "Program 12","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "13.00", programEnd = "15.00", channelId = 4,false,false,false),
            ProgramRowItems(programID = 13, programName = "Program 13","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "15.00", programEnd = "17.00", channelId = 4,false,false,false),
            ProgramRowItems(programID = 14, programName = "Program 14","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "17.00", programEnd = "19.00", channelId = 4,false,false,false),
            ProgramRowItems(programID = 15, programName = "Program 15","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "19.00", programEnd = "22.00", channelId = 4,false,false,false),


            ProgramRowItems(programID = 1, programName = "Program 1", "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "1.00", programEnd = "1.30", channelId = 5,false,false,false),
            ProgramRowItems(programID = 2, programName = "Program 2","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "1.30", programEnd = "3.00", channelId = 5,false,false,false),
            ProgramRowItems(programID = 3, programName = "Program 3", "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "3.00", programEnd = "4.00", channelId = 5,false,false,false),
            ProgramRowItems(programID = 4, programName = "Program 4","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "4.00", programEnd = "5.00", channelId = 5,false,false,false),
            ProgramRowItems(programID = 5, programName = "Program 5", "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "5.00", programEnd = "5.30", channelId = 5,false,false,false),
            ProgramRowItems(programID = 6, programName = "Program 6","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "5.30", programEnd = "8.00", channelId = 5,false,false,false),
            ProgramRowItems(programID = 7, programName = "Program 7","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "8.00", programEnd = "9.00", channelId = 5,false,false,false),
            ProgramRowItems(programID = 8, programName = "Program 8","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "9.00", programEnd = "10.00", channelId = 5,false,false,false),
            ProgramRowItems(programID = 9, programName = "Program 9","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "10.00", programEnd = "11.00", channelId = 5,false,false,false),
            ProgramRowItems(programID = 10, programName = "Program 10","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "11.00", programEnd = "12.00", channelId = 5,false,false,false),
            ProgramRowItems(programID = 11, programName = "Program 11","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "12.00", programEnd = "13.00", channelId = 5,false,false,false),
            ProgramRowItems(programID = 12, programName = "Program 12","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "13.00", programEnd = "16.00", channelId = 5,false,false,false),
            ProgramRowItems(programID = 13, programName = "Program 13","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "16.00", programEnd = "17.00", channelId = 5,false,false,false),
            ProgramRowItems(programID = 14, programName = "Program 14","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "17.00", programEnd = "19.00", channelId = 5,false,false,false),
            ProgramRowItems(programID = 15, programName = "Program 15","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "19.00", programEnd = "22.00", channelId = 5,false,false,false),


            ProgramRowItems(programID = 1, programName = "Program 1", "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "1.00", programEnd = "2.00", channelId = 6,false,false,false),
            ProgramRowItems(programID = 2, programName = "Program 2","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "2.00", programEnd = "3.00", channelId = 6,false,false,false),
            ProgramRowItems(programID = 3, programName = "Program 3", "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "3.00", programEnd = "4.00", channelId = 6,false,false,false),
            ProgramRowItems(programID = 4, programName = "Program 4","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "4.00", programEnd = "5.00", channelId = 6,false,false,false),
            ProgramRowItems(programID = 5, programName = "Program 5", "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "5.00", programEnd = "5.30", channelId = 6,false,false,false),
            ProgramRowItems(programID = 6, programName = "Program 6","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "5.30", programEnd = "8.00", channelId = 6,false,false,false),
            ProgramRowItems(programID = 7, programName = "Program 7","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "8.00", programEnd = "9.00", channelId = 6,false,false,false),
            ProgramRowItems(programID = 8, programName = "Program 8","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "9.00", programEnd = "10.00", channelId = 6,false,false,false),
            ProgramRowItems(programID = 9, programName = "Program 9","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "10.00", programEnd = "11.00", channelId = 6,false,false,false),
            ProgramRowItems(programID = 10, programName = "Program 10","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "11.00", programEnd = "12.00", channelId = 6,false,false,false),
            ProgramRowItems(programID = 11, programName = "Program 11","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "12.00", programEnd = "13.00", channelId = 6,false,false,false),
            ProgramRowItems(programID = 12, programName = "Program 12","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "13.00", programEnd = "14.00", channelId = 6,false,false,false),
            ProgramRowItems(programID = 13, programName = "Program 13","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "14.00", programEnd = "15.00", channelId = 6,false,false,false),
            ProgramRowItems(programID = 14, programName = "Program 14","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "15.00", programEnd = "17.00", channelId = 6,false,false,false),
            ProgramRowItems(programID = 15, programName = "Program 15","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "17.00", programEnd = "22.00", channelId = 6,false,false,false),


            ProgramRowItems(programID = 1, programName = "Program 1", "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "1.00", programEnd = "2.00", channelId = 7,false,false,false),
            ProgramRowItems(programID = 2, programName = "Program 2","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "2.00", programEnd = "3.00", channelId = 7,false,false,false),
            ProgramRowItems(programID = 3, programName = "Program 3", "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "3.00", programEnd = "4.00", channelId = 7,false,false,false),
            ProgramRowItems(programID = 4, programName = "Program 4","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "4.00", programEnd = "5.00", channelId = 7,false,false,false),
            ProgramRowItems(programID = 5, programName = "Program 5", "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "5.00", programEnd = "5.30", channelId = 7,false,false,false),
            ProgramRowItems(programID = 6, programName = "Program 6","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "5.30", programEnd = "7.30", channelId = 7,false,false,false),
            ProgramRowItems(programID = 7, programName = "Program 7","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "7.30", programEnd = "9.00", channelId = 7,false,false,false),
            ProgramRowItems(programID = 8, programName = "Program 8","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "9.00", programEnd = "10.00", channelId = 7,false,false,false),
            ProgramRowItems(programID = 9, programName = "Program 9","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "10.00", programEnd = "11.00", channelId = 7,false,false,false),
            ProgramRowItems(programID = 10, programName = "Program 10","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "11.00", programEnd = "12.00", channelId = 7,false,false,false),
            ProgramRowItems(programID = 11, programName = "Program 11","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "12.00", programEnd = "13.00", channelId = 7,false,false,false),
            ProgramRowItems(programID = 12, programName = "Program 12","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "13.00", programEnd = "16.00", channelId = 7,false,false,false),
            ProgramRowItems(programID = 13, programName = "Program 13","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "16.00", programEnd = "17.00", channelId = 7,false,false,false),
            ProgramRowItems(programID = 14, programName = "Program 14","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "17.00", programEnd = "19.00", channelId = 7,false,false,false),
            ProgramRowItems(programID = 15, programName = "Program 15","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "19.00", programEnd = "22.00", channelId = 7,false,false,false),


            ProgramRowItems(programID = 1, programName = "Program 1", "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "1.00", programEnd = "2.00", channelId = 8,false,false,false),
            ProgramRowItems(programID = 2, programName = "Program 2","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "2.00", programEnd = "3.00", channelId = 8,false,false,false),
            ProgramRowItems(programID = 3, programName = "Program 3", "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "3.00", programEnd = "4.00", channelId = 8,false,false,false),
            ProgramRowItems(programID = 4, programName = "Program 4","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "4.00", programEnd = "5.00", channelId = 8,false,false,false),
            ProgramRowItems(programID = 5, programName = "Program 5", "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "5.00", programEnd = "5.30", channelId = 8,false,false,false),
            ProgramRowItems(programID = 6, programName = "Program 6","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "5.30", programEnd = "8.00", channelId = 8,false,false,false),
            ProgramRowItems(programID = 7, programName = "Program 7","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "8.00", programEnd = "9.00", channelId = 8,false,false,false),
            ProgramRowItems(programID = 8, programName = "Program 8","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "9.00", programEnd = "10.00", channelId = 8,false,false,false),
            ProgramRowItems(programID = 9, programName = "Program 9","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "10.00", programEnd = "11.00", channelId = 8,false,false,false),
            ProgramRowItems(programID = 10, programName = "Program 10","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "11.00", programEnd = "12.00", channelId = 8,false,false,false),
            ProgramRowItems(programID = 11, programName = "Program 11","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "12.00", programEnd = "13.00", channelId = 8,false,false,false),
            ProgramRowItems(programID = 12, programName = "Program 12","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "13.00", programEnd = "17.00", channelId = 8,false,false,false),
            ProgramRowItems(programID = 13, programName = "Program 13","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "17.00", programEnd = "18.00", channelId = 8,false,false,false),
            ProgramRowItems(programID = 14, programName = "Program 14","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "18.00", programEnd = "19.00", channelId = 8,false,false,false),
            ProgramRowItems(programID = 15, programName = "Program 15","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "19.00", programEnd = "22.00", channelId = 8,false,false,false),


            ProgramRowItems(programID = 1, programName = "Program 1", "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "1.00", programEnd = "2.00", channelId = 9,false,false,false),
            ProgramRowItems(programID = 2, programName = "Program 2","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "2.00", programEnd = "3.00", channelId = 9,false,false,false),
            ProgramRowItems(programID = 3, programName = "Program 3", "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "3.00", programEnd = "4.00", channelId = 9,false,false,false),
            ProgramRowItems(programID = 4, programName = "Program 4","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "4.00", programEnd = "5.00", channelId = 9,false,false,false),
            ProgramRowItems(programID = 5, programName = "Program 5", "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "5.00", programEnd = "5.30", channelId = 9,false,false,false),
            ProgramRowItems(programID = 6, programName = "Program 6","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "5.30", programEnd = "8.00", channelId = 9,true,false,false),
            ProgramRowItems(programID = 7, programName = "Program 7","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "8.00", programEnd = "9.00", channelId = 9,false,false,false),
            ProgramRowItems(programID = 8, programName = "Program 8","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "9.00", programEnd = "10.00", channelId = 9,false,false,false),
            ProgramRowItems(programID = 9, programName = "Program 9","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "10.00", programEnd = "11.00", channelId = 9,false,false,false),
            ProgramRowItems(programID = 10, programName = "Program 10","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "11.00", programEnd = "12.00", channelId = 9,false,false,false),
            ProgramRowItems(programID = 11, programName = "Program 11","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "12.00", programEnd = "13.00", channelId = 9,false,false,false),
            ProgramRowItems(programID = 12, programName = "Program 12","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "13.00", programEnd = "13.30", channelId = 9,false,false,false),
            ProgramRowItems(programID = 13, programName = "Program 13","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "13.30", programEnd = "15.00", channelId = 9,false,false,false),
            ProgramRowItems(programID = 14, programName = "Program 14","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "15.00", programEnd = "18.00", channelId = 9,false,false,false),
            ProgramRowItems(programID = 15, programName = "Program 15","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "18.00", programEnd = "22.00", channelId = 9,false,false,false),


            ProgramRowItems(programID = 1, programName = "Program 1", "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "1.00", programEnd = "2.00", channelId = 10,false,false,false),
            ProgramRowItems(programID = 2, programName = "Program 2","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "2.00", programEnd = "3.00", channelId = 10,false,false,false),
            ProgramRowItems(programID = 3, programName = "Program 3", "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "3.00", programEnd = "4.00", channelId = 10,false,false,false),
            ProgramRowItems(programID = 4, programName = "Program 4","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "4.00", programEnd = "5.00", channelId = 10,false,false,false),
            ProgramRowItems(programID = 5, programName = "Program 5", "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "5.00", programEnd = "5.30", channelId = 10,false,false,false),
            ProgramRowItems(programID = 6, programName = "Program 6","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "5.30", programEnd = "8.00", channelId = 10,false,false,false),
            ProgramRowItems(programID = 7, programName = "Program 7","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "8.00", programEnd = "9.00", channelId = 10,false,false,false),
            ProgramRowItems(programID = 8, programName = "Program 8","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "9.00", programEnd = "10.00", channelId = 10,false,false,false),
            ProgramRowItems(programID = 9, programName = "Program 9","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "10.00", programEnd = "11.00", channelId = 10,false,false,false),
            ProgramRowItems(programID = 10, programName = "Program 10","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "11.00", programEnd = "12.00", channelId = 10,false,false,false),
            ProgramRowItems(programID = 11, programName = "Program 11","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "12.00", programEnd = "14.00", channelId = 10,false,false,false),
            ProgramRowItems(programID = 12, programName = "Program 12","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "14.00", programEnd = "15.00", channelId = 10,false,false,false),
            ProgramRowItems(programID = 13, programName = "Program 13","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "15.00", programEnd = "17.00", channelId = 10,false,false,false),
            ProgramRowItems(programID = 14, programName = "Program 14","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "17.00", programEnd = "19.00", channelId = 10,false,false,false),
            ProgramRowItems(programID = 15, programName = "Program 15","https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/AMC%20HD.png",programStart = "19.00", programEnd = "22.00", channelId = 10,false,false,false),

            )

    }

    fun returnProgramRows( programsList: MutableList<ProgramRowItems>):Int {
        var numberOfShows =0
        programsList.forEach {
            it.programID
            numberOfShows++
        }

        return programsList.distinctBy { it.programID }.size
    }

    fun returnChannelRows(channelsList: MutableList<ChannelRowItems>):Int {
        return channelsList.distinctBy { it.channelID }.size
    }
}