package com.aw.forcement.sbp

import Json4Kotlin_Base
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.aw.forcement.R
import com.aw.passanger.api.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_business_details.*


class BusinessDetails : AppCompatActivity() {

    private lateinit var subCountyID: String
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

        radio_section.setOnClickListener { building_occupancy ="SECTION" }
        radio_whole.setOnClickListener { building_occupancy= "WHOLE" }

        getSubCounties()
        getFloor()
    }
    fun saveValues(){
        save(this,"business_name",ed_business_name.text.toString())
        save(this,"subCountyID",subCountyID)
        save(this,"wardID",wardID)
        save(this,"plotNumber",ed_plot_number.text.toString())
        save(this,"physicalAddress",ed_physical_address.text.toString())
        save(this,"buildingName",ed_building_name.text.toString())
        save(this,"buildingOccupancy",building_occupancy)
        save(this,"floorNo",floorNo)
        save(this,"room_no",ed_room_no.text.toString())

    }
    private fun getSubCounties (){
        val formData = listOf(
            "function" to "getSubCounty",
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

        })
    }
    private fun getWards (){
        val formData = listOf(
            "function" to "getWards",
            "subCountyID" to subCountyID
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

                            }
                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }
                        }

                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@BusinessDetails,response.message, Toast.LENGTH_LONG).show()}

                }

            }

        })
    }
    private fun getFloor (){
        val formData = listOf(
            "function" to "getFloor",
        )
        executeRequest(formData, trade,object : CallBack {
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

        })
    }
}