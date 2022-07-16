package com.milk.funcall.common.media.uploader

import com.milk.funcall.common.data.ApiResponse
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface MediaUploadApiService {
    /**
     *  上传单张图片功能
     * - [@Multipart] 这里用 Multipart,不添加的话会引起崩溃反应
     * - [@Part] 参数注解类型为 List<MultipartBody.Part> 方便上传其它需要的参数或多张图片
     */
    @Multipart
    @POST("/funcall/uploadCDNImg")
    suspend fun uploadSinglePicture(
        @Part partLis: List<MultipartBody.Part>
    ): ApiResponse<String>

    /**
     *  上传多张图片功能
     * - [@Multipart] 这里用 Multipart,不添加的话会引起崩溃反应
     * - [@Part] 参数注解类型为 List<MultipartBody.Part> 方便上传其它需要的参数或多张图片
     */
    @Multipart
    @POST("/funcall/uploadImgList")
    suspend fun uploadMultiplePicture(
        @Part partLis: List<MultipartBody.Part>
    ): ApiResponse<MutableList<String>>
}