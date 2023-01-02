package com.example.settings_playground.utils

import org.junit.After
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(value = Parameterized::class)
class AndroidTestingTest(private val input:String, private val expected:Boolean) {
    private lateinit var testing:AndroidTesting

    @Before
    fun beforeTest(){
        //arrange
        testing = AndroidTesting()
    }
    @After
    fun afterTest(){
        print("after each test")
    }
    @Test
    fun isPallindrome() {
        //arrange
        //act
        val result = testing.isPallindrome(input)
        //assert
        assertEquals(expected,result)
    }

    companion object{
        @JvmStatic
        @Parameters(name = "{index} : {0} is pallindrome - {1}")
        fun data():List<Array<Any>>{
            return listOf(
                arrayOf("hello",false),
                arrayOf("level",true),
                arrayOf("a",true),
                arrayOf("",true)
            )
        }
    }
}