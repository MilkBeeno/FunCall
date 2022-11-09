package com.milk.funcall.user.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.milk.funcall.R
import com.milk.funcall.common.media.loader.ImageLoader

class ImageMediaAdapter : RecyclerView.Adapter<ImageMediaAdapter.ImageMediaViewHolder>() {
    private val imageList: MutableList<String> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageMediaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image_media, parent, false)
        return ImageMediaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageMediaViewHolder, position: Int) {
        val targetView = holder.itemView as AppCompatImageView
        ImageLoader.Builder()
            .request(imageList[position])
            .target(targetView)
            .placeholder(R.drawable.common_list_default_medium)
            .build()
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    internal fun setNewData(images: MutableList<String>) {
        imageList.clear()
        images.forEach { imageList.add(it) }
        notifyDataSetChanged()
    }

    internal fun removeItem(position: Int) {
        imageList.removeAt(position)
        notifyItemRemoved(position)
    }

    internal fun getItem(position: Int) = imageList[position]

    class ImageMediaViewHolder(view: View) : RecyclerView.ViewHolder(view)
}