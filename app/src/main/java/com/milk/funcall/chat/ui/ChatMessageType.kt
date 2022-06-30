package com.milk.funcall.chat.ui

// 私聊消息类型枚举
enum class ChatMessageType(val value: Int) {
    TextSend(1),
    TextReceived(2)
}