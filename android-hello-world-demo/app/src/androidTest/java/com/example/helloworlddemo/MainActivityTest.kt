package com.example.helloworlddemo

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun clickButton_displaysFormattedGreeting() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val expected = GreetingFormatter().formatDefaultGreeting(
            context.getString(R.string.hello_message)
        )

        onView(withId(R.id.displayButton)).perform(click())
        onView(withId(R.id.messageText)).check(matches(withText(expected)))
    }
}

