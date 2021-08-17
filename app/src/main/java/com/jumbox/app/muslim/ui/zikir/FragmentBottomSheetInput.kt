package com.jumbox.app.muslim.ui.zikir

import android.os.Bundle
import com.jumbox.app.muslim.R
import com.jumbox.app.muslim.base.BaseBottomSheetDialogFragment
import com.jumbox.app.muslim.databinding.FragmentBottomSheetInputBinding

/**
 * Created by Jumadi Janjaya date on 03/05/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/
class FragmentBottomSheetInput(private val value: String, private val callback: (value: String) -> Unit) : BaseBottomSheetDialogFragment<FragmentBottomSheetInputBinding>() {
    override fun getLayoutId() = R.layout.fragment_bottom_sheet_input

    override fun initView() {
        binding.textInputLayout.editText!!.setText(value)
        binding.btnSave.setOnClickListener {
            callback.invoke(binding.textInputLayout.editText!!.text.toString())
            dismiss()
        }
        binding.btnCancel.setOnClickListener { dismiss() }
    }

    override fun initData(savedInstanceState: Bundle?) {}
}