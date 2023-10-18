import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.aw.forcement.R
import com.aw.forcement.ro.AlertRing
import java.text.SimpleDateFormat
import java.util.*


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
		// Using a ternary operator
		// Using a ternary operator
		holder.tv_address.text = if (list.address != null) list.address.replace(
			"old Value",
			""
		) else "No address available"

		holder.tv_logins.text = list.logins
		holder.tv_transactions.text = list.transactions
		holder.tv_inspections.text = list.inspections
		holder.tv_amount.text = list.amount



		holder.tv_time.text = getTimeAgo(list.lastSeen)


		//holder.tv_time.text =list.lastSeen.getTimeAgo()

		holder.layoutView.setOnClickListener {
			context.startActivity(Intent(context,com.aw.forcement.history.MyHistory::class.java).putExtra("bottomBar","hide").putExtra("idNo",list.idNNumber).putExtra("names",list.names))
		}

		holder.call_blue.setOnClickListener {
			/*val phoneNumber = list.phoneNumber
			val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
			context.startActivity(intent)*/
			makeCall("+"+list.phoneNumber)

		}

		holder.bell.setOnClickListener {
			context.startActivity(Intent(context,AlertRing::class.java).putExtra("idNo",list.idNo).putExtra("names",list.names))
		}

	}

	private fun makeCall (phoneNumber: String) {
		if (ContextCompat.checkSelfPermission (context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
			val intent = Intent (Intent.ACTION_CALL, Uri.parse ("tel:$phoneNumber"))
			context.startActivity (intent)
		} else {
			ActivityCompat.requestPermissions (context as Activity, arrayOf (Manifest.permission.CALL_PHONE), 1)
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
