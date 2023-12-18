package com.itwings.dataVerification.Screens

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
/*import com.esri.arcgisruntime.concurrent.ListenableFuture
import com.esri.arcgisruntime.data.ArcGISFeature
import com.esri.arcgisruntime.data.FeatureEditResult
import com.esri.arcgisruntime.data.ServiceFeatureTable
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.geometry.SpatialReferences*/
import com.itwings.dataVerification.FamilyModel
import com.itwings.dataVerification.HttpRequest
import com.itwings.dataVerification.R
import com.itwings.dataVerification.User
import com.itwings.dataVerification.databinding.ActivityUpdateMemberCountBinding
import com.itwings.dataVerification.databinding.RowMembersBinding
import com.itwings.wastemanagement.Utills.Comman
import org.json.JSONException
import org.json.JSONObject


class UpdateMemberCount : AppCompatActivity() {

   /* lateinit var mServiceFeature: ServiceFeatureTable
    private var feature: ArcGISFeature? = null*/
    lateinit var binding: ActivityUpdateMemberCountBinding
    lateinit var ctx: Activity
    lateinit var cmn: Comman
    var user : User = User()
    var isAdditionalMember : Boolean = false
    var memList: ArrayList<String>? = ArrayList()
    companion object{
        var familyInfo : FamilyModel = FamilyModel()
        var familyType : String = ""
        var familyLcCode : String = ""
        var mLatitude: Double = 0.0
        var mLongitude: Double = 0.0
        var isFresh : Boolean = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getSupportActionBar() != null) {
            getSupportActionBar()?.hide();
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_member_count)
        ctx = this
        cmn = Comman(ctx)
        user = cmn.getUser()
        binding.loginAt.setText(""+cmn.getCurrentDateWithTime())
        memList!!.add("1")
        memList!!.add("2")
        memList!!.add("3")
        memList!!.add("4")
        memList!!.add("5")
        memList!!.add("6")
        memList!!.add("7")
        memList!!.add("8")
        memList!!.add("9")
        memList!!.add("10")
        memList!!.add("More Then 10")
        addMembers()

        binding.btBack.setOnClickListener {
            finish()
        }


        if (!isFresh){
            binding.spinnerActualMembers.isEnabled = false
        }


        binding.tfMemberCount.doOnTextChanged { text, start, before, count ->

            if (text.isNullOrEmpty()){
                binding.layAddDelMember.visibility = View.GONE
            }else{
                var count : Int = binding.tfMemberCount.text.toString().toInt()

                if (count > familyInfo.memberDetails!!.size){
                    isAdditionalMember = true
                    val incValue = count - familyInfo.memberDetails!!.size
                    binding.layAddDelEditText.setHint("Additional Members(अतिरिक्त सदस्य)")
                    binding.tfAddOrDelMembers.setText("" + incValue)

                }else{
                    isAdditionalMember = false
                    val incValue = familyInfo.memberDetails!!.size - count
                    binding.layAddDelEditText.setHint("Deleted Members(हटाए गए सदस्य)")
                    binding.tfAddOrDelMembers.setText("" + incValue)
                }
                binding.layAddDelMember.visibility = View.VISIBLE
            }
        }

        binding.btSubmit.setOnClickListener {
            if (isFresh){
                if (binding.tfMemberCount.text.toString().isNullOrEmpty()){
                    cmn.showToast("Please Enter Actual Member Count")
                }else{
                  //  submitDetailsGIS()
                }
            }else{
                cmn.showToast("Information Already been submitted !!")
            }

        }

    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        //setUpFeatureLayerNonTracable()
        initMembersSpinner()
    }

    private fun initMembersSpinner() {
        binding.spinnerActualMembers.setItem(memList!!)
        binding.spinnerActualMembers.setOnItemSelectedListener(object :
                AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
            ) {
                if (position == 10){
                    binding.outlinedTextField.visibility = View.VISIBLE
                    binding.tfMemberCount.visibility = View.VISIBLE
                    binding.tfMemberCount.setText("")
                }else{
                    binding.tfMemberCount.setText(""+memList!!.get(position))
                    binding.tfMemberCount.visibility = View.GONE
                    binding.outlinedTextField.visibility = View.GONE
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        })
    }
 /*   fun setUpFeatureLayerNonTracable(){
        mServiceFeature =
                ServiceFeatureTable(resources.getString(R.string.gis_update_member_count))
        mServiceFeature.loadAsync()
        mServiceFeature.addDoneLoadingListener(
                Runnable {
                    feature =
                            mServiceFeature.createFeature() as ArcGISFeature?
                    cmn.printLog("service Feature loaded")
                })
    }*/


   /* private fun submitDetailsGIS() {
        var dialog = ProgressDialog(ctx)
        dialog.setTitle(getString(R.string.app_name))
        dialog.setMessage("Please wait")
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

        var attributes = HashMap<String, Any>()
        //  attributes.put("faridabad_property_location_poi", model.point);
       // attributes.put("id", user.mobile.toString())
        attributes.put("userid", familyInfo.mobileno.toString())
        attributes.put("logintxnid", user.loginTxnID)
        attributes.put("pollcode", familyLcCode)
        //attributes.put("familystatus", familyType)
        attributes.put("actualmember", binding.tfMemberCount.text.toString().toInt())
        if (isAdditionalMember){
            attributes.put("additionalmember", binding.tfAddOrDelMembers.text.toString().toInt())
            attributes.put("deletedmember", 0)
        }else{
            attributes.put("additionalmember", 0)
            attributes.put("deletedmember", binding.tfAddOrDelMembers.text.toString().toInt())
        }
        attributes.put("latitude", mLatitude)
        attributes.put("longitude", mLongitude)
       // attributes.put("latitude", familyInfo.newFamilyID)
        //attributes.put("status", "")
         attributes.put("status", "N")


       // val newPoint = Point(mLongitude, mLatitude, SpatialReferences.getWgs84())
       // feature!!.attributes.putAll(attributes)
       // feature!!.geometry = newPoint

        cmn.printLog("latitude = " + mLatitude)
        cmn.printLog("longitude = " + mLongitude)

        cmn.printLog("" + attributes.toString())

        val addFeatureFuture: ListenableFuture<Void> = mServiceFeature.addFeatureAsync(
                feature
        )
        addFeatureFuture.addDoneListener(Runnable {
            try {
                addFeatureFuture.get()
                if (mServiceFeature is ServiceFeatureTable) {
                    val applyEditsFuture: ListenableFuture<List<FeatureEditResult>> =
                            mServiceFeature.applyEditsAsync()
                    applyEditsFuture.addDoneListener(Runnable {
                        try {
                            val featureEditResults: List<FeatureEditResult> = applyEditsFuture.get()
                            dialog.dismiss()
                            updateMemberCount()
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

    fun addMembers(){
        for (i in 0..(familyInfo.memberDetails?.size ?: 0) -1){
            val rowBinding: RowMembersBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.row_members,
                null,
                false
            )
            rowBinding.tvFamilyId.setText("" + familyInfo.memberDetails!!.get(i).newFamilyID)
            rowBinding.tvMemberId.setText("" + familyInfo.memberDetails!!.get(i).newMemberID)
            rowBinding.tvAge.setText("" + familyInfo.memberDetails!!.get(i).age)
            rowBinding.tvGender.setText("" + familyInfo.memberDetails!!.get(i).gender)
            rowBinding.tvName.setText("" + familyInfo.memberDetails!!.get(i).name)
            rowBinding.tvRelationWithHead.setText("" + familyInfo.memberDetails!!.get(i).relationWithHead)
            binding.holderMembers.addView(rowBinding.root)
        }
    }


    private fun updateMemberCount() {
        val parameters = JSONObject()
        try {
           // parameters.put("familyId", familyInfo.newFamilyID)
            parameters.put("LoginTxnID", user.loginTxnID)
            parameters.put("pollcode", familyLcCode)
            parameters.put("UserId", familyInfo.mobileno)
            parameters.put("actualMember", binding.tfMemberCount.text.toString())
            if (isAdditionalMember){
                parameters.put("additionalMember", binding.tfAddOrDelMembers.text.toString())
                parameters.put("deletedMember", "0")
            }else{
                parameters.put("additionalMember", "")
                parameters.put("deletedMember", binding.tfAddOrDelMembers.text.toString())
            }
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
                                        FamilyIncomeVerification.dataUpdater!!.reloadData()
                                        finish()
                                    }else{
                                        cmn.showToast("" + message.optString("message"))
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

            val request = HttpRequest("api/UpdateMembersVerification", parameters, handler, ctx)
            request.postAPI()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

}