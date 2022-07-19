package com.milk.funcall.common.media.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import tv.danmaku.ijk.media.player.IMediaPlayer
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import java.io.IOException

class IjkVideoView : FrameLayout {
    private var mediaPlayer: IMediaPlayer? = null
    private var surfaceView: SurfaceView? = null
    private var listener: VideoPlayerListener? = null
    private var current: Context? = null
    private var videoPath = ""
    val duration: Long
        get() = mediaPlayer?.duration ?: 0
    val currentPosition: Long
        get() = mediaPlayer?.currentPosition ?: 0

    constructor(ctx: Context) : super(ctx) {
        initVideoView(ctx)
    }

    constructor(ctx: Context, attrs: AttributeSet?) : super(ctx, attrs) {
        initVideoView(ctx)
    }

    constructor(ctx: Context, attrs: AttributeSet?, @AttrRes attr: Int) : super(ctx, attrs, attr) {
        initVideoView(ctx)
    }

    private fun initVideoView(context: Context) {
        current = context
        isFocusable = true
    }

    /** 设置视频地址、根据是否第一次播放视频，做不同的操作 */
    fun setVideoPath(path: String) {
        if (videoPath.isBlank()) {
            videoPath = path
            createSurfaceView()
        } else {
            videoPath = path
            load()
        }
    }

    private fun createSurfaceView() {
        surfaceView = SurfaceView(current)
        surfaceView?.holder?.addCallback(LmnSurfaceCallback())
        val layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.CENTER
        )
        surfaceView?.layoutParams = layoutParams
        addView(surfaceView)
    }

    private fun load() {
        //每次都要重新创建IMediaPlayer
        createPlayer()
        try {
            mediaPlayer?.dataSource = videoPath
        } catch (e: IOException) {
            e.printStackTrace()
        }
        //给mediaPlayer设置视图
        mediaPlayer?.setDisplay(surfaceView?.holder)
        mediaPlayer?.prepareAsync()
    }

    private fun createPlayer() {
        mediaPlayer?.stop()
        mediaPlayer?.setDisplay(null)
        mediaPlayer?.release()
        val ijkMediaPlayer = IjkMediaPlayer()
        IjkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG)
        //开启硬解码
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1)
        mediaPlayer = ijkMediaPlayer
        mediaPlayer?.setOnPreparedListener(listener)
        mediaPlayer?.setOnInfoListener(listener)
        mediaPlayer?.setOnSeekCompleteListener(listener)
        mediaPlayer?.setOnBufferingUpdateListener(listener)
        mediaPlayer?.setOnErrorListener(listener)
        mediaPlayer?.setOnCompletionListener(listener)
    }

    fun setListener(listener: VideoPlayerListener) {
        this.listener = listener
        mediaPlayer?.setOnPreparedListener(listener)
    }

    fun start() {
        mediaPlayer?.start()
    }

    fun release() {
        mediaPlayer?.reset()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun stop() {
        mediaPlayer?.stop()
    }

    fun reset() {
        mediaPlayer?.reset()
    }

    fun seekTo(l: Long) {
        mediaPlayer?.seekTo(l)
    }

    private inner class LmnSurfaceCallback : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder) = Unit
        override fun surfaceDestroyed(holder: SurfaceHolder) = Unit
        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            load()
        }
    }

    abstract class VideoPlayerListener : IMediaPlayer.OnErrorListener,
        IMediaPlayer.OnBufferingUpdateListener, IMediaPlayer.OnSeekCompleteListener,
        IMediaPlayer.OnPreparedListener, IMediaPlayer.OnInfoListener,
        IMediaPlayer.OnCompletionListener {
        override fun onBufferingUpdate(iMediaPlayer: IMediaPlayer, i: Int) = Unit
        override fun onSeekComplete(iMediaPlayer: IMediaPlayer) = Unit
        override fun onPrepared(iMediaPlayer: IMediaPlayer) = Unit
        override fun onCompletion(p0: IMediaPlayer?) = Unit
        override fun onError(iMediaPlayer: IMediaPlayer, i: Int, i1: Int) = false
        override fun onInfo(iMediaPlayer: IMediaPlayer, i: Int, i1: Int) = false
    }
}