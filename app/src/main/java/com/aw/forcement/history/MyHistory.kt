package com.aw.forcement.history

import Json4Kotlin_Base
import MyHistoryAdapter
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.aw.forcement.R
import com.aw.forcement.tabs.Home
import com.aw.passanger.api.CallBack
import com.aw.passanger.api.biller
import com.aw.passanger.api.executeRequest
import com.aw.passanger.api.getValue
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_my_history.*
import kotlinx.android.synthetic.main.recycler_view.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class MyHistory : AppCompatActivity() {

    var cal: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_history)

        imageClose.setOnClickListener {
            startActivity(Intent(this,Home::class.java))
            finish()
        }

        //Date from
        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat = "dd MMMM yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            tv_date_from.text = sdf.format(cal.time)
            Toast.makeText(this,cal.time.toString(),Toast.LENGTH_LONG).show()

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

            val myFormat = "dd MMMM yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            tv_date_to.text = sdf.format(cal.time)
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
            "idNo" to "37694813",
            "dateFrom" to "2023-07-01",
            "dateTo" to "2023-08-10"
        )
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
                //  runOnUiThread {  progress_circular.visibility = View.GONE }
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){
                    runOnUiThread {
                        val adapter = MyHistoryAdapter(this@MyHistory, response.data.myHistory)
                        adapter.notifyDataSetChanged()
                        recyclerView.layoutManager = LinearLayoutManager(this@MyHistory)
                        recyclerView.adapter = adapter
                        recyclerView.setHasFixedSize(false)


                        val totalAmount = response.data.myHistory.sumOf{ item -> item.amount.toDouble() }

                        val df = DecimalFormat("#,##0.00")
                        df.roundingMode = RoundingMode.HALF_UP
                        tv_amount.text ="KES "+ df.format(totalAmount)

                        tv_number.text = response.data.myHistory.size.toString()
                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@MyHistory,response.message, Toast.LENGTH_LONG).show()}

                }

            }

        })
    }
}