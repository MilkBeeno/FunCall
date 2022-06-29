package com.milk.funcall.common.net

import com.milk.funcall.common.net.host.MainHost
import com.milk.funcall.common.net.interceptor.ApiLogInterceptor
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
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(8, TimeUnit.SECONDS)
                .writeTimeout(8, TimeUnit.SECONDS)
                .addInterceptor(ApiLogInterceptor())
                .build()
        }

    fun obtainRetrofit(): Retrofit {
        if (mainRetrofit == null)
            mainRetrofit = Retrofit.Builder()
                .baseUrl(MainHost().realUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(JsonConvert.gson))
                .build()
        return checkNotNull(mainRetrofit)
    }
}