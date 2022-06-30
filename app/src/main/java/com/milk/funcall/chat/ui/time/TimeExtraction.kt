package com.milk.funcall.chat.ui.time

import java.util.*

internal fun Calendar.dayStr(): String {
    val datStr = this[Calendar.DAY_OF_MONTH].toString()
    return if (datStr.length == 1) "0".plus(datStr) else datStr
}

internal fun Calendar.monthStr(): String {
    val monthStr = (this[Calendar.MONTH] + 1).toString()
    return if (monthStr.length == 1) "0".plus(monthStr) else monthStr
}

internal fun Calendar.yearStr(count: Int = 4): String {
    val yearStr = this[Calendar.YEAR].toString()
    return yearStr.substring(yearStr.length - count)
}

internal fun Calendar.hourStr(): String {
    val hourStr = this[Calendar.HOUR_OF_DAY].toString()
    return if (hourStr.length == 1) "0".plus(hourStr) else hourStr
}

internal fun Calendar.minuteStr(): String {
    val minuteStr = this[Calendar.MINUTE].toString()
    return if (minuteStr.length == 1) "0".plus(minuteStr) else minuteStr
}