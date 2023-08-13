package com.aw.passanger.api


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.AsyncTask
import android.os.Build
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.BuildConfig
import com.aw.forcement.R
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.core.extensions.jsonBody
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSocketFactory


/*
const val paysol ="https://api.paysol.co.ke/paysol/index.php"
const val URL ="https://api.paysol.co.ke/"
*/

const val URL ="https://api.craftcollect.africa/homabay/"
const val paysol ="https://api.craftcollect.africa/homabay/paysol/index.php"

/*const val URL ="https://api.elgeyomarakwet.go.ke/"
const val paysol ="api.elgeyomarakwet.go.ke/paysol/index.php"*/


var parking = "parking/"
var rent = "rent/"
var trade = "trade/"
var biller ="biller/"
var liquor ="liquor/"
var authentication ="authentication/"

//Your have created as a system user. Your username: 32783823 and password: 9227. Download the app from here. https://shorturl.at/acvT3

interface CallBack {
    fun onSuccess(result: String?)
}

fun executeRequest(formData: List<Pair<String, String>>, stream:String, callback: CallBack) {

    FuelManager.instance.socketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory
    FuelManager.instance.hostnameVerifier = HostnameVerifier { _, _ -> true }
    // get the version code from BuildConfig
    val versionCode =  "35"//todo change verson code

    val newFormData = formData + Pair("versionCode", versionCode.toString())
    Fuel.post(URL+stream, newFormData)
        .timeout(0)
                .authentication().bearer("MTVlNmJkNDE1NWZiNDBiZTZlZTVmNjMwZDg5ZmNkMTU1NTRiOTM2MDBlY2U2ZmI2YjUwNGE4MWRmOWJjYTFkZA==")
        .responseString {request, response, result ->
            println("##Request$request")
            println("##Response$result")
            callback.onSuccess(result.get())
        }
}

fun executePaysolRequest(formData: List<Pair<String, String>>, stream:String, callback: CallBack) {

    FuelManager.instance.socketFactory = SSLSocketFactory.getDefault() as SSLSocketFactory
    FuelManager.instance.hostnameVerifier = HostnameVerifier { _, _ -> true }

    Fuel.post(paysol, formData)
        .timeout(0)
        .authentication().bearer("MTVlNmJkNDE1NWZiNDBiZTZlZTVmNjMwZDg5ZmNkMTU1NTRiOTM2MDBlY2U2ZmI2YjUwNGE4MWRmOWJjYTFkZA==")
        .responseString { request, response, result ->
            println("##Request$request")
            println("##Response$response")
            callback.onSuccess(result.get())
        }

    /* println("##Request ${formData.toString()}")
     Fuel.post(URL+stream,formData).timeout(0).authentication().responseString {
             _, _, result ->
         println("##Response$result")
         callback.onSuccess(result.get())
     }*/

}


fun executeJsonRequest(json: String, function: String, callback: CallBack) {
    println("##Request ${json.toString()}")
    Fuel.post(URL+function).timeout(0).jsonBody(json).responseString {
            _, _, result ->
        println("##Response$result")
        callback.onSuccess(result.get())
    }

}

fun executeGetRequest(function:String,callback: CallBack) {
    Fuel.get(URL+function).authentication().bearer("MTVlNmJkNDE1NWZiNDBiZTZlZTVmNjMwZDg5ZmNkMTU1NTRiOTM2MDBlY2U2ZmI2YjUwNGE4MWRmOWJjYTFkZA==").responseString {
            _, _, result ->
        println("##Response$result")
        callback.onSuccess(result.get())
    }

}

class DoAsync(val handler: () -> Unit) : AsyncTask<Void, Void, Void>() {
    override fun doInBackground(vararg params: Void?): Void? {
        handler()
        return null
    }
}

fun Context.statusBarTransparent(activity: Activity){
    activity.window.apply {
        clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        statusBarColor = Color.TRANSPARENT
    }
}

fun isConnectingToInternet(context: Context): Boolean {
    val connectivity = context.getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
    val info = connectivity.activeNetworkInfo
    if (info != null && info.isConnected) try {
        val url = URL("http://www.google.com")
        val urlc = url
            .openConnection() as HttpURLConnection
        urlc.connectTimeout = 3000
        urlc.connect()
        if (urlc.responseCode == 200) {
            return true
        }
    } catch (e1: MalformedURLException) {
        e1.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return false
}

/** CHECK WHETHER INTERNET CONNECTION IS AVAILABLE OR NOT  */
fun checkConnection(context: Context): Boolean {
    val connMgr = context.getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = connMgr.activeNetworkInfo
    if (activeNetworkInfo != null) {
        // connected to the internet
        //    Toast.makeText(context, activeNetworkInfo.getTypeName(), Toast.LENGTH_SHORT).show();
        if (activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI) {
            // connected to wifi
            return true
        } else if (activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE) {
            // connected to the mobile provider's data plan
            return true
        }
    }
    return false
}

fun checkNetworkAndInternetConnection(context: Context){
    if(checkConnection(context)){
        DoAsync{
            if(!isConnectingToInternet(context)){
                alert(context,"No Internet❗","Your device is connected to a network but there is no active internet or data ")
                println("## No internet")

            }
        }.execute()
    }else{
        alert(context,"Network Not Connected❗","Your device is offline")
    }
}

fun imageToBase64(bm: Bitmap): String? {
    val baos = ByteArrayOutputStream()
    bm.compress(Bitmap.CompressFormat.JPEG, 100, baos) // bm is the bitmap object
    val b = baos.toByteArray()
    return Base64.encodeToString(b, Base64.DEFAULT)
}
fun base64toImage(mBase64string: String): Bitmap? {
    val decodedString = Base64.decode(mBase64string, Base64.DEFAULT)
    val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

    return decodedByte
}

fun alert(context: Context,title: String, message: String){
    val alertDialog: AlertDialog? = context.let {
        val builder = AlertDialog.Builder(it)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setNeutralButton("Dismiss") { dialog, which ->
            dialog.dismiss()
        }
        builder.create()
    }
    if (alertDialog != null) {
        alertDialog.show()
    }
}

fun save(context: Context, key: String?, value: String?) {
    val prefs = context.getSharedPreferences("com.aw.paysol", Context.MODE_PRIVATE)
    val editor = prefs.edit()
    editor.putString(key, value)
    editor.apply()
}

fun getValue(context: Context, key: String?): String? {
    //if 1st time , register the user
    val prefs = context.getSharedPreferences("com.aw.paysol", Context.MODE_PRIVATE)
    return prefs.getString(key, "")
}

@RequiresApi(Build.VERSION_CODES.M)
fun isOnline(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (connectivityManager != null) {
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
    }
    return false
}

fun uploadImage(bitmap: Bitmap, fileName: String){
    DoAsync {

        val formData = listOf(
            "function" to "uploadImage",
            "fileName" to fileName,
            "base64string" to imageToBase64(bitmap)
        )
        executeRequest(formData as List<Pair<String, String>>,"login", object : CallBack {
            override fun onSuccess(result: String?) {

            }
        })

    }.execute()

}
