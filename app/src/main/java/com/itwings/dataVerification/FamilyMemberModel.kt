package com.itwings.dataVerification

import com.itwings.dataVerification.databinding.RowFamilyMembersBinding

class FamilyMemberModel {

    var familyStatus: String? = null
    var newFamilyID: String? = null
    var newMemberID: String? = null
    var name: String? = null
    var fatherNAME: String? = null
    var age = 0
    var gender: String? = null
    var relationWithHead: String? = null
    var mobileno: String? = null
    var address: String? = null
    var isHouseHoldMember1: Any? = null
    var relationshipwithHH_code = 0
    var estimatedIncome: String? = null
    var memberStatus: String? = null
    var memberCount = 0
    var familyType: String? = null
    var lgdcode : String? = null
    var btlgdcode : String? = null
    var wardLGDcode : String = ""
    var phase : String = ""

    lateinit var rowBinding : RowFamilyMembersBinding
    var index : Int = 0
    var selectedMemberStatus = ""
    var selectedAnnualIncome = ""

  //  lateinit var mServiceFeature: ServiceFeatureTable
  //  var feature: ArcGISFeature? = null

    constructor()


}