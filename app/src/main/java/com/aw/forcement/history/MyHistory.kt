package com.aw.forcement.history

import Json4Kotlin_Base
import MyHistoryAdapter
import android.app.DatePickerDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
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
import kotlinx.android.synthetic.main.progressbar.*
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
     var idNo =""
     var message =""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_history)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        idNo = intent.getStringExtra("idNo").toString()

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

        val today = org.joda.time.LocalDate.now()
        val formatter = org.joda.time.format.DateTimeFormat.forPattern("d MMM yyyy")
        val formattedDate = formatter.print(today)

        // Create a Spanned object from an HTML string with an <u> tag
        val spanned = Html.fromHtml("<u> $formattedDate </u>")

        tv_date_from.text = spanned
        tv_date_to.text = spanned

        val formatter2 = org.joda.time.format.DateTimeFormat.forPattern("yyyy-MM-dd")
        val formattedDate2 = formatter2.print(today)


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

        val names = intent.getStringExtra("names").toString().toLowerCase().split(" ").joinToString(" ") { it.capitalize() }

        if(intent.getStringExtra("bottomBar")=="hide"){
            tv_title.text = names+"'s Logs"
            bottomBar.visibility = View.GONE
        }else{
            tv_title.text = names+" Logs"
            bottomBar.visibility = View.VISIBLE
        }


        getMyHistory()

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
                        val adapter = MyHistoryAdapter(this@MyHistory, response.data.myHistory)
                        adapter.notifyDataSetChanged()
                        recyclerView.layoutManager = LinearLayoutManager(this@MyHistory)
                        recyclerView.adapter = adapter
                        recyclerView.setHasFixedSize(false)

                        val totalAmount = response.data.myHistory.sumOf{ item -> item.amount.toDouble() }


                        targetMargin.text = "${response.data.myOverview.units} ${response.data.myOverview.difference}"
                        message2.text = response.data.myOverview.message2

                        tv_percentage.text = response.data.myOverview.percentage

                        val df = DecimalFormat("#,##0.00")
                        df.roundingMode = RoundingMode.HALF_UP
                        tv_amount.text ="KES "+ df.format(totalAmount)

                        tv_number.text = response.data.myHistory.size.toString()

                        val list =  response.data.myOverview.message2

                        if(list=="Under Performing"){

                            val colorStateList1 = ColorStateList.valueOf(Color.parseColor("#F44242"))
                            banner.backgroundTintList = colorStateList1


                            val colorStateList = ColorStateList.valueOf(Color.parseColor("#FFF700"))
                            message2.backgroundTintList = colorStateList

                        }
                        else if(list=="You are Below Average"){

                            val colorStateList1 = ColorStateList.valueOf(Color.parseColor("#F4B342"))
                            banner.backgroundTintList = colorStateList1

                            val colorStateList = ColorStateList.valueOf(Color.parseColor("#F44242"))
                            message2.backgroundTintList = colorStateList
                            message2.setTextColor(Color.parseColor("#FFFFFF"))

                            tv_amount.setTextColor(Color.parseColor("#2B2F34"))
                            tv_percentage.setTextColor(Color.parseColor("#2B2F34"))
                            tv_header_line.setTextColor(Color.parseColor("#2B2F34"))
                            targetMargin.setTextColor(Color.parseColor("#2B2F34"))
                            tv_message_.setTextColor(Color.parseColor("#2B2F34"))
                            tv_number.setTextColor(Color.parseColor("#2B2F34"))


                        }
                        else if(list=="Performing"){

                            val colorStateList1 = ColorStateList.valueOf(Color.parseColor("#0067CF"))
                            banner.backgroundTintList = colorStateList1

                            val colorStateList = ColorStateList.valueOf(Color.parseColor("#FFF700"))
                            message2.backgroundTintList = colorStateList
                            message2.setTextColor(Color.parseColor("#040035"))

                        }
                        else if(list=="Top Performer"){

                            val colorStateList1 = ColorStateList.valueOf(Color.parseColor("#05A50A"))
                            banner.backgroundTintList = colorStateList1


                            val colorStateList = ColorStateList.valueOf(Color.parseColor("#FFF700"))
                            message2.backgroundTintList = colorStateList

                            message2.setTextColor(Color.parseColor("#040035"))

                        }
                        else{
                           // holder.layout.setBackgroundResource(R.drawable.bg_blue)
                        }

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
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@MyHistory,result, Toast.LENGTH_LONG).show()
                }
            }

        })
    }
}