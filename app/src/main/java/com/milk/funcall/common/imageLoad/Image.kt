package com.milk.funcall.common.imageLoad

import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.Px
import coil.load
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.milk.funcall.R
import java.io.File

fun ImageView.loadCirclePicture(
    imageUrl: String,
    @DrawableRes placeholder: Int = R.drawable.common_default_man
) = load(imageUrl = imageUrl, circlePicture = true, placeholder = placeholder)

fun ImageView.loadRoundPicture(
    imageUrl: String,
    @Px round: Float = 0f,
    @DrawableRes placeholder: Int = R.drawable.home_default
) = load(
    imageUrl = imageUrl,
    topLeft = round,
    topRight = round,
    bottomLeft = round,
    bottomRight = round,
    placeholder = placeholder
)

fun ImageView.loadRoundPicture(
    imageUrl: String,
    @Px topLeft: Float = 0f,
    @Px topRight: Float = 0f,
    @Px bottomLeft: Float = 0f,
    @Px bottomRight: Float = 0f,
    @DrawableRes placeholder: Int = R.drawable.home_default
) = load(
    imageUrl = imageUrl,
    topLeft = topLeft,
    topRight = topRight,
    bottomLeft = bottomLeft,
    bottomRight = bottomRight,
    placeholder = placeholder
)

fun ImageView.load(
    imageUrl: String,
    circlePicture: Boolean = false,
    @Px topLeft: Float = 0f,
    @Px topRight: Float = 0f,
    @Px bottomLeft: Float = 0f,
    @Px bottomRight: Float = 0f,
    @DrawableRes placeholder: Int
) {
    load(imageUrl) {
        crossfade(true)
        crossfade(500)
        placeholder(placeholder)
        error(placeholder)
        if (circlePicture)
            transformations(CircleCropTransformation())
        else
            transformations(
                RoundedCornersTransformation(
                    topLeft,
                    topRight,
                    bottomLeft,
                    bottomRight
                )
            )
    }
}

fun ImageView.load(
    @DrawableRes imageId: Int,
    circlePicture: Boolean = false,
    @Px topLeft: Float = 0f,
    @Px topRight: Float = 0f,
    @Px bottomLeft: Float = 0f,
    @Px bottomRight: Float = 0f
) {
    load(imageId) {
        crossfade(true)
        crossfade(500)
        if (circlePicture)
            transformations(CircleCropTransformation())
        else
            transformations(
                RoundedCornersTransformation(
                    topLeft,
                    topRight,
                    bottomLeft,
                    bottomRight
                )
            )
    }
}

fun ImageView.load(
    filePath: File,
    circlePicture: Boolean = false,
    @Px topLeft: Float = 0f,
    @Px topRight: Float = 0f,
    @Px bottomLeft: Float = 0f,
    @Px bottomRight: Float = 0f
) {
    load(filePath) {
        crossfade(true)
        crossfade(500)
        if (circlePicture)
            transformations(CircleCropTransformation())
        else
            transformations(
                RoundedCornersTransformation(
                    topLeft,
                    topRight,
                    bottomLeft,
                    bottomRight
                )
            )
    }
}