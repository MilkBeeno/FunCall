package com.milk.funcall.main.ui.adapter

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
import com.milk.funcall.main.data.HomModel
import com.milk.simple.ktx.color

class HomeAdapter : AbstractPagingAdapter<HomModel>(
    layoutId = R.layout.item_hone,
    diffCallback = object : DiffUtil.ItemCallback<HomModel>() {
        override fun areItemsTheSame(oldItem: HomModel, newItem: HomModel): Boolean {
            return oldItem.userId == newItem.userId
        }

        override fun areContentsTheSame(oldItem: HomModel, newItem: HomModel): Boolean {
            return oldItem.userName == newItem.userName
                    && oldItem.userAvatar == newItem.userAvatar
                    && oldItem.userImage == newItem.userImage
                    && oldItem.isOnline == newItem.isOnline
        }
    }
) {
    override fun convert(holder: PagingViewHolder, item: HomModel) {
        holder.setText(R.id.tvUserName, item.userName)
        holder.getView<AppCompatImageView>(R.id.ivUserImage).apply {
            val params = layoutParams
            params.height = dpToPx(context, if (item.isSmallImage) 125f else 223f).toInt()
            layoutParams = params
            loadSimple(
                item.userImage,
                if (item.isSmallImage)
                    R.drawable.home_default_small
                else
                    R.drawable.home_default_big
            )
        }
        holder.getView<AppCompatImageView>(R.id.ivUserAvatar)
            .loadAvatar(item.userAvatar)
        holder.getView<View>(R.id.vState).setBackgroundResource(
            if (item.isOnline)
                R.drawable.shape_home_online_state
            else
                R.drawable.shape_home_offline_state
        )
        holder.getView<AppCompatTextView>(R.id.tvState).apply {
            setTextColor(
                if (item.isOnline)
                    context.color(R.color.FF58FFD3)
                else
                    context.color(R.color.FFEAECF6)
            )
            setText(if (item.isOnline) R.string.home_online else R.string.home_offline)
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