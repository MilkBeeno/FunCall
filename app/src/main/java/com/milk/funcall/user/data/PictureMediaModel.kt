package com.milk.funcall.user.data

import java.io.Serializable

data class PictureMediaModel(
    var targetId: Long = 0,
    var position: Int = 0,
    var isBlacked: Boolean = false,
    var imageUnlocked: Boolean = false,
    var unlockMethod: Int = 0,
    var remainUnlockCount: Int = 0,
    var pictureUrls: MutableList<String> = mutableListOf()
) : Serializable