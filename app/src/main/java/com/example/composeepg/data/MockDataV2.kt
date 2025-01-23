package com.example.composeepg.data

import com.example.composeepg.jctvguide.data.Event
import com.example.composeepg.screens.libtvguide.data.Channel
import kotlin.random.Random
import kotlin.time.Duration.Companion.minutes

class MockDataV2 {



    fun createMockChannels(): List<Channel> {
        return mutableListOf(
            Channel(id = 1, title = "ESPN", icon = "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/ESPN%20HD.png"),
            Channel(id = 2, title = "MTV", icon = "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/MTV.png"),
            Channel(id = 3, title = "WGN", icon = "https://ott-logos.s3.us-east-1.amazonaws.com/WGNHD.png"),
            Channel(id = 4, title = "BET", icon = "https://ott-logos.s3.us-east-1.amazonaws.com/BETHD.png"),
            Channel(id = 5, title = "BBC", icon = "https://ott-logos.s3.us-east-1.amazonaws.com/BBCHD.png"),
            Channel(id = 6, title = "AMC", icon = "https://github.com/Jasmeet181/mediaportal-us-logos/blob/master/TV/.Light/CHCH%20TV.png?raw=true"),
            Channel(id = 7, title = "Discovery", icon = "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/Discovery.png"),
            Channel(id = 8, title = "History", icon = "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/History%20HD.png"),
            Channel(id = 9, title = "Discovery Life", icon = "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/Discovery%20Life.png"),
            Channel(id = 10, title = "The CW", icon = "https://raw.githubusercontent.com/Jasmeet181/mediaportal-us-logos/master/TV/.Light/The%20CW.png"),
        )
    }

    fun generateMockPrograms(channelId: Int, start: Long, end: Long): List<Event> {
        var currentStartTime = start
        var i = 1
        return buildList {
            while (currentStartTime < end) {
                // Random duration between 30 and 120 minutes for each event
                val duration = Random.nextInt(30, 120).minutes.inWholeMilliseconds
                val eventEndTime = minOf(currentStartTime + duration, end)

                add(
                    Event(
                        i,
                        "Event $i",
                        "Description of event $i",
                        currentStartTime,
                        eventEndTime
                    ).also {
                        currentStartTime = eventEndTime
                    })


                // Update start time for the next program
                currentStartTime = eventEndTime
                i++
            }
        }
    }

}