package com.jumbox.app.muslim.base

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Jumadi Janjaya date on 19/07/2020.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/

@Suppress("UNCHECKED_CAST")
abstract class BaseAdapter<T , VH : ViewDataBinding> : RecyclerView.Adapter<DataBoundViewHolder<VH>>() {

    val currentList = ArrayList<T>()
    private val fullList = ArrayList<T>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBoundViewHolder<VH> {
        val binding = createBinding(parent, viewType)
        return DataBoundViewHolder(binding)
    }

    protected abstract fun createBinding(parent: ViewGroup, viewType: Int): VH

    override fun onBindViewHolder(holder: DataBoundViewHolder<VH>, position: Int) {
        bindHolder(holder, position)
        bind(holder.binding, getItem(position))
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int = currentList.size

    fun getItem(position: Int): T? {
        return currentList[position]
    }

    fun getPosition(t: T?) : Int {
       t?.let {return currentList.indexOf(it) }
        return  -1
    }

    open fun submitList(list: List<T>?) {
        list?.let {
            fullList.clear()
            fullList.addAll(it)
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize() = currentList.size

                override fun getNewListSize() = it.size

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean  = areItemsTheSame(currentList[oldItemPosition], it[newItemPosition])

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean  = currentList[oldItemPosition] == it[newItemPosition]

            })
            currentList.clear()
            currentList.addAll(list)
            result.dispatchUpdatesTo(this)
        }
    }

    open fun search(query: CharSequence) {
        val list = fullList.filter { areItemsTheFilter(query, it)  }
        currentList.clear()
        currentList.addAll(list)
        notifyDataSetChanged()
    }

    open fun areItemsTheFilter(query: CharSequence, item: T) : Boolean {
        return false
    }

    open fun areItemsTheSame(oldItem: T, newItem: T) : Boolean {
        return false
    }

    open fun reload() {
        currentList.clear()
        currentList.addAll(fullList)
        notifyDataSetChanged()
    }

    protected abstract fun bind(binding: VH, item: T?)
    open fun bindHolder(holder: DataBoundViewHolder<VH>, position: Int) {}


}
