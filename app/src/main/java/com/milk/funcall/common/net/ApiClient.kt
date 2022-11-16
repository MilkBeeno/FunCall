package com.milk.funcall.common.net

import com.milk.funcall.common.net.host.MainHost
import com.milk.funcall.common.net.interceptor.ApiLogInterceptor
import com.milk.funcall.common.net.interceptor.ApiParamsInterceptor
import com.milk.funcall.common.net.json.JsonConvert
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private var mainRetrofit: Retrofit? = null
    private val client: OkHttpClient
        get() {
            return OkHttpClient.Builder()
                .callTimeout(15, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(ApiParamsInterceptor())
                .addInterceptor(ApiLogInterceptor())
                .build()
        }

    private var uploadRetrofit: Retrofit? = null
    private val uploadClient: OkHttpClient
        get() {
            return OkHttpClient.Builder()
                .callTimeout(45, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .addInterceptor(ApiLogInterceptor())
                .build()
        }

    internal fun getMainRetrofit(): Retrofit {
        if (mainRetrofit == null)
            mainRetrofit = Retrofit.Builder()
                .baseUrl(MainHost().realUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(JsonConvert.gson))
                .build()
        return checkNotNull(mainRetrofit)
    }

    internal fun getUploadRetrofit(): Retrofit {
        if (uploadRetrofit == null)
            uploadRetrofit = Retrofit.Builder()
                .baseUrl(MainHost().realUrl)
                .client(uploadClient)
                .addConverterFactory(GsonConverterFactory.create(JsonConvert.gson))
                .build()
        return checkNotNull(uploadRetrofit)
    }
}