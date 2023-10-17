
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.ParseException
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aw.forcement.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Created by Alex Boey on 8/1/2016.
 */
class OverviewAdapter(private val context: Context, mList: List<Overview>) :
	RecyclerView.Adapter<OverviewAdapter.ViewHolder?>() {
	private var mList: List<Overview> = ArrayList<Overview>()

	inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		var layout: FrameLayout = itemView.findViewById<View>(R.id.frame_layout) as FrameLayout
		var image_icon: ImageView = itemView.findViewById<View>(R.id.image_icon) as ImageView
		var tv_icon: ImageView = itemView.findViewById<View>(R.id.tv_icon) as ImageView
		var tv_header: TextView = itemView.findViewById<View>(R.id.tv_header) as TextView
		var tv_amount: TextView = itemView.findViewById<View>(R.id.tv_amount) as TextView
		var tv_difference: TextView = itemView.findViewById<View>(R.id.tv_difference) as TextView
		var tv_message: TextView = itemView.findViewById<View>(R.id.tv_message) as TextView
		var message2: TextView = itemView.findViewById<View>(R.id.message2) as TextView
		var tv_percentage: TextView = itemView.findViewById<View>(R.id.tv_percentage) as TextView

	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val view: View = LayoutInflater.from(parent.context)
			.inflate(R.layout.item_overview, parent, false)
		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val list: Overview = mList[position]

		try {



			if(list.message2.isEmpty()){
				holder.message2.visibility = View.GONE
			}else{
				holder.message2.text = list.message2
			}

			//list.units
			holder.tv_header.text = list.item
			holder.tv_amount.text =list.units+" "+list.today

			holder.tv_message.text = list.message
			if(list.difference.replace (",", "").toInt()<0){
				holder.image_icon.setImageResource (R.drawable.arrow_down);
			}else{
				holder.image_icon.setImageResource (R.drawable.arrow_up);
			}

			if(position==0){
				holder.tv_difference.text = list.units+" "+list.difference.toString()

				holder.tv_percentage.text = list.percentage

				if(list.message2=="Under Performing"){
					holder.layout.setBackgroundResource(R.drawable.bg_red)
					val colorStateList = ColorStateList.valueOf(Color.parseColor("#FFF700"))
					holder.message2.backgroundTintList = colorStateList

				}
				else if(list.message2=="You are Below Average"){

					holder.layout.setBackgroundResource(R.drawable.bg_orange)
					val colorStateList = ColorStateList.valueOf(Color.parseColor("#F44242"))
					holder.message2.backgroundTintList = colorStateList
					holder.message2.setTextColor(Color.parseColor("#FFFFFF"))
					holder.tv_header.setTextColor(Color.parseColor("#2B2F34"))
					holder.tv_amount.setTextColor(Color.parseColor("#2B2F34"))
					holder.tv_percentage.setTextColor(Color.parseColor("#2B2F34"))
					holder.tv_difference.setTextColor(Color.parseColor("#2B2F34"))
					holder.tv_message.setTextColor(Color.parseColor("#2B2F34"))

				}
				else if(list.message2=="Performing"){

					holder.layout.setBackgroundResource(R.drawable.bg_blue)
					val colorStateList = ColorStateList.valueOf(Color.parseColor("#FFF700"))
					holder.message2.backgroundTintList = colorStateList
					holder.message2.setTextColor(Color.parseColor("#040035"))

				}
				else if(list.message2=="Top Performer"){

					holder.layout.setBackgroundResource(R.drawable.bg_blue)
					val colorStateList = ColorStateList.valueOf(Color.parseColor("#FFF700"))
					holder.message2.backgroundTintList = colorStateList
					holder.message2.setTextColor(Color.parseColor("#040035"))

				}
				else{
					holder.layout.setBackgroundResource(R.drawable.bg_blue)
				}

				holder.tv_icon.setImageResource(R.drawable.dollar)
			}
			if(position==1){
				holder.tv_difference.text =list.units +" "+ list.difference.toString()
				holder.layout.setBackgroundResource(R.drawable.bg_red)
				holder.tv_icon.setImageResource(R.drawable.inspection)
			}
			if(position==2){
				holder.tv_difference.text =list.units +" "+ list.difference.toString()
				holder.layout.setBackgroundResource(R.drawable.bg_green)
				holder.tv_icon.setImageResource(R.drawable.customers)
			}
			if(position==3){
				holder.tv_difference.text = list.units +" "+ list.difference.toString()
				holder.layout.setBackgroundResource(R.drawable.bg_dark_green)
				holder.tv_icon.setImageResource(R.drawable.trophy)
			}
			if(position==4){
				holder.tv_difference.text = list.units +" "+ list.difference.toString()
				holder.layout.setBackgroundResource(R.drawable.bg_orange)
				holder.tv_icon.setImageResource(R.drawable.trophy)
			}

		}catch (ex:NullPointerException ){

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
