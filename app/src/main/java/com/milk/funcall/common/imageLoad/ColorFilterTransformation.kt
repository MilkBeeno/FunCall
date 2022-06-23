package com.milk.funcall.common.imageLoad

import android.graphics.*
import androidx.annotation.ColorInt
import coil.size.Size
import coil.transform.Transformation

class ColorFilterTransformation(@ColorInt private val color: Int) : Transformation {
    override val cacheKey: String
        get() = "${ColorFilterTransformation::class.java.name}-$color"

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        val width = input.width
        val height = input.height
        val config = input.config
        val output = Bitmap.createBitmap(width, height, config)
        val canvas = Canvas(output)
        val paint = Paint()
        paint.isAntiAlias = true
        paint.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        canvas.drawBitmap(input, 0f, 0f, paint)
        return output
    }
}