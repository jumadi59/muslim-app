package com.jumbox.app.muslim.ui.hadist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import androidx.core.view.isGone
import com.google.android.material.chip.Chip
import com.jumbox.app.muslim.R
import com.jumbox.app.muslim.base.BaseAdapter
import com.jumbox.app.muslim.base.DataBoundViewHolder
import com.jumbox.app.muslim.databinding.LayoutItemStepGroupBinding
import com.jumbox.app.muslim.utils.gone
import com.jumbox.app.muslim.utils.visible
import com.jumbox.app.muslim.vo.Kitab
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Created by Jumadi Janjaya date on 16/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/
class HadistAdapter(private val callbackClick: (kitab: String, id: Int) -> Unit) : BaseAdapter<Kitab, LayoutItemStepGroupBinding>() {

    private var currentItemExpand: Pair<Int, DataBoundViewHolder<LayoutItemStepGroupBinding>>? = null

    override fun createBinding(parent: ViewGroup, viewType: Int): LayoutItemStepGroupBinding {
        val binding = LayoutItemStepGroupBinding.bind(LayoutInflater.from(parent.context).inflate(R.layout.layout_item_step_group, parent, false))
        val item = getItem(viewType)
        item?.ids?.forEach { number ->
            Chip(binding.root.context).apply {
                text = context.getString(R.string.number, number.toString())
                binding.chipGroup.addView(this)
                setOnClickListener {
                    callbackClick.invoke(item.kitab, number)
                } }
        }
        return binding
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun submitList(list: List<Kitab>?) {
        currentItemExpand = null
        GlobalScope.launch(Dispatchers.Main) {
            super.submitList(list)
        }
    }

    override fun bind(binding: LayoutItemStepGroupBinding, item: Kitab?) {
        binding.expandText.text = item!!.kitab.replace("_", " ")
    }

    override fun bindHolder(holder: DataBoundViewHolder<LayoutItemStepGroupBinding>, position: Int) {
        if (position != currentItemExpand?.first) {
            holder.binding.chipGroup.gone()
            holder.binding.divider.gone()
            holder.binding.expandArrow.rotation = 0f
        } else if (holder.binding.chipGroup.isGone) {
            holder.binding.chipGroup.visible()
            holder.binding.divider.visible()
            holder.binding.expandArrow.rotation = 180f
        }


        holder.binding.root.setOnClickListener {
            if (holder.binding.chipGroup.isGone) {
                holder.binding.animation(true) {
                    currentItemExpand?.second?.binding?.animation(false) {}
                    currentItemExpand = Pair(position, holder)
                }
            } else {
                holder.binding.animation(false) {
                    if (position != currentItemExpand?.first) currentItemExpand?.second?.binding?.animation(false) {}
                    currentItemExpand = null
                }
            }
        }
    }

    private fun LayoutItemStepGroupBinding.animation(isOpen: Boolean, callBackEnd: () -> Unit) {
        expandArrow.let {
            if (isOpen)
                it.animate().rotation(180f).setDuration(300).start()
            else
                it.animate().rotation(0f).setDuration(300).start()
        }
        chipGroup.let {
            if (isOpen) {
                divider.visible()
                it.startAnimationExpand(isOpen, callBackEnd)
            } else {
                divider.gone()
                it.startAnimationExpand(isOpen, callBackEnd)
            }
        }
    }

    private fun View.startAnimationExpand(isOpen: Boolean = true, callBackEnd: () -> Unit) {

        val actualHeight: Int
        val animation =if (isOpen) {
            measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            actualHeight = measuredHeight

            layoutParams.height = 0
            visible()
            object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                    layoutParams.height =
                            if (interpolatedTime == 1f) ViewGroup.LayoutParams.WRAP_CONTENT else (actualHeight * interpolatedTime).toInt()
                    requestLayout()
                }
            }
        } else {
            actualHeight = measuredHeight
            object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                    if (interpolatedTime == 1f) {
                        gone()
                    } else {
                        layoutParams.height = actualHeight - (actualHeight * interpolatedTime).toInt()
                        requestLayout()
                    }
                }
            }
        }
        animation.duration = (actualHeight / context.resources.displayMetrics.density).toLong()
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {}

            override fun onAnimationEnd(p0: Animation?) {
                callBackEnd.invoke()
            }

            override fun onAnimationRepeat(p0: Animation?) {}

        })
        startAnimation(animation)
    }

    override fun areItemsTheFilter(query: CharSequence, item: Kitab): Boolean {
        return false
    }

    override fun areItemsTheSame(oldItem: Kitab, newItem: Kitab): Boolean {
        return oldItem.kitab == oldItem.kitab
    }
}