import android.content.Context
import android.graphics.Color
import android.net.ParseException
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aw.forcement.R
import com.aw.passanger.api.getValue
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Created by Alex Boey on 8/1/2016.
 */
class SubCountyRevAdapter(private val context: Context, mList: List<SubCountiesRevenue>) :
	RecyclerView.Adapter<SubCountyRevAdapter.ViewHolder?>() {
	private var mList: List<SubCountiesRevenue> = ArrayList<SubCountiesRevenue>()

	inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		var layoutView: LinearLayout = itemView.findViewById<View>(R.id.layoutView) as LinearLayout
		var view_performance: View = itemView.findViewById<View>(R.id.view_performance) as View
		var tv_number: TextView = itemView.findViewById<View>(R.id.tv_number) as TextView
		var tv_name: TextView = itemView.findViewById<View>(R.id.tv_name) as TextView
		var tv_target: TextView = itemView.findViewById<View>(R.id.tv_target) as TextView
		var tv_amount: TextView = itemView.findViewById<View>(R.id.tv_amount) as TextView
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val view: View = LayoutInflater.from(parent.context)
			.inflate(R.layout.item_sub_county_rev, parent, false)
		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val list: SubCountiesRevenue = mList[position]

		if (isEven(position)){
			holder.layoutView.setBackgroundColor(Color.parseColor("#11707070"))

			if(getValue(context,"subCountyID")==list.subCountyID.toString()){
				holder.layoutView.setBackgroundColor(Color.parseColor("#2D0067CF"))
			}
		}else{
			if(getValue(context,"subCountyID")==list.subCountyID.toString()){
				holder.layoutView.setBackgroundColor(Color.parseColor("#2D0067CF"))
			}

		}

		val progress = calculateProgress(list.amountTotal.toDouble(),list.target.toDouble())
		if (progress>= 100){
			holder.view_performance.setBackgroundColor(Color.parseColor("#047A10"))//Above Target
		}

		if (progress >=90 && progress<100){
			holder.view_performance.setBackgroundColor(Color.parseColor("#0067CF"))//Performing 90-100%
		}

		if (progress >80 && progress<90){
			holder.view_performance.setBackgroundColor(Color.parseColor("#FFDF0E"))//Below Target 80-90%
		}

		if (progress <80){
			holder.view_performance.setBackgroundColor(Color.parseColor("#FF0E0E"))//Under Performing below 80%
		}


		if(list.position_rank.toInt() < list.position_rank_previous.toInt()){
			holder.tv_name.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.rising_rank, 0, 0, 0)
		}

		if(list.position_rank.toInt() > list.position_rank_previous.toInt()){
			holder.tv_name.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.dropping_rank, 0, 0, 0)
		}

		if(list.position_rank.toInt() == list.position_rank_previous.toInt()){
			holder.tv_name.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.maintained_rank, 0, 0, 0)
		}


		holder.tv_number.text = list.position_rank
		val nameSub = list.subCountyName

		val names = nameSub.toLowerCase().split(" ").joinToString(" ") { it.capitalize() }
		holder.tv_name.text = names
		holder.tv_target.text = progress.toString() +"%"
		holder.tv_amount.text = "KES "+ formatNumber(list.amountTotal.toInt())

	}

	private fun isEven(number: Int): Boolean {
		return number % 2 ==0
	}

	private fun calculateProgress(collected: Double, target: Double): Double {
		if (target == 0.0) {
			return 0.0
		}

		val progress = (collected / target) * 100
		val decFormat = DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale.US))
		decFormat.roundingMode = RoundingMode.HALF_UP

		// Remove grouping separator and parse the formatted string
		val formattedProgress = decFormat.format(progress)
		val parsedProgress = decFormat.parse(formattedProgress)?.toDouble() ?: 0.0

		return parsedProgress
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
	//Mvita, Changamwe, Kisauni, Jomvu, Nyali and Likoni.

	fun getName(subCountyID: String): String {
		val nameMap = mapOf(
			"43247" to "Awendo Sub-County",
			"43251" to "Kuria East Sub-County.",
			"43246" to "Kuria West Sub-County",
			"43248" to "Nyatike Sub-County",
			"43252" to "Rongo Sub-County",
			"43249" to "Suna East Sub-County",
			"43245" to "Suna West Sub-County",
			"43253" to "Uriri Sub-County.",
			"43250" to "Nyatike Sub-County"
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
