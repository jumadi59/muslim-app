package com.jumbox.app.muslim.ui.quran

import android.graphics.Color
import android.os.Bundle
import androidx.core.widget.NestedScrollView
import com.jumbox.app.muslim.R
import com.jumbox.app.muslim.base.BaseBottomSheetDialogFragment
import com.jumbox.app.muslim.databinding.FragmentBottomSheetTafsirBinding
import com.jumbox.app.muslim.vo.Surah
import com.jumbox.app.muslim.vo.Verse
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject
import android.util.TypedValue




/**
 * Created by Jumadi Janjaya date on 16/05/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/
class BottomSheetTafsir(private val surah: Surah, private val verse: Verse) : BaseBottomSheetDialogFragment<FragmentBottomSheetTafsirBinding>(), HasAndroidInjector {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Any>
    private val viewModel: QuranViewModel by viewModel(QuranViewModel::class)

    override fun getLayoutId() = R.layout.fragment_bottom_sheet_tafsir

    override fun initView() {
        binding.surah = surah
        viewModel.tafsir(surah.number)
        binding.nestedScrollView.setOnScrollChangeListener { _: NestedScrollView?, _: Int, scrollY: Int, _: Int, _: Int ->
            if (scrollY == 0)
                binding.toolbar.setBackgroundColor(Color.TRANSPARENT)
            else {
                val value = TypedValue()
                requireActivity().theme.resolveAttribute(R.attr.backgroundColor, value, true)
                binding.toolbar.setBackgroundResource(value.data)
            }
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        viewModel.responseTafsir.observe(this) {
            val text = "${it.name} (${it.source})"
            binding.tvSource.text = text
            binding.tvSource.isSelected = true
            binding.tafsir = it.list.find { tafsir -> tafsir.number == verse.number }
        }
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return activityInjector
    }
}