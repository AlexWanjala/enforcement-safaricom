package com.aw.passanger.api


import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.AsyncTask
import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.core.extensions.jsonBody
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

const val URL ="https://api.paysol.co.ke/"
var parking = "parking/"
var trade = "trade/"
var paysol ="paysol/"
var biller ="biller/"



interface CallBack {
    fun onSuccess(result: String?)
}

fun executeRequest(formData: List<Pair<String, String>>, stream:String, callback: CallBack) {
    println("##Request ${formData.toString()}")
    Fuel.post(URL+stream,formData).authentication().responseString {
            _, _, result ->
        println("##Response$result")
        callback.onSuccess(result.get())
    }

}


fun executeJsonRequest(json: String, function: String, callback: CallBack) {
    println("##Request ${json.toString()}")
    Fuel.post(URL+function).jsonBody(json).responseString {
            _, _, result ->
        println("##Response$result")
        callback.onSuccess(result.get())
    }

}


fun executeGetRequest(function:String,callback: CallBack) {
    Fuel.get(URL+function).authentication().responseString {
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
