package com.milk.funcall.common.media.engine

import android.content.Context
import android.net.Uri
import com.luck.picture.lib.engine.CompressFileEngine
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener
import com.luck.picture.lib.utils.DateUtils
import top.zibin.luban.Luban
import top.zibin.luban.OnNewCompressListener
import top.zibin.luban.OnRenameListener
import java.io.File

class ImageCompressEngine : CompressFileEngine {
    override fun onStartCompress(
        context: Context,
        source: ArrayList<Uri?>,
        call: OnKeyValueResultCallbackListener
    ) {
        Luban.with(context).load(source).ignoreBy(100)
            .setRenameListener(object : OnRenameListener {
                override fun rename(filePath: String?): String {
                    if (filePath == null) return ""
                    val indexOf = filePath.lastIndexOf(".")
                    val postfix = if (indexOf != -1) filePath.substring(indexOf) else ".jpg"
                    return DateUtils.getCreateFileName("CMP_").toString() + postfix
                }
            }).setCompressListener(object : OnNewCompressListener {
                override fun onStart() = Unit
                override fun onSuccess(source: String?, compressFile: File?) {
                    call.onCallback(source, compressFile?.absolutePath)
                }

                override fun onError(source: String?, e: Throwable?) {
                    call.onCallback(source, null)
                }
            }).launch()
    }

    companion object {
        val current = ImageCompressEngine()
    }
}