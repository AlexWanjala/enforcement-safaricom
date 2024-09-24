import android.content.Context
import android.content.Intent
import android.net.ParseException
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.aw.forcement.R
import com.aw.passanger.api.getValue
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


/**
 * Created by Alex Boey on 8/1/2016.
 */
class HistoryAdapter(private val context: Context, mList: List<Queries>) :
	RecyclerView.Adapter<HistoryAdapter.ViewHolder?>() {
	private var mList: List<Queries> = ArrayList<Queries>()

	inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		var layoutView: LinearLayout = itemView.findViewById<View>(R.id.layoutView) as LinearLayout
		var tvPhysicalAddress: TextView = itemView.findViewById<View>(R.id.tvPhysicalAddress) as TextView
		var tvStatus: TextView = itemView.findViewById<View>(R.id.tvStatus) as TextView
		var tvNumberPlate: TextView = itemView.findViewById<View>(R.id.tvNumberPlate) as TextView
		var tvNumbering: TextView = itemView.findViewById<View>(R.id.tvNumbering) as TextView
		var tvTime: TextView = itemView.findViewById<View>(R.id.tvTime) as TextView
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val view: View = LayoutInflater.from(parent.context)
			.inflate(R.layout.item, parent, false)
		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val list: Queries = mList[position]

		holder.tvNumberPlate.text = list.whichitem
		holder.tvPhysicalAddress.text = list.addressString.replace("\n", "")
		holder.tvStatus.text = list.currentState
		holder.tvNumbering.text = "${position + 1}."
		holder.tvTime.text = covertTimeToText(list.dateCreated)

		// Set onClickListener to launch Google Maps
		holder.layoutView.setOnClickListener {
			val intent = Intent(
				Intent.ACTION_VIEW,
				Uri.parse("http://maps.google.com/maps?saddr=${getValue(context, "latitude")},${getValue(context, "longitude")}&daddr=${list.latitude},${list.longitude}")
			)
			context.startActivity(intent)
		}

		// Change layoutView background color if position is even
		if (position % 2 == 0) {
			holder.layoutView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
		}
	}


	override fun getItemCount(): Int {
		return mList.size
	}
	init {
		this.mList = mList
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
