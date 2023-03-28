package com.aw.forcement

import Json4Kotlin_Base
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aw.forcement.tabs.Home
import com.aw.passanger.api.*
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.message.*
import kotlinx.android.synthetic.main.progressbar.*
import java.util.ArrayList


class Login : AppCompatActivity() {

    private var intentFilter: IntentFilter? = null
    private var smsReceiver: SMSReceiver? = null
    private val arrayList = ArrayList<String>()
    private lateinit var sStreetName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        login.setOnClickListener {

            if(edUsername.text.equals("") || edPassword.text.equals("") ){
                Toast.makeText(this,"ALl fields require",Toast.LENGTH_LONG).show()
            }else{
                loadPage()
            }
        }


        tvForgot.setOnClickListener {
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
        }

        initBroadCast()

       /* getParkingStreet()*/

    }

 /*   private fun getParkingStreet(){

        executeGetRequest("parkingstreet",object : CallBack {
            override fun onSuccess(result: String?) {
                runOnUiThread {  progress_circular.visibility = View.GONE }
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.status==1){
                    arrayList.clear()
                    if(getValue(this@Login,"sStreetName")!!.isEmpty()){
                        arrayList.add("Select Options")
                    }else{
                        arrayList.add(getValue(this@Login,"sStreetName").toString())
                    }

                    for (item in response.data.parkingstreet){
                        if(getValue(this@Login,"sStreetName").toString()!=item.streetName)
                            arrayList.add(item.streetName)
                    }

                    runOnUiThread {  spinner(response) }
                }else{
                    runOnUiThread { Toast.makeText(this@Login, response.message, Toast.LENGTH_SHORT).show() }

                }

            }

        })

    }
    private fun spinner(response: Json4Kotlin_Base2){
        val adapters = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item,arrayList)
        adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spinner2.adapter = adapters
        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                if(p0!!.getItemAtPosition(p2).toString()!="Select Options"){
                    sStreetName = p0.getItemAtPosition(p2).toString()
                    save(this@Login,"sStreetName",sStreetName)

                    //get The id of the selected item
                    for (item in response.response_data.parkingstreet){
                        if(sStreetName==item.streetName){
                            save(this@Login,"sStreetNameId",item.id)
                            save(this@Login,"TownId",item.id)
                        }

                    }
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

    }
*/
    private fun forgetPassword() {
        save(this@Login,"pass", "" )
        progress_circular.visibility = View.VISIBLE
        val formData = listOf(
            "email" to edUsername.text.toString(),
            "TownId" to getValue(this,"TownId").toString()
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

        })
    }
    private fun loadPage(){
        login()
     /*   val mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // Checking GPS is enabled
        val mGPS = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if(!mGPS){
            runOnUiThread {   tvMessage.text = "Turn ON GPS" }
            Toast.makeText(this,"Turn ON GPS",Toast.LENGTH_SHORT).show()
        }else{
            login()

        }*/
    }

    private fun login (){

        progress_circular.visibility = View.VISIBLE
        val formData = listOf(
            "function" to "login",
            "username" to "demo@site.com" /*edUsername.text.toString()*/,
            "password" to "1234"/*edPassword.text.toString()*/,
            "TownId" to getValue(this,"TownId").toString()
        )
        executeRequest(formData, parking,object : CallBack{
            override fun onSuccess(result: String?) {
                runOnUiThread {  progress_circular.visibility = View.GONE }
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){
                    save(this@Login,"email", edUsername.text.toString() )
                    save(this@Login,"pass", edPassword.text.toString() )
                    save(this@Login,"id", response.data.userdata.id)
                    save(this@Login,"firstName", response.data.userdata.firstName)
                    save(this@Login,"lastName", response.data.userdata.lastName)
                    save(this@Login,"phone", response.data.userdata.phone)
                    save(this@Login,"idNo", response.data.userdata.idNo)
                    save(this@Login,"username",response.data.userdata.firstName+" "+response.data.userdata.lastName)
                   // startActivity(Intent(this@Login, Offstreet::class.java))
                  //  startActivity(Intent(this@Login, Offstreet::class.java))
                    startActivity(Intent(this@Login, Home::class.java))
                }else if(response.status==2){
                    save(this@Login,"email", edUsername.text.toString() )
                    save(this@Login,"pass", edPassword.text.toString() )
                    save(this@Login,"id", response.data.userdata.id)
                    save(this@Login,"firstName", response.data.userdata.firstName)
                    save(this@Login,"lastName", response.data.userdata.lastName)
                    save(this@Login,"phone", response.data.userdata.phone)
                    save(this@Login,"idNo", response.data.userdata.idNo)
                    runOnUiThread {   tvMessage.text = "" }
                    startActivity(Intent(this@Login,ChangePassword::class.java))
                }else{
                    runOnUiThread {   tvMessage.text = response.message }

                }

            }

        })
    }
    override fun onResume() {
        super.onResume()
        edUsername.setText(getValue(this@Login,"email").toString())
        edPassword.setText(getValue(this@Login,"pass").toString())
        registerReceiver(smsReceiver, intentFilter)

        Log.e("###", AppSignatureHashHelper(this@Login).appSignatures.toString())
       // edUsername.setText(AppSignatureHashHelper(this@Login).appSignatures.toString())
    }
    private fun initBroadCast() {
        intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        smsReceiver = SMSReceiver()
        smsReceiver?.setOTPListener(object : SMSReceiver.OTPReceiveListener {
            override fun onOTPReceived(otp: String?) {
                showToast("OTP Received: $otp")
                runOnUiThread { edPassword.setText(otp) }
                login()
            }
        })
    }
    override fun onPause() {
        super.onPause()
        unregisterReceiver(smsReceiver)
    }
    private fun showToast(msg: String?) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
    private fun initSmsListener() {
        val client = SmsRetriever.getClient(this)
        client.startSmsRetriever()
    }
    override fun onDestroy() {
        super.onDestroy()
        smsReceiver = null
    }


}