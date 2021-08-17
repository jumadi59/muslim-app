package com.jumbox.app.muslim.ui.prayer

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import com.jumbox.app.muslim.R
import com.jumbox.app.muslim.base.BaseAdapter
import com.jumbox.app.muslim.binding.Binding
import com.jumbox.app.muslim.databinding.LayoutItemJadwalBinding
import com.jumbox.app.muslim.utils.nameResource
import com.jumbox.app.muslim.vo.Prayer

/**
 * Created by Jumadi Janjaya date on 08/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/
class PrayAdapter(private val notifyCallback: (prayer: Prayer) -> Unit) : BaseAdapter<Prayer, LayoutItemJadwalBinding>() {

    var currentIndex = -2

    override fun createBinding(parent: ViewGroup, viewType: Int): LayoutItemJadwalBinding {
        return LayoutItemJadwalBinding.bind(LayoutInflater.from(parent.context).inflate(R.layout.layout_item_jadwal, parent, false))
    }

    fun update(position: Int, item: Prayer) {
        currentList.removeAt(position)
        currentList.add(position, item)
        notifyDataSetChanged()
    }

    override fun bind(binding: LayoutItemJadwalBinding, item: Prayer?) {
        val context = binding.root.context
        item?.let {
            binding.tvName.text = context.getString(it.name.nameResource(R.string::class.java))
            Binding.dateTimeFormat(binding.tvTime, it.time)
            if (it.alarm)
                binding.btnNotification.setImageResource(if (it.type == "notify") R.drawable.ic_baseline_notifications_24 else R.drawable.ic_baseline_volume_up_24)
            else binding.btnNotification.setImageResource(if (it.type == "notify") R.drawable.ic_baseline_notifications_off_24 else R.drawable.ic_baseline_volume_off_24)

            if (currentIndex == getPosition(it)) {
                binding.root.setBackgroundResource(R.drawable.background_round)
                binding.root.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#30000000"))
                val size = binding.root.context.resources.getDimension(R.dimen.tv_big2)
                binding.tvName.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
                binding.tvTime.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
            } else {
                binding.root.background = null
                binding.root.backgroundTintList = null
                val size = binding.root.context.resources.getDimension(R.dimen.tv_big)
                binding.tvName.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
                binding.tvTime.setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
            }

            binding.btnNotification.setOnClickListener {
                notifyCallback.invoke(item)
                if (item.alarm)
                    binding.btnNotification.setImageResource(if (item.type == "notify") R.drawable.ic_baseline_notifications_off_24 else R.drawable.ic_baseline_volume_off_24)
                else binding.btnNotification.setImageResource(if (item.type == "notify") R.drawable.ic_baseline_notifications_24 else R.drawable.ic_baseline_volume_up_24)
            }
        }
    }

    override fun areItemsTheFilter(query: CharSequence, item: Prayer): Boolean {
        return false
    }
}