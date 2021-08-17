package com.jumbox.app.muslim.ui.asmaulhusna

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import com.jumbox.app.muslim.R
import com.jumbox.app.muslim.base.BaseAdapter
import com.jumbox.app.muslim.databinding.LayoutItemNamesBinding
import com.jumbox.app.muslim.vo.NameAllah


/**
 * Created by Jumadi Janjaya date on 11/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/
class StackCardAdapter : BaseAdapter<NameAllah, LayoutItemNamesBinding>() {

    private val colors = arrayOf("#6f4ba9", "#ff5231", "#0a9de1", "#8e2680", "#0f0c29", "#48c900")

    override fun createBinding(parent: ViewGroup, viewType: Int): LayoutItemNamesBinding {
        return LayoutItemNamesBinding.bind(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_item_names,
                parent,
                false
            )
        )
    }

    override fun bind(binding: LayoutItemNamesBinding, item: NameAllah?) {
        val color = Color.parseColor(colors[getPosition(item) % colors.size])

        binding.cardFront.setCardBackgroundColor(ColorStateList.valueOf(color))
        binding.tintBackgroundCard.backgroundTintList =  ColorStateList.valueOf(color)
        binding.names = item
        item?.let {
            val numberOf = "${it.index} of $itemCount"
            binding.tvNumberOf.text = numberOf
        }
    }


    override fun areItemsTheFilter(query: CharSequence, item: NameAllah): Boolean {
        return false
    }
}