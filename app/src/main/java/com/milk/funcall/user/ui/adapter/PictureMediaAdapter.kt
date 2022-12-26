package com.milk.funcall.user.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.milk.funcall.R
import com.milk.funcall.account.Account
import com.milk.funcall.common.ad.AdConfig
import com.milk.funcall.common.constrant.AdCodeKey
import com.milk.funcall.common.media.loader.ImageLoader
import com.milk.funcall.user.data.PictureMediaModel
import com.milk.funcall.user.ui.view.MediaLockedLayout
import com.milk.simple.ktx.visible

class PictureMediaAdapter : RecyclerView.Adapter<PictureMediaAdapter.ImageMediaViewHolder>() {
    private var pictureMediaModel: PictureMediaModel? = null
    private val imageList: MutableList<String> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageMediaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_picture_media, parent, false)
        return ImageMediaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageMediaViewHolder, position: Int) {
        val targetView =
            holder.itemView.findViewById<AppCompatImageView>(R.id.ivUserImage)
        ImageLoader.Builder()
            .request(imageList[position])
            .target(targetView)
            .placeholder(R.drawable.common_list_default_medium)
            .build()
        val mlImage =
            holder.itemView.findViewById<MediaLockedLayout>(R.id.mlImage)
        pictureMediaModel?.let {
            if (!Account.userSubscribe && position in 2 until imageList.size) {
                val adUnitId = AdConfig.getAdvertiseUnitId(AdCodeKey.VIEW_USER_IMAGE)
                if (adUnitId.isNotBlank() && !it.imageUnlocked) {
                    mlImage.visible()
                    mlImage.setBackgroundColor()
                    mlImage.setMediaTimes(it.unlockMethod, it.remainUnlockCount)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    internal fun setNewData(pictureMediaModel: PictureMediaModel) {
        imageList.clear()
        this.pictureMediaModel = pictureMediaModel
        pictureMediaModel.pictureUrls.forEach { imageList.add(it) }
        notifyDataSetChanged()
    }

    internal fun removeItem(position: Int) {
        imageList.removeAt(position)
        notifyItemRemoved(position)
    }

    internal fun getItem(position: Int) = imageList[position]

    class ImageMediaViewHolder(view: View) : RecyclerView.ViewHolder(view)
}