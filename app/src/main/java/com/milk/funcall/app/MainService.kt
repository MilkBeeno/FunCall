package com.milk.funcall.app

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class MainService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder {
        return MainBinder()
    }

    private inner class MainBinder : Binder()
}