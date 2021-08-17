package com.jumbox.app.muslim.ui.quran

import android.view.LayoutInflater
import android.view.ViewGroup
import com.jumbox.app.muslim.R
import com.jumbox.app.muslim.base.BaseAdapter
import com.jumbox.app.muslim.databinding.LayoutItemListSurahBinding
import com.jumbox.app.muslim.vo.Surah
import java.util.*

/**
 * Created by Jumadi Janjaya date on 10/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/
class SurahAdapter(private val callbackClick: (surah: Surah) -> Unit) : BaseAdapter<Surah, LayoutItemListSurahBinding>() {

    override fun createBinding(parent: ViewGroup, viewType: Int): LayoutItemListSurahBinding {
        return LayoutItemListSurahBinding.bind(LayoutInflater.from(parent.context).inflate(R.layout.layout_item_list_surah, parent, false))
    }

    override fun bind(binding: LayoutItemListSurahBinding, item: Surah?) {
        binding.surah = item
        binding.root.setOnClickListener {
            item?.let { surah -> callbackClick.invoke(surah) }
        }
    }

    override fun areItemsTheFilter(query: CharSequence, item: Surah): Boolean {
        return item.name.toLowerCase(Locale.getDefault()).contains(query.toString().toLowerCase(Locale.getDefault()))
    }
}