package com.gordiyx.budgettracker

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 */

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Get the app context from the instrumentation registry.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        // Assert that the package name is correct
        assertEquals("com.alterpat.budgettracker", appContext.packageName)
    }
}