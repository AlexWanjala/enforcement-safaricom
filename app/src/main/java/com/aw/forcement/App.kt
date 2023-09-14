package com.sanxynet.printooth

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.mazenrashed.printooth.Printooth

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        Printooth.init(this)
    }

}