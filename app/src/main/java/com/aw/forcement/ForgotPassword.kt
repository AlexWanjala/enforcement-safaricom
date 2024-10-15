package com.aw.forcement

import Json4Kotlin_Base
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.aw.forcement.api.TextToSpeechUtil
import com.aw.forcement.tabs.Home
import com.aw.passanger.api.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_forgot_password.edUsername
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.message.*
import kotlinx.android.synthetic.main.progressbar.*

class ForgotPassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        loginLayout.setOnClickListener { finish() }

        btnGeneratePassword.setOnClickListener {
            if(edUsername.text.isNotEmpty()){
                forgetPassword()
            }else{
                tvMessage.text ="Enter ID/ Username"
            }

        }
    }



    private fun forgetPassword (){
        tvMessage.text =""
        progress_circular.visibility = View.VISIBLE
        val formData = listOf(
            "function" to "forgetPassword",
            "idNo" to edUsername.text.toString(),
        )
        executeRequest(formData, authentication,object : CallBack{
            override fun onSuccess(result: String?) {
                runOnUiThread {  progress_circular.visibility = View.GONE }

                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){

                  runOnUiThread {
                      tvMessage.text = response.message
                      tvMessage.setTextColor(Color.parseColor("#228B22"))
                      btnGeneratePassword.text ="Login"
                      btnGeneratePassword.setOnClickListener { finish() }
                  }

                } else{
                    runOnUiThread {
                        tvMessage.text = response.message
                        tvMessage.setTextColor(Color.RED)
                    }
                    // startActivity(Intent(this@Login, Home::class.java))
                }
            }
            override fun onFailure(result: String?) {
                runOnUiThread {
                    TextToSpeechUtil.speak(result.toString())
                    Toast.makeText(this@ForgotPassword,result, Toast.LENGTH_LONG).show()
                    progress_circular.visibility = View.GONE

                }
            }
        })
    }
}