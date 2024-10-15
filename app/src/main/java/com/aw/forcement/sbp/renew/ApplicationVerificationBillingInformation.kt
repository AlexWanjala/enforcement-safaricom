package com.aw.forcement.sbp.renew

import AdapterFeeAndCharges
import Const
import FeesAndCharges
import Json4Kotlin_Base
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.aw.forcement.InvoiceDetials
import com.aw.forcement.R
import com.aw.forcement.sbp.application.Businesses
import com.aw.forcement.tabs.Home
import com.aw.passanger.api.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_application_verification_billing_information_renewal.*
import kotlinx.android.synthetic.main.activity_application_verification_billing_information_renewal.edPhone
import kotlinx.android.synthetic.main.activity_application_verification_billing_information_renewal.print_invoice
import kotlinx.android.synthetic.main.activity_application_verification_billing_information_renewal.tvAmount
import kotlinx.android.synthetic.main.activity_application_verification_billing_information_renewal.tvSendPushDisabled
import kotlinx.android.synthetic.main.activity_application_verification_billing_information_renewal.tv_message
import kotlinx.android.synthetic.main.activity_application_verification_billing_information_renewal.tv_proceed
import kotlinx.android.synthetic.main.message_box.view.*
import kotlinx.android.synthetic.main.payment_recieved.view.*
import kotlinx.android.synthetic.main.payment_unsuccesfull.view.*
import kotlinx.android.synthetic.main.recycler_view.*
import kotlinx.android.synthetic.main.send_later.*
import kotlinx.android.synthetic.main.send_later.view.*


import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class ApplicationVerificationBillingInformation : AppCompatActivity() {


     lateinit var messageBoxView : View
     lateinit var messageBoxInstance: androidx.appcompat.app.AlertDialog // Declare as AlertDialog

    lateinit var messageBoxViewResponse : View
    lateinit var messageBoxInstanceResponse: androidx.appcompat.app.AlertDialog // Declare as AlertDialog

     var approvedDeclined =""
    var stageStatus =""


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
    var billNo =""
    var penalized = "false"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_application_verification_billing_information_renewal)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        messageBoxViewFailed = LayoutInflater.from(this).inflate(R.layout.payment_unsuccesfull, null)
        messageBoxViewPaid = LayoutInflater.from(this).inflate(R.layout.payment_recieved, null)
        messageBoxViewInfo = LayoutInflater.from(this).inflate(R.layout.send_later, null)


        messageBoxView = LayoutInflater.from(this).inflate(R.layout.message_box, null)
        messageBoxViewResponse = LayoutInflater.from(this).inflate(R.layout.succesfull, null)

        val originalDate = LocalDateTime.parse(Const.instance.getBill().billDetails.dateCreated.split(".")[0], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        val formattedDate = originalDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm a"))


        tv_bill_no.text = Const.instance.getReceipt().receiptDetails.billNo
        tv_date_created.text = formattedDate
        tv_payment_status.text = Const.instance.getReceipt().receiptDetails.status
        tv_receipt_no.text = Const.instance.getReceipt().receiptDetails.transactionCode
        tv_payment_mode.text = Const.instance.getReceipt().receiptDetails.source
        tv_ref.text = Const.instance.getReceipt().receiptDetails.billNo
        tv_ref.text = Const.instance.getReceipt().receiptDetails.billNo
        tv_bill_created_by.text = Const.instance.getReceipt().receiptDetails.names
        tv_paid_by.text = Const.instance.getReceipt().receiptDetails.paidBy

        val original = LocalDateTime.parse(Const.instance.getReceipt().receiptDetails.dateCreated.split(".")[0], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        val formatted = original.format(DateTimeFormatter.ofPattern("dd MMM yyyy hh:mm a"))

        tv_date_paid.text =formatted
        tv_billed_amount.text = "KES "+ Const.instance.getBill().billDetails.detailAmount
        tv_receipt_amount.text = "KES " + Const.instance.getBill().billDetails.receiptAmount
        tv_validated_amount.text = "KES " + Const.instance.getEntries().billTotal

        tv_remaining_amount.text = "KES "+(Const.instance.getEntries().billTotal.toInt() - Const.instance.getBill().billDetails.receiptAmount.toInt()).toString()

        val changes = getTotalNumberOfChanges()
        var changesMessage =""
        val businessSubCategoryMessage = getBusinessSubCategoryComparison()
        val actualChangesMade = getActualChangesMade()

        if(changes == 0){
            if(businessSubCategoryMessage==""){
                changesMessage ="No Changes done. $businessSubCategoryMessage"
                val color = Color.parseColor("#8DC6D2DD")
                layout.backgroundTintList = ColorStateList.valueOf(color)
                tv_message.setTextColor(resources.getColor(R.color.black))
            }else{
                changesMessage ="$businessSubCategoryMessage"
                val color = Color.parseColor("#F35611")
                layout.backgroundTintList = ColorStateList.valueOf(color)
                tv_message.setTextColor(Color.parseColor("#CB2020"))

            }


        }
        else{
             changesMessage ="Changes made to $actualChangesMade. $businessSubCategoryMessage"
            // Create a color object from the hex string
            val color = Color.parseColor("#F35611")
            layout.backgroundTintList = ColorStateList.valueOf(color)
            tv_message.setTextColor(Color.parseColor("#CB2020"))
        }
        tv_message.text = changesMessage


        tv_proceed.setOnClickListener {
            approvedDeclined =""
            stageStatus ="Approved"
            submit()
        }

        radio_now.setOnClickListener {
            tv_phone_mpesa.text ="Mpesa Phone Number"
            payNow ="true"
        }
        radio_later.setOnClickListener {
            tv_phone_mpesa.text ="Mpesa Phone Number to Pay Later"
            payNow ="false"
        }

        loadSelectedFeeAndCharges()

    }


    fun loadSelectedFeeAndCharges(){

        totalAmount =0.0
        runOnUiThread {
            val adapter = AdapterFeeAndCharges(this@ApplicationVerificationBillingInformation, Const.instance.getSelectedFeesAndCharges())
            adapter.notifyDataSetChanged()
            recyclerView.layoutManager = LinearLayoutManager(this@ApplicationVerificationBillingInformation)
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

    private fun submit(){
        (messageBoxView as View?)!!.tv_message.text ="Processing Application"
        showMessageBox()

        val gson = Gson()
        val json = gson.toJson(Const.instance.getBusiness())


        val formData = listOf(
            "function" to "businessValidationRenew",
            "business" to json,
            "description" to approvedDeclined+""+tv_message.text.toString(),
            "comments" to "",
            "billNo" to  Const.instance.getBill().billDetails.billNo,
            "statusID" to (Const.instance.getEntries().statusID.toInt() + 1).toString(),
            "idNo" to getValue(this,"idNo"),
            "names" to getValue(this,"username"),
            "phoneNumber" to getValue(this,"phoneNumber").toString(),
            "balanceAmount" to tv_remaining_amount.text.toString().replace("KES","").replace(" ",""),
            "stageStatus" to stageStatus,
            "deviceId" to getDeviceIdNumber(this),
            "sessionNames" to getValue(this,"username").toString(),
            "sessionIdNo" to getValue(this,"idNo").toString()

        )
        executeRequest(formData, trade,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){
                    runOnUiThread {

                        (messageBoxView as View?)!!.tv_message.text ="Submitted"
                        messageBoxInstance.dismiss()

                        businessID = response.data.business.businessID.toString()
                        description = response.data.business.buildingName.toString()

                        Const.instance.setFeesAndCharges(response.data.feesAndCharges)

                        runOnUiThread {
                            showMessageBox()
                            generateBill()
                        }
                    }
                }
                else{
                    runOnUiThread {
                      Toast.makeText(this@ApplicationVerificationBillingInformation, response.message,Toast.LENGTH_LONG).show()
                    }
                }

            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@ApplicationVerificationBillingInformation,result, Toast.LENGTH_LONG).show()
                }
            }

        })


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

    private fun getTotalNumberOfChanges(): Int {
        // Get the original and updated business objects from the Const singleton class
        val originalBusiness = Const.instance.getOriginalBusiness()
        val updatedBusiness = Const.instance.getBusiness()

       // Declare a variable to store the total number of changes made
        var changes = 0

       // Use the equals function to compare the properties of the business objects
        if (originalBusiness.id != updatedBusiness?.id) changes++
        if (originalBusiness.businessID != updatedBusiness?.businessID) changes++
        if (originalBusiness.businessName != updatedBusiness?.businessName) changes++
        // ... other properties
        // Print the result
        println("The total number of changes made is $changes")

        return changes
    }
    private fun getActualChangesMade(): String {
        // Get the original and updated business objects from the Const singleton class
        val originalBusiness = Const.instance.getOriginalBusiness()
        val updatedBusiness = Const.instance.getBusiness()

        val changedProperties = mutableListOf<Pair<String, String>>()

        if (originalBusiness.id != updatedBusiness?.id) changedProperties.add(Pair("id", "${originalBusiness.id} -> ${updatedBusiness?.id}"))
        if (originalBusiness.businessID != updatedBusiness?.businessID) changedProperties.add(Pair("businessID", "${originalBusiness.businessID} -> ${updatedBusiness?.businessID}"))
        if (originalBusiness.businessName != updatedBusiness?.businessName) changedProperties.add(Pair("businessName", "${originalBusiness.businessName} -> ${updatedBusiness?.businessName}"))
        if (originalBusiness.subCountyID != updatedBusiness?.subCountyID) changedProperties.add(Pair("subCountyID", "${originalBusiness.subCountyID} -> ${updatedBusiness?.subCountyID}"))
        if (originalBusiness.subCountyName != updatedBusiness?.subCountyName) changedProperties.add(Pair("businessName", "${originalBusiness.subCountyName} -> ${updatedBusiness?.subCountyName}"))
        if (originalBusiness.wardName != updatedBusiness?.wardName) changedProperties.add(Pair("wardName", "${originalBusiness.wardName} -> ${updatedBusiness?.wardName}"))
        if (originalBusiness.plotNumber != updatedBusiness?.plotNumber) changedProperties.add(Pair("plotNumber", "${originalBusiness.plotNumber} -> ${updatedBusiness?.plotNumber}"))
        if (originalBusiness.physicalAddress != updatedBusiness?.physicalAddress) changedProperties.add(Pair("physicalAddress", "${originalBusiness.physicalAddress} -> ${updatedBusiness?.physicalAddress}"))
        if (originalBusiness.buildingName != updatedBusiness?.buildingName) changedProperties.add(Pair("buildingName", "${originalBusiness.buildingName} -> ${updatedBusiness?.buildingName}"))
        if (originalBusiness.buildingOccupancy != updatedBusiness?.buildingOccupancy) changedProperties.add(Pair("buildingOccupancy", "${originalBusiness.buildingOccupancy} -> ${updatedBusiness?.buildingOccupancy}"))
        if (originalBusiness.floorNo != updatedBusiness?.floorNo) changedProperties.add(Pair("floorNo", "${originalBusiness.floorNo} -> ${updatedBusiness?.floorNo}"))
        if (originalBusiness.roomNo != updatedBusiness?.roomNo) changedProperties.add(Pair("roomNo", "${originalBusiness.roomNo} -> ${updatedBusiness?.roomNo}"))
        if (originalBusiness.premiseSize != updatedBusiness?.premiseSize) changedProperties.add(Pair("premiseSize", "${originalBusiness.premiseSize} -> ${updatedBusiness?.premiseSize}"))
        if (originalBusiness.numberOfEmployees != updatedBusiness?.numberOfEmployees) changedProperties.add(Pair("numberOfEmployees", "${originalBusiness.numberOfEmployees} -> ${updatedBusiness?.numberOfEmployees}"))
        if (originalBusiness.tonnage != updatedBusiness?.tonnage) changedProperties.add(Pair("tonnage", "${originalBusiness.tonnage} -> ${updatedBusiness?.tonnage}"))
        if (originalBusiness.businessDes != updatedBusiness?.businessDes) changedProperties.add(Pair("businessDes", "${originalBusiness.businessDes} -> ${updatedBusiness?.businessDes}"))
        if (originalBusiness.businessSubCategory != updatedBusiness?.businessSubCategory) changedProperties.add(Pair("businessSubCategory", "${originalBusiness.businessSubCategory} -> ${updatedBusiness?.businessSubCategory}"))
        if (originalBusiness.businessEmail != updatedBusiness?.businessEmail) changedProperties.add(Pair("businessEmail", "${originalBusiness.businessEmail} -> ${updatedBusiness?.businessEmail}"))
        if (originalBusiness.postalAddress != updatedBusiness?.postalAddress) changedProperties.add(Pair("postalAddress", "${originalBusiness.postalAddress} -> ${updatedBusiness?.postalAddress}"))
        if (originalBusiness.postalCode != updatedBusiness?.postalCode) changedProperties.add(Pair("postalCode", "${originalBusiness.postalCode} -> ${updatedBusiness?.postalCode}"))
        if (originalBusiness.businessPhone != updatedBusiness?.businessPhone) changedProperties.add(Pair("businessPhone", "${originalBusiness.businessPhone} -> ${updatedBusiness?.businessPhone}"))
        if (originalBusiness.contactPersonNames != updatedBusiness?.contactPersonNames) changedProperties.add(Pair("contactPersonNames", "${originalBusiness.contactPersonNames} -> ${updatedBusiness?.contactPersonNames}"))
        if (originalBusiness.businessRole != updatedBusiness?.businessRole) changedProperties.add(Pair("businessRole", "${originalBusiness.businessRole} -> ${updatedBusiness?.businessRole}"))
        if (originalBusiness.contactPersonPhone != updatedBusiness?.contactPersonPhone) changedProperties.add(Pair("contactPersonPhone", "${originalBusiness.contactPersonPhone} -> ${updatedBusiness?.contactPersonPhone}"))
        if (originalBusiness.contactPersonEmail != updatedBusiness?.contactPersonEmail) changedProperties.add(Pair("contactPersonEmail", "${originalBusiness.contactPersonEmail} -> ${updatedBusiness?.contactPersonEmail}"))
        if (originalBusiness.fullNames != updatedBusiness?.fullNames) changedProperties.add(Pair("fullNames", "${originalBusiness.fullNames} -> ${updatedBusiness?.fullNames}"))
        if (originalBusiness.ownerID != updatedBusiness?.ownerID) changedProperties.add(Pair("ownerID", "${originalBusiness.ownerID} -> ${updatedBusiness?.ownerID}"))
        if (originalBusiness.ownerPhone != updatedBusiness?.ownerPhone) changedProperties.add(Pair("ownerPhone", "${originalBusiness.ownerPhone} -> ${updatedBusiness?.ownerPhone}"))
        if (originalBusiness.ownerEmail != updatedBusiness?.ownerEmail) changedProperties.add(Pair("ownerEmail", "${originalBusiness.ownerEmail} -> ${updatedBusiness?.ownerEmail}"))
        if (originalBusiness.kraPin != updatedBusiness?.kraPin) changedProperties.add(Pair("kraPin", "${originalBusiness.kraPin} -> ${updatedBusiness?.kraPin}"))
        if (originalBusiness.createdBy != updatedBusiness?.createdBy) changedProperties.add(Pair("createdBy", "${originalBusiness.createdBy} -> ${updatedBusiness?.createdBy}"))
        if (originalBusiness.dateCreated != updatedBusiness?.dateCreated) changedProperties.add(Pair("dateCreated", "${originalBusiness.dateCreated} -> ${updatedBusiness?.dateCreated}"))
        if (originalBusiness.lat != updatedBusiness?.lat) changedProperties.add(Pair("lat", "${originalBusiness.lat} -> ${updatedBusiness?.lat}"))
        if (originalBusiness.lng != updatedBusiness?.lng) changedProperties.add(Pair("lng", "${originalBusiness.lng} -> ${updatedBusiness?.lng}"))


        // Print the result
        var names = ""
        var index = 0


        for ((name, value) in changedProperties) {

            if (index == changedProperties.size - 1) {
                names += "and $name"
            } else {
                names += "$name, "
            }
            index++

        }

        println("The names of the changed properties are: $names")

       return  names
    }
    private fun getBusinessSubCategoryComparison(): String {
        // Get the original and updated business objects from the Const singleton class
        val originalBusiness = Const.instance.getOriginalBusiness()
        val updatedBusiness = Const.instance.getBusiness()

        // Use a conditional statement to check if there was a mismatch between the subCountyName and the wardName on the original business object
        if (originalBusiness.feeID != updatedBusiness?.feeID) {

            return "There was a change in Sub Category. ${originalBusiness.businessSubCategory} changed to ${updatedBusiness?.businessSubCategory} The applicant will be required to make additional payment for this."
        } else {

            return ""
        }

    }

    private fun getBillItem(): List<FeesAndCharges> {

        Const.instance.getSelectedFeesAndCharges().forEach {

            if (it.feeId == getValue(this, "feeID").toString()) {
                it.customer = "Business ID: $businessID"

            }else{
                it.customer = ""
            }

            it.zone = getValue(this@ApplicationVerificationBillingInformation,"zone").toString()
            it.amount = it.unitFeeAmount
            it.subCountyName =  getValue(this, "subCountyName").toString()
            it.subCountyID = getValue(this, "subCountyID").toString()
            it.wardID = getValue(this, "wardID").toString()
            it.wardName =  getValue(this, "wardName").toString()
            it.idNo = getValue(this@ApplicationVerificationBillingInformation,"idNo").toString()
            it.phoneNumber = getValue(this@ApplicationVerificationBillingInformation,"phoneNumber").toString()
            it.names = getValue(this@ApplicationVerificationBillingInformation,"names").toString()
            it.customerPhoneNumber =  getValue(this, "owner_phone").toString()
            it.description = getValue(this, "business_name").toString()
            it.fiscalYear = getValue(this, "fiscalYear").toString()
        }

        return Const.instance.getSelectedFeesAndCharges()
    }

    //	context.startActivity(Intent(context,InvoiceDetials::class.java).putExtra("billNo",list.billNo)
    private fun generateBill (){

        tv_message.text ="Generating bill please wait.."
        val formData = listOf(
            "function" to "generateBill3",
            "penalized" to penalized.toString(),
            "billItem" to  Gson().toJson(getBillItem()),
            "payNow" to payNow,
            "customerPhoneNumber" to edPhone.text.toString(),
            "deviceId" to getDeviceIdNumber(this)
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

                            print_invoice.visibility = View.VISIBLE

                            billNo =  response.data.billGenerated.billNo
                            print_invoice.setOnClickListener {
                                startActivity(Intent(this@ApplicationVerificationBillingInformation, InvoiceDetials::class.java).putExtra("billNo", billNo))
                            }

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
                        Toast.makeText(this@ApplicationVerificationBillingInformation,response.message, Toast.LENGTH_LONG).show()
                    }


                }else{
                    runOnUiThread {  Toast.makeText(this@ApplicationVerificationBillingInformation,response.message, Toast.LENGTH_LONG).show()}

                }

            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@ApplicationVerificationBillingInformation,result, Toast.LENGTH_LONG).show()
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
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@ApplicationVerificationBillingInformation,result, Toast.LENGTH_LONG).show()
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
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@ApplicationVerificationBillingInformation,result, Toast.LENGTH_LONG).show()
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
    private fun showMessageBoxPayment(transaction_code: String,payer: String,amount: String, des: String,category:String){

        // Check if messageBoxView has a parent
        if (messageBoxViewPaid.parent != null) {
            // Remove messageBoxView from its parent
            (messageBoxViewPaid.parent as ViewGroup).removeView(messageBoxViewPaid)
        }

        val messageBoxBuilder = androidx.appcompat.app.AlertDialog.Builder(this).setView(
            messageBoxViewPaid as View?
        )
        messageBoxInstancePaid = messageBoxBuilder.show()

        messageBoxViewPaid.tv_transaction.text = transaction_code
        messageBoxViewPaid.tv_payer.text = payer
        messageBoxViewPaid.tv_amount.text = amount
        messageBoxViewPaid.tv_des.text = des
        messageBoxViewPaid.tv_category.text = category

        messageBoxViewPaid.print_receipt.setOnClickListener {
            startActivity(Intent(this@ApplicationVerificationBillingInformation, com.aw.forcement.others.ReceiptDetails::class.java).putExtra("transaction_code", transaction_code))
        }

        messageBoxViewPaid.okay.setOnClickListener {
            messageBoxInstancePaid.dismiss()

            finishAffinity()
            startActivity(Intent(this, Businesses::class.java))

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

        messageBoxInstanceInfo.print_invoice_.setOnClickListener {
            startActivity(Intent(this@ApplicationVerificationBillingInformation, InvoiceDetials::class.java).putExtra("billNo", billNo))
        }

        messageBoxViewInfo.okay_later.setOnClickListener {
            messageBoxInstanceInfo.dismiss()
            Const.instance.setBusiness(null)
            finishAffinity()
            startActivity(Intent(this, Businesses::class.java))
        }

    }

}