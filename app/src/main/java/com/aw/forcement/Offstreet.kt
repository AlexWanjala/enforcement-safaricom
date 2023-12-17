package com.aw.forcement

import Json4Kotlin_Base
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Camera
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import com.aw.forcement.ocr.OcrDetectorProcessor
import com.aw.forcement.ocr.OcrGraphic
import com.aw.forcement.ocr.camera.CameraSource
import com.aw.forcement.ocr.camera.CameraSourcePreview
import com.aw.forcement.ocr.camera.GraphicOverlay
import com.aw.forcement.others.CessPayments
import com.aw.forcement.others.Street
import com.aw.passanger.api.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson

import kotlinx.android.synthetic.main.activity_offstreet.*
import kotlinx.android.synthetic.main.activity_offstreet.edPhone
import kotlinx.android.synthetic.main.activity_offstreet.edPlate
import kotlinx.android.synthetic.main.activity_offstreet.progressBar1
import kotlinx.android.synthetic.main.activity_offstreet.tvSendPush
import kotlinx.android.synthetic.main.activity_offstreet.tv_message
import kotlinx.android.synthetic.main.activity_street.*
import java.io.IOException
import java.lang.NullPointerException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class Offstreet : AppCompatActivity(){
    private val arrayList = ArrayList<String>()
    private val arrayList2 = ArrayList<String>()
    lateinit var category_code: String
    lateinit var duration : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offstreet)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        imageReports.setOnClickListener { startActivity(Intent(this,Transactions::class.java)) }
        imageScan.setOnClickListener { startActivity(Intent(this,Street::class.java)) }

        getCategory()
        tvSendPush.setOnClickListener {
            if(edPlate.text.isEmpty()){
                Toast.makeText(this,"Please input Number Plate",Toast.LENGTH_LONG).show()
            }else{
                if(edPhone.text.isEmpty()){
                    Toast.makeText(this,"Please input Number Plate",Toast.LENGTH_LONG).show()
                }else{
                    tvSendPush.visibility = View.GONE
                    tvSendPushDisabled.visibility = View.VISIBLE
                    matatuPayment()
                }
            }
        }

        imagePay.setOnClickListener { startActivity(Intent(this,CessPayments::class.java)) }
        initOCR()
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
    private fun getCategory(){
        progressBar1.visibility = View.VISIBLE
        val formData = listOf(
            "function" to "getZones",
            "deviceId" to getDeviceIdNumber(this)
        )
        executeRequest(formData, parking,object : CallBack {
            override fun onSuccess(result: String?) {
                runOnUiThread {  progressBar1.visibility = View.GONE }
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){
                     //spinner_zone
                    runOnUiThread {
                        progressBar1.visibility = View.GONE

                        for(data in response.data.categories){
                            arrayList.add(data.category)
                        }

                        for(data in response.data.durations){
                            arrayList2.add(data.duration)
                        }

                        //Spinner
                        val adapters = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item,arrayList)
                        adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        spinner_search2.adapter = adapters
                        spinner_search2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {

                                category_code = response.data.categories[postion].category;
                            }
                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }
                        }

                        //Zones
                        val adaptersZone = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item,arrayList2)
                        adaptersZone.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        spinner_zone.adapter = adaptersZone
                        spinner_zone.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {

                                duration = response.data.durations[postion].duration
                            }
                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }
                        }
                    }

                }else{
                    runOnUiThread {
                      //  Toast.makeText(this@Pay,response.message, Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@Offstreet,result, Toast.LENGTH_LONG).show()
                }

            }
        })
    }
    private fun matatuPayment(){
        tv_message.text ="Generating bill please wait..$duration $category_code"
        val formData = listOf(
            "function" to "matatuPayment",
            "numberPlate" to edPlate.text.toString(),
            "category" to category_code,
            "duration" to duration,
            "zone" to getValue(this,"zone").toString(),
            "names" to getValue(this,"names").toString(),
            "phoneNumber" to getValue(this,"phoneNumber").toString(),
            "idNo" to getValue(this,"idNo").toString(),
            "username" to getValue(this,"username").toString(),
            "subCountyID" to getValue(this,"subCountyID").toString(),
            "subCountyName" to getValue(this,"subCountyName").toString(),
            "wardID" to getValue(this,"wardID").toString(),
            "wardName" to getValue(this,"wardName").toString(),
            "deviceId" to getDeviceIdNumber(this)
        )
        executeRequest(formData, parking,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){
                    runOnUiThread {tv_message.text ="Bill generated success.." }

                    customerPayBillOnline(
                        response.data.billGenerated.billNo,
                        response.data.billGenerated.payBillNo,
                        response.data.billGenerated.amount
                    )

                }
                else{
                    runOnUiThread {
                        tv_message.text = response.message
                    }
                }
            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    tv_message.text = result
                    Toast.makeText(this@Offstreet,result, Toast.LENGTH_LONG).show()
                }

            }
        })

    }
    private fun customerPayBillOnline(accountReference: String, payBillNumber: String, amount: String){
        // progressBar1.visibility = View.VISIBLE
        runOnUiThread {   tv_message.text ="Sending Payment Request.." }
        val formData = listOf(
            "function" to "customerPayBillOnline",
            "payBillNumber" to payBillNumber,
            "amount" to amount,
            "accountReference" to accountReference,
            "transactionDesc" to accountReference,
            "phoneNumber" to edPhone.text.toString(),
            "token" to "im05WXYH2rwRruPjCICieOs8m4E8IoltnDEhyPUv6bnB9cU60gD48SnJPC6oh7EpsPaAUGC8wqIdtVVjGlWLxqFssshxMHxHjEQJ"
        )
        executePaysolRequest(formData, paysol,object : CallBack {
            override fun onSuccess(result: String?) {
                //  runOnUiThread {  progress_circular.visibility = View.GONE }
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    runOnUiThread {
                        // progressBar1.visibility = View.GONE
                        checkPayment(accountReference)
                    }

                }else{
                    runOnUiThread {
                        tvSendPush.visibility = View.VISIBLE
                        tvSendPushDisabled.visibility = View.GONE
                        tv_message.text = response.message
                    }

                }

            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    tv_message.text = result
                    Toast.makeText(this@Offstreet,result, Toast.LENGTH_LONG).show()
                }

            }

        })

    }
    fun checkPayment(accountReference: String){
        //  runOnUiThread {   progressBarPayments.visibility = View.VISIBLE }
        val formData = listOf(
            "function" to "checkPayment",
            "accNo" to accountReference,
            "token" to "im05WXYH2rwRruPjCICieOs8m4E8IoltnDEhyPUv6bnB9cU60gD48SnJPC6oh7EpsPaAUGC8wqIdtVVjGlWLxqFssshxMHxHjEQJ"
        )
        executePaysolRequest(formData, paysol,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)

                if(response.success){

                    if(response.data.push.callback_returned=="PAID"){

                        runOnUiThread {

                            tvSendPush.visibility = View.VISIBLE
                            tvSendPushDisabled.visibility = View.GONE

                            tv_message.text ="Payment Received #${response.data.push.transaction_code} KES ${response.data.push.amount}"
/*
                            transactionCode.text = response.data.push.transaction_code
                            tvAmount.text = "KES "+response.data.push.amount
                            tvRef.text = response.data.push.ref
                            tvStatus.text = response.data.push.callback_returned; */

                        }


                    }else if(response.data.push.callback_returned=="PENDING"){
                        runOnUiThread { tv_message.text ="Waiting for payment.." }
                        TimeUnit.SECONDS.sleep(2L)
                        checkPayment(accountReference)
                    }else{
                        runOnUiThread {
                            tv_message.text = response.data.push.message
                            tvSendPush.visibility = View.VISIBLE
                            tvSendPushDisabled.visibility = View.GONE

                        }
                    }

                }
                else{
                    runOnUiThread { tv_message.text ="Waiting for payment.." }
                    TimeUnit.SECONDS.sleep(2L)
                    checkPayment(accountReference)
                }

            }

            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@Offstreet,result, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    //OCR STARTS HERE
    private fun initOCR(){

        try {

            preview = findViewById<View>(R.id.preview) as CameraSourcePreview
            graphicOverlay = findViewById<View>(R.id.graphicOverlay) as GraphicOverlay<OcrGraphic?>

            // Set good defaults for capturing text.
            val autoFocus = true
            val useFlash = false

            // Check for the camera permission before accessing the camera.  If the
            // permission is not granted yet, request permission.
            val rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            if (rc == PackageManager.PERMISSION_GRANTED) {
                createCameraSource(autoFocus, useFlash)
            } else {
                requestCameraPermission()
            }
            gestureDetector = GestureDetector(this, CaptureGestureListener())
            scaleGestureDetector = ScaleGestureDetector(this, ScaleListener())
            Snackbar.make(
                graphicOverlay!!, "Tap to Speak. Pinch/Stretch to zoom",
                Snackbar.LENGTH_LONG
            )
                .show()

            // Set up the Text To Speech engine.
            val listener = TextToSpeech.OnInitListener { status ->
                if (status == TextToSpeech.SUCCESS) {
                    Log.d("OnInitListener", "Text to speech engine started successfully.")
                    tts!!.language = Locale.US
                } else {
                    Log.d("OnInitListener", "Error starting the text to speech engine.")
                }
            }
            tts = TextToSpeech(this.applicationContext, listener)
        } catch (ex: NullPointerException) {
        }

    }
    private var cameraSource: CameraSource? = null
    private var preview: CameraSourcePreview? = null
    private var graphicOverlay: GraphicOverlay<OcrGraphic?>? = null

    // Helper objects for detecting taps and pinches.
    private var scaleGestureDetector: ScaleGestureDetector? = null
    private var gestureDetector: GestureDetector? = null

    // A TextToSpeech engine for speaking a String value.
    private var tts: TextToSpeech? = null
    private fun goToSettings() {
        val myAppSettings = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse(
                "package:$packageName"
            )
        )
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT)
        myAppSettings.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(myAppSettings)
    }

    /**
     * Initializes the UI and creates the detector pipeline.
     */
    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private fun requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission")
        val permissions = arrayOf(Manifest.permission.CAMERA)
        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CAMERA
            )
        ) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM)
            return
        }
        val thisActivity: Activity = this
        val listener = View.OnClickListener {
            ActivityCompat.requestPermissions(
                thisActivity, permissions,
                RC_HANDLE_CAMERA_PERM
            )
        }
        Snackbar.make(
            graphicOverlay!!, "permission_camera_rationale",
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction("ok", listener)
            .show()
    }
    override fun onTouchEvent(e: MotionEvent): Boolean {
        val b = scaleGestureDetector!!.onTouchEvent(e)
        val c = gestureDetector!!.onTouchEvent(e)
        return b || c || super.onTouchEvent(e)
    }
    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the ocr detector to detect small text samples
     * at long distances.
     *
     * Suppressing InlinedApi since there is a check that the minimum version is met before using
     * the constant.
     */
    @SuppressLint("InlinedApi")
    private fun createCameraSource(autoFocus: Boolean, useFlash: Boolean) {
        val context = applicationContext

        // A text recognizer is created to find text.  An associated multi-processor instance
        // is set to receive the text recognition results, track the text, and maintain
        // graphics for each text block on screen.  The factory is used by the multi-processor to
        // create a separate tracker instance for each text block.
        val textRecognizer = TextRecognizer.Builder(context).build()
        textRecognizer.setProcessor(OcrDetectorProcessor(graphicOverlay!!))
        if (!textRecognizer.isOperational) {
            // Note: The first time that an app using a Vision API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any text,
            // barcodes, or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
            Log.w(TAG, "Detector dependencies are not yet available.")

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            val lowstorageFilter = IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW)
            val hasLowStorage = registerReceiver(null, lowstorageFilter) != null
            if (hasLowStorage) {
                Toast.makeText(this, "low_storage_error", Toast.LENGTH_LONG).show()
                Log.w(TAG, "low_storage_error")
            }
        }

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the text recognizer to detect small pieces of text.
        cameraSource = CameraSource.Builder(applicationContext, textRecognizer)
            .setFacing(CameraSource.CAMERA_FACING_BACK)
            .setRequestedPreviewSize(1280, 1024)
            .setRequestedFps(2.0f)
            .setFlashMode(if (useFlash) Camera.Parameters.FLASH_MODE_TORCH else null)
            .setFocusMode(if (autoFocus) Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO else null)
            .build()
    }

    /**
     * Restarts the camera.
     */
    override fun onResume() {
        super.onResume()
        startCameraSource()
    }

    /**
     * Stops the camera.
     */
    override fun onPause() {
        super.onPause()
        if (preview != null) {
            preview!!.stop()
        }
    }
    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    override fun onDestroy() {
        super.onDestroy()
        /* if (preview != null) {
             preview!!.release()
         }*/
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on [.requestPermissions].
     *
     *
     * **Note:** It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     *
     *
     * @param requestCode  The request code passed in [.requestPermissions].
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     * which is either [PackageManager.PERMISSION_GRANTED]
     * or [PackageManager.PERMISSION_DENIED]. Never null.
     * @see .requestPermissions
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: $requestCode")
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }
        if (grantResults.size != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source")
            // we have permission, so create the camerasource
            val autoFocus = intent.getBooleanExtra(AutoFocus, true)
            val useFlash = intent.getBooleanExtra(UseFlash, false)
            createCameraSource(autoFocus, useFlash)
            return
        }
        Log.e(
            TAG, "Permission not granted: results len = " + grantResults.size +
                    " Result code = " + if (grantResults.size > 0) grantResults[0] else "(empty)"
        )
        val listener = DialogInterface.OnClickListener { dialog, id -> finish() }
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Multitracker sample")
            .setMessage("no_camera_permission")
            .setPositiveButton("ok", listener)
            .show()
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    @Throws(SecurityException::class)
    private fun startCameraSource() {
        // check that the device has play services available.
        val code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
            applicationContext
        )
        if (code != ConnectionResult.SUCCESS) {
            val dlg = GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS)
            dlg!!.show()
        }
        if (cameraSource != null) {
            try {
                preview!!.start(cameraSource, graphicOverlay)
            } catch (e: IOException) {
                Log.e(TAG, "Unable to start camera source.", e)
                cameraSource!!.release()
                cameraSource = null
            }
        }
    }

    /**
     * onTap is called to speak the tapped TextBlock, if any, out loud.
     *
     * @param rawX - the raw position of the tap
     * @param rawY - the raw position of the tap.
     * @return true if the tap was on a TextBlock
     */
    private fun onTap(rawX: Float, rawY: Float): Boolean {
        val graphic = graphicOverlay!!.getGraphicAtLocation(rawX, rawY)
        var text: TextBlock? = null
        if (graphic != null) {
            text = graphic.textBlock
            if (text != null && text.value != null) {
                Log.d(TAG, "text data is being spoken! " + text.value)
                // Speak the string.
                tts!!.speak(text.value, TextToSpeech.QUEUE_ADD, null, "DEFAULT")
                Toast.makeText(this, text.value.toString(), Toast.LENGTH_SHORT).show()
                runOnUiThread {

                    val name:String =  text.value.toString().replace("","")
                    edPlate.setText(name)


                }
            } else {
                Log.d(TAG, "text data is null")
            }
        } else {
            Log.d(TAG, "no text detected")
        }
        return text != null
    }

    private inner class CaptureGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            return onTap(e.rawX, e.rawY) || super.onSingleTapConfirmed(e)
        }
    }

    private inner class ScaleListener : ScaleGestureDetector.OnScaleGestureListener {
        /**
         * Responds to scaling events for a gesture in progress.
         * Reported by pointer motion.
         *
         * @param detector The detector reporting the event - use this to
         * retrieve extended info about event state.
         * @return Whether or not the detector should consider this event
         * as handled. If an event was not handled, the detector
         * will continue to accumulate movement until an event is
         * handled. This can be useful if an application, for example,
         * only wants to update scaling factors if the change is
         * greater than 0.01.
         */
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            return false
        }

        /**
         * Responds to the beginning of a scaling gesture. Reported by
         * new pointers going down.
         *
         * @param detector The detector reporting the event - use this to
         * retrieve extended info about event state.
         * @return Whether or not the detector should continue recognizing
         * this gesture. For example, if a gesture is beginning
         * with a focal point outside of a region where it makes
         * sense, onScaleBegin() may return false to ignore the
         * rest of the gesture.
         */
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            return true
        }

        /**
         * Responds to the end of a scale gesture. Reported by existing
         * pointers going up.
         *
         *
         * Once a scale has ended, [ScaleGestureDetector.getFocusX]
         * and [ScaleGestureDetector.getFocusY] will return focal point
         * of the pointers remaining on the screen.
         *
         * @param detector The detector reporting the event - use this to
         * retrieve extended info about event state.
         */
        override fun onScaleEnd(detector: ScaleGestureDetector) {
            if (cameraSource != null) {
                cameraSource!!.doZoom(detector.scaleFactor)
            }
        }
    }

    companion object {
        private const val TAG = "Street"

        // Intent request code to handle updating play services if needed.
        private const val RC_HANDLE_GMS = 9001

        // Permission request codes need to be < 256
        private const val RC_HANDLE_CAMERA_PERM = 2

        // Constants used to pass extra data in the intent
        const val AutoFocus = "AutoFocus"
        const val UseFlash = "UseFlash"
        const val TextBlockObject = "String"
        fun hideKeyboard(activity: Activity) {
            val imm = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            //Find the currently focused view, so we can grab the correct window token from it.
            var view = activity.currentFocus
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

}
