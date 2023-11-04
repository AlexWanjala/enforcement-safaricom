package com.aw.forcement.sbp.application

import AdapterFeeAndCharges
import Business
import Const
import FeesAndCharges
import Json4Kotlin_Base
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.aw.forcement.R
import com.aw.forcement.tabs.Home
import com.aw.passanger.api.*
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.android.synthetic.main.activity_billing_information.*
import kotlinx.android.synthetic.main.activity_billing_information.btn_previous
import kotlinx.android.synthetic.main.activity_billing_information.edPhone
import kotlinx.android.synthetic.main.activity_billing_information.tvAmount
import kotlinx.android.synthetic.main.activity_billing_information.tvSendPushDisabled
import kotlinx.android.synthetic.main.activity_billing_information.tv_message
import kotlinx.android.synthetic.main.message_box.view.*
import kotlinx.android.synthetic.main.payment_recieved.view.*
import kotlinx.android.synthetic.main.payment_unsuccesfull.view.*
import kotlinx.android.synthetic.main.recycler_view.*
import java.util.concurrent.TimeUnit

class BillingInformation : AppCompatActivity() {

    lateinit var messageBoxView : View
    lateinit var messageBoxInstance: androidx.appcompat.app.AlertDialog // Declare as AlertDialog

    lateinit var messageBoxViewFailed : View
    lateinit var messageBoxInstanceFailed: androidx.appcompat.app.AlertDialog // Declare as AlertDialog

    lateinit var messageBoxViewInfo : View
    lateinit var messageBoxInstanceInfo: androidx.appcompat.app.AlertDialog

    lateinit var messageBoxViewPaid : View
    lateinit var messageBoxInstancePaid: androidx.appcompat.app.AlertDialog // Declare as AlertDialog
    var businessID =""
    var description =""
    var payNow =""
    var totalAmount = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_billing_information)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Initialize messageBoxView here
        messageBoxView = LayoutInflater.from(this).inflate(R.layout.message_box, null)
        messageBoxViewFailed = LayoutInflater.from(this).inflate(R.layout.payment_unsuccesfull, null)
        messageBoxViewPaid = LayoutInflater.from(this).inflate(R.layout.payment_recieved, null)
        messageBoxViewInfo = LayoutInflater.from(this).inflate(R.layout.send_later, null)

        closeBottom.setOnClickListener {
            finishAffinity()
            startActivity(Intent(this,Home::class.java))
        }

        tv_business_name.text = getValue(this,"business_name")
        tv_business_category.text = getValue(this,"business_category")
        tv_business_sub_category.text = getValue(this,"business_sub_category")
        tv_owners_id.text = getValue(this,"owner_id")
        tv_business_phone.text = getValue(this,"business_phone")
        tv_business_email.text = getValue(this,"business_email")


        tv_proceed.setOnClickListener {
            if(payNow==""){
                Toast.makeText(this,"Select Pay Now or Later",Toast.LENGTH_LONG).show()
            }else{
                registerBusiness()
            }

        }
        btn_previous.setOnClickListener { finish() }
        radio_now.setOnClickListener {
            tv_phone_mpesa.text ="Mpesa Phone Number"
            payNow ="true"
        }
        radio_later.setOnClickListener {
            tv_phone_mpesa.text ="Mpesa Phone Number to Pay Later"
            payNow ="false"
        }

        btn_finish.setOnClickListener {
            finishAffinity()
            startActivity(Intent(this,Home::class.java))
        }


        loadSelectedFeeAndCharges()
    }

     fun loadSelectedFeeAndCharges(){
         totalAmount =0.0
        runOnUiThread {
            val adapter = AdapterFeeAndCharges(this@BillingInformation, Const.instance.getSelectedFeesAndCharges())
            adapter.notifyDataSetChanged()
            recyclerView.layoutManager = LinearLayoutManager(this@BillingInformation)
            recyclerView.adapter = adapter
            recyclerView.setHasFixedSize(false)
        }

         val selectedFeesAndCharges = Const.instance.getSelectedFeesAndCharges()

        // Loop through the selected fees and charges
         for (feeAndCharge in selectedFeesAndCharges) {
        // Parse the unitFeeAmount as a double and add it to the total amount
             totalAmount += feeAndCharge.unitFeeAmount.toDouble()
         }

         tvAmount.text ="KES "+ totalAmount
    }

    private fun getJsonData(): String {
        val updatedBusiness = Business(
            id = "",
            businessID = "",
            businessName = getValue(this, "business_name").toString(),
            subCountyID = getValue(this, "subCountyID").toString(),
            subCountyName =  getValue(this, "subCountyName").toString(),
            wardID = getValue(this, "wardID").toString(),
            wardName = getValue(this, "wardName").toString(),
            plotNumber = getValue(this, "plotNumber").toString(),
            physicalAddress = getValue(this, "physicalAddress").toString(),
            buildingName = getValue(this, "buildingName").toString(),
            buildingOccupancy = getValue(this, "buildingOccupancy").toString(),
            floorNo = getValue(this, "floorNo").toString(),
            roomNo = getValue(this, "room_no").toString(),
            premiseSize = getValue(this, "premise_size").toString(),
            numberOfEmployees = getValue(this, "number_of_employees").toString(),
            tonnage = getValue(this, "tonnage").toString(),
            businessDes = getValue(this, "business_des").toString(),
            businessCategory = getValue(this, "business_category").toString(),
            businessSubCategory = getValue(this, "business_sub_category").toString(),
            incomeTypeID = getValue(this, "incomeTypeID").toString(),
            feeID = getValue(this, "feeID").toString(),
            businessEmail = getValue(this, "business_email").toString(),
            postalAddress = getValue(this, "postal_address").toString(),
            postalCode = getValue(this, "postal_code").toString(),
            businessPhone = getValue(this, "business_phone").toString(),
            contactPersonNames = getValue(this, "contact_person_names").toString(),
            contactPersonIDNo = getValue(this, "contact_person_idNo").toString(),
            businessRole = getValue(this, "business_role").toString(),
            contactPersonPhone = getValue(this, "contact_person_phone").toString(),
            contactPersonEmail = getValue(this, "contact_person_email").toString(),
            fullNames = getValue(this, "full_names").toString(),
            ownerID = getValue(this, "owner_id").toString(),
            ownerPhone = getValue(this, "owner_phone").toString(),
            ownerEmail = getValue(this, "owner_email").toString(),
            kraPin = getValue(this, "kra_pin").toString(),
            createdBy = getValue(this, "username").toString(),
            createdByIDNo = getValue(this, "idNo").toString(),
            dateCreated = "",
            lat = getValue(this, "lat").toString(),
            lng = getValue(this, "lng").toString(),
            conservancy = getValue(this, "conservancy").toString(),
            fireLicence = "",
            liquorLicence = "",
            businessType = "",
        )


        Const.instance.setBusiness(updatedBusiness)
        val gson = Gson()
        return gson.toJson(Const.instance.getBusiness())
    }

    private fun registerBusiness(){
        (messageBoxView as View?)!!.tv_message.text ="Processing Application"
        showMessageBox()

        val formData = listOf(
            "function" to "registerBusiness",
            "business" to getJsonData(),

            )
        executeRequest(formData, trade,object : CallBack {
            override fun onSuccess(result: String?) {

                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    businessID = response.data.business.businessID.toString()
                    description = response.data.business.buildingName.toString()

                    Const.instance.setFeesAndCharges(response.data.feesAndCharges)

                    runOnUiThread {
                        showMessageBox()
                        generateBill()
                    }

                }else{ }

            }

        })
    }

    private fun getBillItem(): List<FeesAndCharges> {

        Const.instance.getSelectedFeesAndCharges().forEach {

            if (it.feeId == getValue(this, "feeID").toString()) {
                it.customer = "Business ID: $businessID"

            }else{
                it.customer = ""
            }

            it.zone = getValue(this@BillingInformation,"zone").toString()
            it.amount = it.unitFeeAmount
            it.subCountyName =  getValue(this, "subCountyName").toString()
            it.subCountyID = getValue(this, "subCountyID").toString()
            it.wardID = getValue(this, "wardID").toString()
            it.wardName =  getValue(this, "wardName").toString()
            it.idNo = getValue(this@BillingInformation,"idNo").toString()
            it.phoneNumber = getValue(this@BillingInformation,"phoneNumber").toString()
            it.names = getValue(this@BillingInformation,"names").toString()
            it.customerPhoneNumber =  getValue(this, "owner_phone").toString()
            it.description = getValue(this, "business_name").toString()
            it.fiscalYear = getValue(this, "fiscalYear").toString()
        }

        return Const.instance.getSelectedFeesAndCharges()
    }

    private fun generateBill (){

        tv_message.text ="Generating bill please wait.."
        val formData = listOf(
            "function" to "generateBill3",
            "billItem" to  Gson().toJson(getBillItem()),
            "payNow" to payNow,
            "customerPhoneNumber" to edPhone.text.toString(),
        )
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                    if(response.success){
                        runOnUiThread {

                            tv_message.text ="Bill generated success.."
                            tv_proceed.visibility = View.GONE
                            tvSendPushDisabled.visibility = View.VISIBLE

                            if (payNow=="true"){

                                customerPayBillOnline(
                                    response.data.billGenerated.billNo,
                                    response.data.billGenerated.payBillNo,
                                    response.data.billGenerated.amount)
                            }else{
                                messageBoxInstance.dismiss()
                                showMessageBoxPaymentInfo()
                            }
                        }

                    }else{
                        Toast.makeText(this@BillingInformation,response.message, Toast.LENGTH_LONG).show()
                    }


                }else{
                    runOnUiThread {  Toast.makeText(this@BillingInformation,response.message, Toast.LENGTH_LONG).show()}

                }

            }

        })
    }


    private fun customerPayBillOnline(accountReference: String, payBillNumber: String, amount: String){
        // progressBar1.visibility = View.VISIBLE
        runOnUiThread {
            tv_message.text ="Sending Payment Request.."
            (messageBoxView as View?)!!.tv_message.text ="Sending Payment Request.."

        }
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
                        tv_proceed.visibility = View.VISIBLE
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

                            messageBoxInstance.dismiss()

                            tv_proceed.visibility = View.VISIBLE
                            tvSendPushDisabled.visibility = View.GONE
                            tv_message.text ="Payment Received #${response.data.push.transaction_code} KES ${response.data.push.amount}"

                            //v_transaction: String,payer: String,amount: String, des: String,category:String
                            showMessageBoxPayment(
                                response.data.transaction.transaction_code,
                                response.data.transaction.names,
                                response.data.transaction.amount,
                                "${description} ${edPhone.text}",
                                businessID
                            )
                        }


                    }else if(response.data.push.callback_returned=="PENDING"){
                        runOnUiThread {
                            tv_message.text ="Waiting for payment.."
                            (messageBoxView as View?)!!.tv_message.text ="Waiting for payment.."

                        }
                        TimeUnit.SECONDS.sleep(2L)
                        checkPayment(accountReference)
                    }else{
                        runOnUiThread {
                            messageBoxInstance.dismiss()
                            tv_message.text = response.data.push.message
                            tv_proceed.visibility = View.VISIBLE
                            tvSendPushDisabled.visibility = View.GONE
                            showMessageBoxPaymentFail(response.data.push.message)

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
    private fun showMessageBox(){
        // Check if messageBoxView has a parent
        if (messageBoxView.parent != null) {
            // Remove messageBoxView from its parent
            (messageBoxView.parent as ViewGroup).removeView(messageBoxView)
        }
        val messageBoxBuilder = androidx.appcompat.app.AlertDialog.Builder(this).setView(messageBoxView as View?)
        messageBoxInstance = messageBoxBuilder.show()
    }
    private fun showMessageBoxPayment(transaction: String,payer: String,amount: String, des: String,category:String){

        // Check if messageBoxView has a parent
        if (messageBoxViewPaid.parent != null) {
            // Remove messageBoxView from its parent
            (messageBoxViewPaid.parent as ViewGroup).removeView(messageBoxViewPaid)
        }

        val messageBoxBuilder = androidx.appcompat.app.AlertDialog.Builder(this).setView(
            messageBoxViewPaid as View?
        )
        messageBoxInstancePaid = messageBoxBuilder.show()

        messageBoxViewPaid.tv_transaction.text = transaction
        messageBoxViewPaid.tv_payer.text = payer
        messageBoxViewPaid.tv_amount.text = amount
        messageBoxViewPaid.tv_des.text = des
        messageBoxViewPaid.tv_category.text = category
        messageBoxViewPaid.okay.setOnClickListener {
            messageBoxInstancePaid.dismiss()

            finishAffinity()
            startActivity(Intent(this,Businesses::class.java))

        }

    }
    private fun showMessageBoxPaymentFail(message: String) {

        // Check if messageBoxView has a parent
        if (messageBoxViewFailed.parent != null) {
            // Remove messageBoxView from its parent
            (messageBoxViewFailed.parent as ViewGroup).removeView(messageBoxViewFailed)
        }

        val messageBoxBuilder = androidx.appcompat.app.AlertDialog.Builder(this).setView(
            messageBoxViewFailed as View?
        )
        messageBoxInstanceFailed = messageBoxBuilder.show()

        if (message.contains("invalid")) {
            (messageBoxViewFailed as View?)!!.tv_title.text = "Wrong PIN"
            (messageBoxViewFailed as View?)!!.tv_message_unpaid.text = "The Payer typed an incorrect pin. Send the request again to retry."
            (messageBoxViewFailed as View?)?.imageIcon?.setImageResource(R.drawable.wrong_pin)
        } else
            if (message.contains("insufficient")) {
                (messageBoxViewFailed as View?)!!.tv_title.text = "Insufficient Funds"
                (messageBoxViewFailed as View?)!!.tv_message_unpaid.text =
                    "Payer needs more money. Tell them to top up MPESA to pay."
                (messageBoxViewFailed as View?)?.imageIcon?.setImageResource(R.drawable.insufficient)
            } else
                if (message.contains("cancel")) {
                    (messageBoxViewFailed as View?)!!.tv_title.text = "Request Canceled !"
                    (messageBoxViewFailed as View?)!!.tv_message_unpaid.text =
                        "Payer canceled payment. Click Resend request to try again."
                    (messageBoxViewFailed as View?)?.imageIcon?.setImageResource(R.drawable.explamation)
                } else
                    if (message.contains("timeout")) {
                        (messageBoxViewFailed as View?)!!.tv_title.text = "Phone unreachable"
                        (messageBoxViewFailed as View?)!!.tv_message_unpaid.text =
                            "Phone unreachable. Ask payer to switch On their phone"
                        (messageBoxViewFailed as View?)?.imageIcon?.setImageResource(R.drawable.phone_unreachable)
                    } else {
                        (messageBoxViewFailed as View?)!!.tv_title.text = "No Payment"
                        (messageBoxViewFailed as View?)!!.tv_message_unpaid.text = message
                        (messageBoxViewFailed as View?)?.imageIcon?.setImageResource(R.drawable.explamation)
                    }

        (messageBoxViewFailed as View?)!!.tv_close.setOnClickListener { messageBoxInstanceFailed.dismiss()  }
        (messageBoxViewFailed as View?)!!.resend.setOnClickListener {
            messageBoxInstanceFailed.dismiss()
           runOnUiThread { generateBill() }
        }

    }
    private fun showMessageBoxPaymentInfo(){

        // Check if messageBoxView has a parent
        if (messageBoxViewInfo.parent != null) {
            // Remove messageBoxView from its parent
            (messageBoxViewInfo.parent as ViewGroup).removeView(messageBoxViewInfo)
        }

        val messageBoxBuilder = androidx.appcompat.app.AlertDialog.Builder(this).setView(
            messageBoxViewInfo as View?
        )
        messageBoxInstanceInfo = messageBoxBuilder.show()
        messageBoxInstanceInfo.setCanceledOnTouchOutside (false)

        messageBoxViewInfo.okay.setOnClickListener {
            messageBoxInstanceInfo.dismiss()
            finishAffinity()
            startActivity(Intent(this,Businesses::class.java))
        }

    }
}