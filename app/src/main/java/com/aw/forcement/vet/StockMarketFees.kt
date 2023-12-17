package com.aw.forcement.vet

import Json4Kotlin_Base
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.aw.forcement.R
import com.aw.passanger.api.*
import com.google.gson.Gson
import com.toptoche.searchablespinnerlibrary.SearchableSpinner
import kotlinx.android.synthetic.main.activity_stock_market_fees.*
import kotlinx.android.synthetic.main.layout_service.*


class StockMarketFees : AppCompatActivity() {
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
    private val selectedFeeAndCharges = mutableMapOf<SearchableSpinner, String>()
    private var serviceCounter = 2



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock_market_fees)

        val layoutService = findViewById<LinearLayout>(R.id.layout_service)
        val btnAddService = findViewById<TextView>(R.id.btn_add_service)

        btnAddService.setOnClickListener {
            addNewServiceLayout(layoutService)
        }

        btnNext.setOnClickListener {
            getSelectedFeeAndChargesItems()
        }

        getIncomeTypes()
    }

    private fun addNewServiceLayout(parentLayout: LinearLayout) {
        // Increment the counter when adding a new service layout
        val newServiceLayout = LayoutInflater.from(this).inflate(R.layout.layout_service, null)

        // Find the views in the new layout
        val spinnerIncomeType =
            newServiceLayout.findViewById<SearchableSpinner>(R.id.spinnerIncomeType)
        val spinnerFeeAndCharges =
            newServiceLayout.findViewById<SearchableSpinner>(R.id.spinnerFeeAndCharges)
        val edQuantity = newServiceLayout.findViewById<EditText>(R.id.edQuantity)
        val btnRemoveService = newServiceLayout.findViewById<TextView>(R.id.btn_remove_service)
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

        // Add a listener to spinnerFeeAndCharges to track selected items
        spinnerFeeAndCharges.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                // Store the selected item for the corresponding spinnerFeeAndCharges
                selectedFeeAndCharges[spinnerFeeAndCharges] = arrayList2[position]
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
            "incomeTypePrefix" to "MKT",
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
                                save(this@StockMarketFees, "incomeTypeDescription", incomeTypeDescription)

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
                        Toast.makeText(this@StockMarketFees, response.message, Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@StockMarketFees,result, Toast.LENGTH_LONG).show()
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
                    } else {
                        spinnerFeeAndCharges.adapter = null
                        Toast.makeText(this@StockMarketFees, response.message, Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@StockMarketFees, result, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    // Function to retrieve the selected FeeAndCharges items for each layout
    // Function to retrieve the selected FeeAndCharges items
    private fun getSelectedFeeAndChargesItems() {
        for ((spinner, selectedValue) in selectedFeeAndCharges) {
            // Log or use the selected values as needed
            Log.d("SelectedItems", "Spinner: $spinner, Selected Value: $selectedValue")
        }
    }

}