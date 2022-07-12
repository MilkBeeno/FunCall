package com.milk.funcall.user.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.milk.funcall.R
import com.milk.funcall.common.media.loader.ImageLoader
import com.milk.funcall.user.data.UserMediaModel

class ImageMediaAdapter(private val imageList: MutableList<UserMediaModel>) :
    RecyclerView.Adapter<ImageMediaAdapter.ImageMediaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageMediaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image_media, parent, false)
        return ImageMediaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageMediaViewHolder, position: Int) {
        val targetView = holder.itemView as AppCompatImageView
        ImageLoader.Builder()
            .request(imageList[position].thumbUrl)
            .target(targetView)
            .placeholder(R.drawable.common_list_default_medium)
            .build()
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    class ImageMediaViewHolder(view: View) : RecyclerView.ViewHolder(view)
}