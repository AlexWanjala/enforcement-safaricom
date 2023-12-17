package com.aw.forcement.others

import Json4Kotlin_Base
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import com.aw.forcement.ChangePassword
import com.aw.forcement.R
import com.aw.forcement.tabs.Home
import com.aw.passanger.api.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_business.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_parking.*
import kotlinx.android.synthetic.main.activity_parking.back
import kotlinx.android.synthetic.main.activity_receipt.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.bottom_sheet.bottomSheetLayout
import kotlinx.android.synthetic.main.bottom_sheet_clamp.*
import kotlinx.android.synthetic.main.date.*
import kotlinx.android.synthetic.main.message.*
import kotlinx.android.synthetic.main.progressbar.*

class Parking : AppCompatActivity() {
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private val arrayList = ArrayList<String>()
    private val arrayListTown = ArrayList<String>()
    lateinit var reasonId: String
    lateinit var townId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parking)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
        back.setOnClickListener { finish() }
        val response = Gson().fromJson(intent.getStringExtra("result"), Json4Kotlin_Base::class.java)

        tvNumberPlate.text = response.data.parking.numberPlate
        tvDuration.text = response.data.parking.duration
        tvCategory.text = response.data.parking.category
        tvParkingFee.text = "KES"+" " +response.data.parking.billTotal
        tvAmountPaid.text = "KES"+" " +response.data.parking.receiptAmount
        tvZone.text = response.data.parking.zone
        tvPaymentDate.text = response.data.parking.startDate
        tvPenalty.text ="KES 0.0"
        tvCurrentState.text = response.data.parking.status
        tvStart.text = response.data.parking.startDate
        tvEnd.text = response.data.parking.endDate

        if(response.data.parking.status =="UNPAID"){
            button.text ="CLAMP VEHICLE"
            button.setBackgroundColor(Color.RED)
        }
        if(response.data.parking.status =="UNPAID")
            tvCurrentState.setBackgroundColor(Color.parseColor("#AC4A4A"))
             button.setOnClickListener { GetEnforcementReasons()
             }

             save(this,"numberPlate",response.data.parking.numberPlate)

    }

    fun toggleBottomSheet(){
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

   fun GetEnforcementReasons(){
        progress.visibility = View.VISIBLE

       val formData = listOf(
           "function" to "clampreasons",
           "deviceId" to getDeviceIdNumber(this)
       )
       executeRequest(formData, com.aw.passanger.api.parking,object : CallBack{
           override fun onSuccess(result: String?) {
               runOnUiThread {  progress.visibility = View.GONE }
               val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
               if(response.success){
                   runOnUiThread {
                       toggleBottomSheet()

                       closeit.setOnClickListener {
                           bottomSheetBehavior.state= BottomSheetBehavior.STATE_COLLAPSED
                       }

                       buttonClamp.setOnClickListener { clampVehicle() }

                       for(data in response.data.clampreasons){
                           arrayList.add(data.reason)
                       }

                       //Spinner
                       val adapters = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item,arrayList)
                       adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                       spinner.adapter = adapters
                       spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                           override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {
                               reasonId  = response.data.clampreasons[postion].id.toString()
                           }
                           override fun onNothingSelected(p0: AdapterView<*>?) {

                           }
                       }

                   }

               }else{
                   runOnUiThread {
                       tvMessage.text = response.message }

               }

           }
           override fun onFailure(result: String?) {
               runOnUiThread {
                   Toast.makeText(this@Parking,result, Toast.LENGTH_LONG).show()
               }
           }

       })

}
    fun clampVehicle(){

        progress.visibility = View.VISIBLE
        val formData = listOf(
            "function" to "clampVehicle",
            "numberPlate" to getValue(this,"numberPlate").toString(),
            "padlockNumber" to padlockNumber.text.toString(),
            "reason" to reasonId,
            "TownId" to getValue(this,"TownId").toString(),
            "deviceId" to getDeviceIdNumber(this)
        )
        executeRequest(formData, parking,object : CallBack{
            override fun onSuccess(result: String?) {
                runOnUiThread {  progress.visibility = View.GONE }
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){
                    runOnUiThread {
                        tvCurrentState.setBackgroundColor(Color.parseColor("#AC4A4A"))
                        tvCurrentState.text ="CLAMPED"
                        button.text ="VEHICLE HAS BEEN CLAMPED"
                        button.setBackgroundColor(Color.RED)
                        Toast.makeText(this@Parking,response.message,Toast.LENGTH_LONG).show()
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                }
                else{
                    runOnUiThread {
                        Toast.makeText(this@Parking,response.message,Toast.LENGTH_LONG).show()
                    }
                }

            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@Parking,result, Toast.LENGTH_LONG).show()
                }
            }

        })
    }

}