package com.milk.funcall.chat.ui.time

object TimePattern {
    // 一整天时间总秒数
    const val TIME_OF_TOTAL_DAY_MILLIS = (24 * 3600 * 1000).toLong()

    // 13/5/2022 17.46 模式
    const val dd_MM_yyyy_HH_mm = "dd/MM/yyyy HH:mm"

    // 西五区时间 GMT 格式
    const val GMT_05 = "GMT-05"
}