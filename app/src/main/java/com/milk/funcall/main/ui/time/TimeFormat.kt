package com.milk.funcall.main.ui.time

import java.text.SimpleDateFormat
import java.util.*

// 服务器时间戳（秒级）自动补全（毫秒级）
internal fun autoCompleteTime(seconds: Long): Long = if (System.currentTimeMillis()
        .toString().length - seconds.toString().length == 3
) seconds * 1000 else seconds

// 客户端和服务端时间校准
fun Long.timeCalibration(
    timeZone: String = TimePattern.GMT_05,
    pattern: String = TimePattern.dd_MM_yyyy_HH_mm
): String {
    var finalTime = ""
    try {
        if (this <= 0) return finalTime
        val formatter = SimpleDateFormat(pattern, Locale.ENGLISH)
        formatter.timeZone = TimeZone.getTimeZone(timeZone.uppercase())
        finalTime = formatter.format(autoCompleteTime(this))
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return finalTime
}

// 将时间转为 IM 通用消息、
fun Long.convertMessageTime(localTime: Long = System.currentTimeMillis()): String {
    var finalTime: String = ""
    val operateCompleteTime = autoCompleteTime(this)
    val localCompleteTime = autoCompleteTime(localTime)
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = operateCompleteTime
    when {
        operateCompleteTime > localCompleteTime -> {
            val differValue = operateCompleteTime - localCompleteTime
            // 如果未来时间比本地快10s、则矫正到本地时间 59m59s
            finalTime = if (differValue in 0..10000) {
                calendar.hourStr()
                    .plus(":")
                    .plus(calendar.minuteStr())
            } else {
                calendar.dayStr()
                    .plus("/")
                    .plus(calendar.monthStr())
                    .plus("/")
                    .plus(calendar.yearStr())
                    .plus(" ")
                    .plus(calendar.hourStr())
                    .plus(":")
                    .plus(calendar.minuteStr())
            }
        }
        inTheSameDay(operateCompleteTime, localCompleteTime) -> {
            finalTime = calendar.hourStr()
                .plus(":")
                .plus(calendar.minuteStr())
        }
        inTheYesterday(operateCompleteTime, localCompleteTime) -> {
            finalTime = "Yesterday"
                .plus(" ")
                .plus(calendar.hourStr())
                .plus(":")
                .plus(calendar.minuteStr())
        }
        inTheSameWeek(operateCompleteTime, localCompleteTime) -> {
            finalTime = calendar.weekAllStr()
                .plus(" ")
                .plus(calendar.hourStr())
                .plus(":")
                .plus(calendar.minuteStr())
        }
        inTheSameYear(operateCompleteTime, localCompleteTime) -> {
            finalTime = calendar.dayStr()
                .plus("/")
                .plus(calendar.monthStr())
                .plus(" ")
                .plus(calendar.hourStr())
                .plus(":")
                .plus(calendar.minuteStr())
        }
        else -> {
            finalTime = calendar.dayStr()
                .plus("/")
                .plus(calendar.monthStr())
                .plus("/")
                .plus(calendar.yearStr())
                .plus(" ")
                .plus(calendar.hourStr())
                .plus(":")
                .plus(calendar.minuteStr())
        }
    }
    return finalTime
}

internal fun Calendar.weekAllStr(): String {
    return when (this[Calendar.DAY_OF_WEEK]) {
        Calendar.SUNDAY -> "Sunday"
        Calendar.MONDAY -> "Monday"
        Calendar.TUESDAY -> "Tuesday"
        Calendar.WEDNESDAY -> "Wednesday"
        Calendar.THURSDAY -> "Thursday"
        Calendar.FRIDAY -> "Friday"
        Calendar.SATURDAY -> "Saturday"
        else -> ""
    }
}





