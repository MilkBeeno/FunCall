package com.milk.funcall.common.media.uploader

import com.milk.funcall.common.net.ApiClient

object ApiService {
    val mediaUploadApiService: MediaUploadApiService =
        ApiClient.obtainUploadRetrofit().create(MediaUploadApiService::class.java)
}