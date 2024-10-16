package com.aw.forcement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.android.synthetic.main.activity_scan_options.*

class ScanOptions : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_options)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        imageClose.setOnClickListener { finish() }
        scanReceipt.setOnClickListener { startActivity(Intent(this,ScanClass::class.java).putExtra("type","receipt")) }
        scanTrade.setOnClickListener { startActivity(Intent(this,ScanClass::class.java).putExtra("type","trade")) }
        scan_liquor.setOnClickListener { startActivity(Intent(this,ScanClass::class.java).putExtra("type","liquor")) }
    }
}