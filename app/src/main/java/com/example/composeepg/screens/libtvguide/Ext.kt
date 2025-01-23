package com.example.composeepg.screens.libtvguide


import android.util.Log
import com.example.composeepg.jctvguide.data.Event
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.byUnicodePattern
import kotlin.random.Random
import kotlin.time.Duration.Companion.minutes

fun generateEvents(channel: Int, start: Long, stop: Long): List<Event> {
    Log.d("EPG","generateEvent start $start end $stop")
    var startTime = start
    var i = 1
    return buildList {
        while (startTime < stop) {
            val endTime =
                if (startTime.plus(30.minutes.inWholeMilliseconds) >= stop) stop else startTime.plus(
                    Random.nextInt(30, 120).minutes.inWholeMilliseconds
                )
            add(
                Event(
                    i,
                    "Event $i",
                    "Description of event $i",
                    startTime,
                    endTime
                ).also {
                    startTime = endTime
                })
            i++
        }
    }
}

val now
    get() = Clock.System.now().toEpochMilliseconds()

fun Long.formatToPattern(pattern: String): String {
    return Instant.fromEpochMilliseconds(this)
        .format(DateTimeComponents.Format {
            byUnicodePattern(pattern)
        })
}