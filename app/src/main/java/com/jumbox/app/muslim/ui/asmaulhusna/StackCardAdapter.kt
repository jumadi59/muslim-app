package com.jumbox.app.muslim.ui.asmaulhusna

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.jumbox.app.muslim.R
import com.jumbox.app.muslim.base.BaseAdapter
import com.jumbox.app.muslim.databinding.LayoutItemNamesBinding
import com.jumbox.app.muslim.utils.setTextAsBitmap
import com.jumbox.app.muslim.vo.NameAllah


/**
 * Created by Jumadi Janjaya date on 11/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/
class StackCardAdapter : BaseAdapter<NameAllah, LayoutItemNamesBinding>() {

    companion object {

        private var bitmapCaches = HashMap<Int, Bitmap>()
    }
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

        if (bitmapCaches[item?.index] == null && item?.index != null) {
            bitmapCaches[item.index] = binding.imgName.setTextAsBitmap(
                if (item.unicode.isEmpty()) item.arabic else item.unicode,
                binding.root.context.resources.getDimension(R.dimen.tv_120sp),
                ResourcesCompat.getColor(binding.root.context.resources, R.color.white, binding.root.context.theme),
                ResourcesCompat.getFont(binding.root.context, R.font.allah_names))
        } else binding.imgName.setImageBitmap(bitmapCaches[item?.index])

        item?.let {
            val numberOf = "${it.index} of $itemCount"
            binding.tvNumberOf.text = numberOf
        }
    }


    override fun areItemsTheFilter(query: CharSequence, item: NameAllah): Boolean {
        return false
    }
}