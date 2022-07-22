package com.milk.funcall.user.ui.adapter

import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.DiffUtil
import com.milk.funcall.R
import com.milk.funcall.common.media.loader.ImageLoader
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
            return oldItem.targetId == newItem.targetId
        }

        override fun areContentsTheSame(
            oldItem: UserSimpleInfoModel,
            newItem: UserSimpleInfoModel
        ) = false
    }
) {
    override fun convert(holder: PagingViewHolder, item: UserSimpleInfoModel) {
        val isOnline = item.targetOnline == OnlineState.Online.value
        holder.setText(R.id.tvUserName, item.targetName)
        holder.getView<AppCompatImageView>(R.id.ivUserImage).apply {
            val params = layoutParams
            params.height = context.dp2px(if (item.isMediumImage) 125f else 223f).toInt()
            layoutParams = params
            ImageLoader.Builder()
                .request(item.targetImage)
                .placeholder(
                    if (item.isMediumImage)
                        R.drawable.common_list_default_small
                    else
                        R.drawable.common_list_default_big
                )
                .target(this)
                .build()
        }
        ImageLoader.Builder()
            .loadAvatar(item.targetAvatar, item.targetGender)
            .target(holder.getView(R.id.ivUserAvatar))
            .build()
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

    override fun createFooterAdapter(): FooterLoadStateAdapter {
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
}