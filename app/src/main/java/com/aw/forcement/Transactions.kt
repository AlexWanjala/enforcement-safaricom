package com.aw.forcement

import Json4Kotlin_Base
import TransAdapter
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.aw.passanger.api.*
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.gson.Gson
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.printable.ImagePrintable
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.data.printer.DefaultPrinter
import com.mazenrashed.printooth.ui.ScanningActivity
import com.mazenrashed.printooth.utilities.Printing
import com.mazenrashed.printooth.utilities.PrintingCallback
import kotlinx.android.synthetic.main.activity_transactions.*
import kotlinx.android.synthetic.main.activity_transactions.et_search_bar
import kotlinx.android.synthetic.main.recycler_view.*
import net.glxn.qrgen.android.QRCode
import java.text.SimpleDateFormat
import java.util.*

class Transactions : AppCompatActivity() {

    private var printing : Printing? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transactions)
        //when floationg acition button is clicked
        btnDatePicker2.setOnClickListener {

            // Initiation date picker with
            // MaterialDatePicker.Builder.datePicker()
            // and building it using build()
            val datePicker = MaterialDatePicker.Builder.dateRangePicker().build()
            datePicker.show(supportFragmentManager, "DatePicker")

            // Setting up the event for when ok is clicked
            datePicker.addOnPositiveButtonClickListener {
                //  Toast.makeText(this, "${datePicker.headerText} is selected", Toast.LENGTH_LONG).show()
                Toast.makeText(this, "${datePicker.selection.toString()} is selected", Toast.LENGTH_LONG).show()
                println("##Response${datePicker.headerText} :::: ${datePicker.selection.toString()}")
                getQueries("",datePicker.headerText)
            }

            // Setting up the event for when cancelled is clicked
            datePicker.addOnNegativeButtonClickListener {
                Toast.makeText(this, "${datePicker.headerText} is cancelled", Toast.LENGTH_LONG).show()
            }

            // Setting up the event for when back button is pressed
            datePicker.addOnCancelListener {
                Toast.makeText(this, "Date Picker Cancelled", Toast.LENGTH_LONG).show()
            }
        }

        et_search_bar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                getQueries(s.toString(),"")
            }

        })

        getQueries("","")

        //Bluetooth printer
        if (Printooth.hasPairedPrinter())
            printing = Printooth.printer()
        initListeners()

    }

    fun getQueries (keyword: String,range: String){

        // progress_circular.visibility = View.VISIBLE
        val formData = listOf(
            "function" to "getTransactions",
            "keyword" to keyword,
            "range" to range,
            "idNo" to getValue(this,"idNo").toString(),
            "token" to "im05WXYH2rwRruPjCICieOs8m4E8IoltnDEhyPUv6bnB9cU60gD48SnJPC6oh7EpsPaAUGC8wqIdtVVjGlWLxqFssshxMHxHjEQJ"
        )
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
                //  runOnUiThread {  progress_circular.visibility = View.GONE }
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){
                    runOnUiThread {
                        val adapter = TransAdapter(this@Transactions, response.data.transactions)
                        adapter.notifyDataSetChanged()
                        recyclerView.layoutManager = LinearLayoutManager(this@Transactions)
                        recyclerView.adapter = adapter
                        recyclerView.setHasFixedSize(false)
                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@Transactions,response.message,Toast.LENGTH_LONG).show()}

                }

            }

        })
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
                        save(this@Transactions,"incomeTypeDescription",response.data.billDetails.incomeTypeDescription)
                        save(this@Transactions,"description",response.data.billInfo[0].description)
                        printReceipt()
                    }

                }else{

                    runOnUiThread {
                        save(this@Transactions,"incomeTypeDescription",getValue(this@Transactions,"ref").toString())
                        printReceipt()
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
                    this@Transactions,
                    ScanningActivity::class.java
                ),
            )
        else printDetails()
    }
    private fun initListeners() {
        /* callback from printooth to get printer process */
        printing?.printingCallback = object : PrintingCallback {
            override fun connectingWithPrinter() {
                Toast.makeText(this@Transactions, "Connecting with printer", Toast.LENGTH_SHORT).show()
            }

            override fun printingOrderSentSuccessfully() {
                Toast.makeText(this@Transactions, "Order sent to printer", Toast.LENGTH_SHORT).show()
            }

            override fun connectionFailed(error: String) {
                Toast.makeText(this@Transactions, "Failed to connect printer", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: String) {
                Toast.makeText(this@Transactions, error, Toast.LENGTH_SHORT).show()
            }

            override fun onMessage(message: String) {
                Toast.makeText(this@Transactions, "Message: $message", Toast.LENGTH_SHORT).show()
            }

            override fun disconnected() {
                Toast.makeText(this@Transactions, "Disconnected Printer", Toast.LENGTH_SHORT).show()
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

        val title2 ="COUNTY GOVERNMENT OF KISII\n\n#\n\n\n"
        add(
            TextPrintable.Builder()
                .setText(title2)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .build())


        val bmp = BitmapFactory.decodeResource(resources, R.drawable.county_logo_print)
        val argbBmp = bmp.copy(Bitmap.Config.ARGB_8888, false)
        val scaledLogo = Bitmap.createScaledBitmap(argbBmp, 145, 180, true)
        add(
            ImagePrintable.Builder(scaledLogo)
                .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
                .build())



          val transactioncode = getValue(this@Transactions,"transaction_code")
          val amount = getValue(this@Transactions,"amount")
          val ref = getValue(this@Transactions,"ref")
          val username = getValue(this@Transactions,"username")
          val names = getValue(this@Transactions,"names")
          val phone = getValue(this@Transactions,"phone")
          val incomeTypeDescription = getValue(this@Transactions,"incomeTypeDescription")?.capitalize()
          val description = getValue(this@Transactions,"description")

        val input = getValue(this@Transactions,"date")
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEE dd MMM yy hh:mma", Locale.getDefault())
        val date = input?.let { inputFormat.parse(it) }
        val humanDate = date?.let { outputFormat.format(it) }


        val message ="\n\nFor: $description #Mpesa\nTransaction Code: $transactioncode\nAmount: KES $amount\nPayer: $names\nDate: $humanDate\nPrinted By: $username at Kisii town\n"

        add(
            TextPrintable.Builder()
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

        val footer ="\nLipa Ushuru Tujenge\n\n#EndlessPotential\n\n\n\n\n\n\n"
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