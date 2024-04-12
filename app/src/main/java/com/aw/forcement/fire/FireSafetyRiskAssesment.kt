package com.aw.forcement.fire
import AdapterFireSafety
import AdapterOther
import Const
import Json4Kotlin_Base
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.aw.forcement.R
import com.aw.passanger.api.*
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

        val businessID = intent.getStringExtra("businessID")

        tv_business_ID.text ="Business ID: ${businessID}"

        //Layout service other measures
        val layoutService = findViewById<LinearLayout>(R.id.layout_service)
        layoutServiceList.add(layoutService)//add the initial layout
        val btnAddService = findViewById<TextView>(R.id.btn_add_service)
        btnAddService.setOnClickListener {
            addNewServiceLayout(layoutService)
        }

        //Layout service Recommendation
        val layoutServiceRecommendation = findViewById<LinearLayout>(R.id.layout_service_recommendation)
        layoutServiceListRecommendation.add(layoutServiceRecommendation)//add the initial layout
        val btnAddServiceRecommendation = findViewById<TextView>(R.id.btn_add_service_recommendation)
        btnAddServiceRecommendation.setOnClickListener {
            addNewServiceLayoutRecommendation(layoutServiceRecommendation)
        }

        btn_next.setOnClickListener {
            //Get the additional measures and add them to the selected measures
            retrieveTextFromEdOtherMeasure()

            //Get the additional recommendations and add them to the selected measures
            retrieveTextFromEdRecommendation()
            startActivity(Intent(this,FireCertificateBilling::class.java).putExtra("businessID",businessID))
        }
        btn_previous.setOnClickListener { finish() }

        getFireMeasures()
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
            //add to measures
            Const.instance.addFireSafety(text)
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
            //add recommendations to safety
            Const.instance.addFireSafety("Recommendations: $text")
        }
        return texts
    }

    //Other measures

    private lateinit var adapter: AdapterFireSafety
    private fun getFireMeasures (){
        val formData = listOf(
            "function" to "getFireMeasures",
            "deviceId" to getDeviceIdNumber(this)
        )
        executeRequest(formData, fire,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                runOnUiThread {
                    if(response.success){
                        recyclerView.adapter = null
                        adapter = AdapterFireSafety(this@FireSafetyRiskAssesmentActivity, response.data.fireSafety)
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

