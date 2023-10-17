package com.aw.forcement.ro

import Json4Kotlin_Base
import UsersRoAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.aw.forcement.R
import com.aw.passanger.api.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main_ro.*
import kotlinx.android.synthetic.main.activity_main_ro.radio_active
import kotlinx.android.synthetic.main.activity_main_ro.radio_inactive
import kotlinx.android.synthetic.main.activity_main_ro.radio_logged_out
import kotlinx.android.synthetic.main.activity_main_ro.recyclerView3
import kotlinx.android.synthetic.main.activity_revenue_agents_logs.*
import kotlinx.android.synthetic.main.progressbar.*


class RevenueAgentsLogs : AppCompatActivity() {

    var category =""
    var status ="Inactive"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_revenue_agents_logs)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        tv_title.text = getValue(this,"subCountyName")


        radio_all.setOnClickListener {category =""  }
        radio_collectors.setOnClickListener {
            category ="COLLECTOR"
            getUsersBySubCounty()
        }
        radio_inspectors.setOnClickListener {
            category ="INSPECTOR"
            getUsersBySubCounty()
        }
        radio_enforcers.setOnClickListener {
            category ="ENFORCEMENT"
            getUsersBySubCounty()
        }


        radio_active.setOnClickListener {
            status = "Active"
            getUsersBySubCounty()

        }
        radio_inactive.setOnClickListener {
            status = "Inactive"
            getUsersBySubCounty()
        }
        radio_logged_out.setOnClickListener {
            status = "Logged Out"
            getUsersBySubCounty()
        }
        getUsersBySubCounty()
        tvBack.setOnClickListener { finish() }

    }

    override fun onResume() {
        try {
            if(getValue(this,"getUsersBySubCounty").toString().isNotEmpty()){
                updateUserUI(getValue(this,"getUsersBySubCounty").toString(), getValue(this,"status").toString())
            }
        }catch (ex: Exception ){
            save(this,"getUsersBySubCounty","")
        }
        super.onResume()
    }
    private fun getUsersBySubCounty (){
        progress_circular.visibility = View.VISIBLE
        val formData = listOf(
            "function" to "getUsersBySubCounty",
            "subCountyID" to  getValue(this,"subCountyID").toString(),
            "status" to  status,
            "category" to category
        )
        executeRequest(formData, authentication,object : CallBack {
            override fun onSuccess(result: String?) {
                runOnUiThread {   progress_circular.visibility = View.GONE }
                save(this@RevenueAgentsLogs,"getUsersBySubCounty",result)
                save(this@RevenueAgentsLogs,"status",status)
                if (result != null) {
                    updateUserUI(result,status)
                }
            }

        })
    }
    fun updateUserUI(result: String,status: String){
        val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
        runOnUiThread { recyclerView3.adapter = null }
        if(response.success){
            runOnUiThread {
                val adapter = UsersRoAdapter(this@RevenueAgentsLogs, response.data.users)
                if (status === "Active") {
                    radio_active.text =status+" (${response.data.users.size})"
                }

                if (status === "Inactive") {
                    radio_inactive.text =status+" (${response.data.users.size})"
                }

                if (status === "Logged Out") {
                    radio_logged_out.text =status+" (${response.data.users.size})"
                }
                adapter.notifyDataSetChanged()
                recyclerView3.layoutManager = LinearLayoutManager(this@RevenueAgentsLogs)
                recyclerView3.adapter = adapter
                recyclerView3.setHasFixedSize(false)
            }

        }else{
            runOnUiThread {
                //Toast.makeText(this@MyHistory,response.message, Toast.LENGTH_LONG).show()
            }

        }
    }

}