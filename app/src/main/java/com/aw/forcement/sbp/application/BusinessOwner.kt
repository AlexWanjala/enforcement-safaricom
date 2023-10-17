package com.aw.forcement.sbp.application

import Business
import Const
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
            saveValues()
            startActivity(Intent(this, BusinessDetails::class.java))
        }
    }

    private fun saveValues(){

        save(this,"full_names",ed_full_names.text.toString())
        save(this,"owner_id",ed_owner_id.text.toString())
        save(this,"owner_phone",ed_phone.text.toString())
        save(this,"owner_email",ed_email.text.toString())
        save(this,"kra_pin",ed_kra_pin.text.toString())

    }

}