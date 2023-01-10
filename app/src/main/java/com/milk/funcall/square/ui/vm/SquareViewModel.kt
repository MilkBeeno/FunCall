package com.milk.funcall.square.ui.vm

import androidx.lifecycle.ViewModel
import com.milk.funcall.account.Account
import com.milk.funcall.common.timer.MilkTimer
import com.milk.funcall.square.data.SquareModel
import com.milk.funcall.square.ui.repo.SquareRepository
import com.milk.funcall.user.status.Gender
import com.milk.simple.ktx.ioScope
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.*

class SquareViewModel : ViewModel() {
    internal val squareInfoFlow = MutableSharedFlow<SquareModel>()
    internal val onlineNumberFlow = MutableSharedFlow<Int>()
    private val squareRepository by lazy { SquareRepository() }

    internal fun launchTimedRefresh() {
        MilkTimer.Builder()
            .setMillisInFuture(15 * 60 * 1000L)
            .setOnFinishedListener {
                getSquareInfo()
                launchTimedRefresh()
            }
            .build()
            .start()
    }

    internal fun getSquareInfo() {
        ioScope {
            val apiResponse = squareRepository.getSquareInfo(Account.userGender)
            val apiResult = apiResponse.data
            if (apiResponse.success && apiResult != null) {
                squareInfoFlow.emit(apiResult)
                onlineNumberFlow.emit(getOnlineNumber())
            }
        }
    }

    private fun getOnlineNumber(): Int {
        val random = Random()
        return if (Account.userGender == Gender.Man.value) {
            random.nextInt(300) + 700
        } else {
            random.nextInt(100) + 200
        }
    }
}