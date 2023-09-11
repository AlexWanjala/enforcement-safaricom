package com.aw.forcement.sbp.applications

import Json4Kotlin_Base
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.aw.forcement.R
import com.aw.forcement.adapters.AdapterStatus
import com.aw.passanger.api.CallBack
import com.aw.passanger.api.executeRequest
import com.aw.passanger.api.getValue
import com.aw.passanger.api.trade
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_applications.*
import kotlinx.android.synthetic.main.activity_applications_detail.*
import kotlinx.android.synthetic.main.activity_applications_detail.tv_message_header
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.recycler_view.*


class ApplicationsDetail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_applications_detail)
        tv_message_header.text = getValue(this,"header")
        getBusinessDetails()
    }


    private fun getBusinessDetails (){
        runOnUiThread {  progress_circular.visibility = View.VISIBLE }
        val formData = listOf(
            "function" to "getBusinessDetails",
            "id" to intent.getStringExtra("id").toString(),
        )
        executeRequest(formData, trade,object : CallBack {
            override fun onSuccess(result: String?) {
                runOnUiThread {  progress_circular.visibility = View.GONE }
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){
                    runOnUiThread {
                        tv_business_name.text = response.data.business.businessName
                        tv_address.text = response.data.business.physicalAddress

                        val adapter = AdapterStatus(this@ApplicationsDetail, response.data.statuses)
                        adapter.notifyDataSetChanged()
                        recyclerView.layoutManager = LinearLayoutManager(this@ApplicationsDetail)
                        recyclerView.adapter = adapter
                        recyclerView.setHasFixedSize(false)

                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@ApplicationsDetail,response.message, Toast.LENGTH_LONG).show()}

                }

            }

        })
    }
}


