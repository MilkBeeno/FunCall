package com.milk.funcall.chat.status

/** 私聊消息发送状态 */
enum class ChatMsgSendStatus(val value: Int) {
    // 消息发送中
    Sending(1),

    // 消息发送失败
    SendFailed(2),

    // 消息发送成功
    SendSuccess(3)
}