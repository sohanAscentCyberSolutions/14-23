package com.itwings.dataVerification.Screens

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.itwings.dataVerification.HttpRequest
import com.itwings.dataVerification.R
import com.itwings.dataVerification.User
import com.itwings.dataVerification.databinding.ActivityOTPBinding
import com.itwings.wastemanagement.Utills.Comman
import `in`.aabhasjindal.otptextview.OTPListener
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class OTP : AppCompatActivity() , OTPListener{


   lateinit var binding: ActivityOTPBinding
   lateinit var ctx: Activity
    var mobileNo = ""
    var txn = ""
   lateinit var cmn: Comman
    var latitude: String = ""
    var longitude: String = ""

    val countDownTimer: CountDownTimer = object : CountDownTimer(120000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            binding!!.waitingTimer.setText(millisecondsToTime(millisUntilFinished))
        }

        override fun onFinish() {
            binding!!.waitingTimer.text = "OTP not detected !! Enter manualy or resend the otp."
            binding!!.resendOtp.visibility = View.VISIBLE
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getSupportActionBar() != null) {
            getSupportActionBar()?.hide();
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_o_t_p)
        ctx = this
        cmn = Comman(ctx)
        mobileNo = intent.getStringExtra("mobileNo")!!
        txn = intent.getStringExtra("txn")!!
        latitude = intent.getStringExtra("latitude")!!
        longitude = intent.getStringExtra("longitude")!!
        // binding.mobNo.setText(""+mobileNo);
        // binding.mobNo.setText(""+mobileNo);
        countDownTimer.start()
        binding.loginAt.setText(""+cmn.getCurrentDateWithTime())
        // OtpReader.bind(this::onOTPComplete,"VD-ILAXLN");


        // OtpReader.bind(this::onOTPComplete,"VD-ILAXLN");
        binding.btVerify.setOnClickListener {
            if (binding.otpView.otp!!.toString().length < 6) {
                cmn!!.showToast("Please Enter 4 Digit OTP.")
                binding.otpView.showError()
            } else {
                binding.otpView.requestFocusOTP()
            }
        }
        binding.resendOtp.setOnClickListener {
            cmn!!.showToast("Otp resend successfully !!")
            //sendOTP();
        }

        binding.otpView.otpListener = this


    }

    private fun millisecondsToTime(milliseconds: Long): String? {
        return "Waiting for OTP " + String.format(
            "%d : %d ",
            TimeUnit.MILLISECONDS.toMinutes(milliseconds),
            TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds))
        ) + "Seconds"
    }

    override fun onDestroy() {
        if (countDownTimer != null) {
            countDownTimer.onFinish()
            countDownTimer.cancel()
        }
        super.onDestroy()
    }

    override fun onStop() {
        super.onStop()
        if (countDownTimer != null) {
            countDownTimer.onFinish()
            countDownTimer.cancel()
        }
    }

    override fun onInteractionListener() {
    }

    override fun onOTPComplete(otp: String) {
        verifyOtp(otp)
    }

    private fun verifyOtp(otp: String) {
        val parameters = JSONObject()
        try {
            parameters.put("Mobileno", mobileNo)
            parameters.put("txn", txn)
            parameters.put("otp", otp)
            parameters.put("DeptCode", "TEST")
            parameters.put("ServiceCode", "NIC")
            parameters.put("DeptKey", "984oyu3yt2")
            val handler: Handler = object : Handler() {
                override fun handleMessage(msg: Message) {
                    val aResponse = msg.data.getString("message")
                    if (null != aResponse) {
                        try {
                            val objectRes = JSONObject(aResponse)
                            if(objectRes.optString("status").equals("True")){
                                var result1 = objectRes.optJSONArray("result")

                                if(result1 != null && result1.length() > 0 ){

                                }else{
                                    cmn.showToast(""+objectRes.optString("message"))
                                }

                                var result = result1.optJSONObject(0)
                                cmn.setIsLogged(true)
                                val user = User()
                                user.username = result.optString("userName")
                                var districtlist = result.optJSONArray("district")
                                for (i in 0..districtlist.length()-1) {
                                    user.district.add(districtlist.optString(i))
                                }
                                var bTownlist = result.optJSONArray("bTown")
                                for (i in 0..bTownlist.length()-1) {
                                    user.bTown.add(bTownlist.optString(i))
                                }
                                var wardVillagelist = result.optJSONArray("wVillage")
                                for (i in 0..wardVillagelist.length()-1) {
                                    user.wardVillage.add(wardVillagelist.optString(i))
                                }
                                user.group = result.optString("group")
                                user.mobile = mobileNo
                                user.contactedYes = result.optString("contactedYes")
                                user.contactedNotTraceable = result.optString("contactedNotTraceable")
                                user.totalPushed = result.optString("totalPushed")
                                user.totalPending = result.optString("totalPending")
                                cmn.saveUser(user)
                               /* startActivity(Intent(applicationContext, Dashboard::class.java))
                                finish()
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)*/
                                saveData()
                            }
                           /* val user_token = objectRes.optString("user_token")
                            cmn.setToken(user_token)
                            cmn.setContactNo(mobileNo)
                            cmn.setIsLogged(true)
                            cmn.showToast("Otp verified successfully !!")
                            if (cmn.getUserType().equalsIgnoreCase(cmn.userCustomer)) {
                                startActivity(Intent(ctx, DashboardCustomer::class.java))
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                            } else if (cmn.getUserType().equalsIgnoreCase(cmn.userOwner)) {
                                startActivity(
                                    Intent(
                                        ctx,
                                        com.itwings.watertankerservices.ui.owner.Dashboard::class.java
                                    )
                                )
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                            } else if (cmn.getUserType().equalsIgnoreCase(cmn.userDriver)) {
                                val loggedin_vehicle = objectRes.optString("loggedin_vehicle")
                                cmn.saveDriverProfile(DriverDataParser().parseProfile(objectRes))
                                cmn.setLoggedInVehicle(loggedin_vehicle)
                                startActivity(Intent(ctx, DashboardDriver::class.java))
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                            } else {
                                cmn.setIsLogged(false)
                                cmn.showToast("we are working on it.")
                            }*/
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            cmn.showToast(""+e.toString())
                        }
                    } else {
                        // ALERT MESSAGE
                        Toast.makeText(
                            ctx,
                            "Not Got Response From Server.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            val request = HttpRequest("VerifyOTPforLogin", parameters, handler, ctx)
            request.postAPI()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun saveData() {
        val parameters = JSONObject()
        try {
            parameters.put("Mobileno", mobileNo)
            parameters.put("token", "gmda@gis!")
            parameters.put("DeptCode", "TEST")
            parameters.put("ServiceCode", "NIC")
            parameters.put("DeptKey", "984oyu3yt2")
            parameters.put("LoginDate", cmn.getCurrentDate())
            parameters.put("UserLongitude", longitude)
            parameters.put("Group", cmn.getUser().group)
            parameters.put("UserLatitude", latitude)

            val handler: Handler = object : Handler() {
                override fun handleMessage(msg: Message) {
                    val aResponse = msg.data.getString("message")
                    if (null != aResponse) {
                        try {
                            val objectRes = JSONObject(aResponse)
                            if(objectRes.optString("status").equals("true")){
                                startActivity(Intent(applicationContext, Dashboard::class.java))
                                finish()
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                                /* if (message.optString("status").equals("Successfull")){
                                     var result = message.optJSONArray("result")
                                     if (result.length() > 0){
                                         var result = message.optJSONArray("result")
                                         val data = result.optJSONObject(0)
                                         val user = User()

                                         var lgdList : ArrayList<HashMap<String,String>>? = ArrayList()

                                         var array = data.optJSONArray("wardVillageList")

                                         for (i in 0..array.length()-1) {
                                             val data = array.optJSONObject(i)
                                             // cmn.printLog("data new ---"+data.optString("pollBoothCode"))
                                             var temdata = HashMap<String,String>()
                                             temdata.put("code" , data.optString("wardVillageLgdCode"))
                                             temdata.put("village" , data.optString("wardVillage"))
                                             lgdList!!.add(temdata)
                                         }

                                         user.lgdList = lgdList
                                         user.loginTxnID = data.optString("loginTxnID")
                                         user.username = data.optString("username")
                                         user.role = data.optString("role")
                                         user.bTown = data.optString("bTown")
                                         user.designation = data.optString("designation")
                                         user.wardVillage = data.optString("wardVillage")
                                         user.district = data.optString("district")
                                         user.zoneCode = data.optString("zoneCode")
                                         user.districtLgdCode = data.optString("districtLgdCode")
                                         user.blockTownLgdCode = data.optString("blockTownLgdCode")
                                         user.wardVillageLgdCode = data.optString("wardVillageLgdCode").replace(" " , "")
                                         user.mobile = mobileNo
                                         cmn.setIsLogged(true)
                                         cmn.saveUser(user)
                                         startActivity(Intent(applicationContext, Dashboard::class.java))
                                         finish()
                                         overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                                     }else{
                                         cmn.showToast("User Information Not Found !!")
                                     }
                                 }else if (message.optString("status").equals("Failed")){
                                     var result = message.optJSONObject("result")
                                     cmn.showToast(""+result.optString("message"))
                                 }else{
                                     cmn.showToast(""+message.optString("message"))
                                 }*/
                            }
                            /* val user_token = objectRes.optString("user_token")
                             cmn.setToken(user_token)
                             cmn.setContactNo(mobileNo)
                             cmn.setIsLogged(true)
                             cmn.showToast("Otp verified successfully !!")
                             if (cmn.getUserType().equalsIgnoreCase(cmn.userCustomer)) {
                                 startActivity(Intent(ctx, DashboardCustomer::class.java))
                                 overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                             } else if (cmn.getUserType().equalsIgnoreCase(cmn.userOwner)) {
                                 startActivity(
                                     Intent(
                                         ctx,
                                         com.itwings.watertankerservices.ui.owner.Dashboard::class.java
                                     )
                                 )
                                 overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                             } else if (cmn.getUserType().equalsIgnoreCase(cmn.userDriver)) {
                                 val loggedin_vehicle = objectRes.optString("loggedin_vehicle")
                                 cmn.saveDriverProfile(DriverDataParser().parseProfile(objectRes))
                                 cmn.setLoggedInVehicle(loggedin_vehicle)
                                 startActivity(Intent(ctx, DashboardDriver::class.java))
                                 overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                             } else {
                                 cmn.setIsLogged(false)
                                 cmn.showToast("we are working on it.")
                             }*/
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            cmn.showToast(""+e.toString())
                        }
                    } else {
                        // ALERT MESSAGE
                        Toast.makeText(
                            ctx,
                            "Not Got Response From Server.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            val request = HttpRequest("userlogin", parameters, handler, ctx , true)
            request.postAPIOwnServer()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

}