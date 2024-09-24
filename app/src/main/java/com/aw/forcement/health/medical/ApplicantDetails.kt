package com.aw.forcement.health.medical

import Const
import Individual
import Json4Kotlin_Base
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.aw.forcement.R
import com.aw.passanger.api.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_applicant_details.*
import kotlinx.android.synthetic.main.activity_stock_market_fees_summary.*
import kotlinx.android.synthetic.main.message_box.view.*

class ApplicantDetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_applicant_details)

        ed_id_no.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0 != null) {
                    if(p0.length>6){
                        getIndividual(ed_id_no.text.toString())
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        btn_next.setOnClickListener {
            val names = ed_names.text.toString().trim()
            val idNo = ed_id_no.text.toString().trim()
            val phone = ed_phone.text.toString().trim()
            val email = ed_email.text.toString().trim()
            val jobTile = ed_title.text.toString().trim()

// Check if any of the fields is null or empty
            if (names.isNotEmpty() && idNo.isNotEmpty() && phone.isNotEmpty() && email.isNotEmpty()) {
                val individual = Individual(
                    names,
                    idNo,
                    phone,
                    email,
                    getValue(this,"subCountyID").toString(),
                    getValue(this,"subCountyName").toString(),
                    getValue(this,"wardID").toString(),
                    getValue(this,"wardName").toString(),
                    jobTile

                )
                Const.instance.setIndividual(individual)
                startActivity(Intent(this,MedicalAssesment::class.java))
            } else {
                // Handle case where any field is null or empty, show error message or prompt user to fill in all fields
                // For example:
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }




        }


    }

    private fun getIndividual(idNo: String) {
        tvMessage.text = "Searching..."
        tvMessage.setTextColor(Color.GREEN)
        val formData = listOf(
            "function" to "getIndividual",
            "idNo" to idNo,
        )
        executeRequest(formData, health, object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if (response.success) {
                    val individual = response.data.individual
                    val names = individual.names
                    val idNo = individual.idNo
                    val phone = individual.phoneNumber
                    val email = individual.email
                    val jobTile = individual.jobTitle

                    runOnUiThread {
                        ed_names.setText(names)
                        ed_id_no.setText(idNo)
                        ed_phone.setText(phone)
                        ed_email.setText(email)
                        if (jobTile != null) {
                            ed_title.setText(jobTile)
                        } else {
                            ed_title.setText("") // Or set it to some default value
                        }
                        tvMessage.text = ""
                    }

                } else {
                    runOnUiThread {
                        tvMessage.text = response.message
                        tvMessage.setTextColor(Color.RED)
                    }

                }

            }

            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@ApplicantDetails, result, Toast.LENGTH_LONG).show()
                }
            }

        })
    }

}