package com.itwings.dataVerification.Screens

import android.Manifest
import android.app.Activity
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
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.itwings.dataVerification.*
import com.itwings.dataVerification.Adepters.CustomSpinnerAdapter
import com.itwings.dataVerification.Adepters.FamilyListAdepter
import com.itwings.dataVerification.Models.KeyValueModel
import com.itwings.dataVerification.databinding.ActivityFamilyIncomeVerificationBinding
import com.itwings.wastemanagement.Utills.Comman
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class FamilyIncomeVerification : AppCompatActivity() , DataUpdater {
    lateinit var binding: ActivityFamilyIncomeVerificationBinding
    lateinit var ctx: Activity
    lateinit var cmn: Comman
    var user : User = User()
    var type = 0;
    var lcCodeList: ArrayList<String>? = ArrayList()
    var familyTypeList: ArrayList<String>? = ArrayList()
    var dataListFresh: java.util.ArrayList<FamilyModel> = java.util.ArrayList<FamilyModel>()
    var blockSearchData: java.util.ArrayList<FamilyModel> = java.util.ArrayList<FamilyModel>()
    var dataListFilter: java.util.ArrayList<FamilyModel> = java.util.ArrayList<FamilyModel>()
    var dataListExisting: java.util.ArrayList<FamilyModel> = java.util.ArrayList<FamilyModel>()
    lateinit var gps : GPSTracker
    lateinit var adepter : FamilyListAdepter
    //lateinit var mServiceFeature: ServiceFeatureTable
    //private var feature: ArcGISFeature? = null
    var selectedLcCode = ""
    var isSearchByBlockActive = false
    var selectedFamilyType = ""
    private var mLatitude: Double = 0.0
    private var mLongitude: Double = 0.0

    private var isInitialScreen = true

   /* lateinit var mServiceFeatureNonTracable: ServiceFeatureTable
    private var featureNonTracable: ArcGISFeature? = null*/
    var blocks: java.util.ArrayList<String> = java.util.ArrayList<String>()
    var districts: java.util.ArrayList<String> = java.util.ArrayList<String>()
    companion object{
       var dataUpdater : DataUpdater? = null
    }
    lateinit var idAdapter : CustomSpinnerAdapter
    lateinit var districtAdpt : CustomSpinnerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getSupportActionBar() != null) {
            getSupportActionBar()?.hide();
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_family_income_verification)
        ctx = this
        cmn = Comman(ctx)
        lcCodeList = ArrayList()
        familyTypeList = ArrayList()
        binding.loginAt.setText(""+cmn.getCurrentDateWithTime())
        dataUpdater = this
        user = cmn.getUser()
        gps =  GPSTracker(this);
        binding.btBack.setOnClickListener {
            finish()
        }

        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 0) {
                    type = 0
                } else {
                    type = 1
                }
                updateData()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        binding.etFilter.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                dataListFilter.clear()
                val filterStr = binding.etFilter.text.toString().toLowerCase()
                if (filterStr.isEmpty()) {
                    if (type == 0){
                        if (isSearchByBlockActive){
                            dataListFilter.addAll(blockSearchData)
                        }else{
                            dataListFilter.addAll(dataListFresh)
                        }
                    }else{
                        dataListFilter.addAll(dataListExisting)
                    }
                } else {
                    if (type == 0){
                        if (isSearchByBlockActive){
                            for (model in blockSearchData) {
                                if (model.beneficiaryId.toLowerCase()
                                        .contains(filterStr)
                                    || model.address.toLowerCase()
                                        .contains(filterStr)
                                    || model.name.toLowerCase().startsWith(filterStr)
                                    || model.wardVillage.toLowerCase().startsWith(filterStr)
                                    || model.hoF_Name.toLowerCase().startsWith(filterStr)

                                ) {
                                    dataListFilter.add(model)
                                }
                            }
                        }else{
                            for (model in dataListFresh) {
                                if (model.beneficiaryId.toLowerCase()
                                        .contains(filterStr)
                                    || model.address.toLowerCase()
                                        .contains(filterStr)
                                    || model.name.toLowerCase().startsWith(filterStr)
                                    || model.wardVillage.toLowerCase().startsWith(filterStr)
                                    || model.hoF_Name.toLowerCase().startsWith(filterStr)

                                ) {
                                    dataListFilter.add(model)
                                }
                            }
                        }
                    }else{
                        for (model in dataListExisting) {
                            if (model.beneficiaryId.toLowerCase()
                                    .contains(filterStr)
                                || model.name.toLowerCase().startsWith(filterStr)
                                || model.wardVillage.toLowerCase().startsWith(filterStr)
                                || model.hoF_Name.toLowerCase().startsWith(filterStr)
                            ) {
                                dataListFilter.add(model)
                            }
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
        /*if (user.role.equals(cmn.roleZonal)){
            binding.spinnerLcCode.visibility = View.GONE
            binding.tabLayout.visibility = View.GONE
            type = 0
            getMembers(true)
        }else{
            getLcCode()
        }*/

       /* binding.floating_short_button.setOnClickListener {

        }
*/
        getMembers(true)
    }

    fun setupBlockFilter(){
        // access the spinner
        if (binding.spFilterBlock != null) {
            idAdapter = CustomSpinnerAdapter(ctx, blocks)
            binding.spFilterBlock.setAdapter(idAdapter)
            binding.spFilterBlock.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                        dataListFilter.clear()
                        //  blocks.add("Search By Block/Town")
                        //  blocks.add("Clear Filter")
                        if (position == 0 || position == 1){
                            blockSearchData.clear()
                            isSearchByBlockActive = false
                            for (model in dataListFresh) {
                                if (binding.spFilterDistrict.selectedItem.toString().toLowerCase()
                                        .equals(
                                            model.district.toLowerCase()
                                        )){
                                    dataListFilter.add(model)
                                }
                            }
                        }else {
                            isSearchByBlockActive = true
                            for (model in dataListFresh) {
                                if (model.blockTown.toLowerCase()
                                        .equals(blocks.get(position).toLowerCase())) {
                                    if (binding.spFilterDistrict.selectedItem.toString().toLowerCase()
                                            .equals(
                                                model.district.toLowerCase()
                                            )){
                                        dataListFilter.add(model)
                                    }
                                  //  dataListFilter.add(model)
                                }
                            }
                            blockSearchData.addAll(dataListFilter)
                        }
                        if (adepter != null) {
                            adepter.notifyDataSetChanged()
                        }

                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }
    }

    fun setupDistrictFilter(){
        // access the spinner
        if (binding.spFilterDistrict != null) {
            districts.addAll(user.district)
            districtAdpt = CustomSpinnerAdapter(ctx, districts)
            binding.spFilterDistrict.setAdapter(districtAdpt)
            binding.spFilterDistrict.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                    dataListFilter.clear()
                    blocks.clear()
                    blocks.add("Search By Block-Town")
                    blocks.add("Clear Filter")
                    val obj1 = KeyValueModel()
                    for (model in dataListFresh) {
                        if (binding.spFilterDistrict.selectedItem.toString().toLowerCase()
                                .equals(model.district.toLowerCase())) {
                            dataListFilter.add(model)
                            if (!blocks.contains(model.blockTown)){
                                blocks.add(model.blockTown)
                            }
                        }
                    }
                    setupBlockFilter()
                    if (adepter != null) {
                        adepter.notifyDataSetChanged()
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
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
  /*  fun setUpFeatureLayerNonTracable(){
        mServiceFeatureNonTracable =
            ServiceFeatureTable(resources.getString(R.string.gis_update_non_tracable_income))
        mServiceFeatureNonTracable.loadAsync()
        mServiceFeatureNonTracable.addDoneLoadingListener(
            Runnable {
                featureNonTracable =
                    mServiceFeatureNonTracable.createFeature() as ArcGISFeature?
                cmn.printLog("service Feature loaded")
            })
    }*/

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
      //  setUpFeatureLayerNonTracable()
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
                binding.spinnerFamilyType.visibility = View.VISIBLE
              //  initFamilyTypeSpinner()
                selectedLcCode = lcCodeList!!.get(position)
                getFamilyType()
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        })
    }

    private fun initFamilyTypeSpinner() {
       /* familyTypeList!!.add("phase 1")
        familyTypeList!!.add("phase 2")
        familyTypeList!!.add("phase 3")
        familyTypeList!!.add("phase 4")
        familyTypeList!!.add("phase 5")*/
        binding.spinnerFamilyType.setItem(familyTypeList!!)
        binding.spinnerFamilyType.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
binding.dataView.visibility = View.VISIBLE
                selectedFamilyType = familyTypeList!!.get(position)
                getMembers(true)
                getMembers(false)
            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        })
    }

    fun setUserData(){
        binding.welcomeName.setText("Welcome," + user.username)
        binding.tvMobile.setText("" + cmn.getMobileNo())
     //   binding.tvDistrict.setText("" + user.district)
        setupDistrictFilter()
        var dist : String = user.district.toString().replace("[" , "")
        dist = dist.replace("]" , "")
        dist = dist.replace("," ,"-")
        binding.tvGroup.setText("" + dist)
        var bTown : String = user.bTown.toString().replace("[" , "")
        bTown = bTown.replace("]" , "")
        bTown = bTown.replace("," ,"-")
        binding.town.setText("" + bTown)
        var wardVillage : String = user.wardVillage.toString().replace("[" , "")
        wardVillage = wardVillage.replace("]" , "")
        wardVillage = wardVillage.replace("," ,"-")
        binding.town.setText("" + bTown)
        binding.village.setText("" + wardVillage)
      //  binding.tvGroup.setText("Group : "+user.group)
       /* val user = cmn.getUser()
        binding.welcomeName.setText("Welcome," + user.username)

        binding.tvDistrict.setText("" + user.district)
        binding.town.setText("" + user.bTown)
        if (user.role.equals(cmn.roleZonal)){
            binding.layWardVillage.visibility = View.GONE
        }else{
            binding.village.setText("" + cmn.getSelectedLGDVillage())
        }
        binding.tvdesignation.setText("Designation : "+cmn.getFullDesignation())*/
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

    private fun getFamilyType() {
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
                                familyTypeList!!.clear()
                                var message = objectRes.optJSONObject("message")
                                if (message.optString("status").equals("Successfull")){
                                    var result = message.optJSONArray("result")
                                    for (i in 0..result.length()-1) {
                                        val data = result.optJSONObject(i)
                                        // cmn.printLog("data new ---"+data.optString("pollBoothCode"))
                                        familyTypeList!!.add("" + data.optString("code"))
                                    }
                                    initFamilyTypeSpinner()
                                    binding.spinnerFamilyType.visibility = View.VISIBLE
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

            val request = HttpRequest("api/getfamilytype", parameters, handler, ctx)
            request.postAPI()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun updateData(){
        dataListFilter.clear()
       if (type == 0){
           for (i in 0..dataListFresh.size-1) {
               var selectedDistrict = binding.spFilterDistrict.selectedItem.toString().toLowerCase()
               if (selectedDistrict.equals(dataListFresh.get(i).district.toLowerCase())){
                   dataListFilter.addAll(dataListFresh)
               }
           }
       }else{
           dataListFilter.addAll(dataListExisting)
       }
        adepter = FamilyListAdepter(
                this,
                dataListFilter,
                object : FamilyListAdepter.ClickAdepterListener {
                    override fun membernotTracableClicked(v: View?, position: Int) {
                        getMemberTracableAllow(dataListFilter.get(position))
                    }

                    override fun enterInfoClicked(v: View?, position: Int) {
                        FillFamilyInfo.isFresh = true
                        FillFamilyInfo.familyInfo = dataListFilter.get(position)
                        //  FillFamilyInfo.familyLcCode = selectedLcCode
                        //  FillFamilyInfo.familyType = selectedFamilyType
                        startActivity(Intent(applicationContext, FillFamilyInfo::class.java))
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                       /* if (user.role.equals(cmn.roleZonal)){
                            methodWithPermissionsBoundryMap(dataListFilter.get(position))
                        }else{
                            FillFamilyInfo.isFresh = binding.tabLayout.selectedTabPosition == 0
                            FillFamilyInfo.familyInfo = dataListFilter.get(position)
                            FillFamilyInfo.familyLcCode = selectedLcCode
                            FillFamilyInfo.familyType = selectedFamilyType
                            startActivity(Intent(applicationContext, FillFamilyInfo::class.java))
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                        }*/
                    }

                    override fun updateFamilyMemberCountClicked(v: View?, position: Int) {
                        UpdateMemberCount.familyInfo = dataListFilter.get(position)
                        UpdateMemberCount.familyLcCode = selectedLcCode
                        UpdateMemberCount.mLatitude = mLatitude
                        UpdateMemberCount.mLongitude = mLongitude
                        UpdateMemberCount.familyType = selectedFamilyType
                        UpdateMemberCount.isFresh = binding.tabLayout.selectedTabPosition == 0
                        startActivity(Intent(applicationContext, UpdateMemberCount::class.java))
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    }

                    override fun membersClicked(v: View?, position: Int) {
                        MembersDetails.familyInfo = dataListFilter.get(position)
                        startActivity(Intent(applicationContext, MembersDetails::class.java))
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    }

                })
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(ctx)
        binding.reclycalView.layoutManager = mLayoutManager
        binding.reclycalView.itemAnimator = DefaultItemAnimator()
        binding.reclycalView.adapter = adepter
        binding.dataView.visibility = View.VISIBLE
        if (adepter != null) {
            adepter.notifyDataSetChanged()
        }

    }

    fun methodWithPermissionsBoundryMap(data : FamilyModel) = runWithPermissions(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    ) {
       /* val intent = Intent(applicationContext, BoundryMap::class.java)
        BoundryMap.familyInfo = data
        intent.putExtra("isIncomeVerification" , true)
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)*/
    }
/*

    private fun getMembers(isFresh : Boolean) {
        val parameters = JSONObject()
        try {
            parameters.put("Mobileno", user.mobile)
            parameters.put("LoginTxnID", user.loginTxnID)
            if (user.role.equals(cmn.roleZonal)){
                parameters.put("PollBoothCode", cmn.roleZonal)
                parameters.put("familyType", "")
            }else{
                parameters.put("PollBoothCode", selectedLcCode)
                parameters.put("familyType", selectedFamilyType)
            }
            if (isFresh){
                parameters.put("FamilyStatus", "Fresh")
            }else{
                parameters.put("FamilyStatus", "Existing")
            }

            val handler: Handler = object : Handler() {
                override fun handleMessage(msg: Message) {
                    val aResponse = msg.data.getString("message")
                    if (null != aResponse) {
                        try {
                            val message = JSONObject(aResponse)
                            if (message.optString("status").equals("Successfull")){
                                var result = message.optJSONObject("result")
                                var familyList = result.optJSONArray("familyList")
                                if (isFresh){
                                    dataListFresh.clear()
                                }else{
                                    dataListExisting.clear()
                                }
                                for (i in 0..familyList.length()-1) {
                                    val data = familyList.optJSONObject(i)
                                    if (isFresh){
                                        dataListFresh.add(Parser().parseFamily(data))
                                    }else{
                                        dataListExisting.add(Parser().parseFamily(data))
                                    }
                                }
                                updateData()
                            }else if (message.optString("status").equals("Failed")){
                                cmn.showToast("" + message.optString("message"))
                            }else{
                                cmn.showToast("" + message.optString("message"))
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
            if (user.role.equals(cmn.roleZonal)){
                val request = HttpRequest("http://164.100.137.245/PPPAPI/api/Account/GetIncomeVerification_Results", parameters, handler, ctx)
                request.postAPIURLEncodedTemp("http://164.100.137.245/PPPAPI/api/Account/GetIncomeVerification_Results")
            }else{
                val request = HttpRequest("http://164.100.137.245/PPPAPI/api/Account/GetIncomeVerification_Results", parameters, handler, ctx)
                request.postAPIURLEncodedTemp("http://164.100.137.245/PPPAPI/api/Account/GetIncomeVerification_Results")
            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

*/

    override fun onResume() {
        super.onResume()
        reloadData()
    }

 private fun getMembers(isFresh : Boolean) {
        val parameters = JSONObject()
        try {

            parameters.put("Mobileno", cmn.getMobileNo())
            parameters.put("DeptCode", "TEST")
            parameters.put("ServiceCode", "NIC")
            parameters.put("DeptKey", "984oyu3yt2")

           /* parameters.put("Mobileno", user.mobile)
            parameters.put("LoginTxnID", user.loginTxnID)
            if (user.role.equals(cmn.roleZonal)){
                parameters.put("PollBoothCode", cmn.roleZonal)
                parameters.put("familyType", "")
            }else{
                parameters.put("PollBoothCode", selectedLcCode)
                parameters.put("familyType", selectedFamilyType)
            }
            if (isFresh){
                parameters.put("FamilyStatus", "Fresh")
            }else{
                parameters.put("FamilyStatus", "Existing")
            }*/
            val handler: Handler = object : Handler() {
                override fun handleMessage(msg: Message) {
                    val aResponse = msg.data.getString("message")
                    if (null != aResponse) {
                        dataListFresh.clear()
                        try {
                            val objectRes = JSONObject(aResponse)
                            if(objectRes.optString("status").equals("True")){
                               // var message = objectRes.optJSONObject("message")
                                if (isFresh){
                                    dataListFresh.clear()
                                }else{
                                    dataListExisting.clear()
                                }
                                var familyList = objectRes.optJSONArray("result")
                                for (i in 0..familyList.length()-1) {
                                    val data = familyList.optJSONObject(i)
                                    if (isFresh){
                                        val dataObj = Parser().parseFamily(data)
                                        dataListFresh.add(dataObj)
                                    }else{
                                        dataListExisting.add(Parser().parseFamily(data))
                                    }
                                }
                                if (isInitialScreen){
                                    setUserData()
                                    isInitialScreen = false
                                }
                                updateData()

                                /*if (message.optString("status").equals("Successfull")){
                                    var result = message.optJSONObject("result")
                                    var familyList = result.optJSONArray("familyList")
                                    for (i in 0..familyList.length()-1) {
                                        val data = familyList.optJSONObject(i)
                                        if (isFresh){
                                            dataListFresh.add(Parser().parseFamily(data))
                                        }else{
                                            dataListExisting.add(Parser().parseFamily(data))
                                        }
                                    }
                                    updateData()
                                }else if (message.optString("status").equals("Failed")){
                                    if (isFresh){
                                        cmn.showToast("" + message.optString("message"))
                                    }
                                    updateData()
                                }else{
                                    if (isFresh){
                                        cmn.showToast("" + message.optString("message"))
                                    }
                                    updateData()
                                }*/
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

            val request = HttpRequest("GetDataForVerification", parameters, handler, ctx)
            request.postAPIURLEncoded()

           /* if (user.role.equals(cmn.roleZonal)){
                val request = HttpRequest("api/GetDataForVerification", parameters, handler, ctx)
                request.postAPIURLEncoded()
            }else{
                val request = HttpRequest("api/getincomeverificationresult", parameters, handler, ctx)
                request.postAPIURLEncoded()
            }
*/
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }


    private fun getMemberTracableAllow(data : FamilyModel) {
        val parameters = JSONObject()
        try {
            parameters.put("familyType", selectedFamilyType)
            parameters.put("lcCode", selectedLcCode)
            parameters.put("mobileno", data.mobileno)
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
                                      //  submitNonTracableDetails(data)
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
            val request = HttpRequest("api/getFlagForNotTraceableAllow", parameters, handler, ctx)
            request.postAPI()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

   /* private fun submitNonTracableDetails(model: FamilyModel) {
        var dialog = ProgressDialog(ctx)
        dialog.setTitle(getString(R.string.app_name))
        dialog.setMessage("Please wait")
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

        var attributes = HashMap<String, Any>()
        //  attributes.put("faridabad_property_location_poi", model.point);
        attributes.put("familyid", model.newFamilyID)
        attributes.put("familytype", selectedFamilyType)
        attributes.put("leadtype", user.designation)
        attributes.put("latitude", mLatitude)
        attributes.put("lccode", selectedLcCode)
        attributes.put("longitude", mLongitude)
        //attributes.put("status", "")
        attributes.put("status", "N")


        val newPoint = Point(mLongitude, mLatitude, SpatialReferences.getWgs84())
        featureNonTracable!!.attributes.putAll(attributes)
       // featureNonTracable!!.geometry = newPoint

        cmn.printLog("latitude = " + mLatitude)
        cmn.printLog("longitude = " + mLongitude)

        cmn.printLog("" + attributes.toString())

        val addFeatureFuture: ListenableFuture<Void> = mServiceFeatureNonTracable.addFeatureAsync(
            featureNonTracable
        )
        addFeatureFuture.addDoneListener(Runnable {
            try {
                addFeatureFuture.get()
                if (mServiceFeatureNonTracable is ServiceFeatureTable) {
                    val applyEditsFuture: ListenableFuture<List<FeatureEditResult>> =
                            mServiceFeatureNonTracable.applyEditsAsync()
                    applyEditsFuture.addDoneListener(Runnable {
                        try {
                            val featureEditResults: List<FeatureEditResult> = applyEditsFuture.get()
                            dialog.dismiss()
                            updateNotTracableAllow(model)
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
    }*/

    private fun updateNotTracableAllow(data : FamilyModel) {
        val parameters = JSONObject()
        try {
            parameters.put("familyId", data.beneficiaryId)
            parameters.put("familyType", selectedFamilyType)
            parameters.put("lcCode", selectedLcCode)
            parameters.put("leadType", user.designation)
            parameters.put("latitude", mLatitude)
            parameters.put("longitude", mLongitude)

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

            val request = HttpRequest("api/UpdateNotTraceable", parameters, handler, ctx)
            request.postAPI()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun reloadData() {
        getMembers(true)
       // getMembers(false)
    }

}