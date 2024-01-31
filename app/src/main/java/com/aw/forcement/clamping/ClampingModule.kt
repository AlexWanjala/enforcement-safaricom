package com.aw.forcement.clamping

import AdapterClamp
import Json4Kotlin_Base
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
import kotlinx.android.synthetic.main.activity_clamping_module.*
import kotlinx.android.synthetic.main.item_other.view.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.recycler_view.*

class ClampingModule : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clamping_module)

        radio_tobe_clamped.isChecked = true
        save(this,"status","0")
        radio_tobe_clamped.setOnClickListener {
            save(this,"status","0")
            getClamped("")
        }

        radio_clamped.setOnClickListener {
            save(this,"status","1")
            getClamped("")
        }

        radio_tobe_unclamped.setOnClickListener {
            save(this,"status","2")
            getClamped("")
        }



        edSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                getClamped(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })


        getClamped("")
    }

    private fun getClamped(keyword: String) {
        runOnUiThread { progress_circular.visibility = View.VISIBLE }

        val formData = listOf(
            "function" to "getClamped",
            "deviceId" to getDeviceIdNumber(this),
            "keyword" to keyword,
            "subCountyID" to getValue(this, "subCountyID").toString(),
            "status" to getValue(this, "status").toString()
        )

        executeRequest(formData, parking, object : CallBack {
            override fun onSuccess(result: String?) {
                runOnUiThread { progress_circular.visibility = View.GONE }
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)

              runOnUiThread {
                  recyclerView.adapter = null
                  if (response.success) {

                          val adapter = AdapterClamp(this@ClampingModule, response.data.clamped)
                          recyclerView.layoutManager = LinearLayoutManager(this@ClampingModule)
                          recyclerView.adapter = adapter
                          recyclerView.setHasFixedSize(false)
                  } else {

                          Toast.makeText(this@ClampingModule, response.message, Toast.LENGTH_LONG).show()
                  }
              }


            }

            override fun onFailure(result: String?) {
                runOnUiThread {
                    progress_circular.visibility = View.VISIBLE
                    Toast.makeText(this@ClampingModule, result, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

}