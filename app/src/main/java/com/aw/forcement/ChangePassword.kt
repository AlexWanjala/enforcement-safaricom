package com.aw.forcement

import Json4Kotlin_Base
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.aw.passanger.api.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_change_password.*



import kotlinx.android.synthetic.main.message.*
import kotlinx.android.synthetic.main.progressbar.*

class ChangePassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        submit.setOnClickListener {
            if(edNewPassword.text.toString()==edPasswordConfirm.text.toString()){
                changePassword()
            }else{
                Toast.makeText(this,"Mismatch",Toast.LENGTH_LONG).show()
            }


        }
    }

    private fun changePassword (){
        progress_circular.visibility = View.VISIBLE
        val formData = listOf(
            "function" to "changePassword",
            "password" to edNewPassword.text.toString(),
            "idNo" to  getValue(this,"idNo").toString()
        )
        executeRequest(formData,authentication,object : CallBack {
            override fun onSuccess(result: String?) {
                runOnUiThread {  progress_circular.visibility = View.GONE }
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)

                if(response.success){
                    runOnUiThread {
                        tvMessage.text = response.message
                        tvMessage.setTextColor(Color.parseColor("#2AAA8A"))
                       // Toast.makeText(this@ChangePassword, response.message,Toast.LENGTH_LONG).show()

                        submit.setText("LOGIN AGAIN")
                        submit.setOnClickListener {
                            finishAffinity()
                            startActivity(Intent(this@ChangePassword, Login::class.java))
                        }


                    }

                }else{
                    runOnUiThread {
                        tvMessage.text = response.message
                    }
                }


            }

            override fun onFailure(result: String?) {
                runOnUiThread {   tvMessage.text = result }
            }

        })
    }

    override fun onResume() {
        super.onResume()


    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
        startActivity(Intent(this@ChangePassword, Login::class.java))
    }
}