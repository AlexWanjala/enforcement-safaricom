package com.aw.forcement.clamping

import AdapterClamp
import Json4Kotlin_Base
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.aw.forcement.R
import com.aw.passanger.api.CallBack
import com.aw.passanger.api.executeRequest
import com.aw.passanger.api.parking
import com.aw.passanger.api.trade
import com.google.gson.Gson
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.recycler_view.*

class ClampingModule : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clamping_module)


        getClamped()
    }

    private fun getClamped (){
        runOnUiThread {  progress_circular.visibility = View.VISIBLE }
        val formData = listOf(
            "function" to "getClamped",

        )
        executeRequest(formData, parking,object : CallBack {
            override fun onSuccess(result: String?) {
                runOnUiThread {  progress_circular.visibility = View.GONE }
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){
                    runOnUiThread {

                        val adapter = AdapterClamp(this@ClampingModule, response.data.clamped)
                        adapter.notifyDataSetChanged()
                        recyclerView.layoutManager = LinearLayoutManager(this@ClampingModule)
                        recyclerView.adapter = adapter
                        recyclerView.setHasFixedSize(false)
                    }
                }else{
                    runOnUiThread { Toast.makeText(this@ClampingModule,response.message, Toast.LENGTH_LONG).show() }
                }
            }

            override fun onFailure(result: String?) {

                runOnUiThread {
                    progress_circular.visibility = View.VISIBLE
                    Toast.makeText(this@ClampingModule,result, Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}