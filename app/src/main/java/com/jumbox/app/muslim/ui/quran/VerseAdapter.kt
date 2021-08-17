package com.jumbox.app.muslim.ui.quran

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.ViewDataBinding
import com.jumbox.app.muslim.R
import com.jumbox.app.muslim.base.BaseAdapter
import com.jumbox.app.muslim.base.DataBoundViewHolder
import com.jumbox.app.muslim.databinding.LayoutItemAyatBinding
import com.jumbox.app.muslim.databinding.LayoutItemBasmallahBinding
import com.jumbox.app.muslim.vo.Bookmark
import com.jumbox.app.muslim.vo.Translations
import com.jumbox.app.muslim.vo.Verse

/**
 * Created by Jumadi Janjaya date on 10/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/

class VerseAdapter(
        private val callbackShare: (verse: Verse) -> Unit,
        private val callbackBookmark: (verse: Verse) -> Unit,
        private val callbackTafsir: (verse: Verse) -> Unit) : BaseAdapter<Verse , ViewDataBinding>() {

    private val bookmarks = ArrayList<Bookmark>()
    var currentFocus = -2
    var isTopBasmallah = true
    set(value) {
        if (value != isTopBasmallah)
            field = value
    }

    override fun bind(binding: ViewDataBinding, item: Verse?) {
        val context = binding.root.context
        if (currentFocus == item?.number) {
            val colorTo = ResourcesCompat.getColor(context.resources, R.color.accent_translucent, context.theme)
            ValueAnimator.ofObject(ArgbEvaluator(), Color.TRANSPARENT, colorTo).apply {
                duration = 250
                repeatCount = 5
                repeatMode = ValueAnimator.REVERSE
                addUpdateListener {
                    if (it.animatedValue is Int) binding.root.setBackgroundColor(it.animatedValue as Int)
                }
                start()
            }
        } else binding.root.background = null

        if (binding is LayoutItemAyatBinding)
            item?.let { verse ->
                binding.verse = verse.copy(bookmark = bookmarks.find { verse.surah == it.number && verse.number == it.verse } != null)

                binding.btnShare.setOnClickListener {
                    callbackShare.invoke(verse)
                }
                binding.btnFavorite.setOnClickListener {
                    callbackBookmark.invoke(verse)
                }
                binding.btnTafsir.setOnClickListener {
                    callbackTafsir.invoke(verse)
                }
            }
    }

    override fun getItemCount() = if (isTopBasmallah && super.getItemCount() > 0) super.getItemCount() + 1 else super.getItemCount()

    fun submitBookmark(list: List<Bookmark>?) {
        bookmarks.clear()
        list?.let {
            bookmarks.addAll(list)
            notifyDataSetChanged()
        }
    }

    fun updateVerse(oldItem: Verse, newItem: Verse) {
        getPosition(oldItem).let { i ->
            currentList.remove(oldItem)
            currentList.add(i, newItem)
            if (newItem.bookmark)
                bookmarks.add(Bookmark(0, newItem.surah, "", Translations("", "", ""), "", newItem.number))
            notifyItemChanged(i + if (isTopBasmallah) 1 else 0)
        }
    }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return if (viewType == 1)
            LayoutItemBasmallahBinding.bind(LayoutInflater.from(parent.context).inflate(R.layout.layout_item_basmallah, parent, false))
        else
            LayoutItemAyatBinding.bind(LayoutInflater.from(parent.context).inflate(R.layout.layout_item_ayat, parent, false))
    }

    override fun onBindViewHolder(holder: DataBoundViewHolder<ViewDataBinding>, position: Int) {
        if (isTopBasmallah && position == 0) bind(holder.binding, null)
        else bind(holder.binding, getItem(position + if (isTopBasmallah) -1 else 0))
        holder.binding.executePendingBindings()
    }

    override fun getItemViewType(position: Int): Int {
        return if (isTopBasmallah && position == 0) 1 else 2
    }

    override fun areItemsTheFilter(query: CharSequence, item: Verse): Boolean {
        return false
    }
}