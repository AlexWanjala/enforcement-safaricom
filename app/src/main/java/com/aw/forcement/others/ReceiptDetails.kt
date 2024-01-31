package com.aw.forcement.others

import Json4Kotlin_Base
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.aw.forcement.BuildConfig
import com.aw.forcement.R
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
import kotlinx.android.synthetic.main.activity_receipt_details.*
import kotlinx.android.synthetic.main.progressbar.*
import net.glxn.qrgen.android.QRCode
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ReceiptDetails : AppCompatActivity() {

    private var printing : Printing? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt_details)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        imageClose.setOnClickListener { finish() }
        tv_verify.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            builder.setTitle(intent.getStringExtra("transaction_code").toString())
            builder.setMessage("Select the options below")
            builder.setNeutralButton("Verify") { dialog, which ->
                dialog.dismiss()
                if(intent.getBooleanExtra("verified",false)){
                    Toast.makeText(this,"Already Verified",Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                    return@setNeutralButton
                }
                Toast.makeText(this,"Verified Success",Toast.LENGTH_LONG).show()
                val formData = listOf(
                    "function" to "verifyTransaction",
                    "transaction_code" to  intent.getStringExtra("transaction_code").toString(),
                    "idNo" to getValue(this,"idNo").toString(),
                    "deviceId" to getDeviceIdNumber(this)
                )

                executeRequest(formData, biller,object : CallBack {
                    override fun onSuccess(result: String?) {

                    }
                    override fun onFailure(result: String?) {
                        runOnUiThread {
                            Toast.makeText(this@ReceiptDetails,result, Toast.LENGTH_LONG).show()
                        }
                    }

                })
            }
            builder.setNegativeButton(android.R.string.no) { dialog, which ->
                dialog.dismiss()
            }

            builder.show()
        }
        print.setOnClickListener {
            printReceipt()
        }
        getReceipt()

        //Bluetooth printer
        if (Printooth.hasPairedPrinter())
            printing = Printooth.printer()
        initListeners()

    }

    fun humanDate(input: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = inputFormat.parse(input)
        val outputFormat = SimpleDateFormat("dd MMM yyyy 'AT' hh:mm a")
        val output = outputFormat.format(date)
        return output
    }
    private fun getReceipt(){
        progress_circular.visibility = View.VISIBLE
        val formData = listOf(
                "function" to "getReceipt",
            "receiptNo" to intent.getStringExtra("transaction_code").toString(),
            "latitude" to getValue(this,"latitude").toString(),
            "longitude" to getValue(this,"longitude").toString(),
            "idNo" to getValue(this,"idNo").toString(),
            "username" to getValue(this,"username").toString(),
            "addressString" to getValue(this,"addressString").toString(),
            "deviceId" to getDeviceIdNumber(this)
        )
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
                runOnUiThread {  progress_circular.visibility = View.GONE }
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)

                if(response.success){
                    runOnUiThread {
                        tv_name.text = response.data.receiptDetails.paidBy
                        tv_phone.text = response.data.receiptDetails.customerPhoneNumber
                        tv_date.text = humanDate(response.data.receiptDetails.dateCreated)
                        tv_des.text = response.data.receiptDetails.description
                        val amount = intent.getStringExtra("amount")
                        val nf = NumberFormat.getInstance(Locale.US)
                        val f1 = nf.parse(amount).toFloat()
                        tv_amount.text = "KES "+ nf.format(f1) // or df.format(f2);

                        tv_zone.text = "${response.data.receiptDetails.zone}, ${response.data.receiptDetails.wardName}, ${response.data.receiptDetails.subCountyName}"
                        tv_create_by.text = response.data.receiptDetails.names

                        if(!intent.getBooleanExtra("verified",false)){
                            tv_status.text = "Receipt has not been inspected"
                            tv_status.setTextColor(Color.parseColor("#b30000"))
                        }else{
                            tv_status.text = "Receipt inspected"
                          //  tv_status.setTextColor(Color.parseColor("#b30000"))
                        }

                        var descriptions =""
                        var item =""

                        for (receiptInfo in response.data.receiptInfos) {

                            val  customer = receiptInfo.customer
                            val  des = receiptInfo.description

                            item +="${customer} ${receiptInfo.feeDescription}:   ${receiptInfo.receiptAmount}\n";

                            if (des.contains(":")) {
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
                            } else {
                                descriptions =  receiptInfo.description
                            }


                        }

                        save(this@ReceiptDetails,"r_headline",response.data.county.headline)
                        save(this@ReceiptDetails,"r_dateCreated",response.data.receiptDetails.dateCreated)
                        save(this@ReceiptDetails,"r_source",response.data.receiptDetails.source)
                        save(this@ReceiptDetails,"r_currency",response.data.receiptDetails.currency)
                        save(this@ReceiptDetails,"r_item",item)
                        save(this@ReceiptDetails,"r_ussd",response.data.county.ussd)
                        save(this@ReceiptDetails,"r_incomeTypeDescription",response.data.receiptDetails.incomeTypeDescription)
                        save(this@ReceiptDetails,"r_description",descriptions)
                        save(this@ReceiptDetails,"r_date",response.data.receiptDetails.dateCreated)
                        save(this@ReceiptDetails,"r_subCountyName",response.data.receiptDetails.subCountyName)
                        save(this@ReceiptDetails,"r_zone",response.data.receiptDetails.zone)
                        save(this@ReceiptDetails,"r_names",response.data.receiptDetails.names)
                        save(this@ReceiptDetails,"r_transactionCode",response.data.receiptDetails.transactionCode)
                        save(this@ReceiptDetails,"r_payer",response.data.receiptDetails.paidBy)
                        save(this@ReceiptDetails,"r_payerPhone",response.data.receiptDetails.customerPhoneNumber)
                        save(this@ReceiptDetails,"r_billNo",response.data.receiptDetails.billNo)
                        save(this@ReceiptDetails,"r_receiptNo",response.data.receiptDetails.receiptNo)
                        save(this@ReceiptDetails,"r_receiptAmount",response.data.receiptDetails.receiptAmount)

                    }
                }else{
                    runOnUiThread {
                        Toast.makeText(this@ReceiptDetails,response.message,Toast.LENGTH_LONG).show()
                        finish()
                    }
                }
            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@ReceiptDetails,result, Toast.LENGTH_LONG).show()
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
                Toast.makeText(this@ReceiptDetails, "Connecting with printer", Toast.LENGTH_SHORT).show()
            }

            override fun printingOrderSentSuccessfully() {
                Toast.makeText(this@ReceiptDetails, "Order sent to printer", Toast.LENGTH_SHORT).show()
            }

            override fun connectionFailed(error: String) {
                Toast.makeText(this@ReceiptDetails, "Failed to connect printer", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: String) {
                Toast.makeText(this@ReceiptDetails, error, Toast.LENGTH_SHORT).show()
            }

            override fun onMessage(message: String) {
                Toast.makeText(this@ReceiptDetails, "Message: $message", Toast.LENGTH_SHORT).show()
            }

            override fun disconnected() {
                Toast.makeText(this@ReceiptDetails, "Disconnected Printer", Toast.LENGTH_SHORT).show()
                finish()
                startActivity(Intent(this@ReceiptDetails, ReceiptDetails::class.java))
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

        val dateCreated = getValue(this@ReceiptDetails,"r_dateCreated")
        val source = getValue(this@ReceiptDetails,"r_source")
        val currency = getValue(this@ReceiptDetails,"r_currency")
        val ussd = getValue(this@ReceiptDetails,"r_ussd")
        val payerPhone = getValue(this@ReceiptDetails,"r_payerPhone")
        val payer = getValue(this@ReceiptDetails,"r_payer")
        val receiptNo = getValue(this@ReceiptDetails,"r_receiptNo")
        val item = getValue(this@ReceiptDetails,"r_item")
        val transactioncode = getValue(this@ReceiptDetails,"r_transactionCode")
        val amount = getValue(this@ReceiptDetails,"r_receiptAmount")
        val ref = getValue(this@ReceiptDetails,"r_billNo")
        val username = getValue(this@ReceiptDetails,"username")
        val names = getValue(this@ReceiptDetails,"r_names")
        val phone = getValue(this@ReceiptDetails,"payer_phone")
        val incomeTypeDescription = getValue(this@ReceiptDetails,"r_incomeTypeDescription")?.capitalize()
        val description = getValue(this@ReceiptDetails,"r_description")
        val zone = getValue(this@ReceiptDetails,"r_zone")?.toUpperCase()
        val subCounty = getValue(this@ReceiptDetails,"r_subCountyName")?.toUpperCase()


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

        val transactioncode = getValue(this@ReceiptDetails,"transaction_code")
        val amount = getValue(this@ReceiptDetails,"amount")
        val ref = getValue(this@ReceiptDetails,"ref")
        val username = getValue(this@ReceiptDetails,"username")
        val names = getValue(this@ReceiptDetails,"payer_names")
        val phone = getValue(this@ReceiptDetails,"payer_phone")
        val incomeTypeDescription = getValue(this@ReceiptDetails,"incomeTypeDescription")?.capitalize()
        val description = getValue(this@ReceiptDetails,"description")
        val date = getValue(this@ReceiptDetails,"date")



        *//* val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
         val outputFormat = SimpleDateFormat("EEE dd MMM yy hh:mma", Locale.getDefault())
         val date = input?.let { inputFormat.parse(it) }
         val humanDate = date?.let { outputFormat.format(it) }*//*
        val humanDate = date
        val zone = getValue(this@ReceiptDetails,"zone")
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

    //printer services ends here

}