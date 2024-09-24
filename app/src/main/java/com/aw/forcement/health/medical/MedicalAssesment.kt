package com.aw.forcement.health.medical
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
import kotlinx.android.synthetic.main.activity_fire_certificate_billing.*
import kotlinx.android.synthetic.main.activity_hygiene_certificate_billing.*
import kotlinx.android.synthetic.main.activity_medical_assesment.*
import kotlinx.android.synthetic.main.activity_medical_assesment.btnNext
import kotlinx.android.synthetic.main.activity_medical_assesment.btnNextDisabled
import kotlinx.android.synthetic.main.activity_medical_assesment.checkbox
import kotlinx.android.synthetic.main.activity_medical_assesment.ed_observations

import kotlinx.android.synthetic.main.recycler_view.*

class MedicalAssesment : AppCompatActivity() {


    //Other Measures
    private val layoutServiceList = mutableListOf<LinearLayout>()
    private var serviceCounter = 2


    private val arrayListOther = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medical_assesment)



        //Layout service other measures
        val layoutService = findViewById<LinearLayout>(R.id.layout_service_tests)
        layoutServiceList.add(layoutService)//add the initial layout
        val btnAddService = findViewById<TextView>(R.id.btn_add_service)
        btnAddService.setOnClickListener {
            addNewServiceLayout(layoutService)
        }


        checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // CheckBox is checked
                btnNextDisabled.visibility = View.GONE
                btnNext.visibility = View.VISIBLE

            } else {
                // CheckBox is not checked
                btnNextDisabled.visibility = View.VISIBLE
                btnNext.visibility = View.GONE

            }
        }


        btnNext.setOnClickListener {
            //Get the additional measures and add them to the selected measures
            retrieveTextFromEdOtherMeasure()
            Const.instance.addTests(ed_observations.text.toString())
            startActivity(Intent(this,MedicalCertificateBilling::class.java))
        }
        btn_previous.setOnClickListener { finish() }

    }

    ////Other Measures service
    private fun addNewServiceLayout(parentLayout: LinearLayout) {
        // Increment the counter when adding a new service layout
        val newServiceLayout = LayoutInflater.from(this).inflate(R.layout.layout_service_tests, null)

        val edTests = newServiceLayout.findViewById<EditText>(R.id.ed_tests_done)

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
            val edTests = layout.findViewById<EditText>(R.id.ed_tests_done)
            val text = edTests.text.toString()
            texts.add(text)
            //add to measures
            Const.instance.addTests(text)
        }
        return texts
    }

}

