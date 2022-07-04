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
import com.milk.funcall.user.data.HomDetailModel
import com.milk.funcall.user.type.OnlineState
import com.milk.simple.ktx.color

class HomeAdapter : AbstractPagingAdapter<HomDetailModel>(
    layoutId = R.layout.item_hone,
    diffCallback = object : DiffUtil.ItemCallback<HomDetailModel>() {
        override fun areItemsTheSame(oldItem: HomDetailModel, newItem: HomDetailModel): Boolean {
            return oldItem.userId == newItem.userId
        }

        override fun areContentsTheSame(oldItem: HomDetailModel, newItem: HomDetailModel) = false
    }
) {
    override fun convert(holder: PagingViewHolder, item: HomDetailModel) {
        val isOnline = item.onlineState == OnlineState.Online.value
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

    override fun obtainFooterAdapter(hasHeader: Boolean): FooterLoadStateAdapter {
        return FooterLoadStateAdapter(R.layout.layout_paging_foot, hasHeader)
    }

    private fun dpToPx(context: Context, value: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value,
            context.resources.displayMetrics
        )
    }
}