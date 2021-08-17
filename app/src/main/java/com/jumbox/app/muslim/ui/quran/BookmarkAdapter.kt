package com.jumbox.app.muslim.ui.quran

import android.view.LayoutInflater
import android.view.ViewGroup
import com.jumbox.app.muslim.R
import com.jumbox.app.muslim.base.BaseAdapter
import com.jumbox.app.muslim.databinding.LayoutItemBookmarkBinding
import com.jumbox.app.muslim.vo.Bookmark
import java.util.*


/**
 * Created by Jumadi Janjaya date on 18/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/
class BookmarkAdapter(private val callbakClick: (bookmark: Bookmark) -> Unit) : BaseAdapter<Bookmark, LayoutItemBookmarkBinding>() {
    override fun createBinding(parent: ViewGroup, viewType: Int): LayoutItemBookmarkBinding {
        return LayoutItemBookmarkBinding.bind(LayoutInflater.from(parent.context).inflate(R.layout.layout_item_bookmark, parent, false))
    }

    override fun areItemsTheFilter(query: CharSequence, item: Bookmark): Boolean {
        return item.name.toLowerCase(Locale.getDefault()).contains(query.toString().toLowerCase(
                Locale.getDefault()))
    }
    fun removeItem(position: Int) {
        currentList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun restoreItem(item: Bookmark?, position: Int) {
        if (item != null) {
            currentList.add(position, item)
            notifyItemInserted(position)
        }
    }

    override fun bind(binding: LayoutItemBookmarkBinding, item: Bookmark?) {
        binding.bookmark = item
        item?.let { bookmark ->
            binding.root.setOnClickListener {
                callbakClick.invoke(bookmark)
            }
        }
    }
}