package com.aw.forcement.health.hygiene

import Json4Kotlin_Base
import RequirementsAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.aw.forcement.R
import com.aw.passanger.api.CallBack
import com.aw.passanger.api.executeRequest
import com.aw.passanger.api.health
import com.aw.passanger.api.trade
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_hygiene_certificate_billing.*
import kotlinx.android.synthetic.main.activity_requirements.*
import kotlinx.android.synthetic.main.activity_requirements.imageClose

class Requirements : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_requirements)

        imageClose.setOnClickListener { finish() }

        getRequirements()
    }


    private fun getRequirements(){

        val formData = listOf(
            "function" to "getRequirements"
        )
        executeRequest(formData, health,object : CallBack {
            override fun onSuccess(result: String?) {

                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)

                if(response.success){

                    runOnUiThread {
                        val adapter = RequirementsAdapter(this@Requirements, response.data.requirements)
                        adapter.notifyDataSetChanged()
                        recyclerView.layoutManager = LinearLayoutManager(this@Requirements)
                        recyclerView.adapter = adapter
                        recyclerView.setHasFixedSize(false)
                    }

                    // runOnUiThread { bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED }
                }
                else{
                    runOnUiThread {
                        recyclerView.adapter = null
                        Toast.makeText(this@Requirements,response.message,Toast.LENGTH_LONG).show()
                    }
                }
            }
            override fun onFailure(result: String?) {
                runOnUiThread {

                    Toast.makeText(this@Requirements,result, Toast.LENGTH_LONG).show()
                }
            }

        })
    }
}