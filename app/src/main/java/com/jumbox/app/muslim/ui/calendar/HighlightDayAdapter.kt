package com.jumbox.app.muslim.ui.calendar

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.jumbox.app.muslim.R
import com.jumbox.app.muslim.base.BaseAdapter
import com.jumbox.app.muslim.databinding.LayoutItemHighlightDayBinding
import com.jumbox.app.muslim.vo.HighlightDay
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

/**
 * Created by Jumadi Janjaya date on 03/05/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/
class HighlightDayAdapter : BaseAdapter<HighlightDay, LayoutItemHighlightDayBinding>() {

    private var currentFocus = -2

    override fun createBinding(parent: ViewGroup, viewType: Int): LayoutItemHighlightDayBinding {
        return LayoutItemHighlightDayBinding.bind(LayoutInflater.from(parent.context).inflate(R.layout.layout_item_highlight_day, parent, false))
    }

    fun setCurrentFocus(localDate: LocalDate) : HighlightDay? {
        val old = currentFocus
        val highlightDay = currentList.find { it.localDate == localDate }
        currentFocus = if (highlightDay != null) getPosition(highlightDay) else -2

        if (currentFocus >= 0)
            notifyItemChanged(currentFocus)
        if (old >= 0)
            notifyItemChanged(old)

        return highlightDay
    }

    override fun bind(binding: LayoutItemHighlightDayBinding, item: HighlightDay?) {
        val context = binding.root.context
        binding.highlightDay = item

        binding.tvDay.text = item?.localDate?.dayOfMonth?.toString()
        binding.tvMoon.text = item?.localDate?.month?.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        binding.layoutDay.backgroundTintList = ColorStateList.valueOf(ResourcesCompat.getColor(context.resources,
                if(currentFocus == this.getPosition(item)) R.color.green_700 else R.color.grey_light, context.theme))
        binding.tvMoon.setTextColor(ResourcesCompat.getColor(context.resources,
                if(currentFocus == this.getPosition(item)) R.color.white else R.color.green_700, context.theme))
        binding.tvDay.setTextColor(ResourcesCompat.getColor(context.resources,
                if(currentFocus == this.getPosition(item)) R.color.white else R.color.black, context.theme))
    }
}