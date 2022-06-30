package com.milk.funcall.chat.ui.time

import java.util.*

private val defaultTime: Long
    get() = System.currentTimeMillis()

// 返回是否是相同的值、更具Calendar取不同的Key的值
private fun isTheSameValue(compareKey: Int, originTime: Long, targetTime: Long): Boolean {
    val originCalendar: Calendar = Calendar.getInstance().apply { timeInMillis = originTime }
    val targetCalendar: Calendar = Calendar.getInstance().apply { timeInMillis = targetTime }
    return targetCalendar.get(compareKey) - originCalendar.get(compareKey) == 0
}

internal fun inTheSameYear(originTime: Long, targetTime: Long = defaultTime): Boolean {
    return isTheSameValue(Calendar.YEAR, originTime, targetTime)
}

internal fun inTheSameMonth(originTime: Long, targetTime: Long = defaultTime): Boolean {
    return inTheSameYear(originTime, targetTime)
            && isTheSameValue(Calendar.MONTH, originTime, targetTime)
}

internal fun inTheSameWeek(time: Long, targetTime: Long = defaultTime): Boolean {
    return inTheSameMonth(time, targetTime)
            && isTheSameValue(Calendar.WEEK_OF_YEAR, time, targetTime)
}

internal fun inTheSameDay(originTime: Long, targetTime: Long = defaultTime): Boolean {
    return inTheSameMonth(originTime, targetTime)
            && isTheSameValue(Calendar.DAY_OF_MONTH, originTime, targetTime)
}

internal fun inTheYesterday(taskTime: Long, targetTime: Long = defaultTime): Boolean {
    return inTheSameDay(taskTime, targetTime - TimePattern.TIME_OF_TOTAL_DAY_MILLIS)
}

internal fun inTheSameHour(originTime: Long, targetTime: Long = defaultTime): Boolean {
    return inTheSameDay(originTime, targetTime)
            && isTheSameValue(Calendar.HOUR_OF_DAY, originTime, targetTime)
}

internal fun inTheSameMillis(originTime: Long, targetTime: Long = defaultTime): Boolean {
    return inTheSameHour(originTime, targetTime)
            && isTheSameValue(Calendar.MINUTE, originTime, targetTime)
}
