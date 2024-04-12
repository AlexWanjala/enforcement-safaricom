package com.aw.forcement.fire.applications

import FireAdapter
import Json4Kotlin_Base
import SbpAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.aw.forcement.R
import com.aw.passanger.api.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_applications.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.recycler_view.*


class ApplicationsFire : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_applications)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        tv_message_header.text = intent.getStringExtra("header")
        edSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Toast.makeText(this@ApplicationsFire,p0.toString(),Toast.LENGTH_LONG).show()

                if(p0?.length!! >3){
                    getApplications(p0.toString())
                }


            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        getApplications("")

    }

    private fun getApplications (search: String){
        runOnUiThread {  progress_circular.visibility = View.VISIBLE }
        val formData = listOf(
            "function" to "getApplications",
            "keyword" to   intent.getStringExtra("keyword").toString(),
            "search" to   search,
           // "subCountyName" to getValue(this,"subCountyName").toString(),
            "deviceId" to getDeviceIdNumber(this)

        )
        executeRequest(formData, fire,object : CallBack {
            override fun onSuccess(result: String?) {
                runOnUiThread {  progress_circular.visibility = View.GONE }
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){
                    runOnUiThread {
                        val adapter = FireAdapter(this@ApplicationsFire, response.data.businesses)
                        adapter.notifyDataSetChanged()
                        recyclerView.layoutManager = LinearLayoutManager(this@ApplicationsFire)
                        recyclerView.adapter = adapter
                        recyclerView.setHasFixedSize(false)
                    }
                }else{
                    runOnUiThread {Toast.makeText(this@ApplicationsFire,response.message, Toast.LENGTH_LONG).show() }
                }
            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@ApplicationsFire,result, Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}