package com.milk.funcall.common.media.loader

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import coil.load
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.milk.funcall.R
import com.milk.funcall.account.Account
import com.milk.funcall.user.status.Gender
import okhttp3.HttpUrl
import java.io.File
import java.nio.ByteBuffer

class ImageLoader(
    val targetView: AppCompatImageView,
    val data: Any,
    val placeholderResId: Int,
    val placeholderDrawable: Drawable?,
    val errorResId: Int,
    val errorDrawable: Drawable?,
    var isCircle: Boolean,
    var isRoundCorner: Boolean,
    var roundRadius: Float,
    var topLeftRadius: Float,
    var topRightRadius: Float,
    var bottomLeftRadius: Float,
    var bottomRightRadius: Float
) {

    init {
        initializeView()
    }

    private fun initializeView() {
        when {
            isCircle -> {
                setCircleImage(targetView, data)
            }
            isRoundCorner -> {
                setRoundCornerImage(targetView, data)
            }
            else -> setSimpleImage(targetView, data)
        }
    }

    private fun setCircleImage(targetView: AppCompatImageView, data: Any) {
        targetView.load(data) {
            crossfade(true)
            crossfade(200)
            transformations(CircleCropTransformation())
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

    private fun setRoundCornerImage(targetView: AppCompatImageView, data: Any) {
        targetView.load(data) {
            crossfade(true)
            crossfade(200)
            when {
                roundRadius > 0f ->
                    transformations(
                        RoundedCornersTransformation(
                            topLeft = roundRadius,
                            topRight = roundRadius,
                            bottomLeft = roundRadius,
                            bottomRight = roundRadius
                        )
                    )

                else ->
                    transformations(
                        RoundedCornersTransformation(
                            topLeft = topLeftRadius,
                            topRight = topRightRadius,
                            bottomLeft = bottomLeftRadius,
                            bottomRight = bottomRightRadius
                        )
                    )
            }
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

    private fun setSimpleImage(targetView: AppCompatImageView, data: Any) {
        targetView.load(data) {
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


    /**
     * The default supported [data] types  are:
     *
     * - [String] (treated as a [Uri])
     * - [Uri] (`android.resource`, `content`, `file`, `http`, and `https` schemes)
     * - [HttpUrl]
     * - [File]
     * - [DrawableRes] [Int]
     * - [Drawable]
     * - [Bitmap]
     * - [ByteArray]
     * - [ByteBuffer]
     */
    class Builder {
        private var data: Any? = null
        private var placeholderResId: Int = 0
        private var placeholderDrawable: Drawable? = null
        private var errorResId: Int = 0
        private var errorDrawable: Drawable? = null
        private var isCircle: Boolean = false
        private var isRoundCorner: Boolean = false
        private var roundRadius: Float = 0f
        private var topLeftRadius: Float = 0f
        private var topRightRadius: Float = 0f
        private var bottomLeftRadius: Float = 0f
        private var bottomRightRadius: Float = 0f
        private var targetView: AppCompatImageView? = null

        internal fun request(data: Any?) = apply {
            this.data = data
        }

        internal fun loadAvatar(data: Any?, gender: String = Account.userGender) = apply {
            request(data)
            isCircle = true
            placeholderResId = if (gender == Gender.Woman.value)
                R.drawable.common_default_woman
            else
                R.drawable.common_default_man
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


        internal fun isCircle(isCircle: Boolean) = apply {
            this.isCircle = isCircle
        }

        internal fun isRoundCorner(isRoundCorner: Boolean) = apply {
            this.isRoundCorner = isRoundCorner
        }

        internal fun roundRadius(roundRadius: Float) = apply {
            this.roundRadius = roundRadius
        }

        internal fun roundTopRadius(topLeftRadius: Float, topRightRadius: Float) = apply {
            this.topLeftRadius = topLeftRadius
            this.topRightRadius = topRightRadius
        }

        internal fun roundBottomRadius(bottomLeftRadius: Float, bottomRightRadius: Float) = apply {
            this.bottomLeftRadius = bottomLeftRadius
            this.bottomRightRadius = bottomRightRadius
        }

        internal fun target(targetView: AppCompatImageView) = apply {
            this.targetView = targetView
        }

        internal fun build(): ImageLoader {
            return ImageLoader(
                targetView = checkNotNull(targetView),
                data = checkRequestEffectivity(data),
                placeholderResId = placeholderResId,
                placeholderDrawable = placeholderDrawable,
                errorResId = errorResId,
                errorDrawable = errorDrawable,
                isCircle = isCircle,
                isRoundCorner = isRoundCorner,
                roundRadius = roundRadius,
                topLeftRadius = topLeftRadius,
                topRightRadius = topRightRadius,
                bottomLeftRadius = bottomLeftRadius,
                bottomRightRadius = bottomRightRadius
            )
        }

        private fun checkRequestEffectivity(requestAddress: Any?): Any {
            return when (requestAddress) {
                is String,
                is Uri,
                is HttpUrl,
                is File,
                is DrawableRes,
                is Drawable,
                is Bitmap,
                is ByteArray,
                is ByteBuffer -> requestAddress
                else -> throw  Throwable("Request address is invalided.")
            }
        }
    }
}