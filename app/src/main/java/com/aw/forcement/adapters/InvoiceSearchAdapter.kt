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
import com.aw.forcement.InvoiceDetials
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
class InvoiceSearchAdapter(private val context: Context, mList: List<BillDetails>) :
	RecyclerView.Adapter<InvoiceSearchAdapter.ViewHolder?>() {
	private var mList: List<BillDetails> = ArrayList<BillDetails>()

	inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		var layoutView: ConstraintLayout = itemView.findViewById<View>(R.id.layoutView) as ConstraintLayout
		var tv_bill : TextView = itemView.findViewById<View>(R.id.tv_bill) as TextView
		var tv_phone: TextView = itemView.findViewById<View>(R.id.tv_phone) as TextView
		var tv_des: TextView = itemView.findViewById<View>(R.id.tv_des) as TextView
		var tv_status: TextView = itemView.findViewById<View>(R.id.tv_status) as TextView
		var tv_amount: TextView = itemView.findViewById<View>(R.id.tv_amount) as TextView
		var tv_date: TextView = itemView.findViewById<View>(R.id.tv_date) as TextView
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val view: View = LayoutInflater.from(parent.context)
			.inflate(R.layout.item_invoice, parent, false)
		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val list: BillDetails = mList[position]

		holder.tv_bill.text = list.billNo
		holder.tv_phone.text = list.customerPhoneNumber

		holder.tv_des.text = list.incomeTypeDescription
		holder.tv_date.text = covertTimeToText(list.dateCreated)
		holder.tv_amount.text = "KES "+ list.detailAmount
		holder.tv_status.text =  "PAID KES "+ list.receiptAmount


		holder.layoutView.setOnClickListener {

			context.startActivity(Intent(context,InvoiceDetials::class.java).putExtra("billNo",list.billNo)

			)
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
