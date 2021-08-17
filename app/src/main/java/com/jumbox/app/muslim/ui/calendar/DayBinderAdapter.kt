package com.jumbox.app.muslim.ui.calendar

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.jumbox.app.muslim.R
import com.jumbox.app.muslim.databinding.CalendarDayBinding
import com.jumbox.app.muslim.utils.DateHijri
import com.jumbox.app.muslim.utils.gone
import com.jumbox.app.muslim.utils.setTextColorRes
import com.jumbox.app.muslim.utils.visible
import com.jumbox.app.muslim.vo.HighlightDay
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import java.time.LocalDate

/**
 * Created by Jumadi Janjaya date on 04/05/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/
class DayBinderAdapter(
        private val highlightDays: List<HighlightDay>,
        private val callbackSelect: (calendarDay: CalendarDay) -> Unit) : DayBinder<DayBinderAdapter.DayViewContainer> {

    private val today = LocalDate.now()
    var selectDay: CalendarDay? = null
    var selectDayPositionView: View? = null

    inner class DayViewContainer(val binding: CalendarDayBinding) : ViewContainer(binding.root)

    private fun findHighlightDay(hijri: DateHijri.Formatter) : Boolean {
        return highlightDays.find { hijri.day == it.hijriDay && hijri.month == it.hijriMonth } != null
    }

    fun bind(binding: CalendarDayBinding, day: CalendarDay) {
        val context = binding.root.context
        val hijri = DateHijri(day.date).writeIslamicDate()

        binding.tvDay.text = day.date.dayOfMonth.toString()
        binding.tvDateHijri.text = hijri.day.toString()

        binding.root.setOnClickListener {
            if (day.owner == DayOwner.THIS_MONTH) {
                selectDay?.let { callbackSelect.invoke(it) }
                selectDay = day
                callbackSelect.invoke(day)
            }
        }

        if (findHighlightDay(hijri))
            binding.dotHighlight.visible()
        else
            binding.dotHighlight.gone()

        if (day.owner == DayOwner.THIS_MONTH) {
            if (selectDay == null && today == day.date) {
                selectDay = day
            }

            if (today == day.date) {
                binding.tvDay.apply {
                    setTextColorRes(R.color.colorIconDark)
                }
                binding.tvDateHijri.setTextColorRes(R.color.colorIconDark)
                binding.backgroundSelect.apply {
                    imageTintList = ColorStateList.valueOf(ResourcesCompat.getColor(context.resources,R.color.grey_light, context.theme))
                    visible()
                }
            }

            if (selectDay?.date == day.date) {
                selectDayPositionView = binding.root
                binding.tvDay.apply {
                    setTextColorRes(R.color.white)
                }
                binding.tvDateHijri.setTextColorRes(R.color.white)
                binding.backgroundSelect.apply {
                    imageTintList = ColorStateList.valueOf(ResourcesCompat.getColor(context.resources,R.color.green_700, context.theme))
                    visible()
                }
            } else if (today != day.date) {
                binding.tvDay.apply {
                    setTextColorRes(R.color.colorIconDark)
                }
                binding.tvDateHijri.setTextColorRes(R.color.colorIconDark)
                binding.backgroundSelect.gone()
            }
        } else {
            binding.tvDay.apply {
                setTextColorRes(R.color.grey)
                setTypeface(typeface, Typeface.NORMAL)
            }
            binding.tvDateHijri.setTextColorRes(R.color.grey)
            binding.backgroundSelect.gone()
        }
    }

    override fun bind(container: DayViewContainer, day: CalendarDay) {
        bind(container.binding, day)
    }

    override fun create(view: View): DayViewContainer = DayViewContainer(CalendarDayBinding.bind(view))
}