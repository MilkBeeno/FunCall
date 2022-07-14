package com.milk.funcall.common.media

import android.content.Context
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.utils.MediaUtils
import com.luck.picture.lib.utils.PictureFileUtils
import com.milk.funcall.BuildConfig
import com.milk.simple.log.Logger

object MediaLogger {
    fun analyticalSelectResults(context: Context, result: ArrayList<LocalMedia>) {
        if (!BuildConfig.DEBUG) return
        val tag = MediaLogger::class.java.name
        result.forEach { media ->
            if (media.width == 0 || media.height == 0) {
                if (PictureMimeType.isHasImage(media.mimeType)) {
                    val imageExtraInfo = MediaUtils.getImageSize(context, media.path)
                    media.width = imageExtraInfo.width
                    media.height = imageExtraInfo.height
                } else if (PictureMimeType.isHasVideo(media.mimeType)) {
                    val videoExtraInfo = MediaUtils.getVideoSize(context, media.path)
                    media.width = videoExtraInfo.width
                    media.height = videoExtraInfo.height
                }
            }
            Logger.d("文件名: " + media.fileName, tag)
            Logger.d("是否压缩:" + media.isCompressed, tag)
            Logger.d("压缩:" + media.compressPath, tag)
            Logger.d("初始路径:" + media.path, tag)
            Logger.d("绝对路径:" + media.realPath, tag)
            Logger.d("是否裁剪:" + media.isCut, tag)
            Logger.d("裁剪路径:" + media.cutPath, tag)
            Logger.d("是否开启原图:" + media.isOriginal, tag)
            Logger.d("原图路径:" + media.originalPath, tag)
            Logger.d("沙盒路径:" + media.sandboxPath, tag)
            Logger.d("水印路径:" + media.watermarkPath, tag)
            Logger.d("视频缩略图:" + media.videoThumbnailPath, tag)
            Logger.d("原始宽高: " + media.width + "x" + media.height, tag)
            Logger.d("裁剪宽高: " + media.cropImageWidth + "x" + media.cropImageHeight)
            val fileSize = PictureFileUtils.formatAccurateUnitFileSize(media.size)
            Logger.d("文件大小: $fileSize", tag)
        }
    }
}