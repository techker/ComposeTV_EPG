package com.example.composeepg.data

import java.util.concurrent.TimeUnit

class EpgData {

     fun getHalfHour(): Long {
        val hour = TimeUnit.HOURS.toMillis(1)
        return hour / 2
    }
     fun getHour(): Long {
        val hour = TimeUnit.HOURS.toMillis(1)
        return hour
    }
     fun getDay(): Long {
        val day = TimeUnit.DAYS.toMillis(1)
        return day
    }

}