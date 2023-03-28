package com.aw.forcement.history

import BaseObject
import HistoryAdapter
import Json4Kotlin_Base
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.aw.forcement.R
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_receipt_history.*
import kotlinx.android.synthetic.main.recycler_view.*

class ReceiptHistory : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt_history)

        val response = Gson().fromJson(intent.getStringExtra("result"), Json4Kotlin_Base::class.java)

       /* allReceipt.setOnClickListener {
            val baseList: MutableList<BaseObject> = ArrayList<BaseObject>()
            response.response_data.history[0].allreceipts.forEachIndexed { index, item ->
                baseList.add(item)
            }
            adapter(baseList)
        }

        receiptValid.setOnClickListener {
            val baseList: MutableList<BaseObject> = ArrayList<BaseObject>()
            response.response_data.history[0].validreceipts.forEachIndexed { index, item ->
                baseList.add(item)
            }
            adapter(baseList)
        }

        receiptInvalid.setOnClickListener {
            val baseList: MutableList<BaseObject> = ArrayList<BaseObject>()
            response.response_data.history[0].invalidreceipts.forEachIndexed { index, item ->
                baseList.add(item)
            }
            adapter(baseList)
        }*/

    }

  /*  private fun adapter(mList: List<BaseObject>){
        val adapter =
            HistoryAdapter(this@ReceiptHistory,mList)
        adapter.notifyDataSetChanged()
        recyclerView.layoutManager = LinearLayoutManager(this@ReceiptHistory)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(false)
    }*/
}