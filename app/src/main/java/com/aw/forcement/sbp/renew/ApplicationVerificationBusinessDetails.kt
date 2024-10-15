package com.aw.forcement.sbp.renew

import Const
import Json4Kotlin_Base
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import com.aw.forcement.R
import com.aw.passanger.api.CallBack
import com.aw.passanger.api.biller
import com.aw.passanger.api.executeRequest
import com.aw.passanger.api.getDeviceIdNumber
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_application_verification_business_details.*
import kotlinx.android.synthetic.main.update.view.*
import kotlinx.android.synthetic.main.update.view.okay
import kotlinx.android.synthetic.main.update.view.tv_title
import kotlinx.android.synthetic.main.update_demographics.view.*

class ApplicationVerificationBusinessDetails : AppCompatActivity() {

    lateinit var messageBoxViewDemo : View
    var wardName =""
    var subCountyName =""
    var subCountyID =""
    var wardID =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_application_verification_business_details)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
         messageBoxViewDemo = LayoutInflater.from(this).inflate(R.layout.update_demographics, null)

        btn_next.setOnClickListener { startActivity(Intent(this,ApplicationVerificationBusinessActivityInformation::class.java)) }


    }

    private fun displayValues(){

        tv_business_name.text = Const.instance.getBusiness()?.businessName
        tv_sub_county.text = Const.instance.getBusiness()?.subCountyName
        tv_ward.text = Const.instance.getBusiness()?.wardName
        tv_address.text = Const.instance.getBusiness()?.physicalAddress
        tv_business_building.text = Const.instance.getBusiness()?.buildingName
        tv_floor.text = Const.instance.getBusiness()?.floorNo
        tv_room_number.text = Const.instance.getBusiness()?.roomNo

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showMessageBox(title: String,message: String){

        val messageBoxView = LayoutInflater.from(this).inflate(R.layout.update, null)
        val messageBoxBuilder = AlertDialog.Builder(this).setView(messageBoxView)
        val messageBoxInstance = messageBoxBuilder.show()

        messageBoxView.tv_title.text = "Change Business "+ title
        messageBoxView.ed_message.setText(message)

        //set Listener
        messageBoxView.setOnClickListener(){

            messageBoxInstance.dismiss()
        }

        messageBoxView.okay.setOnClickListener {

            if (title == "Business Name") {
                val currentBusiness = Const.instance.getBusiness()
                val newBusiness = currentBusiness?.copy(businessName = messageBoxView.ed_message.text.toString())
                Const.instance.setBusiness(newBusiness)
            }
            if(title=="Physical Address"){
                val currentBusiness = Const.instance.getBusiness()
                val newBusiness = currentBusiness?.copy(physicalAddress = messageBoxView.ed_message.text.toString())
                Const.instance.setBusiness(newBusiness)
            }
            if(title=="Building Name"){
                val currentBusiness = Const.instance.getBusiness()
                val newBusiness = currentBusiness?.copy(buildingName = messageBoxView.ed_message.text.toString())
                Const.instance.setBusiness(newBusiness)
            }

            if(title=="Floor No"){
                val currentBusiness = Const.instance.getBusiness()
                val newBusiness = currentBusiness?.copy(floorNo = messageBoxView.ed_message.text.toString())
                Const.instance.setBusiness(newBusiness)
            }

            if(title=="Room No"){
                val currentBusiness = Const.instance.getBusiness()
                val newBusiness = currentBusiness?.copy(roomNo = messageBoxView.ed_message.text.toString())
                Const.instance.setBusiness(newBusiness)
            }

            displayValues()
            messageBoxInstance.dismiss()
        }

    }

    private fun getSubCounties (){

        val formData = listOf(
            "function" to "getSubCounty",
            "deviceId" to getDeviceIdNumber(this)
        )
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    runOnUiThread {

                        arrayList2.clear()

                        var position = 0
                        var x = 0

                        for (data in response.data.subCounties) {
                            arrayList2.add(data.subCountyName)
                            if (data.subCountyID == Const.instance.getBusiness()?.subCountyID) {
                                position = x
                            }
                            x++
                        }

                        //Spinner
                        val adapters = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item, arrayList2)
                        adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        messageBoxViewDemo.spinner_sub_county.adapter = adapters

                      // Set the initial selection of the spinner based on the business object's sub-county ID
                        messageBoxViewDemo.spinner_sub_county.setSelection(position)

                      // Declare a variable to store the sub-county ID of the selected option
                        var subCountyID_ = response.data.subCounties[position].subCountyID

                        messageBoxViewDemo.spinner_sub_county.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {
                                // Update the sub-county ID variable with the new selection
                                subCountyID_ = response.data.subCounties[postion].subCountyID
                                subCountyID = response.data.subCounties[postion].subCountyID
                                subCountyName = response.data.subCounties[postion].subCountyName

                               // Get the wards based on the sub-county ID
                                getWards(subCountyID_)

                            }

                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }
                        }
                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@ApplicationVerificationBusinessDetails,response.message, Toast.LENGTH_LONG).show()}

                }

            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@ApplicationVerificationBusinessDetails,result, Toast.LENGTH_LONG).show()
                }
            }

        })
    }


    private val arrayList3 = ArrayList<String>()
    private val arrayList2 = ArrayList<String>()

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showMessageBoxDemo(title: String,message: String){

        // Check if messageBoxView has a parent
        if (messageBoxViewDemo.parent != null) {
            // Remove messageBoxView from its parent
            (messageBoxViewDemo.parent as ViewGroup).removeView(messageBoxViewDemo)
        }


        val messageBoxBuilderDemo = AlertDialog.Builder(this).setView(messageBoxViewDemo)
        val messageBoxInstance = messageBoxBuilderDemo.show()

        messageBoxViewDemo.tv_title.text = "Change Business "+ title


        //set Listener
        messageBoxViewDemo.setOnClickListener(){

            messageBoxInstance.dismiss()
        }

        messageBoxViewDemo.okay.setOnClickListener {
            val currentBusiness = Const.instance.getBusiness()
            val newBusiness = currentBusiness
                ?.copy(wardID = wardID)
                ?.copy(subCountyID = subCountyID)
                ?.copy(subCountyName= subCountyName)
                ?.copy(wardName = wardName)

            Const.instance.setBusiness(newBusiness)

            displayValues()
            messageBoxInstance.dismiss()
        }

        getSubCounties()

    }

    fun getWards(subCountyID: String) {

        val formData = listOf(
            "function" to "getWards",
            "subCountyID" to subCountyID,
            "deviceId" to getDeviceIdNumber(this)
        )
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    arrayList3.clear()

                    runOnUiThread { var position = 0
                        var x = 0

                        for (data in response.data.wards) {
                            arrayList3.add(data.wardName)
                            if (data.wardID == Const.instance.getBusiness()?.wardID) {
                                position = x
                            }
                            x++
                        }

                        //Spinner
                        val adapters = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item, arrayList3)
                        adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        messageBoxViewDemo.spinner_ward.adapter = adapters

                       // Set the initial selection of the spinner based on the business object's sub-county ID
                        messageBoxViewDemo.spinner_ward.setSelection(position)

                       // Declare a variable to store the sub-county ID of the selected option
                        var wardID_ = response.data.wards[position].wardID

                        messageBoxViewDemo.spinner_ward.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {
                           // Update the sub-county ID variable with the new selection
                                wardID_ = response.data.wards[postion].wardID
                                wardID = response.data.wards[position].wardID
                                wardName = response.data.wards[position].wardName

                            }

                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }
                        } }

                }else{
                    runOnUiThread {  Toast.makeText(this@ApplicationVerificationBusinessDetails,response.message, Toast.LENGTH_LONG).show()}

                }

            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@ApplicationVerificationBusinessDetails,result, Toast.LENGTH_LONG).show()
                }
            }

        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun update(view: View) {
        when (view.id) {
            R.id.btn_business_name -> {
                showMessageBox("Business Name", Const.instance.getBusiness()!!.businessName)
            }

            R.id.btn_sub_county -> {
                showMessageBoxDemo("Sub County",Const.instance.getBusiness()!!.subCountyID)
            }

            R.id.btn_ward -> {
                showMessageBoxDemo("Ward",Const.instance.getBusiness()!!.wardID)
            }

            R.id.btn_physical_address -> {
                showMessageBox("Physical Address",Const.instance.getBusiness()!!.physicalAddress)
            }

            R.id.btn_building_name -> {
                showMessageBox("Building Name",Const.instance.getBusiness()!!.buildingName)
            }

            R.id.btn_floor_no -> {
                showMessageBox("Floor No",Const.instance.getBusiness()!!.floorNo)
            }

            R.id.btn_room_no -> {
                showMessageBox("Room No",Const.instance.getBusiness()!!.roomNo)
            }

            else -> {

            }
        }
    }

    override fun onResume() {
        displayValues()
        super.onResume()
    }

}