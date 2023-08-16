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
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.aw.forcement.R
import com.aw.passanger.api.*
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Created by Alex Boey on 8/1/2016.
 */
class UsersAdapter(private val context: Context, mList: List<Users>) :
	RecyclerView.Adapter<UsersAdapter.ViewHolder?>() {
	private var mList: List<Users> = ArrayList<Users>()

	inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		var layoutView: LinearLayout = itemView.findViewById<View>(R.id.layoutView) as LinearLayout
		var nameTag: TextView = itemView.findViewById<View>(R.id.nameTag) as TextView
		var name: TextView = itemView.findViewById<View>(R.id.name) as TextView
		var tv_des: TextView = itemView.findViewById<View>(R.id.tv_des) as TextView

	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val view: View = LayoutInflater.from(parent.context)
			.inflate(R.layout.item_user, parent, false)
		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val list: Users = mList[position]

		holder.name.text = list.names
		holder.nameTag.text = list.names[0].toString() + list.names[1].toString()
		holder.tv_des.text = list.category +" "+ list.subCountyName
		holder.layoutView.setOnClickListener {

			val phoneNumber = list.phoneNumber
			val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
			context.startActivity(intent)

		}

	}

	override fun getItemCount(): Int {
		return mList.size
	}
	init {
		this.mList = mList
	}

	private fun getTimeAgo(time: String): String {

		val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
		val date = simpleDateFormat.parse(time)

		val calendar = Calendar.getInstance()
		calendar.time = date

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
			if (interval == 1) "$interval min ago" else "$interval min ago"
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
