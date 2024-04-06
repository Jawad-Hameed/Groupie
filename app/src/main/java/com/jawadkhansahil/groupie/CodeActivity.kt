package com.jawadkhansahil.groupie

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.jawadkhansahil.groupie.databinding.ActivityCodeBinding
import `in`.aabhasjindal.otptextview.OTPListener
import java.util.concurrent.TimeUnit


class CodeActivity : AppCompatActivity() {

    var phoneNumber: String? = null
    lateinit var binding: ActivityCodeBinding
    var progressDialog: ProgressDialog? = null
    lateinit var auth: FirebaseAuth
    var verificationId2 = ""
    val timerOut = 60L
    private var resendTimer: CountDownTimer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        binding = ActivityCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        phoneNumber = intent.getStringExtra("phoneNumber")


        auth = FirebaseAuth.getInstance()
        binding.submitOTPButton.isEnabled = false
        binding.phoneText.text = "Enter OTP send to your phone number \n $phoneNumber"

        showProgressDialog("Sending OTP. . .")

        PhoneAuthProvider.verifyPhoneNumber(setupPhoneAuthOptions(phoneNumber!!))

        binding.resendOTP.setOnClickListener {
            resendVerificationCode(phoneNumber!!)
        }
        binding.otpView.otpListener = object : OTPListener {
            override fun onInteractionListener() {
                binding.submitOTPButton.isEnabled =  false
            }

            override fun onOTPComplete(otp: String) {
                binding.submitOTPButton.isEnabled =  true
                verifyPhoneNumberWithCode(verificationId2, otp)
            }
        }


    }
    private fun setupPhoneAuthOptions(phoneNumber: String): PhoneAuthOptions {
        return PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(timerOut, TimeUnit.SECONDS)
            .setActivity(this@CodeActivity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signInWithPhoneAuthCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    progressDialog?.dismiss()
                    Toast.makeText(this@CodeActivity, e.message, Toast.LENGTH_SHORT).show()
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    progressDialog?.dismiss()
                    verificationId2 = verificationId
                    resendCodeTimer()
                    Toast.makeText(this@CodeActivity, "OTP Sent", Toast.LENGTH_SHORT).show()
                }
            }).build()

    }

    private fun resendVerificationCode(phoneNumber: String) {
        showProgressDialog("Resending OTP. . .")
        PhoneAuthProvider.verifyPhoneNumber(setupPhoneAuthOptions(phoneNumber))
    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        showProgressDialog("Verifying OTP. . .")
        if (verificationId != null) {
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            signInWithPhoneAuthCredential(credential)
        } else {
            Toast.makeText(this@CodeActivity, "Verification ID is null", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this@CodeActivity) { task ->
                progressDialog?.dismiss()
                if (task.isSuccessful) {
                    val intent = Intent(this@CodeActivity, UsernameActivity::class.java)
                    intent.putExtra("phoneNumber", phoneNumber)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@CodeActivity, "Invalid OTP Code", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun showProgressDialog(message: String) {
        progressDialog = ProgressDialog(this@CodeActivity)
        progressDialog!!.setMessage(message)
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
    }

    private fun resendCodeTimer(){
        resendTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.resendOTP.isEnabled = false
                val secondsLeft = millisUntilFinished / 1000
                binding.resendOTP.text = "Resend OTP in $secondsLeft seconds"
            }

            override fun onFinish() {
                binding.resendOTP.isEnabled = true
                binding.resendOTP.text = "Resend OTP"
                resendTimer?.cancel()
            }
        }
        resendTimer?.start()
    }
}
