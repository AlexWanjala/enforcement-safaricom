import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ParseException
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.aw.forcement.R
import com.aw.forcement.ro.AlertRing
import com.aw.passanger.api.*
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timer


/**
 * Created by Alex Boey on 8/1/2016.
 */
class UsersRoAdapter(private val context: Context, mList: List<Users>) :
	RecyclerView.Adapter<UsersRoAdapter.ViewHolder?>() {
	private var mList: List<Users> = ArrayList<Users>()

	inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		var layoutView: LinearLayout = itemView.findViewById<View>(R.id.layoutView) as LinearLayout
		var tv_name: TextView = itemView.findViewById<View>(R.id.tv_name) as TextView
		var tv_time: TextView = itemView.findViewById<View>(R.id.tv_time) as TextView
		var tv_address: TextView = itemView.findViewById<View>(R.id.tv_address) as TextView
		var bell: ImageView = itemView.findViewById<View>(R.id.bell) as ImageView
		var call_blue: ImageView = itemView.findViewById<View>(R.id.call_blue) as ImageView
		var tv_logins: TextView = itemView.findViewById<View>(R.id.tv_logins) as TextView
		var tv_transactions: TextView = itemView.findViewById<View>(R.id.tv_transactions) as TextView
		var tv_inspections: TextView = itemView.findViewById<View>(R.id.tv_inspections) as TextView
		var tv_amount: TextView = itemView.findViewById<View>(R.id.tv_amount) as TextView

	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val view: View = LayoutInflater.from(parent.context)
			.inflate(R.layout.item_user_ro, parent, false)
		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val list: Users = mList[position]

		val names = list.names.toLowerCase().split(" ").joinToString(" ") { it.capitalize() }

		holder.tv_name.text = names
		holder.tv_address.text = list.address
		holder.tv_logins.text = list.logins
		holder.tv_transactions.text = list.transactions
		holder.tv_inspections.text = list.inspections
		holder.tv_amount.text = list.amount

		//holder.tv_time.text =list.lastSeen.getTimeAgo()

		holder.layoutView.setOnClickListener {
			context.startActivity(Intent(context,com.aw.forcement.history.MyHistory::class.java).putExtra("idNo",list.idNNumber).putExtra("names",list.names))
		}

		holder.call_blue.setOnClickListener {
			val phoneNumber = list.phoneNumber
			val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
			context.startActivity(intent)

		}

		holder.bell.setOnClickListener {
			context.startActivity(Intent(context,AlertRing::class.java).putExtra("idNo",list.idNo).putExtra("names",list.names))
		}

	}

	override fun getItemCount(): Int {
		return mList.size
	}
	init {
		this.mList = mList
	}

	fun Date.getTimeAgo(): String {
		val calendar = Calendar.getInstance()
		calendar.time = this

		val year = calendar.get(Calendar.YEAR)
		val month = calendar.get(Calendar.MONTH)
		val day = calendar.get(Calendar.DAY_OF_MONTH)
		val hour = calendar.get(Calendar.HOUR_OF_DAY)
		val minute = calendar.get(Calendar.MINUTE)

		val currentCalendar = Calendar.getInstance()

		val currentYear = currentCalendar.get(Calendar.YEAR)
		val currentMonth = currentCalendar.get(Calendar.MONTH)
		val currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH)
		val currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY)
		val currentMinute = currentCalendar.get(Calendar.MINUTE)

		return if (year < currentYear ) {
			val interval = currentYear - year
			if (interval == 1) "$interval year ago" else "$interval years ago"
		} else if (month < currentMonth) {
			val interval = currentMonth - month
			if (interval == 1) "$interval month ago" else "$interval months ago"
		} else  if (day < currentDay) {
			val interval = currentDay - day
			if (interval == 1) "$interval day ago" else "$interval days ago"
		} else if (hour < currentHour) {
			val interval = currentHour - hour
			if (interval == 1) "$interval hour ago" else "$interval hours ago"
		} else if (minute < currentMinute) {
			val interval = currentMinute - minute
			if (interval == 1) "$interval minute ago" else "$interval minutes ago"
		} else {
			"a moment ago"
		}
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