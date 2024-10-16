import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ParseException
import android.net.Uri
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.aw.forcement.R
import com.aw.forcement.others.ReceiptDetails
import com.aw.forcement.sbp.applications.ApplicationsDetail
import com.aw.passanger.api.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Created by Alex Boey on 8/1/2016.
 */
class AdapterClamp(private val context: Context, mList: List<Clamped>) :
	RecyclerView.Adapter<AdapterClamp.ViewHolder?>() {
	private var mList: List<Clamped> = ArrayList<Clamped>()

	inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		var layoutView: LinearLayout = itemView.findViewById<View>(R.id.layoutView) as LinearLayout
		var tv_number_plate: TextView = itemView.findViewById<View>(R.id.tv_number_plate) as TextView
		var tv_category: TextView = itemView.findViewById<View>(R.id.tv_category) as TextView
		var tv_zone: TextView = itemView.findViewById<View>(R.id.tv_zone) as TextView
		var tv_street: TextView = itemView.findViewById<View>(R.id.tv_street) as TextView
		var tv_time: TextView = itemView.findViewById<View>(R.id.tv_time) as TextView
		var tv_no: TextView = itemView.findViewById<View>(R.id.tv_no) as TextView
		var btn_tow: TextView = itemView.findViewById<View>(R.id.btnTow) as Button

	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val view: View = LayoutInflater.from(parent.context)
			.inflate(R.layout.item_clamped, parent, false)
		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val list: Clamped = mList[position]

		holder.tv_no.text = "${position+1}."

		if (isEven(position)){
			holder.layoutView.setBackgroundColor(Color.parseColor("#11707070"))
		}else{
			holder.layoutView.setBackgroundColor(Color.parseColor("#FFFFFF"))

		}



		if(list.clampedStatus=="TOWED"){
			holder.btn_tow.visibility = View.GONE
		}

		holder.btn_tow.setOnClickListener {

			val builder = AlertDialog.Builder(context)
			builder.setTitle("Tow "+ list.numberPlate)
			builder.setMessage("Confirm towing ${list.numberPlate}")
			builder.setNeutralButton("Tow Now") { dialog, which ->
				dialog.dismiss()
				val formData = listOf(
					"function" to "towVehicle",
					"id" to  list.id.toString(),
				)

				val handler = Handler(context.mainLooper)

				executeRequest(formData, parking,object : CallBack {
					override fun onSuccess(result: String?) {

						handler.post {
							Toast.makeText(context, "Success", Toast.LENGTH_LONG).show()
						}
					}
					override fun onFailure(result: String?) {

						handler.post {
							Toast.makeText(context, "Success", Toast.LENGTH_LONG).show()
						}

					}

				})
			}
			builder.setNegativeButton(android.R.string.no) { dialog, which ->
				dialog.dismiss()
			}

			builder.show()
		}
		holder.tv_number_plate.text = list.numberPlate
		holder.tv_category.text = list.feeDescription
		holder.tv_zone.text = list.zone
		holder.tv_street.text = list.address
		holder.tv_time.text = covertTimeToText(list.dateCreated)

		holder.layoutView.setOnClickListener {

			// Get the latitude and longitude from the list object
			val lat = list.lat
			val lng = list.lng
			val uri = Uri.parse("geo:0,0?q=$lat,$lng")
			val intent = Intent(Intent.ACTION_VIEW, uri)
			intent.setPackage("com.google.android.apps.maps")

			context.startActivity(intent)

		}


	}

	fun isEven(number: Int): Boolean {
		return number % 2 ==0
	}

	private fun calculateProgress(collected: Int, target: Int): Double {
		if(target==0){
			return 0.0
		}
		val progress = (collected.toDouble() / target.toDouble()) * 100
		val decFormat = DecimalFormat("#,##0.00")
		decFormat.roundingMode = RoundingMode.HALF_UP
		return decFormat.format(progress).toDouble()
	}
	private fun formatNumber(number: Int): String {
		val numberFormat = NumberFormat.getInstance(Locale.US)
		return numberFormat.format(number)
	}


	override fun getItemCount(): Int {
		return mList.size
	}
	init {
		this.mList = mList
	}

	fun getName(subCountyID: String): String {
		val nameMap = mapOf(
			"43247" to "Kisumu Central",
			"43251" to "Kisumu East",
			"43246" to "Kisumu West",
			"43248" to "Nyakach",
			"43252" to "Nyando",
			"43249" to "Muhoroni",
			"43245" to "Seme",
			"43253" to "Kisumu Central",
			"43250" to "Kisumu East"
		)
		return nameMap[subCountyID] ?: "Unknown"
	}

	fun covertTimeToText(dataDate: String?): String? {
		var convTime: String? = null
		try {
			val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
			val pasTime: Date = dateFormat.parse(dataDate)
			val nowTime = Date()
			val dateDiff: Long = nowTime.getTime() - pasTime.getTime()
			val detik: Long = TimeUnit.MILLISECONDS.toSeconds(dateDiff)
			val menit: Long = TimeUnit.MILLISECONDS.toMinutes(dateDiff)
			val jam: Long = TimeUnit.MILLISECONDS.toHours(dateDiff)
			val hari: Long = TimeUnit.MILLISECONDS.toDays(dateDiff)
			if (detik < 60) {
				convTime = detik.toString() + " sec ago"
			} else if (menit < 60) {
				convTime = menit.toString() + " min ago"
			} else if (jam < 24) {
				convTime = jam.toString() + " hours ago"
			} else if (hari >= 7) {
				convTime = if (hari > 30) {
					(hari / 30).toString() + " last month"
				} else if (hari > 360) {
					(hari / 360).toString() + " tahun lalu"
				} else {
					(hari / 7).toString() + " last year"
				}
			} else if (hari < 7) {
				convTime = hari.toString() + " yesterday"
			}
		} catch (e: ParseException) {
			e.printStackTrace()
			Log.e("ConvTimeE", e.message.toString())
		}
		return convTime
	}

	private fun toTitleCase(str: String?): String? {
		if (str == null) {
			return null
		}
		var space = true
		val builder = StringBuilder(str)
		val len = builder.length
		for (i in 0 until len) {
			val c = builder[i]
			if (space) {
				if (!Character.isWhitespace(c)) {
					// Convert to title case and switch out of whitespace mode.
					builder.setCharAt(i, Character.toTitleCase(c))
					space = false
				}
			} else if (Character.isWhitespace(c)) {
				space = true
			} else {
				builder.setCharAt(i, Character.toLowerCase(c))
			}
		}
		return builder.toString()
	}
}
