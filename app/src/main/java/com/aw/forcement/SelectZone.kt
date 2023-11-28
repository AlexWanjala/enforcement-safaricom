package com.aw.forcement

import Json4Kotlin_Base
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.aw.forcement.tabs.Home
import com.aw.passanger.api.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_cess_payments.*
import kotlinx.android.synthetic.main.activity_select_zone.*
import kotlinx.android.synthetic.main.bottom_sheet_sbp_permit.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.update_demographics.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SelectZone : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener  {
    private val arrayList = ArrayList<String>()
    var zone ="";

    private val arrayList2 = ArrayList<String>()
    var wardName ="";

    private val arrayList3 = ArrayList<String>()
    var subCountyName ="";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_zone)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        layout_subs.visibility = View.GONE

        zone = getValue(this,"zone").toString()
        wardName = getValue(this,"wardName").toString()
        subCountyName = getValue(this,"subCountyName").toString()
        btn_done.setOnClickListener {
            updateUser()
        }

        swipeRefreshLayout.setOnRefreshListener(this)


        getSubCounty()
        val category = getValue(this,"category")
        if (category == "ICT OFFICER" ||  category == "SUPERVISORS"  || category == "SUPER ADMIN" || category == "DEPUTY DIRECTOR"
            || category == "ACCOUNTANTS" || category == "DIRECTOR REVENUE" || category == "ENFORCEMENT" ){
            layout_subs.visibility = View.VISIBLE
        }else{
            getWards(getValue(this,"subCountyID").toString())
        }



    }


    private fun updateUser(){
        progress_circular.visibility = View.VISIBLE

        val formData = listOf(
            "function" to "updateUser",
            "names" to getValue(this,"username").toString(),
            "email" to getValue(this,"email").toString(),
            "phoneNumber" to getValue(this,"phoneNumber").toString(),
            "category" to getValue(this,"category").toString(),
            "idNo" to getValue(this,"idNo").toString(),
            "zone" to getValue(this,"zone").toString(),
            "subCountyID" to getValue(this,"subCountyID").toString(),
            "subCountyName" to getValue(this,"subCountyName").toString(),
            "wardID" to getValue(this,"wardID").toString(),
            "wardName" to getValue(this,"wardName").toString(),
            "gender" to getValue(this,"gender").toString(),
            "id" to getValue(this,"id").toString()
        )
        executeRequest(formData, authentication,object : CallBack {
            override fun onSuccess(result: String?) {
                Log.d("###", "Response result: $result")
                runOnUiThread {   progress_circular.visibility = View.GONE }
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    runOnUiThread {
                        val formatter = SimpleDateFormat("yyyy-MM-dd")
                        val date = formatter.format(Date())
                        save(this@SelectZone,"date",date)
                        save(this@SelectZone,"zone",zone)
                        startActivity(Intent(this@SelectZone,Home::class.java))
                        finishAffinity()

                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@SelectZone,response.message, Toast.LENGTH_LONG).show()}

                }

            }

            override fun onFailure(result: String?) {
                runOnUiThread {
                    progress_circular.visibility = View.GONE
                    Toast.makeText(this@SelectZone,result, Toast.LENGTH_LONG).show()
                }
            }

        })
    }


    private fun getZones (){

        arrayList.clear()

        val formData = listOf(
            "function" to "getZones",
            "subCountyID" to getValue(this,"subCountyID").toString())
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    runOnUiThread {

                        if (response.data.zones.any { it.zone == zone }) {
                            arrayList.add(zone)
                        }

                        for(data in response.data.zones){
                            if (zone!=data.zone)
                                arrayList.add(data.zone)
                        }

                        //Spinner
                        val adapters = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item,arrayList)
                        adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        spinner_zone.adapter = adapters
                        spinner_zone.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {
                                zone = arrayList[postion]

                                for(data in response.data.zones){
                                    if (zone==data.zone) {
                                        save(this@SelectZone,"zone",data.zone)
                                        save(this@SelectZone,"zoneCode",data.zoneCode)

                                    }
                                }
                            }
                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }
                        }
                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@SelectZone,response.message, Toast.LENGTH_LONG).show()}

                }

            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@SelectZone,result, Toast.LENGTH_LONG).show()
                }
            }

        })
    }
    private fun getWards(subCountyID : String) {

        arrayList2.clear()
        val formData = listOf(
            "function" to "getWards",
            "subCountyID" to subCountyID
        )
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    runOnUiThread {

                        if (response.data.wards.any { it.wardName == wardName }) {
                            arrayList2.add(wardName)
                        }


                        for(data in response.data.wards){
                            if (wardName!=data.wardName)
                                arrayList2.add(data.wardName)
                        }

                        //Spinner
                        val adapters = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item,arrayList2)
                        adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        spinner_ward.adapter = adapters
                        spinner_ward.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {
                                wardName = arrayList2[postion]

                                for(data in response.data.wards){
                                    if (wardName==data.wardName) {
                                        save(this@SelectZone,"wardID",data.wardID)
                                        save(this@SelectZone,"wardName",data.wardName)

                                    }
                                }
                            }
                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }
                        }
                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@SelectZone,response.message, Toast.LENGTH_LONG).show()}

                }

            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@SelectZone,result, Toast.LENGTH_LONG).show()
                }
            }


        })
    }

    private fun getSubCounty() {
        swipeRefreshLayout.isRefreshing = true
        arrayList3.clear()
        val formData = listOf(
            "function" to "getSubCounty",
        )
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    runOnUiThread {
                        swipeRefreshLayout.isRefreshing = false

                      /*  // Check if wardName exists in response.data.wards before adding it to arrayList2
                        if (!response.data.subCounties.any { it.subCountyName == subCountyName }) {
                            arrayList3.add(subCountyName)
                        }
*/
                        arrayList3.add(subCountyName)
                        for(data in response.data.subCounties){
                            if (subCountyName!=data.subCountyName)
                                arrayList3.add(data.subCountyName)
                        }

                        //Spinner
                        val adapters = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item,arrayList3)
                        adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        spinner_sub_county.adapter = adapters
                        spinner_sub_county.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {
                                subCountyName = arrayList3[postion]

                                for(data in response.data.subCounties){
                                    if (subCountyName==data.subCountyName) {
                                        save(this@SelectZone,"subCountyID",data.subCountyID)
                                        save(this@SelectZone,"subCountyName",data.subCountyName)
                                        runOnUiThread {
                                            getWards(data.subCountyID)
                                            getZones()
                                        }
                                    }
                                }
                            }
                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }
                        }
                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@SelectZone,response.message, Toast.LENGTH_LONG).show()}

                }

            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    swipeRefreshLayout.isRefreshing = false
                    progress_circular.visibility = View.GONE
                    Toast.makeText(this@SelectZone,result, Toast.LENGTH_LONG).show()
                }
            }

        })
    }

    override fun onBackPressed() {
        Toast.makeText(this,"The only way out is to select the zone and click done",Toast.LENGTH_LONG).show()
    }

    override fun onRefresh() {
     runOnUiThread {
       //  getSubCounty()
        // getZones()
     }
    }
}

