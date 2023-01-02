package com.example.settings_playground.utils

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.settings_playground.R
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test

class ContextManagerTest {

    private lateinit var contextManager:ContextManager

    @Before
    fun setUp() {
        contextManager = ContextManager()
    }

    //if we want to have exception as expected then declare that inside test bracket
    @Test(expected = java.lang.Exception::class)
    fun testContextForException() {
        //getting context in non ui tests
        val context = ApplicationProvider.getApplicationContext<Context>()
        contextManager.testContext(context,1000000000)
    }
    @Test()
    fun testContextDefault() {
        //getting context in non ui tests
        val context = ApplicationProvider.getApplicationContext<Context>()
        val result = contextManager.testContext(context,R.string.app_name)
        assertEquals("Settings-Playground",result)
    }

}