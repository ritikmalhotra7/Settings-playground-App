package com.example.settings_playground.ui.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.settings_playground.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("taget","A.onCreate with data: ${savedInstanceState.toString()}")
        setContentView(R.layout.activity_main)
    }
    override fun onStart() {
        Log.d("taget","A.onStart")
        super.onStart()
    }

    override fun onResume() {
        Log.d("taget","A.onResume")
        super.onResume()
    }

    override fun onPause() {
        Log.d("taget","A.onPause")
        super.onPause()
    }

    override fun onStop() {
        Log.d("taget","A.onStop")
        super.onStop()
    }

    override fun onRestart() {
        Log.d("taget","A.onRestart")
        super.onRestart()
    }
    override fun onDestroy() {
        Log.d("taget","A.onDestroy")
        super.onDestroy()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        Log.d("taget","A.onRestoreInstanceState")
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d("taget","A.onSaveInstanceState")
        super.onSaveInstanceState(outState)
    }
}