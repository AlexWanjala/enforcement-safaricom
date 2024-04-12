package com.aw.forcement.fire

import AdapterFeeAndChargesList
import Const
import Json4Kotlin_Base
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.aw.forcement.BuildConfig
import com.aw.forcement.InvoiceDetials
import com.aw.forcement.R
import com.aw.forcement.others.CessPaymentsMatatus
import com.aw.forcement.sbp.application.Businesses
import com.aw.forcement.tabs.Home
import com.aw.passanger.api.*
import com.google.gson.Gson
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.printable.ImagePrintable
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.data.printer.DefaultPrinter
import com.mazenrashed.printooth.ui.ScanningActivity
import com.mazenrashed.printooth.utilities.Printing
import com.mazenrashed.printooth.utilities.PrintingCallback
import kotlinx.android.synthetic.main.activity_billing_information.*
import kotlinx.android.synthetic.main.activity_billing_information.radio_later
import kotlinx.android.synthetic.main.activity_billing_information.radio_now
import kotlinx.android.synthetic.main.activity_billing_information.tv_phone_mpesa
import kotlinx.android.synthetic.main.activity_stock_market_fees_summary.*
import kotlinx.android.synthetic.main.activity_stock_market_fees_summary.edPhone
import kotlinx.android.synthetic.main.activity_stock_market_fees_summary.imageClose
import kotlinx.android.synthetic.main.activity_stock_market_fees_summary.tvAmount
import kotlinx.android.synthetic.main.activity_stock_market_fees_summary.tvSendPush
import kotlinx.android.synthetic.main.activity_stock_market_fees_summary.tvSendPushDisabled
import kotlinx.android.synthetic.main.activity_stock_market_fees_summary.tv_message
import kotlinx.android.synthetic.main.activity_total_invoicing_details.*
import kotlinx.android.synthetic.main.message_box.view.*
import kotlinx.android.synthetic.main.payment_offline.view.*
import kotlinx.android.synthetic.main.payment_offline.view.imageIcon
import kotlinx.android.synthetic.main.payment_offline.view.tv_title
import kotlinx.android.synthetic.main.payment_recieved.view.*
import kotlinx.android.synthetic.main.payment_unsuccesfull.view.*
import kotlinx.android.synthetic.main.recycler_view.*
import kotlinx.android.synthetic.main.send_later.*
import kotlinx.android.synthetic.main.send_later.view.*
import net.glxn.qrgen.android.QRCode
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule

class TotalInvoicingDetails : AppCompatActivity() {

    lateinit var messageBoxViewTimeOut: View
    lateinit var messageBoxInstanceTimeOut: androidx.appcompat.app.AlertDialog

    lateinit var messageBoxView: View
    lateinit var messageBoxInstance: androidx.appcompat.app.AlertDialog // Declare as AlertDialog

    lateinit var messageBoxViewFailed: View
    lateinit var messageBoxInstanceFailed: androidx.appcompat.app.AlertDialog // Declare as AlertDialog

    lateinit var messageBoxViewPaid: View
    lateinit var messageBoxInstancePaid: androidx.appcompat.app.AlertDialog // Declare as AlertDialog

    lateinit var messageBoxViewInfo : View
    lateinit var messageBoxInstanceInfo: androidx.appcompat.app.AlertDialog

    var pushButton = false
    var showTimeout = true
    var checkPayment = true

    var totalAmount =0.0

    private var printing : Printing? = null
    var payNow =""
    var billNo =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_total_invoicing_details)


        radio_now.setOnClickListener {
            tv_phone_mpesa.text ="Mpesa Phone Number"
            payNow ="true"
        }
        radio_later.setOnClickListener {
            tv_phone_mpesa.text ="Mpesa Phone Number to Pay Later"
            payNow ="false"
        }


        imageClose.setOnClickListener { finish() }

        //Initialize messageBoxView here
        messageBoxViewTimeOut = LayoutInflater.from(this).inflate(R.layout.payment_offline, null)
        messageBoxView = LayoutInflater.from(this).inflate(R.layout.message_box, null)
        messageBoxViewFailed = LayoutInflater.from(this).inflate(R.layout.payment_unsuccesfull, null)
        messageBoxViewPaid = LayoutInflater.from(this).inflate(R.layout.payment_recieved, null)
        messageBoxViewInfo = LayoutInflater.from(this).inflate(R.layout.send_later, null)

        loadSelectedFeeAndCharges()

        tvSendPush.setOnClickListener {
            if(edPhone.text.isEmpty()){
                Toast.makeText(this,"Phone Required", Toast.LENGTH_LONG).show()
            }else{

                if(payNow==""){
                    Toast.makeText(this,"Select Pay Now or Later",Toast.LENGTH_LONG).show()
                }else{
                    generateBill()
                }


            }
        }

        //Bluetooth printer
        if (Printooth.hasPairedPrinter())
            printing = Printooth.printer()
        initListeners()


        //RegisterFire before sending stk Push

    }

    override fun onResume() {
        super.onResume()
    }


    private fun loadSelectedFeeAndCharges(){

        runOnUiThread {
            val adapter = AdapterFeeAndChargesList(this, Const.instance.getSelectedFeesAndChargesList())
            adapter.notifyDataSetChanged()
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter
            recyclerView.setHasFixedSize(false)
        }

        for ((spinner, selectedValue) in Const.instance.getSelectedFeesAndChargesList()) {
            // Log or use the selected values as needed
            Log.d("SelectedItems", "Spinner: $spinner, Selected Value: ${selectedValue.feeDescription} Quantity: ${selectedValue.quantity}")

            // Check if unitFeeAmount is not empty before converting to Double
            if (selectedValue.unitFeeAmount.isNotEmpty()) {
                // Check if selectedValue.quantity is not empty or null
                val quantity = if (selectedValue.quantity.isNullOrEmpty()) "1" else selectedValue.quantity
                // Add the unitFeeAmount multiplied by quantity to the total sum
                totalAmount += selectedValue.unitFeeAmount.toDouble() * quantity.toInt()
                // Log or perform additional actions as needed

                Log.d("Calculation", "UnitFeeAmount: ${selectedValue.unitFeeAmount}, Quantity: $quantity")
            }
            else {
                Log.e("Error", "Empty or non-numeric unitFeeAmount encountered.")
                // Handle the error or log a message as needed
            }
        }

        tvAmount.text ="KES "+ totalAmount
    }

    fun getJson(): String {

        val feesAndChargesMap = Const.instance.getSelectedFeesAndChargesList()

        // Iterate through the map and update the fields
        for ((spinner, feesAndCharges) in feesAndChargesMap) {

            val quantity = if (feesAndCharges.quantity.isNullOrEmpty()) "1" else feesAndCharges.quantity
            val desc ="Qty ${quantity} X KES ${feesAndCharges.unitFeeAmount}"
            // Modify the fields as needed
            feesAndCharges.zone = getValue(this, "zone").toString()
            feesAndCharges.revenueStreamItem = getValue(this, "subCountyName").toString()
            feesAndCharges.amount = (feesAndCharges.unitFeeAmount.toDouble()* quantity.toInt()).toString()
            feesAndCharges.subCountyName = getValue(this, "subCountyName").toString()
            feesAndCharges.subCountyID = getValue(this, "subCountyID").toString()
            feesAndCharges.wardID = getValue(this, "wardID").toString()
            feesAndCharges.wardName = getValue(this, "wardName").toString()
            feesAndCharges.idNo = getValue(this, "idNo").toString()
            feesAndCharges.phoneNumber = getValue(this, "phoneNumber").toString()
            feesAndCharges.names = getValue(this, "names").toString()
            feesAndCharges.customerPhoneNumber = edPhone.text.toString()
            feesAndCharges.description =  Const.instance.getSelectedFireSafety().toString()

            // Update the customer in the FeesAndCharges object
            if(feesAndCharges.accountDesc.contains("Certificate")){

                feesAndCharges.customer = "Business ID: ${intent.getStringExtra("businessID").toString()}"

            }

        }

        // Get the selected fees and charges list from Const.instance
        val feesAndChargesList = Const.instance.getSelectedFeesAndChargesList().values.toList()
        // Convert the list to a JSON string using Gson
        val gson = Gson()
        return gson.toJson(feesAndChargesList)
    }

    private fun generateBill (){
        showMessageBox()
        tv_message.text = "Generating bill please wait.."
        (messageBoxView as View?)!!.tv_message.text =
            "Generating bill please wait.."


        val formData = listOf(
            "function" to "generateBill3",
            "billItem" to getJson(),
            "deviceId" to getDeviceIdNumber(this),
            "payNow" to payNow,
            "customerPhoneNumber" to edPhone.text.toString()
        )
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                    if(response.success){
                        runOnUiThread {
                            tv_message.text ="Bill generated success.."
                            tvSendPush.visibility = View.GONE
                            tvSendPushDisabled.visibility = View.VISIBLE
                        }

                        billNo =  response.data.billGenerated.billNo


                        if (payNow=="true"){

                            customerPayBillOnline(
                                response.data.billGenerated.billNo,
                                response.data.billGenerated.payBillNo,
                                response.data.billGenerated.amount)
                        }
                        else{
                            runOnUiThread {
                                messageBoxInstance.dismiss()
                                showMessageBoxPaymentInfo()
                            }

                        }


                    }else{
                        Toast.makeText(this@TotalInvoicingDetails,response.message, Toast.LENGTH_LONG).show()
                    }


                }else{
                    runOnUiThread {  Toast.makeText(this@TotalInvoicingDetails,response.message, Toast.LENGTH_LONG).show()}

                }

            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@TotalInvoicingDetails,result, Toast.LENGTH_LONG).show()
                }
            }

        })
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
            startActivity(Intent(this, InvoiceDetials::class.java).putExtra("billNo", billNo))
        }

        messageBoxViewInfo.okay_later.setOnClickListener {
            messageBoxInstanceInfo.dismiss()
            Const.instance.setBusiness(null)
            finishAffinity()
            startActivity(Intent(this, Home::class.java))
        }

    }
    private fun showMessageBox() {
        // Check if messageBoxView has a parent
        if (messageBoxView.parent != null) {
            // Remove messageBoxView from its parent
            (messageBoxView.parent as ViewGroup).removeView(messageBoxView)
        }
        val messageBoxBuilder =
            androidx.appcompat.app.AlertDialog.Builder(this).setView(messageBoxView as View?)
        messageBoxInstance = messageBoxBuilder.show()
        messageBoxInstance.setCanceledOnTouchOutside(false)
    }
    private fun showMessageBoxPayment(transaction: String, payer: String, amount: String, des: String, category: String) {
        stopTimer()

        val messageBoxBuilder = androidx.appcompat.app.AlertDialog.Builder(this).setView(
            messageBoxViewPaid as View?
        )
        messageBoxInstancePaid = messageBoxBuilder.create() // Create dialog without showing it yet
        messageBoxInstancePaid.setCanceledOnTouchOutside(false)

        messageBoxViewPaid.tv_transaction.text = transaction
        messageBoxViewPaid.tv_payer.text = payer
        messageBoxViewPaid.tv_amount.text = amount
        messageBoxViewPaid.tv_des.text = des
        messageBoxViewPaid.tv_category.text = category
        messageBoxViewPaid.okay.setOnClickListener { messageBoxInstancePaid.dismiss() }

        // Show the dialog only if the activity is not finishing
        if (!isFinishing) {
            messageBoxInstancePaid.show()
        }
    }

    override fun onPause() {
        super.onPause()
        // Dismiss the dialog to prevent window leaks
        if (::messageBoxInstancePaid.isInitialized && messageBoxInstancePaid.isShowing) {
            messageBoxInstancePaid.dismiss()
        }
    }

    private fun showMessageBoxPaymentFail(message: String,accountReference: String,payBillNumber: String, amount: String) {
        stopTimer()

        // Check if messageBoxView has a parent
        if (messageBoxViewFailed.parent != null) {
            // Remove messageBoxView from its parent
            (messageBoxViewFailed.parent as ViewGroup).removeView(messageBoxViewFailed)
        }

        val messageBoxBuilder = androidx.appcompat.app.AlertDialog.Builder(this).setView(
            messageBoxViewFailed as View?
        )
        messageBoxInstanceFailed = messageBoxBuilder.show()
        messageBoxInstanceFailed.setCanceledOnTouchOutside(false)

        if (message.contains("invalid")) {
            (messageBoxViewFailed as View?)!!.tv_title.text = "Wrong PIN"
            (messageBoxViewFailed as View?)!!.tv_message_unpaid.text =
                "The Payer typed an incorrect pin. Send the request again to retry."
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

        (messageBoxViewFailed as View?)!!.btnPayOffline.setOnClickListener {
            messageBoxInstanceFailed.dismiss()
            showMessageBoxTimeOut(accountReference,payBillNumber,amount)
        }
        (messageBoxViewFailed as View?)!!.tv_close.setOnClickListener { messageBoxInstanceFailed.dismiss() }
        (messageBoxViewFailed as View?)!!.resend.setOnClickListener {
            messageBoxInstanceFailed.dismiss()
            generateBill()
        }

    }
    lateinit var timer: TimerTask
    private fun stopTimer() {
        timer.cancel()
    }
    private fun showMessageBoxTimeOut( accountReference: String, payBillNumber: String, amount: String) {
        showTimeout = false
        // Check if messageBoxView has a parent
        if (messageBoxViewTimeOut.parent != null) {
            // Remove messageBoxView from its parent
            (messageBoxViewTimeOut.parent as ViewGroup).removeView(messageBoxViewTimeOut)
        }
        val messageBoxBuilder = androidx.appcompat.app.AlertDialog.Builder(this).setView(messageBoxViewTimeOut as View?)
        messageBoxInstanceTimeOut = messageBoxBuilder.show()
        messageBoxInstanceTimeOut.setCanceledOnTouchOutside(false)
        messageBoxViewTimeOut.tv_paybill.text = payBillNumber
        messageBoxViewTimeOut.tv_acc_no.text = accountReference
        messageBoxViewTimeOut.tv_amount_.text = "KES ${amount}"
        messageBoxViewTimeOut.btn_resend.setOnClickListener {
            messageBoxInstanceTimeOut.dismiss()
            showMessageBox()
            customerPayBillOnline(
                accountReference,
                payBillNumber,
                amount
            )
        }
        messageBoxViewTimeOut.tv_close_.setOnClickListener { messageBoxInstanceTimeOut.dismiss()}
        messageBoxViewTimeOut.btn_verify_payment.setOnClickListener {

            messageBoxViewTimeOut.btn_verify_payment.text = "Please Wait.."
            checkPayment = true
            checkPayment(accountReference,payBillNumber,amount)

        }

    }
    private fun customerPayBillOnline(accountReference: String, payBillNumber: String, amount: String) {

        checkPayment = true

        timer = Timer().schedule(65000) {
            runOnUiThread {
                if(showTimeout){
                    pushButton = true
                    checkPayment = false

                    if (showTimeout){
                        tv_message.text =""
                        tvSendPush.visibility = View.VISIBLE
                        tvSendPushDisabled.visibility = View.GONE

                        messageBoxInstance.dismiss()
                        // Check if messageBoxInstanceFailed has been initialized before trying to dismiss it
                        if (::messageBoxInstanceFailed.isInitialized) {
                            messageBoxInstanceFailed.dismiss()
                        }
                        showMessageBoxTimeOut(
                            accountReference,
                            payBillNumber,
                            amount)
                    }
                }
            }
        }


        // progressBar1.visibility = View.VISIBLE
        runOnUiThread {
            tv_message.text = "Sending Payment Request.."
            (messageBoxView as View?)!!.tv_message.text = "Sending Payment Request.."

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
        executePaysolRequest(formData, paysol, object : CallBack {
            override fun onSuccess(result: String?) {
                //  runOnUiThread {  progress_circular.visibility = View.GONE }
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if (response.success) {

                    runOnUiThread {
                        // progressBar1.visibility = View.GONE
                        checkPayment(accountReference, payBillNumber, amount)
                    }

                } else {
                    runOnUiThread {
                        tvSendPush.visibility = View.VISIBLE
                        tvSendPushDisabled.visibility = View.GONE
                        tv_message.text = response.message
                    }

                }

            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@TotalInvoicingDetails,result, Toast.LENGTH_LONG).show()
                }
            }

        })

    }
    fun checkPayment(accountReference: String,payBillNumber: String, amount: String) {

        if(checkPayment){

            //  runOnUiThread {   progressBarPayments.visibility = View.VISIBLE }
            val formData = listOf(
                "function" to "checkPayment",
                "accNo" to accountReference,
                "token" to "im05WXYH2rwRruPjCICieOs8m4E8IoltnDEhyPUv6bnB9cU60gD48SnJPC6oh7EpsPaAUGC8wqIdtVVjGlWLxqFssshxMHxHjEQJ"
            )
            executePaysolRequest(formData, paysol, object : CallBack {
                override fun onSuccess(result: String?) {
                    val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)

                    if (response.success) {

                        if (response.data.push.callback_returned == "PAID") {

                            runOnUiThread {
                                showTimeout = false

                                messageBoxInstance.dismiss()

                                tvSendPush.visibility = View.VISIBLE
                                tvSendPushDisabled.visibility = View.GONE
                                tv_message.text =
                                    "Payment Received #${response.data.push.transaction_code} KES ${response.data.push.amount}"

                                save(
                                    this@TotalInvoicingDetails,
                                    "transaction_code",
                                    response.data.push.transaction_code
                                )
                                save(this@TotalInvoicingDetails, "amount", response.data.push.amount)
                                save(this@TotalInvoicingDetails, "payer_phone", response.data.push.account_from)
                                save(this@TotalInvoicingDetails, "ref", response.data.push.ref)
                                save(this@TotalInvoicingDetails, "payer_names", response.data.transaction.names)
                                save(this@TotalInvoicingDetails, "date", response.data.transaction.date)

                                //v_transaction: String,payer: String,amount: String, des: String,category:String
                                showMessageBoxPayment(
                                    response.data.transaction.transaction_code,
                                    response.data.transaction.names,
                                    response.data.transaction.amount,
                                    "${edPhone.text}","")

                                getReceipt(response.data.transaction.transaction_code)
/*
                            transactionCode.text = response.data.push.transaction_code
                            tvAmount.text = "KES "+response.data.push.amount
                            tvRef.text = response.data.push.ref
                            tvStatus.text = response.data.push.callback_returned; */


                            }


                        }
                        else if (response.data.push.callback_returned == "PENDING") {

                            if(checkPayment){
                                runOnUiThread {
                                    tv_message.text = "Waiting for payment.."
                                    (messageBoxView as View?)!!.tv_message.text = "Waiting for payment.."

                                }
                                TimeUnit.SECONDS.sleep(3L)
                                checkPayment(accountReference,payBillNumber,amount)
                            }

                        }
                        else {
                            runOnUiThread {
                                messageBoxInstance.dismiss()
                                tv_message.text = response.data.push.message
                                tvSendPush.visibility = View.VISIBLE
                                tvSendPushDisabled.visibility = View.GONE
                                showMessageBoxPaymentFail(response.data.push.message,accountReference,payBillNumber,amount)

                            }
                        }

                    } else {
                        if(checkPayment){
                            runOnUiThread { tv_message.text = "Waiting for payment.." }
                            TimeUnit.SECONDS.sleep(3L)
                            checkPayment(accountReference,payBillNumber,amount)
                        }
                    }
                }
                override fun onFailure(result: String?) {
                    runOnUiThread {
                        Toast.makeText(this@TotalInvoicingDetails,result, Toast.LENGTH_LONG).show()
                    }
                }

            })
        }

    }
    private fun getReceipt(transaction_code: String){
        val formData = listOf(
            "function" to "getReceipt",
            "receiptNo" to transaction_code,
            "latitude" to getValue(this,"latitude").toString(),
            "longitude" to getValue(this,"longitude").toString(),
            "idNo" to getValue(this,"idNo").toString(),
            "username" to getValue(this,"username").toString(),
            "addressString" to getValue(this,"addressString").toString(),
            "deviceId" to getDeviceIdNumber(this)
        )
        executeRequest(formData, biller,object : CallBack {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){
                    runOnUiThread {

                        var descriptions =""
                        var item =""

                        for (receiptInfo in response.data.receiptInfos) {

                            val  customer = receiptInfo.customer
                            val  des = receiptInfo.description

                            item +="${receiptInfo.feeDescription}:   ${receiptInfo.receiptAmount}\n";

                            if (":" in des && "," in des) {
                                // Both ":" and "," are present in the description
                                //Seller: Tdd, Seller ID No: yggg, Buyer: rtt, Buyer ID: tyy, Assistant Chief: ftt, Chief: yy, Location: fgg
                                val array = des.split(",")

                                array.forEach { element ->
                                    // Process each element
                                    val array2 = element.split(":")
                                    if (array2.size >= 2) { // Ensure that array2 has at least two elements
                                        descriptions += "${array2[0].trim()}: ${array2[1].trim()}\n"
                                    } else {
                                        // Handle the case where array2 doesn't have enough elements
                                        // You can log an error or handle it based on your requirement
                                    }
                                }



                            }



                        }

                        save(this@TotalInvoicingDetails,"r_headline",response.data.county.headline)
                        save(this@TotalInvoicingDetails,"r_dateCreated",response.data.receiptDetails.dateCreated)
                        save(this@TotalInvoicingDetails,"r_source",response.data.receiptDetails.source)
                        save(this@TotalInvoicingDetails,"r_currency",response.data.receiptDetails.currency)
                        save(this@TotalInvoicingDetails,"r_item",item)
                        save(this@TotalInvoicingDetails,"r_ussd",response.data.county.ussd)
                        save(this@TotalInvoicingDetails,"r_incomeTypeDescription",response.data.receiptDetails.incomeTypeDescription)
                        save(this@TotalInvoicingDetails,"r_description",descriptions)
                        save(this@TotalInvoicingDetails,"r_date",response.data.receiptDetails.dateCreated)
                        save(this@TotalInvoicingDetails,"r_subCountyName",response.data.receiptDetails.subCountyName)
                        save(this@TotalInvoicingDetails,"r_zone",response.data.receiptDetails.zone)
                        save(this@TotalInvoicingDetails,"r_names",response.data.receiptDetails.names)
                        save(this@TotalInvoicingDetails,"r_transactionCode",response.data.receiptDetails.transactionCode)
                        save(this@TotalInvoicingDetails,"r_payer",response.data.receiptDetails.paidBy)
                        save(this@TotalInvoicingDetails,"r_payerPhone",response.data.receiptDetails.customerPhoneNumber)
                        save(this@TotalInvoicingDetails,"r_billNo",response.data.receiptDetails.billNo)
                        save(this@TotalInvoicingDetails,"r_receiptNo",response.data.receiptDetails.receiptNo)
                        save(this@TotalInvoicingDetails,"r_receiptAmount",response.data.receiptDetails.receiptAmount)

                        tvSendPush.setText("Print")
                        tvSendPush.setOnClickListener { printReceipt() }
                        pushButton = true
                        printReceipt()


                    }
                }else{
                    runOnUiThread {
                        //Toast.makeText(this@TotalInvoicingDetails,response.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@TotalInvoicingDetails,result, Toast.LENGTH_LONG).show()
                }
            }

        })


    }

    //printer services starts here
    @RequiresApi(Build.VERSION_CODES.O)
    fun printReceipt(){
        if (!Printooth.hasPairedPrinter())
            resultLauncher.launch(
                Intent(
                    this,
                    ScanningActivity::class.java
                ),
            )
        else printDetails()
    }
    private fun initListeners() {
        /* callback from printooth to get printer process */
        printing?.printingCallback = object : PrintingCallback {
            override fun connectingWithPrinter() {
                Toast.makeText(this@TotalInvoicingDetails, "Connecting with printer", Toast.LENGTH_SHORT).show()
            }

            override fun printingOrderSentSuccessfully() {
                Toast.makeText(this@TotalInvoicingDetails, "Order sent to printer", Toast.LENGTH_SHORT).show()
            }

            override fun connectionFailed(error: String) {
                Toast.makeText(this@TotalInvoicingDetails, "Failed to connect printer", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: String) {
                Toast.makeText(this@TotalInvoicingDetails, error, Toast.LENGTH_SHORT).show()
            }

            override fun onMessage(message: String) {
                Toast.makeText(this@TotalInvoicingDetails, "Message: $message", Toast.LENGTH_SHORT).show()
            }

            override fun disconnected() {
                Toast.makeText(this@TotalInvoicingDetails, "Disconnected Printer", Toast.LENGTH_SHORT).show()
                finish()
                startActivity(Intent(this@TotalInvoicingDetails, CessPaymentsMatatus::class.java))
            }

        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun printDetails() {
        val printables = getSomePrintables()
        printing?.print(printables)
    }
    /* Customize your printer here with text, logo and QR code */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getSomePrintables() = java.util.ArrayList<Printable>().apply {

        val dateCreated = getValue(this@TotalInvoicingDetails,"r_dateCreated")
        val source = getValue(this@TotalInvoicingDetails,"r_source")
        val currency = getValue(this@TotalInvoicingDetails,"r_currency")
        val ussd = getValue(this@TotalInvoicingDetails,"r_ussd")
        val payerPhone = getValue(this@TotalInvoicingDetails,"r_payerPhone")
        val payer = getValue(this@TotalInvoicingDetails,"r_payer")
        val receiptNo = getValue(this@TotalInvoicingDetails,"r_receiptNo")
        val item = getValue(this@TotalInvoicingDetails,"r_item")
        val transactioncode = getValue(this@TotalInvoicingDetails,"r_transactionCode")
        val amount = getValue(this@TotalInvoicingDetails,"r_receiptAmount")
        val ref = getValue(this@TotalInvoicingDetails,"r_billNo")
        val username = getValue(this@TotalInvoicingDetails,"username")
        val names = getValue(this@TotalInvoicingDetails,"r_names")
        val phone = getValue(this@TotalInvoicingDetails,"payer_phone")
        val incomeTypeDescription = getValue(this@TotalInvoicingDetails,"r_incomeTypeDescription")?.capitalize()
        val description = getValue(this@TotalInvoicingDetails,"r_description")
        val zone = getValue(this@TotalInvoicingDetails,"r_zone")?.toUpperCase()
        val subCounty = getValue(this@TotalInvoicingDetails,"r_subCountyName")?.toUpperCase()


        // Parse the input string to LocalDateTime
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val parsedDateTime = LocalDateTime.parse(dateCreated, inputFormatter)
        val outputFormatter = DateTimeFormatter.ofPattern("d/MMM/yyyy h:mm:ss a")
        val date = parsedDateTime.format(outputFormatter)

        var time = getCurrentDateTime()


        val bmp = BitmapFactory.decodeResource(resources, R.drawable.print_county_logo)
        val argbBmp = bmp.copy(Bitmap.Config.ARGB_8888, false)
        val scaledLogo = Bitmap.createScaledBitmap(argbBmp, 145, 180, true)
        add(
            ImagePrintable.Builder(scaledLogo)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .build())

        val title2 = when (BuildConfig.FLAVOR) {
            "homabay" -> "COUNTY GOVERNMENT OF HOMABAY\n"
            "meru" -> "COUNTY GOVERNMENT OF MERU\n"
            "kisumu" -> "COUNTY GOVERNMENT OF KISUMU\n"
            "elgeyo" -> "COUNTY GOVERNMENT OF ELGEYO MARAKWET\n"
            else -> "COUNTY GOVERNMENT OF UNKNOWN\n"
        }

        add(
            TextPrintable.Builder()
                .setText(title2)
                .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .build())

        val title ="${subCounty}, ${zone}\n"
        add(
            TextPrintable.Builder()
                .setText(title)
                .setFontSize(0.1.toInt().toByte())
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                // .setNewLinesAfter(1)
                .build())

        val title3 ="-----------------------\n"
        add(
            TextPrintable.Builder()
                .setText(title3)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                // .setNewLinesAfter(1)
                .build())

        add(
            TextPrintable.Builder()
                .setText("OFFICIAL RECEIPT (KES)\n")
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                // .setNewLinesAfter(1)
                .build())


        var text ="Date:  ${date}\n";
        text +="Receipt No:  ${receiptNo}\n"
        text +="Served By:    ${names}\n"
        text +="Mode:     ${source}(${transactioncode})\n"
        text +="Payer:         ${payer}\n"
        text +="Payer Phone:     ${payerPhone}\n"
        text +="Invoice No:     ${ref}\n\n"

        add(
            TextPrintable.Builder()
                .setText(text)
                .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                .build())

        text ="____________________________\n";
        text +="Item's                  Total\n";
        text +="____________________________\n";
        add(
            TextPrintable.Builder()
                .setText(text)
                .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                .build())

        if (item != null) {
            text =item
        }

        add(
            TextPrintable.Builder()
                .setText(text)
                .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                .build())

        text ="_____________________________\n";
        text +="SUB TOTAL          ${currency} ${amount}\n";
        text +="____________________________\n";
        add(
            TextPrintable.Builder()
                .setText(text)
                .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                .build())


        text ="\nDetails\n"
        add(
            TextPrintable.Builder()
                .setText(text)
                .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .build())

        text ="This Receipt was Printed on ${time} By ${username}\n\n"
        add(
            TextPrintable.Builder()
                .setText(text)
                .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                .build())

        if (description != null) {
            text = description
        }
        add(
            TextPrintable.Builder()
                .setText(text)
                .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                .build())


        text ="Payment Code:$transactioncode, Amount:$amount, Payer:$names, Date: $time, Printed By: $username"

        val qr: Bitmap = QRCode.from(text)
            .withSize(200, 200).bitmap()
        add(
            ImagePrintable.Builder(qr)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .build())

        text = "${ussd}"
        add(
            TextPrintable.Builder()
                .setText(text)
                .setFontSize(10)
                .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                .build())


        val title35 ="\n\n\n\n\n\n"
        add(
            TextPrintable.Builder()
                .setText(title35)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                // .setNewLinesAfter(1)
                .build())



        /*


           val bmp = BitmapFactory.decodeResource(resources, R.drawable.print_county_logo)
           val argbBmp = bmp.copy(Bitmap.Config.ARGB_8888, false)
           val scaledLogo = Bitmap.createScaledBitmap(argbBmp, 145, 180, true)
           add(
               ImagePrintable.Builder(scaledLogo)
                   .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                   .build())

           val transactioncode = getValue(this@MovementFeesSummary,"transaction_code")
           val amount = getValue(this@MovementFeesSummary,"amount")
           val ref = getValue(this@MovementFeesSummary,"ref")
           val username = getValue(this@MovementFeesSummary,"username")
           val names = getValue(this@MovementFeesSummary,"payer_names")
           val phone = getValue(this@MovementFeesSummary,"payer_phone")
           val incomeTypeDescription = getValue(this@MovementFeesSummary,"incomeTypeDescription")?.capitalize()
           val description = getValue(this@MovementFeesSummary,"description")
           val date = getValue(this@MovementFeesSummary,"date")



           *//* val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
         val outputFormat = SimpleDateFormat("EEE dd MMM yy hh:mma", Locale.getDefault())
         val date = input?.let { inputFormat.parse(it) }
         val humanDate = date?.let { outputFormat.format(it) }*//*
        val humanDate = date
        val zone = getValue(this@MovementFeesSummary,"zone")
        val message ="\n\nFor: $description #Mpesa\nTransaction Code: $transactioncode\nAmount: KES $amount\nPayer: $names\nDate: $humanDate\nPrinted By: $username at $zone\n"

        add(
            TextPrintable.Builder()
                .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                .setText(message)
                // .setNewLinesAfter(1)
                .build())

        val message2 ="Payment Code:$transactioncode, Amount:$amount, Payer:$names, Date: $humanDate, Printed By: $username"

        val qr: Bitmap = QRCode.from(message2)
            .withSize(200, 200).bitmap()
        add(
            ImagePrintable.Builder(qr)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .build())


        val footer = when (BuildConfig.FLAVOR) {
            "homabay" -> "Lipa Ushuru Tujenge\n\n#EndlessPotential\n\n\n\n\n\n"
            "meru" -> "Lipa Ushuru Tujenge\n\n#Making Meru Happy\n\n\n\n\n\n\n"
            else -> "Lipa Ushuru Tujenge\n\n#\n\n\n\n\n"
        }

        add(
            TextPrintable.Builder()
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setText(footer)
                .build())
*/

    }
    /* Inbuilt activity to pair device with printer or select from list of pair bluetooth devices */
    @RequiresApi(Build.VERSION_CODES.O)
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == ScanningActivity.SCANNING_FOR_PRINTER &&  result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
//            val intent = result.data
            printDetails()
        }
    }

}