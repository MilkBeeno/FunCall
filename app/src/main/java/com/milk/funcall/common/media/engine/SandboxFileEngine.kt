package com.milk.funcall.common.media.engine

import android.content.Context
import com.luck.picture.lib.engine.UriToFileTransformEngine
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener
import com.luck.picture.lib.utils.SandboxTransformUtils

internal class SandboxFileEngine : UriToFileTransformEngine {
    override fun onUriToFileAsyncTransform(
        context: Context,
        srcPath: String,
        mineType: String,
        call: OnKeyValueResultCallbackListener
    ) {
        call.onCallback(
            srcPath,
            SandboxTransformUtils.copyPathToSandbox(context, srcPath, mineType)
        )
    }

    companion object {
        val current = SandboxFileEngine()
    }
}