package com.itwings.dataVerification.Screens

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.itwings.dataVerification.R
import com.itwings.dataVerification.databinding.ActivityMpinBinding
import com.itwings.wastemanagement.Utills.Comman

class mPin : AppCompatActivity() {

    lateinit var binding: ActivityMpinBinding
    lateinit var ctx: Activity
    lateinit var cmn: Comman

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getSupportActionBar() != null) {
            getSupportActionBar()?.hide();
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mpin)
        ctx = this
        cmn = Comman(ctx)



        binding.btSetmPin.setOnClickListener {

            if (binding.otpView.otp.toString().isEmpty()){
                cmn.showToast("Please Enter mPin !!")
            }else if (binding.mpinVerify.otp.toString().isEmpty()){
                cmn.showToast("Please Enter verify mPin !!")
            }else if (!binding.otpView.otp.toString().equals(binding.mpinVerify.otp.toString())){
                cmn.showToast("Please Enter Correct verify mPin !!")
            }else{
                startActivity(Intent(applicationContext, Dashboard::class.java))
                finish()
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }

        }

    }
}
