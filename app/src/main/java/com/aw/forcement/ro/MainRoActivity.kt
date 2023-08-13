package com.aw.forcement.ro

import Json4Kotlin_Base
import OverviewAdapter
import UsersAdapter
import UsersRoAdapter
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import com.aw.forcement.R
import com.aw.forcement.adapters.DotsIndicatorDecoration
import com.aw.passanger.api.*
import com.bekawestberg.loopinglayout.library.LoopingLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main_page.*
import kotlinx.android.synthetic.main.activity_main_ro.*
import kotlinx.android.synthetic.main.activity_main_ro.contact
import kotlinx.android.synthetic.main.activity_main_ro.tvName
import kotlinx.android.synthetic.main.activity_my_history.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.bottom_sheet_contact.*
import kotlinx.android.synthetic.main.message.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.recycler_view.*


class MainRoActivity : AppCompatActivity() {
    private lateinit var bottomSheetBehaviorContact: BottomSheetBehavior<ConstraintLayout>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
       // statusBarTransparent(this)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)

        openDrawer.setOnClickListener {  if (drawerLayout.isDrawerOpen(GravityCompat.START)) {  drawerLayout.closeDrawer(GravityCompat.START) } else {  drawerLayout.openDrawer(GravityCompat.START) } }

            tvName.text = "Hello "+getValue(this,"names").toString()

        bottomSheetBehaviorContact = BottomSheetBehavior.from(bottomSheetLayoutContact)
        contact.setOnClickListener { toggleBottomSheetContact() }
        collectionOverview()
        tv_overview.text ="Here is an Overview for "+ getValue(this,"subCountyName")!!.toLowerCase() +" Sub County"

        radio_active.setOnClickListener {  getUsersBySubCounty("Active") }
        radio_inactive.setOnClickListener {  getUsersBySubCounty("Inactive") }
        radio_logged_out.setOnClickListener {  getUsersBySubCounty("Logged Out") }

        getUsersBySubCounty("Inactive")
    }
    private fun toggleBottomSheetContact(){

        if (bottomSheetBehaviorContact.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehaviorContact.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            bottomSheetBehaviorContact.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }
        getUsersPaginated()

    }
    private fun collectionOverview(){

        val formData = listOf(
            "function" to "collectionOverview",
            "latitude" to getValue(this,"latitude").toString(),
            "longitude" to getValue(this,"longitude").toString(),
            "idNo" to getValue(this,"idNo").toString(),
            "username" to getValue(this,"username").toString(),
            "addressString" to getValue(this,"addressString").toString()
        )
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)

                if(response.success){
                    runOnUiThread {

                        with(recyclerView) {
                            layoutManager = LoopingLayoutManager(context, LoopingLayoutManager.HORIZONTAL, false)
                            adapter = OverviewAdapter(this@MainRoActivity, response.data.overview)
                            addItemDecoration(
                                DotsIndicatorDecoration(
                                    colorInactive = ContextCompat.getColor(context, R.color.grey_indicator),
                                    colorActive = ContextCompat.getColor(context, R.color.white)
                                )
                            )
                        }

                    }
                }else{
                    runOnUiThread {
                        tvMessage.text = response.message }
                }
            }

        })


    }
    var exit: Boolean =false
    override fun onBackPressed() {
        if(exit){
            finish()
        }else{
            bottomSheetBehaviorContact.state = BottomSheetBehavior.STATE_COLLAPSED

            exit = true
        }
    }
    private fun getUsersPaginated (){

        val formData = listOf(
            "function" to "getUsersPaginated",
            "page" to "1",
            "rows_per_page" to "100",
            "category" to "ICT OFFICER",
            "search" to ""
        )
        executeRequest(formData, authentication,object : CallBack {
            override fun onSuccess(result: String?) {
                //  runOnUiThread {  progress_circular.visibility = View.GONE }
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){
                    runOnUiThread {
                        val adapter = UsersAdapter(this@MainRoActivity, response.data.users)
                        adapter.notifyDataSetChanged()
                        recyclerView2.layoutManager = LinearLayoutManager(this@MainRoActivity)
                        recyclerView2.adapter = adapter
                        recyclerView2.setHasFixedSize(false)
                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@MainRoActivity,response.message, Toast.LENGTH_LONG).show()}

                }

            }

        })
    }
    override fun onResume() {
        try {
            if(getValue(this,"getUsersBySubCounty").toString().isNotEmpty()){
                updateUserUI(getValue(this,"getUsersBySubCounty").toString(), getValue(this,"status").toString())
            }
        }catch (ex: Exception ){
            save(this,"getUsersBySubCounty","")
        }


        super.onResume()
    }
    private fun getUsersBySubCounty (status: String){
        val formData = listOf(
            "function" to "getUsersBySubCounty",
            "subCountyID" to  getValue(this,"subCountyID").toString(),
            "status" to  status
        )
        executeRequest(formData, authentication,object : CallBack {
            override fun onSuccess(result: String?) {
                save(this@MainRoActivity,"getUsersBySubCounty",result)
                save(this@MainRoActivity,"status",status)
                if (result != null) {
                    updateUserUI(result,status)
                }
            }

        })
    }
    fun updateUserUI(result: String,status: String){
        val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
        runOnUiThread { recyclerView3.adapter = null }
        if(response.success){
            runOnUiThread {
                val adapter = UsersRoAdapter(this@MainRoActivity, response.data.users)
                if (status === "Active") {
                    radio_active.text =status+" (${response.data.users.size})"
                }

                if (status === "Inactive") {
                    radio_inactive.text =status+" (${response.data.users.size})"
                }

                if (status === "Logged Out") {
                    radio_logged_out.text =status+" (${response.data.users.size})"
                }
                adapter.notifyDataSetChanged()
                recyclerView3.layoutManager = LinearLayoutManager(this@MainRoActivity)
                recyclerView3.adapter = adapter
                recyclerView3.setHasFixedSize(false)
            }

        }else{
            runOnUiThread {
                //Toast.makeText(this@MyHistory,response.message, Toast.LENGTH_LONG).show()
            }

        }
    }
}