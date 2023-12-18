package com.itwings.dataVerification

import com.itwings.dataVerification.Models.DocsModel
import org.json.JSONObject

class Parser {



    fun parseFamily (data : JSONObject) : FamilyModel{

        val model = FamilyModel()

        if (data != null){
            model.beneficiaryId = data.optString("beneficiaryId")
           // model.pollBoothCode = data.optString("pollBoothCode")
            model.newMemberId = data.optString("newMemberId")
            model.name = data.optString("memberName")
            model.group = data.optString("group")
            model.fatherNAME = data.optString("fatherNAME")
            model.wardVillage = data.optString("wardVillage")
            model.age = data.optInt("age")
            model.scheme = data.optString("scheme")
            model.mobileno = data.optString("mobileNo")
            model.blockTown = data.optString("blockTown")
            model.district = data.optString("district")
            model.hoF_Name = data.optString("hoF_Name")
            model.memberCount = data.optString("memberCount")
            model.address = data.optString("address")
            model.date_Benefit = data.optString("date_Benefit")
            model.benefit_Amount = data.optString("benefit_Amount")
            model.isHouseHoldMember1 = data.optBoolean("isHouseHoldMember1")
           /* val membersList = ArrayList<FamilyMemberModel>()
            val members = data.optJSONArray("memberDetails")

            for (i in 0..members.length()-1){
                membersList.add(parseFamilyMember(members.optJSONObject(i)))
            }
            model.memberDetails = membersList */
        }

        return model
    }

    fun parseFamilyMember (data : JSONObject) : FamilyMemberModel{

        val model = FamilyMemberModel()

        if (data != null){

            model.familyStatus = data.optString("familyStatus")
            model.newFamilyID = data.optString("newFamilyID")
            model.newMemberID = data.optString("newMemberID")
            model.name = data.optString("name")
            model.fatherNAME = data.optString("fatherNAME")
            model.age = data.optInt("age")
            model.gender = data.optString("gender")
            model.relationWithHead = data.optString("relationWithHead")
            model.mobileno = data.optString("mobileno")
            model.address = data.optString("address")
            model.isHouseHoldMember1 = data.optString("isHouseHoldMember1")
            model.relationshipwithHH_code = data.optInt("relationshipwithHH_code")
            model.estimatedIncome = data.optString("estimatedIncome")
            model.memberStatus = data.optString("memberStatus")
            model.memberCount = data.optInt("memberCount")
            model.familyType = data.optString("familyType")
            model.lgdcode = data.optString("lgdcode")
            model.btlgdcode = data.optString("btlgdcode")
            model.wardLGDcode = data.optString("wardLGDcode")

        }
        return model
    }



    fun parseFamilyMemberDOB (data : JSONObject) : FamilyMemberModel{

        val model = FamilyMemberModel()

        if (data != null){

            model.familyStatus = data.optString("familyStatus")
            model.newFamilyID = data.optString("newFamilyId")
            model.newMemberID = data.optString("newMemberId")
            model.name = data.optString("name")
            model.fatherNAME = data.optString("fatherNAME")
            model.age = data.optInt("age")
            model.gender = data.optString("gender")
            model.relationWithHead = data.optString("relationWithHead")
            model.mobileno = data.optString("mobileNo")
            model.address = data.optString("address")
            model.isHouseHoldMember1 = data.optString("isHouseHoldMember1")
            model.relationshipwithHH_code = data.optInt("relationshipwithHH_code")
            model.estimatedIncome = data.optString("estimatedIncome")
            model.memberStatus = data.optString("memberStatus")
            model.memberCount = data.optInt("memberCount")
            model.familyType = data.optString("familyType")
            model.phase = data.optString("phase")

        }
        return model
    }

    fun parseMasterDoc (data : JSONObject) : DocsModel{

        val model = DocsModel()

        if (data != null) {
            model.documentTypeId = data.optString("documentTypeId")
            model.documentName = data.optString("documentName")
            model.numberOfDocumentRequired = data.optInt("numberOfDocumentRequired")
        }
        return model
    }
}