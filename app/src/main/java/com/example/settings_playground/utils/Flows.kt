package com.example.settings_playground.utils

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow

fun main() {
    CoroutineScope(Dispatchers.Default).launch {
        for (i in 0..10000)
            println("$i - Hello from a different thread! - ${Thread.currentThread().name}")
    }
    // this code will be executed on the main thread
    CoroutineScope(Dispatchers.IO).launch {
        println("Hello from the main thread!")
    }
    /*runBlocking {
        val job = CoroutineScope(Dispatchers.IO).launch{
            producer().collect {
                println("$it - ${Thread.currentThread().name}")
            }
        }
        //to cancel a flow just have to stop consumer
        CoroutineScope(Dispatchers.IO).launch {
            delay(5000)
            job.cancel()
        }
    }*/

}

fun producer() = flow<Int> {
    val list = listOf<Int>(1, 2, 3, 4, 5, 6, 7, 8)
    list.forEach {
        delay(2000)
        emit(it)
    }
}

