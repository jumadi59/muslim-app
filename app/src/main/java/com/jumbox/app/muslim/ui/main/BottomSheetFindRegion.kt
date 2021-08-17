package com.jumbox.app.muslim.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.jumbox.app.muslim.R
import com.jumbox.app.muslim.base.BaseBottomSheetDialogFragment
import com.jumbox.app.muslim.databinding.FragmentBottomSheetSearchBinding
import com.jumbox.app.muslim.utils.hideInput
import com.jumbox.app.muslim.utils.textChangedListener
import com.jumbox.app.muslim.vo.Suggestion
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * Created by Jumadi Janjaya date on 07/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/
class BottomSheetFindRegion(private val callback: (suggestion: Suggestion) -> Unit) :
    BaseBottomSheetDialogFragment<FragmentBottomSheetSearchBinding>(), HasAndroidInjector {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Any>
    private val viewModel: MainViewModel by viewModel(MainViewModel::class)

    private val suggestionAdapter: SuggestionAdapter by lazy { SuggestionAdapter {
        callback.invoke(it)
        dismiss()
    }
    }
    override fun getLayoutId() = R.layout.fragment_bottom_sheet_search

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initView() {
        binding.tvTitle.setText(R.string.title_search_location)
        binding.actionClose.setOnClickListener { dismiss() }

        binding.rvList.let {
            it.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            val dividerItemDecoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            dividerItemDecoration.setDrawable(AppCompatResources.getDrawable(requireContext(), R.drawable.divider)!!)
            it.addItemDecoration(dividerItemDecoration)
            it.adapter = suggestionAdapter
        }
        binding.etSearch.setHint(R.string.search_city)
        binding.etSearch.textChangedListener {
            if (!it.isNullOrEmpty()) {
                suggestionAdapter.search(it)
            } else {
                suggestionAdapter.reload()
            }
        }

        binding.etSearch.setOnEditorActionListener { view, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                binding.etSearch.hideInput(view.windowToken)
                updateInput()
                true
            } else {
                false
            }
        }

        binding.etSearch.setOnKeyListener { view, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                binding.etSearch.hideInput(view.windowToken)
               updateInput()
                true
            } else {
                false
            }
        }
    }

    private fun updateInput() {
        binding.etSearch.text.let {
            if (!it.isNullOrEmpty()) {
                suggestionAdapter.search(it)
            } else {
                suggestionAdapter.reload()
            }
        }
    }

    override fun initData(savedInstanceState: Bundle?) {

        viewModel.responseRegions.observe(this) {
            suggestionAdapter.submitList(it)
        }
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return activityInjector
    }
}