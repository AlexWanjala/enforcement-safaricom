package com.aw.forcement.fire
import AdapterOther
import Json4Kotlin_Base
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.aw.forcement.R
import com.aw.passanger.api.CallBack
import com.aw.passanger.api.biller
import com.aw.passanger.api.executeRequest
import com.aw.passanger.api.getDeviceIdNumber
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_fire_safety_risk_assesment.*
import kotlinx.android.synthetic.main.recycler_view.*

class FireSafetyRiskAssesmentActivity : AppCompatActivity() {


    //Other Measures
    private val layoutServiceList = mutableListOf<LinearLayout>()
    private var serviceCounter = 2


    //Additional Recommendation
    private val layoutServiceListRecommendation = mutableListOf<LinearLayout>()
    private var serviceCounterRecommendation = 2


    private val arrayListOther = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fire_safety_risk_assesment)
        tv_business_ID.text ="Business ID: "+ intent.getStringExtra("businessID")

        //Layout service other measures
        val layoutService = findViewById<LinearLayout>(R.id.layout_service)
        val btnAddService = findViewById<TextView>(R.id.btn_add_service)
        btnAddService.setOnClickListener {
            addNewServiceLayout(layoutService)
        }

        //Layout service Recommendation
        val layoutServiceRecommendation = findViewById<LinearLayout>(R.id.layout_service_recommendation)
        val btnAddServiceRecommendation = findViewById<TextView>(R.id.btn_add_service_recommendation)
        btnAddServiceRecommendation.setOnClickListener {
            addNewServiceLayoutRecommendation(layoutServiceRecommendation)
        }

        getIncomeTypesOther()
    }

    ////Other Measures service
    private fun addNewServiceLayout(parentLayout: LinearLayout) {
        // Increment the counter when adding a new service layout
        val newServiceLayout = LayoutInflater.from(this).inflate(R.layout.layout_other_measures, null)

        val edOtherMeasure = newServiceLayout.findViewById<EditText>(R.id.ed_other_measure)

        val btnRemoveService = newServiceLayout.findViewById<TextView>(R.id.btn_remove_service)
        btnRemoveService.visibility = View.VISIBLE
        val tvItemNumber = newServiceLayout.findViewById<TextView>(R.id.tv_item_number)

        btnRemoveService.setOnClickListener {
            // Decrement the counter when removing a service layout
            removeLastServiceLayout(newServiceLayout as LinearLayout)
        }

        // Set the position or number on the tv_item_number TextView
        tvItemNumber.text = "Bill item no $serviceCounter."
        serviceCounter++

        // Set up any additional configurations or listeners for the new layout elements if needed

        // Add the new layout to the parent layout and store a reference in the list
        parentLayout.addView(newServiceLayout)
        layoutServiceList.add(newServiceLayout as LinearLayout)
    }
    private fun removeLastServiceLayout(serviceLayout: LinearLayout) {
        if (layoutServiceList.contains(serviceLayout)) {
            // Decrement the counter when removing a service layout
            serviceCounter--

            // Remove the last added layout from the parent layout and the list
            layoutServiceList.remove(serviceLayout)
            val parentLayout = serviceLayout.parent as LinearLayout
            parentLayout.removeView(serviceLayout)
        }
    }
    private fun retrieveTextFromEdOtherMeasure(): List<String> {
        val texts = mutableListOf<String>()
        for (layout in layoutServiceList) {
            val edOtherMeasure = layout.findViewById<EditText>(R.id.ed_other_measure)
            val text = edOtherMeasure.text.toString()
            texts.add(text)
        }
        return texts
    }


    ////Additional Recommendation service
    private fun addNewServiceLayoutRecommendation(parentLayout: LinearLayout) {
        // Increment the counter when adding a new service layout
        val newServiceLayout = LayoutInflater.from(this).inflate(R.layout.layout_recomenations, null)

        val edAdditionalRecommendation = newServiceLayout.findViewById<EditText>(R.id.ed_additional_recommendation)

        val btnRemoveServiceRecommendation = newServiceLayout.findViewById<TextView>(R.id.btn_remove_service_recommendation)
        btnRemoveServiceRecommendation.visibility = View.VISIBLE
        val tvItemNumber = newServiceLayout.findViewById<TextView>(R.id.tv_item_number_recommendation)

        btnRemoveServiceRecommendation.setOnClickListener {
            // Decrement the counter when removing a service layout
            removeLastServiceLayoutRecommendation(newServiceLayout as LinearLayout)
        }

        // Set the position or number on the tv_item_number TextView
        tvItemNumber.text = "Bill item no $serviceCounterRecommendation."
        serviceCounterRecommendation++

        // Set up any additional configurations or listeners for the new layout elements if needed

        // Add the new layout to the parent layout and store a reference in the list
        parentLayout.addView(newServiceLayout)
        layoutServiceListRecommendation.add(newServiceLayout as LinearLayout)
    }
    private fun removeLastServiceLayoutRecommendation(serviceLayout: LinearLayout) {
        if (layoutServiceListRecommendation.contains(serviceLayout)) {
            // Decrement the counter when removing a service layout
            serviceCounterRecommendation--

            // Remove the last added layout from the parent layout and the list
            layoutServiceListRecommendation.remove(serviceLayout)
            val parentLayout = serviceLayout.parent as LinearLayout
            parentLayout.removeView(serviceLayout)
        }
    }
    private fun retrieveTextFromEdRecommendation(): List<String> {
        val texts = mutableListOf<String>()
        for (layout in layoutServiceListRecommendation) {
            val edAdditionalRecommendation = layout.findViewById<EditText>(R.id.ed_additional_recommendation)
            val text = edAdditionalRecommendation.text.toString()
            texts.add(text)
        }
        return texts
    }

    //Other measures
    private fun getIncomeTypesOther(){

        val formData = listOf(
            "function" to "getIncomeTypes",
            "incomeTypePrefix" to "SBP",
            "keyword" to "Other",
            "deviceId" to getDeviceIdNumber(this)
        )
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    runOnUiThread {

                        for(data in response.data.incomeTypes){
                            arrayListOther.add(data.incomeTypeDescription)
                        }

                        //Spinner
                        val adapters = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item,arrayListOther)
                        adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        spinner_category_other.adapter = adapters
                        spinner_category_other.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {

                                //business_category = response.data.incomeTypes[postion].incomeTypeDescription
                                spinnerFeeAndChargesOther(response.data.incomeTypes[postion].incomeTypeId)

                            }
                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }
                        }
                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@FireSafetyRiskAssesmentActivity,response.message, Toast.LENGTH_LONG).show()}

                }

            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@FireSafetyRiskAssesmentActivity,result, Toast.LENGTH_LONG).show()
                }
            }

        })
    }
    private lateinit var adapter: AdapterOther
    private fun spinnerFeeAndChargesOther (incomeTypeId: String){
        val formData = listOf(
            "function" to "getFeesAndCharges",
            "incomeTypeId" to incomeTypeId,
            "deviceId" to getDeviceIdNumber(this)
        )
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                runOnUiThread {
                    if(response.success){
                        recyclerView.adapter = null
                        adapter = AdapterOther(this@FireSafetyRiskAssesmentActivity, response.data.feesAndCharges)
                        recyclerView.layoutManager = LinearLayoutManager(this@FireSafetyRiskAssesmentActivity)
                        recyclerView.adapter = adapter
                        recyclerView.setHasFixedSize(false)

                    } else{
                        Toast.makeText(this@FireSafetyRiskAssesmentActivity,response.message, Toast.LENGTH_LONG).show()
                    }
                }

            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@FireSafetyRiskAssesmentActivity,result, Toast.LENGTH_LONG).show()
                }
            }

        })
    }

}

