package com.example.settings_playground.utils

import kotlinx.coroutines.*

class CoroutinesSample {
    fun main(args:Array<String>){

    }
    val c = runBlocking{
        //task 1 - when you want some block of code to be in different context
        withContext(Dispatchers.IO){
            delay(1000)
        }
        //task 2 - fire and forgot
        CoroutineScope(Dispatchers.IO).launch{
            delay(1000)
        }
        //task 3 - need something in return
        val a = CoroutineScope(Dispatchers.IO).async {
            delay(1000)
        }
        a.await()
        //task 4 - want to do something simultaneously on different threads use async for that
        CoroutineScope(Dispatchers.IO).launch {
            val a = async{delay(1000)}
            val b = async{delay(2000)}
        }
    }

}