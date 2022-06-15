package com.milk.funcall.common.mdr.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "ChatMessageTable",
    primaryKeys = ["userId", "messageUniqueId"],
    indices = [Index(value = ["userId", "messageUniqueId"], unique = true)]
)
data class ChatMessageEntity(
    @ColumnInfo(name = "messageUniqueId")
    var messageUniqueId: String = "",
    @ColumnInfo(name = "userId")
    var userId: Long = 0,
    @ColumnInfo(name = "targetId")
    var targetId: Long = 0,
    @ColumnInfo(name = "targetName")
    var targetName: String = "",
    @ColumnInfo(name = "targetImage")
    var targetImage: String = "",
    @ColumnInfo(name = "messageType")
    var messageType: Int = 0,
    @ColumnInfo(name = "operationTime")
    var operationTime: Long = 0,
    @ColumnInfo(name = "messageContent")
    var messageContent: String = "",
    @ColumnInfo(name = "isAcceptMessage")
    var isAcceptMessage: Boolean = false,
    @ColumnInfo(name = "isReadMessage")
    var isReadMessage: Boolean = false,
    @ColumnInfo(name = "isSendSuccess")
    var isSendSuccess: Boolean = false
)