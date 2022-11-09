package com.milk.funcall.account.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import coil.util.CoilUtils
import com.milk.funcall.R
import com.milk.funcall.common.media.loader.ImageLoader

class EditProfileImageAdapter :
    RecyclerView.Adapter<EditProfileImageAdapter.EditProfileImageViewHolder>() {

    private var imageList: MutableList<String> = mutableListOf()
    private var clickListener: ((Int, String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditProfileImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_edit_profile_image, parent, false)
        return EditProfileImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: EditProfileImageViewHolder, position: Int) {
        val imageView = holder.itemView as AppCompatImageView
        val imageUrl = imageList[position]
        if (imageUrl.isNotBlank()) {
            ImageLoader.Builder()
                .request(imageUrl)
                .placeholder(R.drawable.common_list_default_medium)
                .target(imageView)
                .build()
        } else {
            imageView.setImageResource(R.drawable.common_media_add)
        }
        imageView.setOnClickListener {
            clickListener?.invoke(position, imageUrl)
        }
    }

    override fun onViewRecycled(holder: EditProfileImageViewHolder) {
        CoilUtils.dispose(holder.itemView)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    internal fun setItemOnClickListener(clickListener: ((Int, String) -> Unit)) {
        this.clickListener = clickListener
    }

    @SuppressLint("NotifyDataSetChanged")
    internal fun setNewData(images: MutableList<String>) {
        imageList.clear()
        images.forEach { imageList.add(it) }
        if (images.size < 6) imageList.add("")
        notifyDataSetChanged()
    }

    class EditProfileImageViewHolder(view: View) : RecyclerView.ViewHolder(view)
}