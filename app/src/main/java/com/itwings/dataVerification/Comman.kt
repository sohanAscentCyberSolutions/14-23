package com.itwings.wastemanagement.Utills

import android.app.*
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.ClipboardManager
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.itwings.dataVerification.User
import java.io.ByteArrayOutputStream
import java.net.URLEncoder
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.*


class Comman {

    val roleZonal: String = "Zonal"
    val roleTeamLead: String = "Team Lead"
    val roleX: String = "X User"
    var context : Activity
    val preferenceName : String = "PPP"
    var sharedpreferences: SharedPreferences? = null
    lateinit var progressDialog : ProgressDialog
    constructor(context: Activity){
        this.context = context
        sharedpreferences = context.getSharedPreferences(
            "hyd", 0
        )
    }

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 100

        val userTeamLead = "T"


        val logInTime : String = "logInTime"
        val lastSynchTime : String = "lastSynchTime"
        fun getcurrentdate(format: String): String {

            // String cuurent_date = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss")
            val cuurent_date = SimpleDateFormat(format)
                .format(Calendar.getInstance().time)
            //  Date date=Calendar.getInstance().getTime();
            println("cuurent_date$cuurent_date")
            return cuurent_date
        }
    }

    fun SetLogInTime(value: String?) {
        // save the data
        val editor = sharedpreferences!!.edit()
        editor.putString(logInTime, value)
        editor.commit()
    }

    fun setIsLogged(value: Boolean) {
        // save the data
        val editor = sharedpreferences!!.edit()
        editor.putBoolean("islogged", value)
        editor.commit()
    }

    fun getIsLogged(): Boolean? {
        return sharedpreferences?.getBoolean("islogged", false)
    }


    fun getFullDesignation(): String {
        var designation : String = ""
        if (getUser().designation.equals("T")){
            designation = "Team Lead"
        }else if (getUser().designation.equals("C")){
            designation = "CollegeStudent"
        }else if (getUser().designation.equals("S")){
            designation = "Social Worker"
        }else if (getUser().designation.equals("O")){
            designation = "Operator"
        }else if (getUser().designation.equals("V")){
            designation = "Volunteer"
        }else if (getUser().designation.equals("X")){
            designation = "X User"
        }

        return designation
    }

    fun SetLastDbSyncTime(value: String?) {
        // save the data
        val editor = sharedpreferences!!.edit()
        editor.putString(lastSynchTime, value)
        editor.commit()
    }

    fun getSelectedLGD(): String {
        return sharedpreferences?.getString("selectedLGD", "").toString()
    }

    fun setSelectedLGD(value: String?) {
        // save the data
        val editor = sharedpreferences!!.edit()
        editor.putString("selectedLGD", value)
        editor.commit()
    }
    fun setcontactedYes(value: String?) {
        // save the data
        val editor = sharedpreferences!!.edit()
        editor.putString("contactedYes", value)
        editor.commit()
    }
    fun setcontactedNotTraceable(value: String?) {
        // save the data
        val editor = sharedpreferences!!.edit()
        editor.putString("contactedNotTraceable", value)
        editor.commit()
    }
    fun settotalPushed(value: String?) {
        // save the data
        val editor = sharedpreferences!!.edit()
        editor.putString("totalPushed", value)
        editor.commit()
    }
    fun settotalPending(value: String?) {
        // save the data
        val editor = sharedpreferences!!.edit()
        editor.putString("totalPending", value)
        editor.commit()
    }
    fun gettotalPending(): String {
        return sharedpreferences?.getString("totalPending", "").toString()
    }
    fun gettotalPushed(): String {
        return sharedpreferences?.getString("totalPushed", "").toString()
    }
    fun getcontactedNotTraceable(): String {
        return sharedpreferences?.getString("contactedNotTraceable", "").toString()
    }
    fun getcontactedYes(): String {
        return sharedpreferences?.getString("contactedYes", "").toString()
    }
    fun getMobileNo(): String {
        return sharedpreferences?.getString("selectedMobileNo", "").toString()
    }
    fun setSetMobileNo(value: String?) {
        // save the data
        val editor = sharedpreferences!!.edit()
        editor.putString("selectedMobileNo", value)
        editor.commit()
    }

    fun setAccessUrlGMDA(value: String?) {
        // save the data
        val editor = sharedpreferences!!.edit()
        editor.putString("selectedUrlGMDA", value)
        editor.commit()
    }
    fun getAccessUrlGMDA(): String {
        var url = sharedpreferences?.getString("selectedUrlGMDA", "").toString()
        printLog("")
        return url
    }
    fun setSelectedLGDVillage(value: String?) {
        // save the data
        val editor = sharedpreferences!!.edit()
        editor.putString("selectedLGDVillage", value)
        editor.commit()
    }

    fun getSelectedLGDVillage(): String {
        return sharedpreferences?.getString("selectedLGDVillage", "").toString()
    }

    fun isInternetOn(): Boolean {
        val connectivityManager: ConnectivityManager
        var wifiInfo: NetworkInfo
        var mobileInfo: NetworkInfo
        var connected = false
        try {
            connectivityManager = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivityManager != null) {
                val info = connectivityManager.allNetworkInfo
                if (info != null) for (i in info.indices) if (info[i].state == NetworkInfo.State.CONNECTED) {
                    connected = true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return connected
    }

    /**************************************************** get shared preferences  */

    fun getLogInTime(): String {
        return sharedpreferences?.getString(logInTime, "0").toString()
    }

    fun getLastDbSyncTime(): String {
        return sharedpreferences?.getString(lastSynchTime, "0").toString()
    }

    fun saveUser(profile: User) {
        val gson = Gson()
        val editor = sharedpreferences!!.edit()
        val json: String = gson.toJson(profile)
        editor.putString("user", json)
        editor.commit()
    }

    fun getUser(): User {
        val gson = Gson()
        val json: String = sharedpreferences?.getString("user", "").toString()
        return gson.fromJson(json, User::class.java)
    }

    fun showToast(text: String){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    fun showAlert(text: String){
        val builder = AlertDialog.Builder(context)
        builder.setMessage(text)
        builder.setPositiveButton(android.R.string.ok) { dialog, which ->
        }
        builder.show()
    }

    fun showProgressDialog(text: String){
        progressDialog = ProgressDialog(context)
        progressDialog.setMessage(text)
        progressDialog.show()
    }

    fun hideProgressDialog(){
        if (progressDialog.isShowing)
        progressDialog.dismiss()
    }

    fun printLog(text: String){
       Log.e("log-->", text)
    }

    fun isEmpty(field: TextInputEditText) : Boolean{
        return field.text.toString().isEmpty()
    }

    fun showDatePicker(field: TextInputEditText){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH + 1)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            context,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                var dayOfMonthStr = ""
                var monthOfYearStr = ""

                val monthOfYearTemp = monthOfYear + 1

                if (dayOfMonth < 10) {
                    dayOfMonthStr = "0$dayOfMonth"
                } else {
                    dayOfMonthStr = "" + dayOfMonth
                }

                if (monthOfYearTemp < 10) {
                    monthOfYearStr = "0$monthOfYearTemp"
                } else {
                    monthOfYearStr = "" + monthOfYearTemp
                }

                // Display Selected date in textbox
                field.setText("" + year + "/" + monthOfYearStr + "/" + dayOfMonthStr)

            },
            year,
            month,
            day
        )

        dpd.show()
    }

    fun showDatePickerMaxDate(field: TextInputEditText, date: Date){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH + 1)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            context,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                var dayOfMonthStr = ""
                var monthOfYearStr = ""

                val monthOfYearTemp = monthOfYear + 1

                if (dayOfMonth < 10) {
                    dayOfMonthStr = "0$dayOfMonth"
                } else {
                    dayOfMonthStr = "" + dayOfMonth
                }

                if (monthOfYearTemp < 10) {
                    monthOfYearStr = "0$monthOfYearTemp"
                } else {
                    monthOfYearStr = "" + monthOfYearTemp
                }
                // Display Selected date in textbox
                field.setText("" + year + "/" + monthOfYearStr + "/" + dayOfMonthStr)

            },
            year,
            month,
            day
        )
        dpd.datePicker.maxDate = date.time
        dpd.show()
    }

    fun showDatePickerMaxDateClearField(field: TextInputEditText , fieldtoClear: TextInputEditText, date: Date){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH + 1)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
                context,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    var dayOfMonthStr = ""
                    var monthOfYearStr = ""

                    val monthOfYearTemp = monthOfYear + 1

                    if (dayOfMonth < 10) {
                        dayOfMonthStr = "0$dayOfMonth"
                    } else {
                        dayOfMonthStr = "" + dayOfMonth
                    }

                    if (monthOfYearTemp < 10) {
                        monthOfYearStr = "0$monthOfYearTemp"
                    } else {
                        monthOfYearStr = "" + monthOfYearTemp
                    }
                    // Display Selected date in textbox
                    field.setText("" + year + "/" + monthOfYearStr + "/" + dayOfMonthStr)
                    fieldtoClear.setText("")
                },
                year,
                month,
                day
        )
        dpd.datePicker.maxDate = date.time
        dpd.show()
    }

    fun showDatePickerMinDateWithOpeningDate(field: TextInputEditText , date: Date ){
        val c = Calendar.getInstance()
        c.time = date
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            context,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                var dayOfMonthStr = ""
                var monthOfYearStr = ""

                val monthOfYearTemp = monthOfYear + 1

                if (dayOfMonth < 10) {
                    dayOfMonthStr = "0$dayOfMonth"
                } else {
                    dayOfMonthStr = "" + dayOfMonth
                }

                if (monthOfYearTemp < 10) {
                    monthOfYearStr = "0$monthOfYearTemp"
                } else {
                    monthOfYearStr = "" + monthOfYearTemp
                }
                // Display Selected date in textbox
                field.setText("" + year + "/" + monthOfYearStr + "/" + dayOfMonthStr)
            },
            year,
            month,
            day
        )
        dpd.datePicker.minDate = date.time
        dpd.show()
    }

    fun showDatePickerwithYearDifferenceWithMinDate(
        field: TextInputEditText,
        field1: TextInputEditText,
        maxDate: Date
    ){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            context,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                var dayOfMonthStr = ""
                var monthOfYearStr = ""

                val monthOfYearTemp = monthOfYear + 1

                if (dayOfMonth < 10) {
                    dayOfMonthStr = "0$dayOfMonth"
                } else {
                    dayOfMonthStr = "" + dayOfMonth
                }

                if (monthOfYearTemp < 10) {
                    monthOfYearStr = "0$monthOfYearTemp"
                } else {
                    monthOfYearStr = "" + monthOfYearTemp
                }

                val selectedDate = "$dayOfMonthStr-$monthOfYearStr-$year"

                // Display Selected date in textbox
                field.setText(selectedDate)

                val diff = getDiffYears(getDateFromString(selectedDate), Date())
                field1.setText("" + diff)
            },
            year,
            month,
            day
        )
        dpd.datePicker.maxDate = maxDate.time
        dpd.show()
    }

    fun getDateFromString(date: String) : Date{
        val format = SimpleDateFormat("yyyy/MM/dd")
        try {
            val date = format.parse(date)
            System.out.println(date)
            return date
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return Date()
    }

    fun getDiffYears(first: Date?, last: Date?): Int {
        val a = getCalendar(first)
        val b = getCalendar(last)
        var diff = b[YEAR] - a[YEAR]
        if (a[MONTH] > b[MONTH] ||
            a[MONTH] === b[MONTH] && a[DATE] > b[DATE]
        ) {
            diff--
        }
        return diff
    }

    fun getCalendar(date: Date?): Calendar {
        val cal = Calendar.getInstance(Locale.US)
        cal.time = date
        return cal
    }
    fun showTimePicker(field: TextInputEditText){
        val calendar = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
        }
        val timePickerDialog : TimePickerDialog = TimePickerDialog(
            context, timeSetListener, calendar.get(
                Calendar.HOUR_OF_DAY
            ), calendar.get(Calendar.MINUTE), false
        )
       timePickerDialog.show()
    }

    fun getRandomString(length: Int) : String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
                .map { allowedChars.random() }
                .joinToString("")
    }
     fun getImeiNumber(): String? {
       val tempId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        return tempId
    }

    fun getCurrentDate():String{
        val sdf = SimpleDateFormat("MM/dd/yyyy")
        return sdf.format(Date())
    }
    fun getCurrentDateWithTime():String{
        val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm a")
        val temp = sdf.format(Date())
        return temp.toUpperCase()
    }

    fun getDifferenceHoures(from: Date) : Long{
        val diff: Long = from.time - getDateFromString(getCurrentDate()).time
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        return  hours
    }

    fun parseImagetoBase64(image: Bitmap): String? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun parseBytestoBase64(image: ByteArray): String? {
        return Base64.encodeToString(image, Base64.DEFAULT)
    }
    fun copyToClipboard(copyText: String?) {
        val sdk = Build.VERSION.SDK_INT
        if (sdk < Build.VERSION_CODES.HONEYCOMB) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
            clipboard!!.text = copyText
        } else {
            val clipboard =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager?
            val clip = ClipData
                .newPlainText("Your OTP", copyText)
            clipboard!!.setPrimaryClip(clip)
        }
        showToast("result copied")
        //displayAlert("Your OTP is copied");
    }

     fun sendMsg(mobile: String, msg: String){
        try {
            val packageManager = context.packageManager
            val i = Intent(Intent.ACTION_VIEW)
            val url =
                "https://wa.me/$mobile" + "?text=" + URLEncoder.encode(msg, "utf-8")
            i.setPackage("com.whatsapp")
            i.data = Uri.parse(url)
            if (i.resolveActivity(packageManager) != null) {
                context.startActivity(i)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}