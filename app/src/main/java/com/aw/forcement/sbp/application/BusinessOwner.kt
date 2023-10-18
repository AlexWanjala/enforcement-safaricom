package com.aw.forcement.sbp.application

import Business
import Const
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.aw.forcement.R
import com.aw.passanger.api.save
import kotlinx.android.synthetic.main.activity_business_owner.*


class BusinessOwner : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_owner)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        btn_next.setOnClickListener {
            saveIfNotEmpty()

        }
    }

    private fun saveIfNotEmpty() {
       // Get the text from the edit texts
        val fullNames = ed_full_names.text.toString()
        val ownerId = ed_owner_id.text.toString()
        val ownerPhone = ed_phone.text.toString()
        val ownerEmail = ed_email.text.toString()
        val kraPin = ed_kra_pin.text.toString()

        //Check if any of them is empty
        if (fullNames.isEmpty() || ownerId.isEmpty() || ownerPhone.isEmpty() || ownerEmail.isEmpty() || kraPin.isEmpty()) {
       //Show a toast message or an error message
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()

        } else {
         //Save the data using your save function
        save(this, "full_names", fullNames)
        save(this, "owner_id", ownerId)
        save(this, "owner_phone", ownerPhone)
        save(this, "owner_email", ownerEmail)
        save(this, "kra_pin", kraPin)
        startActivity(Intent(this, BusinessDetails::class.java))
        }
    }

}