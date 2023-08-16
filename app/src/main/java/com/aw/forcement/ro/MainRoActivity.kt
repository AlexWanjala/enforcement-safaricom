package com.aw.forcement.ro

import Json4Kotlin_Base
import OverviewAdapter
import UsersAdapter
import UsersRoAdapter
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import com.aw.forcement.ChangePassword
import com.aw.forcement.R
import com.aw.forcement.adapters.DotsIndicatorDecoration
import com.aw.forcement.adapters.LoopingSnapHelper
import com.aw.forcement.tabs.Home
import com.aw.passanger.api.*
import com.bekawestberg.loopinglayout.library.LoopingLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main_ro.*
import kotlinx.android.synthetic.main.bottom_sheet_contact.*
import kotlinx.android.synthetic.main.message.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.recycler_view.*
import java.util.*
import kotlin.concurrent.timerTask


class MainRoActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener  {
    private lateinit var bottomSheetBehaviorContact: BottomSheetBehavior<ConstraintLayout>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)


        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        openDrawer.setOnClickListener {  if (drawerLayout.isDrawerOpen(GravityCompat.START)) {  drawerLayout.closeDrawer(GravityCompat.START) } else {  drawerLayout.openDrawer(GravityCompat.START) } }

       //get a reference to the navigation view
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
       // set the listener for the navigation view
        navigationView.setNavigationItemSelectedListener(this)
        val nameTag = navigationView.getHeaderView(0).findViewById<TextView>(R.id.nameTag)
        val name = navigationView.getHeaderView(0).findViewById<TextView>(R.id.name)
        val tv_des = navigationView.getHeaderView(0).findViewById<TextView>(R.id.tv_des)
        //set the text of the nameTag TextView
        nameTag.text = getValue(this,"names").toString()[0].toString()+ getValue(this,"names").toString()[1].toString()
        tvName.text = "Hello "+getValue(this,"names").toString()
        name.text = getValue(this,"names").toString().toLowerCase().split(" ").joinToString(" ") { it.capitalize() }
        tv_des.text = getValue(this,"category").toString().toLowerCase().split(" ").joinToString(" ") { it.capitalize() }
        //category

        val layoutSwitch = navigationView.findViewById<LinearLayout>(R.id.layoutSwitch)
        layoutSwitch.setOnClickListener {
            startActivity(Intent(this, Home::class.java))
        }

        tv_view_all.setOnClickListener {
            startActivity(Intent(this, RevenueAgentsLogs::class.java))
        }



        bottomSheetBehaviorContact = BottomSheetBehavior.from(bottomSheetLayoutContact)
        contact.setOnClickListener { toggleBottomSheetContact() }
        collectionOverview()
        tv_overview.text ="Here is an Overview for "+ getValue(this,"subCountyName")!!.toLowerCase() +" Sub County"

        radio_active.setOnClickListener {  getUsersBySubCounty("Active") }
        radio_inactive.setOnClickListener {  getUsersBySubCounty("Inactive") }
        radio_logged_out.setOnClickListener {  getUsersBySubCounty("Logged Out") }

        getUsersBySubCounty("Inactive")



        //Todo for recycler view to scroll
        val snapHelper = LoopingSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)
        val timer = Timer()
        val task = timerTask {
            recyclerView.smoothScrollBy(720, 0) // Scroll 100
        }
        timer.schedule(task, 0, 8000) // 0 delay, 3000 period


        // //getValue(this,"names").toString()[0].toString()+ getValue(this,"names").toString()[1].toString()
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
      runOnUiThread {    progress_circular.visibility = View.VISIBLE }
        val formData = listOf(
            "function" to "collectionOverviewRo",
            "latitude" to getValue(this,"latitude").toString(),
            "longitude" to getValue(this,"longitude").toString(),
            "subCountyID" to getValue(this,"subCountyID").toString(),
            "subCountyName" to getValue(this,"subCountyName").toString(),
            "username" to getValue(this,"username").toString(),
            "addressString" to getValue(this,"addressString").toString()
        )
        executeRequest(formData, biller,object : CallBack {
            override fun onSuccess(result: String?) {
                runOnUiThread {    progress_circular.visibility = View.GONE }
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){
                    runOnUiThread {
                        progress_circular.visibility = View.GONE

                        //todo if you copy this copy also another code in the onCreate method for scrolling timer
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
                }
                else{
                    runOnUiThread {
                        progress_circular.visibility = View.GONE
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
        progress_circular.visibility = View.VISIBLE
        val formData = listOf(
            "function" to "getUsersPaginated",
            "page" to "1",
            "rows_per_page" to "100",
            "category" to "ICT OFFICER",
            "search" to ""
        )
        executeRequest(formData, authentication,object : CallBack {
            override fun onSuccess(result: String?) {
                 runOnUiThread {  progress_circular.visibility = View.GONE }
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
        progress_circular.visibility = View.VISIBLE
        val formData = listOf(
            "function" to "getUsersBySubCounty",
            "subCountyID" to  getValue(this,"subCountyID").toString(),
            "status" to  status
        )
        executeRequest(formData, authentication,object : CallBack {
            override fun onSuccess(result: String?) {
                runOnUiThread {   progress_circular.visibility = View.GONE }
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


    // override the onNavigationItemSelected method
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_user_account -> {
                startActivity(Intent(this,MainRoActivity::class.java))
                finishAffinity()
            }
            R.id.nav_transaction -> {
                startActivity(Intent(this,TransactionsRo::class.java))
            }
            R.id.nav_revenue_agents -> {
                startActivity(Intent(this,RevenueAgentsLogs::class.java))
            }
            R.id.nav_transactions_break_down -> {
                startActivity(Intent(this,TransactionsBreakDown::class.java))
            }
            R.id.nav_total_county_collections -> {
                startActivity(Intent(this,TotalCountyCollection::class.java))
            }
            R.id.nav_logout_off -> {
                startActivity(Intent(this,Long::class.java))
                finishAffinity()
            }
        }
        //close the drawer after selecting an item
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        //return true to indicate that the item was handled
        return true
    }
}