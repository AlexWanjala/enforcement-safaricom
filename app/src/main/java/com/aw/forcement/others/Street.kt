package com.aw.forcement.others

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
import android.os.Bundle
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import com.aw.forcement.R
import com.aw.forcement.ocr.OcrDetectorProcessor
import com.aw.forcement.ocr.OcrGraphic
import com.aw.forcement.ocr.camera.CameraSource
import com.aw.forcement.ocr.camera.CameraSourcePreview
import com.aw.forcement.ocr.camera.GraphicOverlay
import com.aw.passanger.api.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_street.*
import kotlinx.android.synthetic.main.clamp.*
import kotlinx.android.synthetic.main.clamp.view.*
import kotlinx.android.synthetic.main.clamp.view.imageIcon
import kotlinx.android.synthetic.main.clamp.view.tv_close
import kotlinx.android.synthetic.main.clamp.view.tv_message_unpaid
import kotlinx.android.synthetic.main.clamp.view.tv_title
import kotlinx.android.synthetic.main.message_box.view.*
import kotlinx.android.synthetic.main.message_box.view.tv_message
import kotlinx.android.synthetic.main.pay.*
import kotlinx.android.synthetic.main.pay.view.*
import kotlinx.android.synthetic.main.payment_offline.view.*
import kotlinx.android.synthetic.main.payment_recieved.view.*
import kotlinx.android.synthetic.main.payment_unsuccesfull.view.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule

class Street : AppCompatActivity() {

    lateinit var messageBoxViewClamp : View
    lateinit var messageBoxInstanceClamp: androidx.appcompat.app.AlertDialog // Declare as AlertDialog

    private val arrayList = ArrayList<String>()
    private val arrayList2 = ArrayList<String>()

    private val arrayListParking = ArrayList<String>()
    private val arrayListParkingFees = ArrayList<String>()
    private val arrayListReasons = ArrayList<String>()
    lateinit var amount: String
    lateinit var feeIdClamp: String
    lateinit var feeIdParking: String

    lateinit var feeDescription: String
    lateinit var incomeTypeDescription : String
    lateinit var description : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_street)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        //Initialize messageBoxView here
        messageBoxViewClamp = LayoutInflater.from(this).inflate(R.layout.clamp, null)
        messageBoxViewTimeOut = LayoutInflater.from(this).inflate(R.layout.payment_offline, null)
        messageBoxView = LayoutInflater.from(this).inflate(R.layout.message_box, null)
        messageBoxViewFailed = LayoutInflater.from(this).inflate(R.layout.payment_unsuccesfull, null)
        messageBoxViewPaid = LayoutInflater.from(this).inflate(R.layout.payment_recieved, null)
        messageBoxViewPay = LayoutInflater.from(this).inflate(R.layout.pay, null)


        tvSubmit.setOnClickListener {
            if(edPlate.text.toString().isEmpty()){

                Thread {
                    tts!!.speak("Please Scan or enter plate number", TextToSpeech.QUEUE_ADD, null, "DEFAULT")
                }.start()

            }else{
                getParking(edPlate.text.toString().replace("",""))
            }
        }

        btnClamp.setOnClickListener {
            if(edPlate.text.isEmpty()){
                Toast.makeText(this,"Enter Plate Number",Toast.LENGTH_LONG).show()
            }else{
                showMessageBoxClamp()
            }

        }

        switchCamera.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                cameraView.visibility = View.VISIBLE
                //startCameraSource()
            } else {
                cameraView.visibility = View.GONE
                /* if (preview != null) {
                     preview!!.stop()
                 }*/
            }
        }


        if (intent.hasExtra("numberPlate")) {
            val numberPlate = intent.getStringExtra("numberPlate")
            getParking(numberPlate.toString().replace("",""))
        }




        initOCR()
    }

    fun humanDate(input: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = inputFormat.parse(input)
        val outputFormat = SimpleDateFormat("dd MMM yyyy 'AT' hh:mm a")
        val output = outputFormat.format(date)
        return output
    }

    private fun showMessageBoxClamp(){

        // Check if messageBoxView has a parent
        if (messageBoxViewClamp.parent != null) {
            // Remove messageBoxView from its parent
            (messageBoxViewClamp.parent as ViewGroup).removeView(messageBoxViewClamp)
        }

        val messageBoxBuilder = androidx.appcompat.app.AlertDialog.Builder(this).setView(
            messageBoxViewClamp as View?
        )
        messageBoxInstanceClamp = messageBoxBuilder.show()
        messageBoxInstanceClamp.setCanceledOnTouchOutside(false)

        messageBoxViewClamp.btn_clamp.setOnClickListener {
            generatePenalty()
            messageBoxInstanceClamp.dismiss()
        }
        messageBoxViewClamp.tv_close.setOnClickListener {

            messageBoxInstanceClamp.dismiss()
        }

        val clampingDuration = getValue(this,"clampingDuration")
        messageBoxViewClamp.tv_clamping_duration.text ="If the customer doesn't pay within ${clampingDuration} minutes after we send them the information, then clamping fees will be applicable."

        getIncomeTypes()
        getIncomeTypesParking()
        getClampReasons()

    }
    private fun getParking(plateNumber : String){
       // progress_circular.visibility = View.VISIBLE

        val formData = listOf(
            "function" to "getParking",
            "numberPlate" to plateNumber.trim(),
            "latitude" to getValue(this,"latitude").toString(),
            "longitude" to getValue(this,"longitude").toString(),
            "idNo" to getValue(this,"idNo").toString(),
            "username" to getValue(this,"username").toString(),
            "addressString" to getValue(this,"addressString").toString(),
            "deviceId" to getDeviceIdNumber(this)
        )
        executeRequest(formData, parking,object : CallBack {
            override fun onSuccess(result: String?) {
              //  runOnUiThread {  progress_circular.visibility = View.GONE }
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){
                    runOnUiThread {
                        edPlate.setText(response.data.parking.numberPlate)
                        plate.text = response.data.parking.numberPlate
                        tv_for.text = response.data.parking.category

                        if (response.data.parking.status!="PAID"){
                            tv_status.setTextColor(Color.RED)
                            tv_amount.setTextColor(Color.RED)
                            tv_amount.text = "KES "+response.data.parking.billBalance

                            if(response.data.parking.status.equals("CLAMPED") || response.data.parking.status.equals("AREAS & PENALTY")){
                                btnClamp.text = "PAY"
                                btnClamp.setOnClickListener { showMessageBoxPay(response.data.parking.billBalance,response.data.parking.parkingCode,response.data.payBill.shortCode) }

                            }


                        }else{

                                tv_amount.text = "KES "+response.data.parking.receiptAmount
                                tv_status.setTextColor(Color.parseColor("#09754E"))


                        }


                        tv_for.text = response.data.parking.category+" for "+ response.data.parking.duration
                        tv_status.text = response.data.parking.status

                        if(response.data.parking.startDate.isNotEmpty())
                         date_paid.text = humanDate(response.data.parking.startDate)

                        if(response.data.parking.endDate.isNotEmpty())
                            date_expiry.text = humanDate(response.data.parking.endDate)

                        tv_last.text = response.data.parking.zone
                      /*  plate.text = response.data.parking.numberPlate.trim()
                        vehicleCategory.text = response.data.parking.category.trim()
                        zone.text =  response.data.parking.zone.trim()
                        street.text = getValue(this@Street,"addressString").toString().trim()
                        duration.text = response.data.parking.duration.trim()
                        status.text = response.data.parking.status.trim()
                        if(response.data.parking.status.trim()=="PAID"){
                            status.setTextColor(Color.parseColor("#12a637"))
                        }else{
                            status.setTextColor(Color.RED)
                        }*/
                      tts!!.speak(response.message, TextToSpeech.QUEUE_ADD, null, "DEFAULT")
                    }



                }else{
                    runOnUiThread {

                    }

                }

            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@Street,result, Toast.LENGTH_LONG).show()
                }
            }

        })

    }

    private fun generatePenalty (){

        val formData = listOf(
            "function" to "generatePenalty",
            "feeIdParking" to feeIdParking,
            "feeIdClamp" to feeIdClamp,
            "numberPlate" to edPlate.text.toString(),
            "zone" to getValue(this,"zone").toString(),
            "names" to getValue(this,"names").toString(),
            "phoneNumber" to getValue(this,"phoneNumber").toString(),
            "idNo" to getValue(this,"idNo").toString(),
            "username" to getValue(this,"username").toString(),
            "subCountyID" to getValue(this,"subCountyID").toString(),
            "subCountyName" to getValue(this,"subCountyName").toString(),
            "wardID" to getValue(this,"wardID").toString(),
            "wardName" to getValue(this,"wardName").toString(),
            "lat" to getValue(this,"latitude").toString(),
            "lng" to getValue(this,"longitude").toString(),
            "names" to getValue(this,"username").toString(),
            "address" to getValue(this,"address").toString(),
            "customerPhoneNumber" to "",
            "description" to description,
            "deviceId" to getDeviceIdNumber(this)
        )
        executeRequest(formData, parking,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    runOnUiThread {
                        runOnUiThread {
                            Toast.makeText(this@Street,response.message, Toast.LENGTH_LONG).show()
                            getParking( edPlate.text.toString())
                        }

                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@Street,response.message, Toast.LENGTH_LONG).show()}

                }

            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@Street,result, Toast.LENGTH_LONG).show()
                }
            }

        })
    }
    private fun getIncomeTypesParking(){

        val formData = listOf(
            "function" to "getIncomeTypes",
            "incomeTypePrefix" to "PKN",
            "deviceId" to getDeviceIdNumber(this)

            )
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    runOnUiThread {

                        for(data in response.data.incomeTypes){
                            arrayListParking.add(data.incomeTypeDescription)
                        }

                        //Spinner
                        val adapters = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item,arrayListParking)
                        adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        messageBoxViewClamp.spinnerIncomeType.adapter = adapters
                        messageBoxViewClamp.spinnerIncomeType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {
                                incomeTypeDescription = response.data.incomeTypes[postion].incomeTypeDescription
                                spinnerFeeAndChargesParking(response.data.incomeTypes[postion].incomeTypeId)
                            }
                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }
                        }
                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@Street,response.message, Toast.LENGTH_LONG).show()}

                }

            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@Street,result, Toast.LENGTH_LONG).show()
                }
            }

        })
    }
    private fun spinnerFeeAndChargesParking (incomeTypeId: String){
        val formData = listOf(
            "function" to "getFeesAndCharges",
            "incomeTypeId" to incomeTypeId,
            "deviceId" to getDeviceIdNumber(this)
        )
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                runOnUiThread {
                   /* runOnUiThread { tvAmount.text = "" }*/
                    arrayListParkingFees.clear()
                    val adapters = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item,arrayListParkingFees)
                    adapters.clear()
                    if(response.success){
                        runOnUiThread {

                            for(data in response.data.feesAndCharges){
                                arrayListParkingFees.add(data.feeDescription +" - "+ data.zone)
                            }

                            //Spinner
                            adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                            messageBoxViewClamp.spinnerFeeAndCharges.adapter = adapters
                            messageBoxViewClamp.spinnerFeeAndCharges.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {
                                    feeDescription =  response.data.feesAndCharges[postion].feeDescription
                                    amount = response.data.feesAndCharges[postion].unitFeeAmount
                                    feeIdParking = response.data.feesAndCharges[postion].feeId


                                }
                                override fun onNothingSelected(p0: AdapterView<*>?) {

                                }
                            }
                        }
                    }
                    else{
                        spinnerFeeAndCharges.adapter = null
                        Toast.makeText(this@Street,response.message, Toast.LENGTH_LONG).show() }
                }

            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@Street,result, Toast.LENGTH_LONG).show()
                }
            }

        })
    }

    private fun getClampReasons (){

        val formData = listOf(
            "function" to "getClampReasons",
        )
        executeRequest(formData, parking,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    runOnUiThread {
                        for (data in response.data.clampreasons) {
                            arrayListReasons.add(data.reason)
                        }

                        val adapters = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item, arrayListReasons)
                        adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        messageBoxViewClamp.spinnerClamping.adapter = adapters

                        messageBoxViewClamp.spinnerClamping.adapter = adapters
                        messageBoxViewClamp.spinnerClamping.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {
                                val clampReasons = response.data.clampreasons
                                if (clampReasons.size > postion) {
                                    val reason = clampReasons[postion].reason

                                    description = reason

                                    // Check if street is not null before calling setDescription

                                } else {
                                    // Handle the case where clampreasons or clampreasons.size is null or invalid
                                }
                            }

                            override fun onNothingSelected(p0: AdapterView<*>?) {
                            }
                        }
                    }


                }else{
                    runOnUiThread {  Toast.makeText(this@Street,response.message, Toast.LENGTH_LONG).show()}

                }

            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@Street,result, Toast.LENGTH_LONG).show()
                }
            }

        })
    }
    private fun getIncomeTypes (){

        val formData = listOf(
            "function" to "getIncomeTypes",
            "incomeTypePrefix" to "CLMP",
            "deviceId" to getDeviceIdNumber(this)

        )
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    runOnUiThread {

                        for(data in response.data.incomeTypes){
                            arrayList.add(data.incomeTypeDescription)
                        }

                        //Spinner
                        val adapters = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item,arrayList)
                        adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        messageBoxViewClamp.spinnerIncomeTypeClamp.adapter = adapters
                        messageBoxViewClamp.spinnerIncomeTypeClamp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {
                                spinnerFeeAndCharges(response.data.incomeTypes[postion].incomeTypeId)
                                incomeTypeDescription = response.data.incomeTypes[postion].incomeTypeDescription
                            }
                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }
                        }
                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@Street,response.message, Toast.LENGTH_LONG).show()}

                }

            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@Street,result, Toast.LENGTH_LONG).show()
                }
            }

        })
    }
    private fun spinnerFeeAndCharges (incomeTypeId: String){
        val formData = listOf(
            "function" to "getFeesAndCharges",
            "incomeTypeId" to incomeTypeId,
            "deviceId" to getDeviceIdNumber(this)
        )
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                runOnUiThread {  arrayList2.clear()
                    val adapters = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item,arrayList2)
                    adapters.clear()
                    if(response.success){
                        runOnUiThread {
                            for(data in response.data.feesAndCharges){
                                arrayList2.add("${data.feeDescription} KES ${data.unitFeeAmount}")
                            }

                            //Spinner
                            adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                            messageBoxViewClamp.spinnerFeeAndChargesClamp.adapter = adapters
                            messageBoxViewClamp.spinnerFeeAndChargesClamp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {
                                    //  response.data.feesAndCharges[postion].feeId
                                    amount = response.data.feesAndCharges[postion].unitFeeAmount
                                    feeIdClamp = response.data.feesAndCharges[postion].feeId
                                    save(this@Street,"description",response.data.feesAndCharges[postion].feeDescription)
                                    runOnUiThread {
                                        feeDescription =  response.data.feesAndCharges[postion].feeDescription
                                      /*  tvUnits.text =  response.data.feesAndCharges[postion].feeDescription
                                        tvAmount.text ="KES "+amount*/
                                    }
                                }
                                override fun onNothingSelected(p0: AdapterView<*>?) {

                                }
                            }
                        }
                    }
                    else{
                        messageBoxViewClamp.spinnerFeeAndChargesClamp.adapter = null
                        Toast.makeText(this@Street,response.message, Toast.LENGTH_LONG).show() }
                }

            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@Street,result, Toast.LENGTH_LONG).show()
                }
            }

        })
    }






    //Payment Process
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

    lateinit var messageBoxViewPay : View
    lateinit var messageBoxInstancePay: androidx.appcompat.app.AlertDialog // Declare as AlertDialog

    lateinit var timer: TimerTask
    private fun stopTimer() {
        timer.cancel()
    }
    private fun customerPayBillOnline(accountReference: String, payBillNumber: String, amount: String) {

        checkPayment = true

        timer = Timer().schedule(65000) {
            runOnUiThread {
                if(showTimeout){
                    pushButton = true
                    checkPayment = false

                    if (showTimeout){
                       /* tv_message.text =""
                        tvSendPush.visibility = View.VISIBLE
                        tvSendPushDisabled.visibility = View.GONE*/

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
            messageBoxViewPay.tv_message.text = "Sending Payment Request.."
            (messageBoxView as View?)!!.tv_message.text = "Sending Payment Request.."

        }
        val formData = listOf(
            "function" to "customerPayBillOnline",
            "payBillNumber" to payBillNumber,
            "amount" to amount,
            "accountReference" to accountReference,
            "transactionDesc" to accountReference,
            "phoneNumber" to messageBoxViewPay.edPhone.text.toString(),
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
                        /*tvSendPush.visibility = View.VISIBLE
                        tvSendPushDisabled.visibility = View.GONE
                        tv_message.text = response.message*/
                    }

                }

            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@Street,result, Toast.LENGTH_LONG).show()
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

                               /* tvSendPush.visibility = View.VISIBLE
                                tvSendPushDisabled.visibility = View.GONE
                                tv_message.text = "Payment Received #${response.data.push.transaction_code} KES ${response.data.push.amount}"
*/
                                save(this@Street, "description", edPlate.text.toString())
                                save(
                                    this@Street,
                                    "transaction_code",
                                    response.data.push.transaction_code
                                )
                                save(this@Street, "amount", response.data.push.amount)
                                save(this@Street, "payer_phone", response.data.push.account_from)
                                save(this@Street, "ref", response.data.push.ref)
                                save(this@Street, "payer_names", response.data.transaction.names)
                                save(this@Street, "date", response.data.transaction.date)

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

                               // tvSendPush.setText("Print")
                               // tvSendPush.setOnClickListener { printReceipt() }
                                pushButton = true
                                //printReceipt()

                            }


                        }
                        else if (response.data.push.callback_returned == "PENDING") {

                            if(checkPayment){
                                runOnUiThread {
                                 //   tv_message.text = "Waiting for payment.."
                                    (messageBoxView as View?)!!.tv_message.text = "Waiting for payment.."

                                }
                                TimeUnit.SECONDS.sleep(3L)
                                checkPayment(accountReference,payBillNumber,amount)
                            }

                        }
                        else {
                            runOnUiThread {
                                messageBoxInstance.dismiss()
                              //  tv_message.text = response.data.push.message
                             //   tvSendPush.visibility = View.VISIBLE
                            //    tvSendPushDisabled.visibility = View.GONE
                                showMessageBoxPaymentFail(response.data.push.message,accountReference,payBillNumber,amount)

                            }
                        }

                    } else {
                        if(checkPayment){
                          //  runOnUiThread { tv_message.text = "Waiting for payment.." }
                            TimeUnit.SECONDS.sleep(3L)
                            checkPayment(accountReference,payBillNumber,amount)
                        }
                    }
                }
                override fun onFailure(result: String?) {
                    runOnUiThread {
                        Toast.makeText(this@Street,result, Toast.LENGTH_LONG).show()
                    }
                }

            })
        }

    }
    private fun showMessageBoxPay(amount: String,accNo: String,payBillNumber: String) {
        showTimeout = false
        // Check if messageBoxView has a parent
        if (messageBoxViewPay.parent != null) {
            // Remove messageBoxView from its parent
            (messageBoxViewPay.parent as ViewGroup).removeView(messageBoxViewPay)
        }
        val messageBoxBuilder = androidx.appcompat.app.AlertDialog.Builder(this).setView(messageBoxViewPay as View?)
        messageBoxInstancePay = messageBoxBuilder.show()
        messageBoxInstancePay.setCanceledOnTouchOutside(false)
        messageBoxViewPay.tvAmount.text ="KES ${amount}"
        messageBoxViewPay.tv_close_button.setOnClickListener {messageBoxInstancePay.dismiss() }
        messageBoxViewPay.tvSendPush.setOnClickListener {
            showMessageBox()
            customerPayBillOnline(
                accNo,
                payBillNumber,
                amount
            )
        }



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
        if (!isFinishing && !isDestroyed) {
            messageBoxInstanceFailed = messageBoxBuilder.show()
        }


        // Check if initialized before using
        if (::messageBoxInstanceFailed.isInitialized) {
            messageBoxInstanceFailed.setCanceledOnTouchOutside(false)
        }

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
           Toast.makeText(this,"to generate Bill I guess to get parking again",Toast.LENGTH_LONG).show()
        }

    }




    //OCR STARTS HERE
    private fun initOCR(){

       try {

           preview = findViewById<View>(R.id.preview) as CameraSourcePreview
           graphicOverlay = findViewById<View>(R.id.graphicOverlay) as GraphicOverlay<OcrGraphic?>

           // Set good defaults for capturing text.
           val autoFocus = true
           val useFlash = false

           // Check for the camera permission before accessing the camera.  If the
           // permission is not granted yet, request permission.
           val rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
           if (rc == PackageManager.PERMISSION_GRANTED) {
               createCameraSource(autoFocus, useFlash)
           } else {
               requestCameraPermission()
           }
           gestureDetector = GestureDetector(this, CaptureGestureListener())
           scaleGestureDetector = ScaleGestureDetector(this, ScaleListener())

         //  Snackbar.make(graphicOverlay!!, "Tap to Speak. Pinch/Stretch to zoom", Snackbar.LENGTH_LONG).show()
           // Set up the Text To Speech engine.
           val listener = TextToSpeech.OnInitListener { status ->
               if (status == TextToSpeech.SUCCESS) {
                   Log.d("OnInitListener", "Text to speech engine started successfully.")
                   tts!!.language = Locale.US
               } else {
                   Log.d("OnInitListener", "Error starting the text to speech engine.")
               }
           }
           tts = TextToSpeech(this.applicationContext, listener)
       } catch (ex: NullPointerException) {
       }

    }
    private var cameraSource: CameraSource? = null
    private var preview: CameraSourcePreview? = null
    private var graphicOverlay: GraphicOverlay<OcrGraphic?>? = null

    // Helper objects for detecting taps and pinches.
    private var scaleGestureDetector: ScaleGestureDetector? = null
    private var gestureDetector: GestureDetector? = null

    // A TextToSpeech engine for speaking a String value.
    private var tts: TextToSpeech? = null
    private fun goToSettings() {
        val myAppSettings = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse(
                "package:$packageName"
            )
        )
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT)
        myAppSettings.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(myAppSettings)
    }

    /**
     * Initializes the UI and creates the detector pipeline.
     */


    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private fun requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission")
        val permissions = arrayOf(Manifest.permission.CAMERA)
        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CAMERA
            )
        ) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM)
            return
        }
        val thisActivity: Activity = this
        val listener = View.OnClickListener {
            ActivityCompat.requestPermissions(
                thisActivity, permissions,
                RC_HANDLE_CAMERA_PERM
            )
        }
        Snackbar.make(
            graphicOverlay!!, "permission_camera_rationale",
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction("ok", listener)
            .show()
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        val b = scaleGestureDetector!!.onTouchEvent(e)
        val c = gestureDetector!!.onTouchEvent(e)
        return b || c || super.onTouchEvent(e)
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the ocr detector to detect small text samples
     * at long distances.
     *
     * Suppressing InlinedApi since there is a check that the minimum version is met before using
     * the constant.
     */
    @SuppressLint("InlinedApi")
    private fun createCameraSource(autoFocus: Boolean, useFlash: Boolean) {
        val context = applicationContext

        // A text recognizer is created to find text.  An associated multi-processor instance
        // is set to receive the text recognition results, track the text, and maintain
        // graphics for each text block on screen.  The factory is used by the multi-processor to
        // create a separate tracker instance for each text block.
        val textRecognizer = TextRecognizer.Builder(context).build()
        textRecognizer.setProcessor(OcrDetectorProcessor(graphicOverlay!!))
        if (!textRecognizer.isOperational) {
            // Note: The first time that an app using a Vision API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any text,
            // barcodes, or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
            Log.w(TAG, "Detector dependencies are not yet available.")

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            val lowstorageFilter = IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW)
            val hasLowStorage = registerReceiver(null, lowstorageFilter) != null
            if (hasLowStorage) {
                Toast.makeText(this, "low_storage_error", Toast.LENGTH_LONG).show()
                Log.w(TAG, "low_storage_error")
            }
        }

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the text recognizer to detect small pieces of text.
        cameraSource = CameraSource.Builder(applicationContext, textRecognizer)
            .setFacing(CameraSource.CAMERA_FACING_BACK)
            .setRequestedPreviewSize(1280, 1024)
            .setRequestedFps(2.0f)
            .setFlashMode(if (useFlash) Camera.Parameters.FLASH_MODE_TORCH else null)
            .setFocusMode(if (autoFocus) Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO else null)
            .build()
    }

    /**
     * Restarts the camera.
     */
    override fun onResume() {
        super.onResume()
        startCameraSource()
    }

    /**
     * Stops the camera.
     */
    override fun onPause() {
        super.onPause()
        if (preview != null) {
            preview!!.stop()
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    override fun onDestroy() {
        super.onDestroy()
       /* if (preview != null) {
            preview!!.release()
        }*/
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on [.requestPermissions].
     *
     *
     * **Note:** It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     *
     *
     * @param requestCode  The request code passed in [.requestPermissions].
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     * which is either [PackageManager.PERMISSION_GRANTED]
     * or [PackageManager.PERMISSION_DENIED]. Never null.
     * @see .requestPermissions
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: $requestCode")
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }
        if (grantResults.size != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source")
            // we have permission, so create the camerasource
            val autoFocus = intent.getBooleanExtra(AutoFocus, true)
            val useFlash = intent.getBooleanExtra(UseFlash, false)
            createCameraSource(autoFocus, useFlash)
            return
        }
        Log.e(
            TAG, "Permission not granted: results len = " + grantResults.size +
                    " Result code = " + if (grantResults.size > 0) grantResults[0] else "(empty)"
        )
        val listener = DialogInterface.OnClickListener { dialog, id -> finish() }
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Multitracker sample")
            .setMessage("no_camera_permission")
            .setPositiveButton("ok", listener)
            .show()
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    @Throws(SecurityException::class)
    private fun startCameraSource() {
        // check that the device has play services available.
        val code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
            applicationContext
        )
        if (code != ConnectionResult.SUCCESS) {
            val dlg = GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS)
            dlg!!.show()
        }
        if (cameraSource != null) {
            try {
                preview!!.start(cameraSource, graphicOverlay)
            } catch (e: IOException) {
                Log.e(TAG, "Unable to start camera source.", e)
                cameraSource!!.release()
                cameraSource = null
            }
        }
    }
//Probox
    /**
     * onTap is called to speak the tapped TextBlock, if any, out loud.
     *
     * @param rawX - the raw position of the tap
     * @param rawY - the raw position of the tap.
     * @return true if the tap was on a TextBlock
     */
    private fun onTap(rawX: Float, rawY: Float): Boolean {
        val graphic = graphicOverlay!!.getGraphicAtLocation(rawX, rawY)
        var text: TextBlock? = null
        if (graphic != null) {
            text = graphic.textBlock
            if (text != null && text.value != null) {
                Log.d(TAG, "text data is being spoken! " + text.value)
                // Speak the string.
                tts!!.speak(text.value, TextToSpeech.QUEUE_ADD, null, "DEFAULT")
                Toast.makeText(this, text.value.toString(), Toast.LENGTH_SHORT).show()
                runOnUiThread {

                    val name:String =  text.value.toString().replace("","")
                    edPlate.setText(name)
                    getParking(edPlate.text.toString())

                }
            } else {
                Log.d(TAG, "text data is null")
            }
        } else {
            Log.d(TAG, "no text detected")
        }
        return text != null
    }

    private inner class CaptureGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            return onTap(e.rawX, e.rawY) || super.onSingleTapConfirmed(e)
        }
    }

    private inner class ScaleListener : ScaleGestureDetector.OnScaleGestureListener {
        /**
         * Responds to scaling events for a gesture in progress.
         * Reported by pointer motion.
         *
         * @param detector The detector reporting the event - use this to
         * retrieve extended info about event state.
         * @return Whether or not the detector should consider this event
         * as handled. If an event was not handled, the detector
         * will continue to accumulate movement until an event is
         * handled. This can be useful if an application, for example,
         * only wants to update scaling factors if the change is
         * greater than 0.01.
         */
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            return false
        }

        /**
         * Responds to the beginning of a scaling gesture. Reported by
         * new pointers going down.
         *
         * @param detector The detector reporting the event - use this to
         * retrieve extended info about event state.
         * @return Whether or not the detector should continue recognizing
         * this gesture. For example, if a gesture is beginning
         * with a focal point outside of a region where it makes
         * sense, onScaleBegin() may return false to ignore the
         * rest of the gesture.
         */
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            return true
        }

        /**
         * Responds to the end of a scale gesture. Reported by existing
         * pointers going up.
         *
         *
         * Once a scale has ended, [ScaleGestureDetector.getFocusX]
         * and [ScaleGestureDetector.getFocusY] will return focal point
         * of the pointers remaining on the screen.
         *
         * @param detector The detector reporting the event - use this to
         * retrieve extended info about event state.
         */
        override fun onScaleEnd(detector: ScaleGestureDetector) {
            if (cameraSource != null) {
                cameraSource!!.doZoom(detector.scaleFactor)
            }
        }
    }

    companion object {
        private const val TAG = "Street"

        // Intent request code to handle updating play services if needed.
        private const val RC_HANDLE_GMS = 9001

        // Permission request codes need to be < 256
        private const val RC_HANDLE_CAMERA_PERM = 2

        // Constants used to pass extra data in the intent
        const val AutoFocus = "AutoFocus"
        const val UseFlash = "UseFlash"
        const val TextBlockObject = "String"
        fun hideKeyboard(activity: Activity) {
            val imm = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            //Find the currently focused view, so we can grab the correct window token from it.
            var view = activity.currentFocus
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

}
