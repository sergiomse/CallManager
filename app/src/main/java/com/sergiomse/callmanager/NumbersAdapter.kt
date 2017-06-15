package com.sergiomse.callmanager

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.ImageView
import com.sergiomse.callmanager.model.NumberEntry
import com.sergiomse.callmanager.model.NumberType


/**
 * Created by sergiomse@gmail.com.
 */

class NumbersAdapter(var context: Context, var numbers: List<NumberEntry>) : RecyclerView.Adapter<NumbersAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val TAG = ViewHolder::class.java.simpleName

        var numberIcon: ImageView = itemView.findViewById(R.id.iconNumber)
        var contactIcon: ImageView = itemView.findViewById(R.id.iconContact)
        var numberView: TextView = itemView.findViewById(R.id.number)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent!!.context).inflate(R.layout.numbers_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        when(numbers[position].type) {
            NumberType.NUMBER -> {
                holder!!.contactIcon.visibility = View.GONE
                holder.numberView.text = numbers[position].number
            }

            NumberType.CONTACT -> {
                holder!!.numberIcon.visibility = View.GONE
                holder.numberView.text = numbers[position].name
            }
            else -> {
            }
        }

    }

    override fun getItemCount(): Int {
        return numbers.count()
    }
}