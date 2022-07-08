package com.milk.funcall.user.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.milk.funcall.R
import com.milk.funcall.common.imageLoad.loadSimple
import com.milk.funcall.user.data.UserMediaModel

class UserImageAdapter(private val imageList: MutableList<UserMediaModel>) :
    RecyclerView.Adapter<UserImageAdapter.ImagesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_info_image, parent, false)
        return ImagesViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {
        holder.itemView.findViewById<ShapeableImageView>(R.id.ivUserImage)
            .loadSimple(imageList[position].url, R.drawable.user_info_image)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    inner class ImagesViewHolder(view: View) : RecyclerView.ViewHolder(view)
}