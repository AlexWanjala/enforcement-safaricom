package com.aw.forcement.fire

import FireSpAdapter
import Json4Kotlin_Base
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.aw.forcement.R
import com.aw.passanger.api.CallBack
import com.aw.passanger.api.executeRequest
import com.aw.passanger.api.trade
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_search_for_business.*
import kotlinx.android.synthetic.main.recycler_view.*

class SearchForBusiness : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_for_business)

        ed_search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                getBusinesses(p0.toString())
            }

        })
    }

    private fun getBusinesses(keyword: String){

        val formData = listOf(
            "function" to "getBusinesses",
            "search" to keyword,
        )
        executeRequest(formData, trade,object : CallBack {
            override fun onSuccess(result: String?) {

                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)

                if(response.success){

                            runOnUiThread {
                                val adapter = FireSpAdapter(this@SearchForBusiness, response.data.businesses)
                                adapter.notifyDataSetChanged()
                                recyclerView.layoutManager = LinearLayoutManager(this@SearchForBusiness)
                                recyclerView.adapter = adapter
                                recyclerView.setHasFixedSize(false)
                            }

                    // runOnUiThread { bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED }
                }
                else{
                    runOnUiThread {
                        recyclerView.adapter = null
                        Toast.makeText(this@SearchForBusiness,response.message,Toast.LENGTH_LONG).show()
                    }
                }
            }
            override fun onFailure(result: String?) {
                runOnUiThread {

                    Toast.makeText(this@SearchForBusiness,result, Toast.LENGTH_LONG).show()
                }
            }

        })
    }
}