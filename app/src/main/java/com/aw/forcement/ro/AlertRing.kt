package com.aw.forcement.ro

import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
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

        val name = intent.getStringExtra("names")!!.split(" ")[0].lowercase().split(" ").joinToString(" ") { it.capitalize() }

        tv_title.text = "Alert " + name+"!"
        tv_message.text =  "To alert ${name} to stay active, click the button below and his phone will ring."

        tv_close.setOnClickListener { finish() }

        try {
            val am = getSystemService(AUDIO_SERVICE) as AudioManager
            am.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                0
            )
            val mPlayer2: MediaPlayer = MediaPlayer.create(this, R.raw.timer)

            tv_ring.setOnClickListener {
                layoutTime.visibility = View.VISIBLE

                tv_title.text = "Alerting " + name+"!"

                mPlayer2.start()

                object : CountDownTimer(10000, 1000) {

                    override fun onTick(millisUntilFinished: Long) {
                        tv_timer.text = (millisUntilFinished / 1000).toString()
                        if((millisUntilFinished/1000).toInt()==5){
                            mPlayer2.start()
                        }
                    }

                    override fun onFinish() {
                        tv_message.text =  "You have successfully alerted ${name} phone. Keep track of his activities and intervene if needed."
                        tv_ring.setOnClickListener { finish() }
                        tv_ring.text = "close"
                        mPlayer2.start()
                        layoutTime.visibility = View.GONE
                    }
                }.start()

            }


        } catch (e: Exception) {
            e.printStackTrace()
        }



    }
}