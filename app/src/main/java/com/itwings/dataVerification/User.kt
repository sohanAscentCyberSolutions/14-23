package com.itwings.dataVerification

class User {

    var loginTxnID: String = ""
    var district: java.util.ArrayList<String> = java.util.ArrayList<String>()
    var bTown: java.util.ArrayList<String> = java.util.ArrayList<String>()
    var wardVillage: java.util.ArrayList<String> = java.util.ArrayList<String>()
    var username: String = ""
    var designation: String = ""
    var mobile: String = ""
    var districtLgdCode: String = ""
    var blockTownLgdCode: String = ""
    var wardVillageLgdCode: String = ""
    var role : String = ""
    var zoneCode : String = ""
    var group : String = ""
    var lgdList : ArrayList<HashMap<String, String>>? = ArrayList()
    var contactedYes: String = ""
    var contactedNotTraceable: String = ""
    var totalPushed: String = ""
    var totalPending: String = ""
    constructor()


}