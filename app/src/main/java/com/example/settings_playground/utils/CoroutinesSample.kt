package com.example.settings_playground.utils

import kotlinx.coroutines.*

fun main() {
    CoroutineScope(Dispatchers.IO).launch { c.await() }
}

val c = runBlocking {
    //task 1 - when you want some block of code to be in different context
    withContext(Dispatchers.IO) {
        delay(1000)
        println("task1 - ${Thread.currentThread().name}")
    }
    //task 2 - fire and forgot
    CoroutineScope(Dispatchers.IO).launch {
        delay(1000)
        println("task2 - ${Thread.currentThread().name}")
    }
    //task 3 - need something in return
    val a = CoroutineScope(Dispatchers.IO).async {
        delay(1000)
        println("task3 - ${Thread.currentThread().name}")

    }
    a.await()
    //task 4 - want to do something simultaneously on different threads use async for that
    async {
        async {
            delay(1000)
            println("task4.1- ${Thread.currentThread().name}")
        }.await()
        async {
            delay(2000)
            println("task4.2- ${Thread.currentThread().name}")
        }.await()
    }
}