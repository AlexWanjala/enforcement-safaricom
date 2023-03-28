package com.aw.forcement

import HistoryAdapter
import Json4Kotlin_Base
import TransAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.aw.passanger.api.CallBack
import com.aw.passanger.api.executeRequest
import com.aw.passanger.api.parking
import com.aw.passanger.api.paysol
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_parking_history.*
import kotlinx.android.synthetic.main.activity_transactions.*
import kotlinx.android.synthetic.main.activity_transactions.et_search_bar
import kotlinx.android.synthetic.main.recycler_view.*

class Transactions : AppCompatActivity() {
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

    }

    private fun getQueries (keyword: String,range: String){

        // progress_circular.visibility = View.VISIBLE
        val formData = listOf(
            "function" to "getTransactions",
            "companyCode" to "ABSOLUTE",
            "keyword" to keyword,
            "range" to range,
            "token" to "im05WXYH2rwRruPjCICieOs8m4E8IoltnDEhyPUv6bnB9cU60gD48SnJPC6oh7EpsPaAUGC8wqIdtVVjGlWLxqFssshxMHxHjEQJ"
        )
        executeRequest(formData, paysol,object : CallBack {
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
}