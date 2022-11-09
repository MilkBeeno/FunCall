package com.milk.funcall.common.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.milk.simple.log.Logger
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

object BitmapUtil {
    /** 将图片地址转成 bitmap */
    internal fun obtain(imageUrl: String): Bitmap? {
        var inputStream: InputStream? = null
        var outputStream: ByteArrayOutputStream? = null
        var url: URL? = null
        try {
            Logger.d("进行网络连接", "BitmapUtil")
            url = URL(imageUrl)
            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.requestMethod = "GET"
            httpURLConnection.readTimeout = 2000
            httpURLConnection.connect()
            if (httpURLConnection.responseCode == 200) {
                inputStream = httpURLConnection.inputStream
                outputStream = ByteArrayOutputStream()
                val buffer = ByteArray(1024 * 8)
                var len = -1
                while (inputStream.read(buffer).also { len = it } != -1) {
                    outputStream.write(buffer, 0, len)
                }
                val bu = outputStream.toByteArray()
                return BitmapFactory.decodeByteArray(bu, 0, bu.size)
            } else {
                Logger.d(
                    "网络连接失败----" + httpURLConnection.responseCode,
                    "BitmapUtil"
                )
            }
        } catch (e: Exception) {
            Logger.d("第一个catch----" + e.message, "BitmapUtil")
            e.printStackTrace()
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    Logger.d("第二个catch----" + e.message, "BitmapUtil")
                    e.printStackTrace()
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close()
                } catch (e: IOException) {
                    Logger.d("第三个catch----" + e.message, "BitmapUtil")
                    e.printStackTrace()
                }
            }
        }
        return null
    }
}