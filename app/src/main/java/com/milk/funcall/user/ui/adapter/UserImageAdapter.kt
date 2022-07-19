package com.milk.funcall.user.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.milk.funcall.R
import com.milk.funcall.common.media.loader.ImageLoader

class UserImageAdapter(
    private val imageList: MutableList<String>,
    private val clickRequest: (Int) -> Unit = {}
) : RecyclerView.Adapter<UserImageAdapter.ImagesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_info_image, parent, false)
        return ImagesViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {
        val targetView =
            holder.itemView.findViewById<ShapeableImageView>(R.id.ivUserImage)
        ImageLoader.Builder()
            .request(imageList[position])
            .target(targetView)
            .placeholder(R.drawable.common_list_default_medium)
            .build()
        holder.itemView.setOnClickListener { clickRequest(position) }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    inner class ImagesViewHolder(view: View) : RecyclerView.ViewHolder(view)
}