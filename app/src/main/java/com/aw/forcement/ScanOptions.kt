package com.aw.forcement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_scan_options.*

class ScanOptions : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_options)

        imageClose.setOnClickListener { finish() }
        scanReceipt.setOnClickListener { startActivity(Intent(this,ScanClass::class.java).putExtra("type","receipt")) }
        scanTrade.setOnClickListener { startActivity(Intent(this,ScanClass::class.java).putExtra("type","trade")) }
    }
}