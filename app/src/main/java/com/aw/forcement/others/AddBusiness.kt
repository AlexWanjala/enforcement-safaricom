package com.aw.forcement.others

import Json4Kotlin_Base
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.aw.forcement.R
import com.aw.passanger.api.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_add_business.*
import kotlinx.android.synthetic.main.activity_add_business.edPhoneNumber
import kotlinx.android.synthetic.main.activity_add_business.tvSendPayment
import kotlinx.android.synthetic.main.activity_add_business.tv_message


import java.util.concurrent.TimeUnit

class AddBusiness : AppCompatActivity() {

    private lateinit var postalCode: String
    private lateinit var subCountyID: String
    private lateinit var wardID: String
    private lateinit var businessActivity: String
    private lateinit var businessActivityDescription: String
    private lateinit var brimsCode: String
    private lateinit var parentBrimsCode: String
    private lateinit var registrationFee: String
    private lateinit var sbpFee: String

    private val arrayList = ArrayList<String>()
    private val arrayList2 = ArrayList<String>()
    private val arrayList3 = ArrayList<String>()
    private val arrayList4 = ArrayList<String>()
    private val arrayList5 = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_business)

        tvSendPayment.setOnClickListener {
            addBusiness()
        }

        getSubCounties()
        getTradeCategory()
    }


    private fun getSubCounties (){
        val formData = listOf(
            "function" to "getSubCounties",
            "countyID" to "3"
        )
        executeRequest(formData, trade,object : CallBack {
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
                        spinnerSubCounty.adapter = adapters
                        spinnerSubCounty.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {
                                subCountyID =response.data.subCounties[postion].subCountyID
                                getWards()

                            }
                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }
                        }

                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@AddBusiness,response.message, Toast.LENGTH_LONG).show()}

                }

            }

        })
    }
    private fun getWards (){
        val formData = listOf(
            "function" to "getWards",
            "subCountyID" to subCountyID
        )
        executeRequest(formData, trade,object : CallBack {
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
                        spinnerWard.adapter = adapters
                        spinnerWard.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {
                                wardID =response.data.wards[postion].wardID.toString()

                            }
                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }
                        }

                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@AddBusiness,response.message, Toast.LENGTH_LONG).show()}

                }

            }

        })
    }

    private fun getTradeCategory (){
        val formData = listOf(
            "function" to "getTradeCategory",
        )
        executeRequest(formData, trade,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    runOnUiThread {
                        arrayList4.clear()
                        for(data in response.data.tradeCategories){
                            arrayList4.add(data.businessActivityName)
                        }

                        //Spinner
                        val adapters = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item,arrayList4)
                        adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        spinnerBusinessActivity.adapter = adapters
                        spinnerBusinessActivity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {
                                businessActivity = response.data.tradeCategories[postion].businessActivityName
                                parentBrimsCode =response.data.tradeCategories[postion].brimsCode
                                getTradeSubCategory()

                            }
                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }
                        }

                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@AddBusiness,response.message, Toast.LENGTH_LONG).show()}

                }

            }

        })
    }
    private fun getTradeSubCategory (){
        val formData = listOf(
            "function" to "getTradeSubCategory",
            "brimsCode" to parentBrimsCode
        )
        executeRequest(formData, trade,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    runOnUiThread {
                        arrayList5.clear()
                        for(data in response.data.tradeSubCategories){
                            arrayList5.add(data.businessActivityDescription)
                        }

                        //Spinner
                        val adapters = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item,arrayList5)
                        adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        spinnerBusinessActivityDescription.adapter = adapters
                        spinnerBusinessActivityDescription.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {
                                brimsCode =  response.data.tradeSubCategories[postion].brimsCode
                                businessActivityDescription = response.data.tradeSubCategories[postion].businessActivityDescription
                                sbpFee = response.data.tradeSubCategories[postion].sbpFee
                                registrationFee = response.data.tradeSubCategories[postion].registrationFee

                                tvAmount.text = "KES $sbpFee"

                            }
                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }
                        }

                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@AddBusiness,response.message, Toast.LENGTH_LONG).show()}

                }

            }

        })
    }

    private fun addBusiness (){
        runOnUiThread {tv_message.text ="Please wait..generating bill.." }
        val formData = listOf(
            "function" to "addBusiness2",
            "businessName" to edbusinessName.text.toString(),
            "certificateOfIncorporation" to edCertificateOfIncorporation.text.toString(),
            "pinNumber" to edPin.text.toString(),
            "VATNumber" to edVatNumber.text.toString(),
            "POBox" to edBox.text.toString(),
            "postalCode" to edPostal.text.toString(),
            "plotNo" to edPlotNumber.text.toString(),
            "town" to edPostalTown.text.toString(),
            "telephone1" to edPhoneNumber.text.toString() ,
            "telephone2" to edPhoneNumber2.text.toString(),
            "countyID" to "36",
            "subCountyID" to subCountyID ,
            "wardID" to wardID,
            "address" to edEmail.text.toString(),
            "createdBy" to "null",
            "physicalAddress" to edPhysicalAddress.text.toString() ,
            "businessActivity" to businessActivity,
            "businessActivityDescription" to businessActivityDescription,
            "brimsCode" to brimsCode,
            "registrationFee" to registrationFee,
            "sbpFee" to sbpFee,
            "email" to edEmail.text.toString(),
        )
        executeRequest(formData, trade,object : CallBack {
            override fun onSuccess(result: String?) {

                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)

                if(response.success){
                    runOnUiThread {tv_message.text ="Bill generated success.." }

                    customerPayBillOnline(
                        response.data.billDetails.billNo,
                       "4087493",
                        response.data.billDetails.detailAmount
                    )

                }
                else{
                    runOnUiThread {tv_message.text =response.message }

                }
            }

        })
    }
    fun alert(title: String, message: String){
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle(title)
        //set message for alert dialog
        builder.setMessage(message)
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Close"){dialogInterface, which ->
          finish()
        }
        //performing cancel action
        builder.setNeutralButton("Cancel"){dialogInterface , which ->
           finish()
        }
        //performing negative action
        /*  builder.setNegativeButton("No"){dialogInterface, which ->
              Toast.makeText(applicationContext,"clicked No",Toast.LENGTH_LONG).show()
          }*/
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
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
            "phoneNumber" to edPhoneNumber.text.toString(),
            "token" to "im05WXYH2rwRruPjCICieOs8m4E8IoltnDEhyPUv6bnB9cU60gD48SnJPC6oh7EpsPaAUGC8wqIdtVVjGlWLxqFssshxMHxHjEQJ"
        )
        executeRequest(formData, paysol,object : CallBack {
            override fun onSuccess(result: String?) {
                //  runOnUiThread {  progress_circular.visibility = View.GONE }
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    runOnUiThread {
                        // progressBar1.visibility = View.GONE
                        checkPayment(accountReference)
                    }

                }else{
                    runOnUiThread { tv_message.text =response.message }
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
        executeRequest(formData, paysol,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)

                if(response.success){

                    if(response.data.push.callback_returned=="PAID"){

                        runOnUiThread {

                            tv_message.text ="Payment Received #${response.data.push.transaction_code} KES ${response.data.push.amount}"

                            /*
                            transactionCode.text = response.data.push.transaction_code
                            tvAmount.text = "KES "+response.data.push.amount
                            tvRef.text = response.data.push.ref
                            tvStatus.text = response.data.push.callback_returned; */
                            alert("Payment Received","${response.data.push.transaction_code} KES ${response.data.push.amount}")

                        }


                    }else if(response.data.push.callback_returned=="PENDING"){
                        runOnUiThread { tv_message.text ="Waiting for payment.." }
                        TimeUnit.SECONDS.sleep(2L)
                        checkPayment(accountReference)
                    }else{
                        runOnUiThread { tv_message.text = response.data.push.message}
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