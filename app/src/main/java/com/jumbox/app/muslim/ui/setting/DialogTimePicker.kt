package com.jumbox.app.muslim.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.jumbox.app.muslim.R
import com.jumbox.app.muslim.databinding.FragmentDialogTimePickerBinding

/**
 * Created by Jumadi Janjaya date on 28/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/
class DialogTimePicker(private val current: String, private val callbackValue: (string: String) -> Unit) : DialogFragment() {

    lateinit var binding: FragmentDialogTimePickerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dialog_time_picker, container, false)
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val values = arrayOf(
            getString(R.string.minute, "-10"),
            getString(R.string.minute, "-9"),
            getString(R.string.minute, "-8"),
            getString(R.string.minute, "-7"),
            getString(R.string.minute, "-6"),
            getString(R.string.minute, "-5"),
            getString(R.string.minute, "-4"),
            getString(R.string.minute, "-3"),
            getString(R.string.minute, "-2"),
            getString(R.string.minute, "-1"),
            getString(R.string.minute, "0"),
            getString(R.string.minute, "+1"),
            getString(R.string.minute, "+2"),
            getString(R.string.minute, "+3"),
            getString(R.string.minute, "+4"),
            getString(R.string.minute, "+5"),
            getString(R.string.minute, "+6"),
            getString(R.string.minute, "+7"),
            getString(R.string.minute, "+8"),
            getString(R.string.minute, "+9"),
            getString(R.string.minute, "+10"),
        )
        binding.picker.refreshByNewDisplayedValues(values)
        binding.picker.value = values.indexOf(values.find { it.split(" ")[0] == current })

        binding.btnOk.setOnClickListener {
            callbackValue.invoke(binding.picker.displayedValues[binding.picker.value])
            dismiss()
        }
    }
}