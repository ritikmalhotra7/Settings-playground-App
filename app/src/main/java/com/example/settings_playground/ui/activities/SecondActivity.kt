package com.example.settings_playground.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.settings_playground.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        Log.d("taget","B.onCreate with data: ${savedInstanceState.toString()}")
    }

    override fun onStart() {
        Log.d("taget","B.onStart")
        super.onStart()
    }

    override fun onResume() {
        Log.d("taget","B.onResume")
        super.onResume()
    }

    override fun onPause() {
        Log.d("taget","B.onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.d("taget","B.onStop")
        super.onStop()
    }

    override fun onRestart() {
        Log.d("taget","B.onRestart")
        super.onRestart()
    }
    override fun onDestroy() {
        Log.d("taget","B.onDestroy")
        super.onDestroy()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        Log.d("taget","B.onRestoreInstanceState")
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d("taget","B.onSaveInstanceState")
        super.onSaveInstanceState(outState)
    }


}