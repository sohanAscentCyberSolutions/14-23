package com.itwings.dataVerification.Screens

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
/*import com.esri.arcgisruntime.concurrent.ListenableFuture
import com.esri.arcgisruntime.data.ArcGISFeature
import com.esri.arcgisruntime.data.FeatureEditResult
import com.esri.arcgisruntime.data.ServiceFeatureTable
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.geometry.SpatialReferences*/
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.itwings.dataVerification.*
import com.itwings.dataVerification.Adepters.FamilyListDOBAdepter
import com.itwings.dataVerification.databinding.ActivityMemberDobVerificationBinding
import com.itwings.wastemanagement.Utills.Comman
import org.json.JSONException
import org.json.JSONObject
import kotlin.collections.ArrayList


class MemberDOBVerification : AppCompatActivity() , DataUpdater {
    lateinit var binding: ActivityMemberDobVerificationBinding
    lateinit var ctx: Activity
    lateinit var cmn: Comman
    var user : User = User()
    var lcCodeList: ArrayList<String>? = ArrayList()
    var phaseList: ArrayList<String>? = ArrayList()
    var dataList: java.util.ArrayList<FamilyMemberModel> = java.util.ArrayList<FamilyMemberModel>()
    var dataListFilter: java.util.ArrayList<FamilyMemberModel> = java.util.ArrayList<FamilyMemberModel>()

    lateinit var gps : GPSTracker
    lateinit var adepter : FamilyListDOBAdepter
   /* lateinit var mServiceMarkAsDead: ServiceFeatureTable
    private var featureMarkAsDead: ArcGISFeature? = null*/
    var selectedLcCode = ""
    var selectedPhase = ""
    private var mLatitude: Double = 0.0
    private var mLongitude: Double = 0.0


    companion object{
       var dataUpdater : DataUpdater? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getSupportActionBar() != null) {
            getSupportActionBar()?.hide();
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_member_dob_verification)
        ctx = this
        cmn = Comman(ctx)
        lcCodeList = ArrayList()
        dataUpdater = this
        user = cmn.getUser()
        gps =  GPSTracker(this);
        binding.btBack.setOnClickListener {
            finish()
        }
        getLcCode()
        setUserData()
        binding.loginAt.setText(""+cmn.getCurrentDateWithTime())
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(ctx)
        binding.reclycalView.layoutManager = mLayoutManager
        binding.reclycalView.itemAnimator = DefaultItemAnimator()

        adepter = FamilyListDOBAdepter(
                this,
                dataListFilter,
                object : FamilyListDOBAdepter.ClickAdepterListener {
                    override fun membernotTracableClicked(v: View?, position: Int) {
                       // updateNotTracableOrDead(dataListFilter.get(position) , false)
                       // submitDetailsMarkAsDead(dataListFilter.get(position) , false)
                        showNonTracableOptions(dataListFilter.get(position))
                    }

                    override fun btDOBVerificationClicked(v: View?, position: Int) {
                        FillDOBInfo.familyInfo = dataListFilter.get(position)
                        FillDOBInfo.lcCode = selectedLcCode
                        FillDOBInfo.mLatitude = mLatitude
                        FillDOBInfo.mLongitude = mLongitude
                        startActivity(Intent(applicationContext, FillDOBInfo::class.java))
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    }

                    override fun markAsDeadClicked(v: View?, position: Int) {
                        //submitDetailsMarkAsDead(dataListFilter.get(position) , true , "")
                      //  updateNotTracableOrDead(dataListFilter.get(position) , true)
                    }

                })

        binding.reclycalView.adapter = adepter

        binding.etFilter.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                dataListFilter.clear()
                val filterStr = binding.etFilter.text.toString().toLowerCase()
                if (filterStr.isEmpty()) {
                    dataListFilter.addAll(dataList)
                } else {
                    for (model in dataList) {
                        if (model.newFamilyID!!.toLowerCase()
                                        .startsWith(filterStr) || model.mobileno!!.toLowerCase()
                                        .startsWith(filterStr) || model.name!!.toLowerCase().startsWith(filterStr)
                        ) {
                            dataListFilter.add(model)
                        }
                    }
                }
                if (adepter != null) {
                    adepter.notifyDataSetChanged()
                }
                Log.e("text changed", "records found" + dataListFilter.size)
            }
        })
        getLocation()
    }

    fun showNonTracableOptions(familyInfo : FamilyMemberModel ){
        val builder = MaterialAlertDialogBuilder(ctx, R.style.MaterialThemeDialog)
        var currentselection : Int = -1
        // dialog title
        builder.setTitle("Select An Option")
        val namesList  = resources.getStringArray(R.array.nonTracableOptions)
        builder.setSingleChoiceItems(
                namesList, // array
                -1 // initial selection (-1 none)
        ){ dialog, i ->
        }
        // alert dialog other buttons
        builder.setNegativeButton("Cancel", null)

        // set dialog non cancelable
        builder.setCancelable(false)
        // finally, create the alert dialog and show it
        val dialog = builder.create()
        dialog.show()

        // initially disable the positive button
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false

        // dialog list item click listener
        dialog.listView.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->
                    // enable positive button when user select an item
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                            .isEnabled = position != -1
                    currentselection = position
                    if (currentselection !=-1){
                        if (currentselection == 0){
                            NonTracableOptions.familyInfo = familyInfo
                            NonTracableOptions.lcCode = selectedLcCode
                            NonTracableOptions.mLatitude = mLatitude
                            NonTracableOptions.mLongitude = mLongitude
                            startActivity(Intent(applicationContext, NonTracableOptions::class.java))
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                        }else if (currentselection == 1){
                            //submitDetailsMarkAsDead(familyInfo , false , "Shifted to Outside Haryana.")
                              // updateNotTracable("Shifted Outside Haryana",familyInfo)
                        }else if (currentselection == 2){
                            //submitDetailsMarkAsDead(familyInfo , false , "Shifted to inside Haryana But address not known.")
                          //  updateNotTracable("Shifted inside Haryana WVCode is null or Blank",familyInfo)
                        }else if (currentselection == 3){
                            //submitDetailsMarkAsDead(familyInfo , false , "Shifted to unknown place.")
                            //  updateNotTracable("Shifted inside Haryana WVCode is null or Blank",familyInfo)
                        }
                        dialog.dismiss()
                    }
                }
    }

    fun getLocation(){
        if (gps.canGetLocation()) {
            mLatitude = gps.latitude
            mLongitude = gps.longitude
            Handler().postDelayed(Runnable {
                mLatitude = gps.latitude
                mLongitude = gps.longitude
                Log.e("latitude = ", "" + mLatitude)
                Log.e("longitude = ", "" + mLongitude)
            }, 3000)

        } else gps.showSettingsAlert(this)
    }

    private fun initLCSpinner() {

        /*lcCodeList!!.add("86221")*/

        binding.spinnerLcCode.setItem(lcCodeList!!)
        binding.spinnerLcCode.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedLcCode = lcCodeList!!.get(position)
                getMembers()
               // getPhase()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        })
    }

    private fun initPhaseSpinner() {
        /* familyTypeList!!.add("phase 1")
         familyTypeList!!.add("phase 2")
         familyTypeList!!.add("phase 3")
         familyTypeList!!.add("phase 4")
         familyTypeList!!.add("phase 5")*/
        binding.spinnerPhase.setItem(phaseList!!)
        binding.spinnerPhase.setOnItemSelectedListener(object :
                AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
            ) {
                binding.dataView.visibility = View.VISIBLE
                selectedPhase = phaseList!!.get(position)
                getMembers()
            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        })
    }

    fun setUserData(){
        val user = cmn.getUser()
        binding.welcomeName.setText("Welcome," + user.username)
        binding.tvMobile.setText("" + user.mobile)
        binding.tvDistrict.setText("" + user.district)
        binding.town.setText("" + user.bTown)
        if (user.role.equals(cmn.roleZonal)){
            binding.layWardVillage.visibility = View.GONE
        }else{
            binding.village.setText("" + cmn.getSelectedLGDVillage())
        }
        binding.tvdesignation.setText("Designation : "+cmn.getFullDesignation())
       /* binding.welcomeName.setText("Welcome," + "Ranbir Singh")
        binding.tvMobile.setText("7973972631" )
        binding.tvDistrict.setText("SONIPAT")
        binding.town.setText("GOHANA BL" )
        binding.village.setText("Niat")*/
    }

    private fun getLcCode() {
        val parameters = JSONObject()
        try {
            parameters.put("Mobileno", user.mobile)
            parameters.put("LoginTxnID", user.loginTxnID)
            val handler: Handler = object : Handler() {
                override fun handleMessage(msg: Message) {
                    val aResponse = msg.data.getString("message")
                    if (null != aResponse) {
                        try {
                            val objectRes = JSONObject(aResponse)
                            if(objectRes.optString("status").equals("working")){
                                var message = objectRes.optJSONObject("message")
                                if (message.optString("status").equals("Success")){
                                    var result = message.optJSONArray("result")
                                    for (i in 0..result.length()-1) {
                                        val data = result.optJSONObject(i)
                                       // cmn.printLog("data new ---"+data.optString("pollBoothCode"))
                                        lcCodeList!!.add("" + data.optString("pollBoothCode"))
                                    }
                                    initLCSpinner()
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

            val request = HttpRequest("api/getllccode", parameters, handler, ctx)
            request.postAPI()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
    private fun getPhase() {
        val parameters = JSONObject()
        try {
           /* parameters.put("mobileno", user.mobile)
            parameters.put("lccode", selectedLcCode)
            parameters.put("LoginTxnID", user.loginTxnID)*/
            parameters.put("mobileno", "9729517527")
            parameters.put("lccode", "97216")
            parameters.put("LoginTxnID", "2209141030004793")
            val handler: Handler = object : Handler() {
                override fun handleMessage(msg: Message) {
                    val aResponse = msg.data.getString("message")
                    if (null != aResponse) {
                        try {
                            val objectRes = JSONObject(aResponse)
                            if(objectRes.optString("status").equals("working")){
                                phaseList!!.clear()
                                var message = objectRes.optJSONObject("message")
                                if (message.optString("status").equals("Successfull")){
                                    var result = message.optJSONObject("result")
                                    var lstPhase = result.optJSONArray("lstPhase")
                                    for (i in 0..lstPhase.length()-1) {
                                        val data = lstPhase.optJSONObject(i)
                                        // cmn.printLog("data new ---"+data.optString("pollBoothCode"))
                                        phaseList!!.add("" + data.optString("phaseNo"))
                                    }
                                    initPhaseSpinner()
                                    binding.spinnerPhase.visibility = View.VISIBLE
                                }else if (message.optString("status").equals("Failed")){
                                    var result = message.optJSONObject("result")
                                    cmn.showToast("" + result.optString("message"))
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

            val request = HttpRequest("api/DOBPhase", parameters, handler, ctx)
            request.postAPI()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun updateData(){
        dataListFilter.clear()
        dataListFilter.addAll(dataList)
        if (adepter != null) {
            adepter.notifyDataSetChanged()
        }
        binding.dataView.visibility = View.VISIBLE
    }

    private fun getMembers() {
        val parameters = JSONObject()
        try {
            parameters.put("mobileNo", user.mobile)
            parameters.put("LoginTxnID", user.loginTxnID)
            parameters.put("lcCode", selectedLcCode)
           /* parameters.put("mobileNo", "9729517527")
            parameters.put("lcCode", "97216")
            parameters.put("LoginTxnID", "2209141030004793")*/
          //  parameters.put("Phase", selectedPhase)

            val handler: Handler = object : Handler() {
                override fun handleMessage(msg: Message) {
                    val aResponse = msg.data.getString("message")
                    if (null != aResponse) {
                        try {
                            val objectRes = JSONObject(aResponse)
                            if(objectRes.optString("status").equals("working")){
                                var message = objectRes.optJSONObject("message")
                                if (message.optString("status").equals("Successfull")){
                                    var result = message.optJSONObject("result")
                                    var familyList = result.optJSONArray("userDetailsList")
                                    dataList.clear()
                                    for (i in 0..familyList.length()-1) {
                                        val data = familyList.optJSONObject(i)
                                        dataList.add(Parser().parseFamilyMemberDOB(data))
                                    }
                                    updateData()
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

            val request = HttpRequest("api/GetDataForDobVerification", parameters, handler, ctx)
            request.postAPIRow()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
       // setUpFeatureLayerMarkAsDead()
    }

  /*  fun setUpFeatureLayerMarkAsDead(){
        mServiceMarkAsDead =
                ServiceFeatureTable(resources.getString(R.string.gis_mark_as_dead))
        mServiceMarkAsDead.loadAsync()
        mServiceMarkAsDead.addDoneLoadingListener(
                Runnable {
                    featureMarkAsDead =
                            mServiceMarkAsDead.createFeature() as ArcGISFeature?
                    cmn.printLog("service Feature loaded")
                })
    }*/


   /* private fun submitDetailsMarkAsDead(data : FamilyMemberModel , isDead : Boolean , remarks: String) {
        var dialog = ProgressDialog(ctx)
        dialog.setTitle(getString(R.string.app_name))
        dialog.setMessage("Please wait")
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

        var attributes = HashMap<String, Any>()
        attributes.put("logintxnid", user.loginTxnID.toString())
        attributes.put("memberid",data.newMemberID.toString())
        attributes.put("updatedby",user.mobile.toString())
        attributes.put("latitude", mLatitude)
        attributes.put("longitude", mLongitude)
        if (isDead){
            attributes.put("status_d_t", "D")
        }else{
            attributes.put("status_d_t", "N")
        }
       // val newPoint = Point(UpdateMemberCount.mLongitude, UpdateMemberCount.mLatitude, SpatialReferences.getWgs84())
        featureMarkAsDead!!.attributes.putAll(attributes)
       // featureMarkAsDead!!.geometry = newPoint

        cmn.printLog("latitude = " + UpdateMemberCount.mLatitude)
        cmn.printLog("longitude = " + UpdateMemberCount.mLongitude)

        cmn.printLog("" + attributes.toString())

        val addFeatureFuture: ListenableFuture<Void> = mServiceMarkAsDead.addFeatureAsync(
            featureMarkAsDead
        )
        addFeatureFuture.addDoneListener(Runnable {
            try {
                addFeatureFuture.get()
                if (mServiceMarkAsDead is ServiceFeatureTable) {
                    val applyEditsFuture: ListenableFuture<List<FeatureEditResult>> =
                        mServiceMarkAsDead.applyEditsAsync()
                    applyEditsFuture.addDoneListener(Runnable {
                        try {
                            val featureEditResults: List<FeatureEditResult> = applyEditsFuture.get()
                            dialog.dismiss()
                            if (isDead){
                                updateNotTracableOrDead(data , isDead)
                            }else{
                                updateNotTracable(remarks , data)
                            }
                        } catch (e: java.lang.Exception) {
                            dialog.dismiss()
                            cmn.showToast(""+e.localizedMessage)
                        }
                    })
                } else {
                    dialog.dismiss()
                    cmn.showToast("Arc GIS Error !!")
                }
            } catch (e: InterruptedException) {
                dialog.dismiss()
                cmn.showToast(""+e.localizedMessage)
            } catch (e: ExecutionException) {
                dialog.dismiss()
                cmn.showToast(""+e.localizedMessage)
            }
        })
    }
*/
    private fun updateNotTracableOrDead(data : FamilyMemberModel , isDead : Boolean) {
        val parameters = JSONObject()
        try {
            parameters.put("LoginTxnID", user.loginTxnID)
            parameters.put("MemberId", data.newMemberID)
            parameters.put("UpdatedBy", user.mobile)
            parameters.put("Latitude", mLatitude)
            parameters.put("Longitude", mLongitude)
            parameters.put("Phase", data.phase)
            if (isDead){
                parameters.put("Flag", "D")
            }else{
                parameters.put("Flag", "N")
            }

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
                                        getMembers()
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

            val request = HttpRequest("api/MarkAsDead", parameters, handler, ctx)
            request.postAPI()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun reloadData() {
        getMembers()
    }

    private fun updateNotTracable(remarks : String , data : FamilyMemberModel) {
        val parameters = JSONObject()
        try {
            parameters.put("LoginTxnID", user.loginTxnID)
            parameters.put("MemberId", data.newMemberID)
            parameters.put("UpdatedBy", user.mobile)
            parameters.put("mobileNumber", data.mobileno)
            parameters.put("Latitude", mLatitude)
            parameters.put("Longitude", mLongitude)
            parameters.put("Flag", "N")
            parameters.put("Remarks", remarks)
            parameters.put("DCode", "")
            parameters.put("BTCode", "")
            parameters.put("WVCode", "")
            parameters.put("Phase", data.phase)

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
                                        getMembers()
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