package com.milk.funcall.main.ui.vm

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.milk.funcall.common.data.ApiPagingResponse
import com.milk.funcall.common.paging.NetworkPagingSource
import com.milk.funcall.main.data.HomModel

class HomeViewModel : ViewModel() {
    val pagingSource = Pager(
        PagingConfig(
            pageSize = 12,
            prefetchDistance = 1,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            NetworkPagingSource { getData(it) }
        }
    )
    var isload = false
    private fun getData(index: Int): ApiPagingResponse<HomModel> {
        if (index==1) return ApiPagingResponse(code = 2000, message = "", data = mutableListOf())
      //  if (isload) return throw Exception("错误")
        isload = true
        //Logger.d("打印的Index是=${index}", "hlc")
        val list = mutableListOf<HomModel>()
        for (i in 0..2) {
            list.add(HomModel(userId = i * index.toLong(), userName = "数据是=${i * index}"))
        }
        if (index == 3) return throw Exception("错误")
        return ApiPagingResponse(code = 2000, message = "", data = list)
    }
}