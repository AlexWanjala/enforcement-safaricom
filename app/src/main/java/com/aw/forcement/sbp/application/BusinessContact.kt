package com.aw.forcement.sbp.application

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
import com.aw.passanger.api.executeRequest
import com.aw.passanger.api.save
import com.aw.passanger.api.trade
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_business_contact.*
import kotlinx.android.synthetic.main.activity_business_contact.btn_next
import kotlinx.android.synthetic.main.activity_business_contact.btn_previous
import kotlinx.android.synthetic.main.activity_business_details.*


class BusinessContact : AppCompatActivity() {

    private val arrayList = ArrayList<String>()
    var business_role = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_contact)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        btn_next.setOnClickListener { startActivity(Intent(this, BusinessActivityInformation::class.java))

            saveValues()
        }

        btn_previous.setOnClickListener { finish() }

        getRoles()
    }

    private fun saveValues(){
        save(this,"business_email",ed_business_email.text.toString())
        save(this,"postal_address",ed_postal_address.text.toString())
        save(this,"postal_code",ed_postal_code.text.toString())
        save(this,"business_phone",ed_business_phone.text.toString())
        save(this,"contact_person_names",ed_contact_person_names.text.toString())
        save(this,"contact_person_idNo",ed_contact_person_idNo.text.toString())
        save(this,"business_role",business_role)
        save(this,"contact_person_phone",ed_contact_person_phone.text.toString())
        save(this,"contact_person_email",ed_contact_person_email.text.toString())

    }

    private fun getRoles (){
        val formData = listOf(
            "function" to "getRoles",
        )
        executeRequest(formData, trade,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    runOnUiThread {
                        arrayList.clear()
                        for(data in response.data.roles){
                            arrayList.add(data.role)
                        }

                        //Spinner
                        val adapters = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item,arrayList)
                        adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        spinner_role.adapter = adapters
                        spinner_role.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {
                                business_role =response.data.roles[postion].role

                            }
                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }
                        }

                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@BusinessContact,response.message, Toast.LENGTH_LONG).show()}

                }

            }

        })
    }
}