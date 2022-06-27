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
        //if (index==1) return ApiPagingResponse(code = 2000, message = "", data = mutableListOf())
        //  if (isload) return throw Exception("错误")
        // isload = true
        //Logger.d("打印的Index是=${index}", "hlc")
        val list = mutableListOf<HomModel>()
        for (i in 0..20) {
            list.add(
                HomModel(
                    userId = (i * index).toLong(),
                    userName = "用户名字是${(i * index)}Hello word！",
                    userAvatar = "https://cdn.pixabay.com/photo/2018/04/19/21/17/panda-3334356_960_720.jpg",
                    userImage = "https://cdn.pixabay.com/photo/2017/04/24/17/13/frog-2257133__340.jpg",
                    isOnline = index < 5,
                    isSmallImage = (i * index) % 2 == 0
                )
            )
        }
        //if (index == 3) return throw Exception("错误")
        return ApiPagingResponse(code = 2000, message = "", data = list)
    }
}