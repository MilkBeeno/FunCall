package com.milk.funcall.common.net.interceptor

import com.milk.funcall.account.Account
import com.milk.funcall.common.net.json.JsonConvert
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class ApiParamsInterceptor : Interceptor {
    private val jsonContentType by lazy { "application/json; charset=utf-8".toMediaTypeOrNull() }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBuilder = request.newBuilder()
        val urlBuilder = request.url.newBuilder()
        when (request.method) {
            GET_REQUEST -> {
                requestBuilder.url(urlBuilder.build())
            }
            POST_REQUEST -> {
                val requestBody = formBoyConvert(request)
                requestBuilder.post(checkNotNull(requestBody))
            }
        }
        if (Account.userToken.isNotBlank())
            requestBuilder.addHeader("X-Access-Token", Account.userToken)
        return chain.proceed(requestBuilder.build())
    }

    private fun formBoyConvert(request: Request): RequestBody? {
        val requestBody = request.body
        if (requestBody is FormBody) {
            val isNoEncoded = request.headers["Encoded"] == "false"
            val paramsMap = mutableMapOf<String, String>()
            for (index in 0 until requestBody.size) {
                paramsMap[requestBody.encodedName(index)] =
                    if (isNoEncoded)
                        requestBody.value(index)
                    else
                        requestBody.encodedValue(index)
            }
            val jsonParams = JsonConvert.toJson(paramsMap)
            return jsonParams.toRequestBody(jsonContentType)
        }
        return requestBody
    }

    companion object {
        private const val POST_REQUEST = "POST"
        private const val GET_REQUEST = "GET"
    }
}