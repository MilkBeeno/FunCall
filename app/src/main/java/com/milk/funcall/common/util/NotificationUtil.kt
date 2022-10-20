package com.milk.funcall.common.util

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.milk.funcall.R
import com.milk.funcall.user.ui.act.UserInfoActivity
import com.milk.simple.log.Logger

object NotificationUtil {
    private const val CHANNEL_ID = "Funcall100"
    private const val CHANNEL_NAME = "Fun Call User Message"

    internal fun isEnabled(context: Context): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    internal fun getPermission(context: Context) {
        val intent = Intent()
        intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
        intent.putExtra("app_package", context.packageName)
        intent.putExtra("app_uid", context.applicationInfo.uid)
        intent.putExtra("android.provider.extra.APP_PACKAGE", context.applicationInfo.uid)
        context.startActivity(intent)
    }

    internal fun toSystemSetting(context: Context) {
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
        intent.data = Uri.fromParts("package", context.packageName, null)
        context.startActivity(intent)
    }

    @SuppressLint("UnspecifiedImmutableFlag", "InlinedApi")
    internal fun show(
        context: Context,
        userId: String,
        title: String,
        content: String,
        avatar: Bitmap
    ) {
        val finalChannelId = createChannel(context, CHANNEL_ID, CHANNEL_NAME)
        val intent = Intent(context, UserInfoActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        try {
            intent.putExtra("USER_ID", userId.toLong())
        } catch (e: NumberFormatException) {
            Logger.d("类型转换错误信息:${e.message}", "NotificationUtil")
            e.printStackTrace()
        }

        val flag = PendingIntent.FLAG_UPDATE_CURRENT
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, flag)
        val notification = NotificationCompat.Builder(context, finalChannelId)
            .setContentTitle(title)
            .setContentText(content)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.login_logo)
            .setLargeIcon(avatar)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setOngoing(true)
            .build()
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(100, notification)
    }

    private fun createChannel(context: Context, channelId: String, channelName: String): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
            channelId
        } else ""
    }
}