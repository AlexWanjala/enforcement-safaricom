package com.aw.forcement

import Json4Kotlin_Base
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import com.aw.forcement.others.ReceiptDetails
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
import kotlinx.android.synthetic.main.activity_invoice_detials.*
import kotlinx.android.synthetic.main.activity_receipt_details.*
import kotlinx.android.synthetic.main.activity_receipt_details.imageClose
import kotlinx.android.synthetic.main.activity_receipt_details.print
import kotlinx.android.synthetic.main.activity_receipt_details.tv_amount
import kotlinx.android.synthetic.main.activity_receipt_details.tv_create_by
import kotlinx.android.synthetic.main.activity_receipt_details.tv_date
import kotlinx.android.synthetic.main.activity_receipt_details.tv_des
import kotlinx.android.synthetic.main.activity_receipt_details.tv_phone
import kotlinx.android.synthetic.main.activity_receipt_details.tv_status
import kotlinx.android.synthetic.main.activity_receipt_details.tv_zone
import kotlinx.android.synthetic.main.progressbar.*
import net.glxn.qrgen.android.QRCode
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.io.print

class InvoiceDetials : AppCompatActivity() {

    private var printing : Printing? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice_detials)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        imageClose.setOnClickListener { finish() }
      /*  tv_verify.setOnClickListener {

            val builder = AlertDialog.Builder(this)
            builder.setTitle(intent.getStringExtra("transaction_code").toString())
            builder.setMessage("Select the options below")
            builder.setNeutralButton("Verify") { dialog, which ->
                dialog.dismiss()
                if(intent.getBooleanExtra("verified",false)){
                    Toast.makeText(this,"Already Verified", Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                    return@setNeutralButton
                }
                Toast.makeText(this,"Verified Success", Toast.LENGTH_LONG).show()
                val formData = listOf(
                    "function" to "verifyTransaction",
                    "transaction_code" to  intent.getStringExtra("transaction_code").toString(),
                    "idNo" to getValue(this,"idNo").toString()
                )

                executeRequest(formData, biller,object : CallBack {
                    override fun onSuccess(result: String?) {

                    }

                })
            }
            builder.setNegativeButton(android.R.string.no) { dialog, which ->
                dialog.dismiss()
            }

            builder.show()
        }*/
        print.setOnClickListener { printReceipt() }
        getBill()

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
    private fun getBill(){
        progress_circular.visibility = View.VISIBLE
        val formData = listOf(
            "function" to "getBill",
            "billNo" to intent.getStringExtra("billNo").toString(),
            "latitude" to getValue(this,"latitude").toString(),
            "longitude" to getValue(this,"longitude").toString(),
            "idNo" to getValue(this,"idNo").toString(),
            "username" to getValue(this,"username").toString(),
            "addressString" to getValue(this,"addressString").toString()
        )
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
                runOnUiThread {  progress_circular.visibility = View.GONE }
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)

                if(response.success){
                    runOnUiThread {
                        tv_incomeTypeDescription.text = response.data.billDetails.incomeTypeDescription
                        tv_phone.text = response.data.billDetails.customerPhoneNumber
                        tv_date.text = humanDate(response.data.billDetails.dateCreated)

                        tv_des.text = response.data.billDetails.description

                        val amount =response.data.billDetails.detailAmount
                        val nf = NumberFormat.getInstance(Locale.US)
                        val f1 = nf.parse(amount).toFloat()
                        tv_amount.text = "KES "+ nf.format(f1) // or df.format(f2);


                        tv_zone.text = "${response.data.billDetails.zone}, ${response.data.billDetails.wardName}, ${response.data.billDetails.subCountyName}"
                        tv_create_by.text = response.data.billDetails.names

                        tv_status.text = response.data.billDetails.status
                        if(response.data.billDetails.status=="PAID"){
                            tv_status.setTextColor(Color.GREEN)
                        }else{
                            tv_status.setTextColor(Color.RED)
                        }

                        tv_create_by.text = response.data.billDetails.names


                        var descriptions ="";

                        for (billInfo in response.data.billInfo) {

                            val  feeDescription = billInfo.feeDescription
                            val  customer = billInfo.customer
                            val  description = billInfo.description

                            descriptions += "${customer} ${description} ${feeDescription}";

                        }

                        tv_des.text = descriptions

                        save(this@InvoiceDetials,"billNo",response.data.billDetails.billNo)
                        save(this@InvoiceDetials,"incomeTypeDescription",response.data.billDetails.incomeTypeDescription)
                        save(this@InvoiceDetials,"description",descriptions)
                        save(this@InvoiceDetials,"date",response.data.billDetails.dateCreated)
                        save(this@InvoiceDetials,"amount",response.data.billDetails.detailAmount)
                        save(this@InvoiceDetials,"payBillNo",response.data.payBill.shortCode)


                    }
                }else{
                    runOnUiThread {
                        Toast.makeText(this@InvoiceDetials,response.message, Toast.LENGTH_LONG).show()
                        finish()
                    }
                }
            }

        })


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
                tv_printer.text = "Connecting with printer"
                tv_printer.setTextColor(Color.BLUE)
              //  Toast.makeText(this@InvoiceDetials, "Connecting with printer", Toast.LENGTH_SHORT).show()
            }

            override fun printingOrderSentSuccessfully() {
                tv_printer.text = "Order sent to printer"
                tv_printer.setTextColor(Color.GREEN)
               // Toast.makeText(this@InvoiceDetials, "Order sent to printer", Toast.LENGTH_SHORT).show()
            }

            override fun connectionFailed(error: String) {
                tv_printer.text = "Failed to connect printer"
                tv_printer.setTextColor(Color.RED)
               // Toast.makeText(this@InvoiceDetials, "Failed to connect printer", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: String) {
               // Toast.makeText(this@InvoiceDetials, error, Toast.LENGTH_SHORT).show()
                tv_printer.text = error
                tv_printer.setTextColor(Color.RED)
            }

            override fun onMessage(message: String) {
                Toast.makeText(this@InvoiceDetials, "Message: $message", Toast.LENGTH_SHORT).show()
                tv_printer.text = message
                tv_printer.setTextColor(Color.GREEN)
            }

            override fun disconnected() {
                tv_printer.text = "Disconnected Printer"
                tv_printer.setTextColor(Color.GREEN)
               // Toast.makeText(this@InvoiceDetials, "Disconnected Printer", Toast.LENGTH_SHORT).show()
                //finish()
               // startActivity(Intent(this@InvoiceDetials, ReceiptDetails::class.java))
            }

        }
    }
    private fun printDetails() {
        val printables = getSomePrintables()
        printing?.print(printables)
    }
    /* Customize your printer here with text, logo and QR code */
    private fun getSomePrintables() = java.util.ArrayList<Printable>().apply {

        val title ="\n\nOFFICIAL INVOICE\n\n"
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

        val billNo = getValue(this@InvoiceDetials,"billNo")
        val amount = getValue(this@InvoiceDetials,"amount")
        val payBill = getValue(this@InvoiceDetials,"payBillNo")
        val username = getValue(this@InvoiceDetials,"username")
        val names = getValue(this@InvoiceDetials,"payer_names")
        val phone = getValue(this@InvoiceDetials,"payer_phone")
        val incomeTypeDescription = getValue(this@InvoiceDetials,"incomeTypeDescription")?.capitalize()
        val description = getValue(this@InvoiceDetials,"description")
        val date = getValue(this@InvoiceDetials,"date")



        /* val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
         val outputFormat = SimpleDateFormat("EEE dd MMM yy hh:mma", Locale.getDefault())
         val date = input?.let { inputFormat.parse(it) }
         val humanDate = date?.let { outputFormat.format(it) }*/

        val humanDate = date
        val zone = getValue(this@InvoiceDetials,"zone")
        val message ="\n\nFor: $description\nBillNo: $billNo\nAmount: KES $amount\nPayBillNo: $payBill\nDate: $humanDate\nPrinted By: $username at $zone\n"

        add(
            TextPrintable.Builder()
                .setAlignment(DefaultPrinter.ALIGNMENT_LEFT)
                .setFontSize(DefaultPrinter.FONT_SIZE_NORMAL)
                .setText(message)
                // .setNewLinesAfter(1)
                .build())

        val message2 ="\n\nFor: $description\nBillNo: $billNo\nAmount: KES $amount\nPayBillNo: $payBill\nDate: $humanDate\nPrinted By: $username at $zone\n"

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