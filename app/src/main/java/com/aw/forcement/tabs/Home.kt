package com.aw.forcement.tabs

import Json4Kotlin_Base
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aw.forcement.*
import com.aw.forcement.others.*
import com.aw.passanger.api.*
import com.aw.passanger.api.parking
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_page.*
import kotlinx.android.synthetic.main.bottom_nav.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.message.*
import kotlinx.android.synthetic.main.progressbar.*
import java.io.IOException
import java.util.*


class Home : AppCompatActivity() {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private var locationPermissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        addBusiness.setOnClickListener { startActivity(Intent(this, AddBusiness::class.java)) }
        imagePay.setOnClickListener { startActivity(Intent(this, CessPayments::class.java).putExtra("incomeTypePrefix","")) }
        imageScan.setOnClickListener { startActivity(Intent(this, ScanOptions::class.java)) }
        imageHome.setColorFilter(ContextCompat.getColor(this, R.color.selector))

        history.setOnClickListener {  startActivity(Intent(this, History::class.java))
            finish()}
        profile.setOnClickListener {  startActivity(Intent(this, Profile::class.java))
            finish()}

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)

        receipt.setOnClickListener { startActivity(Intent(this, CessPayments::class.java).putExtra("incomeTypePrefix","MKT")) }
        business.setOnClickListener { toggleBottomSheet("business") }
        cess.setOnClickListener { toggleBottomSheet("cess") }
       // parking.setOnClickListener { startActivity(Intent(this, Offstreet::class.java)) }
        matatusStageCess.setOnClickListener { startActivity(Intent(this, CessPaymentsMatatus::class.java)) }

      //  offstreet.setOnClickListener {  startActivity(Intent(this, Offstreet::class.java)) }
        transaction.setOnClickListener {  startActivity(Intent(this, Transactions::class.java)) }
      //  offstreet.setOnClickListener {  startActivity(Intent(this, Street::class.java))  }
        closeBottom.setOnClickListener {   bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED }
        streetParking.setOnClickListener { startActivity(Intent(this, Street::class.java)) }
        imagePaking.setOnClickListener { startActivity(Intent(this, Parking::class.java))  }
      //  imagePaking.setOnClickListener { startActivity(Intent(this, CessPaymentsMatatus::class.java))  }


    }

    private fun toggleBottomSheet(type: String){
        runOnUiThread {   tvMessage.text = ""
            edSearch.text.clear()
        }

        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }
        if(type=="receipt"){
            tvTitle.text = getString(R.string.receipt_query)
            tvEnter.text = getString(R.string.enter_receipt)
            buttonSearch.setOnClickListener {
                queryReceiptNumber()
            }
        }
        if(type=="business"){
            tvTitle.text = getString(R.string.business_query)
            tvEnter.text = getString(R.string.enter_business)
            buttonSearch.setOnClickListener { checkBusiness() }
        }
        if(type=="cess"){
            tvTitle.text = getString(R.string.cess_query)
            tvEnter.text = getString(R.string.enter_number_plate)
            buttonSearch.setOnClickListener {
                enforceByPlateNumber()
            }
        }
        if(type=="parking"){
            tvTitle.text = getString(R.string.parking)
            tvEnter.text = getString(R.string.enter_number_plate)
            buttonSearch.setOnClickListener {  getParking() }
        }
    }
    override fun onResume() {
        super.onResume()
        //name.text = getValue(this,"username")
     //   nameTag.text = getValue(this,"firstName").toString()[0].toString()+""+getValue(this,"lastName").toString()[0].toString()

        locationPermission()

    }
     private fun queryReceiptNumber(){

         if(edSearch.text.toString().isEmpty()){
             Toast.makeText(this,"Empty",Toast.LENGTH_LONG).show()
             return
         }

         progress_circular.visibility = View.VISIBLE
         val formData = listOf(
             "function" to "getReceipt",
             "receiptNo" to edSearch.text.toString(),
             "latitude" to getValue(this,"latitude").toString(),
             "longitude" to getValue(this,"longitude").toString(),
             "idNo" to getValue(this,"idNo").toString(),
             "username" to getValue(this,"username").toString(),
             "addressString" to getValue(this,"addressString").toString()
         )
         executeRequest(formData, com.aw.passanger.api.parking,object : CallBack{
             override fun onSuccess(result: String?) {
                 runOnUiThread {  progress_circular.visibility = View.GONE }
                 val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)

                 if(response.success){
                     runOnUiThread { bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED }
                     startActivity(Intent(this@Home,Receipt::class.java).putExtra("result",result))
                     runOnUiThread {   tvMessage.text = ""}
                 }else{
                     runOnUiThread {
                         tvMessage.text = response.message }
                 }
             }

         })


     }
     private fun checkBusiness(){

         if(edSearch.text.toString().isEmpty()){
             Toast.makeText(this,"Empty",Toast.LENGTH_LONG).show()
             return
         }
         progress_circular.visibility = View.VISIBLE
         val formData = listOf(
             "function" to "printTradePermit",
             "businessID" to edSearch.text.toString(),
             "latitude" to getValue(this,"latitude").toString(),
             "longitude" to getValue(this,"longitude").toString(),
             "idNo" to getValue(this,"idNo").toString(),
             "username" to getValue(this,"username").toString(),
             "addressString" to getValue(this,"addressString").toString()
         )
         executeRequest(formData, trade,object : CallBack{
             override fun onSuccess(result: String?) {
                 runOnUiThread {  progress_circular.visibility = View.GONE }
                 val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                 if(response.success){
                     runOnUiThread { bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED }
                     startActivity(Intent(this@Home,Business::class.java).putExtra("result",result))
                     runOnUiThread {   tvMessage.text = ""}
                 }else{
                     runOnUiThread {   tvMessage.text = response.message }
                 }
             }
         })

     }
     private fun getParking(){
        progress_circular.visibility = View.VISIBLE

        val formData = listOf(
            "function" to "getParking",
            "numberPlate" to edSearch.text.toString(),
            "latitude" to getValue(this,"latitude").toString(),
            "longitude" to getValue(this,"longitude").toString(),
            "idNo" to getValue(this,"idNo").toString(),
            "username" to getValue(this,"username").toString(),
            "addressString" to getValue(this,"addressString").toString()
        )
        executeRequest(formData, com.aw.passanger.api.parking,object : CallBack{
            override fun onSuccess(result: String?) {
                runOnUiThread {  progress_circular.visibility = View.GONE }
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){
                    runOnUiThread { bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED }
                    startActivity(Intent(this@Home,Parking::class.java).putExtra("result",result))
                    runOnUiThread {   tvMessage.text = ""}

                }else{
                    runOnUiThread {
                        buttonSearch.text ="CLAMP IT!!"
                        tvMessage.text = response.message }

                }

            }

        })

    }
     private fun enforceByPlateNumber(){

         if(edSearch.text.toString().isEmpty()){
             Toast.makeText(this,"Empty",Toast.LENGTH_LONG).show()
             return
         }
         progress_circular.visibility = View.VISIBLE
         val formData = listOf(
             "function" to "enforceByPlateNumber",
             "customer" to edSearch.text.toString(),
             "idNo" to getValue(this,"idNo").toString(),
             "latitude" to getValue(this,"latitude").toString(),
             "longitude" to getValue(this,"longitude").toString(),
             "TownId" to getValue(this,"TownId").toString()
         )
         executeRequest(formData, biller,object : CallBack {
             override fun onSuccess(result: String?) {
                 runOnUiThread {  progress_circular.visibility = View.GONE }
                 val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                 if(response.success){
                     runOnUiThread { bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED }
                     startActivity(Intent(this@Home,Cess::class.java).putExtra("result",result))
                     runOnUiThread {   tvMessage.text = ""}
                 }else{
                     runOnUiThread {   tvMessage.text = response.message }
                 }

             }

         })

     }
    var exit: Boolean =false
    override fun onBackPressed() {
        if(exit){
            finish()
        }else{
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            exit = true
        }
    }

    //Device location
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        locationPermissionGranted = false
        when (requestCode) {
            requestCode -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                    finishAffinity()
                    startActivity(Intent(this,Home::class.java))
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }

    }
    private fun locationPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }else{
            locationPermissionGranted = true

            val mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val mGPS = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

            getDeviceLocation()
        }

    }
    //Get device location
    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                println("## locationPermissionGranted")
                val locationResult = LocationServices.getFusedLocationProviderClient(this)
                locationResult.lastLocation.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        println("## Current location is okay ")
                        // Set the map's camera position to the current location of the device.
                        val lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            println("## lastKnownLocation lat{${lastKnownLocation.latitude}} long{${lastKnownLocation.longitude}}")
                            save(this,"latitude", lastKnownLocation.latitude.toString())
                            save(this,"longitude", lastKnownLocation.longitude.toString())
                            val address = getAddress(lastKnownLocation.latitude,lastKnownLocation.longitude)
                            println("## address lat{${address}}")
                        }else{
                            println("## lastKnownLocation null ")
                        }
                    } else {
                        println("## Current location is null. Using defaults."+ task.exception)
                    }
                }
            }else{
                println("## locationPermissionNotGranted")

            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }
    private fun getAddress(lat: Double, lng: Double) {
        // Initializing Geocoder
        val mGeocoder = Geocoder(applicationContext, Locale.getDefault())
        var addressString= ""

        // Reverse-Geocoding starts
        try {
            val addressList: List<Address> = mGeocoder.getFromLocation(lat,lng, 1)!!

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
                addressString = sb.toString()
              //  runOnUiThread {  tvAddress.text = addressString.replace("null","") }
                save(this,"addressString",addressString.replace("null",""))
                save(this,"latLng","${lat}, $lng")
               // Log.d("###",tvAddress.text.toString())
            }
        } catch (e: IOException) {
            Toast.makeText(applicationContext,"Unable connect to Geocoder",Toast.LENGTH_LONG).show()
        }

    }

}