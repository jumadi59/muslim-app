package com.jumbox.app.muslim.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import com.jumbox.app.muslim.R
import com.jumbox.app.muslim.base.BaseAdapter
import com.jumbox.app.muslim.databinding.LayoutItemSuggestionBinding
import com.jumbox.app.muslim.vo.Suggestion
import java.util.*

/**
 * Created by Jumadi Janjaya date on 07/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/

class SuggestionAdapter(private val callbackClick: (id: Suggestion) -> Unit) : BaseAdapter<Suggestion, LayoutItemSuggestionBinding>() {

    override fun createBinding(parent: ViewGroup, viewType: Int): LayoutItemSuggestionBinding {
        return LayoutItemSuggestionBinding.bind(LayoutInflater.from(parent.context).inflate(R.layout.layout_item_suggestion, parent, false))
    }

    override fun bind(binding: LayoutItemSuggestionBinding, item: Suggestion?) {
        item?.let { suggestion ->
            binding.tvTextView.text = suggestion.name
            binding.root.setOnClickListener {
                callbackClick.invoke(suggestion)
            }
        }
    }

    override fun areItemsTheFilter(query: CharSequence, item: Suggestion): Boolean {
        return item.name.toLowerCase(Locale.getDefault()).contains(query.toString().toLowerCase(Locale.getDefault()))
    }
}