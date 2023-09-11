package com.aw.forcement.sbp.applications

import Business
import Const
import Json4Kotlin_Base
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.aw.forcement.R
import com.aw.forcement.adapters.AdapterStatus
import com.aw.passanger.api.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_applications.*
import kotlinx.android.synthetic.main.activity_applications_detail.*
import kotlinx.android.synthetic.main.activity_applications_detail.tv_message_header
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.recycler_view.*


class ApplicationsDetail : AppCompatActivity() {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_applications_detail)
        tv_message_header.text = getValue(this,"header")
        close.setOnClickListener { finish() }

        runOnUiThread {  getBusinessDetails() }
        save(this,"","")

        inspection.setOnClickListener { startActivity(Intent(this,ApplicationVerificationOwner::class.java)) }


    }



    private fun getBusinessDetails (){
        runOnUiThread {  progress_circular.visibility = View.VISIBLE }
        val formData = listOf(
            "function" to "getBusinessDetails",
            "id" to intent.getStringExtra("id").toString(),
        )
        executeRequest(formData, trade,object : CallBack {
            override fun onSuccess(result: String?) {
                runOnUiThread {  progress_circular.visibility = View.GONE }
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){
                    runOnUiThread {

                        tv_business_name.text = response.data.business.businessName
                        tv_address.text = response.data.business.physicalAddress

                        //Store in Const
                        Const.instance.setBusiness(response.data.business)

                        val latLng = LatLng(response.data.business.lat.toDouble(),response.data.business.lng.toDouble())

                        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment?
                        mapFragment?.getMapAsync(object : OnMapReadyCallback {
                            override fun onMapReady(googleMap: GoogleMap) {

                                runOnUiThread {    mMap = googleMap

                                    val success= googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this@ApplicationsDetail,R.raw.style_json));

                                    if (!success) {
                                        Log.e(ContentValues.TAG, "Style parsing failed.")
                                    }

                                    moveMapCamera(mMap, latLng)
                                    placeMarkerOnMap(this@ApplicationsDetail,mMap, latLng) }
                            }

                        })



                        val adapter = AdapterStatus(this@ApplicationsDetail, response.data.statuses)
                        adapter.notifyDataSetChanged()
                        recyclerView.layoutManager = LinearLayoutManager(this@ApplicationsDetail)
                        recyclerView.adapter = adapter
                        recyclerView.setHasFixedSize(false)

                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@ApplicationsDetail,response.message, Toast.LENGTH_LONG).show()}

                }

            }

        })
    }


    fun moveMapCamera(mMap: GoogleMap,latLng : LatLng){

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                latLng, 16.0f
            )
        )
    }
    fun placeMarkerOnMap(context: Context, mMap: GoogleMap, location: LatLng) {

        val height = 31
        val width = 19
        val bitmaps = context.resources.getDrawable(R.drawable.business_location) as BitmapDrawable
        val b = bitmaps.bitmap
        //   val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)
        val markerOptions = MarkerOptions().position(location)
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(b))
        mMap.addMarker(markerOptions)

    }


}


