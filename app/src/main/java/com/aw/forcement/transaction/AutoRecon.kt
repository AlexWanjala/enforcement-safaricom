package com.aw.forcement.transaction

import android.Manifest
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aw.forcement.R
import com.aw.passanger.api.CallBack
import com.aw.passanger.api.callback
import com.aw.passanger.api.executeJsonRequest
import kotlinx.android.synthetic.main.activity_auto_recon.*
import org.json.JSONObject

class AutoRecon : AppCompatActivity() {

    private val PERMISSION_REQUEST_READ_CLIPBOARD = 1001
    private lateinit var edMpesa: EditText

    private lateinit var clipboardManager: ClipboardManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auto_recon)

        imageClose.setOnClickListener { finish() }

        // Initialize your EditText
        edMpesa = findViewById(R.id.edMpesa)
        edMpesa.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Not needed
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Not needed
            }

            override fun afterTextChanged(p0: Editable?) {
                // Not needed
            }
        })

        // Check for permission to read clipboard
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_READ_CLIPBOARD
            )
        } else {
            // Permission already granted or not needed
            readFromClipboard()
        }

        submitMessage.setOnClickListener {
            processPastedMessage(edMpesa.text.toString())
        }
    }

        fun reconMessage(jsonString: String){
        executeJsonRequest(jsonString,callback,object : CallBack {

            override fun onSuccess(result: String?) {

                runOnUiThread {
                    edMpesa.setText("")
                    tvMessage.text = result
                }

            }

            override fun onFailure(result: String?) {
                runOnUiThread {
                    tvMessage.text = result
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()

        // Check for permission to read clipboard
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_READ_CLIPBOARD
            )
        } else {
            // Permission already granted or not needed
            readFromClipboard()
        }
    }
    private fun readFromClipboard() {
        // Get the clipboard manager
        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        // Check if there is data on the clipboard
        if (clipboardManager.hasPrimaryClip()) {
            val clipData = clipboardManager.primaryClip
            if (clipData != null && clipData.itemCount > 0) {
                // Get the first item from the clipboard
                val item = clipData.getItemAt(0)

                // Check if the item contains text
                if (item.text != null) {
                    // Get the text from the clipboard item
                    val pastedMessage = item.text.toString()

                    // Log the content of the clipboard item
                    Log.e("Clipboard", "Pasted message: $pastedMessage")

                    // Now proceed with your logic to extract and process the pasted message
                    processPastedMessage(pastedMessage)
                } else {
                    Toast.makeText(this, "Clipboard does not contain text", Toast.LENGTH_LONG).show()
                }
            }
        } else {
           // Toast.makeText(this, "No data on clipboard", Toast.LENGTH_LONG).show()
        }
    }
    private fun processPastedMessage(pastedMessage: String) {
        // Define the regular expression pattern to match the sample message structure
        val pattern = "([A-Z0-9]+) Confirmed\\. Ksh([\\d.,]+) sent to ([\\w\\s]+) for account ([\\w\\s]+) on (\\d{1,2}/\\d{1,2}/\\d{2,4} at \\d{1,2}:\\d{2} [AP]M)"


        // Create a regex object with the defined pattern
        val regex = Regex(pattern)

        // Match the pasted message against the regex pattern
        val matchResult = regex.find(pastedMessage)

        // If there's a match
        if (matchResult != null) {
            // Extract the groups from the match result
            val (transactionCode, amountSent, recipient, account, dateTime) = matchResult.destructured

            // Set the extracted information into your EditText
            edMpesa.setText(pastedMessage)

            // Assuming amountSent is a string representing the amount
            val amountWithoutCommaOrDecimal = amountSent.replace(",", "").split(".")[0]

// Convert the resulting string to an integer
            val amountInt = amountWithoutCommaOrDecimal.toIntOrNull() ?: 0

            // Print or use the extracted information
            println("Transaction Code: $transactionCode")
            println("Amount Sent: $amountInt")
            println("Recipient: $recipient")
            println("Account: $account")
            println("Date and Time: $dateTime")



            // Create a JSON object
            val jsonObject = JSONObject().apply {
                put("payment_type", "MPESA")
                put("account_to", account.trim())
                put("account_from", "")
                put("transaction_code", transactionCode)
                put("amount", amountSent)
                put("account_ref", account)
                put("transaction_desc", "-")
                put("trans_time", convertDateTime(dateTime))
                put("full_names", "##")
                put("status", "PAID")
                put("message", "PAID")
                put("url", "")
                put("receipt_number", transactionCode)
            }
            // Convert the JSON object to a string
            val jsonString = jsonObject.toString()
            reconMessage(jsonString)
        } else {
            // If the message doesn't match the pattern, display a message
            Toast.makeText(
                this,
                "The pasted message does not match the expected format.",
                Toast.LENGTH_SHORT
            ).show()
            println("Pasted message does not match the expected format: $pastedMessage")
        }
    }

    fun convertDateTime(dateTime: String): String {
        val parts = dateTime.split(" at ")
        val dateParts = parts[0].split("/")
        val timeParts = parts[1].split(" ")[0].split(":") // Split by space first to separate time and AM/PM

        val month = String.format("%02d", dateParts[1].toInt()) // Month is at index 1
        val day = String.format("%02d", dateParts[0].toInt()) // Day is at index 0
        val year = if (dateParts[2].length == 2) "20${dateParts[2]}" else dateParts[2]

        val hour = if (parts[1].contains("PM")) {
            if (timeParts[0].toInt() == 12) {
                12 // 12 PM remains 12
            } else {
                timeParts[0].toInt() + 12
            }
        } else {
            if (timeParts[0].toInt() == 12) {
                0 // 12 AM becomes 0
            } else {
                timeParts[0].toInt()
            }
        }

        val minute = timeParts[1].toInt()

        return "$year-$month-$day ${String.format("%02d", hour)}:${String.format("%02d", minute)}:00"
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_READ_CLIPBOARD) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed to read from clipboard
                readFromClipboard()
            } else {
                // Permission denied, handle accordingly
                // You might want to inform the user that the permission is necessary to proceed
                Toast.makeText(
                    this,
                    "Allow Permission",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}