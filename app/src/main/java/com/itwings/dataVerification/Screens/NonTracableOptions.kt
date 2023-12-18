package com.itwings.dataVerification.Screens

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.itwings.dataVerification.*
import com.itwings.dataVerification.databinding.NonTracableOptionsBinding
import com.itwings.wastemanagement.Utills.Comman
import org.json.JSONException
import org.json.JSONObject


class NonTracableOptions : AppCompatActivity() {


    private lateinit var binding: NonTracableOptionsBinding
    lateinit var ctx: Activity
    lateinit var cmn: Comman
    var user : User = User()
    var selectedDistrictCode = ""
    var selectedBlockTownCode = ""
    var selectedWardVillageCode = ""
    var districtsList: ArrayList<String>? = ArrayList()
    var districtsCodeList: ArrayList<String>? = ArrayList()

    var blockTownsList: ArrayList<String>? = ArrayList()
    var blockTownsCodeList: ArrayList<String>? = ArrayList()

    var wardVillageList: ArrayList<String>? = ArrayList()
    var wardVillageCodeList: ArrayList<String>? = ArrayList()

    companion object{
        var familyInfo : FamilyMemberModel = FamilyMemberModel()
        var lcCode : String = ""
        var mLatitude: Double = 0.0
        var mLongitude: Double = 0.0

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getSupportActionBar() != null) {
            getSupportActionBar()?.hide();
        }
        binding = DataBindingUtil.setContentView(this, R.layout.non_tracable_options)
        ctx = this
        cmn = Comman(ctx)
        user = cmn.getUser()

        getDistricts()


        binding.btcancel.setOnClickListener {
            finish()
        }
        binding.btsubmit.setOnClickListener {
            if (selectedDistrictCode.isEmpty()){
                cmn.showToast("Please Select District")
            }else if (selectedBlockTownCode.isEmpty()){
                cmn.showToast("Please Select Block/Towns")
            }else if (selectedWardVillageCode.isEmpty()){
                cmn.showToast("Please Select Ward/Village")
            }else{
                updateNotTracable()
            }
        }

    }


    private fun getDistricts() {
        val parameters = JSONObject()
        try {
            parameters.put("mobileno", user.mobile)
            parameters.put("LoginTxnID", user.loginTxnID)
            parameters.put("lccode", lcCode)
            val handler: Handler = object : Handler() {
                override fun handleMessage(msg: Message) {
                    val aResponse = msg.data.getString("message")
                    if (null != aResponse) {
                        try {
                            val objectRes = JSONObject(aResponse)
                            var status = objectRes.optString("status")
                            if (status.equals("working")){
                                var message = objectRes.optJSONObject("message")
                                if (message.optString("status").equals("Successfull")){
                                    var result = message.optJSONObject("result")
                                    var districtList = result.optJSONArray("districtList")
                                    for (i in 0..districtList.length()-1) {
                                        val data = districtList.optJSONObject(i)
                                        // cmn.printLog("data new ---"+data.optString("pollBoothCode"))
                                        districtsList!!.add("" + data.optString("dName"))
                                        districtsCodeList!!.add("" + data.optString("dCode"))
                                    }
                                    initDistrictsSpinner()
                                    binding.spinnerBlockTowns.visibility = View.GONE
                                    binding.spinnerWardVillageList.visibility = View.GONE

                                }else if (message.optString("status").equals("Failed")){
                                    cmn.showToast("" + message.optString("message"))
                                }else{
                                    cmn.showToast("" + message.optString("message"))
                                }
                            }


                        } catch (e: JSONException) {
                            e.printStackTrace()
                            cmn.showToast("" + e.toString())
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

            val request = HttpRequest("api/Districts", parameters, handler, ctx)
            request.postAPI()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun initDistrictsSpinner() {

        /*lcCodeList!!.add("86221")*/

        binding.spinnerDistrict.setItem(districtsList!!)
        binding.spinnerDistrict.setOnItemSelectedListener(object :
                AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
            ) {
                selectedDistrictCode = districtsCodeList!!.get(position)
                getBlockTowns()
                selectedBlockTownCode = ""
                selectedWardVillageCode = ""
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        })
    }

    private fun getBlockTowns() {
        val parameters = JSONObject()
        try {
            parameters.put("mobileno", user.mobile)
            parameters.put("LoginTxnID", user.loginTxnID)
            parameters.put("lccode", lcCode)
            parameters.put("DCode", selectedDistrictCode)
            val handler: Handler = object : Handler() {
                override fun handleMessage(msg: Message) {
                    val aResponse = msg.data.getString("message")
                    if (null != aResponse) {
                        try {
                            val objectRes = JSONObject(aResponse)
                            var status = objectRes.optString("status")
                            if (status.equals("working")){
                                var message = objectRes.optJSONObject("message")
                                if (message.optString("status").equals("Successfull")){
                                    var result = message.optJSONObject("result")
                                    var districtList = result.optJSONArray("blockTownList")
                                    for (i in 0..districtList.length()-1) {
                                        val data = districtList.optJSONObject(i)
                                        // cmn.printLog("data new ---"+data.optString("pollBoothCode"))
                                        blockTownsList!!.add("" + data.optString("blockTownName"))
                                        blockTownsCodeList!!.add("" + data.optString("btCode"))
                                    }
                                    initBlockTownSpinner()
                                    binding.spinnerBlockTowns.visibility = View.VISIBLE
                                }else if (message.optString("status").equals("Failed")){
                                    cmn.showToast("" + message.optString("message"))
                                }else{
                                    cmn.showToast("" + message.optString("message"))
                                }
                            }


                        } catch (e: JSONException) {
                            e.printStackTrace()
                            cmn.showToast("" + e.toString())
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

            val request = HttpRequest("api/BlockTowns", parameters, handler, ctx)
            request.postAPI()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun initBlockTownSpinner() {

        /*lcCodeList!!.add("86221")*/

        binding.spinnerBlockTowns.setItem(blockTownsList!!)
        binding.spinnerBlockTowns.setOnItemSelectedListener(object :
                AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
            ) {
                selectedBlockTownCode = blockTownsCodeList!!.get(position)
                getWardVillage()
                selectedWardVillageCode = ""
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        })
    }

    private fun getWardVillage() {
        val parameters = JSONObject()
        try {
            parameters.put("mobileno", user.mobile)
            parameters.put("LoginTxnID", user.loginTxnID)
            parameters.put("lccode", lcCode)
            parameters.put("DCode", selectedDistrictCode)
            parameters.put("BTCode", selectedBlockTownCode)
            val handler: Handler = object : Handler() {
                override fun handleMessage(msg: Message) {
                    val aResponse = msg.data.getString("message")
                    if (null != aResponse) {
                        try {
                            val objectRes = JSONObject(aResponse)
                            var status = objectRes.optString("status")
                            if (status.equals("working")){
                                var message = objectRes.optJSONObject("message")
                                if (message.optString("status").equals("Successfull")){
                                    var result = message.optJSONObject("result")
                                    var districtList = result.optJSONArray("wardVillageList")
                                    for (i in 0..districtList.length()-1) {
                                        val data = districtList.optJSONObject(i)
                                        // cmn.printLog("data new ---"+data.optString("pollBoothCode"))
                                        wardVillageList!!.add("" + data.optString("wardVillageName"))
                                        wardVillageCodeList!!.add("" + data.optString("wvCode"))
                                    }
                                    initWardVillageSpinner()
                                    binding.spinnerWardVillageList.visibility = View.VISIBLE
                                }else if (message.optString("status").equals("Failed")){
                                    cmn.showToast("" + message.optString("message"))
                                }else{
                                    cmn.showToast("" + message.optString("message"))
                                }
                            }


                        } catch (e: JSONException) {
                            e.printStackTrace()
                            cmn.showToast("" + e.toString())
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

            val request = HttpRequest("api/WardVillage", parameters, handler, ctx)
            request.postAPI()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun initWardVillageSpinner() {

        /*lcCodeList!!.add("86221")*/

        binding.spinnerWardVillageList.setItem(blockTownsList!!)
        binding.spinnerWardVillageList.setOnItemSelectedListener(object :
                AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
            ) {
                selectedWardVillageCode = wardVillageCodeList!!.get(position)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        })
    }


    private fun updateNotTracable() {
        val parameters = JSONObject()
        try {
            parameters.put("LoginTxnID", user.loginTxnID)
            parameters.put("MemberId", familyInfo.newMemberID)
            parameters.put("UpdatedBy", user.mobile)
            parameters.put("Latitude", mLatitude)
            parameters.put("Longitude", mLongitude)
            parameters.put("Flag", "N")
            parameters.put("Remarks", "Shifted inside Haryana")
            parameters.put("DCode", selectedDistrictCode)
            parameters.put("BTCode", selectedBlockTownCode)
            parameters.put("WVCode", selectedWardVillageCode)

            val handler: Handler = object : Handler() {
                override fun handleMessage(msg: Message) {
                    val aResponse = msg.data.getString("message")
                    if (null != aResponse) {
                        try {
                            val objectRes = JSONObject(aResponse)
                            if(objectRes.optString("status").equals("working")){
                                var message = objectRes.optJSONObject("message")
                                if (message.optString("status").equals("Successfull")){
                                    var result = message.optString("result")
                                    if (result.equals("1")){
                                        cmn.showToast("" + message.optString("message"))
                                        MemberDOBVerification.dataUpdater!!.reloadData()
                                        finish()
                                    }else{
                                        cmn.showToast("You have not survey 80% of the signed family.")
                                    }
                                }else if (message.optString("status").equals("Failed")){
                                    cmn.showToast("" + message.optString("message"))
                                }else{
                                    cmn.showToast("" + message.optString("message"))
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            cmn.showToast("" + e.toString())
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

            val request = HttpRequest("api/MarkAsNonTraceable", parameters, handler, ctx)
            request.postAPI()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

}