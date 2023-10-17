package com.aw.forcement.history

import BaseObject
import HistoryAdapter
import Json4Kotlin_Base
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.aw.forcement.R
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_business_histoty.*
import kotlinx.android.synthetic.main.recycler_view.*

class BusinessHistoty : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_histoty)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val response = Gson().fromJson(intent.getStringExtra("result"), Json4Kotlin_Base::class.java)

      /*  allSbp.setOnClickListener {
            val baseList: MutableList<BaseObject> = ArrayList<BaseObject>()
            response.response_data.history[0].allbusinesses.forEachIndexed { index, item ->
                baseList.add(item)
            }
            adapter(baseList)
        }

        sbpValid.setOnClickListener {
            val baseList: MutableList<BaseObject> = ArrayList<BaseObject>()
            response.response_data.history[0].validbusinesses.forEachIndexed { index, item ->
                baseList.add(item)
            }
            adapter(baseList)
        }

        sbpInvalid.setOnClickListener {
            val baseList: MutableList<BaseObject> = ArrayList<BaseObject>()
            response.response_data.history[0].invalidbusinesses.forEachIndexed { index, item ->
                baseList.add(item)
            }
            adapter(baseList)
        }*/
    }

   /* private fun adapter(mList: List<BaseObject>){
        val adapter =
            HistoryAdapter(this@BusinessHistoty,mList)
        adapter.notifyDataSetChanged()
        recyclerView.layoutManager = LinearLayoutManager(this@BusinessHistoty)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(false)
    }*/
}