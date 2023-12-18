package com.itwings.dataVerification.Screens

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.options
/*import com.esri.arcgisruntime.concurrent.ListenableFuture
import com.esri.arcgisruntime.data.ArcGISFeature
import com.esri.arcgisruntime.data.FeatureEditResult
import com.esri.arcgisruntime.data.ServiceFeatureTable*/
import com.itwings.dataVerification.*
import com.itwings.dataVerification.Models.DocsModel
import com.itwings.dataVerification.databinding.ActivityFillDobInfoBinding
import com.itwings.wastemanagement.Utills.Comman
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class FillDOBInfo : AppCompatActivity() {

    lateinit var binding: ActivityFillDobInfoBinding
    lateinit var ctx: Activity
    lateinit var cmn: Comman
    var user : User = User()
    var memMasterDocList: ArrayList<DocsModel>? = ArrayList()
    var selectedDoc : DocsModel? = null
    val documentVoterIdTypeIdBefore = 100
    val documentVoterIdTypeIdAfter = 102
  /*  lateinit var mServiceFeature: ServiceFeatureTable
    private var feature: ArcGISFeature? = null*/
    companion object{
        var familyInfo : FamilyMemberModel = FamilyMemberModel()
        var lcCode : String = ""
        var mLatitude: Double = 0.0
        var mLongitude: Double = 0.0
    }

    var lcMasterDocListNames: ArrayList<String>? = ArrayList()

    var selectedFrontImage : Bitmap? = null
    var selectedBackImage : Bitmap? = null

    var selectedFrontUrl : Uri? = null
    var selectedBackUri : Uri? = null
    var isProofSelectionFront = true

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            // use the returned uri
            val uriContent = result.uriContent
            if (isProofSelectionFront){
                binding.ivDocFront.setImageURI(uriContent)
                binding.ivDeleteFront.visibility = View.VISIBLE
               selectedFrontImage = MediaStore.Images.Media.getBitmap(
                   this.contentResolver,
                   uriContent
               )
                selectedFrontUrl = uriContent
            }else{
                binding.ivDocBack.setImageURI(uriContent)
                binding.ivDeleteBack.visibility = View.VISIBLE
                selectedBackImage = MediaStore.Images.Media.getBitmap(
                    this.contentResolver,
                    uriContent
                )
                selectedBackUri = uriContent
            }
        } else {
            // an error occurred
            val exception = result.error
        }
    }
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
       // setUpFeatureLayer()
    }
  /*  fun setUpFeatureLayer(){
        mServiceFeature =
                ServiceFeatureTable(resources.getString(R.string.gis_update_dob_details))
        mServiceFeature.loadAsync()
        mServiceFeature.addDoneLoadingListener(
            Runnable {
                feature =
                    mServiceFeature.createFeature() as ArcGISFeature?
                cmn.printLog("service Feature loaded")
            })
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getSupportActionBar() != null) {
            getSupportActionBar()?.hide();
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_fill_dob_info)
        ctx = this
        cmn = Comman(ctx)
        memMasterDocList = ArrayList()
        binding.loginAt.setText(""+cmn.getCurrentDateWithTime())
        user = cmn.getUser()
        binding.btBack.setOnClickListener {
            finish()
        }

        setFamilyInfo()
        getMasterDocs()

        binding.tfDOB.setOnClickListener {
            cmn.showDatePickerMaxDateClearField(binding.tfDOB, binding.tfDateOfDoc, Date())
        }

        binding.tfDateOfDoc.setOnClickListener {
            if (selectedDoc == null){
                cmn.showToast("Please select DOB Proof.")
            }else{
                if (selectedDoc!!.documentTypeId.equals("" + documentVoterIdTypeIdBefore)) {
                    val sdf = SimpleDateFormat("yyyy/MM/dd")
                    val strDateSelected = sdf.parse(binding.tfDOB.text.toString())
                    val voterIdDate = sdf.parse("2011/12/30")
                    if (voterIdDate.after(strDateSelected)) {
                        cmn.printLog("date is less then 2012")
                        val cal = Calendar.getInstance()
                        cal.time = cmn.getDateFromString(binding.tfDOB.text.toString())
                        cal.add(Calendar.YEAR, +18)
                        cal.add(Calendar.DATE, +1)
                        val date = cal.time
                        cmn.showDatePickerMinDateWithOpeningDate(
                            binding.tfDateOfDoc,
                            date
                        )
                    }else{
                       cmn.showAlert("Date of birth should be less then 2012.")
                    }
                }else{
                    if(binding.tfDOB.text.toString().isNullOrEmpty()){
                        cmn.showToast("Please select date of birth.")
                    }else{
                        cmn.showDatePickerMinDateWithOpeningDate(
                            binding.tfDateOfDoc,
                            cmn.getDateFromString(binding.tfDOB.text.toString())
                        )
                    }
                }
            }

        }
        binding.btSubmit.setOnClickListener {
            if (binding.tfDOB.text.toString().isNullOrEmpty()){
                cmn.showToast("Please Select DOB !!")
            }else if (selectedDoc == null){
                cmn.showToast("Please Select DOB Proof !!")
            }else  if (selectedDoc!!.numberOfDocumentRequired == 0){
               // submitDetailsGIS()
            }else{
                if(selectedDoc!!.numberOfDocumentRequired == 0){
                    binding.layProof.visibility = View.GONE
                }else if(selectedDoc!!.numberOfDocumentRequired == 1){
                    binding.layProof.visibility = View.VISIBLE
                    binding.layProofBack.visibility = View.GONE
                }else{
                    binding.layProofBack.visibility = View.VISIBLE
                    binding.layProofBack.visibility = View.VISIBLE
                    binding.layProof.visibility = View.VISIBLE
                }

                if (selectedDoc!!.documentTypeId.equals("" + documentVoterIdTypeIdBefore) || selectedDoc!!.documentTypeId.equals("" + documentVoterIdTypeIdAfter) ){
                    if (binding.tfVoterCardNo.text.toString().isNullOrEmpty()){
                        cmn.showToast("Please enter Voter Card Number !!")
                    }else if (binding.tfDateOfDoc.text.toString().isNullOrEmpty()){
                        cmn.showToast("Please Select Date Of Document !!")
                    }else if(selectedDoc!!.numberOfDocumentRequired == 0){
                       // submitDetailsGIS()
                    }else if(selectedDoc!!.numberOfDocumentRequired == 1){
                        if (selectedFrontImage == null){
                            cmn.showToast("Please Select Front Image !!")
                        }else{
                           // submitDetailsGIS()
                        }
                    }else{
                        if (selectedFrontImage == null){
                            cmn.showToast("Please Select Front Image !!")
                        }else if (selectedBackImage == null){
                            cmn.showToast("Please Select Back Image !!")
                        }else{
                           // submitDetailsGIS()
                        }
                    }
                }else if (binding.tfDateOfDoc.text.toString().isNullOrEmpty()){
                    cmn.showToast("Please Select Date Of Document !!")
                }else if(selectedDoc!!.numberOfDocumentRequired == 0){
                   // submitDetailsGIS()
                }else if(selectedDoc!!.numberOfDocumentRequired == 1) {
                    if (selectedFrontImage == null){
                        cmn.showToast("Please Select Front Image !!")
                    }else{
                       // submitDetailsGIS()
                    }
                }else{
                    if (selectedFrontImage == null){
                        cmn.showToast("Please Select Front Image !!")
                    }else if (selectedBackImage == null){
                        cmn.showToast("Please Select Back Image !!")
                    }else{
                       // submitDetailsGIS()
                    }
                }
            }
        }

        binding.ivDocFront.setOnClickListener {
            isProofSelectionFront = true
            cropImage.launch(
                options {
                }
            )
        }

        binding.ivDocBack.setOnClickListener {
            isProofSelectionFront = false
            cropImage.launch(
                options {
                }
            )
        }

        binding.ivDeleteFront.setOnClickListener {
            binding.ivDocFront.setImageResource(R.drawable.placeholder)
            binding.ivDeleteFront.visibility = View.GONE
            selectedFrontImage = null
        }

        binding.ivDeleteBack.setOnClickListener {
            binding.ivDocBack.setImageResource(R.drawable.placeholder)
            binding.ivDeleteBack.visibility = View.GONE
            selectedBackImage = null
        }

    }

    fun setFamilyInfo(){
        binding.tvName.setText("" + familyInfo.name)
        binding.tvFamilyId.setText("" + familyInfo.newFamilyID)
        binding.tvAddress.setText("" + familyInfo.address)
        binding.tvGender.setText("" + familyInfo.gender)
        binding.tvMobile.setText("" + familyInfo.mobileno)
    }

    private fun initMasterDocSpinner() {

        /*lcCodeList!!.add("86221")*/

        for (i in 0..memMasterDocList!!.size-1) {
            lcMasterDocListNames!!.add(memMasterDocList!!.get(i).documentName)
        }

        binding.spinnerDocsList.setItem(lcMasterDocListNames!!)
        binding.spinnerDocsList.setOnItemSelectedListener(object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (memMasterDocList!!.get(position).numberOfDocumentRequired == 0) {
                    binding.layDOB.visibility = View.GONE
                } else {
                    binding.layDOB.visibility = View.VISIBLE
                }
                selectedDoc = memMasterDocList!!.get(position)

                if (selectedDoc!!.documentTypeId.equals("" + documentVoterIdTypeIdBefore) || selectedDoc!!.documentTypeId.equals("" + documentVoterIdTypeIdAfter)) {
                    binding.tfVoterCardNo.visibility = View.VISIBLE
                    binding.layVoterCardNo.visibility = View.VISIBLE
                } else {
                    binding.tfVoterCardNo.visibility = View.GONE
                    binding.layVoterCardNo.visibility = View.GONE
                }

                if (selectedDoc!!.numberOfDocumentRequired == 0) {
                    binding.layProof.visibility = View.GONE
                } else if (selectedDoc!!.numberOfDocumentRequired == 1) {
                    binding.layProof.visibility = View.VISIBLE
                    binding.layProofBack.visibility = View.GONE
                } else {
                    binding.layProofBack.visibility = View.VISIBLE
                    binding.layProofBack.visibility = View.VISIBLE
                    binding.layProof.visibility = View.VISIBLE
                }

                binding.tfDateOfDoc.setText("")
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        })
    }

    private fun getMasterDocs() {
        val parameters = JSONObject()
        try {
            parameters.put("LoginTxnID", user.loginTxnID)
            parameters.put("lcCode", lcCode)
            parameters.put("mobileNo", user.mobile)
            parameters.put("FamilyId", familyInfo.newFamilyID)
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
                                    var documentList = result.optJSONArray("documentList")
                                    for (i in 0..documentList.length()-1) {
                                        val data = documentList.optJSONObject(i)
                                        // cmn.printLog("data new ---"+data.optString("pollBoothCode"))
                                        memMasterDocList!!.add(Parser().parseMasterDoc(data))
                                    }
                                    initMasterDocSpinner()
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

            val request = HttpRequest("api/MasterDocument", parameters, handler, ctx)
            request.postAPI()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun updateInformation() {
        val parameters = JSONObject()
        try {
            parameters.put("LoginTxnID", user.loginTxnID)
            parameters.put("LCCode", lcCode)
            parameters.put("FamilyId", familyInfo.newFamilyID)
            parameters.put("MemberId", familyInfo.newMemberID)
            parameters.put("DOB", binding.tfDOB.text.toString())
            parameters.put("Age", familyInfo.age)
            parameters.put("DOBProofType", selectedDoc!!.documentTypeId)
            parameters.put("UpdatedBy", user.mobile)
            parameters.put("Latitude", mLatitude)
            parameters.put("Longitude", mLongitude)
            parameters.put("Longitude", mLongitude)
          //  parameters.put("Phase", familyInfo.phase)

            if (selectedDoc!!.numberOfDocumentRequired > 0){
                if (selectedDoc!!.numberOfDocumentRequired == 1){
                    val image1 = createPDFWithImage(selectedFrontImage!!)
                    cmn.printLog("image1 character count = " + image1.length)
                    parameters.put("VoterCardIssueDate", binding.tfDateOfDoc.text.toString())
                    parameters.put("Base64Image1", createPDFWithImage(selectedFrontImage!!))
                   // parameters.put("Base64Image2", createPDFWithImage(selectedBackImage!!))
                }else{
                    val image2 = createPDFWithImage(selectedBackImage!!)
                    val image1 = createPDFWithImage(selectedFrontImage!!)
                    cmn.printLog("image1 character count = " + image1.length)
                    cmn.printLog("image2 character count = " + image2.length)
                    parameters.put("VoterCardIssueDate", binding.tfDateOfDoc.text.toString())
                    parameters.put("Base64Image1", createPDFWithImage(selectedFrontImage!!))
                    parameters.put("Base64Image2", createPDFWithImage(selectedBackImage!!))
                }

            }
            if (selectedDoc!!.documentTypeId.equals("" + documentVoterIdTypeIdBefore) || selectedDoc!!.documentTypeId.equals("" + documentVoterIdTypeIdAfter)){
                parameters.put("VoterCardNumber", binding.tfVoterCardNo.text.toString())
            }
           // parameters.put("VoterCardNumber", familyInfo.age)
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
                                    MemberDOBVerification.dataUpdater!!.reloadData()
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
                val request = HttpRequest(
                    "api/SaveDataForDobVerification",
                    parameters,
                    handler,
                    ctx
                )
                request.postAPI()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

  /*  private fun submitDetailsGIS() {
        var dialog = ProgressDialog(ctx)
        dialog.setTitle(getString(R.string.app_name))
        dialog.setMessage("Please wait")
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

        var attributes = HashMap<String, Any>()
        attributes.put("logintxnid", user.loginTxnID.toString())
        attributes.put("lccode", lcCode)
        attributes.put("updatedby", user.mobile.toString())
        attributes.put("familyid", familyInfo.newFamilyID.toString())
        attributes.put("memberid", familyInfo.newMemberID.toString())

        if (selectedDoc!!.numberOfDocumentRequired > 0){
            if (selectedDoc!!.numberOfDocumentRequired == 1){
                val image1 = createPDFWithImage(selectedFrontImage!!)
                cmn.printLog("image1 character count = " + image1.length)
                attributes.put("votercardissuedate", binding.tfDateOfDoc.text.toString())
                attributes.put("base64image1", image1)
               // attributes.put("base64image2", "")
            }else{
                val image2 = createPDFWithImage(selectedBackImage!!)
                val image1 = createPDFWithImage(selectedFrontImage!!)
                cmn.printLog("image1 character count = " + image1.length)
                attributes.put("votercardissuedate", binding.tfDateOfDoc.text.toString())
                cmn.printLog("image2 character count = " + image2.length)
                attributes.put("base64image1", image1)
                attributes.put("base64image2", image2)
            }

        }
        if (selectedDoc!!.documentTypeId.equals("" + documentVoterIdTypeIdBefore) || selectedDoc!!.documentTypeId.equals("" + documentVoterIdTypeIdAfter)){
            attributes.put("votercardnumber", binding.tfVoterCardNo.text.toString())
        }
        attributes.put("dob", binding.tfDOB.text.toString())
        attributes.put("age", familyInfo.age.toString())
        attributes.put("dobprooftype", selectedDoc!!.documentTypeId)
        attributes.put("latitude", mLatitude)
        attributes.put("longitude", mLongitude)
        attributes.put("status", "N")


        // val newPoint = Point(UpdateMemberCount.mLongitude, UpdateMemberCount.mLatitude, SpatialReferences.getWgs84())
        feature!!.attributes.putAll(attributes)
        // featureMarkAsDead!!.geometry = newPoint

        cmn.printLog("latitude = " + UpdateMemberCount.mLatitude)
        cmn.printLog("longitude = " + UpdateMemberCount.mLongitude)

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
                            updateInformation()
                        } catch (e: java.lang.Exception) {
                            dialog.dismiss()
                            cmn.printLog("gis response " + e.printStackTrace())
                            cmn.showToast("GIS response " + e.printStackTrace())
                        }
                    })
                } else {
                    dialog.dismiss()
                    cmn.showToast("Arc GIS Error !!")
                }
            } catch (e: InterruptedException) {
                dialog.dismiss()
                cmn.printLog("IO response " + e.cause)
                cmn.showToast("IO response " + e.localizedMessage)
            } catch (e: ExecutionException) {
                dialog.dismiss()
                cmn.printLog("EXECU exe" + e.cause)
                cmn.showToast("EXECU exe" + e.localizedMessage)
            }
        })
    }*/


    private fun createPDFWithImage(path: Bitmap) : String{
        val file: File = getOutputFile()!!

        val tempBitmap = path

        val scaledBitmap = Bitmap.createScaledBitmap(
            tempBitmap, 43, 43,
            false
        )

        if (file != null) {
            try {
                val fileOutputStream = FileOutputStream(file)
                val pdfDocument = PdfDocument()
                val pageInfo = PageInfo.Builder(scaledBitmap.width, scaledBitmap.height, 1).create()
                val page = pdfDocument.startPage(pageInfo)
                val canvas: Canvas = page.canvas
                val paint = Paint()
                paint.setColor(Color.BLUE)
                canvas.drawPaint(paint)
                canvas.drawBitmap(scaledBitmap, 0f, 0f, null)
                pdfDocument.finishPage(page)
                scaledBitmap.recycle()
                pdfDocument.writeTo(fileOutputStream)
                pdfDocument.close()
              return encodeFileToBase64Binary(file)!!

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return ""
    }


    private fun getOutputFile(): File? {
        val root = File(getExternalFilesDir(null), "My PDF Folder")
        var isFolderCreated = true
        if (!root.exists()) {
            isFolderCreated = root.mkdir()
        }
        return if (isFolderCreated) {
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val imageFileName = "PDF_$timeStamp"
            File(root, "$imageFileName.pdf")
        } else {
            Toast.makeText(this, "Folder is not created", Toast.LENGTH_SHORT).show()
            null
        }
    }


    private fun encodeFileToBase64Binary(yourFile: File): String? {
        val size = yourFile.length().toInt()
        val bytes = ByteArray(size)
        try {
            val buf = BufferedInputStream(FileInputStream(yourFile))
            buf.read(bytes, 0, bytes.size)
            buf.close()
        } catch (e: FileNotFoundException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
        return cmn.parseBytestoBase64(bytes)
    }





}



