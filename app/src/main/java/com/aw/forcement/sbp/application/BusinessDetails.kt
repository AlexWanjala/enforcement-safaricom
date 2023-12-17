package com.aw.forcement.sbp.application

import Json4Kotlin_Base
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.aw.forcement.R
import com.aw.passanger.api.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_business_details.*


class BusinessDetails : AppCompatActivity() {

    private lateinit var subCountyID: String
    private lateinit var subCountyName: String
    private lateinit var wardName: String
    private lateinit var wardID: String
    private lateinit var businessActivity: String
    private lateinit var businessActivityDescription: String
    private lateinit var brimsCode: String
    private lateinit var parentBrimsCode: String
    private lateinit var registrationFee: String

    private val arrayList = ArrayList<String>()
    private val arrayList2 = ArrayList<String>()
    private val arrayList3 = ArrayList<String>()
    private val arrayList4 = ArrayList<String>()
    private val arrayList5 = ArrayList<String>()

    var building_occupancy =""
    var floorNo =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_details)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        radio_section.setOnClickListener { building_occupancy ="SECTION" }
        radio_whole.setOnClickListener { building_occupancy= "WHOLE" }

        btn_next.setOnClickListener {
            saveIfNotEmpty()
        }
        btn_previous.setOnClickListener { finish() }

        getSubCounties()
        getFloor()
    }

    private fun saveIfNotEmpty () {
        // Get the text from the edit texts
        val businessName = ed_business_name.text.toString ()
        val subCountyID = subCountyID
        val subCountyName = subCountyName
        val wardName = wardName
        val wardID = wardID
        val plotNumber = ed_plot_number.text.toString ()
        val physicalAddress = ed_physical_address.text.toString ()
        val buildingName = ed_building_name.text.toString ()
        val buildingOccupancy = building_occupancy
        val floorNo = floorNo
        val roomNo = ed_room_no.text.toString ()

       // Check if any of them is empty
        if (businessName.isEmpty () || subCountyID.isEmpty () || subCountyName.isEmpty () || wardName.isEmpty () || wardID.isEmpty () || plotNumber.isEmpty () || physicalAddress.isEmpty ()) {
           // Show a toast message or an error message
            Toast.makeText (this, "Please fill all the fields", Toast.LENGTH_SHORT).show ()
        } else {
         // Save the data using your save function
            save (this, "business_name", businessName)
            save (this, "subCountyID", subCountyID)
            save (this, "subCountyName", subCountyName)
            save (this, "wardName", wardName)
            save (this, "wardID", wardID)
            save (this, "plotNumber", plotNumber)
            save (this, "physicalAddress", physicalAddress)
            save (this, "buildingName", buildingName)
            save (this, "buildingOccupancy", buildingOccupancy)
            save (this, "floorNo", floorNo)
            save (this, "room_no", roomNo)
            startActivity(Intent(this, BusinessContact::class.java))
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

                        for(data in response.data.subCounties){
                            arrayList2.add(data.subCountyName)
                        }

                        //Spinner
                        val adapters = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item,arrayList2)
                        adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        spinner_sub_county.adapter = adapters
                        spinner_sub_county.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {
                                subCountyID =response.data.subCounties[postion].subCountyID
                                subCountyName =response.data.subCounties[postion].subCountyName
                                getWards()

                            }
                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }
                        }

                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@BusinessDetails,response.message, Toast.LENGTH_LONG).show()}

                }

            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@BusinessDetails,result, Toast.LENGTH_LONG).show()
                }
            }

        })
    }
    private fun getWards (){
        val formData = listOf(
            "function" to "getWards",
            "subCountyID" to subCountyID,
            "deviceId" to getDeviceIdNumber(this)
        )
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    runOnUiThread {
                        arrayList3.clear()
                        for(data in response.data.wards){
                            arrayList3.add(data.wardName)
                        }
                        //Spinner
                        val adapters = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item,arrayList3)
                        adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        spinner_ward.adapter = adapters
                        spinner_ward.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {
                                wardID =response.data.wards[postion].wardID.toString()
                                wardName =response.data.wards[postion].wardName.toString()
                            }
                            override fun onNothingSelected(p0: AdapterView<*>?) {
                            }
                        }
                    }
                }else{
                    runOnUiThread { Toast.makeText(this@BusinessDetails,response.message, Toast.LENGTH_LONG).show()}
                }
            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@BusinessDetails,result, Toast.LENGTH_LONG).show()
                }
            }

        })
    }
    private fun getFloor (){
        val formData = listOf(
            "function" to "getFloor",
            "deviceId" to getDeviceIdNumber(this)
        )
        executeRequest(formData,trade,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    runOnUiThread {
                        arrayList4.clear()
                        for(data in response.data.floors){
                            arrayList4.add(data.floor)
                        }

                        //Spinner
                        val adapters = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item,arrayList4)
                        adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        spinner_floor.adapter = adapters
                        spinner_floor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {
                                floorNo =response.data.floors[postion].floor
                            }
                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }
                        }
                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@BusinessDetails,response.message, Toast.LENGTH_LONG).show()}

                }

            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@BusinessDetails,result, Toast.LENGTH_LONG).show()
                }
            }

        })
    }
}