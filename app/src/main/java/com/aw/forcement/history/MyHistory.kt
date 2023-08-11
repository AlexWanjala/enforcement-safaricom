package com.aw.forcement.history

import Json4Kotlin_Base
import MyHistoryAdapter
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.aw.forcement.R
import com.aw.forcement.tabs.Home
import com.aw.forcement.tabs.Profile
import com.aw.passanger.api.CallBack
import com.aw.passanger.api.biller
import com.aw.passanger.api.executeRequest
import com.aw.passanger.api.getValue
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_my_history.*
import kotlinx.android.synthetic.main.bottom_nav.*
import kotlinx.android.synthetic.main.recycler_view.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class MyHistory : AppCompatActivity() {

    var cal: Calendar = Calendar.getInstance()

     var dateTo =""
     var dateFrom =""
     var history ="Collections"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_history)


        radio_collections.isChecked = true
        radio_collections.setOnClickListener {
            history ="Collections"
            getMyHistory()
        }
        radio_inspection.setOnClickListener {
            history ="Inspections"
            getMyHistory()
        }
        radio_enforcement.setOnClickListener {
            history ="Enforcements"
            getMyHistory()
        }

        DrawableCompat.setTint(DrawableCompat.wrap(imageHistory.drawable), ContextCompat.getColor(this, R.color.bg_button))
        tvHistory.setTextColor(resources.getColor(R.color.bg_button))

        imageHome.setOnClickListener {
            startActivity(Intent(this,Home::class.java))
            finish()
        }

        imageProfile.setOnClickListener {
            startActivity(Intent(this,Profile::class.java))
            finish()
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

        getMyHistory()



    }

    private fun getMyHistory (){
        // progress_circular.visibility = View.VISIBLE
        val formData = listOf(
            "function" to "getMyHistory",
            "history" to history,
            "idNo" to  getValue(this,"idNo").toString(),
            "dateFrom" to dateFrom,//2023-07-01
            "dateTo" to dateTo//2023-08-10
        )
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
                //  runOnUiThread {  progress_circular.visibility = View.GONE }
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                runOnUiThread { recyclerView.adapter = null }
                if(response.success){
                    runOnUiThread {
                        val adapter = MyHistoryAdapter(this@MyHistory, response.data.myHistory)
                        adapter.notifyDataSetChanged()
                        recyclerView.layoutManager = LinearLayoutManager(this@MyHistory)
                        recyclerView.adapter = adapter
                        recyclerView.setHasFixedSize(false)

                        val totalAmount = response.data.myHistory.sumOf{ item -> item.amount.toDouble() }

                        var target = getValue(this@MyHistory,"target").toString()
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
                    }

                }

            }

        })
    }
}