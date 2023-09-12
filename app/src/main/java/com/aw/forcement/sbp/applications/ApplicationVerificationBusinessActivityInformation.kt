package com.aw.forcement.sbp.applications

import Const
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.aw.forcement.R
import kotlinx.android.synthetic.main.activity_application_verification_business_details.*
import kotlinx.android.synthetic.main.activity_application_verification_business_information.*
import kotlinx.android.synthetic.main.update.view.*

class ApplicationVerificationBusinessActivityInformation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_application_verification_business_information)

    }

    private fun displayValues(){
        tv_premise.text = Const.instance.getBusiness().premiseSize
        tv_business_des.text = Const.instance.getBusiness().businessDes

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showMessageBox(title: String,message: String){

        val messageBoxView = LayoutInflater.from(this).inflate(R.layout.update, null)
        val messageBoxBuilder = AlertDialog.Builder(this).setView(messageBoxView)
        val messageBoxInstance = messageBoxBuilder.show()

        messageBoxView.tv_title.text = "Change Business "+ title
        messageBoxView.ed_message.setText(message)

        //set Listener
        messageBoxView.setOnClickListener(){

            messageBoxInstance.dismiss()
        }

        messageBoxView.okay.setOnClickListener {

            if (title == "Business Premise") {
                val currentBusiness = Const.instance.getBusiness()
                val newBusiness = currentBusiness.copy(premiseSize = messageBoxView.ed_message.text.toString())
                Const.instance.setBusiness(newBusiness)
            }
            if(title=="Business Description"){
                val currentBusiness = Const.instance.getBusiness()
                val newBusiness = currentBusiness.copy(businessDes = messageBoxView.ed_message.text.toString())
                Const.instance.setBusiness(newBusiness)
            }


            displayValues()
            messageBoxInstance.dismiss()
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun update(view: View) {
        when (view.id) {
            R.id.btn_business_premise -> {
                showMessageBox("Business Premise", Const.instance.getBusiness().premiseSize)
            }

            R.id.btn_business_des -> {
                showMessageBox("Business Description",Const.instance.getBusiness().businessDes)
            }


            else -> {

            }
        }
    }

    override fun onResume() {
        displayValues()
        super.onResume()
    }

}