package com.aw.forcement.sbp.applications

import Json4Kotlin_Base
import SbpAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.aw.forcement.R
import com.aw.passanger.api.CallBack
import com.aw.passanger.api.executeRequest
import com.aw.passanger.api.getValue
import com.aw.passanger.api.trade
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_applications.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.recycler_view.*


class Applications : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_applications)

        tv_message_header.text = getValue(this,"header")

        getApplications()
    }

    private fun getApplications (){
        runOnUiThread {  progress_circular.visibility = View.VISIBLE }
        val formData = listOf(
            "function" to "getApplications",
            "page" to "1",
            "rows_per_page" to "100",
            "search" to ""
        )
        executeRequest(formData, trade,object : CallBack {
            override fun onSuccess(result: String?) {
                runOnUiThread {  progress_circular.visibility = View.GONE }
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){
                    runOnUiThread {
                        val adapter = SbpAdapter(this@Applications, response.data.businesses)
                        adapter.notifyDataSetChanged()
                        recyclerView.layoutManager = LinearLayoutManager(this@Applications)
                        recyclerView.adapter = adapter
                        recyclerView.setHasFixedSize(false)
                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@Applications,response.message, Toast.LENGTH_LONG).show()}

                }

            }

        })
    }
}