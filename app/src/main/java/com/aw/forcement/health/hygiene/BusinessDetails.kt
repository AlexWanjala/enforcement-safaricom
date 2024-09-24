package com.aw.forcement.health.hygiene

import Json4Kotlin_Base
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.aw.forcement.R
import com.aw.forcement.fire.FireSafetyRiskAssesmentActivity
import com.aw.passanger.api.CallBack
import com.aw.passanger.api.executeRequest
import com.aw.passanger.api.trade
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_business_details2.*


class BusinessDetails : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_details2)
        val businessID = intent.getStringExtra("businessID")

        btn_next.setOnClickListener {

           startActivity(Intent(this,HygieneCertificateBilling::class.java).putExtra("businessID",businessID))

        }
        btn_previous.setOnClickListener {
            finish()
        }

        closeBottom.setOnClickListener { finish() }

        if (businessID != null) {
            getBusiness(businessID)
        }

    }
    private fun getBusiness(keyword: String){

        val formData = listOf(
            "function" to "getBusiness",
            "businessID" to keyword,
        )
        executeRequest(formData, trade,object : CallBack {
            override fun onSuccess(result: String?) {

                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)

                if(response.success){

                    runOnUiThread {
                        tv_full_names.text = response.data.business.fullNames
                        tv_id_no.text = response.data.business.ownerID
                        tv_phone_number.text = response.data.business.ownerPhone
                        tv_email.text =response.data.business.ownerEmail
                        tv_kra_pin.text =response.data.business.kraPin
                        tv_business_name.text = response.data.business.businessName
                        tv_business_category.text = response.data.business.businessCategory
                        tv_business_sub_category.text =response.data.business.businessSubCategory
                        tv_business_activity.text =response.data.business.businessDes
                        tv_sub_county.text = response.data.business.subCountyName
                        tv_ward.text = response.data.business.wardName
                        tv_physical_address.text = response.data.business.physicalAddress
                        tv_plot_no.text = response.data.business.plotNumber
                        tv_business_ID.text = response.data.business.businessID

                    }


                }
                else{
                    runOnUiThread {

                        Toast.makeText(this@BusinessDetails,response.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
            override fun onFailure(result: String?) {
                runOnUiThread {

                    Toast.makeText(this@BusinessDetails,result, Toast.LENGTH_LONG).show()
                }
            }

        })
    }
}