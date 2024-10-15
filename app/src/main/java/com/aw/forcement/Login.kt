package com.aw.forcement

import Const
import Json4Kotlin_Base
import SelectedRoles
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.aw.forcement.tabs.Home
import com.aw.passanger.api.*
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.message.*
import kotlinx.android.synthetic.main.progressbar.*
import java.util.ArrayList
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.AsyncTask
import android.os.Build
import android.telephony.TelephonyManager
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import com.aw.forcement.api.TextToSpeechUtil
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Tasks
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main_page.*
import java.time.LocalDateTime
import java.time.ZoneOffset

import java.util.*


class Login : AppCompatActivity() {

    private var intentFilter: IntentFilter? = null
    private var smsReceiver: SMSReceiver? = null
    private val arrayList = ArrayList<String>()
    private lateinit var sStreetName: String

    private val appUpdateManager by lazy { AppUpdateManagerFactory.create(this) }

    @RequiresApi(34)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        login.setOnClickListener {

            if(edUsername.text.equals("") || edPassword.text.equals("") ){
                Toast.makeText(this,"ALl fields require",Toast.LENGTH_LONG).show()
            }else{
               // Toast.makeText(this,"jjdjd",Toast.LENGTH_LONG).show()
                loadPage()
            }
        }

        TextToSpeechUtil.initialize(this@Login) {
            // Initialization is complete, you can start using text-to-speech
        }

        tvPrivacy.setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse("https://levetot.co.ke/privacy_policy.html")
            startActivity(openURL)
        }

    /*    tvForgot.setOnClickListener {
            if(edUsername.text.isEmpty()){
                Toast.makeText(this@Login,"Enter your email or username",Toast.LENGTH_LONG).show()
                edPassword.visibility = View.GONE
                tvPasswordLable.visibility = View.GONE
                login.visibility = View.GONE
                edUsername.hint = "Enter email or username"
            }else{

                initSmsListener()
                forgetPassword()

            }
        }*/

       // initBroadCast()

        forgotPassword.setOnClickListener {
            startActivity(Intent(this,ForgotPassword::class.java))

        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        updateApp()

       getIMEI()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getIMEI() {
        // Check for the READ_PHONE_STATE permission before accessing the IMEI
        if (checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted, proceed to get the IMEI
            val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val imei: String? = try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Use the getImei() method for Android 10 and above
                    telephonyManager.imei
                } else {
                    // Use the deprecated getDeviceId() method for older versions
                    telephonyManager.deviceId
                }
            } catch (e: SecurityException) {
                // Handle the case where accessing IMEI is not allowed
                e.printStackTrace()
                null
            }

            // Note: This may return null on devices where the IMEI is not available.
            val imeiResult = imei ?: "IMEI not available"
            // Handle the IMEI result as needed, e.g., display it, use it in your app, etc.
        } else {
            // Permission not granted, request it from the user
            requestPermissions(arrayOf(android.Manifest.permission.READ_PHONE_STATE), PERMISSION_REQUEST_READ_PHONE_STATE)
        }
    }


    // Define a constant for your permission request code
    companion object {
        private const val PERMISSION_REQUEST_READ_PHONE_STATE = 1
    }


    private fun forgetPassword() {
        save(this@Login,"pass", "" )
        progress_circular.visibility = View.VISIBLE
        val formData = listOf(
            "email" to edUsername.text.toString(),
            "TownId" to getValue(this,"TownId").toString(),
            "deviceId" to getDeviceIdNumber(this)
        )
        executeRequest(formData,"forgetpassword",object : CallBack{
            override fun onSuccess(result: String?) {
                runOnUiThread {  progress_circular.visibility = View.GONE }
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.status==1){
                    runOnUiThread {
                        tvMessage.text = response.message
                        edPassword.visibility = View.VISIBLE
                        tvPasswordLable.visibility = View.VISIBLE
                        login.visibility = View.VISIBLE
                        edPassword.text.clear()

                    }
                }else if(response.status==2){
                    runOnUiThread {   tvMessage.text = response.message }
                }else{
                    runOnUiThread {   tvMessage.text = response.message }

                }

            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    Toast.makeText(this@Login,result, Toast.LENGTH_LONG).show()
                }
            }

        })
    }
    @RequiresApi(34)
    private fun loadPage(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            runOnUiThread {
                tvMessage.text ="Please Allow permission to access location in app settings"
                tvMessage.setTextColor(Color.RED)
                login.text ="Allow permission"
                login.setOnClickListener {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    with(intent) {
                        data = Uri.fromParts("package",packageName, null)
                        addCategory(Intent.CATEGORY_DEFAULT)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                        addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                    }

                    startActivity(intent)
                    finish()
                }
            }

        }
        else{

            login()
            GetLocationTask(this).execute()

        /*    val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                login()
                GetLocationTask(this).execute()


            } else {
                showGPSDisabledAlertToUser()
            }*/

        }

    }
    fun showGPSDisabledAlertToUser() {
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
            .setCancelable(false)
            .setPositiveButton("Goto Settings Page To Enable GPS",
                DialogInterface.OnClickListener { dialog, id ->
                    val callGPSSettingIntent = Intent(
                        Settings.ACTION_LOCATION_SOURCE_SETTINGS
                    )
                    startActivity(callGPSSettingIntent)
                })
        alertDialogBuilder.setNegativeButton("Cancel",
            DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        val alert: AlertDialog = alertDialogBuilder.create()
        alert.show()
    }

    private fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    @RequiresApi(34)
    fun isMoreThanSixHoursPassed(context: Context): Boolean {
        val deviceId = getDeviceId(context)
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val savedTime = sharedPreferences.getLong("saved_time_$deviceId", 0)

        val currentTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        val timeDifference = currentTime - savedTime

        // 6 hours in seconds
        val sixHoursInSeconds = 6 * 60 * 60

        return timeDifference > sixHoursInSeconds
    }

    @RequiresApi(34)
    private fun login (){
        tvMessage.text =""
        val versionCode = BuildConfig.VERSION_CODE
        progress_circular.visibility = View.VISIBLE
        val formData = listOf(
            "function" to "login",
            "email" to edUsername.text.toString(),
            "password" to edPassword.text.toString(),
            "latitude" to "",
            "longitude" to "",
            "country" to "",
            "address" to "",
            "locality" to "",
            "versionCode" to versionCode.toString(),
            "deviceId" to "",
           // "IMEI" to emei.text.toString(),

        )
        executeRequest(formData, authentication,object : CallBack{
            override fun onSuccess(result: String?) {
                runOnUiThread {  progress_circular.visibility = View.GONE }

                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    Const.instance.setCategory(response.data.category)

                    save(this@Login,"idNo", edUsername.text.toString() )
                    save(this@Login,"email", edUsername.text.toString() )
                    save(this@Login,"pass", edPassword.text.toString() )
                    save(this@Login,"userid", response.data.user.id.toString())
                    save(this@Login,"zone", response.data.user.zone.toString())
                    save(this@Login,"phoneNumber", response.data.user.phoneNumber)
                    save(this@Login,"idNo", response.data.user.idNo)
                    save(this@Login,"username",response.data.user.names)
                    save(this@Login,"names",response.data.user.names)
                    save(this@Login,"subCountyID",response.data.user.subCountyID)
                    save(this@Login,"subCountyName",response.data.user.subCountyName)
                    save(this@Login,"wardID",response.data.user.wardID)
                    save(this@Login,"wardName",response.data.user.wardName)
                    save(this@Login,"target",response.data.user.target)
                    save(this@Login,"category",response.data.user.category)
                    save(this@Login,"permission",response.data.user.permission)
                    save(this@Login,"email",response.data.user.email)
                    save(this@Login,"id",response.data.user.id.toString())
                    save(this@Login,"gender",response.data.user.gender.toString())
                    save(this@Login,"clampingDuration",response.data.county.clampingDuration)
                    save(this@Login,"code",response.data.user.code)
                   // startActivity(Intent(this@Login, MainRoActivity::class.java))

                    if( response.data.county.loginOTP == "true"){
                        if (isMoreThanSixHoursPassed(this@Login)) {

                            startActivity(Intent(this@Login, OTP::class.java).putExtra("phoneNumber",response.data.user.phoneNumber))

                        } else {

                            startActivity(Intent(this@Login, Home::class.java))
                        }

                    }else{
                        startActivity(Intent(this@Login, Home::class.java))
                    }



                }else if(response.status==2){
                    save(this@Login,"names",response.data.user.names)
                    save(this@Login,"email", edUsername.text.toString() )
                    save(this@Login,"pass", edPassword.text.toString() )
                    save(this@Login,"userid", response.data.user.id.toString())
                    save(this@Login,"zone", response.data.user.zone.toString())
                    save(this@Login,"username", response.data.user.names)
                    save(this@Login,"phoneNumber", response.data.user.phoneNumber)
                    save(this@Login,"idNo", response.data.user.idNo)
                    save(this@Login,"username",response.data.user.email)
                    save(this@Login,"subCountyID",response.data.user.subCountyID)
                    save(this@Login,"subCountyName",response.data.user.subCountyName)
                    save(this@Login,"wardID",response.data.user.wardID)
                    save(this@Login,"target",response.data.user.target)
                    save(this@Login,"permission",response.data.user.permission)
                    save(this@Login,"id",response.data.user.id.toString())
                    save(this@Login,"gender",response.data.user.gender.toString())
                    save(this@Login,"idNo", edUsername.text.toString() )
                    save(this@Login,"clampingDuration",response.data.county.clampingDuration)

                    Const.instance.setCategory(response.data.category)

                    runOnUiThread {   tvMessage.text = "" }
                    startActivity(Intent(this@Login,ChangePassword::class.java))
                }else{
                    runOnUiThread {
                        tvMessage.text = response.message
                    }
                   // startActivity(Intent(this@Login, Home::class.java))
                }
            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    TextToSpeechUtil.speak(result.toString())
                    Toast.makeText(this@Login,result, Toast.LENGTH_LONG).show()
                    progress_circular.visibility = View.GONE

                }
            }
        })
    }
    override fun onResume() {
        super.onResume()

        edUsername.setText(getValue(this@Login,"idNo").toString())
        edPassword.setText(getValue(this@Login,"pass").toString())


        Log.e("###", AppSignatureHashHelper(this@Login).appSignatures.toString())
       // edUsername.setText(AppSignatureHashHelper(this@Login).appSignatures.toString())
       // getLocation()
        GetLocationTask(this).execute()
        val versionCode = BuildConfig.VERSION_CODE
        tvVersion.setText("Version: $versionCode")

         //MPESA	254729994994	RGH7QP68R5	KES 50.00  JAMES
         //MPESA	254729994994	RGH1QP6M4N	KES 20.00 Queenter

        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                // If the update is downloaded but not installed,
                // notify the user to complete the update.
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    popupSnackbarForCompleteUpdate()
                }
            }
    }

    // Displays the snackbar notification and call to action.
    private fun popupSnackbarForCompleteUpdate() {
        Snackbar.make(findViewById(R.id.login),"An update has just been downloaded.", Snackbar.LENGTH_INDEFINITE).apply {
            setAction("RESTART") { appUpdateManager.completeUpdate() }
            setActionTextColor(resources.getColor(R.color.red))
            show()
        }
    }

    private fun updateApp(){

        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                // Request the update.
                appUpdateManager.startUpdateFlowForResult(appUpdateInfo, this, AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE ).build(), 1)

                val listener = InstallStateUpdatedListener { state ->
                    // (Optional) Provide a download progress bar.
                    if (state.installStatus() == InstallStatus.DOWNLOADING) {
                        val bytesDownloaded = state.bytesDownloaded()
                        val totalBytesToDownload = state.totalBytesToDownload()
                        // Show update progress bar.
                        tvMessage.text = totalBytesToDownload.toString()
                    }
                    // Log state or install the update.
                }

// Before starting an update, register a listener for updates.
                appUpdateManager.registerListener(listener)

// Start an update.

// When status updates are no longer needed, unregister the listener.
                appUpdateManager.unregisterListener(listener)

            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode != RESULT_OK) {
          // Handle user's rejection or update failure
                //updateApp()
            }
        }
    }
    private fun initBroadCast() {
        intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        smsReceiver = SMSReceiver()
        smsReceiver?.setOTPListener(object : SMSReceiver.OTPReceiveListener {
            @RequiresApi(34)
            override fun onOTPReceived(otp: String?) {
                showToast("OTP Received: $otp")
                runOnUiThread { edPassword.setText(otp) }
                login()
            }
        })
    }

    private fun showToast(msg: String?) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }


    //Get location
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2
    class GetLocationTask(private val context: Context) : AsyncTask<Void, Void, Location?>() {

        @SuppressLint("MissingPermission")
        override fun doInBackground(vararg params: Void?): Location? {
            val mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            val locationTask = mFusedLocationClient.lastLocation
            return try {
                Tasks.await(locationTask)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        override fun onPostExecute(location: Location?) {
            super.onPostExecute(location)
            if (location != null) {
                val geocoder = Geocoder(context, Locale.getDefault())
                val list: List<Address> = geocoder.getFromLocation(location.latitude, location.longitude, 1) as List<Address>

                Log.e("###","Latitude\n${list[0].latitude}")
                Log.e("###","Longitude\n${list[0].longitude}")
                Log.e("###","Country Name\n${list[0].countryName}")
                Log.e("###", "Locality\n${list[0].locality}")
                Log.e("###", "Address\n${list[0].getAddressLine(0)}")

                save(context,"latitude",list[0].latitude.toString())
                save(context,"longitude",list[0].longitude.toString())
                save(context,"country",list[0].longitude.toString())
                save(context,"address",list[0].getAddressLine(0))
                save(context,"country",list[0].countryName)
                save(context,"locality",list[0].locality)

            }
        }
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
    /*@SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                GetLocationTask(this).execute()
            }
        }
    }
    */

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            permissionId -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    GetLocationTask(this).execute()
                }
            }
            PERMISSION_REQUEST_READ_PHONE_STATE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, now you can call getIMEI() again
                    getIMEI()
                } else {
                    // Permission denied, handle it accordingly (e.g., show a message to the user)
                    Toast.makeText(this, "Permission denied. Unable to get IMEI.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}