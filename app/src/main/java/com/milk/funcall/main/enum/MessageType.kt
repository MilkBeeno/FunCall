package com.milk.funcall.main.enum

// 私聊消息类型枚举
enum class MessageType(val value: Int) {
    Default(0),
    TextSend(1),
    TextReceived(2)
}