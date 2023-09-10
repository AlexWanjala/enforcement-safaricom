package com.aw.forcement.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aw.forcement.R
import com.loukwn.stagestepbar.StageStepBar
import kotlinx.android.synthetic.main.item.*
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Objects


class AdapterStatus(private val context: Context, private val dataSet: List<Objects>) :
        RecyclerView.Adapter<AdapterStatus.ViewHolder>() {


    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val stageStepBar: StageStepBar
        val tvStatus: TextView
        val tvStatusDes: TextView

        init {
            stageStepBar = view.findViewById<StageStepBar>(R.id.stageStepBar)
            tvStatus = view.findViewById<TextView>(R.id.tvStatus)
            tvStatusDes = view.findViewById<TextView>(R.id.tvStatusDes)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item

        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_status, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = dataSet[position]

      /*  viewHolder.tvStatus.text = item.orderStatus
        viewHolder.tvStatusDes.text = item.status
        viewHolder.stageStepBar.setCurrentState(StageStepBar.State(item.completed.toInt(),0))*/

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
