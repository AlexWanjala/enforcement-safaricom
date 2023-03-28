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
import com.aw.forcement.ChangePassword
import com.aw.forcement.Login
import com.aw.forcement.R
import com.aw.forcement.ScanOptions
import com.aw.passanger.api.getValue
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.bottom_nav.*

class Profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

       /*imageScan.setOnClickListener { startActivity(Intent(this, ScanOptions::class.java)) }
        imageProfile.setColorFilter(ContextCompat.getColor(this, R.color.selector))

        home.setOnClickListener {  startActivity(Intent(this, Home::class.java))
            finish() }
        history.setOnClickListener {  startActivity(Intent(this, History::class.java))
            finish()}
        profile.setOnClickListener {  startActivity(Intent(this, Profile::class.java))
            finish()}

        tvChangePassword.setOnClickListener { startActivity(Intent(this, ChangePassword::class.java)) }
        tvLogout.setOnClickListener { startActivity(Intent(this, Login::class.java))
        finishAffinity()}

        nameProfile.text = getValue(this,"FirstName")!!.uppercase()+" "+  getValue(this,"LastName")!!.uppercase()
        tvNameTagProfile.text = getValue(this,"FirstName")!![0].uppercase() +""+  getValue(this,"LastName")!![0].uppercase()


        tvHelp.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "0715321138"))
            this@Profile.startActivity(intent)
        }*/
    }

    override fun onBackPressed() {
        startActivity(Intent(this,Home::class.java))
        finish()
    }


}