package com.milk.funcall.chat.api

import com.milk.funcall.chat.data.ChatMsgReceiveModel
import com.milk.funcall.chat.data.ChatMsgSentTextModel
import com.milk.funcall.chat.data.HeartBeatModel
import com.milk.funcall.common.response.ApiResponse
import retrofit2.http.*

interface ChatApiService {
    @GET("/funcall/heartBeat")
    suspend fun heartBeat(): ApiResponse<HeartBeatModel>

    @GET("/funcall/listChatNewMessage")
    suspend fun getMessagesFromNetwork(): ApiResponse<MutableList<ChatMsgReceiveModel>>

    @FormUrlEncoded
    @Headers("Encoded:false")
    @POST("/funcall/sendMsg")
    suspend fun sendTextChatMessage(
        @Field("faceUserId") targetId: Long,
        @Field("content") content: String
    ): ApiResponse<ChatMsgSentTextModel>
}