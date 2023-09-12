package com.aw.forcement.sbp.applications

import Const
import Json4Kotlin_Base
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.aw.forcement.R
import com.aw.passanger.api.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_application_verification_business_information.*
import kotlinx.android.synthetic.main.update.view.*
import kotlinx.android.synthetic.main.update.view.okay
import kotlinx.android.synthetic.main.update.view.tv_title
import kotlinx.android.synthetic.main.update_demographics.view.*

class ApplicationVerificationBusinessActivityInformation : AppCompatActivity() {

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
        setContentView(R.layout.activity_application_verification_business_information)

           btn_next.setOnClickListener { startActivity(Intent(this, ApplicationVerificationBillingInformation::class.java))}

                val business = Const.instance.getBusiness()

                checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {

                     // updating some data in the const
                        val currentBusiness = Const.instance.getBusiness()
                        val newBusiness = currentBusiness
                            .copy(lat = getValue(this,"latitude").toString())
                            .copy(lng =  getValue(this,"longitude").toString())
                            .copy(physicalAddress = getValue(this,"address").toString())
                             Const.instance.setBusiness(newBusiness)



                    } else {
                      // The checkbox is unchecked
                        val currentBusiness = Const.instance.getBusiness()
                        val newBusiness = currentBusiness
                            .copy(lat = business.lat)
                            .copy(lng =  business.lng)
                            .copy(physicalAddress = business.physicalAddress)
                        Const.instance.setBusiness(newBusiness)

                    }
                }

        getIncomeTypes()
        getTonnage()

    }

    private fun displayValues(){
        tv_premise.text = Const.instance.getBusiness().premiseSize
        tv_business_des.text = Const.instance.getBusiness().businessDes
        ed_number_of_employees.setText(Const.instance.getBusiness().numberOfEmployees)

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

                        arrayList.clear()

                        var position = 0
                        var x = 0

                        for (data in response.data.incomeTypes) {
                            arrayList.add(data.incomeTypeDescription)
                            if (data.incomeTypeId == Const.instance.getBusiness().incomeTypeId) {
                                position = x
                            }
                            x++
                        }

                        //Spinner
                        val adapters = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item, arrayList)
                        adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        spinner_category.adapter = adapters

                        // Set the initial selection of the spinner based on the business object's sub-county ID
                        spinner_category.setSelection(position)

                        // Declare a variable to store the sub-county ID of the selected option
                        var incomeTypeId_ = response.data.incomeTypes[position].incomeTypeId

                        spinner_category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {
                                // Update the sub-county ID variable with the new selection
                                incomeTypeId_ = response.data.incomeTypes[postion].incomeTypeId
                               // incomeTypeId = response.data.incomeTypes[postion].incomeTypeId
                                business_category = response.data.incomeTypes[postion].incomeTypeDescription

                                val currentBusiness = Const.instance.getBusiness()
                                val newBusiness = currentBusiness.copy(incomeTypeId = incomeTypeId_).copy(businessCategory = business_category)
                                Const.instance.setBusiness(newBusiness)

                                // Get the wards based on the sub-county ID
                                spinnerFeeAndCharges(response.data.incomeTypes[postion].incomeTypeId)

                            }

                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }
                        }
                    }

                }else{

                    runOnUiThread {  Toast.makeText(this@ApplicationVerificationBusinessActivityInformation,response.message, Toast.LENGTH_LONG).show()}

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

                            arrayList2.clear()

                            var position = 0
                            var x = 0

                            for (data in response.data.feesAndCharges) {
                                arrayList2.add(data.feeDescription)
                                if (data.feeId == Const.instance.getBusiness().feeId) {
                                    position = x
                                }
                                x++
                            }

                            //Spinner
                            val adapters = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item, arrayList2)
                            adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                             spinner_sub_category.adapter = adapters

                            // Set the initial selection of the spinner based on the business object's sub-county ID
                            spinner_sub_category.setSelection(position)

                            // Declare a variable to store the sub-county ID of the selected option
                            var feeId_ = response.data.feesAndCharges[position].feeId

                            spinner_sub_category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {
                                    // Update the sub-county ID variable with the new selection
                                    feeId_ = response.data.feesAndCharges[postion].feeId
                                    feeId = response.data.feesAndCharges[postion].feeId
                                    business_sub_category = response.data.feesAndCharges[postion].feeDescription

                                    val currentBusiness = Const.instance.getBusiness()
                                    val newBusiness = currentBusiness.copy(feeId = feeId).copy(businessSubCategory = business_sub_category)
                                    Const.instance.setBusiness(newBusiness)

                                    //Update the BillTotal
                                    val currentEntries = Const.instance.getEntries()
                                    val newEntries = currentEntries.copy(billTotal = response.data.feesAndCharges[postion].unitFeeAmount )
                                    Const.instance.setEntries(newEntries)

                                }

                                override fun onNothingSelected(p0: AdapterView<*>?) {

                                }
                            }
                        }

                    }
                    else{
                        spinner_sub_category.adapter = null
                        Toast.makeText(this@ApplicationVerificationBusinessActivityInformation,response.message, Toast.LENGTH_LONG).show() }
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


                    runOnUiThread { var position = 0
                        var x = 0

                        for (data in response.data.tonnage) {
                            arrayList3.add(data.tonnage)
                            if (data.tonnage == Const.instance.getBusiness().tonnage) {
                                position = x
                            }
                            x++
                        }

                        //Spinner
                        val adapters = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item, arrayList3)
                        adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        spinner_tonnage.adapter = adapters

                        // Set the initial selection of the spinner based on the business object's sub-county ID
                        spinner_tonnage.setSelection(position)

                        // Declare a variable to store the sub-county ID of the selected option
                        var tonnage_ = response.data.tonnage[position].tonnage

                        spinner_tonnage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {

                                tonnage_ = response.data.tonnage[postion].tonnage

                                val currentBusiness = Const.instance.getBusiness()
                                val newBusiness = currentBusiness.copy(tonnage = tonnage_)
                                Const.instance.setBusiness(newBusiness)

                            }

                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }
                        } }


                }else{
                    runOnUiThread {  Toast.makeText(this@ApplicationVerificationBusinessActivityInformation,response.message, Toast.LENGTH_LONG).show()}

                }

            }

        })
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun showMessageBox(title: String,message: String){

        val messageBoxView = LayoutInflater.from(this).inflate(R.layout.update, null)
        val messageBoxBuilder = AlertDialog.Builder(this).setView(messageBoxView)
        val messageBoxInstance = messageBoxBuilder.show()

        messageBoxView.tv_title.text = "Change Business "+ title
        messageBoxView.ed_message.setText(message)

        //set Listener
        messageBoxView.setOnClickListener(){

            messageBoxInstance.dismiss()
        }

        messageBoxView.okay.setOnClickListener {

            if (title == "Business Premise") {
                val currentBusiness = Const.instance.getBusiness()
                val newBusiness = currentBusiness.copy(premiseSize = messageBoxView.ed_message.text.toString())
                Const.instance.setBusiness(newBusiness)
            }
            if(title=="Business Description"){
                val currentBusiness = Const.instance.getBusiness()
                val newBusiness = currentBusiness.copy(businessDes = messageBoxView.ed_message.text.toString())
                Const.instance.setBusiness(newBusiness)
            }


            displayValues()
            messageBoxInstance.dismiss()
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun update(view: View) {
        when (view.id) {
            R.id.btn_business_premise -> {
                showMessageBox("Business Premise", Const.instance.getBusiness().premiseSize)
            }

            R.id.btn_business_des -> {
                showMessageBox("Business Description",Const.instance.getBusiness().businessDes)
            }


            else -> {

            }
        }
    }
    override fun onResume() {
        displayValues()
        super.onResume()
    }
}