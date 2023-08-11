package com.aw.forcement

import Json4Kotlin_Base
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Camera
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.aw.forcement.ocr.OcrDetectorProcessor
import com.aw.forcement.ocr.OcrGraphic
import com.aw.forcement.ocr.camera.CameraSource
import com.aw.forcement.ocr.camera.CameraSourcePreview
import com.aw.forcement.ocr.camera.GraphicOverlay
import com.aw.forcement.others.CessPayments
import com.aw.forcement.others.Street
import com.aw.passanger.api.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_street_parking.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


import java.io.IOException
import java.lang.NullPointerException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class StreetParking : AppCompatActivity(){
    private val arrayList = ArrayList<String>()
    private val arrayList2 = ArrayList<String>()

    lateinit var category_code: String
    lateinit var duration : String
    lateinit var payer : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_street_parking)

        getCategory()
        tvSendPush.setOnClickListener {
            showMessageBox()
            if(edPlate.text.isEmpty()){
                Toast.makeText(this,"Please input Number Plate",Toast.LENGTH_LONG).show()
            }else{
                if(edPhone.text.isEmpty()){
                    Toast.makeText(this,"Please input Number Plate",Toast.LENGTH_LONG).show()
                }else{
                    tvSendPush.visibility = View.GONE
                    tvSendPushDisabled.visibility = View.VISIBLE
                    matatuPayment()
                }
            }
        }

        imageClose.setOnClickListener { finish() }



    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showMessageBox(){
        val messageBoxView = LayoutInflater.from(this).inflate(R.layout.payment_recieved, null)
        val messageBoxBuilder = androidx.appcompat.app.AlertDialog.Builder(this).setView(messageBoxView)
        val messageBoxInstance = messageBoxBuilder.show()


        //setting text values
       /// messageBoxView.imageView.setOnClickListener { messageBoxInstance.dismiss()}
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
    private fun getCategory(){
        progressBar1.visibility = View.VISIBLE
        val formData = listOf(
            "function" to "getZones",
        )
        executeRequest(formData, parking,object : CallBack {
            override fun onSuccess(result: String?) {
                runOnUiThread {  progressBar1.visibility = View.GONE }
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){
                     //spinner_zone
                    runOnUiThread {
                        progressBar1.visibility = View.GONE

                        for(data in response.data.categories){
                            arrayList.add(data.category)
                        }

                        for(data in response.data.durations){
                            arrayList2.add(data.duration)
                        }

                        //Spinner
                        val adapters = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item,arrayList)
                        adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        spinner_search2.adapter = adapters
                        spinner_search2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {

                                category_code = response.data.categories[postion].category
                                getParkingCharges()
                            }
                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }
                        }

                        //Zones
                        val adaptersZone = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item,arrayList2)
                        adaptersZone.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        spinner_zone.adapter = adaptersZone
                        spinner_zone.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {

                                duration = response.data.durations[postion].duration
                                runOnUiThread { tvDuration.text = duration }
                            }
                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }
                        }
                    }

                }else{
                    runOnUiThread {
                      //  Toast.makeText(this@Pay,response.message, Toast.LENGTH_LONG).show()
                    }

                }

            }

        })


    }
    private fun getParkingCharges(){
        tv_message.text =""
        val formData = listOf(
            "function" to "getParkingCharges",
            "category" to category_code,
            "duration" to duration,
            "zone" to getValue(this,"zone").toString(),
        )
        executeRequest(formData, parking,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    runOnUiThread { tvAmount.text = "KES "+response.data.feesAndCharge.unitFeeAmount }
                }
                else{
                   runOnUiThread {
                       tv_message.text = response.message
                   }
                }

            }

        })


    }
    private fun matatuPayment(){
        tv_message.text ="Generating bill please wait..$duration $category_code"
        val formData = listOf(
            "function" to "matatuPayment",
            "numberPlate" to edPlate.text.toString(),
            "category" to category_code,
            "duration" to duration,
            "zone" to getValue(this,"zone").toString(),
            "names" to getValue(this,"names").toString(),
            "phoneNumber" to getValue(this,"phoneNumber").toString(),
            "idNo" to getValue(this,"idNo").toString(),
            "username" to getValue(this,"username").toString(),
            "subCountyID" to getValue(this,"subCountyID").toString(),
            "subCountyName" to getValue(this,"subCountyName").toString(),
            "wardID" to getValue(this,"wardID").toString(),
            "wardName" to getValue(this,"wardName").toString(),
            "customerPhoneNumber" to edPhone.text.toString(),
        )
        executeRequest(formData, parking,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){
                    runOnUiThread {tv_message.text ="Bill generated success.." }

                    customerPayBillOnline(
                        response.data.billGenerated.billNo,
                        response.data.billGenerated.payBillNo,
                        response.data.billGenerated.amount
                    )

                }
                else{
                    runOnUiThread {
                        tv_message.text = response.message
                    }
                }

            }

        })


    }
    private fun customerPayBillOnline(accountReference: String, payBillNumber: String, amount: String){
        // progressBar1.visibility = View.VISIBLE
        runOnUiThread {   tv_message.text ="Sending Payment Request.." }
        val formData = listOf(
            "function" to "customerPayBillOnline",
            "payBillNumber" to payBillNumber,
            "amount" to amount,
            "accountReference" to accountReference,
            "transactionDesc" to accountReference,
            "phoneNumber" to edPhone.text.toString(),
            "token" to "im05WXYH2rwRruPjCICieOs8m4E8IoltnDEhyPUv6bnB9cU60gD48SnJPC6oh7EpsPaAUGC8wqIdtVVjGlWLxqFssshxMHxHjEQJ"
        )
        executePaysolRequest(formData, paysol,object : CallBack {
            override fun onSuccess(result: String?) {
                //  runOnUiThread {  progress_circular.visibility = View.GONE }
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    runOnUiThread {
                        // progressBar1.visibility = View.GONE
                        checkPayment(accountReference)
                    }

                }else{
                    runOnUiThread {
                        tvSendPush.visibility = View.VISIBLE
                        tvSendPushDisabled.visibility = View.GONE
                        tv_message.text = response.message
                    }

                }

            }

        })

    }
    fun checkPayment(accountReference: String){
        //  runOnUiThread {   progressBarPayments.visibility = View.VISIBLE }
        val formData = listOf(
            "function" to "checkPayment",
            "accNo" to accountReference,
            "token" to "im05WXYH2rwRruPjCICieOs8m4E8IoltnDEhyPUv6bnB9cU60gD48SnJPC6oh7EpsPaAUGC8wqIdtVVjGlWLxqFssshxMHxHjEQJ"
        )
        executePaysolRequest(formData, paysol,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)

                if(response.success){

                    if(response.data.push.callback_returned=="PAID"){

                        runOnUiThread {

                            tvSendPush.visibility = View.VISIBLE
                            tvSendPushDisabled.visibility = View.GONE

                            tv_message.text ="Payment Received #${response.data.push.transaction_code} KES ${response.data.push.amount}"
/*
                            transactionCode.text = response.data.push.transaction_code
                            tvAmount.text = "KES "+response.data.push.amount
                            tvRef.text = response.data.push.ref
                            tvStatus.text = response.data.push.callback_returned; */

                        }


                    }else if(response.data.push.callback_returned=="PENDING"){
                        runOnUiThread { tv_message.text ="Waiting for payment.." }
                        TimeUnit.SECONDS.sleep(2L)
                        checkPayment(accountReference)
                    }else{
                        runOnUiThread {
                            tv_message.text = response.data.push.message
                            tvSendPush.visibility = View.VISIBLE
                            tvSendPushDisabled.visibility = View.GONE

                        }
                    }

                }
                else{
                    runOnUiThread { tv_message.text ="Waiting for payment.." }
                    TimeUnit.SECONDS.sleep(2L)
                    checkPayment(accountReference)
                }

            }

        })
    }

}
