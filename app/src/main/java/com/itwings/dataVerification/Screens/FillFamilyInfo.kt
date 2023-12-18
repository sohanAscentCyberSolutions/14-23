package com.itwings.dataVerification.Screens

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.*
import android.speech.RecognizerIntent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
/*import com.esri.arcgisruntime.data.ArcGISFeature
import com.esri.arcgisruntime.data.ServiceFeatureTable*/
import com.itwings.dataVerification.*
import com.itwings.dataVerification.Models.AddNewMemberModel
import com.itwings.dataVerification.databinding.ActivityFillFamilyInfoBinding
import com.itwings.dataVerification.databinding.RowAddMemberBinding
import com.itwings.wastemanagement.Utills.Comman
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class FillFamilyInfo : AppCompatActivity() {

    lateinit var binding: ActivityFillFamilyInfoBinding
    lateinit var ctx: Activity
    lateinit var cmn: Comman
    var user : User = User()
    var memStatusList: ArrayList<String>? = ArrayList()
    var annualIncomeList: ArrayList<String>? = ArrayList()

    var memStatusCodeList: ArrayList<String>? = ArrayList()
    var annualIncomeCodeList: ArrayList<String>? = ArrayList()
    var occpationCodeList: ArrayList<String>? = ArrayList()
    var occpationList: ArrayList<String>? = ArrayList()
    var relationshipCodeList: ArrayList<String>? = ArrayList()
    var relationshipList: ArrayList<String>? = ArrayList()
    lateinit var gps : GPSTracker
    var tracebleStatusList: ArrayList<String>? = ArrayList()
    var tracebleStatusCodeList: ArrayList<String>? = ArrayList()

    var genderList: ArrayList<String>? = ArrayList()

    var addNewMemberList: ArrayList<AddNewMemberModel>? = ArrayList()

    var selectedTracableCode = ""

    companion object{
        var familyInfo : FamilyModel = FamilyModel()
        var familyType : String = ""
        var familyLcCode : String = ""
        var isFresh : Boolean = true
    }

    private var mLatitude: Double = 0.0
    private var mLongitude: Double = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getSupportActionBar() != null) {
            getSupportActionBar()?.hide();
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_fill_family_info)
        ctx = this
        cmn = Comman(ctx)
        binding.loginAt.setText(""+cmn.getCurrentDateWithTime())

        genderList!!.add("Please Select Gender")
        genderList!!.add("MALE")
        genderList!!.add("FEMALE")
        genderList!!.add("OTHERS")
        occpationList!!.add("Please Select Occupation")
        occpationCodeList!!.add("Please Select Occupation")

        relationshipList!!.add("Please Select Relation with HOF")
        relationshipCodeList!!.add("Please Select Occupation")

        memStatusList = ArrayList()
        memStatusList!!.add("Please select Member status")
        memStatusCodeList = ArrayList()
        memStatusCodeList!!.add("")
        annualIncomeList = ArrayList()
        annualIncomeList!!.add("Please select Annual Income of Member")
        annualIncomeCodeList = ArrayList()
        annualIncomeCodeList!!.add("")
       // user = cmn.getUser()
        binding.btBack.setOnClickListener {
            finish()
        }
        gps =  GPSTracker(this);
        setFamilyInfo()

       /* if (cmn.getFullDesignation().toLowerCase().equals(cmn.roleX.toLowerCase())){
            binding.tfMemberCount.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s.isNullOrEmpty()){
                        addMembersNewMemberForms(0)
                    }else{
                        if (binding.tfMemberCount.text.toString().toInt() > familyInfo.memberDetails!!.size  ){
                            addMembersNewMemberForms(binding.tfMemberCount.text.toString().toInt() - familyInfo.memberDetails!!.size )
                        }else{
                            addMembersNewMemberForms(0)
                        }
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }
            })
        }
*/


       /* if(!isFresh){
         binding.spinnerActualMembers.isEnabled = false
        }*/

        getFamilyMemberTracableStatus()

        binding.btVoiceSearch.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US")
            try {
                startActivityForResult(intent, 1)
            } catch (a: ActivityNotFoundException) {
                Toast.makeText(
                    ctx,
                    "Oops! Your device doesn't support Speech to Text",
                    Toast.LENGTH_SHORT
                ).show()
            }


        }

        binding.btSubmit.setOnClickListener {
           if (isFresh){
               if (selectedTracableCode.isEmpty()){
                   cmn.showToast("Please Select Traceable Code")
               }/*else   if (binding.tfRemark.text.toString().isEmpty()){
                    cmn.showToast("Please Enter Remark")
                }*/else{
                   /* var dialog = ProgressDialog(ctx)
                    dialog.setTitle(getString(R.string.app_name))
                    dialog.setMessage("Please wait")
                    dialog.setCanceledOnTouchOutside(false)
                    dialog.show()*/
                    updateInformation()
                }
            }else{
                cmn.showToast("Information Already been submitted !!")
            }


        }
        getLocation()
        binding.btAddMembers.visibility = View.GONE
        binding.btAddMembers.setOnClickListener {
            if (checkMembersValidation()){
             //   updateInformationXUser()
            }
        }
    }

    fun checkMembersValidation(): Boolean {
        for (i in 0..addNewMemberList?.size!! -1){
            if (addNewMemberList!!.get(i).rowBinding!!.tfName.text.toString().isEmpty()){
                cmn.showToast("Please Select Member "+(i+1)+" 's Name")
                return false
            }else if (addNewMemberList!!.get(i).rowBinding!!.tfAge.text.toString().isEmpty()){
                cmn.showToast("Please Select Member "+(i+1)+" 's AGE")
                return false
            }else if (addNewMemberList!!.get(i).selectedGender.isEmpty()){
                cmn.showToast("Please Select Member "+(i+1)+" 's Gender")
                return false
            }else if (addNewMemberList!!.get(i).selectedOccupation.isEmpty()){
                cmn.showToast("Please Select Member "+(i+1)+" 's Occupation")
                return false
            }else if (addNewMemberList!!.get(i).selectedRelation.isEmpty()){
                cmn.showToast("Please Select Member "+(i+1)+" 's Relation")
                return false
            }
        }
        return true
    }

    fun addMembersNewMemberForms(count : Int){
        addNewMemberList?.clear()

        for (i in 0..count -1){

            val model = AddNewMemberModel()

            val rowBinding: RowAddMemberBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(this),
                    R.layout.row_add_member,
                    null,
                    false
            )
            rowBinding.tvTitle.setText("ADD MEMBER "+(i+1))
            rowBinding.spinnerGender.setItem(genderList!!)
            rowBinding.spinnerGender.setOnItemSelectedListener(object :
                    AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                        adapterView: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                ) {
                    if (position == 0){
                        model.selectedGender =  ""
                    }else{
                        model.selectedGender =  genderList!!.get(position)
                    }

                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            })
            rowBinding.spinnerOccupation.setItem(occpationList!!)
            rowBinding.spinnerOccupation.setOnItemSelectedListener(object :
                    AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                        adapterView: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                ) {
                    if (position == 0){
                        model.selectedOccupation =  ""
                        model.selectedOccupationCode =  ""
                    }else{
                        model.selectedOccupation =  occpationList!!.get(position)
                        model.selectedOccupationCode =  occpationCodeList!!.get(position)
                    }

                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            })

            rowBinding.spinnerRelationwithHOF.setItem(relationshipList!!)
            rowBinding.spinnerRelationwithHOF.setOnItemSelectedListener(object :
                    AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                        adapterView: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                ) {
                    if (position == 0){
                        model.selectedRelation =  ""
                        model.selectedRelationCode =  ""
                    }else{
                        model.selectedRelation =  relationshipList!!.get(position)
                        model.selectedOccupationCode =  relationshipCodeList!!.get(position)
                    }

                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            })
            model.rowBinding = rowBinding
            addNewMemberList?.add(model)
        }

        refreshNewMembersUI()
    }

    private fun refreshNewMembersUI(){
        if (addNewMemberList?.size == 0){
            binding.holderNewMembers.visibility = View.GONE
            binding.btAddMembers.visibility = View.GONE
        }else{
            binding.holderNewMembers.visibility = View.VISIBLE
            binding.btAddMembers.visibility = View.VISIBLE
        }
        binding.layoutAddNewMembers.removeAllViews()

        for (i in 0..addNewMemberList?.size!! -1){
            binding.layoutAddNewMembers.addView(addNewMemberList?.get(i)!!.rowBinding!!.root)
        }

    }

    private fun initTracebleStatusSpinner() {
        binding.spinnerTracableStatus.setItem(tracebleStatusList!!)
        binding.spinnerTracableStatus.setOnItemSelectedListener(object :
                AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
            ) {
                selectedTracableCode = tracebleStatusCodeList!!.get(position)
                /*if (position == 0){
                    binding.tfMemberCount.setText("")
                    binding.outlinedTextField.visibility = View.GONE
                    binding.tfMemberCount.visibility = View.GONE
                    binding.tfMemberCount.setText("")
                }else if (position == 11){
                    binding.outlinedTextField.visibility = View.VISIBLE
                    binding.tfMemberCount.visibility = View.VISIBLE
                    binding.tfMemberCount.setText("")
                    binding.tfMemberCount.isEnabled = true
                }else{
                    binding.tfMemberCount.setText(""+tracebleStatusList!!.get(position))
                    binding.tfMemberCount.visibility = View.VISIBLE
                    binding.tfMemberCount.isEnabled = false
                    binding.outlinedTextField.visibility = View.VISIBLE
                }*/
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        })
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1){
            if (resultCode === RESULT_OK && null != data) {
                val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)!![0]
                binding.tfRemark.setText(""+result)
            }
        }

    }

    fun setFamilyInfo(){
       // binding.tvfamilyMembers.setText(""+ (familyInfo.memberDetails?.size ?: 0))
        binding.tvFamilyId.setText(""+ familyInfo.beneficiaryId)
        binding.memberName.setText(""+ familyInfo.name)
        binding.tvPersonName.setText(""+ familyInfo.name)
        binding.tvScheme.setText(""+ familyInfo.scheme)
        binding.tvAmount.setText(""+ familyInfo.benefit_Amount)
        binding.tvDate.setText(""+ familyInfo.date_Benefit)

      /*  if (cmn.getFullDesignation().toLowerCase().equals(cmn.roleX.toLowerCase())){
            getOccupationForXUser()
        }*/
    }

  /*  fun addMembers(){
        for (i in 0..(familyInfo.memberDetails?.size ?: 0) -1){
            val rowBinding: RowFamilyMembersBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.row_family_members,
                null,
                false
            )
            familyInfo.memberDetails!!.get(i).index = i

            if (isFresh){
                familyInfo.memberDetails!!.get(i).mServiceFeature =
                        ServiceFeatureTable(resources.getString(R.string.gis_update_member_details_fresh))
            }else{
                rowBinding.spinnerMemberStatus.isEnabled = false
                rowBinding.spinnerAnnualIncome.isEnabled = false
                familyInfo.memberDetails!!.get(i).mServiceFeature =
                        ServiceFeatureTable(resources.getString(R.string.gis_update_member_details_Existing))
            }
            familyInfo.memberDetails!!.get(i).mServiceFeature.loadAsync()
            familyInfo.memberDetails!!.get(i).mServiceFeature.addDoneLoadingListener(
                    Runnable {
                        familyInfo.memberDetails!!.get(i).feature =
                                familyInfo.memberDetails!!.get(i). mServiceFeature.createFeature() as ArcGISFeature?
                        cmn.printLog("service Feature loaded")
                    })


            rowBinding.tvMemberName.setText(""+familyInfo.memberDetails!!.get(i).name)
            rowBinding.tvAge.setText(""+familyInfo.memberDetails!!.get(i).age)
            rowBinding.tvGender.setText(""+familyInfo.memberDetails!!.get(i).gender)
            rowBinding.tvMemberId.setText(""+familyInfo.memberDetails!!.get(i).newMemberID)
            rowBinding.tvMobile.setText(""+familyInfo.memberDetails!!.get(i).mobileno)
            rowBinding.tvRelationWithHead.setText(""+familyInfo.memberDetails!!.get(i).relationWithHead)
            rowBinding.spinnerMemberStatus.setItem(memStatusList!!)
            rowBinding.spinnerMemberStatus.setOnItemSelectedListener(object :
                AdapterView.OnItemSelectedListener {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position == 0){
                        familyInfo.memberDetails!!.get(i).selectedMemberStatus =  ""
                        familyInfo.memberDetails!!.get(i).selectedAnnualIncome =  ""
                        rowBinding.spinnerAnnualIncome.setSelection(0)
                        rowBinding.spinnerAnnualIncome.isEnabled = true
                    }else{
                        familyInfo.memberDetails!!.get(i).selectedMemberStatus =  memStatusCodeList!!.get(position)
                        familyInfo.memberDetails!!.get(i).selectedAnnualIncome =  annualIncomeCodeList!!.get(1)
                        rowBinding.spinnerAnnualIncome.setSelection(1)
                        rowBinding.spinnerAnnualIncome.isEnabled = false
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            })
            rowBinding.spinnerAnnualIncome.setItem(annualIncomeList!!)
            rowBinding.spinnerAnnualIncome.setOnItemSelectedListener(object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position == 0){
                        familyInfo.memberDetails!!.get(i).selectedAnnualIncome =  ""
                    }else{
                        familyInfo.memberDetails!!.get(i).selectedAnnualIncome =  annualIncomeCodeList!!.get(position)
                    }

                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
            })
            familyInfo.memberDetails!!.get(i).rowBinding = rowBinding
            binding.layFamilyMembers.addView(rowBinding.root)
        }
    }*/

    private fun getOccupationForXUser() {
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
                            if (objectRes.optString("status").equals("Successfull")){
                                var result = objectRes.optJSONArray("result")
                                for (i in 0..result.length()-1) {
                                    val data = result.optJSONObject(i)
                                    // cmn.printLog("data new ---"+data.optString("pollBoothCode"))
                                    occpationCodeList!!.add("" + data.optString("occupationId"))
                                    occpationList!!.add("" + data.optString("occupationName"))
                                }
                            }else if (objectRes.optString("status").equals("Failed")){
                                var result = objectRes.optJSONObject("result")
                                cmn.showToast("" + result.optString("message"))
                            }else{
                                cmn.showToast("" + objectRes.optString("message"))
                            }
                            GetRelationWithHofForXUse()
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

            val request = HttpRequest("http://164.100.137.245/PPPAPI/api/Account/GetOccupationForXUser", parameters, handler, ctx)
            request.postAPIURLEncodedTemp("http://164.100.137.245/PPPAPI/api/Account/GetOccupationForXUser")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun GetRelationWithHofForXUse() {
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
                            if (objectRes.optString("status").equals("Successfull")){
                                var result = objectRes.optJSONArray("result")
                                for (i in 0..result.length()-1) {
                                    val data = result.optJSONObject(i)
                                    // cmn.printLog("data new ---"+data.optString("pollBoothCode"))
                                    relationshipCodeList!!.add("" + data.optString("code"))
                                    relationshipList!!.add("" + data.optString("relationship"))
                                }
                            }else if (objectRes.optString("status").equals("Failed")){
                                var result = objectRes.optJSONObject("result")
                                cmn.showToast("" + result.optString("message"))
                            }else{
                                cmn.showToast("" + objectRes.optString("message"))
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

            val request = HttpRequest("http://164.100.137.245/PPPAPI/api/Account/GetRelationWithHofForXUser", parameters, handler, ctx)
            request.postAPIURLEncodedTemp("http://164.100.137.245/PPPAPI/api/Account/GetRelationWithHofForXUser")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun checkMemberValidations() : Boolean {
        for (i in 0..(familyInfo.memberDetails?.size ?: 0)-1){
            if (familyInfo.memberDetails!!.get(i).selectedAnnualIncome.isEmpty()){
                cmn.showToast("Please Select Member status or Annual Income Of Member")
                return false
            }
         /* if (familyInfo.memberDetails!!.get(i).selectedMemberStatus.isEmpty()){
                cmn.showToast("Please Select Member Status")
              familyInfo.memberDetails!!.get(i).rowBinding.spinnerMemberStatus.requestFocus()
              return false
            }else if (familyInfo.memberDetails!!.get(i).selectedAnnualIncome.isEmpty()){
                cmn.showToast("Please Select Annual Income Of Member")
              familyInfo.memberDetails!!.get(i).rowBinding.spinnerAnnualIncome.requestFocus()
              return false
            }else{
                return true
            } */
        }
        return true
    }

    private fun getFamilyMemberTracableStatus() {
        val parameters = JSONObject()
        try {
            parameters.put("Mobileno", cmn.getMobileNo())
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
                                var result = objectRes.optJSONArray("result")
                                for (i in 0..result.length()-1) {
                                    val data = result.optJSONObject(i)
                                    // cmn.printLog("data new ---"+data.optString("pollBoothCode"))
                                    tracebleStatusList!!.add("" + data.optString("name"))
                                    tracebleStatusCodeList!!.add("" + data.optString("code"))
                                }
                              initTracebleStatusSpinner()
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

            val request = HttpRequest("GetContactedMaster", parameters, handler, ctx)
            request.postAPI()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
    private fun getEstimatedIncome() {
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
                                if (message.optString("status").equals("Successfull")){
                                    var result = message.optJSONArray("result")
                                    for (i in 0..result.length()-1) {
                                        val data = result.optJSONObject(i)
                                        // cmn.printLog("data new ---"+data.optString("pollBoothCode"))
                                        annualIncomeCodeList!!.add("" + data.optString("code"))
                                        annualIncomeList!!.add("" + data.optString("name"))
                                    }
                                }else if (message.optString("status").equals("Failed")){
                                    var result = message.optJSONObject("result")
                                    cmn.showToast("" + result.optString("message"))
                                }else{
                                    cmn.showToast("" + message.optString("message"))
                                }
                            }
                           //x addMembers()
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

            val request = HttpRequest("api/GetEstimatedIncomeDetail", parameters, handler, ctx)
            request.postAPI()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
    private fun getMembersInfoJson(): JSONArray {

        var familyData = JSONArray()

        for (i in 0..(familyInfo.memberDetails?.size ?: 0) -1){
            var tempObj = JSONObject()
            var tmepData : FamilyMemberModel = familyInfo.memberDetails?.get(i)!!

            tempObj.put("estimatedIncome" , tmepData.selectedAnnualIncome)
            tempObj.put("MemberStatus" , tmepData.selectedMemberStatus)
            tempObj.put("name" , tmepData.name)
            tempObj.put("newFamilyID" , tmepData.newFamilyID)
            tempObj.put("newMemberID" , tmepData.newMemberID)
            tempObj.put("latitude" , mLatitude)
            tempObj.put("longitude" , mLongitude)
            familyData.put(tempObj)
        }

       return familyData
    }

    private fun getMembersInfoXUserJson(): JSONArray {

        var familyData = JSONArray()

        for (i in 0..(addNewMemberList!!.size ?: 0) -1){
            var tempObj = JSONObject()
            var tmepData : AddNewMemberModel = addNewMemberList?.get(i)!!

            tempObj.put("Name" , tmepData.rowBinding!!.tfName.text.toString())
            tempObj.put("Age" , tmepData.rowBinding!!.tfAge.text.toString())
            if (tmepData.selectedGender.toLowerCase().equals("male")){
                tempObj.put("Gender" , "M")
            }else if (tmepData.selectedGender.toLowerCase().equals("female")){
                tempObj.put("Gender" , "F")
            }else{
                tempObj.put("Gender" , "O")
            }
            tempObj.put("occupation" , tmepData.selectedOccupation)
            tempObj.put("relationWithHOF" , tmepData.selectedRelation)
            familyData.put(tempObj)
        }

        return familyData
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

    private fun updateInformation() {
        val parameters = JSONObject()
        try {
            parameters.put("remarks", binding.tfRemark.text.toString())
            parameters.put("contacted", selectedTracableCode)
            parameters.put("beneficiaryId", familyInfo.beneficiaryId)
            parameters.put("NewMemberId", familyInfo.newMemberId)
            parameters.put("DeptCode", "TEST")
            parameters.put("scheme", familyInfo.scheme)
            parameters.put("ServiceCode", "NIC")
            parameters.put("DeptKey", "984oyu3yt2")
            parameters.put("createdBy", cmn.getMobileNo())
            parameters.put("latitude", mLatitude)
            parameters.put("longitude", mLongitude)


            val handler: Handler = object : Handler() {
                override fun handleMessage(msg: Message) {
                    val aResponse = msg.data.getString("message")
                    if (null != aResponse) {
                        try {
                            val objectRes = JSONObject(aResponse)
                            if(objectRes.optString("status").equals("True")){
                                saveInformation()
                               // saveInformation1()
                               /* var message = objectRes.optString("message")
                                cmn.showToast(""+message)
                                FamilyIncomeVerification.dataUpdater!!.reloadData()
                                finish()*/
                            }else{
                                var message = objectRes.optJSONObject("message")
                                cmn.showToast(""+message)
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
            val request = HttpRequest("SaveRecord", parameters, handler, ctx)
            request.postAPI()
           /* if (isFresh){
                val request = HttpRequest("api/UpdateFamilyDetails", parameters, handler, ctx)
                request.postAPIURLEncoded()
            }else{
                parameters.put("PollCode", familyLcCode)
                val request = HttpRequest("api/UpdateFamilyDetailsforExisting", parameters, handler, ctx)
                request.postAPIURLEncoded()
            }*/
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun saveInformation() {
        val parameters = JSONObject()
        try {
            parameters.put("token", "gmda@gis!")
            parameters.put("DeptCode", "TEST")
            parameters.put("ServiceCode", "NIC")
            parameters.put("DeptKey", "984oyu3yt2")
            parameters.put("CreatedBy", ""+cmn.getMobileNo())
            parameters.put("contacted", selectedTracableCode)
            parameters.put("remarks", binding.tfRemark.text.toString())
            parameters.put("latitude", mLatitude)
            parameters.put("longitude", mLongitude)
            parameters.put("beneficiaryId", familyInfo.beneficiaryId)
            parameters.put("scheme", familyInfo.scheme)
            parameters.put("Date_benefit", familyInfo.date_Benefit)
            parameters.put("Benefit_amount",  familyInfo.benefit_Amount)


            // parameters.put("newFamilyID", familyInfo.beneficiaryId)
          // parameters.put("NewMemberId", familyInfo.newMemberId)









            val handler: Handler = object : Handler() {
                override fun handleMessage(msg: Message) {
                    val aResponse = msg.data.getString("message")
                    if (null != aResponse) {
                        try {
                            val objectRes = JSONObject(aResponse)
                            if(objectRes.optString("status").equals("true")){
                                var message = objectRes.optString("message")
                                saveInformation1()
                            /* cmn.showToast(""+message)
                                FamilyIncomeVerification.dataUpdater!!.reloadData()
                                finish()*/
                            }else{
                                var message = objectRes.optJSONObject("message")
                                cmn.showToast(""+message)
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
            val request = HttpRequest("verificationRecord", parameters, handler, ctx , true)
            request.postAPIOwnServer()
            /* if (isFresh){
                 val request = HttpRequest("api/UpdateFamilyDetails", parameters, handler, ctx)
                 request.postAPIURLEncoded()
             }else{
                 parameters.put("PollCode", familyLcCode)
                 val request = HttpRequest("api/UpdateFamilyDetailsforExisting", parameters, handler, ctx)
                 request.postAPIURLEncoded()
             }*/
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
    private fun saveInformation1() {
        val parameters = JSONObject()
        try {
            parameters.put("token", "gmda@gis!")
            parameters.put("newMemberId", familyInfo.newMemberId)
            parameters.put("group",  familyInfo.group)
            parameters.put("memberName", familyInfo.name)
            parameters.put("mobileNo", familyInfo.mobileno)
            parameters.put("district", familyInfo.district)
            parameters.put("blockTown",  familyInfo.blockTown)
            parameters.put("wardVillage",  familyInfo.blockTown)
            parameters.put("address",  familyInfo.address)
            parameters.put("gender",  "")
            parameters.put("fatherName",  familyInfo.fatherNAME)
            parameters.put("age",  familyInfo.age)
            parameters.put("dob",  "")
            parameters.put("pollBoothCode", "")
            parameters.put("createDate",  ""+cmn.getCurrentDate())
            parameters.put("beneficiaryId", familyInfo.beneficiaryId)
            parameters.put("HoF_Name", familyInfo.hoF_Name)
            parameters.put("Scheme", familyInfo.scheme)
            parameters.put("Date_benefit", familyInfo.date_Benefit)
            parameters.put("Benefit_amount", familyInfo.benefit_Amount)
           // parameters.put("createdBy", cmn.getMobileNo())



          //
           // parameters.put("latitude", mLatitude)
           // parameters.put("longitude", mLongitude)






           /* parameters.put("DeptKey", "984oyu3yt2")
            parameters.put("createdBy", cmn.getMobileNo())
            parameters.put("remarks", binding.tfRemark.text.toString())
            parameters.put("contacted", selectedTracableCode)
            */

            // parameters.put("NewMemberId", familyInfo.newMemberId)

           /*
            parameters.put("DeptCode", "TEST")

            parameters.put("latitude", mLatitude)
            parameters.put("longitude", mLongitude)
            parameters.put("ServiceCode", "NIC")

            parameters.put("Benefit_amount",  familyInfo.benefit_Amount)
            parameters.put("DeptKey", "984oyu3yt2")
            parameters.put("createdBy", cmn.getMobileNo())*/


            val handler: Handler = object : Handler() {
                override fun handleMessage(msg: Message) {
                    val aResponse = msg.data.getString("message")
                    if (null != aResponse) {
                        try {
                            val objectRes = JSONObject(aResponse)
                            if(objectRes.optString("status").equals("true")){
                                var message = objectRes.optString("message")
                                cmn.showToast(""+message)
                                FamilyIncomeVerification.dataUpdater!!.reloadData()
                                finish()
                            }else{
                                var message = objectRes.optJSONObject("message")
                                cmn.showToast(""+message)
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
            val request = HttpRequest("familySurveyRecord", parameters, handler, ctx , true)
            request.postAPIOwnServer()
            /* if (isFresh){
                 val request = HttpRequest("api/UpdateFamilyDetails", parameters, handler, ctx)
                 request.postAPIURLEncoded()
             }else{
                 parameters.put("PollCode", familyLcCode)
                 val request = HttpRequest("api/UpdateFamilyDetailsforExisting", parameters, handler, ctx)
                 request.postAPIURLEncoded()
             }*/
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

   /* private fun updateInformationXUser() {
        val parameters = JSONObject()
        try {
            parameters.put("NewFamilyID", familyInfo.newFamilyID)
            parameters.put("actualMember", binding.tfMemberCount.text.toString())
            parameters.put("additionalMember", addNewMemberList!!.size)
            parameters.put("CreatedBy", user.mobile)
            parameters.put("latitude", mLatitude)
            parameters.put("longitude", mLongitude)

            parameters.put("MemberDetails", getMembersInfoXUserJson())

            val handler: Handler = object : Handler() {
                override fun handleMessage(msg: Message) {
                    val aResponse = msg.data.getString("message")
                    if (null != aResponse) {
                        try {
                            val objectRes = JSONObject(aResponse)
                            if(objectRes.optString("status").equals("working")){
                                var message = objectRes.optJSONObject("message")
                                if (message.optString("status").equals("Successfull")){
                                    cmn.showToast("Saved Successfully !!")
                                    FamilyIncomeVerification.dataUpdater!!.reloadData()
                                    finish()
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

            val request = HttpRequest("api/SaveAdditionalMembersForXUser", parameters, handler, ctx)
            request.postAPIURLEncoded()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }*/

   /* fun submitInfoArcGIS(dialog: ProgressDialog){
        var isArcException = false
        AsyncTask.execute {
            for (model in familyInfo.memberDetails!!){

                if (isArcException){
                    break
                }

                var attributes = HashMap<String, Any>()
                attributes.put("actualmember", binding.tfMemberCount.text.toString())
                attributes.put("createdby", user.mobile.toString())
                attributes.put("designation", user.designation)
                attributes.put("familytype", familyType)
                attributes.put("estimatedincome", model.selectedAnnualIncome)
                attributes.put("name", model.name.toString())
                attributes.put("newfamily_id", model.newFamilyID.toString())
                attributes.put("newmemberid", model.newMemberID.toString())
                attributes.put("latitude", mLatitude)
                attributes.put("longitude", mLongitude)

              //  val newPoint = Point(mLongitude, mLatitude, SpatialReferences.getWgs84())
                model.feature!!.attributes.putAll(attributes)
              //  model.feature!!.geometry = newPoint

                cmn.printLog("" + attributes.toString())

                val addFeatureFuture: ListenableFuture<Void> = model.mServiceFeature.addFeatureAsync(
                        model.feature
                )
                addFeatureFuture.addDoneListener(Runnable {
                    try {
                        addFeatureFuture.get()
                        if (model.mServiceFeature is ServiceFeatureTable) {
                            val applyEditsFuture: ListenableFuture<List<FeatureEditResult>> =
                                    model.mServiceFeature.applyEditsAsync()
                            applyEditsFuture.addDoneListener(Runnable {
                                try {
                                    val featureEditResults: List<FeatureEditResult> =
                                            applyEditsFuture.get()
                                } catch (e: java.lang.Exception) {
                                    dialog.cancel()
                                    isArcException = true
                                    e.printStackTrace()
                                }
                            })
                        }
                    } catch (e: InterruptedException) {
                        // executionException may contain an ArcGISRuntimeException with edit error information.
                        dialog.cancel()
                        isArcException = true
                        if (e.cause is ArcGISRuntimeException) {
                            cmn.printLog("Arc Error : " + e.localizedMessage)
                        } else {
                            cmn.printLog("Exc Error : " + e.localizedMessage)
                        }
                    } catch (e: ExecutionException) {
                        dialog.cancel()
                        isArcException = true
                        if (e.cause is ArcGISRuntimeException) {
                            cmn.printLog("Arc execution : " + e.localizedMessage)
                        } else {
                            cmn.printLog("Exc Error : " + e.localizedMessage)
                        }
                    }
                })
            }
        }
        if (!isArcException){
            dialog.cancel()
            updateInformation()
        }else{
            cmn.showToast("ArcGIS Exception Please Try After Some Time ...")
        }
    }*/
}