package com.jumbox.app.muslim.ui.quran

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.jumbox.app.muslim.R
import com.jumbox.app.muslim.base.BaseBottomSheetDialogFragment
import com.jumbox.app.muslim.databinding.FragmentBottomSheetSearchBinding
import com.jumbox.app.muslim.utils.textChangedListener
import com.jumbox.app.muslim.vo.Surah
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

/**
 * Created by Jumadi Janjaya date on 11/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/
class BottomSheetListSurah(private val callbackClick: (surah: Surah) -> Unit) : BaseBottomSheetDialogFragment<FragmentBottomSheetSearchBinding>(), HasAndroidInjector {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Any>
    private val viewModel: QuranViewModel by viewModel(QuranViewModel::class)

    private val listSurahAdapter: SurahAdapter by lazy { SurahAdapter {
        callbackClick.invoke(it)
        dismiss()
    }
    }
    override fun getLayoutId() = R.layout.fragment_bottom_sheet_search

    override fun initView() {
        binding.tvTitle.setText(R.string.title_search_surah)
        binding.actionClose.setOnClickListener { dismiss() }

        binding.rvList.let {
            it.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            val dividerItemDecoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            dividerItemDecoration.setDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.divider)!!)
            it.addItemDecoration(dividerItemDecoration)
            it.adapter = listSurahAdapter
        }
        binding.etSearch.setHint(R.string.search_surah)
        binding.etSearch.textChangedListener {
            if (!it.isNullOrEmpty()) {
                listSurahAdapter.search(it)
            } else {
                listSurahAdapter.reload()
            }
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateInput()
                true
            } else {
                false
            }
        }

        binding.etSearch.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateInput()
                true
            } else if(event.action == KeyEvent.KEYCODE_BACK) {
                binding.etSearch.clearFocus()
                true
            } else {
                false
            }
        }
    }

    private fun updateInput() {
        binding.etSearch.text.let {
            if (!it.isNullOrEmpty()) {
                listSurahAdapter.search(it)
            } else {
                listSurahAdapter.reload()
            }
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        viewModel.responseListSurah.observe(this) {
            listSurahAdapter.submitList(it)
        }
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return activityInjector
    }
}