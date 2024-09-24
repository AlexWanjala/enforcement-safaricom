package com.aw.forcement.billing

import Const
import FeesAndCharges
import Json4Kotlin_Base
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.aw.forcement.R
import com.aw.forcement.SelectZone
import com.aw.passanger.api.*
import com.google.gson.Gson
import com.toptoche.searchablespinnerlibrary.SearchableSpinner
import kotlinx.android.synthetic.main.activity_park.*
import kotlinx.android.synthetic.main.layout_service.edQuantity
import kotlinx.android.synthetic.main.layout_service.spinnerFeeAndCharges
import kotlinx.android.synthetic.main.layout_service.spinnerIncomeType


class Billing : AppCompatActivity() {
    // Declare response variable at the class level

    private lateinit var response: Json4Kotlin_Base
    private val arrayList = ArrayList<String>()
    private val arrayList2 = ArrayList<String>()
    lateinit var feeDescription: String
    lateinit var incomeTypeDescription: String
    lateinit var payer: String
    lateinit var amount: String
    lateinit var feeId: String
    private val layoutServiceList = mutableListOf<LinearLayout>()
    private val selectedFeeAndCharges = mutableMapOf<SearchableSpinner, FeesAndCharges>()
    private var selectedFeeAndPositionPosition = mutableMapOf<SearchableSpinner, String>()
    private var serviceCounter = 2

    override fun onResume() {
        ed_slaughter_house.setText(getValue(this,"zone"))
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_park)

        tvZone.text ="Zone"
        tvNumberPlateLabel.text ="ID/Customer/No Plate/Business ID"
        edPlate.hint ="Enter customer identifier"
        tv_category.text ="General Billing"

        imageClose.setOnClickListener { finish() }

        ed_slaughter_house.setOnClickListener {
            startActivity(Intent(this, SelectZone::class.java))
        }
        val layoutService = findViewById<LinearLayout>(R.id.layout_service)
        val btnAddService = findViewById<TextView>(R.id.btn_add_service)

        btnAddService.setOnClickListener {
            addNewServiceLayout(layoutService)
        }

        btnNext.setOnClickListener {
            Const.instance.setSelectedFeesAndChargeList(selectedFeeAndCharges)
            startActivity(Intent(this, BillingSummary::class.java))
        }

        edPlate.addTextChangedListener { text ->
            // Retrieve the position for the corresponding spinnerFeeAndCharges
            val position = selectedFeeAndPositionPosition[spinnerFeeAndCharges]
            // Check if the position exists in the map
            if (position != null) {
                // Retrieve the FeesAndCharges object associated with the selected spinner
                val selectedFeesAndCharges = selectedFeeAndCharges[spinnerFeeAndCharges]

                // Check if the FeesAndCharges object exists
                if (selectedFeesAndCharges != null) {
                    // Update the quantity in the FeesAndCharges object
                    selectedFeesAndCharges.customer = text.toString()
                }
            }
        }
        edDescription.addTextChangedListener { text ->
            // Retrieve the position for the corresponding spinnerFeeAndCharges
            val position = selectedFeeAndPositionPosition[spinnerFeeAndCharges]
            // Check if the position exists in the map
            if (position != null) {
                // Retrieve the FeesAndCharges object associated with the selected spinner
                val selectedFeesAndCharges = selectedFeeAndCharges[spinnerFeeAndCharges]

                // Check if the FeesAndCharges object exists
                if (selectedFeesAndCharges != null) {
                    // Update the quantity in the FeesAndCharges object
                    selectedFeesAndCharges.description = text.toString()
                }
            }
        }

        getIncomeTypes()
    }
    private fun addNewServiceLayout(parentLayout: LinearLayout) {
        // Increment the counter when adding a new service layout
        val newServiceLayout = LayoutInflater.from(this).inflate(R.layout.layout_service, null)

        // Find the views in the new layout
        val spinnerIncomeType =
            newServiceLayout.findViewById<SearchableSpinner>(R.id.spinnerIncomeType)
        val spinnerFeeAndCharges = newServiceLayout.findViewById<SearchableSpinner>(R.id.spinnerFeeAndCharges)

        val edQuantity = newServiceLayout.findViewById<EditText>(R.id.edQuantity)

        edQuantity.addTextChangedListener { text ->
            // Retrieve the position for the corresponding spinnerFeeAndCharges
            val position = selectedFeeAndPositionPosition[spinnerFeeAndCharges]

            // Check if the position exists in the map
            if (position != null) {
                // Retrieve the FeesAndCharges object associated with the selected spinner
                val selectedFeesAndCharges = selectedFeeAndCharges[spinnerFeeAndCharges]

                // Check if the FeesAndCharges object exists
                if (selectedFeesAndCharges != null) {
                    // Update the quantity in the FeesAndCharges object
                    selectedFeesAndCharges.quantity = text.toString()

                    // You may want to log or perform additional actions here
                   // Log.d("QuantityUpdate", "Spinner: $spinnerFeeAndCharges, Quantity: ${text.toString()}")
                    getSelectedFeeAndChargesItems()
                }
            }
        }

        edPlate.addTextChangedListener { text ->
            // Retrieve the position for the corresponding spinnerFeeAndCharges
            val position = selectedFeeAndPositionPosition[spinnerFeeAndCharges]
            // Check if the position exists in the map
            if (position != null) {
                // Retrieve the FeesAndCharges object associated with the selected spinner
                val selectedFeesAndCharges = selectedFeeAndCharges[spinnerFeeAndCharges]

                // Check if the FeesAndCharges object exists
                if (selectedFeesAndCharges != null) {
                    // Update the quantity in the FeesAndCharges object
                    selectedFeesAndCharges.customer = text.toString()
                }
            }
        }
        edDescription.addTextChangedListener { text ->
            // Retrieve the position for the corresponding spinnerFeeAndCharges
            val position = selectedFeeAndPositionPosition[spinnerFeeAndCharges]
            // Check if the position exists in the map
            if (position != null) {
                // Retrieve the FeesAndCharges object associated with the selected spinner
                val selectedFeesAndCharges = selectedFeeAndCharges[spinnerFeeAndCharges]

                // Check if the FeesAndCharges object exists
                if (selectedFeesAndCharges != null) {
                    // Update the quantity in the FeesAndCharges object
                    selectedFeesAndCharges.description = text.toString()
                }
            }
        }



        val btnRemoveService = newServiceLayout.findViewById<TextView>(R.id.btn_remove_service)
        btnRemoveService.visibility = View.VISIBLE
        val tvItemNumber = newServiceLayout.findViewById<TextView>(R.id.tv_item_number)

        btnRemoveService.setOnClickListener {
            // Decrement the counter when removing a service layout
            removeLastServiceLayout(newServiceLayout as LinearLayout, spinnerFeeAndCharges)
        }

        // Set the position or number on the tv_item_number TextView
        tvItemNumber.text = "Bill item no " + serviceCounter.toString() + "."
        serviceCounter++

        // Set up any additional configurations or listeners for the new layout elements if needed

        // Add the new layout to the parent layout and store a reference in the list
        parentLayout.addView(newServiceLayout)
        layoutServiceList.add(newServiceLayout as LinearLayout)

        // Update the adapter for the dynamically added spinners
        updateDynamicSpinnerAdapter(spinnerIncomeType)
        updateDynamicSpinnerAdapter(spinnerFeeAndCharges)

        // Add a listener to spinnerIncomeType to update the dynamically added spinnerFeeAndCharges
        spinnerIncomeType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                // Fetch and update data for spinnerFeeAndCharges based on the selected income type
                val selectedIncomeTypeId = response.data.incomeTypes[position].incomeTypeId
                spinnerFeeAndCharges(selectedIncomeTypeId, spinnerFeeAndCharges)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

    }
    private fun updateDynamicSpinnerAdapter(spinner: SearchableSpinner) {
        // Assuming you have a list of items for the spinner (e.g., arrayList or arrayList2)
        val adapter = ArrayAdapter<String>(this, R.layout.simple_spinner_dropdown_item, arrayList)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        // Add any additional configuration or listener if needed
    }
    private fun removeLastServiceLayout(serviceLayout: LinearLayout, spinnerFeeAndCharges: SearchableSpinner) {
        if (layoutServiceList.contains(serviceLayout)) {
            // Decrement the counter when removing a service layout
            serviceCounter--

            // Remove the specific entry from the map when a layout is removed
            selectedFeeAndCharges.remove(spinnerFeeAndCharges)
            // Remove the last added layout from the parent layout and the list
            layoutServiceList.remove(serviceLayout)
            val parentLayout = serviceLayout.parent as LinearLayout
            parentLayout.removeView(serviceLayout)
        }
    }

    private fun getIncomeTypes () {
        val formData = listOf(
            "function" to "getIncomeTypes",
            "incomeTypePrefix" to "",
            "deviceId" to getDeviceIdNumber(this)
        )
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
                response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){
                    runOnUiThread {
                        for(data in response.data.incomeTypes){
                            arrayList.add(data.incomeTypeDescription)
                        }

                        //Spinner
                        val adapters = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item,arrayList)
                        adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        spinnerIncomeType.adapter = adapters
                        spinnerIncomeType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                                incomeTypeDescription = response.data.incomeTypes[position].incomeTypeDescription
                                save(this@Billing, "incomeTypeDescription", incomeTypeDescription)

                                // Fetch and update data for spinnerFeeAndCharges based on the selected income type
                                val selectedIncomeTypeId = response.data.incomeTypes[position].incomeTypeId
                                spinnerFeeAndCharges(selectedIncomeTypeId,spinnerFeeAndCharges)
                            }

                            override fun onNothingSelected(p0: AdapterView<*>?) {
                            }
                        }
                    }

                } else {
                    runOnUiThread {
                        Toast.makeText(this@Billing, response.message, Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@Billing,result, Toast.LENGTH_LONG).show()
                }
            }
        })
    }
    private fun spinnerFeeAndCharges(incomeTypeId: String, spinnerFeeAndCharges: SearchableSpinner) {
        val formData = listOf(
            "function" to "getFeesAndCharges",
            "incomeTypeId" to incomeTypeId,
            "deviceId" to getDeviceIdNumber(this)
        )

        executeRequest(formData, biller, object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                runOnUiThread {
                    arrayList2.clear()
                    val adapters = ArrayAdapter<String>(
                        applicationContext,
                        R.layout.simple_spinner_dropdown_item,
                        arrayList2
                    )
                    adapters.clear()
                    if (response.success) {
                        for (data in response.data.feesAndCharges) {
                            arrayList2.add(data.feeDescription)
                        }

                        // Update the adapter for spinnerFeeAndCharges
                        adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        spinnerFeeAndCharges.adapter = adapters
                        // Add any additional configuration or listener if needed

                        //so that the first one that is not dynamically added can be selected
                        spinnerFeeAndCharges.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                                selectedFeeAndPositionPosition[spinnerFeeAndCharges] = position.toString()
                                // Store the selected item for the corresponding spinnerFeeAndCharges
                                selectedFeeAndCharges[spinnerFeeAndCharges] = response.data.feesAndCharges[position]
                                getSelectedFeeAndChargesItems()

                                edQuantity.addTextChangedListener { text ->
                                    // Retrieve the position for the corresponding spinnerFeeAndCharges
                                    val position = selectedFeeAndPositionPosition[spinnerFeeAndCharges]

                                    // Check if the position exists in the map
                                    if (position != null) {
                                        // Retrieve the FeesAndCharges object associated with the selected spinner
                                        val selectedFeesAndCharges = selectedFeeAndCharges[spinnerFeeAndCharges]

                                        // Check if the FeesAndCharges object exists
                                        if (selectedFeesAndCharges != null) {
                                            // Update the quantity in the FeesAndCharges object
                                            selectedFeesAndCharges.quantity = text.toString()

                                            // You may want to log or perform additional actions here
                                            // Log.d("QuantityUpdate", "Spinner: $spinnerFeeAndCharges, Quantity: ${text.toString()}")
                                            getSelectedFeeAndChargesItems()
                                        }
                                    }
                                }
                            }

                            override fun onNothingSelected(p0: AdapterView<*>?) {
                            }
                        }

                    } else {
                        spinnerFeeAndCharges.adapter = null
                        Toast.makeText(this@Billing, response.message, Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@Billing, result, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun getSelectedFeeAndChargesItems() {
        var totalSum: Double = 0.0

        for ((spinner, selectedValue) in selectedFeeAndCharges) {
            // Log or use the selected values as needed
            Log.d("SelectedItems", "Spinner: $spinner, Selected Value: ${selectedValue.feeDescription} Quantity: ${selectedValue.quantity}")

            // Check if unitFeeAmount is not empty before converting to Double
            if (selectedValue.unitFeeAmount.isNotEmpty()) {
                // Check if selectedValue.quantity is not empty or null
                val quantity = if (selectedValue.quantity.isNullOrEmpty()) "1" else selectedValue.quantity
                // Add the unitFeeAmount multiplied by quantity to the total sum
                totalSum += selectedValue.unitFeeAmount.toDouble() * quantity.toInt()
                // Log or perform additional actions as needed
                Log.d("Calculation", "UnitFeeAmount: ${selectedValue.unitFeeAmount}, Quantity: $quantity")
            } else {
                Log.e("Error", "Empty or non-numeric unitFeeAmount encountered.")
                // Handle the error or log a message as needed
            }
        }
        // Now, you have the total sum of unitFeeAmount for all selected items
        Log.d("TotalSum", "Total Sum: $totalSum")
        // You can use the totalSum as needed, e.g., display it in a TextView or perform further actions

        // You can use the totalSum as needed, e.g., display it in a TextView or perform further actions
        runOnUiThread {
            tv_amount.text ="KES $totalSum"
        }
    }

}