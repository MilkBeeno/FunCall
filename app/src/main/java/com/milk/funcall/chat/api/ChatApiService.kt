package com.milk.funcall.chat.api

import com.milk.funcall.chat.data.ChatMsgReceiveModel
import com.milk.funcall.chat.data.ChatMsgSentTextModel
import com.milk.funcall.chat.data.HeartBeatModel
import com.milk.funcall.common.data.ApiResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ChatApiService {
    @GET("/funcall/heartBeat")
    suspend fun heartBeat(): ApiResponse<HeartBeatModel>

    @GET("/funcall/listChatNewMessage")
    suspend fun getMessagesFromNetwork(): ApiResponse<MutableList<ChatMsgReceiveModel>>

    @FormUrlEncoded
    @POST("/funcall/sendMsg")
    suspend fun sendTextChatMessage(
        @Field("faceUserId") targetId: Long,
        @Field("content") content: String
    ): ApiResponse<ChatMsgSentTextModel>
}