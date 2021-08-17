package com.jumbox.app.muslim.binding

import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.databinding.BindingAdapter
import com.jumbox.app.muslim.BuildConfig
import com.jumbox.app.muslim.R
import com.jumbox.app.muslim.utils.dateFormat
import com.jumbox.app.muslim.utils.textOrnamentHtml
import java.util.*

/**
 * Created by Jumadi Janjaya date on 11/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/
object Binding {

    @JvmStatic
    @BindingAdapter("isGone")
    fun isGone(view: View, b: Boolean) {
        view.visibility = if (b) View.GONE else View.VISIBLE
    }

    @JvmStatic
    @BindingAdapter("isVisible")
    fun isVisible(view: View, b: Boolean) {
        view.visibility = if (b) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("verse", "number", requireAll = true)
    fun verse(textView: TextView, verse: String?, number: Int) {
        val context = textView.context
        val hexColor = ResourcesCompat.getColor(context.resources, R.color.color_text_number, context.theme)
        val textColor = ResourcesCompat.getColor(context.resources, R.color.colorIconDark, context.theme)
        val size = context.resources.getDimensionPixelSize(R.dimen.tv_small)
        val string = "$verse ${number.textOrnamentHtml(hexColor, textColor, size)}"
        textView.text = HtmlCompat.fromHtml(string, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    @JvmStatic
    @BindingAdapter("htmlFormat")
    fun htmlFormat(textView: TextView, text: String?) {
        textView.text = text?.let {
            textView.linksClickable = true
            textView.movementMethod = LinkMovementMethod.getInstance()
            HtmlCompat.fromHtml(it.replace("[APP_NAME]", BuildConfig.APPLICATION_ID), HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
    }

    @JvmStatic
    @BindingAdapter("dateTimeLong", "format", requireAll = false)
    fun dateTimeFormat(textView: TextView, time: Long, format: String? = null) {
        textView.text = dateFormat(format ?: "HH:mm", time)
    }

    @JvmStatic
    @BindingAdapter("android:text", "textEN", requireAll = false)
    fun textLocalLang(textView: TextView, id: String?, en: String?) {
        textView.text = if (Locale.getDefault().language == "in") id else en?:id
    }
}