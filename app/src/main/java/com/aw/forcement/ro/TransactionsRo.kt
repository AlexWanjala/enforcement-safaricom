package com.aw.forcement.ro

import Json4Kotlin_Base
import MyHistoryAdapter
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.aw.forcement.R
import com.aw.forcement.adapters.StreamAdapter
import com.aw.passanger.api.CallBack
import com.aw.passanger.api.biller
import com.aw.passanger.api.executeRequest
import com.aw.passanger.api.getValue
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_transactions_ro.*
import kotlinx.android.synthetic.main.bottom_nav.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.recycler_view.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class TransactionsRo : AppCompatActivity() {

    val cal: Calendar = Calendar.getInstance()
    var history ="Collections"
    var idNo =""
    var message =""

    var dateTo =""
    var dateFrom =""
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transactions_ro)
        message ="My Collections"

        radio_collections.isChecked = true
        radio_collections.setOnClickListener {
            history ="Collections"
            message ="My collections"
            getMyHistory()
        }
        radio_inspection.setOnClickListener {
            history ="Inspections"
            message ="My payments verifications and inspections"
            getMyHistory()
        }
        radio_enforcement.setOnClickListener {
            history ="Enforcements"
            message ="My Enforcements and logs"
            getMyHistory()
        }

        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("d MMM yyyy")
        val formattedDate = today.format(formatter)
        // Create a Spanned object from an HTML string with an <u> tag
        val spanned = Html.fromHtml("<u> $formattedDate </u>")

        tv_date_from.text = spanned
        tv_date_to.text = spanned

        val formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formattedDate2 = today.format(formatter2)
        dateFrom = formattedDate2
        dateTo = formattedDate2


        //Date from
        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)


            val myFormat1 = "yyyy-MM-dd" //
            val sdf1 = SimpleDateFormat(myFormat1, Locale.US)
            dateFrom = sdf1.format(cal.time)


            val myFormat = "dd MMMM yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            val spanned = Html.fromHtml("<u> ${sdf.format(cal.time)} </u>")
            tv_date_from.text = spanned
            idNo = getValue(this,"idNo").toString()
            getMyHistory()


        }
        tv_date_from.setOnClickListener {

            DatePickerDialog(this,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()

        }

        //Date to
        val dateSetListener2 = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat1 = "yyyy-MM-dd" //
            val sdf1 = SimpleDateFormat(myFormat1, Locale.US)
            dateTo = sdf1.format(cal.time)

            val myFormat = "dd MMMM yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            val spanned = Html.fromHtml("<u> ${sdf.format(cal.time)} </u>")
            tv_date_to.text = spanned
            idNo =  getValue(this,"idNo").toString()
            getMyHistory()
        }
        tv_date_to.setOnClickListener {

            DatePickerDialog(this,
                dateSetListener2,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }


        val idNo1 = intent.getStringExtra("idNo") ?: ""
        if (idNo1.isEmpty()) {
            idNo = getValue(this,"idNo").toString()
            getMyHistory()
        }else{
            idNo = intent.getStringExtra("idNo").toString()
            getMyHistory()
            val names = intent.getStringExtra("names").toString().toLowerCase().split(" ").joinToString(" ") { it.capitalize() }
            tv_title.text = names+"'s Logs"
            bottomBar.visibility = View.GONE
        }

        getStreams()

    }
    private fun getStreams (){
        runOnUiThread { progress_circular.visibility = View.VISIBLE }
        val formData = listOf(
            "function" to "getStreams",
            "subCountyID" to  getValue(this,"subCountyID").toString(),
            "dateFrom" to dateFrom,//2023-07-01
            "dateTo" to dateTo//2023-08-10
        )
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
                runOnUiThread {
                    progress_circular.visibility = View.GONE
                    recycler_view_radio.adapter = null
                }
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){
                    runOnUiThread {
                        val adapter = StreamAdapter(this@TransactionsRo, response.data.streams)
                        adapter.notifyDataSetChanged()
                        recycler_view_radio.layoutManager = LinearLayoutManager(this@TransactionsRo,LinearLayoutManager.HORIZONTAL,false)
                        recycler_view_radio.adapter = adapter
                        recycler_view_radio.setHasFixedSize(false)

                    }

                }else{
                    runOnUiThread {
                        //Toast.makeText(this@MyHistory,response.message, Toast.LENGTH_LONG).show() "
                    }

                }

            }

        })
    }
    private fun getMyHistory (){
        runOnUiThread { progress_circular.visibility = View.VISIBLE }
        val formData = listOf(
            "function" to "getMyHistory",
            "history" to history,
            "idNo" to  idNo,
            "dateFrom" to dateFrom,//2023-07-01
            "dateTo" to dateTo//2023-08-10
        )
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
                runOnUiThread {
                    progress_circular.visibility = View.GONE
                    recyclerView.adapter = null
                }
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){
                    runOnUiThread {
                        tv_message_header.text =message+" (${response.data.myHistory.size})"
                        val adapter = MyHistoryAdapter(this@TransactionsRo, response.data.myHistory)
                        adapter.notifyDataSetChanged()
                        recyclerView.layoutManager = LinearLayoutManager(this@TransactionsRo)
                        recyclerView.adapter = adapter
                        recyclerView.setHasFixedSize(false)

                        val totalAmount = response.data.myHistory.sumOf{ item -> item.amount.toDouble() }

                        var target = getValue(this@TransactionsRo,"target").toString()
                        if(target.isEmpty()){
                            target = "0"
                        }

                        val targetMarginValue = target.toDouble() - totalAmount
                        targetMargin.text = targetMarginValue.toString()

                        val df = DecimalFormat("#,##0.00")
                        df.roundingMode = RoundingMode.HALF_UP
                        tv_amount.text ="KES "+ df.format(totalAmount)

                        tv_number.text = response.data.myHistory.size.toString()
                    }

                }else{
                    runOnUiThread {
                        //Toast.makeText(this@MyHistory,response.message, Toast.LENGTH_LONG).show()
                        tv_number.text ="0"
                        tv_amount.text ="KES 0.0"
                        targetMargin.text ="0"
                        tv_message_header.text =message+" (0)"
                    }

                }

            }

        })
    }
}