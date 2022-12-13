package com.milk.funcall.user.ui.dialog

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.milk.funcall.R
import com.milk.funcall.common.media.loader.ImageLoader
import com.milk.funcall.common.ui.dialog.SimpleDialog
import com.milk.funcall.databinding.DialogSayHiBinding
import com.milk.funcall.databinding.ItemSayHiBinding
import com.milk.funcall.user.data.SayHiModel

class SayHiDialog(activity: FragmentActivity) : SimpleDialog<DialogSayHiBinding>(activity) {
    private var clickRequest: (() -> Unit)? = null

    init {
        setGravity(Gravity.CENTER)
        setWidthMatchParent(true)
        binding.ivClose.setOnClickListener { dismiss() }
        binding.tvConfirm.setOnClickListener {
            dismiss()
            this.clickRequest?.invoke()
        }
    }

    internal fun setUserList(sayHiList: MutableList<SayHiModel>) {
        val adapter = SayHiAdapter(sayHiList)
        val layoutManager = GridLayoutManager(activity, 3)
        binding.rvPeoples.layoutManager = layoutManager
        binding.rvPeoples.adapter = adapter
    }

    internal fun setOnConfirmListener(clickRequest: () -> Unit) {
        this.clickRequest = clickRequest
    }

    override fun getViewBinding(): DialogSayHiBinding {
        return DialogSayHiBinding.inflate(LayoutInflater.from(activity))
    }

    private class SayHiAdapter(private val sayHiList: MutableList<SayHiModel>) :
        RecyclerView.Adapter<SayHiAdapter.SayHiViewHolder>() {
        private class SayHiViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SayHiViewHolder {
            val binding = ItemSayHiBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return SayHiViewHolder(binding.root)
        }

        override fun onBindViewHolder(holder: SayHiViewHolder, position: Int) {
            val sayHiModel = sayHiList[position]
            ImageLoader.Builder()
                .loadAvatar(sayHiModel.userAvatar, sayHiModel.userGender)
                .target(holder.itemView.findViewById(R.id.ivUserAvatar))
                .build()
            val title =
                holder.itemView.findViewById<AppCompatTextView>(R.id.tvUserName)
            title.text = sayHiModel.userName
        }

        override fun getItemCount(): Int {
            return sayHiList.size
        }
    }
}