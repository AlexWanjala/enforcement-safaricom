package com.aw.forcement.ro

import Json4Kotlin_Base
import SubCountyRevAdapter
import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import androidx.recyclerview.widget.LinearLayoutManager
import com.aw.forcement.R
import com.aw.passanger.api.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_my_history.*
import kotlinx.android.synthetic.main.activity_my_history.tv_date_from
import kotlinx.android.synthetic.main.activity_my_history.tv_date_to
import kotlinx.android.synthetic.main.activity_total_county_collection.*
import kotlinx.android.synthetic.main.recycler_view.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class TotalCountyCollection : AppCompatActivity() {

    var cal: Calendar = Calendar.getInstance()
    var dateTo =""
    var dateFrom =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_total_county_collection)

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
            getSubCountiesRevenue()


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
            getSubCountiesRevenue()
        }
        tv_date_to.setOnClickListener {

            DatePickerDialog(this,
                dateSetListener2,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)).show()
        }



        getSubCountiesRevenue()
    }

    private fun getSubCountiesRevenue (){
        // progress_circular.visibility = View.VISIBLE
        val formData = listOf(
            "function" to "getSubCountiesRevenue",
            "keyword" to "",
            "page" to  "1",
            "rows_per_page" to "20",
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
                        val adapter = SubCountyRevAdapter(this@TotalCountyCollection, response.data.subCountiesRevenue)
                        adapter.notifyDataSetChanged()
                        recyclerView.layoutManager = LinearLayoutManager(this@TotalCountyCollection)
                        recyclerView.adapter = adapter
                        recyclerView.setHasFixedSize(false)

                        val totalAmount = response.data.subCountiesRevenue.sumOf{ item -> item.amountTotal.toDouble() }
                        val totalTarget = response.data.subCountiesRevenue.sumOf{ item -> item.target.toDouble() }

                        val progress = calculateProgress(totalAmount.toInt(),totalTarget.toInt())

                        tv_total_amount.text ="KES "+ formatNumber(totalAmount.toInt())
                        tv_total_progress.text = progress.toString() + "%"
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

    private fun calculateProgress(collected: Int, target: Int): Double {
        if(target==0){
            return 0.0
        }
        val progress = (collected.toDouble() / target.toDouble()) * 100
        val decFormat = DecimalFormat("#,##0.00")
        decFormat.roundingMode = RoundingMode.HALF_UP
        return decFormat.format(progress).toDouble()
    }
}