package com.aw.forcement

import Json4Kotlin_Base
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.aw.forcement.tabs.Home
import com.aw.passanger.api.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_cess_payments.*
import kotlinx.android.synthetic.main.activity_select_zone.*
import kotlinx.android.synthetic.main.progressbar.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SelectZone : AppCompatActivity() {
    private val arrayList = ArrayList<String>()
    var zone ="";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_zone)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        zone = getValue(this,"zone").toString()
        btn_done.setOnClickListener { updateUserZone() }
        getZones()

    }

    private fun updateUserZone (){
        progress_circular.visibility = View.VISIBLE

        val formData = listOf(
            "function" to "updateUserZone",
            "zone" to zone,
            "idNo" to getValue(this,"idNo").toString())
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
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

        })
    }
    private fun getZones (){

        val formData = listOf(
            "function" to "getZones",
            "subCountyID" to getValue(this,"subCountyID").toString())
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    runOnUiThread {
                        arrayList.add(zone)
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
                            }
                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }
                        }
                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@SelectZone,response.message, Toast.LENGTH_LONG).show()}

                }

            }

        })
    }

    override fun onBackPressed() {
        Toast.makeText(this,"The only way out is to select the zone and click done",Toast.LENGTH_LONG).show()
    }
}

