import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ParseException
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.aw.forcement.R
import com.aw.forcement.others.TransactionsResults
import com.aw.passanger.api.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_cess_payments.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Created by Alex Boey on 8/1/2016.
 */
class TransSearchAdapter(private val context: Context, mList: List<Transactions>) :
	RecyclerView.Adapter<TransSearchAdapter.ViewHolder?>() {
	private var mList: List<Transactions> = ArrayList<Transactions>()

	inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		var layoutView: ConstraintLayout = itemView.findViewById<View>(R.id.layoutView) as ConstraintLayout
		var tv_tag : TextView = itemView.findViewById<View>(R.id.tv_tag) as TextView
		var tv_name: TextView = itemView.findViewById<View>(R.id.tv_name) as TextView
		var tv_code: TextView = itemView.findViewById<View>(R.id.tv_code) as TextView
		var tv_item: TextView = itemView.findViewById<View>(R.id.tv_item) as TextView
		var tv_status: TextView = itemView.findViewById<View>(R.id.tv_status) as TextView
		var tv_amount: TextView = itemView.findViewById<View>(R.id.tv_amount) as TextView
		var tv_date: TextView = itemView.findViewById<View>(R.id.tv_date) as TextView
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val view: View = LayoutInflater.from(parent.context)
			.inflate(R.layout.item_search_transaction, parent, false)
		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val list: Transactions = mList[position]

		if(list.names==null){
			holder.tv_tag.text = "#"
		}else{
			holder.tv_tag.text = "${list.names[0]}${list.names[2]}"
		}


		holder.tv_name.text = list.names
		holder.tv_code.text = list.transaction_code
		holder.tv_item.text = list.account_ref
		holder.tv_date.text = covertTimeToText(list.date)
		holder.tv_amount.text = "KES "+ list.amount

		if(!list.verified){
			holder.tv_status.setTextColor(Color.parseColor("#b30000"))
			holder.tv_status.text = "Receipt has not been inspected"
		}

		holder.layoutView.setOnClickListener {

			save(context,"transaction_code",list.transaction_code)
			save(context,"amount",list.amount)
			save(context,"ref",list.account_ref)
			save(context,"payer_names",list.names)
			save(context,"payer_phone",list.names)


			context.startActivity(Intent(context,com.aw.forcement.others.ReceiptDetails::class.java).putExtra("transaction_code",list.transaction_code).putExtra("verified",list.verified)
				.putExtra("amount",list.amount))
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
