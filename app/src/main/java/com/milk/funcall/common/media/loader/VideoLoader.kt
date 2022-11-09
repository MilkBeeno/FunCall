package com.milk.funcall.common.media.loader

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import coil.decode.VideoFrameDecoder
import coil.imageLoader
import coil.load


class VideoLoader(
    val data: Any,
    val placeholderResId: Int,
    val placeholderDrawable: Drawable?,
    val errorResId: Int,
    val errorDrawable: Drawable?,
    val targetView: AppCompatImageView
) {
    init {
        loadVideo()
    }

    private fun loadVideo() {
        val videoLoader = targetView.context.imageLoader
            .newBuilder()
            .components { add(VideoFrameDecoder.Factory()) }
            .build()
        targetView.load(data, videoLoader) {
            crossfade(true)
            crossfade(200)
            when {
                placeholderResId > 0 -> placeholder(placeholderResId)
                placeholderDrawable != null -> placeholder(placeholderDrawable)
            }
            when {
                errorResId > 0 -> error(errorResId)
                errorDrawable != null -> error(errorDrawable)
                placeholderResId > 0 -> error(placeholderResId)
                placeholderDrawable != null -> error(placeholderDrawable)
            }
        }
    }

    class Builder {
        private var data: Any? = null
        private var placeholderResId: Int = 0
        private var placeholderDrawable: Drawable? = null
        private var errorResId: Int = 0
        private var errorDrawable: Drawable? = null
        private var targetView: AppCompatImageView? = null

        internal fun request(data: Any?) = apply {
            this.data = data
        }

        internal fun placeholder(@DrawableRes drawableResId: Int) = apply {
            this.placeholderResId = drawableResId
            this.placeholderDrawable = null

        }

        internal fun placeholder(drawable: Drawable?) = apply {
            this.placeholderDrawable = drawable
            this.placeholderResId = 0
        }

        internal fun error(@DrawableRes drawableResId: Int) = apply {
            this.errorResId = drawableResId
            this.errorDrawable = null
        }

        internal fun error(drawable: Drawable?) = apply {
            this.errorDrawable = drawable
            this.errorResId = 0
        }

        internal fun target(targetView: AppCompatImageView) = apply {
            this.targetView = targetView
        }

        internal fun build(): VideoLoader {
            return VideoLoader(
                data = checkNotNull(data),
                placeholderResId = placeholderResId,
                placeholderDrawable = placeholderDrawable,
                errorResId = errorResId,
                errorDrawable = errorDrawable,
                targetView = checkNotNull(targetView)
            )
        }
    }
}

