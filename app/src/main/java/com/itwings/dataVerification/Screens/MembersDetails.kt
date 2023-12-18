package com.itwings.dataVerification.Screens

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.itwings.dataVerification.*
import com.itwings.dataVerification.Adepters.FamilyMembersAdepter
import com.itwings.dataVerification.databinding.ActivityMembersDetailsBinding
import com.itwings.wastemanagement.Utills.Comman

class MembersDetails : AppCompatActivity() {


    lateinit var binding: ActivityMembersDetailsBinding
    lateinit var ctx: Activity
    lateinit var cmn: Comman
    var dataList: java.util.ArrayList<FamilyMemberModel> = java.util.ArrayList<FamilyMemberModel>()

    companion object{
        var familyInfo : FamilyModel = FamilyModel()
    }

    lateinit var adepter : FamilyMembersAdepter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getSupportActionBar() != null) {
            getSupportActionBar()?.hide();
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_members_details)
        ctx = this
        cmn = Comman(ctx)
        binding.loginAt.setText(""+cmn.getCurrentDateWithTime())
        dataList = familyInfo.memberDetails!!
        setFamilyInfo()

        binding.btBack.setOnClickListener {
            finish()
        }

        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(ctx)
        binding.reclycalView.layoutManager = mLayoutManager
        binding.reclycalView.itemAnimator = DefaultItemAnimator()

        adepter = FamilyMembersAdepter(
                this,
                dataList)

        binding.reclycalView.adapter = adepter
    }


    fun setFamilyInfo(){
        binding.tvfamilyMembers.setText(""+ (familyInfo.memberDetails?.size ?: 0))
        binding.tvFamilyId.setText(""+ familyInfo.beneficiaryId)
        binding.tvHOFName.setText(""+ familyInfo.name)
    }
}