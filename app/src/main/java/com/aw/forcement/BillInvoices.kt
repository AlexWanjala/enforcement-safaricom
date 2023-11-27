package com.aw.forcement

import InvoiceSearchAdapter
import Json4Kotlin_Base
import TransSearchAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_transactions_results.*
import kotlinx.android.synthetic.main.recycler_view.*

class BillInvoices : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill_invoices)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        imageClose.setOnClickListener { finish() }

        val response = Gson().fromJson(intent.getStringExtra("result"), Json4Kotlin_Base::class.java)

        runOnUiThread {
            val adapter = InvoiceSearchAdapter(this, response.data.billDetailsList)
            adapter.notifyDataSetChanged()
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter
            recyclerView.setHasFixedSize(false)
        }
    }
}