package com.jumbox.app.muslim.base

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Jumadi Janjaya date on 19/07/2020.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/

class DataBoundViewHolder<out T : ViewDataBinding> constructor(val binding: T, defaultView: View? = null) : RecyclerView.ViewHolder(defaultView?: binding.root)
