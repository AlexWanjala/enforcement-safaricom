package com.aw.forcement.others

import Json4Kotlin_Base
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aw.forcement.R
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_business.*

import kotlinx.android.synthetic.main.date.*
import kotlinx.android.synthetic.main.status.*

class Business : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business)

        back.setOnClickListener { finish() }
        val response = Gson().fromJson(intent.getStringExtra("result"), Json4Kotlin_Base::class.java)
        tvBusinessId.text = response.data.permit.businessID
        tvBusinessName.text = response.data.permit.businessName
        tvOwner.text = response.data.permit.telephone1 +"-"+ response.data.permit.telephone2
        tvActivity.text = response.data.permit.brimsCode+"-"+ response.data.permit.businessActivity
        tvPlotNumber.text = response.data.permit.plotNo
        tvPhysicalAddress.text = response.data.permit.physicalAddress
        tvStart.text = response.data.permit.startDate
        tvEnd.text = response.data.permit.endDate
        tvKRAPin.text =response.data.permit.pinNumber
        tvBox.text = response.data.permit.postalCode
        tvDetailedActivity.text = response.data.permit.businessActivityDescription
        tvStatus.text = response.data.permit.status
        tvSBPFee.text = response.data.permit.receiptAmount

    }
}