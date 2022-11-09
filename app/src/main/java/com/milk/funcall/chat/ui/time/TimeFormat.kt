package com.milk.funcall.chat.ui.time

import com.milk.funcall.chat.ui.time.TimePattern.TEST_OF_TIME_PATTERN
import com.milk.simple.ktx.safeToLong
import java.text.SimpleDateFormat
import java.util.*

/** 获取当前手机系统的时区 */
private var currentTimeZone: String = TimePattern.GMT_05
    get() {
        val timeZone = TimeZone.getDefault()
        field = timeZone.getDisplayName(false, TimeZone.SHORT)
        return field
    }

/**  服务器时间戳（秒级）自动补全（毫秒级） */
private fun autoCompleteTime(seconds: Long): Long = if (System.currentTimeMillis()
        .toString().length - seconds.toString().length == 3
) seconds * 1000 else seconds

/** 客户端和服务端时间校准差值 */
private fun Long.timeCalibration(): Long {
    var calibrationValue: Long = 0
    try {
        if (this > 0) {
            val formatter = SimpleDateFormat(TEST_OF_TIME_PATTERN, Locale.ENGLISH)
            formatter.timeZone = TimeZone.getTimeZone(currentTimeZone)
            val finalTime = formatter.format(this)
            val dateTime = formatter.parse(finalTime)?.time.safeToLong()
            if (dateTime > System.currentTimeMillis())
                calibrationValue = dateTime - System.currentTimeMillis()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return calibrationValue
}

/** 将时间转为 IM 通用消息 */
internal fun Long.convertMessageTime(): String {
    val finalTime: String
    val localTime = System.currentTimeMillis()
    val calibrationValue = timeCalibration()
    val operateCompleteTime = this - calibrationValue
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





