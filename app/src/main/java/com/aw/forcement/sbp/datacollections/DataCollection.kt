package com.aw.forcement.sbp.datacollections

import Business
import Json4Kotlin_Base
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.aw.forcement.R
import com.aw.passanger.api.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_data_collection.*
import kotlinx.android.synthetic.main.activity_login.*
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class DataCollection : AppCompatActivity() {

    private val arrayListSubCounty = ArrayList<String>()
    private val arrayListWard = ArrayList<String>()
    private val arrayListCategory = ArrayList<String>()
    private val arrayListFeeAndCharges = ArrayList<String>()
    lateinit var amount: String
    lateinit var feeId: String
    var tonnage: String =""
    var liquorLicence: String ="NO"
    var fireLicence: String ="NO"
    var businessType: String =""
    var business_category: String =""
    var business_sub_category: String =""

    var subCountyID: String =""
     var subCountyName: String =""
   var wardName: String =""
    var wardID: String =""

    private var locationManager : LocationManager? = null
    private var locationVerified = true;
   var permissionId = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_collection)

        ed_gps.setText(getValue(this, "latitude")+","+getValue(this, "longitude").toString())


        checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                ed_gps.setText(getValue(this, "latitude")+","+getValue(this, "longitude").toString())

            } else {
                ed_gps.setText("")
                save(this,"lat","")
                save(this,"lng","")

            }
        }
        checkboxLiqour.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                liquorLicence ="YES"
            } else {
                liquorLicence ="NO"

            }
        }
        checkboxFire.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                fireLicence ="NO"
            } else {
                fireLicence ="NO"

            }
        }


        getIncomeTypes()
        getSubCounties()
      //getBusinessType()

        btn_next.setOnClickListener { dataCollection()  }

    }

    private fun getJsonData(): String {
        val updatedBusiness = Business(
            id = "",
            businessID = "",
            businessName =  ed_business_name.text.toString(),
            subCountyID = subCountyID,
            subCountyName = subCountyName,
            wardID = wardID,
            wardName = wardName,
            plotNumber = ed_plot_number.text.toString(),
            physicalAddress = ed_physical_address.text.toString(),
            buildingName = "",
            buildingOccupancy = "",
            floorNo = "",
            roomNo = "",
            premiseSize = "",
            numberOfEmployees = "",
            tonnage ="",
            businessDes = ed_business_des.text.toString(),
            businessCategory = business_category,
            businessSubCategory = business_sub_category,
            incomeTypeID = "",
            feeID = feeId,
            businessEmail ="",
            postalAddress = "",
            postalCode = "",
            businessPhone = "",
            contactPersonNames = ed_contact_person_names.text.toString(),
            contactPersonIDNo = getValue(this, "contact_person_idNo").toString(),
            businessRole = getValue(this, "business_role").toString(),
            contactPersonPhone = ed_contact_person_phone.text.toString(),
            contactPersonEmail = "",
            fullNames ="",
            ownerID = "",
            ownerPhone = "",
            ownerEmail ="",
            kraPin = "",
            createdBy = getValue(this, "username").toString(),
            createdByIDNo = getValue(this, "idNo").toString(),
            dateCreated = "",
            lat = getValue(this, "latitude").toString(),
            lng = getValue(this, "longitude").toString(),
            liquor = "",
            conservancy = "",
            fireLicence = fireLicence,
            liquorLicence = liquorLicence,
            businessType = ed_business_type.text.toString(),

        )



        Const.instance.setBusiness(updatedBusiness)
        val gson = Gson()
        return gson.toJson(Const.instance.getBusiness())
    }

    private fun dataCollection (){
        progress_circular.visibility = View.VISIBLE
        val formData = listOf(
            "function" to "dataCollection",
            "business" to getJsonData()

        )
        executeRequest(formData, trade,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    runOnUiThread {
                        progress_circular.visibility = View.GONE
                        Toast.makeText(this@DataCollection,response.message, Toast.LENGTH_LONG).show()

                        finish()
                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@DataCollection,response.message, Toast.LENGTH_LONG).show()}

                }

            }

        })
    }


  /*  private fun getBusinessType (){
        val formData = listOf(
            "function" to "getBusinessType",
        )
        executeRequest(formData, trade,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    runOnUiThread {

                        for(data in response.data.businessTypes){
                            arrayList4.add(data.type)
                        }

                        //Spinner
                        val adapters = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item,arrayList4)
                        adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        spinner_type.adapter = adapters
                        spinner_type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {
                                businessType =response.data.businessTypes[postion].type


                            }
                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }
                        }

                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@DataCollection,response.message, Toast.LENGTH_LONG).show()}

                }

            }

        })
    }*/
    private fun getSubCounties (){
        val formData = listOf(
            "function" to "getSubCounty",
        )
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    runOnUiThread {

                        for(data in response.data.subCounties){
                            arrayListSubCounty.add(data.subCountyName)
                        }

                        //Spinner
                        val adapters = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item,arrayListSubCounty)
                        adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        spinner_sub_county.adapter = adapters
                        spinner_sub_county.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {
                                subCountyID =response.data.subCounties[postion].subCountyID
                                subCountyName =response.data.subCounties[postion].subCountyName
                                getWards()

                            }
                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }
                        }

                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@DataCollection,response.message, Toast.LENGTH_LONG).show()}

                }

            }

        })
    }
    private fun getWards (){
        val formData = listOf(
            "function" to "getWards",
            "subCountyID" to subCountyID
        )
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    runOnUiThread {
                        arrayListWard.clear()
                        for(data in response.data.wards){
                            arrayListWard.add(data.wardName)
                        }

                        //Spinner
                        val adapters = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item,arrayListWard)
                        adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        spinner_ward.adapter = adapters
                        spinner_ward.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {
                                wardID =response.data.wards[postion].wardID.toString()
                                wardName =response.data.wards[postion].wardName.toString()

                            }
                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }
                        }

                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@DataCollection,response.message, Toast.LENGTH_LONG).show()}

                }

            }

        })
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
                            arrayListCategory.add(data.incomeTypeDescription)
                        }

                        //Spinner
                        val adapters = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item,arrayListCategory)
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
                    runOnUiThread {  Toast.makeText(this@DataCollection,response.message, Toast.LENGTH_LONG).show()}

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
                runOnUiThread {  arrayListFeeAndCharges.clear()
                    val adapters = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item,arrayListFeeAndCharges)
                    adapters.clear()
                    if(response.success){
                        runOnUiThread {
                            for(data in response.data.feesAndCharges){
                                arrayListFeeAndCharges.add(data.feeDescription)
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
                        Toast.makeText(this@DataCollection,response.message, Toast.LENGTH_LONG).show() }
                }

            }

        })
    }

    override fun onResume() {

        checkPermissions()
       if(!isLocationEnabled()){
           showGPSDisabledAlertToUser()
       }
        getLocation()
        super.onResume()
    }


    //Locational updates

    private fun showGPSDisabledAlertToUser() {
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("GPS is disabled in your device. Enable it so that we can capture the location of the business Would you like to enable it?")
            .setCancelable(false)
            .setPositiveButton("Goto Settings Page To Enable GPS",
                DialogInterface.OnClickListener { dialog, id ->
                    val callGPSSettingIntent = Intent(
                        Settings.ACTION_LOCATION_SOURCE_SETTINGS
                    )
                    startActivity(callGPSSettingIntent)
                })
        alertDialogBuilder.setNegativeButton("Okay",
            DialogInterface.OnClickListener {
                    dialog, id -> dialog.cancel()
                val callGPSSettingIntent = Intent(
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS
                )
                startActivity(callGPSSettingIntent)

            }
        )
        val alert: AlertDialog = alertDialogBuilder.create()
        alert.show()
    }
    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }
    private fun getLocation(){
        // Create persistent LocationManager reference
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }
        locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener)
    }

     fun getAddress(context: Context, lat: Double, lng: Double): String {
          //  val lat = mMap.cameraPosition.target.latitude
           // val lng = mMap.cameraPosition.target.longitude
        // Initializing Geocoder
        val mGeocoder = Geocoder(context, Locale.getDefault())
        // Reverse-Geocoding starts
        try {
            val addressList: List<Address> = mGeocoder.getFromLocation(lat, lng, 1)!!

            // use your lat, long value here
            if (addressList.isNotEmpty()) {
                val address = addressList[0]
                val sb = StringBuilder()
                for (i in 0 until address.maxAddressLineIndex) {
                    sb.append(address.getAddressLine(i)).append("\n")
                }

                // Various Parameters of an Address are appended
                // to generate a complete Address
                if (address.premises != null)
                    sb.append(address.premises).append(", ")

                sb.append(address.subAdminArea).append("\n")
                sb.append(address.locality).append(", ")
                sb.append(address.adminArea).append(", ")
                sb.append(address.countryName).append(", ")
                sb.append(address.featureName)

                // StringBuilder sb is converted into a string
                // and this value is assigned to the
                // initially declared addressString string.
                return sb.toString().replace("null","")


            }
        } catch (e: IOException) {
            Toast.makeText(context, "Unable connect to Geocoder", Toast.LENGTH_LONG).show()
        }
        return "No address found";
    }
    //define the listener
     private val locationListener: LocationListener = object : LocationListener {
         override fun onLocationChanged(location: Location) {
             Log.e("####","${location.latitude} : ${location.longitude}")

             runOnUiThread {
                 locationVerified = true
                 val address = getAddress(this@DataCollection,location.latitude,location.longitude)

                 save(this@DataCollection,"lat",location.latitude.toString())
                 save(this@DataCollection,"lng",location.longitude.toString())
                 save(this@DataCollection,"address",address)
                // message.text = ("" + location.longitude + ":" + location.latitude)
                 ed_gps.setText("${location.latitude},${location.longitude}")
             }
         }
         override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
         override fun onProviderEnabled(provider: String) {}
         override fun onProviderDisabled(provider: String) {}
     }

}