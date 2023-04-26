package com.aw.forcement.others

import Json4Kotlin_Base
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.aw.forcement.R
import com.aw.passanger.api.CallBack
import com.aw.passanger.api.biller
import com.aw.passanger.api.executeRequest
import com.aw.passanger.api.paysol
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_cess_payments.*
import kotlinx.android.synthetic.main.activity_cess_payments.tvAmount
import java.time.temporal.TemporalAmount
import java.util.concurrent.TimeUnit

class CessPayments : AppCompatActivity() {

    private val arrayList = ArrayList<String>()
    private val arrayList2 = ArrayList<String>()
    lateinit var amount: String
    lateinit var feeId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cess_payments)


        edQuantity.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                amountDisplay()

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        tvSendPayment.setOnClickListener {
            if(edQuantity.text.isEmpty()){
                Toast.makeText(this,"Quantity Required",Toast.LENGTH_LONG).show()
            }else{
                if(edPhoneNumber.text.isEmpty()){
                    Toast.makeText(this,"Phone Required",Toast.LENGTH_LONG).show()
                }else{
                    generateBill()
                }
            }
        }

        getIncomeTypes()
    }

    fun amountDisplay(){
        if(edQuantity.text.isNotEmpty()){
            tvUnits.text = "${edQuantity.text} x $amount"
            tvAmount.text = "KES "+  edQuantity.text.toString().toInt() * amount.toString().toInt()
        }else{
            tvUnits.text =""
            tvAmount.text ="KES 0"
        }
    }
    private fun generateBill (){
        tv_message.text ="Generating bill please wait.."
        val formData = listOf(
            "function" to "generateBill2",
            "feeId" to feeId.toString(),
            "amount" to (amount.toString().toInt() *edQuantity.text.toString().toInt()).toString(),
            "customer" to edPhoneNumber.text.toString()
        )
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                    if(response.success){
                        runOnUiThread {tv_message.text ="Bill generated success.." }

                        customerPayBillOnline(
                            response.data.billGenerated.billNo,
                            response.data.billGenerated.payBillNo,
                            response.data.billGenerated.amount,
                        )

                    }else{
                        Toast.makeText(this@CessPayments,response.message,Toast.LENGTH_LONG).show()
                    }


                }else{
                    runOnUiThread {  Toast.makeText(this@CessPayments,response.message, Toast.LENGTH_LONG).show()}

                }

            }

        })
    }
    private fun getIncomeTypes (){

        val formData = listOf(
            "function" to "getIncomeTypes",
            "incomeTypePrefix" to  intent.getStringExtra("incomeTypePrefix").toString()
        )
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    runOnUiThread {

                        for(data in response.data.incomeTypes){
                            arrayList.add(data.incomeTypeDescription)
                        }

                        //Spinner
                        val adapters = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item,arrayList)
                        adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        spinnerIncomeType.adapter = adapters
                        spinnerIncomeType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {
                                spinnerFeeAndCharges(response.data.incomeTypes[postion].incomeTypeId)
                            }
                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }
                        }

                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@CessPayments,response.message, Toast.LENGTH_LONG).show()}

                }

            }

        })
    }
    private fun spinnerFeeAndCharges (incomeTypeId: String){
        val formData = listOf(
            "function" to "getFeesAndCharges",
            "incomeTypeId" to incomeTypeId,
        )
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                runOnUiThread {  arrayList2.clear()
                    val adapters = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item,arrayList2)
                    adapters.clear()


                if(response.success){

                    runOnUiThread {

                        for(data in response.data.feesAndCharges){
                            arrayList2.add(data.feeDescription)
                        }

                        //Spinner
                        adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        spinnerFeeAndCharges.adapter = adapters
                        spinnerFeeAndCharges.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {
                              //  response.data.feesAndCharges[postion].feeId
                                amount = response.data.feesAndCharges[postion].unitFeeAmount
                                feeId = response.data.feesAndCharges[postion].feeId
                                amountDisplay()
                            }
                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }
                        }
                    }

                }
                else{
                    spinnerFeeAndCharges.adapter = null
                    Toast.makeText(this@CessPayments,response.message, Toast.LENGTH_LONG).show()

                }
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
            "phoneNumber" to edPhoneNumber.text.toString(),
            "token" to "im05WXYH2rwRruPjCICieOs8m4E8IoltnDEhyPUv6bnB9cU60gD48SnJPC6oh7EpsPaAUGC8wqIdtVVjGlWLxqFssshxMHxHjEQJ"
        )
        executeRequest(formData, paysol,object : CallBack {
            override fun onSuccess(result: String?) {
                //  runOnUiThread {  progress_circular.visibility = View.GONE }
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    runOnUiThread {
                       // progressBar1.visibility = View.GONE
                        checkPayment(accountReference)
                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@CessPayments,response.message, Toast.LENGTH_LONG).show()}

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
        executeRequest(formData, paysol,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)

                if(response.success){

                    if(response.data.push.callback_returned=="PAID"){

                        runOnUiThread {

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
                        runOnUiThread { tv_message.text = response.data.push.message}
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
}

/*
class CessPayments : AppCompatActivity() {

    private val arrayList = ArrayList<String>()
    private val arrayList2 = ArrayList<String>()
    lateinit var amount: String
    lateinit var feeId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cess_payments)

        edQuantity.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!edQuantity.text.isEmpty()){
                    tvUnits.text = "${edQuantity.text} x ${edAmount.text}"
                    tvAmount.text = "KES "+  edQuantity.text.toString().toInt() * edAmount.text.toString().toInt()
                }else{
                    tvUnits.text =""
                    tvAmount.text ="KES 0"
                }

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        tvSendPayment.setOnClickListener {
            if(edQuantity.text.isEmpty()){
                Toast.makeText(this,"Quantity Required",Toast.LENGTH_LONG).show()
            }else{
                if(edPhoneNumber.text.isEmpty()){
                    Toast.makeText(this,"Phone Required",Toast.LENGTH_LONG).show()
                }else{
                    generateBill()
                }
            }
        }

        getIncomeTypes()
    }
    private fun generateBill (){
        tv_message.text ="Generating bill please wait.."
        val formData = listOf(
            "function" to "generateBill",
            "feeId" to "1",
            "amount" to (edAmount.text.toString().toInt() *edQuantity.text.toString().toInt()).toString(),
            "customer" to edPhoneNumber.text.toString()
        )
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

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
                        Toast.makeText(this@CessPayments,response.message,Toast.LENGTH_LONG).show()
                    }


                }else{
                    runOnUiThread {  Toast.makeText(this@CessPayments,response.message, Toast.LENGTH_LONG).show()}

                }

            }

        })
    }
    private fun getIncomeTypes (){
        val formData = listOf(
            "function" to "getIncomeTypes",
        )
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    runOnUiThread {

                        for(data in response.data.incomeTypes){
                            arrayList.add(data.incomeTypeDescription)
                        }

                        //Spinner
                        val adapters = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item,arrayList)
                        adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        spinnerIncomeType.adapter = adapters
                        spinnerIncomeType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {
                                spinnerFeeAndCharges(response.data.incomeTypes[postion].incomeTypeId)
                            }
                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }
                        }

                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@CessPayments,response.message, Toast.LENGTH_LONG).show()}

                }

            }

        })
    }
    private fun spinnerFeeAndCharges (incomeTypeId: String){
        val formData = listOf(
            "function" to "getFeesAndCharges",
            "incomeTypeId" to incomeTypeId,
        )
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    runOnUiThread {
                        arrayList2.clear()
                        for(data in response.data.feesAndCharges){
                            arrayList2.add(data.feeDescription)
                        }

                        //Spinner
                        val adapters = ArrayAdapter<String>(applicationContext, R.layout.simple_spinner_dropdown_item,arrayList2)
                        adapters.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        spinnerFeeAndCharges.adapter = adapters
                        spinnerFeeAndCharges.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, postion: Int, p3: Long) {
                                //  response.data.feesAndCharges[postion].feeId
                                amount = response.data.feesAndCharges[postion].unitFeeAmount
                                feeId = response.data.feesAndCharges[postion].feeId
                            }
                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }
                        }

                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@CessPayments,response.message, Toast.LENGTH_LONG).show()}

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
            "phoneNumber" to edPhoneNumber.text.toString(),
            "token" to "im05WXYH2rwRruPjCICieOs8m4E8IoltnDEhyPUv6bnB9cU60gD48SnJPC6oh7EpsPaAUGC8wqIdtVVjGlWLxqFssshxMHxHjEQJ"
        )
        executeRequest(formData, paysol,object : CallBack {
            override fun onSuccess(result: String?) {
                //  runOnUiThread {  progress_circular.visibility = View.GONE }
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    runOnUiThread {
                        // progressBar1.visibility = View.GONE
                        checkPayment(accountReference)
                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@CessPayments,response.message, Toast.LENGTH_LONG).show()}

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
        executeRequest(formData, paysol,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)

                if(response.success){

                    if(response.data.push.callback_returned=="PAID"){

                        runOnUiThread {

                            tv_message.text ="Payment Received #${response.data.push.transaction_code} KES ${response.data.push.amount}"
*/
/*
                            transactionCode.text = response.data.push.transaction_code
                            tvAmount.text = "KES "+response.data.push.amount
                            tvRef.text = response.data.push.ref
                            tvStatus.text = response.data.push.callback_returned; *//*


                        }


                    }else if(response.data.push.callback_returned=="PENDING"){
                        runOnUiThread { tv_message.text ="Waiting for payment.." }
                        TimeUnit.SECONDS.sleep(2L)
                        checkPayment(accountReference)
                    }else{
                        runOnUiThread { tv_message.text = response.data.push.message}
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
}*/
