package com.aw.forcement.tabs

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.aw.forcement.ChangePassword
import com.aw.forcement.Login
import com.aw.forcement.R
import com.aw.forcement.ScanOptions
import com.aw.forcement.history.MyHistory
import com.aw.passanger.api.getValue

import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.bottom_nav.*

class Profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        DrawableCompat.setTint(DrawableCompat.wrap(imageProfile.drawable), ContextCompat.getColor(this, R.color.bg_button))
        tvProfile.setTextColor(resources.getColor(R.color.bg_button))

        nameProfile.text = getValue(this,"names").toString()
        tvNameTagProfile.text =getValue(this,"names").toString()[0].toString()+getValue(this,"names").toString()[1].toString()
        email.text = getValue(this,"email").toString()
        tv_zone.text =  getValue(this,"zone").toString()

        imageLogout.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            finishAffinity()
        }

        imageHome.setOnClickListener {
            startActivity(Intent(this,Home::class.java))
            finish()
        }

        imageHistory.setOnClickListener {
            startActivity(Intent(this,MyHistory::class.java))
            finish()
        }

    }

    override fun onBackPressed() {
        startActivity(Intent(this,Home::class.java))
        finish()
    }


}