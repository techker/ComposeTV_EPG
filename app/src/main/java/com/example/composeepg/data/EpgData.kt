package com.example.composeepg.data

import android.content.Context
import android.util.TypedValue
import com.example.composeepg.R
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Collections
import java.util.Date
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.floor

object EpgData {

    /**
     * Hours
     */
     fun getHalfHour(): Long {
        val hour = TimeUnit.HOURS.toMillis(1)
        return hour / 2
    }
    fun getHour(): Long {
        return TimeUnit.HOURS.toMillis(1)
    }
    fun getDay(): Long {
        return TimeUnit.DAYS.toMillis(1)
    }

    fun getWeek(): Long {
        val hour = TimeUnit.HOURS.toMillis(1)
        val day = hour * 24
        return day * 7
    }

    fun getNowTime():Long {
        val calendar = Calendar.getInstance()
        return calendar.timeInMillis
    }

    fun getEndTime():Long{
        val calendar = Calendar.getInstance()
        val nowTime = calendar.timeInMillis
        val hour = TimeUnit.HOURS.toMillis(1)
        //+12 hrs from now
        return (nowTime + (12 * hour))
    }

    fun getStartTimeLookBack(time:Int):Long {
        val hour = TimeUnit.HOURS.toMillis(1)
        val calendar = Calendar.getInstance()
        val nowTime = calendar.timeInMillis
        //If you have lookBack (nowTime - (12 * hour))
        return (nowTime - (time * hour))
    }

     fun generateTimeList(startTime: Long, endTime: Long, halfHour: Long):ArrayList<TimelineModel> {
        val timelineList: ArrayList<TimelineModel> = arrayListOf()
        var i = startTime
        while (i <= endTime) {
            val timelineModel = TimelineModel(i)
            timelineList.add(timelineModel)
            i += halfHour
        }
        return timelineList
    }

    fun convertStringToTime(timeString: String): LocalTime {
        // Replace the dot with a colon to get a standard time format
        val standardTimeString = timeString.replace(".", ":")
        // Parse the string into a LocalTime object
        // Assuming the time string is in a format like "H:mm"
        val formatter = DateTimeFormatter.ofPattern("H:mm")
        return LocalTime.parse(standardTimeString, formatter)
    }

    fun timeStringToMinutes(time: String): Int {
        val (hours, minutes) = time.split(".").map { it.toInt() }
        return hours * 60 + minutes
    }

    /**
     *
     */
    private fun pxPerMinConstant(context: Context): Double {
        return convertDpToPixel(
            context.resources.getDimension(R.dimen.epg_width_one_min).toDouble(), context
        )
    }

    private fun convertDpToPixel(dp: Double, context: Context): Double {
        val r = context.resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            r.displayMetrics
        ).toDouble()
    }

    fun convertMillisecondsToPx(milliseconds: Double, context: Context): Double {
        return milliseconds * pxPerMinConstant(context) / TimeUnit.MINUTES.toMillis(1)
    }
    fun convertPxToMilliseconds(px: Double, context: Context): Double {
        return TimeUnit.MINUTES.toMillis(1) * px / pxPerMinConstant(context)
    }
     fun calculateOffset(context: Context):Double {
        val hour = TimeUnit.HOURS.toMillis(1)
        return convertMillisecondsToPx((hour / 2).toDouble(),context)
    }
    fun getNearestHalfHour(timeMs: Long): Long {
        return Math.round(timeMs.toDouble() / (30 * 60 * 1000)) * (30 * 60 * 1000) // use Math.round to get nearest rounded previous/next half hour from the given time.
    }
    fun getRoundedTimetoNearestPastHalfHour(timeMs: Long): Long {
        return (floor(timeMs.toDouble() / (30 * 60 * 1000)) * (30 * 60 * 1000)).toLong() // use Math.floor to get nearest past half hour from the given time.
    }
    fun getInitialPositionInList(
        currentTime: Double,
        arrayList: ArrayList<BaseProgramModel>
    ): Int {
        val programModel = BaseProgramModel(currentTime.toLong(), 0)
        var pos = Collections.binarySearch(arrayList, programModel, comparatorProgram)
        //TODO handle the situation when the list has less than 2 items
        if (pos < 0) pos = Math.abs(pos) - 2
        return pos
    }


    private val comparatorTime = Comparator<TimelineModel> { u1, u2 -> (u1.time - u2.time).toInt() }
    private val comparatorProgram = Comparator<BaseProgramModel> { u1, u2 -> (u1.startTime - u2.startTime).toInt() }

    fun getInitialProgramOffsetPx(
        programStartTime: Double,
        systemTime: Double,
        context: Context
    ): Int {
        val offsetTime = systemTime - programStartTime
        return convertMillisecondsToPx(offsetTime, context).toInt()
    }

    fun getSelectedPositionInTimelineList(
        selectedTime: Long,
        arrayList: ArrayList<TimelineModel>
    ): Int {
        val timeModel = TimelineModel(selectedTime)
        var pos = Collections.binarySearch(arrayList, timeModel, comparatorTime)
        //TODO handle the situation when the list has less than 2 items
        if (pos < 0) pos = abs(pos) - 2
        return pos
    }

    fun getSelectedPositionOffsetPx(
        programStartTime: Double,
        context: Context
    ): Int {
        val offsetTime = programStartTime
        return convertMillisecondsToPx(offsetTime, context).toInt()
    }

}