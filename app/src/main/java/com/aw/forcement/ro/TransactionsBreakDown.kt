package com.aw.forcement.ro

import CollectionBreakDownAdapter
import Json4Kotlin_Base
import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.aw.forcement.R
import com.aw.passanger.api.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_my_history.radio_collections
import kotlinx.android.synthetic.main.activity_my_history.radio_enforcement
import kotlinx.android.synthetic.main.activity_my_history.radio_inspection
import kotlinx.android.synthetic.main.activity_my_history.targetMargin
import kotlinx.android.synthetic.main.activity_my_history.tv_amount
import kotlinx.android.synthetic.main.activity_my_history.tv_date_from
import kotlinx.android.synthetic.main.activity_my_history.tv_date_to
import kotlinx.android.synthetic.main.activity_my_history.tv_number
import kotlinx.android.synthetic.main.activity_transactions_break_down.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.recycler_view.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class TransactionsBreakDown : AppCompatActivity() {

     var cal: Calendar = Calendar.getInstance()
     var dateTo =""
     var dateFrom =""
     var collectionBy ="STREAMS"
     var idNo =""
    var message =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transactions_break_down)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        imageBack.setOnClickListener { finish() }

        radio_streams.isChecked = true
        radio_streams.setOnClickListener {
            collectionBy ="STREAMS"
            revenueCollectionsBreakdown()
            message ="Collections Breakdown by Revenue streams"
        }
        radio_zones.setOnClickListener {
            collectionBy ="ZONES"
            revenueCollectionsBreakdown()
            message ="Collections Breakdown by Collection Zones"
        }
        radio_agent.setOnClickListener {
            collectionBy ="AGENTS"
            revenueCollectionsBreakdown()
            message ="Collections Breakdown by Revenue Agents"
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
            revenueCollectionsBreakdown()


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
            revenueCollectionsBreakdown()
        }
        tv_date_to.setOnClickListener {

            DatePickerDialog(this,
                dateSetListener2,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        revenueCollectionsBreakdown()

    }

    private fun revenueCollectionsBreakdown (){
        progress_circular.visibility = View.VISIBLE
        val formData = listOf(
            "function" to "revenueCollectionsBreakdown",
            "collectionBy" to collectionBy,
            "subCountyID" to  getValue(this,"subCountyID").toString(),
            "dateFrom" to dateFrom,//2023-07-01
            "dateTo" to dateTo,//2023-08-10
            "deviceId" to getDeviceIdNumber(this)
        )
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
                runOnUiThread {  progress_circular.visibility = View.GONE }
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                runOnUiThread { recyclerView.adapter = null }
                if(response.success){
                    runOnUiThread {
                        tv_message_header.text = message+" (${response.data.collectionsBreakdown.size})"

                        val totalAmount = response.data.collectionsBreakdown.sumOf{ item -> item.amount.toDouble() }

                        val adapter = CollectionBreakDownAdapter(this@TransactionsBreakDown, response.data.collectionsBreakdown,totalAmount)
                        adapter.notifyDataSetChanged()
                        recyclerView.layoutManager = LinearLayoutManager(this@TransactionsBreakDown)
                        recyclerView.adapter = adapter
                        recyclerView.setHasFixedSize(false)



                        var target = getValue(this@TransactionsBreakDown,"target").toString()
                        if(target.isEmpty()){
                            target = "0"
                        }

                        val targetMarginValue = target.toDouble() - totalAmount
                        targetMargin.text = targetMarginValue.toString()

                        val df = DecimalFormat("#,##0.00")
                        df.roundingMode = RoundingMode.HALF_UP
                        tv_amount.text ="KES "+ df.format(totalAmount)

                        tv_number.text = response.data.collectionsBreakdown.size.toString()
                    }

                }else{
                    runOnUiThread {
                      //Toast.makeText(this@MyHistory,response.message, Toast.LENGTH_LONG).show()
                        tv_message_header.text = collectionBy+" (0)"
                        tv_number.text ="0"
                        tv_amount.text ="KES 0.0"
                        targetMargin.text ="0"
                    }

                }

            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@TransactionsBreakDown,result, Toast.LENGTH_LONG).show()
                }
            }

        })
    }
}