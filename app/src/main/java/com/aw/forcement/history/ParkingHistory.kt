package com.aw.forcement.history

import BaseObject
import HistoryAdapter
import Json4Kotlin_Base
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.aw.forcement.ChangePassword
import com.aw.forcement.R
import com.aw.forcement.SelectZone
import com.aw.forcement.tabs.Home
import com.aw.passanger.api.*
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_parking_history.*
import kotlinx.android.synthetic.main.message.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.recycler_view.*

class ParkingHistory : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parking_history)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        getQueries("","")
    }


    private fun getQueries (keyword: String,range: String){

       // progress_circular.visibility = View.VISIBLE
        val formData = listOf(
            "function" to "getQueries",
            "keyword" to keyword,
            "range" to range,
            "deviceId" to getDeviceIdNumber(this)
        )
        executeRequest(formData, parking,object : CallBack {
            override fun onSuccess(result: String?) {
              //  runOnUiThread {  progress_circular.visibility = View.GONE }
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){
                    runOnUiThread {
                        val adapter = HistoryAdapter(this@ParkingHistory, response.data.queries)
                        adapter.notifyDataSetChanged()
                        recyclerView.layoutManager = LinearLayoutManager(this@ParkingHistory)
                        recyclerView.adapter = adapter
                        recyclerView.setHasFixedSize(false)
                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@ParkingHistory,response.message,Toast.LENGTH_LONG).show()}

                }

            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@ParkingHistory,result, Toast.LENGTH_LONG).show()
                }
            }

        })
    }

    override fun onBackPressed() {
        finishAffinity()
        startActivity(Intent(this, Home::class.java))
        super.onBackPressed()
    }


}