package com.aw.forcement.fire.applications

import AdapterBillInfo
import AdapterFeeAndCharges
import AdapterFeeAndChargesList
import AdapterFireSafetySelected
import Const
import Json4Kotlin_Base
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.aw.forcement.R
import com.aw.forcement.tabs.Home
import com.aw.passanger.api.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_application_detail_fire.*
import kotlinx.android.synthetic.main.activity_application_detail_fire.btn_decline
import kotlinx.android.synthetic.main.activity_application_detail_fire.btn_submit
import kotlinx.android.synthetic.main.activity_business_details2.*
import kotlinx.android.synthetic.main.activity_business_details2.btn_next
import kotlinx.android.synthetic.main.activity_business_details2.btn_previous
import kotlinx.android.synthetic.main.activity_business_details2.closeBottom
import kotlinx.android.synthetic.main.activity_business_details2.tv_business_ID
import kotlinx.android.synthetic.main.activity_business_details2.tv_business_activity
import kotlinx.android.synthetic.main.activity_business_details2.tv_business_category
import kotlinx.android.synthetic.main.activity_business_details2.tv_business_name
import kotlinx.android.synthetic.main.activity_business_details2.tv_business_sub_category
import kotlinx.android.synthetic.main.activity_business_details2.tv_sub_county
import kotlinx.android.synthetic.main.activity_business_details2.tv_ward
import kotlinx.android.synthetic.main.message_box.view.*
import kotlinx.android.synthetic.main.recycler_view.*
import kotlinx.android.synthetic.main.succesfull.view.*


class BusinessDetailsApplication : AppCompatActivity() {

    lateinit var messageBoxView : View
    lateinit var messageBoxInstance: androidx.appcompat.app.AlertDialog // Declare as AlertDialog

    lateinit var messageBoxViewResponse : View
    lateinit var messageBoxInstanceResponse: androidx.appcompat.app.AlertDialog // Declare as AlertDialog


    var approvedDeclined =""
    var stageStatus =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_application_detail_fire)
        val businessID = intent.getStringExtra("businessID")
        val billNo = intent.getStringExtra("billNo")

        messageBoxView = LayoutInflater.from(this).inflate(R.layout.message_box, null)
        messageBoxViewResponse = LayoutInflater.from(this).inflate(R.layout.succesfull, null)


        btn_previous.setOnClickListener {
            finish()
        }

        closeBottom.setOnClickListener { finish() }

        if (businessID != null) {
            getBusiness(businessID)
        }
        if (billNo != null) {
            getBill(billNo)
        }


        btn_decline.setOnClickListener {
            approvedDeclined ="Declined | "
            stageStatus ="Declined"
            submit()
        }

        btn_submit.setOnClickListener {
            approvedDeclined =""
            stageStatus ="Approved"
            submit()
        }


    }

    private fun submit(){
        (messageBoxView as View?)!!.tv_message.text ="Processing Application"
        showMessageBox()

        val formData = listOf(
            "function" to "fireValidation",
            "description" to approvedDeclined,
            "comments" to edComments.text.toString(),
            "billNo" to  intent.getStringExtra("billNo").toString(),
            "idNo" to getValue(this,"idNo").toString(),
            "names" to getValue(this,"username").toString(),
            "phoneNumber" to getValue(this,"phoneNumber").toString(),
            "stageStatus" to stageStatus.toString(),
            "deviceId" to getDeviceIdNumber(this).toString()

        )
        executeRequest(formData, fire,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    runOnUiThread {

                        (messageBoxView as View?)!!.tv_message.text ="Submitted"
                        messageBoxInstance.dismiss()
                        showMessageResponse()
                    }

                }
                else{
                    runOnUiThread {
                        Toast.makeText(this@BusinessDetailsApplication, response.message,Toast.LENGTH_LONG).show()
                    }
                }

            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@BusinessDetailsApplication,result, Toast.LENGTH_LONG).show()
                }
            }

        })


    }
    private fun getBusiness(keyword: String){

        val formData = listOf(
            "function" to "getBusiness",
            "businessID" to keyword,
        )
        executeRequest(formData, trade,object : CallBack {
            override fun onSuccess(result: String?) {

                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)

                if(response.success){

                    runOnUiThread {

                        tv_business_name.text = response.data.business.businessName
                        tv_business_category.text = response.data.business.businessCategory
                        tv_business_sub_category.text =response.data.business.businessSubCategory
                        tv_business_activity.text =response.data.business.businessDes
                        tv_sub_county.text = response.data.business.subCountyName
                        tv_ward.text = response.data.business.wardName
                        tv_business_ID.text = response.data.business.businessID

                    }


                }
                else{
                    runOnUiThread {

                        Toast.makeText(this@BusinessDetailsApplication,response.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
            override fun onFailure(result: String?) {
                runOnUiThread {

                    Toast.makeText(this@BusinessDetailsApplication,result, Toast.LENGTH_LONG).show()
                }
            }

        })
    }
    private fun getBill(billNo: String){

        val formData = listOf(
            "function" to "getBill",
            "billNo" to billNo,
        )
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {

                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)

                if(response.success){

                    runOnUiThread {
                        val adapter = AdapterBillInfo(this@BusinessDetailsApplication, response.data.billInfo)
                        adapter.notifyDataSetChanged()
                        recyclerView.layoutManager = LinearLayoutManager(this@BusinessDetailsApplication)
                        recyclerView.adapter = adapter
                        recyclerView.setHasFixedSize(false)


                       // Remove square brackets and split the string by comma [General House Keeping is good, Electrical Installation Done To Recommended , , Recommendations: ]
                        val desItems = response.data.billDetails.description.replace("[\\[\\]]".toRegex(), "").split(",")

                        val selectedFireSafety = mutableListOf<String>()
                        for (item in desItems) {
                            val trimmedItem = item.trim()
                            if (trimmedItem.isNotEmpty()) {
                                selectedFireSafety.add(trimmedItem)
                            }
                        }

                        val adapter2 = AdapterFireSafetySelected(this@BusinessDetailsApplication,selectedFireSafety)
                        adapter2.notifyDataSetChanged()
                        recyclerView2.layoutManager = LinearLayoutManager(this@BusinessDetailsApplication)
                        recyclerView2.adapter = adapter2
                        recyclerView2.setHasFixedSize(false)

                    }


                }
                else{
                    runOnUiThread {

                        Toast.makeText(this@BusinessDetailsApplication,response.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
            override fun onFailure(result: String?) {
                runOnUiThread {

                    Toast.makeText(this@BusinessDetailsApplication,result, Toast.LENGTH_LONG).show()
                }
            }

        })
    }

    //alerts
    private fun showMessageBox(){
        // Check if messageBoxView has a parent
        if (messageBoxView.parent != null) {
            // Remove messageBoxView from its parent
            (messageBoxView.parent as ViewGroup).removeView(messageBoxView)
        }
        val messageBoxBuilder = androidx.appcompat.app.AlertDialog.Builder(this).setView(messageBoxView as View?)
        messageBoxBuilder.setCancelable (false)
        messageBoxInstance = messageBoxBuilder.show()

    }
    private fun showMessageResponse(){


        // Check if messageBoxView has a parent
        if (messageBoxViewResponse.parent != null) {
            // Remove messageBoxView from its parent
            (messageBoxViewResponse.parent as ViewGroup).removeView(messageBoxViewResponse)
        }

        val messageBoxBuilder = androidx.appcompat.app.AlertDialog.Builder(this).setView(
            messageBoxViewResponse as View?
        )
        messageBoxBuilder.setCancelable (false)
        messageBoxInstanceResponse = messageBoxBuilder.show()

        messageBoxViewResponse.okay.setOnClickListener {
            messageBoxInstanceResponse.dismiss()
            finishAffinity()
            startActivity(Intent(this, Home::class.java))
        }

    }
}