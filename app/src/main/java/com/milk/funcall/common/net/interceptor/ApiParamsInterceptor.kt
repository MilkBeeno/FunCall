package com.milk.funcall.common.net.interceptor

import com.milk.funcall.account.Account
import com.milk.funcall.common.net.json.JsonConvert
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

class ApiParamsInterceptor : Interceptor {
    private val postModel = "POST"
    private val getModel = "GET"
    private val jsonContentType by lazy { "application/json; charset=utf-8".toMediaTypeOrNull() }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBuilder = request.newBuilder()
        val urlBuilder = request.url.newBuilder()
        when (request.method) {
            getModel -> {
                //val httpUrl = urlBuilder.build()
                //urlBuilder.addEncodedQueryParameter("","")
                requestBuilder.url(urlBuilder.build())
                if (Account.accessToken.isNotBlank())
                    requestBuilder.addHeader("X-Access-Token", Account.accessToken)
            }
            postModel -> {
                val requestBody = request.body
                val paramsMap = mutableMapOf<String, String>()
                if (requestBody is FormBody) {
                    for (index in 0 until requestBody.size) {
                        paramsMap[requestBody.encodedName(index)] =
                            requestBody.encodedValue(index)
                    }
                }
                val jsonParams = JsonConvert.toJson(paramsMap)
                requestBuilder.post(jsonParams.toRequestBody(jsonContentType))
            }
        }
        return chain.proceed(requestBuilder.build())
    }
}