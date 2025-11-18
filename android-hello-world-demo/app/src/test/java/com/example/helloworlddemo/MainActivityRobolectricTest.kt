package com.example.helloworlddemo

import android.widget.Button
import android.widget.TextView
import org.junit.Assert.assertEquals
import org.junit.Test
import org.robolectric.Robolectric
import org.robolectric.android.controller.ActivityController

class MainActivityRobolectricTest {

    @Test
    fun clickButtonUpdatesMessage() {
        val controller: ActivityController<MainActivity> = Robolectric.buildActivity(MainActivity::class.java).setup()
        val activity = controller.get()
        val messageText: TextView = activity.findViewById(R.id.messageText)
        val displayButton: Button = activity.findViewById(R.id.displayButton)

        assertEquals(activity.getString(R.string.initial_message), messageText.text.toString())

        displayButton.performClick()

        assertEquals(activity.getString(R.string.hello_message), messageText.text.toString())

        controller.pause().stop().destroy()
    }
}
