package com.jumbox.app.muslim.ui.spalsh

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.jumbox.app.muslim.R
import com.jumbox.app.muslim.utils.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule

/**
 * Created by Jumadi Janjaya date on 07/05/2021.
 * Bengkulu, Indonesia.
 * Copyright (c) Jumbox. All rights reserved.
 */

class StartInitializeActivityTest {

    @get:Rule
    var activityRule = object : ActivityTestRule<StartInitializeActivity>(StartInitializeActivity::class.java) {
        override fun getActivityIntent(): Intent {
            val targetContext = InstrumentationRegistry.getInstrumentation().targetContext
            return Intent(targetContext, StartInitializeActivity::class.java)
        }
    }

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.idlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.idlingResource)
    }

    fun startInitializeTest() {
        onView(withId(R.id.tv_city)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_ok)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_ok)).perform(click())
    }
}