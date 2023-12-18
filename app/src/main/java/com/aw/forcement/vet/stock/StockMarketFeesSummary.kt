package com.aw.forcement.vet.stock

import AdapterFeeAndChargesList
import Const
import Json4Kotlin_Base
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.aw.forcement.BuildConfig
import com.aw.forcement.R
import com.aw.forcement.others.CessPaymentsMatatus
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
import kotlinx.android.synthetic.main.activity_stock_market_fees.*
import kotlinx.android.synthetic.main.activity_stock_market_fees_summary.*
import kotlinx.android.synthetic.main.activity_stock_market_fees_summary.edPhone
import kotlinx.android.synthetic.main.activity_stock_market_fees_summary.ed_slaughter_house
import kotlinx.android.synthetic.main.activity_stock_market_fees_summary.tvAmount
import kotlinx.android.synthetic.main.activity_stock_market_fees_summary.tvSendPush
import kotlinx.android.synthetic.main.activity_stock_market_fees_summary.tvSendPushDisabled
import kotlinx.android.synthetic.main.activity_stock_market_fees_summary.tv_message
import kotlinx.android.synthetic.main.message_box.view.*
import kotlinx.android.synthetic.main.payment_offline.view.*
import kotlinx.android.synthetic.main.payment_offline.view.imageIcon
import kotlinx.android.synthetic.main.payment_offline.view.tv_title
import kotlinx.android.synthetic.main.payment_recieved.view.*
import kotlinx.android.synthetic.main.payment_unsuccesfull.view.*
import kotlinx.android.synthetic.main.recycler_view.*
import net.glxn.qrgen.android.QRCode
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule


class StockMarketFeesSummary : AppCompatActivity() {
    
    lateinit var messageBoxViewTimeOut: View
    lateinit var messageBoxInstanceTimeOut: androidx.appcompat.app.AlertDialog

    lateinit var messageBoxView: View
    lateinit var messageBoxInstance: androidx.appcompat.app.AlertDialog // Declare as AlertDialog

    lateinit var messageBoxViewFailed: View
    lateinit var messageBoxInstanceFailed: androidx.appcompat.app.AlertDialog // Declare as AlertDialog

    lateinit var messageBoxViewPaid: View
    lateinit var messageBoxInstancePaid: androidx.appcompat.app.AlertDialog // Declare as AlertDialog

    var pushButton = false
    var showTimeout = true
    var checkPayment = true

    var totalAmount =0.0
    
    private var printing : Printing? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock_market_fees_summary)

        //Initialize messageBoxView here
        messageBoxViewTimeOut = LayoutInflater.from(this).inflate(R.layout.payment_offline, null)
        messageBoxView = LayoutInflater.from(this).inflate(R.layout.message_box, null)
        messageBoxViewFailed = LayoutInflater.from(this).inflate(R.layout.payment_unsuccesfull, null)
        messageBoxViewPaid = LayoutInflater.from(this).inflate(R.layout.payment_recieved, null)

        loadSelectedFeeAndCharges()

        tvSendPush.setOnClickListener {
            if(edPhone.text.isEmpty()){
                Toast.makeText(this,"Phone Required",Toast.LENGTH_LONG).show()
            }else{
                generateBill()
            }
        }

        //Bluetooth printer
        if (Printooth.hasPairedPrinter())
            printing = Printooth.printer()
        initListeners()
        
    }

    override fun onResume() {
        ed_slaughter_house.setText(getValue(this,"zone"))
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
            feesAndCharges.customer = ""
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
            feesAndCharges.description = desc

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
                            tvSendPush.visibility = View.GONE
                            tvSendPushDisabled.visibility = View.VISIBLE
                        }

                        customerPayBillOnline(
                            response.data.billGenerated.billNo,
                            response.data.billGenerated.payBillNo,
                            response.data.billGenerated.amount,
                        )

                    }else{
                        Toast.makeText(this@StockMarketFeesSummary,response.message, Toast.LENGTH_LONG).show()
                    }


                }else{
                    runOnUiThread {  Toast.makeText(this@StockMarketFeesSummary,response.message, Toast.LENGTH_LONG).show()}

                }

            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@StockMarketFeesSummary,result, Toast.LENGTH_LONG).show()
                }
            }

        })
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

        // Check if messageBoxView has a parent
        if (messageBoxViewPaid.parent != null) {
            // Remove messageBoxView from its parent
            (messageBoxViewPaid.parent as ViewGroup).removeView(messageBoxViewPaid)
        }

        val messageBoxBuilder = androidx.appcompat.app.AlertDialog.Builder(this).setView(
            messageBoxViewPaid as View?
        )
        messageBoxInstancePaid = messageBoxBuilder.show()
        messageBoxInstancePaid.setCanceledOnTouchOutside(false)

        messageBoxViewPaid.tv_transaction.text = transaction
        messageBoxViewPaid.tv_payer.text = payer
        messageBoxViewPaid.tv_amount.text = amount
        messageBoxViewPaid.tv_des.text = des
        messageBoxViewPaid.tv_category.text = category
        messageBoxViewPaid.okay.setOnClickListener { messageBoxInstancePaid.dismiss() }

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
                    Toast.makeText(this@StockMarketFeesSummary,result, Toast.LENGTH_LONG).show()
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
                                    this@StockMarketFeesSummary,
                                    "transaction_code",
                                    response.data.push.transaction_code
                                )
                                save(this@StockMarketFeesSummary, "amount", response.data.push.amount)
                                save(this@StockMarketFeesSummary, "payer_phone", response.data.push.account_from)
                                save(this@StockMarketFeesSummary, "ref", response.data.push.ref)
                                save(this@StockMarketFeesSummary, "payer_names", response.data.transaction.names)
                                save(this@StockMarketFeesSummary, "date", response.data.transaction.date)

                                //v_transaction: String,payer: String,amount: String, des: String,category:String
                                showMessageBoxPayment(
                                    response.data.transaction.transaction_code,
                                    response.data.transaction.names,
                                    response.data.transaction.amount,
                                    "${edPhone.text}","")
/*
                            transactionCode.text = response.data.push.transaction_code
                            tvAmount.text = "KES "+response.data.push.amount
                            tvRef.text = response.data.push.ref
                            tvStatus.text = response.data.push.callback_returned; */

                                tvSendPush.setText("Print")
                                tvSendPush.setOnClickListener { printReceipt() }
                                pushButton = true
                                printReceipt()

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
                        Toast.makeText(this@StockMarketFeesSummary,result, Toast.LENGTH_LONG).show()
                    }
                }

            })
        }

    }

    //printer services starts here
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
                Toast.makeText(this@StockMarketFeesSummary, "Connecting with printer", Toast.LENGTH_SHORT).show()
            }

            override fun printingOrderSentSuccessfully() {
                Toast.makeText(this@StockMarketFeesSummary, "Order sent to printer", Toast.LENGTH_SHORT).show()
            }

            override fun connectionFailed(error: String) {
                Toast.makeText(this@StockMarketFeesSummary, "Failed to connect printer", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: String) {
                Toast.makeText(this@StockMarketFeesSummary, error, Toast.LENGTH_SHORT).show()
            }

            override fun onMessage(message: String) {
                Toast.makeText(this@StockMarketFeesSummary, "Message: $message", Toast.LENGTH_SHORT).show()
            }

            override fun disconnected() {
                Toast.makeText(this@StockMarketFeesSummary, "Disconnected Printer", Toast.LENGTH_SHORT).show()
                finish()
                startActivity(Intent(this@StockMarketFeesSummary, CessPaymentsMatatus::class.java))
            }

        }
    }
    private fun printDetails() {
        val printables = getSomePrintables()
        printing?.print(printables)
    }
    /* Customize your printer here with text, logo and QR code */
    private fun getSomePrintables() = java.util.ArrayList<Printable>().apply {

        val title ="\n\nOFFICIAL RECIEPT\n\n"
        add(
            TextPrintable.Builder()
                .setText(title)
                .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                // .setNewLinesAfter(1)
                .build())

        val title2 = when (BuildConfig.FLAVOR) {
            "homabay" -> "COUNTY GOVERNMENT OF HOMABAY\n\n#\n\n\n"
            "meru" -> "COUNTY GOVERNMENT OF MERU\n\n#\n\n\n"
            else -> "COUNTY GOVERNMENT OF UNKNOWN\n\n#\n\n\n"
        }


        add(
            TextPrintable.Builder()
                .setText(title2)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .build())


        val bmp = BitmapFactory.decodeResource(resources, R.drawable.print_county_logo)
        val argbBmp = bmp.copy(Bitmap.Config.ARGB_8888, false)
        val scaledLogo = Bitmap.createScaledBitmap(argbBmp, 145, 180, true)
        add(
            ImagePrintable.Builder(scaledLogo)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .build())

        val transactioncode = getValue(this@StockMarketFeesSummary,"transaction_code")
        val amount = getValue(this@StockMarketFeesSummary,"amount")
        val ref = getValue(this@StockMarketFeesSummary,"ref")
        val username = getValue(this@StockMarketFeesSummary,"username")
        val names = getValue(this@StockMarketFeesSummary,"payer_names")
        val phone = getValue(this@StockMarketFeesSummary,"payer_phone")
        val feeDescription = getValue(this@StockMarketFeesSummary,"feeDescription")
        val incomeTypeDescription = getValue(this@StockMarketFeesSummary,"incomeTypeDescription")?.capitalize()
        val description = getValue(this@StockMarketFeesSummary,"description")
        val date = getValue(this@StockMarketFeesSummary,"date")



        /* val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
         val outputFormat = SimpleDateFormat("EEE dd MMM yy hh:mma", Locale.getDefault())
         val date = input?.let { inputFormat.parse(it) }
         val humanDate = date?.let { outputFormat.format(it) }*/
        val humanDate = date
        val zone = getValue(this@StockMarketFeesSummary,"zone")
        val message = "\n\nType:${incomeTypeDescription}\nDesc: ${feeDescription}\nFor: $description\nMpesa: $transactioncode\nAmount: KES $amount\nPayer: $names\nDate: $humanDate\nPrinted By: $username at $zone\n"

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


    }
    /* Inbuilt activity to pair device with printer or select from list of pair bluetooth devices */
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == ScanningActivity.SCANNING_FOR_PRINTER &&  result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
//            val intent = result.data
            printDetails()
        }
    }

}