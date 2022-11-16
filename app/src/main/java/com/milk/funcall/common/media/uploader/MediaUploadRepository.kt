package com.milk.funcall.common.media.uploader

import com.milk.funcall.common.net.ApiClient
import com.milk.funcall.common.net.retrofit
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class MediaUploadRepository {
    private val mediaUploadApiService: MediaUploadApiService =
        ApiClient.getUploadRetrofit().create(MediaUploadApiService::class.java)

    /** 图片上传、单图上传 */
    internal suspend fun uploadSinglePicture(filePath: String) = retrofit {
        // 1.创建MultipartBody.Builder对象
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        // 2.获取图片，创建请求体
        val file = File(filePath)
        // 3.调用MultipartBody.Builder的addFormDataPart()方法添加表单数据
        val body = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        // 4.添加图片数据，body创建的请求体
        builder.addFormDataPart("file", file.name, body)
        /* 5.创建List<MultipartBody.Part> 集合，
             调用MultipartBody.Builder的build()方法会返回一个新创建的MultipartBody
             再调用MultipartBody的parts()方法返回MultipartBody.Part集合 */
        val parts = builder.build().parts
        // 6.最后进行HTTP请求，传入parts
        mediaUploadApiService.uploadSinglePicture(parts)
    }

    /** 图片上传、多图上传 */
    internal suspend fun uploadMultiplePicture(filePathList: MutableList<String>) = retrofit {
        val parts = mutableListOf<MultipartBody.Part>()
        filePathList.forEach { filePath ->
            val file = File(filePath)
            val requestFile =
                file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val body = MultipartBody.Part
                .createFormData("fileList", file.name, requestFile)
            parts.add(body)
        }
        mediaUploadApiService.uploadMultiplePicture(parts)
    }

    /** 视频上传、单个上传 */
    internal suspend fun uploadSingleVideo(filePath: String) = retrofit {
        val file = File(filePath)
        val body = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("file", file.name, body)
        mediaUploadApiService.uploadSingleVideo(part)
    }
}