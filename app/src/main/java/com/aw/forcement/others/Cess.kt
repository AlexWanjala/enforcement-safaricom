package com.aw.forcement.others

import Json4Kotlin_Base
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aw.forcement.R
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_cess.*
import kotlinx.android.synthetic.main.activity_receipt.back

class Cess : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cess)
        back.setOnClickListener { finish() }
        val response = Gson().fromJson(intent.getStringExtra("result"), Json4Kotlin_Base::class.java)

        tvIdentifyNo.text =  response.data.receiptDetails.zone+" at "+ response.data.receiptDetails.dateCreated
        tvCustomerCess.text = response.data.receiptInfos[0].customer +" # "+ response.data.receiptDetails.paidBy
        tvBillNo.text = response.data.receiptDetails.billNo
        tvBillTotal.text = response.data.receiptDetails.detailAmount
        tvReducingBalance_.text = response.data.receiptDetails.billBalance
        tvFeeDescription.text =response.data.receiptInfos[0].description
        tvAccountDesc.text =  response.data.receiptDetails.incomeTypeDescription
        tvReceiptNo_.text = response.data.receiptDetails.transactionCode
        tvBillingStatus.text = "FULLY PAID"
    }
}