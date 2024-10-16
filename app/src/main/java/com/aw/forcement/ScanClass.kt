package com.aw.forcement

import Json4Kotlin_Base
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aw.forcement.others.Business
import com.aw.forcement.others.Receipt
import com.aw.forcement.others.Street
import com.aw.forcement.others.TransactionsResults
import com.aw.passanger.api.*
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_scan_class.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.message.*
import kotlinx.android.synthetic.main.progressbar.*
import java.io.IOException

class ScanClass : AppCompatActivity() {

    val requestCodeCameraPermission = 1001
    lateinit var cameraSource: CameraSource
    lateinit var barcodeDetector: BarcodeDetector
    var scannedValue = ""
    var boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_class)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        if (ContextCompat.checkSelfPermission(
                this@ScanClass, android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            askForCameraPermission()
        } else {
            setupControls()
        }

        val aniSlide: Animation =
            AnimationUtils.loadAnimation(this@ScanClass, R.anim.scanner_animation)
        barcode_line.startAnimation(aniSlide)
    }


    private fun setupControls() {
        barcodeDetector =
            BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build()

        cameraSource = CameraSource.Builder(this, barcodeDetector)
            .setRequestedPreviewSize(1920, 1080)
            .setAutoFocusEnabled(true) //you should add this feature
            .build()

        cameraSurfaceView.getHolder().addCallback(object : SurfaceHolder.Callback {
            @SuppressLint("MissingPermission")
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    //Start preview after 1s delay
                    cameraSource.start(holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            @SuppressLint("MissingPermission")
            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                try {
                    cameraSource.start(holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }
        })
        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
                Toast.makeText(applicationContext, "Scanner has been closed", Toast.LENGTH_SHORT)
                    .show()
            }
            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() == 1) {

                    scannedValue = barcodes.valueAt(0).rawValue

                    Log.e("########",scannedValue)
                    if(scannedValue.contains("Business")){
                        val businessID = scannedValue.split("|")[0].split(":")[1].trim()
                        runOnUiThread {
                            cameraSource.stop()
                            if(boolean){
                                boolean = false
                                checkBusiness(businessID)
                            }
                        }
                    }
                    else if(scannedValue.contains("Sticker")){

                        val numberPlate = scannedValue.split("~")[0]
                        runOnUiThread {
                            cameraSource.stop()
                            if(boolean){
                                boolean = false
                                startActivity(Intent(this@ScanClass, Street::class.java).putExtra("numberPlate",numberPlate))
                            }
                        }
                    }
                    else{
                        runOnUiThread {
                            cameraSource.stop()
                            if(boolean){
                                boolean = false
                                getTransactions(scannedValue)
                            }
                        }



                    //Don't forget to add this line printing value or finishing activity must run on main thread


                        //Toast.makeText(this@ScanClass, "valuejjj- $scannedValue", Toast.LENGTH_SHORT).show()
                       /* if(intent.getStringExtra("type").toString() == "receipt"){
                            queryReceiptNumber(scannedValue)
                        }else if(intent.getStringExtra("type").toString() == "liquor"){
                            if(scannedValue.length>4)
                                checkLiquor( scannedValue.split(":")[1].split(" ")[1])
                            else
                                checkLiquor( scannedValue)
                        }else{

                            if(scannedValue.length>4)
                            checkBusiness( scannedValue.split(":")[1].split(" ")[1])
                            else
                                checkBusiness( scannedValue)
                        }*/

                    }
                }else
                {
                 // runOnUiThread {   Toast.makeText(this@ScanClass, "value- else", Toast.LENGTH_SHORT).show() }

                }
            }
        })
    }

    private fun askForCameraPermission() {
        ActivityCompat.requestPermissions(
            this@ScanClass,
            arrayOf(android.Manifest.permission.CAMERA),
            requestCodeCameraPermission
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCodeCameraPermission && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupControls()
            } else {
                Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraSource.stop()
    }


    private fun getTransactions(ReceiptNo: String){
     //   Log.e("########",ReceiptNo)
       // Log.i("########MPESACODE",ReceiptNo.split(",")[0].replace(" ","").split(":")[1])

        val formData = listOf(
            "function" to "getTransactions",
            "keyword" to ReceiptNo.split(",")[0].replace(" ","").split(":")[1].toString(),//RGG1PKMB2
            "latitude" to getValue(this,"latitude").toString(),
            "longitude" to getValue(this,"longitude").toString(),
            "idNo" to getValue(this,"idNo").toString(),
            "username" to getValue(this,"username").toString(),
            "addressString" to getValue(this,"addressString").toString(),
            "deviceId" to getDeviceIdNumber(this)
        )
        executeRequest(formData, biller,object : CallBack{
            override fun onSuccess(result: String?) {

                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)

                if(response.success){
                    startActivity(Intent(this@ScanClass, TransactionsResults::class.java).putExtra("result",result))
                    finish()
                }else{
                    runOnUiThread {
                       Toast.makeText(this@ScanClass,response.message,Toast.LENGTH_LONG).show()
                        finish()
                    }
                }
            }

            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@ScanClass,result,Toast.LENGTH_LONG).show()

                }
            }


        })


    }
    private fun checkBusiness(businessID: String){

        val formData = listOf(
            "function" to "printTradePermit",
            "businessID" to businessID,
            "latitude" to getValue(this,"latitude").toString(),
            "longitude" to getValue(this,"longitude").toString(),
            "idNo" to getValue(this,"idNo").toString(),
            "username" to getValue(this,"username").toString(),
            "addressString" to getValue(this,"addressString").toString(),
            "deviceId" to getDeviceIdNumber(this)
        )
        executeRequest(formData, trade,object : CallBack{
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){
                    startActivity(Intent(this@ScanClass,Business::class.java).putExtra("result",result))
                }else{
                    runOnUiThread {   Toast.makeText(this@ScanClass, response.message,Toast.LENGTH_LONG) }
                }
            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@ScanClass,result,Toast.LENGTH_LONG).show()

                }
            }
        })

    }
    private fun checkLiquor(businessID: String){

        val formData = listOf(
            "function" to "printTradePermit",
            "businessID" to businessID,
            "latitude" to getValue(this,"latitude").toString(),
            "longitude" to getValue(this,"longitude").toString(),
            "idNo" to getValue(this,"idNo").toString(),
            "username" to getValue(this,"username").toString(),
            "addressString" to getValue(this,"addressString").toString(),
            "deviceId" to getDeviceIdNumber(this)
        )
        executeRequest(formData, liquor,object : CallBack{
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){
                    startActivity(Intent(this@ScanClass,Business::class.java).putExtra("result",result))
                }else{
                    runOnUiThread {   Toast.makeText(this@ScanClass, response.message,Toast.LENGTH_LONG) }
                }
            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@ScanClass,result,Toast.LENGTH_LONG).show()

                }
            }
        })

    }
}