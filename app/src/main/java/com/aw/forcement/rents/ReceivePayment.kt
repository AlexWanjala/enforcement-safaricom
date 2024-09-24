package com.aw.forcement.rents

import Const
import Json4Kotlin_Base
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
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
import kotlinx.android.synthetic.main.activity_fines.*
import kotlinx.android.synthetic.main.activity_fines.edDescription
import kotlinx.android.synthetic.main.activity_fines.edPhoneNumber
import kotlinx.android.synthetic.main.activity_fines.edQuantity
import kotlinx.android.synthetic.main.activity_fines.imageClose
import kotlinx.android.synthetic.main.activity_fines.spinnerFeeAndCharges
import kotlinx.android.synthetic.main.activity_fines.spinnerIncomeType
import kotlinx.android.synthetic.main.activity_fines.tvAmount
import kotlinx.android.synthetic.main.activity_fines.tvSendPayment
import kotlinx.android.synthetic.main.activity_fines.tvSendPushDisabled
import kotlinx.android.synthetic.main.activity_fines.tvUnits
import kotlinx.android.synthetic.main.activity_fines.tv_message
import kotlinx.android.synthetic.main.activity_fines.tv_title
import kotlinx.android.synthetic.main.activity_my_history.*
import kotlinx.android.synthetic.main.activity_receive_payment.*
import kotlinx.android.synthetic.main.alert_balance_clearence.view.*
import kotlinx.android.synthetic.main.message_box.view.*
import kotlinx.android.synthetic.main.payment_recieved.view.*
import kotlinx.android.synthetic.main.payment_recieved.view.okay
import kotlinx.android.synthetic.main.payment_unsuccesfull.view.*
import net.glxn.qrgen.android.QRCode
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class ReceivePayment : AppCompatActivity() {

    private val arrayList = ArrayList<String>()
    private val arrayList2 = ArrayList<String>()
    lateinit var amount: String
    lateinit var feeId: String
    private var printing : Printing? = null

    lateinit var messageBoxView : View
    lateinit var messageBoxInstance: androidx.appcompat.app.AlertDialog

     lateinit var messageBoxViewDate : View
     lateinit var messageBoxInstanceDate: androidx.appcompat.app.AlertDialog // Declare as AlertDialog

    lateinit var messageBoxViewFailed : View
    lateinit var messageBoxInstanceFailed: androidx.appcompat.app.AlertDialog // Declare as AlertDialog

    lateinit var messageBoxViewPaid : View
    lateinit var messageBoxInstancePaid: androidx.appcompat.app.AlertDialog // Declare as AlertDialog

    var promisedDate =""
    var propertyID =""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receive_payment)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        //Initialize messageBoxView here
        messageBoxView = LayoutInflater.from(this).inflate(R.layout.message_box, null)
        messageBoxViewDate = LayoutInflater.from(this).inflate(R.layout.alert_balance_clearence, null)
        messageBoxViewFailed = LayoutInflater.from(this).inflate(R.layout.payment_unsuccesfull, null)
        messageBoxViewPaid = LayoutInflater.from(this).inflate(R.layout.payment_recieved, null)

        tv_title.text = intent.getStringExtra("title")

        imageClose.setOnClickListener { finish() }

        tvSendPayment.setOnClickListener {

            if(edPhoneNumber.text.isEmpty() || edAmount.text.isEmpty()){
                Toast.makeText(this,"Empty Field", Toast.LENGTH_LONG).show()
            }else{
                showMessageBoxDate()
            }
        }
        getIncomeTypes()
        getProperties()

        //Bluetooth printer
         if (Printooth.hasPairedPrinter())
             printing = Printooth.printer()
         initListeners()

    }

    fun amountDisplay() {
        if (edQuantity.text.isNotEmpty()) {
            tvUnits.text = "${edQuantity.text} x $amount"
            tvAmount.text = "KES " + edQuantity.text.toString().toInt() * amount.toString().toInt()
        } else {
            tvUnits.text = ""
            tvAmount.text = "KES 0"
        }
    }

    private fun generateBill (){
        tv_message.text ="Generating bill please wait.."
        (messageBoxView as View?)!!.tv_message.text ="Generating bill please wait.."
        showMessageBox()
        val formData = listOf(
            "function" to "generateBill2",
            "feeId" to feeId.toString(),
            "amount" to edAmount.text.toString(),
            "customer" to "${Const.instance.getUnits().unitNo}|${Const.instance.getUnits().idNo}|${Const.instance.getUnits().id}", //unitNo | idNo | id
            "zone" to  Const.instance.getUnits().zone,
            "subCountyID" to  Const.instance.getUnits().subCountyID,
            "subCountyName" to  Const.instance.getUnits().subCountyName,
            "wardID" to  Const.instance.getUnits().wardID,
            "wardName" to Const.instance.getUnits().wardName,
            "idNo" to getValue(this,"idNo").toString(),
            "phoneNumber" to getValue(this,"phoneNumber").toString(),
            "customerPhoneNumber" to edPhoneNumber.text.toString(),
            "names" to getValue(this,"username").toString(),
            "description" to "${Const.instance.getUnits().occupants}| ${Const.instance.getUnits().unitNo} | ${Const.instance.getUnits().property}",
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
                            (messageBoxView as View?)!!.tv_message.text ="Bill generated success.."
                            tvSendPayment.visibility = View.GONE
                            tvSendPushDisabled.visibility = View.VISIBLE
                        }

                        customerPayBillOnline(
                            response.data.billGenerated.billNo,
                            response.data.billGenerated.payBillNo,
                            response.data.billGenerated.amount,
                        )

                    }else{
                        (messageBoxView as View?)!!.tv_message.text =response.message
                        messageBoxInstance.dismiss()
                        Toast.makeText(this@ReceivePayment,response.message, Toast.LENGTH_LONG).show()
                    }


                }else{
                    runOnUiThread {  Toast.makeText(this@ReceivePayment,response.message, Toast.LENGTH_LONG).show()}

                }

            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@ReceivePayment,result, Toast.LENGTH_LONG).show()
                }
            }

        })
    }
    private fun getIncomeTypes (){

        val formData = listOf(
            "function" to "getIncomeTypes",
            "incomeTypePrefix" to "HSRENT",
            "deviceId" to getDeviceIdNumber(this),
            "keyword" to "Housing",
        )
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    runOnUiThread {

                        getFeesAndCharges(response.data.incomeTypes[0].incomeTypeId)

                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@ReceivePayment,response.message, Toast.LENGTH_LONG).show()}

                }

            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@ReceivePayment,result, Toast.LENGTH_LONG).show()
                }
            }

        })
    }

    private fun getFeesAndCharges(incomeTypeId: String){
        val formData = listOf(
            "function" to "getFeesAndCharges",
            "incomeTypeId" to incomeTypeId,
            "deviceId" to getDeviceIdNumber(this),
        )
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                runOnUiThread {

                    if(response.success){
                        runOnUiThread {

                           feeId =  response.data.feesAndCharges[0].feeId

                        }
                    }
                    else{

                        Toast.makeText(this@ReceivePayment,response.message, Toast.LENGTH_LONG).show() }
                }

            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@ReceivePayment,result, Toast.LENGTH_LONG).show()
                }
            }

        })
    }

    private fun getProperties (){

        val formData = listOf(
            "function" to "getProperties",
            "incomeTypePrefix" to intent.getStringExtra("incomeTypePrefix").toString(),
            "deviceId" to getDeviceIdNumber(this),
            "page" to "1",
            "rows_per_page" to "10"

        )
        executeRequest(formData, rent,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    runOnUiThread {

                        for(data in response.data.properties){
                            arrayList.add(data.property)
                        }

                        //Spinner
                        val adapters = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item,arrayList)
                        adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        spinnerProperties.adapter = adapters
                        spinnerProperties.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {

                                getUnits(response.data.properties[postion].propertyID)
                                propertyID = response.data.properties[postion].propertyID

                            }
                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }
                        }
                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@ReceivePayment,response.message, Toast.LENGTH_LONG).show()}

                }

            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@ReceivePayment,result, Toast.LENGTH_LONG).show()
                }
            }

        })
    }
    private fun getUnits(propertyID: String){
        val formData = listOf(
            "function" to "getUnits",
            "propertyID" to propertyID,
            "deviceId" to getDeviceIdNumber(this),
            "page" to "1",
            "rows_per_page" to "10"
        )
        executeRequest(formData, rent,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                runOnUiThread {

                    arrayList2.clear()
                    val adapters = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item,arrayList2)
                    adapters.clear()
                    if(response.success){
                        runOnUiThread {
                            for(data in response.data.units){
                                arrayList2.add("${data.property} ${data.unitNo} ${data.occupants}")
                            }

                            //Spinner
                            adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                            spinnerUnits.adapter = adapters
                            spinnerUnits.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {
                                    //  response.data.feesAndCharges[postion].feeId
                                    amount = response.data.units[postion].arrears
                                    Const.instance.setUnits(response.data.units[postion])


                                  //  save(this@ReceivePayment,"description",response.data.feesAndCharges[postion].feeDescription)

                                    runOnUiThread {
                                        tv_tenant.text = response.data.units[postion].occupants
                                        tv_rent.text ="KES "+ response.data.units[postion].monthlyRent
                                        tv_arrears.text ="KES "+ response.data.units[postion].arrears
                                    }
                                  /*  runOnUiThread {
                                        feeDescription =  response.data.units[postion].occupants
                                        tvUnits.text =  response.data.units[postion].unitNo
                                        tvAmount.text ="KES "+amount
                                    }*/
                                }
                                override fun onNothingSelected(p0: AdapterView<*>?) {

                                }
                            }
                        }
                    }
                    else{
                        spinnerFeeAndCharges.adapter = null
                        Toast.makeText(this@ReceivePayment,response.message, Toast.LENGTH_LONG).show() }
                }

            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@ReceivePayment,result, Toast.LENGTH_LONG).show()
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
            "phoneNumber" to edPhoneNumber.text.toString(),
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
                    runOnUiThread {  Toast.makeText(this@ReceivePayment,response.message, Toast.LENGTH_LONG).show()}

                }

            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@ReceivePayment,result, Toast.LENGTH_LONG).show()
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
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)

                if(response.success){

                    if(response.data.push.callback_returned=="PAID"){

                        runOnUiThread {

                            messageBoxInstance.dismiss()
                            tv_message.text ="Payment Received #${response.data.push.transaction_code} KES ${response.data.push.amount}"

                           /* save(this@ReceivePayment,"transaction_code",response.data.push.transaction_code)
                            save(this@ReceivePayment,"amount",response.data.push.amount)
                            save(this@ReceivePayment,"payer_phone",response.data.push.account_from)
                            save(this@ReceivePayment,"ref",response.data.push.ref)
                            save(this@ReceivePayment,"payer_names",response.data.transaction.names)
                            save(this@ReceivePayment,"date",response.data.transaction.date)*/

                            tvSendPayment.visibility = View.VISIBLE
                            tvSendPushDisabled.visibility = View.GONE

                            showMessageBoxPayment(

                                response.data.transaction.transaction_code,
                                response.data.transaction.names,
                                response.data.transaction.amount,
                                "",
                                ""
                            )

                            getReceipt(response.data.push.transaction_code)



                        }


                    }else if(response.data.push.callback_returned=="PENDING"){
                        runOnUiThread {
                            tv_message.text ="Waiting for payment.."
                            (messageBoxView as View?)!!.tv_message.text ="Waiting for payment.."
                        }
                        TimeUnit.SECONDS.sleep(5L)
                        checkPayment(accountReference)
                    }else{
                        runOnUiThread {
                            messageBoxInstance.dismiss()
                            tv_message.text = response.data.push.message
                            tvSendPayment.visibility = View.VISIBLE
                            tvSendPushDisabled.visibility = View.GONE
                            showMessageBoxPaymentFail(response.data.push.message )
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
                    Toast.makeText(this@ReceivePayment,result, Toast.LENGTH_LONG).show()
                }
            }

        })
    }
    //popups
    private fun showMessageBox(){
        // Check if messageBoxView has a parent
        if (messageBoxView.parent != null) {
            // Remove messageBoxView from its parent
            (messageBoxView.parent as ViewGroup).removeView(messageBoxView)
        }
        val messageBoxBuilder = androidx.appcompat.app.AlertDialog.Builder(this).setView(messageBoxView as View?)
        messageBoxInstance = messageBoxBuilder.show()
    }

    private fun showMessageBoxPayment(transaction: String, payer: String, amount: String, des: String, category: String) {

        // Check if the activity is finishing or destroyed
        if (isFinishing || isDestroyed) {
            return // Don't show the dialog if the activity is finishing or destroyed
        }

        // Check if messageBoxView has a parent
        if (messageBoxViewPaid.parent != null) {
            // Remove messageBoxView from its parent
            (messageBoxViewPaid.parent as ViewGroup).removeView(messageBoxViewPaid)
        }

        // Create AlertDialog.Builder
        val messageBoxBuilder = AlertDialog.Builder(this).setView(messageBoxViewPaid as View?)

        // Show the dialog and assign it to messageBoxInstancePaid
        messageBoxInstancePaid = messageBoxBuilder.show()

        // Set text for views in the dialog
        messageBoxViewPaid.tv_transaction.text = transaction
        messageBoxViewPaid.tv_payer.text = payer
        messageBoxViewPaid.tv_amount.text = amount
        messageBoxViewPaid.tv_des.text = des
        messageBoxViewPaid.tv_category.text = category

        // Set onClickListener for the 'okay' button to dismiss the dialog
        messageBoxViewPaid.okay.setOnClickListener {
            messageBoxInstancePaid.dismiss() // Dismiss the dialog if it's not null
        }
    }

    // Override onDestroy method to dismiss the dialog if it's showing


    private fun showMessageBoxDate(){

        //Date from
        val cal: Calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat1 = "yyyy-MM-dd" //
            val sdf1 = SimpleDateFormat(myFormat1, Locale.US)
            promisedDate = sdf1.format(cal.time)


            val myFormat = "dd MMMM yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            val spanned = Html.fromHtml("<u> ${sdf.format(cal.time)} </u>")
            messageBoxViewDate.edDate.setText(spanned)

        }

        // Check if messageBoxView has a parent
        if (messageBoxViewDate.parent != null) {
            // Remove messageBoxView from its parent
            (messageBoxViewDate.parent as ViewGroup).removeView(messageBoxViewDate)
        }

        val messageBoxBuilder = androidx.appcompat.app.AlertDialog.Builder(this).setView(
            messageBoxViewDate as View?
        )
        messageBoxInstanceDate = messageBoxBuilder.show()

        messageBoxViewDate.tv_message_balance.text = "When Will The tenant clear the balance of KES ${edAmount.text}"
        messageBoxViewDate.edDate.setOnClickListener {

            DatePickerDialog(this,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()

        }
        messageBoxViewDate.okay.setOnClickListener {
            //edPlate
            messageBoxInstanceDate.dismiss()
            generateBill()

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
            generateBill()
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

                                    descriptions +="${array2[0].trimStart().trimEnd()}:             ${array2[1].trimStart().trimEnd()}\n";

                                }


                            }

                        }

                        save(this@ReceivePayment,"r_headline",response.data.county.headline)
                        save(this@ReceivePayment,"r_dateCreated",response.data.receiptDetails.dateCreated)
                        save(this@ReceivePayment,"r_source",response.data.receiptDetails.source)
                        save(this@ReceivePayment,"r_currency",response.data.receiptDetails.currency)
                        save(this@ReceivePayment,"r_item",item)
                        save(this@ReceivePayment,"r_ussd",response.data.county.ussd)
                        save(this@ReceivePayment,"r_incomeTypeDescription",response.data.receiptDetails.incomeTypeDescription)
                        save(this@ReceivePayment,"r_description",descriptions)
                        save(this@ReceivePayment,"r_date",response.data.receiptDetails.dateCreated)
                        save(this@ReceivePayment,"r_subCountyName",response.data.receiptDetails.subCountyName)
                        save(this@ReceivePayment,"r_zone",response.data.receiptDetails.zone)
                        save(this@ReceivePayment,"r_names",response.data.receiptDetails.names)
                        save(this@ReceivePayment,"r_transactionCode",response.data.receiptDetails.transactionCode)
                        save(this@ReceivePayment,"r_payer",response.data.receiptDetails.paidBy)
                        save(this@ReceivePayment,"r_payerPhone",response.data.receiptDetails.customerPhoneNumber)
                        save(this@ReceivePayment,"r_billNo",response.data.receiptDetails.billNo)
                        save(this@ReceivePayment,"r_receiptNo",response.data.receiptDetails.receiptNo)
                        save(this@ReceivePayment,"r_receiptAmount",response.data.receiptDetails.receiptAmount)

                        tvSendPayment.text = "Print"
                        tvSendPayment.setOnClickListener {
                            //printing
                            printReceipt()
                        }

                        printReceipt()


                    }
                }else{
                    runOnUiThread {
                        Toast.makeText(this@ReceivePayment,response.message,Toast.LENGTH_LONG).show()
                        finish()
                    }
                }
            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@ReceivePayment,result, Toast.LENGTH_LONG).show()
                }
            }

        })


    }

    //Printer services starts here
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
                Toast.makeText(this@ReceivePayment, "Connecting with printer", Toast.LENGTH_SHORT).show()
            }

            override fun printingOrderSentSuccessfully() {
                Toast.makeText(this@ReceivePayment, "Order sent to printer", Toast.LENGTH_SHORT).show()
            }

            override fun connectionFailed(error: String) {
                Toast.makeText(this@ReceivePayment, "Failed to connect printer", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: String) {
                Toast.makeText(this@ReceivePayment, error, Toast.LENGTH_SHORT).show()
            }

            override fun onMessage(message: String) {
                Toast.makeText(this@ReceivePayment, "Message: $message", Toast.LENGTH_SHORT).show()
            }

            override fun disconnected() {
                Toast.makeText(this@ReceivePayment, "Disconnected Printer", Toast.LENGTH_SHORT).show()
                finish()
                startActivity(Intent(this@ReceivePayment, ReceivePayment::class.java))
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


        val dateCreated = getValue(this@ReceivePayment,"r_dateCreated")
        val source = getValue(this@ReceivePayment,"r_source")
        val currency = getValue(this@ReceivePayment,"r_currency")
        val ussd = getValue(this@ReceivePayment,"r_ussd")
        val payerPhone = getValue(this@ReceivePayment,"r_payerPhone")
        val payer = getValue(this@ReceivePayment,"r_payer")
        val receiptNo = getValue(this@ReceivePayment,"r_receiptNo")
        val item = getValue(this@ReceivePayment,"r_item")
        val transactioncode = getValue(this@ReceivePayment,"r_transactionCode")
        val amount = getValue(this@ReceivePayment,"r_receiptAmount")
        val ref = getValue(this@ReceivePayment,"r_billNo")
        val username = getValue(this@ReceivePayment,"username")
        val names = getValue(this@ReceivePayment,"r_names")
        val phone = getValue(this@ReceivePayment,"payer_phone")
        val incomeTypeDescription = getValue(this@ReceivePayment,"r_incomeTypeDescription")?.capitalize()
        val description = getValue(this@ReceivePayment,"r_description")
        val zone = getValue(this@ReceivePayment,"r_zone")?.toUpperCase()
        val subCounty = getValue(this@ReceivePayment,"r_subCountyName")?.toUpperCase()


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

           val transactioncode = getValue(this@ReceivePayment,"transaction_code")
           val amount = getValue(this@ReceivePayment,"amount")
           val ref = getValue(this@ReceivePayment,"ref")
           val username = getValue(this@ReceivePayment,"username")
           val names = getValue(this@ReceivePayment,"payer_names")
           val phone = getValue(this@ReceivePayment,"payer_phone")
           val incomeTypeDescription = getValue(this@ReceivePayment,"incomeTypeDescription")?.capitalize()
           val description = getValue(this@ReceivePayment,"description")
           val date = getValue(this@ReceivePayment,"date")



           *//* val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
         val outputFormat = SimpleDateFormat("EEE dd MMM yy hh:mma", Locale.getDefault())
         val date = input?.let { inputFormat.parse(it) }
         val humanDate = date?.let { outputFormat.format(it) }*//*
        val humanDate = date
        val zone = getValue(this@ReceivePayment,"zone")
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

