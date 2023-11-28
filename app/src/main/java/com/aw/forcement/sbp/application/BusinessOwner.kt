package com.aw.forcement.sbp.application

import Business
import Const
import Json4Kotlin_Base
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.aw.forcement.R
import com.aw.passanger.api.CallBack
import com.aw.passanger.api.biller
import com.aw.passanger.api.executeRequest
import com.aw.passanger.api.save
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_business_information.*
import kotlinx.android.synthetic.main.activity_business_owner.*
import kotlinx.android.synthetic.main.activity_business_owner.btn_next


class BusinessOwner : AppCompatActivity() {

    private val arrayList = ArrayList<String>()
    var fiscalYear =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_owner)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        btn_next.setOnClickListener {
            saveIfNotEmpty()
        }
        getYear()
    }

    private fun getYear (){
        val formData = listOf(
            "function" to "getYears",

        )
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    runOnUiThread {

                        for(data in response.data.years){
                            arrayList.add(data.year)
                        }

                        //Spinner
                        val adapters = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item,arrayList)
                        adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        spinner_year.adapter = adapters
                        spinner_year.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {
                                fiscalYear = response.data.years[postion].year

                            }
                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }
                        }
                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@BusinessOwner,response.message, Toast.LENGTH_LONG).show()}

                }

            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@BusinessOwner,result, Toast.LENGTH_LONG).show()
                }
            }

        })
    }

    private fun saveIfNotEmpty() {
       // Get the text from the edit texts
        val fullNames = ed_full_names.text.toString()
        val ownerId = ed_owner_id.text.toString()
        val ownerPhone = ed_phone.text.toString()
        val ownerEmail = ed_email.text.toString()
        val kraPin = ed_kra_pin.text.toString()

        //Check if any of them is empty
        if (fullNames.isEmpty() || ownerId.isEmpty() || ownerPhone.isEmpty() || fiscalYear.isEmpty()) {
       //Show a toast message or an error message
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()

        } else {
         //Save the data using your save function
        save(this, "full_names", fullNames)
        save(this, "owner_id", ownerId)
        save(this, "owner_phone", ownerPhone)
        save(this, "owner_email", ownerEmail)
        save(this, "kra_pin", kraPin)
        save(this, "fiscalYear", fiscalYear)
        startActivity(Intent(this, BusinessDetails::class.java))
        }
    }

}