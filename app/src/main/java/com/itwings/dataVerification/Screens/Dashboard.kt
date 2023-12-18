package com.itwings.dataVerification.Screens

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.itwings.dataVerification.HttpRequest
import com.itwings.dataVerification.R
import com.itwings.dataVerification.User
import com.itwings.dataVerification.databinding.ActivityDashboardBinding
import com.itwings.wastemanagement.Utills.Comman
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import org.json.JSONException
import org.json.JSONObject
import java.util.ArrayList

class Dashboard : AppCompatActivity() {

    lateinit var binding: ActivityDashboardBinding
    lateinit var ctx: Activity
    lateinit var cmn: Comman
    var user : User = User()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        if (getSupportActionBar() != null) {
            getSupportActionBar()?.hide();
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)
        ctx = this
        cmn = Comman(ctx)

        user = cmn.getUser()
        setUserData()
        binding.loginAt.setText(""+cmn.getCurrentDateWithTime())
       /* if (cmn.getFullDesignation().toLowerCase().equals(cmn.roleTeamLead.toLowerCase())){
            binding.cardMemberDOBVerification.visibility = View.VISIBLE
            binding.cardFamilyIncomeVerification.visibility = View.VISIBLE
        }else{
            binding.cardMemberDOBVerification.visibility = View.GONE
        }
*/
        binding.cardMemberDOBVerification.visibility = View.GONE
        binding.cardFamilyIncomeVerification.setOnClickListener{
            startActivity(Intent(applicationContext, FamilyIncomeVerification::class.java))
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
           // methodWithPermissionsBoundryMap(true)
        }

        binding.cardMemberDOBVerification.setOnClickListener{
           // startActivity(Intent(applicationContext, FillDOBInfo::class.java))
           // overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            methodWithPermissionsBoundryMap(false)
        }

        binding.logOut.setOnClickListener{
            showLogOutDialog()
           // methodWithPermissionsBoundryMap()

        }

    }

    fun methodWithPermissionsBoundryMap(isIncomeVerification : Boolean) = runWithPermissions(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    ) {

        if (user.lgdList!!.size == 1){
            if (user.role.equals(cmn.roleZonal)){
                startActivity(Intent(applicationContext, FamilyIncomeVerification::class.java))
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }else{
                cmn.setSelectedLGD(user.lgdList!!.get(0).get("code"))
                cmn.setSelectedLGDVillage(user.lgdList!!.get(0).get("village"))
                startActivity(Intent(applicationContext, BoundryMap::class.java).putExtra("isIncomeVerification" , isIncomeVerification))
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
        }else{
            if (user.lgdList!!.size > 0){
                var temlgdNamesList = ArrayList<String>()
                var temlgdcodeList = ArrayList<String>()
                for (i in 0..user.lgdList!!.size -1) {
                    val data = user.lgdList!!.get(i)
                    // cmn.printLog("data new ---"+data.optString("pollBoothCode"))
                    temlgdcodeList.add(data.get("code").toString())
                    temlgdNamesList.add(data.get("village").toString())
                }

                val builder = MaterialAlertDialogBuilder(ctx, R.style.MaterialThemeDialog)
                var currentselection : Int = -1
                // dialog title
                builder.setTitle("Select Village")
                builder.setSingleChoiceItems(
                        temlgdNamesList.toTypedArray(), // array
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
                                if (user.role.equals(cmn.roleZonal)){
                                    startActivity(Intent(applicationContext, FamilyIncomeVerification::class.java))
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                                }else{
                                    cmn.setSelectedLGD(temlgdcodeList.get(currentselection))
                                    cmn.setSelectedLGDVillage(temlgdNamesList.get(currentselection))
                                    startActivity(Intent(applicationContext, BoundryMap::class.java).putExtra("isIncomeVerification" , isIncomeVerification))
                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                                }
                                dialog.dismiss()
                            }
                        }
            }
        }

      /*  */

    }

    override fun onResume() {
        super.onResume()
        getDashboard()
    }

    fun showLogOutDialog() {
        val d = Dialog(ctx)
        d.window!!.attributes.windowAnimations = R.style.DialogAnimationFade
        d.setContentView(R.layout.successwithok)
        val Labeltitle = d.findViewById<TextView>(R.id.Labeltitle)
        Labeltitle.text = "Do you want to logged out ??"
        d.findViewById<View>(R.id.btokk).setOnClickListener {
            Comman(ctx).setIsLogged(false)
            d.dismiss()
            ctx.startActivity(Intent(ctx, LogIn::class.java))
        }
        d.show()
        d.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }


    fun setUserData(){
        user = cmn.getUser()
        binding.welcomeName.setText("Welcome," + user.username)
        binding.tvMobile.setText("" + cmn.getMobileNo())
        var dist : String = user.district.toString().replace("[" , "")
        dist = dist.replace("]" , "")
        dist = dist.replace("," ,"-")
        binding.tvDistrict.setText("" + dist)
        var bTown : String = user.bTown.toString().replace("[" , "")
        bTown = bTown.replace("]" , "")
        bTown = bTown.replace("," ,"-")
        binding.town.setText("" + bTown)
        var wardVillage : String = user.wardVillage.toString().replace("[" , "")
        wardVillage = wardVillage.replace("]" , "")
        wardVillage = wardVillage.replace("," ,"-")
        binding.town.setText("" + bTown)
        binding.village.setText("" + wardVillage)
        binding.tvTotalPending.setText("" + user.totalPending)
        binding.tvTotalContacted.setText("" + user.contactedYes)
        binding.tvNotTraceable.setText("" + user.contactedNotTraceable)
        binding.tvTotalPushed.setText("" + user.totalPushed)
        /*if (user.role.equals(cmn.roleZonal)){
            binding.layWardVillage.visibility = View.GONE
        }else{
            if (user.lgdList!!.size==1){
                binding.village.setText("" + user.lgdList!!.get(0).get("village"))
            }else{
                binding.layWardVillage.visibility = View.GONE
            }

        }*/
        binding.tvGroup.visibility = View.GONE
      //  binding.tvGroup.setText("Groupo : "+user.group)
        /*binding.welcomeName.setText("Welcome," + "Ranbir Singh")
        binding.tvMobile.setText("7973972631" )
        binding.tvDistrict.setText("SONIPAT")
        binding.town.setText("GOHANA BL" )
        binding.village.setText("Niat")*/
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
        d.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
    private fun getDashboard() {
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
                                user.mobile = result.optString("mobile")
                                user.contactedYes = result.optString("contactedYes")
                                user.contactedNotTraceable = result.optString("contactedNotTraceable")
                                user.totalPushed = result.optString("totalPushed")
                                user.totalPending = result.optString("totalPending")
                                cmn.saveUser(user)
                                setUserData()
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

            val request = HttpRequest("GetDashboardDetails", parameters, handler, ctx)
            request.postAPI()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}