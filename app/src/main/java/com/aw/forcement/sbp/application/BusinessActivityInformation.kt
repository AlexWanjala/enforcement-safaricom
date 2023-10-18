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
import com.aw.passanger.api.*
import com.google.gson.Gson


import kotlinx.android.synthetic.main.activity_business_information.*
import kotlinx.android.synthetic.main.activity_business_information.btn_next
import kotlinx.android.synthetic.main.activity_business_information.checkbox
import kotlinx.android.synthetic.main.activity_business_information.ed_number_of_employees
import kotlinx.android.synthetic.main.activity_business_information.spinner_category
import kotlinx.android.synthetic.main.activity_business_information.spinner_sub_category
import kotlinx.android.synthetic.main.activity_business_information.spinner_tonnage


class BusinessActivityInformation : AppCompatActivity() {

    private val arrayList = ArrayList<String>()
    private val arrayList2 = ArrayList<String>()
        private val arrayList3 = ArrayList<String>()
    lateinit var amount: String
    lateinit var feeId: String
     var tonnage: String =""
    var liquor: String =""
    var conservancy: String =""
    lateinit var business_category: String
    lateinit var business_sub_category: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_information)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                save(this,"lat",getValue(this, "latitude").toString())
                save(this,"lng",getValue(this, "longitude").toString())


            } else {
                save(this,"lat","")
                save(this,"lng","")

            }
        }

        btn_next.setOnClickListener {
            if(checkbox.isChecked){

                saveIfNotEmpty()
            }else{
                Toast.makeText(this,"You Must be at the Business Location",Toast.LENGTH_LONG).show()
            }

        }
        btn_previous.setOnClickListener { finish() }
        getIncomeTypes()
        //ed_premise_size
         getTonnage()

        liquorYes.setOnClickListener { liquor ="YES" }
        liquorNo.setOnClickListener { liquor ="NO" }
        conservancyYes.setOnClickListener { conservancy ="YES" }
        conservancyNo.setOnClickListener {conservancy ="NO"  }

    }


    fun saveIfNotEmpty () {
      // Get the text from the edit texts
        val premiseSize = ed_premise_size.text.toString ()
        val numberOfEmployees = ed_number_of_employees.text.toString ()
        val tonnage = tonnage
        val businessDes = ed_business_des.text.toString ()
        val businessCategory = business_category
        val businessSubCategory = business_sub_category
        val amount = amount
        val feeID = feeId
        val liquor = liquor
        val conservancy = conservancy

       // Check if any of them is empty
        if (premiseSize.isEmpty () || numberOfEmployees.isEmpty () || tonnage.isEmpty () || businessDes.isEmpty () || businessCategory.isEmpty () || businessSubCategory.isEmpty () || amount.isEmpty () || feeID.isEmpty () || liquor.isEmpty () || conservancy.isEmpty ()) {
       // Show a toast message or an error message
            Toast.makeText (this, "Please fill all the fields", Toast.LENGTH_SHORT).show ()
        } else {
       // Save the data using your save function
            save (this, "premise_size", premiseSize)
            save (this, "number_of_employees", numberOfEmployees)
            save (this, "tonnage", tonnage)
            save (this, "business_des", businessDes)
            save (this, "business_category", businessCategory)
            save (this, "business_sub_category", businessSubCategory)
            save (this, "amount", amount)
            save (this, "feeID", feeID)
            save (this, "liquor", liquor)
            save (this, "conservancy", conservancy)
            startActivity(Intent(this, BillingInformation::class.java))
        }
    }




    private fun getIncomeTypes (){

        val formData = listOf(
            "function" to "getIncomeTypes",
            "incomeTypePrefix" to "SBP"
        )
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    runOnUiThread {

                        for(data in response.data.incomeTypes){
                            arrayList.add(data.incomeTypeDescription)
                        }

                        //Spinner
                        val adapters = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item,arrayList)
                        adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        spinner_category.adapter = adapters
                        spinner_category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {
                                business_category = response.data.incomeTypes[postion].incomeTypeDescription
                               spinnerFeeAndCharges(response.data.incomeTypes[postion].incomeTypeId)
                            }
                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }
                        }
                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@BusinessActivityInformation,response.message, Toast.LENGTH_LONG).show()}

                }

            }

        })
    }
    private fun getTonnage (){

        val formData = listOf(
            "function" to "getTonnage",
        )
        executeRequest(formData, trade,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    runOnUiThread {

                        for(data in response.data.tonnage){
                            arrayList3.add(data.tonnage)
                        }

                        //Spinner
                        val adapters = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item,arrayList3)
                        adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        spinner_tonnage.adapter = adapters
                        spinner_tonnage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {
                                if (p0 != null) {
                                    tonnage = p0.getItemAtPosition(postion).toString()
                                }

                            }
                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }
                        }
                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@BusinessActivityInformation,response.message, Toast.LENGTH_LONG).show()}

                }

            }

        })
    }
    private fun spinnerFeeAndCharges (incomeTypeId: String){
        save(this,"incomeTypeID",incomeTypeId)
        val formData = listOf(
            "function" to "getFeesAndCharges",
            "incomeTypeId" to incomeTypeId,
        )
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                runOnUiThread {  arrayList2.clear()
                    val adapters = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item,arrayList2)
                    adapters.clear()
                    if(response.success){
                        runOnUiThread {
                            for(data in response.data.feesAndCharges){
                                arrayList2.add(data.feeDescription)
                            }

                            //Spinner
                            adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                            spinner_sub_category.adapter = adapters
                            spinner_sub_category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {
                                    //response.data.feesAndCharges[postion].feeId
                                    business_sub_category =  response.data.feesAndCharges[postion].feeDescription
                                    amount = response.data.feesAndCharges[postion].unitFeeAmount
                                    feeId = response.data.feesAndCharges[postion].feeId


                                }
                                override fun onNothingSelected(p0: AdapterView<*>?) {

                                }
                            }
                        }
                    }
                    else{
                        spinner_sub_category.adapter = null
                        Toast.makeText(this@BusinessActivityInformation,response.message, Toast.LENGTH_LONG).show() }
                }

            }

        })
    }
}