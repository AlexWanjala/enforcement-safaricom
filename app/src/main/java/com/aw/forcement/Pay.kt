package com.aw.forcement

import Json4Kotlin_Base
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.aw.passanger.api.CallBack
import com.aw.passanger.api.executePaysolRequest
import com.aw.passanger.api.executeRequest
import com.aw.passanger.api.paysol
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_pay.*
import java.util.concurrent.TimeUnit

class Pay : AppCompatActivity() {

    private val arrayList = ArrayList<String>()
    lateinit var shortcode: String
    lateinit var accNo: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay)

        tvSendPush.setOnClickListener {
            accNo = edAccount.text.toString()
            customerPayBillOnline()

        }
        getShortcodes()

    }

    private fun customerPayBillOnline(){
        progressBar1.visibility = View.VISIBLE
        val formData = listOf(
            "function" to "customerPayBillOnline",
            "payBillNumber" to "4087493",
            "amount" to edAmount.text.toString(),
            "accountReference" to edAccount.text.toString(),
            "transactionDesc" to edAccount.text.toString(),
            "phoneNumber" to edPhone.text.toString(),
            "token" to "im05WXYH2rwRruPjCICieOs8m4E8IoltnDEhyPUv6bnB9cU60gD48SnJPC6oh7EpsPaAUGC8wqIdtVVjGlWLxqFssshxMHxHjEQJ"
        )
        executePaysolRequest(formData, paysol,object : CallBack {
            override fun onSuccess(result: String?) {
                //  runOnUiThread {  progress_circular.visibility = View.GONE }
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    runOnUiThread {
                        progressBar1.visibility = View.GONE
                        checkPayment()
                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@Pay,response.message, Toast.LENGTH_LONG).show()}

                }

            }

        })

    }

    fun checkPayment(){
      runOnUiThread {   progressBarPayments.visibility = View.VISIBLE }
        val formData = listOf(
            "function" to "checkPayment",
            "accNo" to accNo,
            "token" to "im05WXYH2rwRruPjCICieOs8m4E8IoltnDEhyPUv6bnB9cU60gD48SnJPC6oh7EpsPaAUGC8wqIdtVVjGlWLxqFssshxMHxHjEQJ"
        )
        executePaysolRequest(formData, paysol,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)

                if(response.success){

                    runOnUiThread {
                        progressBarPayments.visibility = View.GONE
                        tv_message.text ="Payment received"

                    }
                    if(response.data.push.callback_returned=="PAID"){

                      runOnUiThread {

                          transactionCode.text = response.data.push.transaction_code
                          tvAmount.text = "KES "+response.data.push.amount
                          tvRef.text = response.data.push.ref
                          tvStatus.text = response.data.push.callback_returned; }


                    }else if(response.data.push.callback_returned=="PENDING"){
                        runOnUiThread { tv_message.text ="Waiting for payment.." }
                        TimeUnit.SECONDS.sleep(2L)
                        checkPayment()
                    }else{
                        runOnUiThread { tv_message.text = response.data.push.message}
                    }

                }
                else{
                    runOnUiThread { tv_message.text ="Waiting for payment.." }
                    TimeUnit.SECONDS.sleep(2L)
                    checkPayment()
                }

            }

        })
    }

    private fun getShortcodes (){

        progressBar1.visibility = View.VISIBLE
        val formData = listOf(
            "function" to "getShortcodes",
            "companyCode" to "ABSOLUTE",
            "keyword" to "",
            "token" to "im05WXYH2rwRruPjCICieOs8m4E8IoltnDEhyPUv6bnB9cU60gD48SnJPC6oh7EpsPaAUGC8wqIdtVVjGlWLxqFssshxMHxHjEQJ"
        )
        executePaysolRequest(formData, paysol,object : CallBack {
            override fun onSuccess(result: String?) {
                 runOnUiThread {  progressBar1.visibility = View.GONE }
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    runOnUiThread {
                        progressBar1.visibility = View.GONE

                        for(data in response.data.shortcodes){
                            arrayList.add(data.business_name)
                        }

                        //Spinner
                        val adapters = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item,arrayList)
                        adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        spinnerShortCode.adapter = adapters
                        spinnerShortCode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {
                                shortcode  = response.data.shortcodes[postion].short_code
                                Toast.makeText(this@Pay,shortcode,Toast.LENGTH_LONG).show()
                            }
                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }
                        }

                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@Pay,response.message, Toast.LENGTH_LONG).show()}

                }

            }

        })
    }
}