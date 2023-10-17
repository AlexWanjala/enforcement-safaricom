package com.aw.forcement.sbp.applications

import Const
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import com.aw.forcement.R
import kotlinx.android.synthetic.main.activity_application_verification.*
import kotlinx.android.synthetic.main.update.*
import kotlinx.android.synthetic.main.update.view.*

class ApplicationVerificationOwner : AppCompatActivity() {


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_application_verification)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        btn_next.setOnClickListener {startActivity(Intent(this,ApplicationVerificationBusinessDetails::class.java)) }

        displayValues()
    }

    private fun displayValues(){
        tv_name.text = Const.instance.getBusiness().fullNames
        tv_owners_id.text = Const.instance.getBusiness().ownerID
        tv_phone.text = Const.instance.getBusiness().ownerPhone
        tv_email.text = Const.instance.getBusiness().ownerEmail
        tv_kra.text = Const.instance.getBusiness().kraPin
    }

    private lateinit var messageBoxInstance: AlertDialog
    private lateinit var messageBoxView: View

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showMessageBox(title: String,message: String){

        messageBoxView = LayoutInflater.from(this).inflate(R.layout.update, null)
        val messageBoxBuilder = AlertDialog.Builder(this).setView(messageBoxView)
        messageBoxInstance = messageBoxBuilder.show()

        messageBoxView.tv_title.text = "Change Owner's "+ title
        messageBoxView.ed_message.setText(message)

        //set Listener
        messageBoxView.setOnClickListener(){

            messageBoxInstance.dismiss()
        }

        messageBoxView.okay.setOnClickListener {

            if (title == "Name") {
                val currentBusiness = Const.instance.getBusiness()
                val newBusiness = currentBusiness.copy(fullNames = messageBoxView.ed_message.text.toString())
                Const.instance.setBusiness(newBusiness)
            }
            if(title=="ID NO"){
                val currentBusiness = Const.instance.getBusiness()
                val newBusiness = currentBusiness.copy(ownerID = messageBoxView.ed_message.text.toString())
                Const.instance.setBusiness(newBusiness)
            }
            if(title=="Phone"){
                val currentBusiness = Const.instance.getBusiness()
                val newBusiness = currentBusiness.copy(ownerPhone = messageBoxView.ed_message.text.toString())
                Const.instance.setBusiness(newBusiness)
            }

            if(title=="Email"){
                val currentBusiness = Const.instance.getBusiness()
                val newBusiness = currentBusiness.copy(ownerEmail = messageBoxView.ed_message.text.toString())
                Const.instance.setBusiness(newBusiness)
            }

            if(title=="Pin"){
                val currentBusiness = Const.instance.getBusiness()
                val newBusiness = currentBusiness.copy(kraPin = messageBoxView.ed_message.text.toString())
                Const.instance.setBusiness(newBusiness)

            }

                displayValues()
                messageBoxInstance.dismiss()
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun update(view: View) {
        when (view.id) {
            R.id.btn_name -> {
                showMessageBox("Name", Const.instance.getBusiness().fullNames)
            }

            R.id.btn_id_no -> {
                showMessageBox("ID NO",Const.instance.getBusiness().ownerID)
            }

            R.id.btn_phone -> {
                showMessageBox("Phone",Const.instance.getBusiness().ownerPhone)
            }

            R.id.btn_email -> {
                showMessageBox("Email",Const.instance.getBusiness().ownerEmail)
            }

            R.id.btn_pin -> {
                showMessageBox("Pin",Const.instance.getBusiness().kraPin)
            }

            else -> {

            }
        }
    }

    override fun onResume() {
        super.onResume()
    }


}