package com.co2team.covidbuster.ui.roomlist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.co2team.covidbuster.R
import com.co2team.covidbuster.model.RoomData

class RoomListArrayAdapter(private val context: Context,
                           private val dataSource: Array<RoomData?>) : BaseAdapter() {

    override fun getCount(): Int {
        return dataSource.size
    }

    override fun getItem(itemId: Int): Any {
        return dataSource[itemId]!!
    }

    override fun getItemId(itemId: Int): Long {
        return dataSource[itemId]!!.id.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var rowView = convertView
        if (convertView == null) {
            val vi = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            rowView = vi.inflate(R.layout.rowlayout, parent, false)
        }

        val textView = rowView!!.findViewById<TextView>(R.id.text1)
        val room = getItem(position) as RoomData

        textView.text = room.name

        return rowView
    }
}