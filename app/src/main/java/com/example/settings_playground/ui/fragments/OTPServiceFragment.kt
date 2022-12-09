package com.example.settings_playground.ui.fragments

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.settings_playground.databinding.FragmentOTPServiceBinding
import com.example.settings_playground.utils.OTPBroadcastReceiver
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import javax.inject.Inject

@AndroidEntryPoint
class OTPServiceFragment : Fragment() {

    private var _binding: FragmentOTPServiceBinding? = null
    private val binding: FragmentOTPServiceBinding get() = _binding!!
    @Inject
    lateinit var auth: FirebaseAuth
    private lateinit var verificationId: String
    private var otpReceiver: OTPBroadcastReceiver? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOTPServiceBinding.inflate(inflater)
        setClickListeners()
        return binding.root
    }

    private fun setClickListeners() {
        binding.apply {
            fragmentOTPServiceBtGetOtp.setOnClickListener { getOTP() }
            fragmentOTPServiceBtVerifyOtp.setOnClickListener {
                val otp = fragmentOTPServiceEtOtp.text
                val credentials = PhoneAuthProvider.getCredential(verificationId, otp.toString())
                signInWithCredentials(credentials)
            }
        }
    }

    private val otpCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
            if (binding.fragmentOTPServiceVs.nextView.id == binding.fragmentOTPServiceBtGetOtp.id) binding.fragmentOTPServiceVs.showNext()
        }

        override fun onVerificationFailed(p0: FirebaseException) {
            if (binding.fragmentOTPServiceVs.nextView.id == binding.fragmentOTPServiceBtGetOtp.id) binding.fragmentOTPServiceVs.showNext()
        }

        override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(p0, p1)
            verificationId = p0
            if (binding.fragmentOTPServiceVs.nextView.id == binding.fragmentOTPServiceLl.id) binding.fragmentOTPServiceVs.showNext()
            consentThroughUser()
        }
    }

    private fun signInWithCredentials(p0: PhoneAuthCredential) {
        auth.signInWithCredential(p0).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "LoggedIn Successfully", Toast.LENGTH_SHORT).show()
                startCountTo(20000L)//after this log out automatically
            } else {
                Log.d("taget", it.exception.toString())
            }
        }
    }

    private fun startCountTo(i: Long) {
        val timer = object : CountDownTimer(i, 1000L) {
            override fun onTick(p0: Long) {
            }

            override fun onFinish() {
                auth.signOut()
                Toast.makeText(context, "LoggedOut Successfully", Toast.LENGTH_SHORT).show()
                if (binding.fragmentOTPServiceVs.nextView.id == binding.fragmentOTPServiceBtGetOtp.id) binding.fragmentOTPServiceVs.showNext()
            }
        }
        timer.start()
    }

    private fun getOTP() {
        val otpBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+918860696081")
            .setTimeout(0L, TimeUnit.SECONDS)//0L to disable firebase verification and enable consent api
            .setActivity(requireActivity())
            .setCallbacks(otpCallback)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(otpBuilder)
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                val message = it.data!!.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                getOTPForMessage(message)
            }
        }

    private fun getOTPForMessage(message: String?) {
        val otpPattern = Pattern.compile("(|^)\\d{6}")
        val matcher = message?.let { otpPattern.matcher(it) }
        if (matcher!!.find()) {
            matcher.group(0)?.let { binding.fragmentOTPServiceEtOtp.setText(it) }
        }
    }

    private fun registerBroadcastReceiver() {
        otpReceiver = OTPBroadcastReceiver()
        otpReceiver!!.otpListener = object : OTPBroadcastReceiver.OTPListener {
            override fun onSuccess(intent: Intent?) {
                resultLauncher.launch(intent)
            }

            override fun onFailure() {
                Log.d("taget", "Failed")
            }
        }
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        requireActivity().registerReceiver(otpReceiver, intentFilter)
    }


    override fun onStart() {
        registerBroadcastReceiver()
        super.onStart()
    }

    override fun onStop() {
        requireActivity().unregisterReceiver(otpReceiver)
        super.onStop()
    }

    private fun consentThroughUser() {
        val client = SmsRetriever.getClient(requireActivity())
        client.startSmsUserConsent(null)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}