import android.app.AlertDialog
import android.content.Context
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
import com.aw.passanger.api.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_cess_payments.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Created by Alex Boey on 8/1/2016.
 */
class TransAdapter(private val context: Context, mList: List<Transactions>) :
	RecyclerView.Adapter<TransAdapter.ViewHolder?>() {
	private var mList: List<Transactions> = ArrayList<Transactions>()

	inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		var layoutView: ConstraintLayout = itemView.findViewById<View>(R.id.layoutView) as ConstraintLayout
		var tvNameTag_: TextView = itemView.findViewById<View>(R.id.tvNameTag_) as TextView
		var tvHeader: TextView = itemView.findViewById<View>(R.id.tvHeader) as TextView
		var tvItem: TextView = itemView.findViewById<View>(R.id.tvItem) as TextView
		var tvDate: TextView = itemView.findViewById<View>(R.id.tvDate) as TextView
		var tvStatus_: TextView = itemView.findViewById<View>(R.id.tvStatus_) as TextView
		var tvVerified: TextView = itemView.findViewById<View>(R.id.tvVerified) as TextView
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val view: View = LayoutInflater.from(parent.context)
			.inflate(R.layout.item, parent, false)
		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val list: Transactions = mList[position]

		if(list.names==null){
			holder.tvNameTag_.text = "#"
		}else{
			holder.tvNameTag_.text = "${list.names[0]}${list.names[2]}"
		}


		holder.tvHeader.text = list.transaction_code
		holder.tvItem.text = list.account_ref + "# "+list.names+ " # "+list.account_from
		holder.tvDate.text = covertTimeToText(list.date)
		holder.tvStatus_.text = "KES "+ list.amount

		if(list.verified){
			holder.tvVerified.text = "Verified"
			holder.tvVerified.setTextColor(Color.parseColor("#269433"))
		}else{
			holder.tvVerified.text = "Unverified"
		}


		holder.layoutView.setOnClickListener {

			val builder = AlertDialog.Builder(context)
			builder.setTitle("${list.names}")
			builder.setMessage("Select the options below")
			builder.setPositiveButton("PRINT") { dialog, which ->
				dialog.dismiss()

				save(context,"transaction_code",list.transaction_code)
				save(context,"amount",list.amount)
				save(context,"phone",list.account_from)
				save(context,"ref",list.account_ref)
				save(context,"names",list.names)
				save(context,"date",list.date)

				if (context is com.aw.forcement.Transactions) {
					(context as com.aw.forcement.Transactions).getReceipt(list.transaction_code)
				}
			}
			builder.setNeutralButton("Verify") { dialog, which ->
				dialog.dismiss()
				if(list.verified){
					Toast.makeText(context,"Already Verified",Toast.LENGTH_LONG).show()
					dialog.dismiss()
					return@setNeutralButton
				}
				Toast.makeText(context,"Verified Success",Toast.LENGTH_LONG).show()
				val formData = listOf(
					"function" to "verifyTransaction",
					"transaction_code" to list.transaction_code,
					"idNo" to getValue(context,"idNo").toString(),
					"deviceId" to getDeviceIdNumber(context)
				)

				executeRequest(formData, biller,object : CallBack {
					override fun onSuccess(result: String?) {
						val response = Gson().fromJson(result, Json4Kotlin_Base::class.java)

						if (context is com.aw.forcement.Transactions) {
							(context as com.aw.forcement.Transactions).getQueries("","")
						}
					}

					override fun onFailure(result: String?) {
						Toast.makeText(context,result,Toast.LENGTH_LONG).show()
					}

				})
			}
			builder.setNegativeButton(android.R.string.no) { dialog, which ->
				dialog.dismiss()
			}

			builder.show()


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
