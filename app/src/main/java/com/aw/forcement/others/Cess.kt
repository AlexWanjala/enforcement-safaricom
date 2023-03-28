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
       /* tvIdentifyNo.text = response.response_data.enforcebyplatenumber.identifyNo
        tvCustomerCess.text = response.response_data.enforcebyplatenumber.customer
        tvBillNo.text = response.response_data.enforcebyplatenumber.billNo
        tvBillTotal.text = response.response_data.enforcebyplatenumber.billTotal
        tvReducingBalance_.text = response.response_data.enforcebyplatenumber.reducingBalance
        tvFeeDescription.text = response.response_data.enforcebyplatenumber.feeDescription
        tvAccountDesc.text = response.response_data.enforcebyplatenumber.accountDesc
        tvReceiptNo_.text = response.response_data.enforcebyplatenumber.receiptNo
        tvBillingStatus.text = response.response_data.enforcebyplatenumber.billingstatus*/
    }
}