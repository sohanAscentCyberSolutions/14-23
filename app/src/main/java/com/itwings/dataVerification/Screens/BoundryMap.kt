package com.itwings.dataVerification.Screens

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
/*import com.esri.arcgisruntime.ArcGISRuntimeEnvironment
import com.esri.arcgisruntime.concurrent.ListenableFuture
import com.esri.arcgisruntime.data.Feature
import com.esri.arcgisruntime.data.FeatureQueryResult
import com.esri.arcgisruntime.data.QueryParameters
import com.esri.arcgisruntime.data.ServiceFeatureTable
import com.esri.arcgisruntime.geometry.*
import com.esri.arcgisruntime.layers.ArcGISMapImageLayer
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.Viewpoint
import com.esri.arcgisruntime.mapping.view.Callout
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol
import com.esri.arcgisruntime.symbology.SimpleFillSymbol
import com.esri.arcgisruntime.symbology.SimpleLineSymbol*/
import com.itwings.dataVerification.*
import com.itwings.dataVerification.databinding.ActivityBoundryMapBinding
import com.itwings.wastemanagement.Utills.Comman


class BoundryMap : AppCompatActivity() {

    private val bufferDistance = 155
  /*  private var mCallout: Callout? = null
    var map: ArcGISMap? = null
    val aimageLayer : ArcGISMapImageLayer? = null*/
    private lateinit var ctx: Activity
    private lateinit var binding: ActivityBoundryMapBinding
    private lateinit var comman: Comman
    var user : User = User()
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    lateinit var gps: GPSTracker
    var isIncomeVerification  = false
    val namesList : ArrayList<String> = ArrayList()
    val codeList : ArrayList<String> = ArrayList()
    companion object{
        var familyInfo : FamilyModel = FamilyModel()
    }

    var selectedVillageLGD : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (supportActionBar != null) {
            supportActionBar?.hide()
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_boundry_map)
        comman = Comman(this)
        ctx = this
        user = comman.getUser()
        selectedVillageLGD = comman.getSelectedLGD()
       // selectedVillageLGD = "60771"
        gps = GPSTracker(this);
        binding.loginAt.setText(""+comman.getCurrentDateWithTime())
        isIncomeVerification = intent.getBooleanExtra("isIncomeVerification", false)

        binding.back.setOnClickListener {
            super.onBackPressed()
        }
      //  setUserData()
        binding.recenter.setOnClickListener {
        //    getLocation(true)
        }

        binding.btContinue.setOnClickListener {
            if (HttpRequest.isStagingAPpp){
                if (isIncomeVerification){
          //          methodWithPermissions()
                }else{
            //        methodWithPermissionsDOB()
                }
            }else{
                if (comman.getSelectedLGDVillage().toLowerCase().contains("ward")){
                    if (user.role.equals(comman.roleZonal)){
              //          startFamilyInfoScreen()
                    }else{
                        if (isIncomeVerification){
                       //     methodWithPermissions()
                        }else{
                         //   methodWithPermissionsDOB()
                        }
                    }
                }else{
                    if (binding.layGisInfo.visibility == View.VISIBLE){
                        if (user.role.equals(comman.roleZonal)){
                           // startFamilyInfoScreen()
                        }else{
                            if (isIncomeVerification){
                              //  methodWithPermissions()
                            }else{
                             //   methodWithPermissionsDOB()
                            }
                        }
                    }else{
                        comman.showAlert("You are not in the selected village/ward.")
                    }
                }
            }


        }
       // getLocation(false)
    }
/*
    fun methodWithPermissions() = runWithPermissions(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    ) {
        startActivity(Intent(applicationContext, FamilyIncomeVerification::class.java))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    fun methodWithPermissionsDOB() = runWithPermissions(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    ) {
        startActivity(Intent(applicationContext, MemberDOBVerification::class.java))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }


    fun startFamilyInfoScreen(){
        FillFamilyInfo.isFresh = true
        FillFamilyInfo.familyInfo = familyInfo
        FillFamilyInfo.familyLcCode = ""
        FillFamilyInfo.familyType = ""
        startActivity(Intent(applicationContext, FillFamilyInfo::class.java))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    fun getLocation(isRecenter: Boolean) {
       *//* latitude = 28.4913911
        longitude = 75.7961779
        if (isRecenter){
            binding.mapView.setViewpoint(Viewpoint(latitude, longitude, 10000.0))
        }else{
            setUpMapView()
        }
        Handler().postDelayed(Runnable {
            Log.e("latitude = ", "" + latitude)
            Log.e("longitude = ", "" + longitude)
            getAddress()
            val callpoint = Point(longitude, latitude, SpatialReferences.getWgs84())
            CreateBuffer(callpoint)
            addDefaultMarker()
        }, 100)*//*

      if (HttpRequest.isStagingAPpp){
          //  latitude = 28.3812297
          //  longitude = 76.9322052
            latitude = 29.5253391
            longitude = 74.8026407
            if (isRecenter){
                binding.mapView.setViewpoint(Viewpoint(latitude, longitude, 10000.0))
            }else{
                setUpMapView()
            }
            Handler().postDelayed(Runnable {
                Log.e("latitude = ", "" + latitude)
                Log.e("longitude = ", "" + longitude)
                getAddress()
                val callpoint = Point(longitude, latitude, SpatialReferences.getWgs84())
                CreateBuffer(callpoint)
                addDefaultMarker()
            }, 100)
        }else{
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
                            comman.printLog("is mock location used : $isMockLocation")
                            latitude = gps.latitude
                            longitude = gps.longitude
                            if (isRecenter){
                                binding.mapView.setViewpoint(Viewpoint(latitude, longitude, 10000.0))
                            }else{
                                setUpMapView()
                            }
                            Handler().postDelayed(Runnable {
                                latitude = gps.latitude
                                longitude = gps.longitude
                                Log.e("latitude = ", "" + latitude)
                                Log.e("longitude = ", "" + longitude)
                                getAddress()
                                val callpoint = Point(longitude, latitude, SpatialReferences.getWgs84())
                                CreateBuffer(callpoint)
                                addDefaultMarker()
                            }, 100)
                        }
                    }catch (e : java.lang.Exception){
                        latitude = gps.latitude
                        longitude = gps.longitude
                        if (isRecenter){
                            binding.mapView.setViewpoint(Viewpoint(latitude, longitude, 10000.0))
                        }else{
                            setUpMapView()
                        }
                        Handler().postDelayed(Runnable {
                            latitude = gps.latitude
                            longitude = gps.longitude
                            Log.e("latitude = ", "" + latitude)
                            Log.e("longitude = ", "" + longitude)
                            getAddress()
                            val callpoint = Point(longitude, latitude, SpatialReferences.getWgs84())
                            CreateBuffer(callpoint)
                            addDefaultMarker()
                        }, 100)
                    }
                }
            } else gps.showSettingsAlert(this)
        }
    }

    fun setUserData() {
        binding.welcomeName.setText("Welcome," + user.username)
        binding.tvMobile.setText("" + user.mobile)
        binding.tvDistrict.setText("" + user.district)
        binding.town.setText("" + user.bTown)
        binding.tvdesignation.setText("Designation : " + comman.getFullDesignation())

        if (user.role.equals(comman.roleZonal)){
            binding.layWardVillage.visibility = View.GONE
        }else{
            binding.village.setText("" + comman.getSelectedLGDVillage())
        }
    }

    var address = ""
    private fun getAddress() {
        val user = comman.getUser()
        val latLng = LatLng(latitude, longitude)
        val geocoder = Geocoder(ctx)
        Log.e("draglat = ", "" + latLng.latitude)
        Log.e("draglong = ", "" + latLng.longitude)
        latitude = latLng.latitude
        longitude = latLng.longitude
        try {
            val addressList: List<Address>? = geocoder.getFromLocation(
                    latLng.latitude,
                    latLng.longitude,
                    1
            )
            if (addressList != null && addressList.size > 0) {
                val locality: String = addressList[0].getAddressLine(0)
                //  val country: String = addressList[0].getCountryName()
                val postalCode: String = addressList[0].postalCode
                address = locality + postalCode
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        binding.tvCurrentLocation.setText("Lat : " + latitude + " , Long : " + longitude + " , " + "LGD Code : " + selectedVillageLGD + " , " + address)
    }

    override fun onPause() {
        binding.mapView.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.dispose()
    }

    fun setUpMapView() {
        comman.showProgressDialog("Loading ....")
        ArcGISRuntimeEnvironment.setApiKey("AAPK27a0c70ca3f940bf98bbb66cced68eeevV6VZAUgB-fpys3fBMfK3t_vecFADVPLN1oQr5PdyBTEXaR0D_-vS1latUR_kQlE")
        map = ArcGISMap(Basemap.Type.TOPOGRAPHIC, latitude, longitude, 10)
        addGraphics()
        binding.mapView.setMap(map)
        binding.mapView.setViewpoint(Viewpoint(latitude, longitude, 10000.0))
    }

    fun addGraphics() {
        mCallout = binding.mapView.getCallout()
        val aimageLayer = ArcGISMapImageLayer("https://hsacggm.in/server/rest/services/Onemap_Haryana/Village_LGD/MapServer")
        // arcGISMap.getOperationalLayers().add(arcGISVectorTiledLayer)
        map!!.operationalLayers.add(aimageLayer)
        // aimageLayer.loadAsync()
        bufferGraphicsOverlay = GraphicsOverlay()
        markerGraphicsOverlay = GraphicsOverlay()
        binding.mapView.getGraphicsOverlays().add(bufferGraphicsOverlay)
        binding.mapView.getGraphicsOverlays().add(markerGraphicsOverlay)
        aimageLayer!!.addDoneLoadingListener {
            comman.hideProgressDialog()
            if (HttpRequest.isStagingAPpp){

            }else if (!user.wardVillage.toLowerCase().contains("ward")){
                if (user.role.equals(comman.roleZonal)){
                    if (familyInfo.memberDetails?.get(0)?.wardLGDcode.isNullOrEmpty() || familyInfo.memberDetails?.get(0)?.wardLGDcode.equals("null")){
                        comman.showAlert("LGD Code Found Null Or Blank Kindly Contact to the technical team.")
                    }else {
                        checkLocationHttp(false)
                    }
                }else{
                    checkLocationHttp(false)
                }
            }
        }
    }
    var result: FeatureQueryResult? = null

    private fun checkLocationHttp(isfromStaticVillage : Boolean) {
        val parameters = JSONObject()
        val user = comman.getUser()
        try {
            parameters.put("geometry", "" + latitude + "," + longitude)
          //  parameters.put("geometry", "" + 29.418826 + "," + 74.652375)
            parameters.put("geometryType", "esriGeometryPoint")
            parameters.put("inSR", "4326")
            parameters.put("spatialRel", "esriSpatialRelWithin")
            parameters.put("inSR", "4326")
            parameters.put("outFields", "*")
            parameters.put("returnGeometry", false)
            parameters.put("f", "json")
            val handler: Handler = object : Handler() {
                override fun handleMessage(msg: Message) {
                    val aResponse = msg.data.getString("message")
                    if (null != aResponse) {
                        try {
                            val tempObj = JSONObject(aResponse)
                            val features = tempObj.optJSONArray("features")

                            var isLGDFound = false

                            if (features.length() > 0){
                                for (i in 0..features.length()-1) {
                                    var obj = features.optJSONObject(i)
                                    var attributes = obj.optJSONObject("attributes")
                                    comman.printLog(
                                            "user lgd : " + selectedVillageLGD + " , layer lgd = " + attributes.optString(
                                                    "lgd_hartro"
                                            )
                                    )
                                   if (isfromStaticVillage){
                                       if (user.role.equals(comman.roleZonal)){
                                           if (attributes.optString(
                                                           "lgd_hartro"
                                                   ).contains(selectedVillageLGD

                                                   )){
                                               binding.tvCurrentLocationGIS.setText(
                                                       "LGD Code : " + selectedVillageLGD + " , District : " + attributes.optString("district") + " , Ward/village Name : " + attributes.optString(
                                                               "newwardvillagename"
                                                       )
                                               )
                                               binding.layGisInfo.visibility = View.VISIBLE
                                               isLGDFound = true
                                           }
                                       }else {
                                           if (attributes.optString(
                                                           "lgd_hartro"
                                                   ).contains(
                                                           selectedVillageLGD.replace(" ", "")
                                                   )){
                                               binding.tvCurrentLocationGIS.setText(
                                                       "LGD Code : " + selectedVillageLGD + " , District : " + attributes.optString("district") + " , Ward/village Name : " + attributes.optString(
                                                               "newwardvillagename"
                                                       )
                                               )
                                               binding.layGisInfo.visibility = View.VISIBLE
                                               isLGDFound = true
                                           }
                                       }
                                   }else{
                                       if (user.role.equals(comman.roleZonal)){
                                           if (attributes.optString(
                                                           "lgd_hartro"
                                                   ).contains(familyInfo.memberDetails?.get(0)?.wardLGDcode?.replace(" ", "").toString()

                                                   )){
                                               binding.tvCurrentLocationGIS.setText(
                                                       "LGD Code : " + familyInfo.memberDetails?.get(0)?.wardLGDcode?.replace(" ", "").toString() + " , District : " + attributes.optString("district") + " , Ward/village Name : " + attributes.optString(
                                                               "newwardvillagename"
                                                       )
                                               )
                                               binding.layGisInfo.visibility = View.VISIBLE
                                               isLGDFound = true
                                           }
                                       }else {
                                           if (attributes.optString(
                                                           "lgd_hartro"
                                                   ).replace(" ", "").contains(
                                                           selectedVillageLGD
                                                   )){
                                               binding.tvCurrentLocationGIS.setText(
                                                       "LGD Code : " + selectedVillageLGD + " , District : " + attributes.optString("district") + " , Ward/village Name : " + attributes.optString(
                                                               "newwardvillagename"
                                                       )
                                               )
                                               binding.layGisInfo.visibility = View.VISIBLE
                                               isLGDFound = true
                                           }
                                       }
                                   }
                                }
                            }


                            if (!isLGDFound){
checkNotListedLocations()
                            }

                        } catch (e: JSONException) {
                            e.printStackTrace()
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
                    "query?geometry=$longitude,$latitude&geometryType=esriGeometryPoint&inSR=4326&spatialRel=esriSpatialRelWithin&outFields=*&returnGeometry=false&f=json",
                    handler,
                    ctx
            )
            request.GisAPI()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun checkNotListedLocations() {
        val featureLayer = ServiceFeatureTable("https://hsacggm.in/server/rest/services/Onemap_Haryana/Village_LGD/MapServer/1")
        featureLayer.loadAsync()
        featureLayer.addDoneLoadingListener(
                Runnable {
                    comman.printLog("service Feature loaded")
                    val queryParams = QueryParameters()
                    queryParams.whereClause = "1=1"
                    var featureResult: ListenableFuture<FeatureQueryResult> =
                            featureLayer.queryFeaturesAsync(
                                    queryParams,
                                    ServiceFeatureTable.QueryFeatureFields.LOAD_ALL
                            )
                    featureResult.addDoneListener {
                        try {
                            var result = featureResult.get()
                            val resultIterator: Iterator<Feature> = result.iterator()
                            while (resultIterator.hasNext()) {
                                // get the extent of the first feature in the result to zoom to
                                val feature = resultIterator.next()
                                val attributes = feature.attributes
                               // comman.printLog("Attriuutes ===" + attributes)

                                if (user.role.equals(comman.roleZonal)){
                                    if (attributes["WardLGDcode"].toString().contains(familyInfo.memberDetails?.get(0)?.wardLGDcode?.replace(" ", "")!!
                                            )){
                                        binding.tvCurrentLocationGIS.setText(
                                                "LGD Code : " + familyInfo.memberDetails?.get(0)?.wardLGDcode + " , District : " + attributes["District"] + " , Ward/village Name : " + attributes[
                                                        "newwardvillagename"]
                                                )
                                        binding.layGisInfo.visibility = View.VISIBLE
                                    }
                                }else {
                                    if (attributes["WardLGDcode"].toString().contains(selectedVillageLGD.replace(" ", "")

                                            )){
                                        binding.tvCurrentLocationGIS.setText(
                                                "LGD Code : " + selectedVillageLGD + " , District : " + attributes["District"] + " , Ward/village Name : " + attributes[
                                                        "newwardvillagename"]
                                        )
                                        binding.layGisInfo.visibility = View.VISIBLE
                                    }
                                }


                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                })
    }

    var geom: Geometry? = null
    private var bufferGraphicsOverlay: GraphicsOverlay? = null
    private var markerGraphicsOverlay: GraphicsOverlay? = null


    private fun CreateBuffer(currentLocationPoint: Point) {
        bufferGraphicsOverlay!!.graphics.clear()
        val linearUnit = LinearUnit(LinearUnitId.METERS)
        val bufferGeom = GeometryEngine.bufferGeodetic(
                currentLocationPoint, bufferDistance.toDouble(), linearUnit,
                0.0001, GeodeticCurveType.GEODESIC
        )
        val simpleFillSymbol = SimpleFillSymbol(
                SimpleFillSymbol.Style.SOLID, Color.argb(
                50,
                239,
                188,
                69
        ), null
        )
        simpleFillSymbol.outline = SimpleLineSymbol(
                SimpleLineSymbol.Style.SOLID, Color.argb(
                255,
                247,
                160,
                46
        ), 1F
        )
        val polyGraphics = Graphic(bufferGeom, simpleFillSymbol)
        if (!bufferGraphicsOverlay!!.graphics.isEmpty()) {
            bufferGraphicsOverlay!!.graphics.clear()
            //mMapView.getGraphicsOverlays().remove(bufferGraphicsOverlay);
        }
        bufferGraphicsOverlay!!.graphics.add(polyGraphics)
        geom = GeometryEngine.project(bufferGeom, SpatialReference.create(4326))

        if (user.wardVillage.toLowerCase().contains("ward")){
            checkVillageCounts(bufferGeom);
        }else{
            checkVillageCounts(bufferGeom);
        }
    }

    fun checkVillageCounts(bufferGeom: Polygon) {
        val featureLayer = ServiceFeatureTable("https://hsacggm.in/server/rest/services/Onemap_Haryana/Village_LGD/FeatureServer/0")
        featureLayer.loadAsync()
        featureLayer.addDoneLoadingListener(
                Runnable {
                    comman.printLog("service Feature loaded")
                    val queryParams = QueryParameters()
                    queryParams.geometry = bufferGeom
                    queryParams.whereClause = "1=1"
                    var featureResult: ListenableFuture<FeatureQueryResult> =
                            featureLayer.queryFeaturesAsync(
                                    queryParams,
                                    ServiceFeatureTable.QueryFeatureFields.LOAD_ALL
                            )
                    featureResult.addDoneListener {
                        try {
                            var result = featureResult.get()
                            val resultIterator: Iterator<Feature> = result.iterator()
                            namesList.clear()
                            codeList.clear()
                            selectedVillageLGD = ""
                            while (resultIterator.hasNext()) {
                                // get the extent of the first feature in the result to zoom to
                                val feature = resultIterator.next()
                                val attr = feature.attributes

                                val json = JSONObject(attr)

                             //   comman.printLog("Attriuutes ===" + json)
                                namesList.add(json.optString("n_v_name"))
                                codeList.add(json.optString("wardlgdcode"))
                            }
                            comman.printLog("code Synch Successfully !!")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        if (namesList.size > 1){
                            val builder = MaterialAlertDialogBuilder(ctx, R.style.MaterialThemeDialog)
                            var currentselection : Int = -1
                            // dialog title
                            builder.setTitle("Select Village")
                            builder.setSingleChoiceItems(
                                    namesList.toTypedArray(), // array
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
                                            checkLocationHttp(false)
                                            dialog.dismiss()
                                        }
                                    }
                        }

                    }
                })
    }

    fun addDefaultMarker() {
        var wgsSR: SpatialReference = SpatialReference.create(4326)
        val pierPoint = Point(longitude, latitude, wgsSR)

        var startDrawable: BitmapDrawable =
                getDrawable(R.drawable.marker) as BitmapDrawable
        try {
            var pinSourceSymbol: PictureMarkerSymbol =
                    PictureMarkerSymbol.createAsync(
                            startDrawable
                    ).get()
            pinSourceSymbol.height = 50F
            pinSourceSymbol.width = 30F
            pinSourceSymbol.loadAsync();
            pinSourceSymbol.addDoneLoadingListener {
                markerGraphicsOverlay!!.graphics.clear()
                var pinSourceGraphic: Graphic = Graphic(pierPoint, pinSourceSymbol)
                markerGraphicsOverlay!!.graphics.add(pinSourceGraphic)
                comman.printLog("pin source done")
                comman.printLog("attributes " + markerGraphicsOverlay!!.graphics[0].symbol.internal)
            }

            comman.printLog("now show the dialog")
        } catch (e: Exception) {
            comman.printLog("" + e.printStackTrace())
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
    }*/
}

