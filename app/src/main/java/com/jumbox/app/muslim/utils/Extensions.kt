package com.jumbox.app.muslim.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.net.Uri
import android.os.IBinder
import android.text.*
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.jumbox.app.muslim.R
import com.jumbox.app.muslim.databinding.LayoutMessageBinding
import com.jumbox.app.muslim.vo.Jadwal
import com.jumbox.app.muslim.vo.Prayer
import java.lang.reflect.Modifier
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


/**
 * Created by Jumadi Janjaya date on 07/04/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/

fun View.gone() : View {
    this.visibility = View.GONE
    return this
}

fun View.visible() : View {
    this.visibility = View.VISIBLE
    return this
}

fun EditText.textChangedListener(
        before: ((text: CharSequence?) -> Unit)? = null,
        on: ((text: CharSequence?) -> Unit)? = null,
        after: ((text: CharSequence?) -> Unit)? = null
) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            before?.invoke(p0)
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            on?.invoke(p0)
        }

        override fun afterTextChanged(p0: Editable?) {
            after?.invoke(p0)
        }
    })
}

fun Context.getColorCompat(@ColorRes color: Int) = ContextCompat.getColor(this, color)

fun TextView.setTextColorRes(@ColorRes color: Int) = setTextColor(context.getColorCompat(color))

fun EditText.hideInput(iBinder: IBinder) {
    val inputMethodManager = ContextCompat.getSystemService(
            this.context,
            InputMethodManager::class.java
    )
    inputMethodManager?.hideSoftInputFromWindow(iBinder, 0)
    if (this.hasFocus())
        this.clearFocus()
}

@SuppressLint("SimpleDateFormat")
fun dateFormat(format: String? = null, time: Long? = null) : String {
    val current = SimpleDateFormat(format ?: "yyyy-MM-dd")
    return current.format(if (time != null) Date(time) else Date())
}

fun Long.isValid(time: Long? = null) : Boolean {
    return this >= time?:Date().time
}

fun Jadwal.convertToList() : List<Prayer> {
    val list = ArrayList<Prayer>()
    list.add(Prayer(
            name = "imsak",
            time = ("$date $imsak").dateTimeStringToLong(),
            backgroundColor = Color.parseColor("#0f0c29"),
            date = date, type = "notify"))
    list.add(Prayer(
            name = "fajr",
            time = ("$date $subuh").dateTimeStringToLong(),
            backgroundColor = Color.parseColor("#0f0c29"),
            date = date, type = "adzan"))
    list.add(Prayer(
            name = "sunrise",
            time = ("$date $terbit").dateTimeStringToLong(),
            backgroundColor = Color.parseColor("#0f0c29"),
            date = date, type = "notify"))
    list.add(Prayer(
            name = "dhuha",
            time = ("$date $dhuha").dateTimeStringToLong(),
            backgroundColor = Color.parseColor("#8e2680"),
            date = date, type = "notify"))
    list.add(Prayer(
            name = "dhuhr",
            time = ("$date $dzuhur").dateTimeStringToLong(),
            backgroundColor = Color.parseColor("#0a9de1"),
            date = date, type = "adzan"))
    list.add(Prayer(
            name = "asr",
            time = ("$date $ashar").dateTimeStringToLong(),
            backgroundColor = Color.parseColor("#0a9de1"),
            date = date, type = "adzan"))
    list.add(Prayer(
            name = "maghrib",
            time = ("$date $maghrib").dateTimeStringToLong(),
            backgroundColor = Color.parseColor("#ff5231"),
            date = date, type = "adzan"))
    list.add(Prayer(
            name = "isha",
            time = ("$date $isya").dateTimeStringToLong(),
            backgroundColor = Color.parseColor("#6f4ba9"),
            date = date, type = "adzan"))
    return list
}

fun String.dateTimeStringToLong() : Long {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return try {
        val today = dateFormat.parse(this)
        today?.time?:0
    } catch (e: ParseException) {
        0
    }
}
fun NestedScrollView.elevationAppBar(appBarLayout: AppBarLayout) {
    this.setOnScrollChangeListener { _: NestedScrollView?, _: Int, scrollY: Int, _: Int, _: Int ->
        if (scrollY == 0)
            appBarLayout.elevation = 0f.dpToPx().toFloat()
        else
            appBarLayout.elevation = 5f.dpToPx().toFloat()
    }
}

fun RecyclerView.elevationAppBar(appBarLayout: AppBarLayout) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        var overallYScroll = 0

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            overallYScroll += dy
            if (overallYScroll > 0)
                appBarLayout.elevation = 5f.dpToPx().toFloat()
            else
                appBarLayout.elevation = 0f.dpToPx().toFloat()
        }

    })
}

fun Float.dpToPx() : Int {
    val metrics = Resources.getSystem().displayMetrics
    return (this * metrics.density + 0.5f).roundToInt()
}

fun isNight() : Boolean {
    val calendar = Calendar.getInstance()
    val timeOfDay = calendar.get(Calendar.HOUR_OF_DAY)
    return timeOfDay >= 17 || (timeOfDay in 0..5)
}

fun Int.textOrnamentHtml(ornamentColor: Int, numberColor: Int, size: Int) : String {
    val arabicNumbers = arrayOf("٠","١","٢","٣","٤","٥","٦","٧","٨","٩")
    val numbers = arrayOf("0","1","2","3","4","5","6","7","8","9")
    val string = "$this"
    val builder = StringBuilder()

    for (i in string.indices) {
        numbers.find { it.toCharArray()[0] == string[i] }?.let {
            builder.append(arabicNumbers[numbers.indexOf(it)])
        }
    }
    val hexColor = "#" + Integer.toHexString(ornamentColor).substring(2)
    val textColor = "#" + Integer.toHexString(numberColor).substring(2)
    return "<font color=\"$hexColor\">&#xFD3F;</font>" +
            "<small color=\"$textColor\">$builder</small>" +
            "<font color=\"$hexColor\">&#xFD3E;</font>"
}
fun LayoutMessageBinding.messageDisplay(
        title: String? = null,
        msg: String? = null,
        @DrawableRes iconId: Int = R.drawable.ic_search,
        hideIcon: Boolean = false,
        btnTxt: String? = null, action: (() -> Any)? = null
) {

    title?.let { tvTitle.text = it }
    msg?.let { tvDescription.text = it }


    ivIcon.setImageResource(iconId)
    if (hideIcon) ivIcon.gone()

    btnTxt?.let { btnAction.text = it }

    btnAction.visibility = if (btnTxt != null || action != null) View.VISIBLE else View.GONE
    btnAction.setOnClickListener { action?.invoke() }
    root.visible()
}

@Throws(IllegalAccessException::class)
fun String.nameResource(clazz: Class<*>) : Int  {
    var value = 0
    val c = clazz.declaringClass
    for (f in clazz.declaredFields) {
        if (Modifier.isStatic(f.modifiers)) {
            val wasAccessible = f.isAccessible
            f.isAccessible = true
            if (f.name == this) {
                value = f.getInt(c)
                break
            }
            f.isAccessible = wasAccessible
        }
    }
    return value
}

fun Activity.getStringWithNameId(nameId: String) : String {
    return getString(nameId.nameResource(R.string::class.java))
}

fun TextView.urlClickListener(callback: (uri: Uri) -> Unit) {
   text = SpannableStringBuilder.valueOf(text).apply {
        getSpans(0, length, URLSpan::class.java).forEach { URLSpan ->
            setSpan(object : ClickableSpan() {
                override fun onClick(V: View) {
                    callback.invoke(Uri.parse(URLSpan.url))
                }
            }, getSpanStart(URLSpan), getSpanEnd(URLSpan), Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            removeSpan(URLSpan)
        }
    }
}

fun ImageView.setTextAsBitmap(text: String, textSize: Float? = 20f, textColor: Int? = Color.WHITE, typeface: Typeface?): Bitmap {
    // adapted from https://stackoverflow.com/a/8799344/1476989
    val paint = TextPaint(ANTI_ALIAS_FLAG)
    paint.textSize = textSize?: 20f
    paint.color = textColor?: Color.WHITE
    paint.textAlign = Paint.Align.RIGHT
    paint.typeface = typeface
    val baseline: Float = -paint.ascent() // ascent() is negative
    var width = (paint.measureText(text) + 0.0f).toInt() // round
    var height = (baseline + paint.descent() + 0.0f).toInt()
    if (width > height) height = width else width = height
    val image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(image)
    canvas.drawText(text, (width/10f), baseline, paint)
    setImageBitmap(image)
    return image
}
