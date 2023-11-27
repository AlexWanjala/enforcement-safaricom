package com.aw.forcement.others

import Json4Kotlin_Base
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.aw.forcement.R
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_business.*

import kotlinx.android.synthetic.main.date.*
import kotlinx.android.synthetic.main.status.*

class Business : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        back.setOnClickListener { finish() }
        val response = Gson().fromJson(intent.getStringExtra("result"), Json4Kotlin_Base::class.java)

        tvBusinessId.text = response.data.permit.businessID
        tvBusinessName.text = response.data.permit.businessName

        tvOwner.text = response.data.permit.ownerPhone +"-"+ response.data.permit.contactPersonNames
        tvActivity.text =  response.data.permit.businessCategory
        tvPlotNumber.text = response.data.permit.plotNumber
        tvPhysicalAddress.text = response.data.permit.physicalAddress
        tvStart.text = response.data.permit.startDate
        tvEnd.text = response.data.permit.endDate
        tvKRAPin.text =response.data.permit.kraPin
        tvBox.text = response.data.permit.postalCode
        tvDetailedActivity.text = response.data.permit.businessSubCategory
        tvStatus.text = response.data.permit.status
        tvSBPFee.text = response.data.permit.receiptAmount
        tvStatus.text =  response.data.permit.stageStatus

    }
}