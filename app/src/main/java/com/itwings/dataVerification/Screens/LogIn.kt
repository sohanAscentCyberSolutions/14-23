package com.itwings.dataVerification.Screens

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.itwings.dataVerification.BuildConfig
import com.itwings.dataVerification.GPSTracker
import com.itwings.dataVerification.HttpRequest
import com.itwings.dataVerification.R
import com.itwings.dataVerification.databinding.ActivityLogInBinding
import com.itwings.wastemanagement.Utills.Comman
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import org.json.JSONException
import org.json.JSONObject


class LogIn : AppCompatActivity() {

    var latitude: Double = 0.0
    var longitude: Double = 0.0
    lateinit var gps: GPSTracker
    private lateinit var binding: ActivityLogInBinding
    lateinit var ctx: Activity
    lateinit var cmn: Comman

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getSupportActionBar() != null) {
            getSupportActionBar()?.hide();
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_log_in)
        ctx = this
        cmn = Comman(ctx)
      //  binding.usernameeditview.setText("7737691424")

        binding.usernameeditview.requestFocus()

        binding.loginAt.setText(""+cmn.getCurrentDateWithTime())

       checkAppUpdate()

        binding.btLogin.setOnClickListener {
            if (binding.usernameeditview.text.toString().length < 10){
                cmn.showToast("Please enter 10 digit mobile number")
            }else{
               // throw RuntimeException("Test Crash") // Force a crash
                sendOtp()
            }
        }
        methodWithPermissions()
       binding.appVersion.setText("App Version : "+BuildConfig.VERSION_NAME)
    }

    fun methodWithPermissions() = runWithPermissions(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    ) {
        gps = GPSTracker(this);
        getLocation()
    }

    private fun sendOtp() {
        val parameters = JSONObject()
        try {
            parameters.put("Mobileno", binding.usernameeditview.text.toString())
            parameters.put("DeptCode", "TEST")
            parameters.put("ServiceCode", "NIC")
            parameters.put("DeptKey", "984oyu3yt2")

           // parameters.put("user_lat", ""+latitude)
           // parameters.put("user_long", ""+longitude)

            val handler: Handler = object : Handler() {
                override fun handleMessage(msg: Message) {
                    val aResponse = msg.data.getString("message")
                   if (null != aResponse) {
                        try {
                            val objectRes = JSONObject(aResponse)
                            var result = objectRes.optJSONObject("result")
                            if(result.optString("status").equals("True")){
                                cmn.showToast("" + result.optString("message"))
                                cmn.setSetMobileNo(binding.usernameeditview.text.toString())
                                var txn = result.optString("txn")
                                startActivity(Intent(applicationContext, OTP::class.java)
                                    .putExtra("mobileNo", binding.usernameeditview.text.toString())
                                    .putExtra("txn", txn)
                                    .putExtra("latitude", ""+latitude)
                                    .putExtra("longitude", ""+longitude))
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            cmn.showToast("Invalid Response from server")
                        }
                    } else {
                        // ALERT MESSAGE
                        Toast.makeText(
                                ctx,
                                "Not Got Response From Server.",
                                Toast.LENGTH_SHORT).show()
                    }
                }
            }
            val request = HttpRequest("OTPRequestforLogin", parameters, handler, ctx)
            request.postAPI()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun checkAppUpdate() {
        val parameters = JSONObject()
        try {
            parameters.put("app_name", "Data_verification_Prod")
           /* if (HttpRequest.isStagingAPpp){
                parameters.put("app_name", "Data_verification")
            }else{
                parameters.put("app_name", "Data_verification_Prod")
            }*/
            val handler: Handler = object : Handler() {
                override fun handleMessage(msg: Message) {
                    val aResponse = msg.data.getString("message")
                    if (null != aResponse) {
                        try {
                            val objectRes = JSONObject(aResponse)
                            var responseDataArray = objectRes.optJSONArray("responseData")
                            if (responseDataArray != null){
                                var responseData = responseDataArray.optJSONObject(0)
                                var app_url: String = ""
                                if (HttpRequest.isStagingAPpp){
                                    app_url = responseData.getString("staging_url")
                                }else{
                                    app_url = responseData.getString("base_url") +"/"
                                }
                                var app_host_url : String = responseData.optString("app_host_url")
                                cmn.setAccessUrlGMDA(app_url)
                             //   cmn.setAccessUrlGMDA("https://onemapcitizens.gmda.gov.in/API_FamilyIdVerification/")
                                val latest_app_version: String = responseData.getString("priority_min_version")
                                val under_maintenance: String = responseData.getString("under_maintainence")
                                val under_maintenance_text: String = responseData.getString("under_maintainence_text")
                                match_version_and_show_dialog    (under_maintenance, under_maintenance_text, app_host_url, latest_app_version)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            cmn.showToast("Invalid Response from server")
                        }
                    } else {
                        // ALERT MESSAGE
                        Toast.makeText(
                                ctx,
                                "Not Got Response From Server.",
                                Toast.LENGTH_SHORT).show()
                    }
                }
            }
            val request = HttpRequest("", parameters, handler, ctx)
            request.postAPIAppUpdate()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
    private fun match_version_and_show_dialog(under_maintenance: String, under_maintenance_text: String, app_host_url: String, latest_app_version: String) {
        val versionName: String = BuildConfig.VERSION_NAME
        Log.d(TAG, "match_version_and_show_dialog: " + versionName.toString() + latest_app_version)
        if (versionName.toDouble() == latest_app_version.toDouble()) {
            Toast.makeText(this@LogIn, "Your Application is Updated", Toast.LENGTH_SHORT).show()
            if (cmn.getIsLogged() == true){
                startActivity(Intent(applicationContext, Dashboard::class.java))
                finish()
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
            if (under_maintenance == "YES") {
               // Toast.makeText(this@LogIn, "" + under_maintenance_text, Toast.LENGTH_SHORT).show()
            } else {
                //  new Handler().postDelayed(this::portal_login, 500);
            }
        } else {
            OpenNewVersion(app_host_url, app_host_url)
        }
    }

    private fun OpenNewVersion(google_play_url: String, apk_url: String) {
        val cancel_btn: Button
        val update_btn: Button
        val updateBox = Dialog(this@LogIn)
        updateBox.setContentView(R.layout.update_app_layout)
        updateBox.setCancelable(false)
        cancel_btn = updateBox.findViewById(R.id.btCancel)
        update_btn = updateBox.findViewById(R.id.btUpdate)
        update_btn.setOnClickListener { v ->
            if (apk_url.contains("https")) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(apk_url)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION
                startActivity(intent)
               // updateBox.dismiss()
               // finish()
            }else{
              cmn.showToast("Invalid Application Url !!")
            }
        }
        cancel_btn.setOnClickListener { v ->
            updateBox.dismiss()
            finish()
        }
        updateBox.show()
    }

    override fun onBackPressed() {
        val d = Dialog(ctx)
        d.window!!.attributes.windowAnimations = R.style.DialogAnimationFade
        d.setContentView(R.layout.successwithok)

        val Labeltitle = d.findViewById<TextView>(R.id.Labeltitle)
        Labeltitle.text = "Do you want to exit the application"

        d.findViewById<View>(R.id.btokk).setOnClickListener {
            d.dismiss()
            finish()
            finishAffinity()
        }

        d.show()
        d.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun getLocation() {

        if (gps.canGetLocation()) {

            latitude = gps.latitude
            longitude = gps.longitude

            if (latitude == 0.0 && longitude == 0.0){
                val cancel_btn: Button
                val msgTv: TextView
                val update_btn: Button
                val updateBox = Dialog(ctx)
                updateBox.setContentView(R.layout.update_app_layout)
                updateBox.setCancelable(false)
                msgTv = updateBox.findViewById(R.id.tvUpdateMsg)
                msgTv.setText("Unable To Fetch Current Location Please Try Again !!")
                cancel_btn = updateBox.findViewById(R.id.btCancel)
                cancel_btn.setText("OK")
                update_btn = updateBox.findViewById(R.id.btUpdate)
                update_btn.visibility = View.GONE
                cancel_btn.setOnClickListener { v ->
                    updateBox.dismiss()
                    finish()
                }
                updateBox.show()
            }else{
                try {
                    val isMockLocation = gps.location.isFromMockProvider
                    if (isMockLocation){
                        val cancel_btn: Button
                        val msgTv: TextView
                        val update_btn: Button
                        val updateBox = Dialog(ctx)
                        updateBox.setContentView(R.layout.update_app_layout)
                        updateBox.setCancelable(false)
                        msgTv = updateBox.findViewById(R.id.tvUpdateMsg)
                        msgTv.setText("Sorry Mock Location Detected Please use device location to process !!")
                        cancel_btn = updateBox.findViewById(R.id.btCancel)
                        update_btn = updateBox.findViewById(R.id.btUpdate)
                        cancel_btn.setText("OK")
                        update_btn.visibility = View.GONE
                        cancel_btn.setOnClickListener { v ->
                            updateBox.dismiss()
                            finish()
                        }
                        updateBox.show()
                    }else{
                        cmn.printLog("is mock location used : $isMockLocation")
                        latitude = gps.latitude
                        longitude = gps.longitude
                        Handler().postDelayed(Runnable {
                            latitude = gps.latitude
                            longitude = gps.longitude
                            Log.e("latitude = ", "" + latitude)
                            Log.e("longitude = ", "" + longitude)
                        }, 100)
                    }
                }catch (e : java.lang.Exception){
                    latitude = gps.latitude
                    longitude = gps.longitude
                }
            }
        } else gps.showSettingsAlert(this)
    }

}