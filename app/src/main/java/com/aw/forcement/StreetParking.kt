package com.aw.forcement

import Json4Kotlin_Base
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.aw.passanger.api.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_street_parking.*
import kotlinx.android.synthetic.main.message_box.view.*
import kotlinx.android.synthetic.main.payment_recieved.view.*
import kotlinx.android.synthetic.main.payment_unsuccesfull.view.*
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class StreetParking : AppCompatActivity(){
    private val arrayList = ArrayList<String>()
    private val arrayList2 = ArrayList<String>()
    lateinit var category_code: String
    lateinit var duration : String
    lateinit var payer : String

    lateinit var messageBoxView : View
    lateinit var messageBoxInstance: androidx.appcompat.app.AlertDialog // Declare as AlertDialog

    lateinit var messageBoxViewFailed : View
    lateinit var messageBoxInstanceFailed: androidx.appcompat.app.AlertDialog // Declare as AlertDialog

    lateinit var messageBoxViewPaid : View
    lateinit var messageBoxInstancePaid: androidx.appcompat.app.AlertDialog // Declare as AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_street_parking)

        // Initialize messageBoxView here
        messageBoxView = LayoutInflater.from(this).inflate(R.layout.message_box, null)
        messageBoxViewFailed = LayoutInflater.from(this).inflate(R.layout.payment_unsuccesfull, null)
        messageBoxViewPaid = LayoutInflater.from(this).inflate(R.layout.payment_recieved, null)

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

        imageClose.setOnClickListener { finish() }

    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
    private fun getCategory(){
        progressBar1.visibility = View.VISIBLE
        val formData = listOf(
            "function" to "getZones",
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

                                category_code = response.data.categories[postion].category
                                getParkingCharges()
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
                                runOnUiThread { tvDuration.text = duration }
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

        })


    }
    private fun getParkingCharges(){
        tv_message.text =""
        val formData = listOf(
            "function" to "getParkingCharges",
            "category" to category_code,
            "duration" to duration,
            "zone" to getValue(this,"zone").toString(),
        )
        executeRequest(formData, parking,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){
                    runOnUiThread { tvAmount.text = "KES "+response.data.feesAndCharge.unitFeeAmount }
                }
                else{
                   runOnUiThread {
                       tv_message.text = response.message
                   }
                }
            }
        })
    }
    private fun matatuPayment(){
        showMessageBox()
        tv_message.text ="Generating bill please wait..$duration $category_code"
        (messageBoxView as View?)!!.tv_message.text ="Generating bill please wait..$duration $category_code"
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
            "customerPhoneNumber" to edPhone.text.toString(),
        )
        executeRequest(formData, parking,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){
                    runOnUiThread {
                        tv_message.text ="Bill generated success.."
                        (messageBoxView as View?)!!.tv_message.text ="Bill generated success.."
                    }

                    customerPayBillOnline(
                        response.data.billGenerated.billNo,
                        response.data.billGenerated.payBillNo,
                        response.data.billGenerated.amount
                    )

                }
                else{
                    runOnUiThread {
                        tv_message.text = response.message
                        (messageBoxView as View?)!!.tv_message.text =response.message
                        tvSendPush.visibility = View.VISIBLE
                        tvSendPushDisabled.visibility = View.GONE
                        messageBoxInstance.dismiss()
                    }
                }

            }

        })


    }
    private fun customerPayBillOnline(accountReference: String, payBillNumber: String, amount: String){
        // progressBar1.visibility = View.VISIBLE
        runOnUiThread {
            tv_message.text ="Sending Payment Request.."
            (messageBoxView as View?)!!.tv_message.text ="Sending Payment Request.."

        }
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

                            messageBoxInstance.dismiss()

                            tvSendPush.visibility = View.VISIBLE
                            tvSendPushDisabled.visibility = View.GONE
                            tv_message.text ="Payment Received #${response.data.push.transaction_code} KES ${response.data.push.amount}"

                            //v_transaction: String,payer: String,amount: String, des: String,category:String
                            showMessageBoxPayment(
                                response.data.transaction.transaction_code,
                                response.data.transaction.names,
                                response.data.transaction.amount,
                                "${category_code} ${edPhone.text}",
                                duration
                            )
/*
                            transactionCode.text = response.data.push.transaction_code
                            tvAmount.text = "KES "+response.data.push.amount
                            tvRef.text = response.data.push.ref
                            tvStatus.text = response.data.push.callback_returned; */

                        }


                    }else if(response.data.push.callback_returned=="PENDING"){
                        runOnUiThread {
                            tv_message.text ="Waiting for payment.."
                            (messageBoxView as View?)!!.tv_message.text ="Waiting for payment.."

                        }
                        TimeUnit.SECONDS.sleep(2L)
                        checkPayment(accountReference)
                    }else{
                        runOnUiThread {
                            messageBoxInstance.dismiss()
                            tv_message.text = response.data.push.message
                            tvSendPush.visibility = View.VISIBLE
                            tvSendPushDisabled.visibility = View.GONE
                            showMessageBoxPaymentFail(response.data.push.message)

                        }
                    }

                }
                else{
                    runOnUiThread { tv_message.text ="Waiting for payment.." }
                    TimeUnit.SECONDS.sleep(2L)
                    checkPayment(accountReference)
                }

            }

        })
    }
    private fun showMessageBox(){
        // Check if messageBoxView has a parent
        if (messageBoxView.parent != null) {
       // Remove messageBoxView from its parent
            (messageBoxView.parent as ViewGroup).removeView(messageBoxView)
        }
        val messageBoxBuilder = androidx.appcompat.app.AlertDialog.Builder(this).setView(messageBoxView as View?)
        messageBoxInstance = messageBoxBuilder.show()
    }
    private fun showMessageBoxPayment(transaction: String,payer: String,amount: String, des: String,category:String){

        // Check if messageBoxView has a parent
        if (messageBoxViewPaid.parent != null) {
            // Remove messageBoxView from its parent
            (messageBoxViewPaid.parent as ViewGroup).removeView(messageBoxViewPaid)
        }

        val messageBoxBuilder = androidx.appcompat.app.AlertDialog.Builder(this).setView(
            messageBoxViewPaid as View?
        )
        messageBoxInstancePaid = messageBoxBuilder.show()

        messageBoxViewPaid.tv_transaction.text = transaction
        messageBoxViewPaid.tv_payer.text = payer
        messageBoxViewPaid.tv_amount.text = amount
        messageBoxViewPaid.tv_des.text = des
        messageBoxViewPaid.tv_category.text = category
        messageBoxViewPaid.okay.setOnClickListener { messageBoxInstancePaid.dismiss() }

    }
    private fun showMessageBoxPaymentFail(message: String) {

        // Check if messageBoxView has a parent
        if (messageBoxViewFailed.parent != null) {
            // Remove messageBoxView from its parent
            (messageBoxViewFailed.parent as ViewGroup).removeView(messageBoxViewFailed)
        }

        val messageBoxBuilder = androidx.appcompat.app.AlertDialog.Builder(this).setView(
            messageBoxViewFailed as View?
        )
        messageBoxInstanceFailed = messageBoxBuilder.show()

        if (message.contains("invalid")) {
            (messageBoxViewFailed as View?)!!.tv_title.text = "Wrong PIN"
            (messageBoxViewFailed as View?)!!.tv_message_unpaid.text = "The Payer typed an incorrect pin. Send the request again to retry."
            (messageBoxViewFailed as View?)?.imageIcon?.setImageResource(R.drawable.wrong_pin)
        } else
            if (message.contains("insufficient")) {
                (messageBoxViewFailed as View?)!!.tv_title.text = "Insufficient Funds"
                (messageBoxViewFailed as View?)!!.tv_message_unpaid.text =
                    "Payer needs more money. Tell them to top up MPESA to pay."
                (messageBoxViewFailed as View?)?.imageIcon?.setImageResource(R.drawable.insufficient)
            } else
                if (message.contains("cancel")) {
                    (messageBoxViewFailed as View?)!!.tv_title.text = "Request Canceled !"
                    (messageBoxViewFailed as View?)!!.tv_message_unpaid.text =
                        "Payer canceled payment. Click Resend request to try again."
                    (messageBoxViewFailed as View?)?.imageIcon?.setImageResource(R.drawable.explamation)
                } else
                    if (message.contains("timeout")) {
                        (messageBoxViewFailed as View?)!!.tv_title.text = "Phone unreachable"
                        (messageBoxViewFailed as View?)!!.tv_message_unpaid.text =
                            "Phone unreachable. Ask payer to switch On their phone"
                        (messageBoxViewFailed as View?)?.imageIcon?.setImageResource(R.drawable.phone_unreachable)
                    } else {
                        (messageBoxViewFailed as View?)!!.tv_title.text = "No Payment"
                        (messageBoxViewFailed as View?)!!.tv_message_unpaid.text = message
                        (messageBoxViewFailed as View?)?.imageIcon?.setImageResource(R.drawable.explamation)
                    }

        (messageBoxViewFailed as View?)!!.tv_close.setOnClickListener { messageBoxInstanceFailed.dismiss()  }
        (messageBoxViewFailed as View?)!!.resend.setOnClickListener {
            messageBoxInstanceFailed.dismiss()
            matatuPayment()
        }

    }
}
