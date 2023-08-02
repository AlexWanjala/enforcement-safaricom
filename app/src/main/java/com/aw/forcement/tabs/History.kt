package com.aw.forcement.tabs

import Json4Kotlin_Base
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.aw.forcement.R
import com.aw.forcement.ScanOptions
import com.aw.forcement.history.BusinessHistoty
import com.aw.forcement.history.CessHistory
import com.aw.forcement.history.ParkingHistory
import com.aw.forcement.history.ReceiptHistory
import com.aw.passanger.api.CallBack
import com.aw.passanger.api.executeRequest
import com.aw.passanger.api.getValue
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.bottom_nav.*

class History : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        //imageScan.setOnClickListener { startActivity(Intent(this, ScanOptions::class.java)) }
        imageHistory.setColorFilter(ContextCompat.getColor(this, R.color.selector))

        home.setOnClickListener {  startActivity(Intent(this, Home::class.java))
            finish() }
        history.setOnClickListener {  startActivity(Intent(this, History::class.java))
            finish()}
        profile.setOnClickListener {  startActivity(Intent(this, Profile::class.java))
            finish()}


        parkingButton.setOnClickListener {  startActivity(Intent(this@History, ParkingHistory::class.java)) }
        businessButton.setOnClickListener {  startActivity(Intent(this@History, BusinessHistoty::class.java)) }
        receiptButton.setOnClickListener {  startActivity(Intent(this@History, ReceiptHistory::class.java)) }
        cessButton.setOnClickListener {  startActivity(Intent(this@History, CessHistory::class.java)) }
    }



    override fun onBackPressed() {
        startActivity(Intent(this,Home::class.java))
        finish()
    }
}
