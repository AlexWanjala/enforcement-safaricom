import android.content.Context
import android.net.ParseException
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aw.forcement.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


/**
 * Created by Alex Boey on 8/1/2016.
 */
class CollectionBreakDownAdapter(
	private val context: Context,
	mList: List<CollectionsBreakdown>,
	totalAmount: Double) :
	RecyclerView.Adapter<CollectionBreakDownAdapter.ViewHolder?>() {
	private var mList: List<CollectionsBreakdown> = ArrayList<CollectionsBreakdown>()
	private var totalAmount: Double = 0.0


	inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		var layoutView: LinearLayout = itemView.findViewById<View>(R.id.layoutView) as LinearLayout
		var tvAmout: TextView = itemView.findViewById<View>(R.id.tv_amount) as TextView
		var tvHeader: TextView = itemView.findViewById<View>(R.id.tv_header) as TextView
		var tv_progress: TextView = itemView.findViewById<View>(R.id.tv_progress) as TextView
		var simpleProgressBar: ProgressBar = itemView.findViewById<View>(R.id.simpleProgressBar) as ProgressBar

	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val view: View = LayoutInflater.from(parent.context)
			.inflate(R.layout.item_break_down, parent, false)
		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val list: CollectionsBreakdown = mList[position]


		holder.tvAmout.text ="KES " + list.amount.toString();
		holder.tvHeader.text = list.title
		var progress =(list.amount.toInt()/totalAmount)*100
		progress = progress.toInt().toDouble()
		holder.simpleProgressBar.setProgress(progress.toInt(),true)
		holder.tv_progress.text = progress.toInt().toString()+"%"


	}

	override fun getItemCount(): Int {
		return mList.size
	}
	init {
		this.mList = mList
		this.totalAmount = totalAmount
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
