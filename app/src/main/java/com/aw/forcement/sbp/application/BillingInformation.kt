package com.aw.forcement.sbp.application

import Json4Kotlin_Base
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aw.forcement.R
import com.aw.passanger.api.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_billing_information.*
import kotlinx.android.synthetic.main.activity_billing_information.edPhone
import kotlinx.android.synthetic.main.activity_billing_information.tvAmount
import kotlinx.android.synthetic.main.activity_billing_information.tvSendPushDisabled
import kotlinx.android.synthetic.main.activity_billing_information.tv_message
import kotlinx.android.synthetic.main.message_box.view.*
import kotlinx.android.synthetic.main.payment_recieved.view.*
import kotlinx.android.synthetic.main.payment_unsuccesfull.view.*
import org.json.JSONObject
import java.util.concurrent.TimeUnit


class BillingInformation : AppCompatActivity() {

    lateinit var messageBoxView : View
    lateinit var messageBoxInstance: androidx.appcompat.app.AlertDialog // Declare as AlertDialog

    lateinit var messageBoxViewFailed : View
    lateinit var messageBoxInstanceFailed: androidx.appcompat.app.AlertDialog // Declare as AlertDialog

    lateinit var messageBoxViewPaid : View
    lateinit var messageBoxInstancePaid: androidx.appcompat.app.AlertDialog // Declare as AlertDialog
    var businessID =""
    var description =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_billing_information)

        // Initialize messageBoxView here
        messageBoxView = LayoutInflater.from(this).inflate(R.layout.message_box, null)
        messageBoxViewFailed = LayoutInflater.from(this).inflate(R.layout.payment_unsuccesfull, null)
        messageBoxViewPaid = LayoutInflater.from(this).inflate(R.layout.payment_recieved, null)

        tv_business_name.text = getValue(this,"business_name")
        tv_business_category.text = getValue(this,"business_category")
        tv_business_sub_category.text = getValue(this,"business_sub_category")
        tv_owners_id.text = getValue(this,"owner_id")
        tv_business_phone.text = getValue(this,"business_phone")
        tv_business_email.text = getValue(this,"business_email")
        tvAmount.text ="KES "+ getValue(this,"amount")
        tv_proceed.setOnClickListener { registerBusiness() }

    }
    private fun getJsonData(): String {

        val jsonObject = JSONObject()
        //Put the values as key-value pairs
        jsonObject.put("function", "registerBusiness")
        jsonObject.put("business_name", getValue(this, "business_name").toString())
        jsonObject.put("subCountyID", getValue(this, "subCountyID").toString())
        jsonObject.put("wardID", getValue(this, "wardID").toString())
        jsonObject.put("plotNumber", getValue(this, "plotNumber").toString())
        jsonObject.put("physicalAddress", getValue(this, "physicalAddress").toString())
        jsonObject.put("buildingName", getValue(this, "buildingName").toString())
        jsonObject.put("buildingOccupancy", getValue(this, "buildingOccupancy").toString())
        jsonObject.put("floorNo", getValue(this, "floorNo").toString())
        jsonObject.put("room_no", getValue(this, "room_no").toString())
        jsonObject.put("premise_size", getValue(this, "premise_size").toString())
        jsonObject.put("number_of_employees", getValue(this, "number_of_employees").toString())
        jsonObject.put("tonnage", getValue(this, "tonnage").toString())
        jsonObject.put("business_des", getValue(this, "business_des").toString())
        jsonObject.put("business_category", getValue(this, "business_category").toString())
        jsonObject.put("business_sub_category", getValue(this, "business_sub_category").toString())
        jsonObject.put("business_email", getValue(this, "business_email").toString())
        jsonObject.put("postal_address", getValue(this, "postal_address").toString())
        jsonObject.put("postal_code", getValue(this, "postal_code").toString())
        jsonObject.put("business_phone", getValue(this, "business_phone").toString())
        jsonObject.put("contact_person_names", getValue(this, "contact_person_names").toString())
        jsonObject.put("contact_person_idNo", getValue(this, "contact_person_idNo").toString())
        jsonObject.put("business_role", getValue(this, "business_role").toString())
        jsonObject.put("contact_person_phone", getValue(this, "contact_person_phone").toString())
        jsonObject.put("contact_person_email", getValue(this, "contact_person_email").toString())
        jsonObject.put("full_names", getValue(this, "full_names").toString())
        jsonObject.put("owner_id", getValue(this, "owner_id").toString())
        jsonObject.put("owner_phone", getValue(this, "owner_phone").toString())
        jsonObject.put("owner_email", getValue(this, "owner_email").toString())
        jsonObject.put("kra_pin", getValue(this, "kra_pin").toString())
        jsonObject.put("createdBy", getValue(this, "username").toString())
        jsonObject.put("createdByIDNo", getValue(this, "idNo").toString())

        return jsonObject.toString()
    }

    private fun registerBusiness(){
        executeJsonRequest(getJsonData(), trade,object: CallBack{
            override fun onSuccess(result: String?) {

                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){
                    businessID = response.data.business.businessID
                    description = response.data.business.buildingName

                    runOnUiThread {
                        showMessageBox()
                        generateBill()
                    }

                }else{

                }

            }

        })
    }

    private fun generateBill (){
        tv_message.text ="Generating bill please wait.."
        val formData = listOf(
            "function" to "generateBill2",
            "feeId" to getValue(this,"feeId").toString(),
            "amount" to getValue(this,"amount").toString(),
            "customer" to businessID,
            "zone" to getValue(this,"zone").toString(),
            "subCountyID" to getValue(this,"subCountyID").toString(),
            "subCountyName" to getValue(this,"subCountyName").toString(),
            "wardID" to getValue(this,"wardID").toString(),
            "wardName" to getValue(this,"wardName").toString(),
            "idNo" to getValue(this,"idNo").toString(),
            "phoneNumber" to getValue(this,"phoneNumber").toString(),
            "names" to getValue(this,"username").toString(),
            "customerPhoneNumber" to edPhone.text.toString(),
            "description" to description,
        )
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                    val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                    if(response.success){
                        runOnUiThread {
                            tv_message.text ="Bill generated success.."
                            tv_proceed.visibility = View.GONE
                            tvSendPushDisabled.visibility = View.VISIBLE
                        }

                        customerPayBillOnline(
                            response.data.billGenerated.billNo,
                            response.data.billGenerated.payBillNo,
                            response.data.billGenerated.amount,
                        )

                    }else{
                        Toast.makeText(this@BillingInformation,response.message, Toast.LENGTH_LONG).show()
                    }


                }else{
                    runOnUiThread {  Toast.makeText(this@BillingInformation,response.message, Toast.LENGTH_LONG).show()}

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
                        tv_proceed.visibility = View.VISIBLE
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

                            tv_proceed.visibility = View.VISIBLE
                            tvSendPushDisabled.visibility = View.GONE
                            tv_message.text ="Payment Received #${response.data.push.transaction_code} KES ${response.data.push.amount}"

                            //v_transaction: String,payer: String,amount: String, des: String,category:String
                            showMessageBoxPayment(
                                response.data.transaction.transaction_code,
                                response.data.transaction.names,
                                response.data.transaction.amount,
                                "${description} ${edPhone.text}",
                                businessID
                            )
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
                            tv_proceed.visibility = View.VISIBLE
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
           runOnUiThread {  generateBill() }
        }

    }

}