package com.milk.funcall.common.util

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import com.milk.simple.log.Logger
import java.io.File
import java.io.FileOutputStream

object FileUtil {

    internal fun saveVideoImagePath(context: Context, videoPath: String): String {
        var file: File? = null
        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(videoPath)
            val bitmap = retriever.frameAtTime
            file = File(
                context.getExternalFilesDir("video_image_path"),
                System.currentTimeMillis().toString().plus(".png")
            )
            if (!file.exists()) {
                file.parentFile?.mkdirs()
                file.createNewFile()
            }
            val fileOutputStream = FileOutputStream(file)
            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            Logger.d("当前视频封面地址是=${file.absolutePath}", "FileUtil")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file?.absolutePath.toString()
    }
}