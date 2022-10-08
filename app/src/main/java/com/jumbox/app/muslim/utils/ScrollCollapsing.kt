package com.jumbox.app.muslim.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.LinearLayout


/**
 * Created by Jumadi Janjaya date on 05/05/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 **/
class ScrollCollapsing @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var downY = 0f
    private var downX = 0f
    private var isScrolling = false
    var callbackScrolling: ((deltaY: Float) -> Boolean?)? = null
    private val gestureDetector = GestureDetector(context, CustomGestureListener())

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return when(ev.action) {
            MotionEvent.ACTION_DOWN -> {
                isScrolling = true
                downY = ev.rawY
                downX = ev.rawX
                super.onInterceptTouchEvent(ev)
            }
            MotionEvent.ACTION_MOVE -> {
                val yDelta = ev.rawY - downY
                val xDelta = ev.rawX - downX
                if ((yDelta > xDelta || yDelta < -xDelta) && isScrolling)
                    true
                else super.onInterceptTouchEvent(ev)
            }
            else -> super.onInterceptTouchEvent(ev)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        isScrolling = gestureDetector.onTouchEvent(event)
        return isScrolling
    }

    inner class CustomGestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onScroll(
            e1: MotionEvent,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            return if (distanceX == 0.0f)
                callbackScrolling?.invoke(distanceY)?:super.onScroll(e1, e2, distanceX, distanceY)
            else super.onScroll(e1, e2, distanceX, distanceY)
        }

    }
}