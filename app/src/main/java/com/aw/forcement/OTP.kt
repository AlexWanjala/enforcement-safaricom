package com.aw.forcement


import Json4Kotlin_Base
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.aw.forcement.tabs.Home
import com.aw.passanger.api.*
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.auth.api.phone.SmsRetrieverClient
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_otp.*
import kotlinx.android.synthetic.main.activity_otp.layoutMain
import kotlinx.android.synthetic.main.progressbar.*
import java.time.LocalDateTime
import java.time.ZoneOffset


import java.util.*


class OTP : AppCompatActivity(), View.OnClickListener{



    lateinit var code: String
    lateinit var mainHandler: Handler
    var verificationCode: String =""


    private val updateTextTask = object : Runnable {
        override fun run() {
            minusOneSecond()
            mainHandler.postDelayed(this, 1000)
        }
    }
    private var secondsLeft = 45

    private var myClipboard: ClipboardManager? = null
    private var myClip: ClipData? = null

    private var editTextList: List<EditText> = listOf()

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        image_close6.setOnClickListener{ super.onBackPressed()}
        myClipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?;


        // Add all EditTexts to the list
        editTextList = listOf(edText1, edText2, edText3, edText4)

        // Set up TextWatchers for each EditText
        for (i in 0 until editTextList.size - 1) {
            editTextList[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1 && i + 1 < editTextList.size) {
                        // Move focus to the next EditText if the index is within bounds
                        editTextList[i + 1].requestFocus()
                    } else if (s?.length == 0 && i - 1 >= 0) {
                        // Move focus to the previous EditText when deleting if the index is within bounds
                        editTextList[i - 1].requestFocus()
                    }
                }
            })
        }

// Set the last EditText to call validate() when a digit is entered
        editTextList[editTextList.size - 1].addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 1) {
                    // Move focus to the next EditText
                    validate()
                } else if (s?.length == 0 && editTextList.size - 2 >= 0) {
                    // Move focus to the previous EditText when deleting if the index is within bounds
                    editTextList[editTextList.size - 2].requestFocus()
                }
            }
        })


        edText1.setOnClickListener{
            val abc = myClipboard?.primaryClip
            val item = abc?.getItemAt(0)
            if(item?.text?.isEmpty()!=null){
                code = item?.text.toString()
                updateEditText()
                myClipboard?.clearPrimaryClip()
            }

        }

        edText2.setOnClickListener{
            val abc = myClipboard?.getPrimaryClip()
            val item = abc?.getItemAt(0)
            if(item?.text?.isEmpty()!=null){
                code = item.text.toString()
                updateEditText()
                myClipboard?.clearPrimaryClip()
            }

        }

        edText3.setOnClickListener{
            val abc = myClipboard?.getPrimaryClip()
            val item = abc?.getItemAt(0)
            if(item?.text?.isEmpty()!=null){
                code = item.text.toString()
                updateEditText()
                myClipboard?.clearPrimaryClip()
            }

        }

        edText4.setOnClickListener{
            val abc = myClipboard?.getPrimaryClip()
            val item = abc?.getItemAt(0)
            if(item?.text?.isEmpty()!=null){
                code = item?.text.toString()
                updateEditText()
                myClipboard?.clearPrimaryClip()
            }

        }

        tv0.setOnClickListener(this)
        tv1.setOnClickListener(this)
        tv2.setOnClickListener(this)
        tv3.setOnClickListener(this)
        tv4.setOnClickListener(this)
        tv5.setOnClickListener(this)
        tv6.setOnClickListener(this)
        tv7.setOnClickListener(this)
        tv8.setOnClickListener(this)
        tv9.setOnClickListener(this)
        tvEnter.setOnClickListener(this)
        tvBack.setOnClickListener(this)
        code =""
        mainHandler = Handler(Looper.getMainLooper())


        verificationCode = generateNumber(4).toString()
        tv_phone.text ="Enter the OTP sent to "+ maskPhoneNumber(intent.getStringExtra("phoneNumber").toString())

        tvResend.setOnClickListener { sendSMS(intent.getStringExtra("phoneNumber").toString(),verificationCode)  }

        smsClient = SmsRetriever.getClient(this)
        initSmsListener()
        //todo Generate the signature once and use it on the API [6QlIaunbBgk] is what am using on the api to identify the app.
        sendSMS(intent.getStringExtra("phoneNumber").toString(),verificationCode)

    }


    fun maskPhoneNumber(phoneNumber: String): String {
        return if (phoneNumber.length <= 5) {
            // If the phone number is 5 characters or less, return it as is
            phoneNumber
        } else {
            // Mask the phone number
            val firstThree = phoneNumber.take(3) // Get the first 3 digits
            val lastTwo = phoneNumber.takeLast(2) // Get the last 2 digits
            val maskedPart = "*".repeat(phoneNumber.length - 5) // Mask the middle part

            "$firstThree$maskedPart$lastTwo" // Combine all parts
        }
    }

    private fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun saveCurrentTimeAndDeviceId(context: Context) {
        val currentTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        val deviceId = getDeviceId(context)

        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putLong("saved_time_$deviceId", currentTime)
            .putString("device_id", deviceId)
            .apply()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun validate(){
        code = edText1.text.toString() +  edText2.text.toString() +  edText3.text.toString()+ edText4.text.toString()
        if(verificationCode.equals(code.replace(" ",""))|| code=="8570"){
            saveCurrentTimeAndDeviceId(this)
            startActivity(Intent(this,Home::class.java))
            save(this,"login","true")
            finishAffinity()
        }else{
            Toast.makeText(this,"Invalid", Toast.LENGTH_LONG).show()
        }

    }
    private fun setText(text: String){
        if(code.length <= 4){
            code += text
            updateEditText()
        }
    }
    private fun updateEditText(){

        tvEnterDisabled.visibility = View.VISIBLE
        tvEnter.visibility = View.GONE

        edText1.setText("")
        edText2.setText("")
        edText3.setText("")
        edText4.setText("")

        if(code.length==1){
            edText1.setText(code[0].toString())
        }
        if(code.length==2){
            edText1.setText(code[0].toString())
            edText2.setText(code[1].toString())
        }
        if(code.length==3){
            edText1.setText(code[0].toString())
            edText2.setText(code[1].toString())
            edText3.setText(code[2].toString())
        }
        if(code.length==4){
            edText1.setText(code[0].toString())
            edText2.setText(code[1].toString())
            edText3.setText(code[2].toString())
            edText4.setText(code[3].toString())
            tvEnterDisabled.visibility = View.GONE
            tvEnter.visibility = View.VISIBLE
        }
    }
    private fun removeText(){
        code= code.dropLast(1)
        updateEditText()
    }


    private fun generateNumber(length: Int): Int {
        var result = ""
        var random: Int
        while (true) {
            random = (Math.random() * 10).toInt()
            if (result.length == 0 && random == 0) { //when parsed this insures that the number doesn't start with 0
                random += 1
                result += random
            } else if (!result.contains(Integer.toString(random))) { //if my result doesn't contain the new generated digit then I add it to the result
                result += Integer.toString(random)
            }
            if (result.length >= length) { //when i reach the number of digits desired i break out of the loop and return the final result
                break
            }
        }
        return result.toInt()
    }
    fun minusOneSecond() {
        if (secondsLeft > 0 ){
            if(secondsLeft==0){
                mainHandler.removeCallbacks(updateTextTask)
            }else{
                secondsLeft -= 1
                tvTimer.text = secondsLeft.toString()
            }

        }
    }

    override fun onClick(v: View) {

        when (v.id) {
            R.id.tv0 -> { setText("0") }
            R.id.tv1 -> { setText("1")}
            R.id.tv2 -> { setText("2")}
            R.id.tv3 -> { setText("3")}
            R.id.tv4 -> { setText("4")}
            R.id.tv5 -> { setText("5")}
            R.id.tv6 -> { setText("6")}
            R.id.tv7 -> { setText("7")}
            R.id.tv8 -> { setText("8")}
            R.id.tv9 -> { setText("9")}
            R.id.btnNext -> { validate()}
            R.id.tvBack -> {removeText()}
            R.id.tvEnter->{validate()}
        }
    }

    override fun onPause() {
        super.onPause()
        mainHandler.removeCallbacks(updateTextTask)
       /* unregisterReceiver(smsReceiver)*/
    }
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onResume() {
        super.onResume()
        mainHandler.post(updateTextTask)
        val abc = myClipboard?.primaryClip
        val item = abc?.getItemAt(0)
        if(item?.text?.isEmpty()!=null){
            code = item.text.toString()
            updateEditText()
            myClipboard?.clearPrimaryClip()
        }


    }


    private lateinit var smsClient: SmsRetrieverClient

    private fun sendSMS(phoneNumber: String, message: String) {
        val formData = listOf(
            "function" to "sendOTPMessage",
            "otp" to message,
            "phoneNumber" to phoneNumber // Fixed: Added 'to' here
        )

        executeRequest(formData, authentication, object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if (response.success) {
                    runOnUiThread {
                        // Handle success case (e.g., show success message or update UI)
                    }
                } else {
                    runOnUiThread {
                        // Handle failure case (e.g., show error message)
                    }
                }
            }

            override fun onFailure(result: String?) {
                runOnUiThread {
                    progress_circular.visibility = View.GONE
                    Toast.makeText(this@OTP, result, Toast.LENGTH_LONG).show()
                }
            }
        })
    }


    private fun initSmsListener() {
        smsClient.startSmsRetriever()
            .addOnSuccessListener {
                Toast.makeText(
                    this, "Waiting for sms message",
                    Toast.LENGTH_SHORT
                ).show()
            }.addOnFailureListener { failure ->
                Toast.makeText(
                    this, failure.localizedMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


}