package com.aw.forcement.sbp.application

import Json4Kotlin_Base
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.aw.forcement.R
import com.aw.passanger.api.*
import com.google.gson.Gson

import kotlinx.android.synthetic.main.activity_business_information.*


class BusinessActivityInformation : AppCompatActivity() {

    private val arrayList = ArrayList<String>()
    private val arrayList2 = ArrayList<String>()
    private val arrayList3 = ArrayList<String>()
    lateinit var amount: String
    lateinit var feeId: String
     var tonnage: String =""
    lateinit var business_category: String
    lateinit var business_sub_category: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_information)

        btn_next.setOnClickListener {
            startActivity(Intent(this, BillingInformation::class.java))
            saveValues()
        }
        getIncomeTypes()
        //ed_premise_size
         getTonnage()
    }

    private fun saveValues(){
        save(this,"premise_size",ed_premise_size.text.toString())
        save(this,"number_of_employees",ed_number_of_employees.text.toString())
        save(this,"tonnage",tonnage)
        save(this,"business_des",ed_business_des.text.toString())
        save(this,"business_category",business_category)
        save(this,"business_sub_category",business_category)
        save(this,"amount",amount)
        save(this,"feeId",feeId)

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