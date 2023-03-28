package com.aw.forcement

import Json4Kotlin_Base
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.aw.passanger.api.CallBack
import com.aw.passanger.api.executeRequest
import com.aw.passanger.api.getValue
import com.aw.passanger.api.save
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.android.synthetic.main.activity_login.edPassword

import kotlinx.android.synthetic.main.message.*
import kotlinx.android.synthetic.main.progressbar.*

class ChangePassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        submit.setOnClickListener {
            if(edPassword.text.toString()==edNewPassword.text.toString()){
                changePassword()
            }else{
                Toast.makeText(this,"Mismatch",Toast.LENGTH_LONG).show()
            }


        }
    }

    private fun changePassword (){
        progress_circular.visibility = View.VISIBLE
        val formData = listOf(
            "oldpassword" to getValue(this,"pass").toString(),
            "newpassword" to edNewPassword.text.toString(),
            "confirmpassword" to edPassword.text.toString(),
            "UserID" to getValue(this,"UserID").toString(),
            "TownId" to getValue(this,"TownId").toString()
        )
        executeRequest(formData,"changepassword",object : CallBack {
            override fun onSuccess(result: String?) {
                runOnUiThread {  progress_circular.visibility = View.GONE }
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.status==1){
                    save(this@ChangePassword,"pass", "" )
                    runOnUiThread {   tvMessage.text = response.message }
                    finish()
                }else if(response.status==2){
                    runOnUiThread {   tvMessage.text = response.message }
                }else{
                    runOnUiThread {   tvMessage.text = response.message }

                }

            }

        })
    }

    override fun onResume() {
        super.onResume()
        tvName.text = getValue(this,"FirstName")!!.uppercase()+" "+  getValue(this,"LastName")!!.uppercase()
        tvNameTag.text = getValue(this,"FirstName")!![0].uppercase() +""+  getValue(this,"LastName")!![0].uppercase()

    }
}