package com.aw.forcement.tabs

import java.util.Timer
import kotlin.concurrent.timerTask
import Json4Kotlin_Base
import OverviewAdapter
import com.aw.forcement.vet.stock.StockMarketFees
import UsersAdapter
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.aw.forcement.*
import com.aw.forcement.adapters.DotsIndicatorDecoration
import com.aw.forcement.adapters.LoopingSnapHelper
import com.aw.forcement.history.MyHistory
import com.aw.forcement.others.*
import com.aw.passanger.api.*
import com.bekawestberg.loopinglayout.library.LoopingLayoutManager
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_page.*
import kotlinx.android.synthetic.main.bottom_nav.*
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlinx.android.synthetic.main.message.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.recycler_view.*
import java.io.IOException
import java.util.*
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.aw.forcement.clamping.ClampingModule
import com.aw.forcement.fines.Fines
import com.aw.forcement.rents.PromisedPayments
import com.aw.forcement.rents.ReceivePayment
import com.aw.forcement.rents.TenancyRegister
import com.aw.forcement.ro.MainRoActivity
import com.aw.forcement.sbp.datacollections.DataCollection
import com.aw.forcement.sbp.application.BusinessOwner
import com.aw.forcement.sbp.application.Businesses
import com.aw.forcement.sbp.applications.Applications
import com.aw.forcement.sbp.datacollections.CollectionsSBP
import com.aw.forcement.parks.ParkFees
import com.aw.forcement.vet.slaughter.SlaughterFees
import com.aw.forcement.vet.vet.VetFees
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main_page.contact
import kotlinx.android.synthetic.main.activity_main_page.openDrawer
import kotlinx.android.synthetic.main.activity_main_page.tvName
import kotlinx.android.synthetic.main.activity_main_ro.*
import kotlinx.android.synthetic.main.activity_my_history.*
import kotlinx.android.synthetic.main.bottom_sheet.closeBottom
import kotlinx.android.synthetic.main.bottom_sheet_contact.*
import kotlinx.android.synthetic.main.bottom_sheet_contact.bottomSheetLayoutContact
import kotlinx.android.synthetic.main.bottom_sheet_fines_penalties.*
import kotlinx.android.synthetic.main.bottom_sheet_fines_penalties.closeBottomFines
import kotlinx.android.synthetic.main.bottom_sheet_fines_penalties.fl_application_general
import kotlinx.android.synthetic.main.bottom_sheet_fines_penalties.fl_application_offense
import kotlinx.android.synthetic.main.bottom_sheet_fines_penalties.fl_constructions
import kotlinx.android.synthetic.main.bottom_sheet_fines_penalties.fl_illegal
import kotlinx.android.synthetic.main.bottom_sheet_fines_penalties.fl_licenses
import kotlinx.android.synthetic.main.bottom_sheet_rentals.*
import kotlinx.android.synthetic.main.bottom_sheet_sbp_data.*
import kotlinx.android.synthetic.main.bottom_sheet_sbp_permit.*
import kotlinx.android.synthetic.main.bottom_sheet_sbp_permit.closeBottomSbp
import kotlinx.android.synthetic.main.bottom_sheet_sbp_permit.fl_application_active
import kotlinx.android.synthetic.main.bottom_sheet_sbp_permit.fl_application_approval
import kotlinx.android.synthetic.main.bottom_sheet_sbp_permit.fl_application_declined
import kotlinx.android.synthetic.main.bottom_sheet_vet_services.*
import android.content.res.Resources
import android.util.DisplayMetrics
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.aw.forcement.assets.machinery.MachineryBilling
import com.aw.forcement.assets.stadium.StadiumBilling
import com.aw.forcement.assets.tractor.TractorBilling
import com.aw.forcement.billing.Billing
import com.aw.forcement.fire.SearchForBusiness
import com.aw.forcement.fire.applications.ApplicationsFire
import com.aw.forcement.health.medical.ApplicantDetails
import com.aw.forcement.history.ParkingHistory
import com.aw.forcement.service.NotificationWorker
import com.aw.forcement.transaction.AutoRecon
import com.aw.forcement.vet.movement.MovementFees
import kotlinx.android.synthetic.main.buttom_sheet_county_asset.*
import kotlinx.android.synthetic.main.buttom_sheet_fire.*
import kotlinx.android.synthetic.main.buttom_sheet_fire.closeBottomFire
import kotlinx.android.synthetic.main.buttom_sheet_fire.fl_fire_invoice
import kotlinx.android.synthetic.main.buttom_sheet_health.*
import kotlinx.android.synthetic.main.buttom_sheet_health.closeBottomHealth
import kotlinx.android.synthetic.main.buttom_sheet_health.fl_food_hygiene
import kotlinx.android.synthetic.main.buttom_sheet_hygiene.*
import kotlinx.android.synthetic.main.buttom_sheet_hygiene.closeBottomHygiene
import kotlinx.android.synthetic.main.buttom_sheet_hygiene.fl_food_hygiene_new_application
import kotlinx.android.synthetic.main.buttom_sheet_medical.*
import java.util.concurrent.TimeUnit


class Home : AppCompatActivity() {

     private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
     private lateinit var bottomSheetLayoutRental: BottomSheetBehavior<ConstraintLayout>
     private lateinit var bottomSheetLayoutFine: BottomSheetBehavior<ConstraintLayout>
     private lateinit var bottomSheetBehaviorSbp: BottomSheetBehavior<ConstraintLayout>
     private lateinit var bottomSheetBehaviorSbpDataCollection: BottomSheetBehavior<ConstraintLayout>
     private lateinit var bottomSheetBehaviorFire: BottomSheetBehavior<ConstraintLayout>
     private lateinit var bottomSheetBehaviorHealth: BottomSheetBehavior<ConstraintLayout>
     private lateinit var bottomSheetBehaviorHygiene: BottomSheetBehavior<ConstraintLayout>
     private lateinit var bottomSheetBehaviorMedical: BottomSheetBehavior<ConstraintLayout>
     private lateinit var bottomSheetBehaviorCountyAsset: BottomSheetBehavior<ConstraintLayout>
     private lateinit var bottomSheetBehaviorLivestock: BottomSheetBehavior<ConstraintLayout>
     private lateinit var bottomSheetBehaviorContact: BottomSheetBehavior<ConstraintLayout>
     private var locationPermissionGranted = false



    fun generateDrawableUrl(drawableName: String): String {
        val densityDpi = Resources.getSystem().displayMetrics.densityDpi

        val drawableBucket = when {
            densityDpi <= DisplayMetrics.DENSITY_LOW -> "drawable-ldpi"
            densityDpi <= DisplayMetrics.DENSITY_MEDIUM -> "drawable-mdpi"
            densityDpi <= DisplayMetrics.DENSITY_HIGH -> "drawable-hdpi"
            densityDpi <= DisplayMetrics.DENSITY_XHIGH -> "drawable-xhdpi"
            densityDpi <= DisplayMetrics.DENSITY_XXHIGH -> "drawable-xxhdpi"
            else -> "drawable-xxxhdpi"
        }

        return "https://api.craftcollect.africa/homabay/assets/$drawableBucket/$drawableName"
    }


    @RequiresApi(Build.VERSION_CODES.S)
     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page_home)
         AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        // Schedule the worker to run every 15 minutes
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(15, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(this).enqueue(workRequest)


        // Example usage:
        val fishUrl = generateDrawableUrl("fish.png")
       // println(fishUrl)

        /* val formatter = SimpleDateFormat("yyyy-MM-dd")
         val date = formatter.format(Date())
         if(getValue(this,"date").toString()!=date){
             startActivity(Intent(this, SelectZone::class.java))

             btn_change_zone
         }
*/

         val bluetoothScanPermission = Manifest.permission.BLUETOOTH_SCAN
         val requestCodeBluetoothScan = 1 // You can choose any request code

         if (ContextCompat.checkSelfPermission(this, bluetoothScanPermission) != PackageManager.PERMISSION_GRANTED) {

             // Request the permission
             ActivityCompat.requestPermissions(this,
                 arrayOf(bluetoothScanPermission),
                 requestCodeBluetoothScan)
         } else {
             // Permission already granted, perform Bluetooth operations
            // startBluetoothDiscovery()
         }


         btn_change_zone.setOnClickListener {  startActivity(Intent(this, SelectZone::class.java)) }

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        openDrawer.setOnClickListener {  if (drawerLayout.isDrawerOpen(GravityCompat.START)) {  drawerLayout.closeDrawer(
            GravityCompat.START) } else {  drawerLayout.openDrawer(GravityCompat.START) } }

        //get a reference to the navigation view
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        // set the listener for the navigation view
        val nameTag = navigationView.getHeaderView(0).findViewById<TextView>(R.id.nameTag)
        val name = navigationView.getHeaderView(0).findViewById<TextView>(R.id.name)
        val tv_des = navigationView.getHeaderView(0).findViewById<TextView>(R.id.tv_des)

        val username = getValue(this, "username")?.toString()

        if (!username.isNullOrEmpty() && username.length >= 2) {
            nameTag.text = username[0].toString() + username[1].toString()
        }


        tvName.text = "Hello "+getValue(this,"username").toString()
        name.text = getValue(this,"username").toString().toLowerCase().split(" ").joinToString(" ") { it.capitalize() }
        tv_des.text = getValue(this,"category").toString().toLowerCase().split(" ").joinToString(" ") { it.capitalize() }

        val layoutSwitch = navigationView.getHeaderView(0).findViewById<LinearLayout>(R.id.layoutSwitch)
        layoutSwitch.setOnClickListener {
            startActivity(Intent(this, MainRoActivity::class.java))
            finishAffinity()
        }

        val category = getValue(this,"category")

         if(BuildConfig.FLAVOR==="kisumu"){

             if (category == "COLLECTOR"){
                 imageChangeZone.visibility = View.GONE
             }

         }

         if(BuildConfig.FLAVOR==="homabay"){

             if (category == "LICENCING OFFICER" ||  category == "REVENUE OFFICER"  || category == "DEPUTY DIRECTOR" || category == "DIRECTOR REVENUE" || category == "SUPER ADMIN"){
                 fl_initiate_application.visibility = View.VISIBLE
             }
             else{
                 fl_initiate_application.visibility = View.GONE
             }

             if (category == "SUPER ADMIN"){
                 fl_application_validation.visibility = View.VISIBLE
             }
             else{
                 fl_application_validation.visibility = View.GONE
             }


             if (category == "REVENUE OFFICER"  || category == "DEPUTY DIRECTOR" || category == "DIRECTOR REVENUE" || category == "SUPER ADMIN" || category == "ACCOUNTANTS"){
                 fl_application_inspection.visibility = View.VISIBLE
             }
             else{
                 fl_application_inspection.visibility = View.GONE
             }

             if (category == "DEPUTY DIRECTOR"){
                 fl_application_approval.visibility = View.VISIBLE
             }
             else{
                 fl_application_approval.visibility = View.GONE
             }

         }





         sbpDataCollection.setOnClickListener {
             startActivity(Intent(this, DataCollection::class.java))
         }

         fl_application_list.setOnClickListener {

             startActivity(Intent(this, Businesses::class.java))
         }

        imgTest.setOnClickListener {  }

        bellNotification.setOnClickListener {
            startActivity(Intent(this, ParkingHistory::class.java))
        }

        //Street Parking
        streetDailyParking.setOnClickListener {  startActivity(Intent(this, StreetParking::class.java)) }
        //Matatu/Bus Park
        matatusStageCess.setOnClickListener { startActivity(Intent(this, CessPaymentsMatatus::class.java)) }
        //Cess
        cess.setOnClickListener { startActivity(Intent(this, CessPayments::class.java).putExtra("incomeTypePrefix","CESS")) }
        //Markets
        markets.setOnClickListener { startActivity(Intent(this, Markets::class.java).putExtra("incomeTypePrefix","MKT")) }

         toilet_module.setOnClickListener { startActivity(Intent(this, Markets::class.java).putExtra("incomeTypePrefix","TOILETS")) }
        //Receipt inspection
        receipt_inspection.setOnClickListener { toggleBottomSheet("Receipting Searching","TRANSACTION") }
        invoices_print.setOnClickListener { toggleBottomSheet("Invoice Searching","INVOICE") }
        //No Plate Verification
        plate_verification.setOnClickListener {  startActivity(Intent(this, Street::class.java)) }

        meteredParking.setOnClickListener {  startActivity(Intent(this, MeteredParking::class.java)) }
        //document_verification
        document_verification.setOnClickListener {  startActivity(Intent(this, ScanClass::class.java)) }
        //history
        history.setOnClickListener {
            startActivity(Intent(this, MyHistory::class.java).putExtra("bottomBar","show").putExtra("names","My History").putExtra("idNo", getValue(this,"idNo")))
            finish()
        }
        clamping_module.setOnClickListener {
            startActivity(Intent(this, ClampingModule::class.java))
        }

        billing_module.setOnClickListener {
            startActivity(Intent(this, Billing::class.java))
        }
         //Fish
         fish_module.setOnClickListener { startActivity(Intent(this, Markets::class.java).putExtra("incomeTypePrefix","FISHCESS")) }
         park_module.setOnClickListener { startActivity(Intent(this, ParkFees::class.java).putExtra("incomeTypePrefix","FISHCESS")) }

        profile.setOnClickListener {  startActivity(Intent(this, Profile::class.java))
            finish()}

         //contact
         contact.setOnClickListener {
             getUsersPaginated()
             toggleBottomSheetContact()
         }
         livestock_module.setOnClickListener { toggleBottomSheetLivestock() }

         //SBP
         sbpDataCollection.setOnClickListener { toggleBottomSheetSbpDataSheet() }
         sbp.setOnClickListener { toggleBottomSheetSbp() }
         fl_initiate_application.setOnClickListener {
             startActivity(Intent(this, BusinessOwner::class.java))
         }
         fl_application_validation.setOnClickListener {
             save(this,"header","Application Validation")
             startActivity(Intent(this, Applications::class.java).putExtra("keyword","2"))
         }
         fl_application_inspection.setOnClickListener {
             save(this,"header","Application Validation")
             startActivity(Intent(this, Applications::class.java).putExtra("keyword","3"))
         }
         fl_application_approval.setOnClickListener {
             save(this,"header","Application Approval")
             startActivity(Intent(this, Applications::class.java).putExtra("keyword","4"))
         }
         fl_application_declined.setOnClickListener {

             save(this,"header","Declined Businesses")
             startActivity(Intent(this, Applications::class.java).putExtra("keyword","Declined"))

         }
         fl_application_active.setOnClickListener {
             save(this,"header","Active Businesses")
             startActivity(Intent(this, Applications::class.java).putExtra("keyword","true"))
         }

         fl_sbp_collection.setOnClickListener {
             save(this,"header","Active Businesses")
             startActivity(Intent(this, DataCollection::class.java))
         }
         fl_data_collections.setOnClickListener {
             startActivity(Intent(this, CollectionsSBP::class.java))
         }

        //Health
        public_health.setOnClickListener { toggleBottomSheetHealth() }
        fl_food_hygiene.setOnClickListener {
            bottomSheetBehaviorHealth.state = BottomSheetBehavior.STATE_COLLAPSED
            Toast.makeText(this,"dhdh",Toast.LENGTH_LONG).show()
        }

        //Hygiene
        fl_food_hygiene.setOnClickListener { toggleBottomSheetHygiene() }
        fl_food_hygiene_new_application.setOnClickListener {   startActivity(Intent(this, com.aw.forcement.health.hygiene.SearchForBusiness::class.java)) }


        //Medical
        fl_medical.setOnClickListener { toggleBottomSheetMedical() }
        fl_medical_new_application.setOnClickListener {  startActivity(Intent(this, ApplicantDetails::class.java)) }



        //County-Asset toggleBottomSheetCountyAsset
        count_asset_module.setOnClickListener { toggleBottomSheetCountyAsset() }
        fl_tractor.setOnClickListener {  startActivity(Intent(this, TractorBilling::class.java)) }
        fl_stadium.setOnClickListener {  startActivity(Intent(this, StadiumBilling::class.java)) }
        fl_machinery.setOnClickListener {  startActivity(Intent(this, MachineryBilling::class.java)) }



        //Fire Service
        fire_module.setOnClickListener { toggleBottomSheetFire() }
        fl_fire_invoice.setOnClickListener {
            startActivity(Intent(this, SearchForBusiness::class.java))
        }
        fl_fire_application_approval.setOnClickListener {
            startActivity(Intent(this, ApplicationsFire::class.java)
                .putExtra("keyword","4")
                .putExtra("header","Pending Approval"))
        }
        fl_fire_application_active.setOnClickListener {
            startActivity(Intent(this, ApplicationsFire::class.java)
                .putExtra("keyword","5")
                .putExtra("header","Active Certificate"))
        }
        fl_fire_invoices.setOnClickListener {
            startActivity(Intent(this, BillInvoices::class.java))
        }


         //Fines and Penaltie
         fines.setOnClickListener { toggleBottomSheetFines() }
         fl_illegal.setOnClickListener {  startActivity(Intent(this, Fines::class.java).putExtra("incomeTypePrefix","SIGNS").putExtra("title",resources.getString(R.string.illegal))) }
         fl_constructions.setOnClickListener {  startActivity(Intent(this, Fines::class.java).putExtra("incomeTypePrefix","CONSTRUCTION").putExtra("title",resources.getString(R.string.constructions))) }
         fl_licenses.setOnClickListener {  startActivity(Intent(this, Fines::class.java).putExtra("incomeTypePrefix","PERMITS").putExtra("title",resources.getString(R.string.licenses))) }
         fl_application_offense.setOnClickListener {  startActivity(Intent(this, Fines::class.java).putExtra("incomeTypePrefix","OFFENCES").putExtra("title",resources.getString(R.string.offences))) }
         fl_application_general.setOnClickListener {  startActivity(Intent(this, Fines::class.java).putExtra("incomeTypePrefix","GENERAL").putExtra("title",resources.getString(R.string.general))) }
         fl_application_towing.setOnClickListener {  startActivity(Intent(this, Fines::class.java).putExtra("incomeTypePrefix","TOWING").putExtra("title",resources.getString(R.string.towing))) }


         //Rent
         rent.setOnClickListener { toggleBottomSheetRental() }
         fl_receive_payment.setOnClickListener {  startActivity(Intent(this, ReceivePayment::class.java)) }
         fl_promised_payments.setOnClickListener {  startActivity(Intent(this, PromisedPayments::class.java)) }
         fl_rentals_register.setOnClickListener {  startActivity(Intent(this, TenancyRegister::class.java)) }

         //Vet & Livestock Services
         fl_live_stock_licencing.visibility = View.GONE
         fl_stock_market_fees.setOnClickListener { startActivity(Intent(this, StockMarketFees::class.java))}
         fl_slaughter.setOnClickListener { startActivity(Intent(this, SlaughterFees::class.java)) }

         fl_vet_and_health_hub.setOnClickListener { startActivity(Intent(this, VetFees::class.java)) }

         fl_live_stock.setOnClickListener { startActivity(Intent(this, MovementFees::class.java)) }

        //Recon
        recon_module.setOnClickListener { startActivity(Intent(this, AutoRecon::class.java)) }

       // liquor_module.setOnClickListener {  }


         //addBusiness.setOnClickListener { startActivity(Intent(this, AddBusiness::class.java)) }
       // imagePay.setOnClickListener { startActivity(Intent(this, CessPayments::class.java).putExtra("incomeTypePrefix","")) }
       // imageScan.setOnClickListener { startActivity(Intent(this, ScanOptions::class.java)) }

        // Home
        DrawableCompat.setTint(DrawableCompat.wrap(imageHome.drawable), ContextCompat.getColor(this, R.color.bg_button))
        tvHome.setTextColor(resources.getColor(R.color.bg_button))


         bottomSheetLayoutRental = BottomSheetBehavior.from(bottomSheetLayoutRentals)
         bottomSheetLayoutFine = BottomSheetBehavior.from(bottomSheetLayoutFines)
         bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
         bottomSheetBehaviorSbp = BottomSheetBehavior.from(bottomSheetLayoutPermit)
         bottomSheetBehaviorContact = BottomSheetBehavior.from(bottomSheetLayoutContact)
         bottomSheetBehaviorLivestock = BottomSheetBehavior.from(bottomSheetLayoutVeterinary)
         bottomSheetBehaviorSbpDataCollection = BottomSheetBehavior.from(bottomSheetLayoutDataCollection)
         bottomSheetBehaviorFire = BottomSheetBehavior.from(bottomSheetFire)
         bottomSheetBehaviorHealth = BottomSheetBehavior.from(bottomSheetHealth)
         bottomSheetBehaviorHygiene = BottomSheetBehavior.from(bottomSheetHygiene)
         bottomSheetBehaviorMedical = BottomSheetBehavior.from(bottomSheetMedical)
         bottomSheetBehaviorCountyAsset = BottomSheetBehavior.from(bottomCountyAsset)


       // business.setOnClickListener { toggleBottomSheet("business") }
       // cess.setOnClickListener { toggleBottomSheet("cess") }
       // parking.setOnClickListener { startActivity(Intent(this, Offstreet::class.java)) }


       // transaction.setOnClickListener {  startActivity(Intent(this, Transactions::class.java)) }
      //  offstreet.setOnClickListener {  startActivity(Intent(this, Street::class.java))  }
         closeBottomRent.setOnClickListener {   bottomSheetLayoutRental.state = BottomSheetBehavior.STATE_COLLAPSED }
         closeBottomFines.setOnClickListener {   bottomSheetLayoutFine.state = BottomSheetBehavior.STATE_COLLAPSED }
         closeBottom.setOnClickListener {   bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED }
         closeBottomSbp.setOnClickListener {   bottomSheetBehaviorSbp.state = BottomSheetBehavior.STATE_COLLAPSED }
         closeBottomSbpData.setOnClickListener {   bottomSheetBehaviorSbpDataCollection.state = BottomSheetBehavior.STATE_COLLAPSED }
         closeBottomFire.setOnClickListener {   bottomSheetBehaviorFire.state = BottomSheetBehavior.STATE_COLLAPSED }
         closeBottomHealth.setOnClickListener {   bottomSheetBehaviorHealth.state = BottomSheetBehavior.STATE_COLLAPSED }
         closeBottomHygiene.setOnClickListener {   bottomSheetBehaviorHygiene.state = BottomSheetBehavior.STATE_COLLAPSED }
         closeBottomMedical.setOnClickListener {   bottomSheetBehaviorMedical.state = BottomSheetBehavior.STATE_COLLAPSED }
          closeBottomCountyAsset.setOnClickListener {   bottomSheetBehaviorCountyAsset.state = BottomSheetBehavior.STATE_COLLAPSED }
        //streetParking.setOnClickListener { startActivity(Intent(this, Street::class.java)) }
        //imagePaking.setOnClickListener { startActivity(Intent(this, Parking::class.java))  }
        // imagePaking.setOnClickListener { startActivity(Intent(this, CessPaymentsMatatus::class.java))  }

        collectionOverview()


        //Todo for recycler view to scroll
        val snapHelper = LoopingSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)
        val timer = Timer()
        val task = timerTask {
            recyclerView.smoothScrollBy(720, 0) // Scroll 100
        }
        timer.schedule(task, 0, 8000) // 0 delay, 3000 period

        //Disable navigation drawer


        if(category =="COLLECTOR" || category=="INSPECTOR" || category =="ENFORCEMENT"){
            // Get a reference to the drawer layout
            val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            val navigationView = findViewById<NavigationView>(R.id.nav_view)
            navigationView.setVisibility(View.GONE)
            imageHomeside.visibility = View.GONE
        }




         requestBluetooth()

    }

    private fun requestBluetooth(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 1)

        }
    }
     private fun toggleBottomSheetSbp(){

        if (bottomSheetBehaviorSbp.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehaviorSbp.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            bottomSheetBehaviorSbp.state = BottomSheetBehavior.STATE_EXPANDED
        }

    }

    private fun toggleBottomSheetSbpDataSheet(){

        if (bottomSheetBehaviorSbpDataCollection.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehaviorSbpDataCollection.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            bottomSheetBehaviorSbpDataCollection.state = BottomSheetBehavior.STATE_EXPANDED
        }

    }
    private fun toggleBottomSheetFire(){

        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehaviorFire.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            bottomSheetBehaviorFire.state = BottomSheetBehavior.STATE_EXPANDED
        }

    }
    private fun toggleBottomSheetHealth(){

        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehaviorHealth.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            bottomSheetBehaviorHealth.state = BottomSheetBehavior.STATE_EXPANDED
        }

    }
    private fun toggleBottomSheetHygiene(){

        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehaviorHygiene.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            bottomSheetBehaviorHygiene.state = BottomSheetBehavior.STATE_EXPANDED
        }

    }
    private fun toggleBottomSheetMedical(){

        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehaviorMedical.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            bottomSheetBehaviorMedical.state = BottomSheetBehavior.STATE_EXPANDED
        }

    }
    private fun toggleBottomSheetCountyAsset(){

        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehaviorCountyAsset.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            bottomSheetBehaviorCountyAsset.state = BottomSheetBehavior.STATE_EXPANDED
        }

    }
     private fun toggleBottomSheet(title: String, type: String){
        runOnUiThread {
            tvTitle.text = title
            tvMessage.text = ""
            edSearch.text.clear()
            buttonSearch.text = type
        }

        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }
        buttonSearch.setOnClickListener {
            if(type=="TRANSACTION"){
                getTransactions()
            }else{
                getBillsPaginate()
            }

        }

    }

    private fun toggleBottomSheetRental(){

        if (bottomSheetLayoutRental.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetLayoutRental.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            bottomSheetLayoutRental.state = BottomSheetBehavior.STATE_EXPANDED
        }

    }

    private fun toggleBottomSheetFines(){

        if (bottomSheetLayoutFine.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetLayoutFine.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            bottomSheetLayoutFine.state = BottomSheetBehavior.STATE_EXPANDED
        }
        buttonSearch.setOnClickListener {
            getTransactions()
        }

    }
     private fun toggleBottomSheetContact(){

        if (bottomSheetBehaviorContact.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehaviorContact.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            bottomSheetBehaviorContact.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }
    }
     private fun toggleBottomSheetLivestock(){

        if (bottomSheetBehaviorLivestock.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehaviorLivestock.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            bottomSheetBehaviorLivestock.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }
     override fun onResume() {
        super.onResume()
         tvZone.text = getValue(this,"zone")
        //name.text = getValue(this,"username")
        //nameTag.text = getValue(this,"firstName").toString()[0].toString()+""+getValue(this,"lastName").toString()[0].toString()

        locationPermission()

    }
     private fun getUsersPaginated (){
        runOnUiThread {  progress_circular.visibility = View.VISIBLE }
        val formData = listOf(
            "function" to "getUsersPaginated",
            "page" to "1",
            "rows_per_page" to "100",
            "category" to "ICT OFFICER",
            "search" to "",
            "deviceId" to getDeviceIdNumber(this)
        )
        executeRequest(formData, authentication,object : CallBack {
            override fun onSuccess(result: String?) {
                runOnUiThread {  progress_circular.visibility = View.GONE }
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)
                if(response.success){
                    runOnUiThread {
                        val adapter = UsersAdapter(this@Home, response.data.users)
                        adapter.notifyDataSetChanged()
                        recyclerView2.layoutManager = LinearLayoutManager(this@Home)
                        recyclerView2.adapter = adapter
                        recyclerView2.setHasFixedSize(false)
                    }

                }else{
                    runOnUiThread {  Toast.makeText(this@Home,response.message, Toast.LENGTH_LONG).show()}

                }

            }

            override fun onFailure(result: String?) {

                runOnUiThread {
                    progress_circular.visibility = View.GONE
                    Toast.makeText(this@Home,result, Toast.LENGTH_LONG).show()
                }
            }


        })
    }
     private fun getTransactions(){

         if(edSearch.text.toString().isEmpty()){
             Toast.makeText(this,"Empty",Toast.LENGTH_LONG).show()
             return
         }

         tvMessage.text =""

         progress_circular.visibility = View.VISIBLE
         val formData = listOf(
             "function" to "getTransactions",
             "keyword" to edSearch.text.toString().trim(),
             "latitude" to getValue(this,"latitude").toString(),
             "longitude" to getValue(this,"longitude").toString(),
             "idNo" to getValue(this,"idNo").toString(),
             "username" to getValue(this,"username").toString(),
             "addressString" to getValue(this,"addressString").toString(),
             "deviceId" to getDeviceIdNumber(this)
         )
         executeRequest(formData, biller,object : CallBack{
             override fun onSuccess(result: String?) {
                 runOnUiThread {  progress_circular.visibility = View.GONE }
                 val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)

                 if(response.success){
                   // runOnUiThread { bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED }
                     startActivity(Intent(this@Home,TransactionsResults::class.java).putExtra("result",result))
                 }else{
                     runOnUiThread {
                         tvMessage.text = response.message }
                 }
             }
             override fun onFailure(result: String?) {
                 runOnUiThread {
                     progress_circular.visibility = View.GONE
                     Toast.makeText(this@Home,result, Toast.LENGTH_LONG).show()
                 }
             }

         })



     }
      private fun getBillsPaginate(){

        if(edSearch.text.toString().isEmpty()){
            Toast.makeText(this,"Empty",Toast.LENGTH_LONG).show()
            return
        }

        tvMessage.text =""
        progress_circular.visibility = View.VISIBLE
        val formData = listOf(
            "function" to "getBillsPaginate",
            "keyword" to edSearch.text.toString().trim(),
            "latitude" to getValue(this,"latitude").toString(),
            "longitude" to getValue(this,"longitude").toString(),
            "idNo" to getValue(this,"idNo").toString(),
            "username" to getValue(this,"username").toString(),
            "addressString" to getValue(this,"addressString").toString(),
            "page" to "1",
            "rows_per_page" to "10",
            "responseName" to "billDetailsList",
            "deviceId" to getDeviceIdNumber(this)
        )
        executeRequest(formData, biller,object : CallBack{
            override fun onSuccess(result: String?) {
                runOnUiThread {  progress_circular.visibility = View.GONE }
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)

                if(response.success){
                    // runOnUiThread { bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED }
                    startActivity(Intent(this@Home,BillInvoices::class.java).putExtra("result",result))
                }else{
                    runOnUiThread {
                        tvMessage.text = response.message
                    }
                }
            }
            override fun onFailure(result: String?) {

                runOnUiThread {
                    progress_circular.visibility = View.GONE
                    Toast.makeText(this@Home,result, Toast.LENGTH_LONG).show()
                }
            }

        })

    }
     private fun collectionOverview(){

        val formData = listOf(
            "function" to "collectionOverview",
            "keyword" to edSearch.text.toString().trim(),
            "latitude" to getValue(this,"latitude").toString(),
            "longitude" to getValue(this,"longitude").toString(),
            "idNo" to getValue(this,"idNo").toString(),
            "username" to getValue(this,"username").toString(),
            "addressString" to getValue(this,"addressString").toString(),
            "deviceId" to getDeviceIdNumber(this)
        )
        executeRequest(formData, biller,object : CallBack{
            override fun onSuccess(result: String?) {
                runOnUiThread {  progress_circular.visibility = View.GONE }
                val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)

                if(response.success){
                    runOnUiThread {

                        //todo if you copy this copy also another code in the onCreate method for scrolling timer
                        with(recyclerView) {
                            layoutManager = LoopingLayoutManager(context, LoopingLayoutManager.HORIZONTAL, false)
                            adapter = OverviewAdapter(this@Home, response.data.overview)
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
            override fun onFailure(result: String?) {

                runOnUiThread {
                    progress_circular.visibility = View.GONE
                    Toast.makeText(this@Home,result, Toast.LENGTH_LONG).show()
                }
            }

        })


    }

     var exit: Boolean =false
     override fun onBackPressed() {
        if(exit){
            finish()
        }else{
            bottomSheetLayoutFine.state = BottomSheetBehavior.STATE_COLLAPSED
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            bottomSheetBehaviorContact.state = BottomSheetBehavior.STATE_COLLAPSED
            bottomSheetBehaviorLivestock.state = BottomSheetBehavior.STATE_COLLAPSED

            exit = true
        }
    }
    //Device location
     override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        locationPermissionGranted = false
        when (requestCode) {
            requestCode -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                    finishAffinity()
                    startActivity(Intent(this,Home::class.java))
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }

    }
     private fun locationPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }else{
            locationPermissionGranted = true

            val mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val mGPS = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

            getDeviceLocation()
        }

    }
     //Get device location
    @SuppressLint("MissingPermission")
     private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                println("## locationPermissionGranted")
                val locationResult = LocationServices.getFusedLocationProviderClient(this)
                locationResult.lastLocation.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        println("## Current location is okay ")
                        // Set the map's camera position to the current location of the device.
                        val lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            println("## lastKnownLocation lat{${lastKnownLocation.latitude}} long{${lastKnownLocation.longitude}}")
                            save(this,"latitude", lastKnownLocation.latitude.toString())
                            save(this,"longitude", lastKnownLocation.longitude.toString())
                            val address = getAddress(lastKnownLocation.latitude,lastKnownLocation.longitude)
                            println("## address lat{${address}}")
                        }else{
                            println("## lastKnownLocation null ")
                        }
                    } else {
                        println("## Current location is null. Using defaults."+ task.exception)
                    }
                }
            }else{
                println("## locationPermissionNotGranted")

            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }
     private fun getAddress(lat: Double, lng: Double) {
        // Initializing Geocoder
        val mGeocoder = Geocoder(applicationContext, Locale.getDefault())
        var addressString= ""

        // Reverse-Geocoding starts
        try {
            val addressList: List<Address> = mGeocoder.getFromLocation(lat,lng, 1)!!

            // use your lat, long value here
            if (addressList.isNotEmpty()) {
                val address = addressList[0]
                val sb = StringBuilder()
                for (i in 0 until address.maxAddressLineIndex) {
                    sb.append(address.getAddressLine(i)).append("\n")
                }

                // Various Parameters of an Address are appended
                // to generate a complete Address
                if (address.premises != null)
                    sb.append(address.premises).append(", ")

                sb.append(address.subAdminArea).append("\n")
                sb.append(address.locality).append(", ")
                sb.append(address.adminArea).append(", ")
                sb.append(address.countryName).append(", ")
                sb.append(address.featureName)

                // StringBuilder sb is converted into a string
                // and this value is assigned to the
                // initially declared addressString string.
                addressString = sb.toString()
              //  runOnUiThread {  tvAddress.text = addressString.replace("null","") }
                save(this,"addressString",addressString.replace("null",""))
                save(this,"latLng","${lat}, $lng")
               // Log.d("###",tvAddress.text.toString())
            }
        } catch (e: IOException) {
            Toast.makeText(applicationContext,"Unable connect to Geocoder",Toast.LENGTH_LONG).show()
        }

    }



}