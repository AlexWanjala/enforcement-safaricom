package com.aw.forcement.others

import Json4Kotlin_Base
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aw.forcement.R
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_receipt.*
import kotlinx.android.synthetic.main.activity_receipt.back
import kotlinx.android.synthetic.main.status.*

class Receipt : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt)
        back.setOnClickListener { finish() }
        val response = Gson().fromJson(intent.getStringExtra("result"), Json4Kotlin_Base::class.java)
        tvReceiptNo.text = response.data.receiptDetails.receiptNo
        tvCustomer.text = response.data.receiptDetails.paidBy
        tvPaymentStatus.text = response.data.receiptDetails.status
        tvStatus.text = response.data.receiptDetails.status
        tvAmount.text = "KES "+response.data.receiptDetails.receiptAmount
        tvReducingBalance.text ="KES "+ response.data.receiptDetails.billBalance
        tvIncomeType.text = response.data.receiptDetails.incomeTypeDescription
        tvBillDate.text = response.data.receiptDetails.dateCreated
        tvPaymentData.text = response.data.receiptDetails.dateModified
        tvReceiptAmount.text = "KES "+ response.data.receiptDetails.receiptAmount
    }
}