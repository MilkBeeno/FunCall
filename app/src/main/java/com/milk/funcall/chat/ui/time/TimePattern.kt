package com.milk.funcall.chat.ui.time

object TimePattern {
    /** 一整天时间总秒数 */
    const val TIME_OF_TOTAL_DAY_MILLIS = (24 * 3600 * 1000).toLong()

    /** Thu, 29 Sep 2016 07:57:42 GMT 模式 */
    const val TEST_OF_TIME_PATTERN = "EEE, d MMM yyyy HH:mm:ss z"

    /** 西五区时间 GMT 格式 */
    const val GMT_05 = "GMT-00"
}