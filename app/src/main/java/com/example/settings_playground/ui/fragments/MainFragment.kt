package com.example.settings_playground.ui.fragments

import android.app.Service
import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.settings_playground.R
import com.example.settings_playground.databinding.FragmentMainBinding
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.UUID

const val ACTION_DOWNLOAD = "ACTION_DOWNLOAD"
const val SEND_BITMAP = "SEND_BITMAP"
@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater)
        setClickListeners()
        return binding.root
    }

    private fun setClickListeners() {
        binding.apply {
            fragmentMainBtOtpService.setOnClickListener { findNavController().navigate(R.id.action_mainFragment_to_OTPServiceFragment) }
            fragmentMainBtTimer.setOnClickListener { findNavController().navigate(R.id.action_mainFragment_to_timerFragment) }
            fragmentMainBtScheduleNotification.setOnClickListener { findNavController().navigate(R.id.action_mainFragment_to_scheduleNotificationFragment) }
            fragmentMainBtFlow.setOnClickListener { findNavController().navigate(R.id.action_mainFragment_to_flowsFragment) }
            val intent = Intent(requireActivity(),DownloadNotificationService::class.java).apply {
                action = ACTION_DOWNLOAD
            }
            fragmentMainBtDownloadNotification.setOnClickListener {
                requireActivity().startService(intent) }
        }
        requireActivity().registerReceiver(br, IntentFilter(SEND_BITMAP))
    }
    val br :BroadcastReceiver = object:BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            p1!!.getParcelableExtra<Bitmap>("bitmap")?.let{
                binding.fragmentMainIv.load(it)
            }
        }
    }

    override fun onDestroyView() {
        requireActivity().unregisterReceiver(br)
        requireActivity().stopService(Intent(requireActivity(),DownloadNotificationService::class.java))
        super.onDestroyView()
    }
}

class DownloadNotificationService() : Service() {
    override fun onBind(p0: Intent?) = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val webPath = "https://media.geeksforgeeks.org/wp-content/uploads/20210224040124/JSBinCollaborativeJavaScriptDebugging6-300x160.png"

        when(intent?.action){
            ACTION_DOWNLOAD->{
                CoroutineScope(Dispatchers.IO).launch {
                    val image = mLoad(webPath)
                    image?.let{
                        mSaveMediaToStorage(it)
                    }
                }

            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun mLoad(string: String): Bitmap? {
        val url: URL = mStringToURL(string)!!
        val connection: HttpURLConnection?
        try {
            connection = url.openConnection() as HttpURLConnection
            connection.connect()
            val inputStream: InputStream = connection.inputStream
            val bufferedInputStream = BufferedInputStream(inputStream)
            return BitmapFactory.decodeStream(bufferedInputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(applicationContext, "Error", Toast.LENGTH_LONG).show()
        }
        return null
    }
    // Function to convert string to URL
    private fun mStringToURL(string: String): URL? {
        try {
            return URL(string)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return null
    }

    private suspend fun mSaveMediaToStorage(bitmap: Bitmap?) {
        val filename = UUID.randomUUID().toString()+".jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }
        fos?.use {
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, it)
            withContext(Dispatchers.Main){
                Toast.makeText(this@DownloadNotificationService, "Saved to Gallery", Toast.LENGTH_SHORT).show()
            }
            sendBroadcast(Intent(SEND_BITMAP).apply {
                putExtra("bitmap", bitmap)
            })
        }
    }
}