package com.aw.forcement.others

import Json4Kotlin_Base
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import com.aw.forcement.R
import com.aw.passanger.api.*
import com.google.gson.Gson
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.printable.ImagePrintable
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printable.RawPrintable
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.data.printer.DefaultPrinter
import com.mazenrashed.printooth.ui.ScanningActivity
import com.mazenrashed.printooth.utilities.Printing
import com.mazenrashed.printooth.utilities.PrintingCallback
import kotlinx.android.synthetic.main.activity_cess_payments.*
import kotlinx.android.synthetic.main.activity_cess_payments.tvAmount
import kotlinx.android.synthetic.main.activity_cess_payments.tv_message
import kotlinx.android.synthetic.main.activity_main.*
import net.glxn.qrgen.android.QRCode
import java.text.SimpleDateFormat
import java.time.temporal.TemporalAmount
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class CessPayments : AppCompatActivity() {

    private val arrayList = ArrayList<String>()
    private val arrayList2 = ArrayList<String>()
    lateinit var amount: String
    lateinit var feeId: String


    private var printing : Printing? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cess_payments)


        edQuantity.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                amountDisplay()

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
        tvSendPayment.setOnClickListener {
            if(edQuantity.text.isEmpty()){
                Toast.makeText(this,"Quantity Required",Toast.LENGTH_LONG).show()
            }else{
                if(edPhoneNumber.text.isEmpty()){
                    Toast.makeText(this,"Phone Required",Toast.LENGTH_LONG).show()
                }else{
                    generateBill()
                }
            }
        }
        getIncomeTypes()

        //Bluetooth printer
        if (Printooth.hasPairedPrinter())
            printing = Printooth.printer()
        initListeners()
    }

    fun amountDisplay(){
        if(edQuantity.text.isNotEmpty()){
            tvUnits.text = "${edQuantity.text} x $amount"
            tvAmount.text = "KES "+  edQuantity.text.toString().toInt() * amount.toString().toInt()
        }else{
            tvUnits.text =""
            tvAmount.text ="KES 0"
        }
    }

    private fun generateBill (){
        tv_message.text ="Generating bill please wait.."
        val formData = listOf(
            "function" to "generateBill2",
            "feeId" to feeId.toString(),
            "amount" to (amount.toString().toInt() *edQuantity.text.toString().toInt()).toString(),
            "customer" to edPhoneNumber.text.toString(),
            "zone" to getValue(this,"zone").toString(),
            "subCountyID" to getValue(this,"subCountyID").toString(),
            "subCountyName" to getValue(this,"subCountyName").toString(),
            "wardID" to getValue(this,"wardID").toString(),
            "wardName" to getValue(this,"wardName").toString(),
            "idNo" to getValue(this,"idNo").toString(),
            "phoneNumber" to getValue(this,"phoneNumber").toString(),
            "names" to getValue(this,"names").toString()
        )
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                    if(response.success){
                        runOnUiThread {
                            tv_message.text ="Bill generated success.."
                            tvSendPayment.visibility = View.GONE
                            tvSendPushDisabled.visibility = View.VISIBLE
                        }

                        customerPayBillOnline(
                            response.data.billGenerated.billNo,
                            response.data.billGenerated.payBillNo,
                            response.data.billGenerated.amount,
                        )

                    }else{
                        Toast.makeText(this@CessPayments,response.message,Toast.LENGTH_LONG).show()
                    }


                }else{
                    runOnUiThread {  Toast.makeText(this@CessPayments,response.message, Toast.LENGTH_LONG).show()}

                }

            }

        })
    }
    private fun getIncomeTypes (){

        val formData = listOf(
            "function" to "getIncomeTypes",
            "incomeTypePrefix" to  intent.getStringExtra("incomeTypePrefix").toString()
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
                        spinnerIncomeType.adapter = adapters
                        spinnerIncomeType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {
                                spinnerFeeAndCharges(response.data.incomeTypes[postion].incomeTypeId)
                            }
                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }
                        }
                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@CessPayments,response.message, Toast.LENGTH_LONG).show()}

                }

            }

        })
    }
    private fun spinnerFeeAndCharges (incomeTypeId: String){
        val formData = listOf(
            "function" to "getFeesAndCharges",
            "incomeTypeId" to incomeTypeId,
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
                            arrayList2.add(data.feeDescription)
                        }

                        //Spinner
                        adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        spinnerFeeAndCharges.adapter = adapters
                        spinnerFeeAndCharges.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {
                              //  response.data.feesAndCharges[postion].feeId
                                amount = response.data.feesAndCharges[postion].unitFeeAmount
                                feeId = response.data.feesAndCharges[postion].feeId
                                amountDisplay()
                            }
                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }
                        }
                    }
                }
                else{
                    spinnerFeeAndCharges.adapter = null
                    Toast.makeText(this@CessPayments,response.message, Toast.LENGTH_LONG).show() }
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
                    runOnUiThread {  Toast.makeText(this@CessPayments,response.message, Toast.LENGTH_LONG).show()}

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


                            tv_message.text ="Payment Received #${response.data.push.transaction_code} KES ${response.data.push.amount}"
                            save(this@CessPayments,"transaction_code",response.data.push.transaction_code)
                            save(this@CessPayments,"amount",response.data.push.amount)
                            save(this@CessPayments,"phone",response.data.push.account_from)
                            save(this@CessPayments,"ref",response.data.push.ref)
                            save(this@CessPayments,"names",response.data.transaction.names)
                            save(this@CessPayments,"date",response.data.transaction.date)

                            tvSendPayment.visibility = View.VISIBLE
                            tvSendPushDisabled.visibility = View.GONE

                            tvSendPayment.text = "Print"
                            tvSendPayment.setOnClickListener {
                                //printing
                                getBillPrint()
                            }


                        }


                    }else if(response.data.push.callback_returned=="PENDING"){
                        runOnUiThread { tv_message.text ="Waiting for payment.." }
                        TimeUnit.SECONDS.sleep(2L)
                        checkPayment(accountReference)
                    }else{
                        runOnUiThread {
                            tv_message.text = response.data.push.message
                            tvSendPayment.visibility = View.VISIBLE
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

    fun printReceipt(){
        if (!Printooth.hasPairedPrinter())
            resultLauncher.launch(
                Intent(
                    this@CessPayments,
                    ScanningActivity::class.java
                ),
            )
        else printDetails()
    }
    fun getBillPrint (){
        val billNo = getValue(this,"ref").toString()
        var stream = biller
        if (billNo.startsWith("SBP")) {
            stream = trade
        }
        if (billNo.startsWith("PKN")) {
            stream = parking
        }
        if (billNo.startsWith("RNT")) {
            stream = rent
        }
        if (billNo.startsWith("BLL")) {
            stream = biller
        }

        val formData = listOf(
            "function" to "getBill",
            "billNo" to billNo
        )
        executeRequest(formData, stream,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){
                    runOnUiThread {

                        save(this@CessPayments,"incomeTypeDescription",response.data.billDetails.incomeTypeDescription)
                        save(this@CessPayments,"description",response.data.billInfo[0].description).toString().toLowerCase().capitalize()
                        printReceipt()
                    }

                }else{

                    runOnUiThread {
                        save(this@CessPayments,"incomeTypeDescription",getValue(this@CessPayments,"ref").toString())
                        printReceipt()
                    }

                }

            }

        })
    }

    //printer services starts here
    private fun initListeners() {
        /* callback from printooth to get printer process */
        printing?.printingCallback = object : PrintingCallback {
            override fun connectingWithPrinter() {
                Toast.makeText(this@CessPayments, "Connecting with printer", Toast.LENGTH_SHORT).show()
            }

            override fun printingOrderSentSuccessfully() {
                Toast.makeText(this@CessPayments, "Order sent to printer", Toast.LENGTH_SHORT).show()
            }

            override fun connectionFailed(error: String) {
                Toast.makeText(this@CessPayments, "Failed to connect printer", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: String) {
                Toast.makeText(this@CessPayments, error, Toast.LENGTH_SHORT).show()
            }

            override fun onMessage(message: String) {
                Toast.makeText(this@CessPayments, "Message: $message", Toast.LENGTH_SHORT).show()
            }

            override fun disconnected() {
                Toast.makeText(this@CessPayments, "Disconnected Printer", Toast.LENGTH_SHORT).show()
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

        val title2 ="COUNTY GOVERNMENT OF HOMABAY\nGenowa En Dongruok\n\n\n"
        add(
            TextPrintable.Builder()
                .setText(title2)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .build())


        val bmp = BitmapFactory.decodeResource(resources, R.drawable.county_logo_pirnt)
        val argbBmp = bmp.copy(Bitmap.Config.ARGB_8888, false)
        val scaledLogo = Bitmap.createScaledBitmap(argbBmp, 145, 180, true)
        add(
            ImagePrintable.Builder(scaledLogo)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .build())



        val transactioncode = getValue(this@CessPayments,"transaction_code")
        val amount = getValue(this@CessPayments,"amount")
        val ref = getValue(this@CessPayments,"ref")
        val username = getValue(this@CessPayments,"username")
        val names = getValue(this@CessPayments,"names")
        val phone = getValue(this@CessPayments,"phone")
        val incomeTypeDescription = getValue(this@CessPayments,"incomeTypeDescription")?.capitalize()
        val description = getValue(this@CessPayments,"description")

        val input = getValue(this@CessPayments,"date")
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEE dd MMM yy hh:mma", Locale.getDefault())
        val date = input?.let { inputFormat.parse(it) }
        val humanDate = date?.let { outputFormat.format(it) }


        val message ="\n\nFor: $description #Mpesa\nTransaction Code: $transactioncode\nAmount: KES $amount\nPayer: $names\nDate: $humanDate\nPrinted By: $username @Homabay Town\n"

        add(
            TextPrintable.Builder()
                .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                .setText(message)
                // .setNewLinesAfter(1)
                .build())

        val message2 ="\n\nFor: Parking Services\nCode: $transactioncode\nAmount: KES $amount\nPayer: $names\nDate: $humanDate"


        val qr: Bitmap = QRCode.from(message2)
            .withSize(200, 200).bitmap()
        add(
            ImagePrintable.Builder(qr)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .build())

        val footer ="\nLipa Ushuru Tujenge Homabay\n#Endless Potential\n\n\n\n\n"
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

