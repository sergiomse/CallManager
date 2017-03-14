package com.sergiomse.callmanager

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import android.view.ViewGroup
import android.view.LayoutInflater




/**
 * Created by sergiomse@gmail.com.
 */

class NumbersAdapter(var context: Context, var numbers: Array<String>) : RecyclerView.Adapter<NumbersAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val TAG = ViewHolder::class.java.simpleName

        var numberView: TextView = itemView.findViewById(R.id.number)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent!!.context).inflate(R.layout.numbers_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder!!.numberView.text = numbers[position]
    }

    override fun getItemCount(): Int {
        return numbers.count()
    }
}