package com.aw.forcement.adapters

import Streams
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.aw.forcement.R
import com.aw.forcement.ro.TransactionsRo


class StreamAdapter(private val context: Context, private val dataSet: List<Streams>) :
        RecyclerView.Adapter<StreamAdapter.ViewHolder>() {

    var selectedPosition = -1
    private var mSelectedItem = -1
    private var lastCheckedRB: RadioButton? = null



    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val radio_1: RadioButton


        init {
            // Define click listener for the ViewHolder's View.
            radio_1 = view.findViewById(R.id.radio_1)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item

        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.radio_group, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = dataSet[position]

        //Load the 1st item
        if(position==0){
            if (context is TransactionsRo) {
                //(context as MainActivity).initRecyclerView(item.packageItems)
                //Check the 1st item
                viewHolder.radio_1.isChecked = true
                if (lastCheckedRB != null) {
                    lastCheckedRB?.isChecked = false
                }
                lastCheckedRB = viewHolder.radio_1

            }
        }
            viewHolder.radio_1.text = item.incomeTypeDescription.toString().lowercase().capitalize()
        viewHolder.radio_1.setOnClickListener {
            if (lastCheckedRB != null) {
                lastCheckedRB?.isChecked = false
            }
            //store the clicked radiobutton
            lastCheckedRB = viewHolder.radio_1

        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
