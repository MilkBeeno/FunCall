package com.milk.funcall.main.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import com.milk.funcall.main.enum.MessageType

@Entity(
    tableName = "ChatMessageTable",
    primaryKeys = ["userId", "messageUniqueId"],
    indices = [Index(value = ["userId", "messageUniqueId"], unique = true)]
)
data class ChatMessageEntity(
    @ColumnInfo(name = "messageType")
    var messageType: Int = MessageType.Default.value,
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
    @ColumnInfo(name = "operationTime")
    var operationTime: Long = 0,
    @ColumnInfo(name = "messageContent")
    var messageContent: String = "",
    @ColumnInfo(name = "isAcceptMessage")
    var isAcceptMessage: Boolean = false,
    @ColumnInfo(name = "isReadMessage")
    var isReadMessage: Boolean = false
)