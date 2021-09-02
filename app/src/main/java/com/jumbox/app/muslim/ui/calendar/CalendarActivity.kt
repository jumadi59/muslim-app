package com.jumbox.app.muslim.ui.calendar

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.children
import androidx.core.view.updateLayoutParams
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import com.jumbox.app.muslim.R
import com.jumbox.app.muslim.base.BaseActivity
import com.jumbox.app.muslim.databinding.ActivityCalendarBinding
import com.jumbox.app.muslim.ui.main.MainViewModel
import com.jumbox.app.muslim.utils.DateHijri
import com.jumbox.app.muslim.utils.dpToPx
import com.jumbox.app.muslim.utils.setTextColorRes
import com.jumbox.app.muslim.vo.HighlightDay
import com.kizitonwose.calendarview.model.InDateStyle
import com.kizitonwose.calendarview.utils.next
import com.kizitonwose.calendarview.utils.yearMonth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.*

class CalendarActivity : BaseActivity<ActivityCalendarBinding, MainViewModel>() {

    private val highlightDayAdapter: HighlightDayAdapter by lazy { HighlightDayAdapter() }
    private lateinit var dayBinderAdapter:  DayBinderAdapter

    private val highlightDays = arrayListOf(
        HighlightDay(1, 1, "Tahun Baru Islam", "", "", "", ""),
        HighlightDay(10, 1, "Hari Asyura (puasa Asyura)", "", "", "", ""),
        HighlightDay(12, 3, "Maulid Nabi Muhammad SAW", "", "", "", ""),
        HighlightDay(27, 7, "Isra Miraj Nabi Muhammad SAW", "", "", "", ""),
        HighlightDay(15, 8, "Malam Nishfu", "", "", "", ""),
        HighlightDay(1, 9, "Awal Ramadan", "", "", "", ""),
        HighlightDay(17, 9, "Nuzulul Qur’an", "17 Ramadan %s H", "", "", ""),
        HighlightDay(1, 10, "Hari Raya Idul Fitri", "", "", "", ""),
        HighlightDay(9, 12, "Hari Arafah (puasa Arafah)", "", "", "", ""),
        HighlightDay(10, 12, "Hari Raya Idul Adha", "", "", "", ""),
    )

    private var endAnimationSwitch = true
    private var switchModeCalendar = false
    private var switchModeView: ImageView? = null
    private val monthTitleFormatter = DateTimeFormatter.ofPattern("MMMM")

    override fun getLayoutId() = R.layout.activity_calendar

    override fun getViewModelClass() = MainViewModel::class

    override fun initView() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowTitleEnabled(false)
        }

        binding.rvList.apply {
            layoutManager = LinearLayoutManager(
                this@CalendarActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = highlightDayAdapter
        }

        initCalendar()

        binding.calendar.monthScrollListener = {
            val firstDate = it.weekDays.first().first().date
            val lastDate = it.weekDays.last().last().date
            val firstHijri = DateHijri(Date.from(firstDate.atStartOfDay(ZoneId.systemDefault()).toInstant())).writeIslamicDate()
            val lastHijri = DateHijri(Date.from(lastDate.atStartOfDay(ZoneId.systemDefault()).toInstant())).writeIslamicDate()
            if (firstHijri.year != lastHijri.year || lastHijri.month == 12) currentDate(lastDate)

            binding.tvTitle.text = if (firstDate.yearMonth == lastDate.yearMonth)
                "${monthTitleFormatter.format(firstDate)} ${firstDate.yearMonth.year}"
            else "${monthTitleFormatter.format(it.yearMonth)} ${it.yearMonth.year}"

            binding.tvSubTitle.text = when {
                firstHijri.year != lastHijri.year -> "${firstHijri.monthName} ${firstHijri.year} H - ${lastHijri.monthName} ${lastHijri.year} H"
                firstHijri.month != lastHijri.month -> "${firstHijri.monthName} - ${lastHijri.monthName} ${firstHijri.year} H"
                else -> "${firstHijri.monthName} ${firstHijri.year} H"
            }
        }

        binding.nestedScrollView.setOnScrollChangeListener { _: NestedScrollView?, _: Int, scrollY: Int, _: Int, oldScrollY: Int ->
            val rectView = Rect().apply { dayBinderAdapter.selectDayPositionView?.getGlobalVisibleRect(this) }
            val rectAppbar = Rect().apply { binding.appBar.getGlobalVisibleRect(this) }
            val rectCalendar = Rect().apply { binding.calendar.getGlobalVisibleRect(this) }

            if (scrollY == 0)
                binding.appBar.elevation = 0f.dpToPx().toFloat()
            else
                binding.appBar.elevation = 5f.dpToPx().toFloat()

            val deltaY = (scrollY - oldScrollY)
            if (rectView.top <= rectAppbar.bottom || rectCalendar.top > rectAppbar.bottom) {
                binding.calendar.top += deltaY
            }
        }
    }

    private fun initCalendar() {
        val daysOfWeek = daysOfWeekFromLocale()

        binding.weekDay.children.forEachIndexed { index, view ->
            (view as TextView).apply {
                text = daysOfWeek[index].getDisplayName(TextStyle.SHORT, Locale.getDefault())
                setTextColorRes(R.color.colorIconDark)
            }
        }

        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(10)
        val endMonth = currentMonth.plusMonths(10)
        binding.calendar.apply {
            setup(startMonth, endMonth, daysOfWeek.first())
            scrollToMonth(currentMonth)
            dayBinderAdapter = DayBinderAdapter(highlightDays) {
                notifyDayChanged(it)
                highlightDayAdapter.setCurrentFocus(it.date)
            }
            dayBinder = dayBinderAdapter
            updateMonthRangeAsync {
                val view = dayBinderAdapter.selectDayPositionView
                val lp = binding.rvList.layoutParams
                lp.height = resources.displayMetrics.heightPixels - binding.appBar.height - (view?.height?:0)
                binding.rvList.layoutParams = lp
            }
        }
        currentDate(LocalDate.now())
    }

    override fun initData(savedInstanceState: Bundle?) {}

    @SuppressLint("InflateParams")
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_calendar, menu)
        switchModeView = layoutInflater.inflate(R.layout.custom_menu_item, null) as ImageView
        menu?.findItem(R.id.action_switch_mode)?.actionView = switchModeView
        switchModeView?.let {
            it.animate().rotation(180f).setDuration(300).start()
            it.setOnClickListener {
                switchModeCalendar = !switchModeCalendar
                switchCalendar(switchModeCalendar)
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @Suppress("NAME_SHADOWING")
    fun switchCalendar(monthToWeek: Boolean) {
        switchModeView?.let {
            if (monthToWeek)
                it.animate().rotation(0f).setDuration(300).start()
            else
                it.animate().rotation(180f).setDuration(300).start()
        }

        val firstDate = binding.calendar.findFirstVisibleDay()?.date ?: return
        val lastDate = binding.calendar.findLastVisibleDay()?.date ?: return

        val oneWeekHeight = binding.calendar.daySize.height
        val oneMonthHeight = oneWeekHeight * 6

        val oldHeight = if (monthToWeek) oneMonthHeight else oneWeekHeight
        val newHeight = if (monthToWeek) oneWeekHeight else oneMonthHeight

        val animator = ValueAnimator.ofInt(oldHeight, newHeight)
        animator.addUpdateListener { animator ->
            binding.calendar.updateLayoutParams {
                height = animator.animatedValue as Int
            }
        }

        animator.doOnStart {
            endAnimationSwitch = false
            if (!monthToWeek) {
                binding.calendar.updateMonthConfiguration(
                        inDateStyle = InDateStyle.ALL_MONTHS,
                        maxRowCount = 6,
                        hasBoundaries = true
                )
            }
        }
        animator.doOnEnd {
            endAnimationSwitch = true
            if (monthToWeek) {
                binding.calendar.updateMonthConfiguration(
                        inDateStyle = InDateStyle.FIRST_MONTH,
                        maxRowCount = 1,
                        hasBoundaries = false
                )
            }

            if (monthToWeek) {
                binding.calendar.scrollToDate(dayBinderAdapter.selectDay?.date?:firstDate)
            } else {
                if (firstDate.yearMonth == lastDate.yearMonth) {
                    binding.calendar.scrollToMonth(firstDate.yearMonth)
                } else {
                    binding.calendar.scrollToMonth(minOf(firstDate.yearMonth.next, YearMonth.now().plusMonths(10)))
                }
            }
        }
        animator.duration = 250
        animator.start()
    }

    private fun currentDate(localDate: LocalDate) {
        val list = ArrayList<HighlightDay>()
        val hijri = DateHijri(localDate).writeIslamicDate()

        highlightDays.forEach {
            list.add(
                it.copy(
                    subTitle = "${it.hijriDay} ${
                        DateHijri.getDisplayName(
                            Calendar.MONTH,
                            it.hijriMonth
                        )
                    } ${hijri.year} H",
                    localDate = DateHijri.getLocalDate(hijri.copy(day = it.hijriDay, month = it.hijriMonth))
                )
            )
        }
        highlightDayAdapter.submitList(list)
        highlightDayAdapter.setCurrentFocus(localDate)
    }

    private fun daysOfWeekFromLocale(): Array<DayOfWeek> {
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        var daysOfWeek = DayOfWeek.values()
        if (firstDayOfWeek != DayOfWeek.MONDAY) {
            val rhs = daysOfWeek.sliceArray(firstDayOfWeek.ordinal..daysOfWeek.indices.last)
            val lhs = daysOfWeek.sliceArray(0 until firstDayOfWeek.ordinal)
            daysOfWeek = rhs + lhs
        }
        return daysOfWeek
    }

}