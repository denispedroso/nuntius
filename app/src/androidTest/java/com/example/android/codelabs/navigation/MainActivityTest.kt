package com.example.android.codelabs.navigation

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivityTest{

    @Test
    fun test_navSecondaryActivity() {

        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        Espresso.onView(withId((R.id.pass_name))).perform((ViewActions.typeText("1234")))

        Espresso.onView(withId(R.id.navigate_destination_button)).perform(ViewActions.click())

        Espresso.onView(withId(R.id.step_two)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}