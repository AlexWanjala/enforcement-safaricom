package com.aw.forcement

import Json4Kotlin_Base
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
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
import kotlinx.android.synthetic.main.activity_street_parking.*
import kotlinx.android.synthetic.main.activity_street_parking.imageClose
import kotlinx.android.synthetic.main.activity_street_parking.tvAmount
import kotlinx.android.synthetic.main.activity_street_parking.tvSendPushDisabled
import kotlinx.android.synthetic.main.activity_street_parking.tv_message
import kotlinx.android.synthetic.main.message_box.view.*
import kotlinx.android.synthetic.main.payment_offline.view.*
import kotlinx.android.synthetic.main.payment_recieved.view.*
import kotlinx.android.synthetic.main.payment_unsuccesfull.view.*
import kotlinx.android.synthetic.main.payment_unsuccesfull.view.imageIcon
import kotlinx.android.synthetic.main.payment_unsuccesfull.view.resend
import kotlinx.android.synthetic.main.payment_unsuccesfull.view.tv_close
import kotlinx.android.synthetic.main.payment_unsuccesfull.view.tv_title
import net.glxn.qrgen.android.QRCode
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule
import kotlin.concurrent.scheduleAtFixedRate


class StreetParking : AppCompatActivity() {
    private val arrayList = ArrayList<String>()
    private val arrayList2 = ArrayList<String>()
    lateinit var feeDescription: String
    lateinit var incomeTypeDescription: String
    lateinit var payer: String

    lateinit var amount: String
    lateinit var feeId: String

    var pushButton = false
    var showTimeout = true
    var checkPayment = true

    lateinit var messageBoxViewTimeOut: View
    lateinit var messageBoxInstanceTimeOut: androidx.appcompat.app.AlertDialog

    lateinit var messageBoxView: View
    lateinit var messageBoxInstance: androidx.appcompat.app.AlertDialog // Declare as AlertDialog

    lateinit var messageBoxViewFailed: View
    lateinit var messageBoxInstanceFailed: androidx.appcompat.app.AlertDialog // Declare as AlertDialog

    lateinit var messageBoxViewPaid: View
    lateinit var messageBoxInstancePaid: androidx.appcompat.app.AlertDialog // Declare as AlertDialog


    private var printing: Printing? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_street_parking)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        //Initialize messageBoxView here
        messageBoxViewTimeOut = LayoutInflater.from(this).inflate(R.layout.payment_offline, null)
        messageBoxView = LayoutInflater.from(this).inflate(R.layout.message_box, null)
        messageBoxViewFailed = LayoutInflater.from(this).inflate(R.layout.payment_unsuccesfull, null)
        messageBoxViewPaid = LayoutInflater.from(this).inflate(R.layout.payment_recieved, null)

        tvSendPush.setOnClickListener {
            if (edPlate.text.isEmpty()) {
                Toast.makeText(this, "Please input Number Plate", Toast.LENGTH_LONG).show()
            } else {
                if (edPhone.text.isEmpty()) {
                    Toast.makeText(this, "Please input Number Plate", Toast.LENGTH_LONG).show()
                } else {
                    tvSendPush.visibility = View.GONE
                    tvSendPushDisabled.visibility = View.VISIBLE
                    generateParkingPayment()
                }
            }
        }
        edPlate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (pushButton) {
                    runOnUiThread {
                        tvSendPush.setText("SEND PAYMENT REQUEST")
                        tvSendPush.setOnClickListener {
                            if (edPlate.text.isEmpty()) {
                                Toast.makeText(
                                    this@StreetParking,
                                    "Please input Number Plate",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                if (edPhone.text.isEmpty()) {
                                    Toast.makeText(
                                        this@StreetParking,
                                        "Please input Number Plate",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    tvSendPush.visibility = View.GONE
                                    tvSendPushDisabled.visibility = View.VISIBLE
                                    generateParkingPayment()
                                }
                            }
                        }
                    }
                    pushButton = false
                }

            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        imageClose.setOnClickListener { finish() }
        getIncomeTypes()

        /* //Bluetooth printer
         if (Printooth.hasPairedPrinter())
             printing = Printooth.printer()
         initListeners()*/

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    private fun getIncomeTypes() {

        val formData = listOf(
            "function" to "getIncomeTypes",
            "incomeTypePrefix" to "PKN",

            )
        executeRequest(formData, biller, object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if (response.success) {

                    runOnUiThread {

                        for (data in response.data.incomeTypes) {
                            arrayList.add(data.incomeTypeDescription)
                        }

                        //Spinner
                        val adapters = ArrayAdapter<String>(
                            applicationContext,
                            R.layout.simple_spinner_dropdown_item,
                            arrayList
                        )
                        adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        spinnerIncomeType.adapter = adapters
                        spinnerIncomeType.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    p0: AdapterView<*>?,
                                    p1: View?,
                                    postion: Int,
                                    p3: Long
                                ) {
                                    incomeTypeDescription = response.data.incomeTypes[postion].incomeTypeDescription
                                    spinnerFeeAndCharges(response.data.incomeTypes[postion].incomeTypeId)
                                }

                                override fun onNothingSelected(p0: AdapterView<*>?) {

                                }
                            }
                    }

                } else {
                    runOnUiThread {
                        Toast.makeText(
                            this@StreetParking,
                            response.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }

            }

        })
    }
    private fun spinnerFeeAndCharges(incomeTypeId: String) {
        val formData = listOf(
            "function" to "getFeesAndCharges",
            "incomeTypeId" to incomeTypeId,
        )
        executeRequest(formData, biller, object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                runOnUiThread {
                    runOnUiThread { tvAmount.text = "" }
                    arrayList2.clear()
                    val adapters = ArrayAdapter<String>(
                        applicationContext,
                        R.layout.simple_spinner_dropdown_item,
                        arrayList2
                    )
                    adapters.clear()
                    if (response.success) {
                        runOnUiThread {

                            for (data in response.data.feesAndCharges) {
                                arrayList2.add(data.feeDescription + " - " + data.zone)
                            }

                            //Spinner
                            adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                            spinnerFeeAndCharges.adapter = adapters
                            spinnerFeeAndCharges.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        p0: AdapterView<*>?,
                                        p1: View?,
                                        postion: Int,
                                        p3: Long
                                    ) {
                                        feeDescription = response.data.feesAndCharges[postion].feeDescription
                                        amount = response.data.feesAndCharges[postion].unitFeeAmount
                                        feeId = response.data.feesAndCharges[postion].feeId

                                        runOnUiThread { tvAmount.text = "KES $amount" }

                                    }

                                    override fun onNothingSelected(p0: AdapterView<*>?) {

                                    }
                                }
                        }
                    } else {
                        spinnerFeeAndCharges.adapter = null
                        Toast.makeText(this@StreetParking, response.message, Toast.LENGTH_LONG)
                            .show()
                    }
                }

            }

        })
    }
    private fun generateParkingPayment() {

        showMessageBox()
        tv_message.text = "Generating bill please wait..$incomeTypeDescription $feeDescription"
        (messageBoxView as View?)!!.tv_message.text =
            "Generating bill please wait..$incomeTypeDescription $feeDescription"

        val formData = listOf(
            "function" to "generateParkingPayment",
            "numberPlate" to edPlate.text.toString(),
            "feeId" to feeId,
            "zone" to getValue(this, "zone").toString(),
            "names" to getValue(this, "names").toString(),
            "phoneNumber" to getValue(this, "phoneNumber").toString(),
            "idNo" to getValue(this, "idNo").toString(),
            "username" to getValue(this, "username").toString(),
            "subCountyID" to getValue(this, "subCountyID").toString(),
            "subCountyName" to getValue(this, "subCountyName").toString(),
            "wardID" to getValue(this, "wardID").toString(),
            "wardName" to getValue(this, "wardName").toString(),
            "customerPhoneNumber" to edPhone.text.toString(),
            "description" to ""
        )

        executeRequest(formData, parking, object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if (response.success) {
                    runOnUiThread {
                        tv_message.text = "Bill generated success.."
                        (messageBoxView as View?)!!.tv_message.text = "Bill generated success.."
                    }



                    customerPayBillOnline(
                        response.data.billGenerated.billNo,
                        response.data.billGenerated.payBillNo,
                        response.data.billGenerated.amount
                    )

                } else {
                    runOnUiThread {
                        tv_message.text = response.message
                        (messageBoxView as View?)!!.tv_message.text = response.message
                        tvSendPush.visibility = View.VISIBLE
                        tvSendPushDisabled.visibility = View.GONE
                        messageBoxInstance.dismiss()
                    }
                }

            }

        })


    }

    //payment Process
    private fun customerPayBillOnline(accountReference: String, payBillNumber: String, amount: String) {

        checkPayment = true

        Timer().schedule(30000) {
            runOnUiThread {
                if(showTimeout){
                    pushButton = true
                    checkPayment = false

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
                        checkPayment(accountReference)
                    }

                } else {
                    runOnUiThread {
                        tvSendPush.visibility = View.VISIBLE
                        tvSendPushDisabled.visibility = View.GONE
                        tv_message.text = response.message
                    }

                }

            }

        })

    }
    fun checkPayment(accountReference: String) {

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

                                save(this@StreetParking, "description", edPlate.text.toString())
                                save(
                                    this@StreetParking,
                                    "transaction_code",
                                    response.data.push.transaction_code
                                )
                                save(this@StreetParking, "amount", response.data.push.amount)
                                save(this@StreetParking, "payer_phone", response.data.push.account_from)
                                save(this@StreetParking, "ref", response.data.push.ref)
                                save(this@StreetParking, "payer_names", response.data.transaction.names)
                                save(this@StreetParking, "date", response.data.transaction.date)

                                //v_transaction: String,payer: String,amount: String, des: String,category:String
                                showMessageBoxPayment(
                                    response.data.transaction.transaction_code,
                                    response.data.transaction.names,
                                    response.data.transaction.amount,
                                    "${feeDescription} ${edPhone.text}",
                                    incomeTypeDescription
                                )
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
                                TimeUnit.SECONDS.sleep(2L)
                                checkPayment(accountReference)
                            }

                        }
                        else {
                            runOnUiThread {
                                messageBoxInstance.dismiss()
                                tv_message.text = response.data.push.message
                                tvSendPush.visibility = View.VISIBLE
                                tvSendPushDisabled.visibility = View.GONE
                                showMessageBoxPaymentFail(response.data.push.message)

                            }
                        }

                    } else {
                        if(checkPayment){
                            runOnUiThread { tv_message.text = "Waiting for payment.." }
                            TimeUnit.SECONDS.sleep(2L)
                            checkPayment(accountReference)
                        }
                    }
                }

            })
        }

    }
    private fun showMessageBoxTimeOut( accountReference: String, payBillNumber: String, amount: String) {
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

        (messageBoxViewFailed as View?)!!.tv_close.setOnClickListener { messageBoxInstanceFailed.dismiss() }
        (messageBoxViewFailed as View?)!!.resend.setOnClickListener {
            messageBoxInstanceFailed.dismiss()
            generateParkingPayment()
        }

    }

    //printer services starts here
    fun printReceipt() {
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
                Toast.makeText(this@StreetParking, "Connecting with printer", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun printingOrderSentSuccessfully() {
                Toast.makeText(this@StreetParking, "Order sent to printer", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun connectionFailed(error: String) {
                Toast.makeText(this@StreetParking, "Failed to connect printer", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onError(error: String) {
                Toast.makeText(this@StreetParking, error, Toast.LENGTH_SHORT).show()
            }

            override fun onMessage(message: String) {
                Toast.makeText(this@StreetParking, "Message: $message", Toast.LENGTH_SHORT).show()
            }

            override fun disconnected() {
                Toast.makeText(this@StreetParking, "Disconnected Printer", Toast.LENGTH_SHORT)
                    .show()
            }

        }
    }

    private fun printDetails() {
        val printables = getSomePrintables()
        printing?.print(printables)
    }

    /* Customize your printer here with text, logo and QR code */
    private fun getSomePrintables() = java.util.ArrayList<Printable>().apply {

        val title = "\n\nOFFICIAL RECIEPT\n\n"
        add(
            TextPrintable.Builder()
                .setText(title)
                .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                // .setNewLinesAfter(1)
                .build()
        )

        val title2 = when (BuildConfig.FLAVOR) {
            "homabay" -> "COUNTY GOVERNMENT OF HOMABAY\n\n#\n\n\n"
            "meru" -> "COUNTY GOVERNMENT OF MERU\n\n#\n\n\n"
            else -> "COUNTY GOVERNMENT OF UNKNOWN\n\n#\n\n\n"
        }


        add(
            TextPrintable.Builder()
                .setText(title2)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .build()
        )


        val bmp = BitmapFactory.decodeResource(resources, R.drawable.print_county_logo)
        val argbBmp = bmp.copy(Bitmap.Config.ARGB_8888, false)
        val scaledLogo = Bitmap.createScaledBitmap(argbBmp, 145, 180, true)
        add(
            ImagePrintable.Builder(scaledLogo)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .build()
        )

        val transactioncode = getValue(this@StreetParking, "transaction_code")
        val amount = getValue(this@StreetParking, "amount")
        val ref = getValue(this@StreetParking, "ref")
        val username = getValue(this@StreetParking, "username")
        val names = getValue(this@StreetParking, "payer_names")
        val phone = getValue(this@StreetParking, "payer_phone")
        val incomeTypeDescription =
            getValue(this@StreetParking, "incomeTypeDescription")?.capitalize()
        val description = getValue(this@StreetParking, "description")
        val date = getValue(this@StreetParking, "date")


        /* val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
         val outputFormat = SimpleDateFormat("EEE dd MMM yy hh:mma", Locale.getDefault())
         val date = input?.let { inputFormat.parse(it) }
         val humanDate = date?.let { outputFormat.format(it) }*/
        val humanDate = date
        val zone = getValue(this@StreetParking, "zone")
        val message =
            "\n\nFor: $description #Mpesa\nTransaction Code: $transactioncode\nAmount: KES $amount\nPayer: $names\nDate: $humanDate\nPrinted By: $username at $zone\n"

        add(
            TextPrintable.Builder()
                .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                .setText(message)
                // .setNewLinesAfter(1)
                .build()
        )

        val message2 =
            "Payment Code:$transactioncode, Amount:$amount, Payer:$names, Date: $humanDate, Printed By: $username"

        val qr: Bitmap = QRCode.from(message2)
            .withSize(200, 200).bitmap()
        add(
            ImagePrintable.Builder(qr)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .build()
        )


        val footer = when (BuildConfig.FLAVOR) {
            "homabay" -> "Lipa Ushuru Tujenge\n\n#EndlessPotential\n\n\n\n\n\n"
            "meru" -> "Lipa Ushuru Tujenge\n\n#Making Meru Happy\n\n\n\n\n\n\n"
            else -> "Lipa Ushuru Tujenge\n\n#\n\n\n\n\n"
        }

        add(
            TextPrintable.Builder()
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .setText(footer)
                .build()
        )


    }

    /* Inbuilt activity to pair device with printer or select from list of pair bluetooth devices */
    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == ScanningActivity.SCANNING_FOR_PRINTER && result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
//            val intent = result.data
                printDetails()
            }
        }
}
