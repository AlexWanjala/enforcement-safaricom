package com.aw.forcement

import InvoiceSearchAdapter
import Json4Kotlin_Base
import TransSearchAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.aw.passanger.api.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_bill_invoices.*
import kotlinx.android.synthetic.main.activity_transactions_results.*
import kotlinx.android.synthetic.main.activity_transactions_results.imageClose
import kotlinx.android.synthetic.main.message.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.recycler_view.*

class BillInvoices : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill_invoices)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        imageClose.setOnClickListener { finish() }

        val intent = intent // Assuming you have the intent object already
        val result = intent.getStringExtra("result")
        if (result != null) {
            loadInvoices(result)
        } else {
            getBillsPaginate("","FIREINSPECTION")
        }

        edSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                getBillsPaginate(p0.toString(),"FIREINSPECTION")
            }

        })


    }

    private fun getBillsPaginate(keyword: String,incomeTypePrefix: String){

        progress_circular.visibility = View.VISIBLE
        val formData = listOf(
            "function" to "getBillsPaginate",
            "keyword" to keyword,
            "latitude" to getValue(this,"latitude").toString(),
            "longitude" to getValue(this,"longitude").toString(),
            "idNo" to getValue(this,"idNo").toString(),
            "username" to getValue(this,"username").toString(),
            "addressString" to getValue(this,"addressString").toString(),
            "page" to "1",
            "rows_per_page" to "10",
            "responseName" to "billDetailsList",
            "deviceId" to getDeviceIdNumber(this),
            "incomeTypePrefix" to incomeTypePrefix
        )

        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
                runOnUiThread {  progress_circular.visibility = View.GONE }
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)

                if(response.success){
                    // runOnUiThread { bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED }
                    loadInvoices(result.toString())
                }else{
                    runOnUiThread {
                       Toast.makeText(this@BillInvoices,response.message,Toast.LENGTH_LONG).show()
                    }
                }
            }
            override fun onFailure(result: String?) {

                runOnUiThread {
                    progress_circular.visibility = View.GONE
                    Toast.makeText(this@BillInvoices,result, Toast.LENGTH_LONG).show()
                }
            }

        })

    }

    private fun loadInvoices(result : String){
        val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)

        runOnUiThread {
            val adapter = InvoiceSearchAdapter(this, response.data.billDetailsList)
            adapter.notifyDataSetChanged()
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter
            recyclerView.setHasFixedSize(false)
        }
    }
}