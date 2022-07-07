package com.milk.funcall.user.ui.adapter

import android.content.Context
import android.util.TypedValue
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
import com.milk.funcall.R
import com.milk.funcall.common.imageLoad.loadAvatar
import com.milk.funcall.common.imageLoad.loadSimple
import com.milk.funcall.common.paging.AbstractPagingAdapter
import com.milk.funcall.common.paging.FooterLoadStateAdapter
import com.milk.funcall.common.paging.PagingViewHolder
import com.milk.funcall.user.data.UserSimpleInfoModel
import com.milk.funcall.user.type.OnlineState
import com.milk.simple.ktx.*

class HomeAdapter : AbstractPagingAdapter<UserSimpleInfoModel>(
    layoutId = R.layout.item_hone,
    diffCallback = object : DiffUtil.ItemCallback<UserSimpleInfoModel>() {
        override fun areItemsTheSame(
            oldItem: UserSimpleInfoModel,
            newItem: UserSimpleInfoModel
        ): Boolean {
            return oldItem.userId == newItem.userId
        }

        override fun areContentsTheSame(
            oldItem: UserSimpleInfoModel,
            newItem: UserSimpleInfoModel
        ) = false
    }
) {
    override fun convert(holder: PagingViewHolder, item: UserSimpleInfoModel) {
        val isOnline = item.userOnline == OnlineState.Online.value
        holder.setText(R.id.tvUserName, item.userName)
        holder.getView<AppCompatImageView>(R.id.ivUserImage).apply {
            val params = layoutParams
            params.height = dpToPx(context, if (item.isMediumImage) 125f else 223f).toInt()
            layoutParams = params
            loadSimple(
                item.userImage,
                if (item.isMediumImage)
                    R.drawable.home_default_small
                else
                    R.drawable.home_default_big
            )
        }
        holder.getView<AppCompatImageView>(R.id.ivUserAvatar)
            .loadAvatar(item.userAvatar)
        holder.getView<View>(R.id.vState).setBackgroundResource(
            if (isOnline) R.drawable.shape_home_online_state else R.drawable.shape_home_offline_state
        )
        holder.getView<AppCompatTextView>(R.id.tvState).apply {
            setTextColor(
                if (isOnline) context.color(R.color.FF58FFD3) else context.color(R.color.FFEAECF6)
            )
            setText(if (isOnline) R.string.home_online else R.string.home_offline)
        }
    }

    override fun obtainFooterAdapter(): FooterLoadStateAdapter {
        return FooterLoadStateAdapter(
            footLayoutId = R.layout.layout_paging_foot,
            pageSize = 8,
            hasHeader = pairHeaderAndFooter.first
        ) { rootView, state ->
            val textView = rootView.findViewById<AppCompatTextView>(R.id.tvFooter)
            when (state) {
                FooterLoadStateAdapter.LoadMoreState.Error -> {
                    rootView.gone()
                    rootView.context.showToast(
                        rootView.context.string(R.string.common_list_load_more_error)
                    )
                }
                FooterLoadStateAdapter.LoadMoreState.Loading -> {
                    rootView.visible()
                    textView.text = rootView.context.string(R.string.common_list_loading)
                }
                FooterLoadStateAdapter.LoadMoreState.NoMoreData -> {
                    rootView.visible()
                    textView.text = rootView.context.string(R.string.common_list_no_more_data)
                }
            }
        }
    }

    private fun dpToPx(context: Context, value: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value,
            context.resources.displayMetrics
        )
    }
}