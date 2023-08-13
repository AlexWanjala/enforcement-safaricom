package com.aw.forcement.ro

import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aw.forcement.R
import com.aw.passanger.api.*
import kotlinx.android.synthetic.main.activity_main_ro.*
import kotlinx.android.synthetic.main.alert_ring.*
import kotlinx.android.synthetic.main.bottom_sheet_contact.*
import kotlinx.android.synthetic.main.message.*
import kotlinx.android.synthetic.main.recycler_view.*


class AlertRing : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.alert_ring)

        val name = intent.getStringExtra("names")!!.split(" ")[0]

        tv_title.text = "Alert " + name+"!"

        "To alert ${name} to stay active, click the button below and his phone will ring."

        tv_close.setOnClickListener { finish() }
        tv_ring.setOnClickListener {

            try {
                val am = getSystemService(AUDIO_SERVICE) as AudioManager
                am.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                    0
                )
                val mPlayer2: MediaPlayer = MediaPlayer.create(this, R.raw.timer)
                mPlayer2.start()

            } catch (e: Exception) {
                e.printStackTrace()
            }


        }

    }
}