package com.aw.forcement.plotrent

import Json4Kotlin_Base
import PlotAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.aw.forcement.R
import com.aw.passanger.api.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_applications.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.recycler_view.*

class PlotRent : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plot_rent)

        closeBottom.setOnClickListener { finish() }

        edSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if(p0?.length!! >3){
                    getPlots(p0.toString())
                }

            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        getPlots("")

    }

    private fun getPlots (search: String){

        runOnUiThread {  progress_circular.visibility = View.VISIBLE }
        val formData = listOf(
            "function" to "getPlots",
            "keyword" to   search,
            "deviceId" to getDeviceIdNumber(this),
            "rows_per_page" to "100",
            "page" to "1"
        )
        executeRequest(formData, plotrent,object : CallBack {
            override fun onSuccess(result: String?) {
                runOnUiThread {  progress_circular.visibility = View.GONE }
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){
                    runOnUiThread {
                        val adapter = PlotAdapter(this@PlotRent, response.data.plots)
                        adapter.notifyDataSetChanged()
                        recyclerView.layoutManager = LinearLayoutManager(this@PlotRent)
                        recyclerView.adapter = adapter
                        recyclerView.setHasFixedSize(false)
                    }
                }else{
                    runOnUiThread { Toast.makeText(this@PlotRent,response.message, Toast.LENGTH_LONG).show() }
                }
            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@PlotRent,result, Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}