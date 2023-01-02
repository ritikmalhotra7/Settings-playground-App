package com.example.settings_playground.utils

import android.content.Context

class AndroidTesting {
    //Junit Test
    fun isPallindrome(input:String):Boolean{
        var left = 0;
        var right = input.length-1
        while(left<right){
            if(!input.substring(left,left+1).equals(input.substring(right,right+1))) return false
            left++;
            right--;
        }
        return true
    }
}

//Non-UI Test
class ContextManager{
    fun testContext(context: Context,id:Int):String = context.resources.getString(id)
}