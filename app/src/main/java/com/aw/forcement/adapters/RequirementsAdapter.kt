import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ParseException
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.aw.forcement.R
import com.aw.forcement.fire.BusinessDetails
import com.aw.forcement.others.ReceiptDetails
import com.aw.forcement.sbp.applications.ApplicationsDetail
import com.aw.passanger.api.getValue
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Created by Alex Boey on 8/1/2016.
 */
class RequirementsAdapter(private val context: Context, mList: List<Requirements>) :
	RecyclerView.Adapter<RequirementsAdapter.ViewHolder?>() {
	private var mList: List<Requirements> = ArrayList<Requirements>()

	inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

		var requirement: TextView = itemView.findViewById<View>(R.id.requirement) as TextView
		var requirementDes: TextView = itemView.findViewById<View>(R.id.requirement_des) as TextView

	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val view: View = LayoutInflater.from(parent.context)
			.inflate(R.layout.item_requirements, parent, false)
		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val list: Requirements = mList[position]



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
