package com.milk.funcall.common.timer

import android.os.CountDownTimer
import com.milk.simple.ktx.mainScope

class MilkTimer(private val builder: Builder) {

    private var milkCountDownTimer: MilkCountDownTimer? = null
    private var timeLeft: Long = 0

    internal fun start() {
        if (milkCountDownTimer == null) {
            milkCountDownTimer = MilkCountDownTimer()
        }
        milkCountDownTimer?.start()
    }

    internal fun finish() {
        mainScope { milkCountDownTimer?.onFinish() }
    }

    private inner class MilkCountDownTimer :
        CountDownTimer(builder.millisInFuture, builder.countDownInterval) {
        override fun onTick(millisUntilFinished: Long) {
            timeLeft = millisUntilFinished
            builder.onTickListener?.invoke(this@MilkTimer, millisUntilFinished)
        }

        override fun onFinish() {
            timeLeft = 0
            builder.onFinishedListener?.invoke()
            milkCountDownTimer?.cancel()
            milkCountDownTimer = null
        }
    }

    class Builder {
        internal var millisInFuture: Long = 0
        internal var countDownInterval: Long = 1000
        internal var onTickListener: ((MilkTimer, Long) -> Unit)? = null
        internal var onFinishedListener: (() -> Unit)? = null

        internal fun setMillisInFuture(millisInFuture: Long): Builder {
            this.millisInFuture = millisInFuture
            return this
        }

        internal fun setCountDownInterval(countDownInterval: Long): Builder {
            this.countDownInterval = countDownInterval
            return this
        }

        internal fun setOnTickListener(onTickListener: (MilkTimer, Long) -> Unit): Builder {
            this.onTickListener = onTickListener
            return this
        }

        internal fun setOnFinishedListener(onFinishedListener: () -> Unit): Builder {
            this.onFinishedListener = onFinishedListener
            return this
        }

        internal fun build(): MilkTimer {
            return MilkTimer(this)
        }
    }
}