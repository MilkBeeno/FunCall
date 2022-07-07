package com.milk.funcall.common.imageLoad

import android.widget.ImageView
import androidx.annotation.DrawableRes
import coil.load
import com.milk.funcall.R
import java.io.File

fun ImageView.loadAvatar(
    imageUrl: String,
    @DrawableRes placeholder: Int = R.drawable.common_default_man
) {
    load(imageUrl) {
        crossfade(true)
        crossfade(200)
        placeholder(placeholder)
        error(placeholder)
    }
}

fun ImageView.loadSimple(
    imageUrl: String,
    @DrawableRes placeholder: Int = R.drawable.home_default_big
) {
    load(imageUrl) {
        crossfade(true)
        crossfade(300)
        placeholder(placeholder)
        error(placeholder)
    }
}

fun ImageView.load(@DrawableRes imageId: Int) {
    load(imageId) {
        crossfade(true)
        crossfade(200)
    }
}

fun ImageView.load(filePath: File) {
    load(filePath) {
        crossfade(true)
        crossfade(200)
    }
}