package com.co2team.covidbuster.ui.roomlist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.ListView
import androidx.fragment.app.ListFragment
import androidx.lifecycle.ViewModelProvider
import com.co2team.covidbuster.R
import com.co2team.covidbuster.model.RoomData
import com.co2team.covidbuster.ui.history.HistoryActivity

const val EXTRA_ROOM_ID = "com.co2team.covidbuster.ui.roomlist.ROOMID"
const val EXTRA_ROOM_NAME = "com.co2team.covidbuster.ui.roomlist.ROOMNAME"

class RoomListFragment : ListFragment() {

    companion object {
        fun newInstance() = RoomListFragment()
    }

    private lateinit var viewModel: RoomListViewModel
    private var roomData = arrayOfNulls<RoomData>(3)


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        roomData[0] = RoomData("ZL O6.12", 1)
        roomData[1] = RoomData("Sihlhof 208", 2)
        roomData[2] = RoomData("ZL O6.16", 3)

        val adapter: ListAdapter = RoomListArrayAdapter(this.requireContext(), roomData)
        listAdapter = adapter

        return inflater.inflate(R.layout.room_list_fragment, container, false)
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)
        // TODO: Open history view here
        val intent = Intent(requireActivity().applicationContext, HistoryActivity::class.java)
        intent.putExtra(EXTRA_ROOM_ID, roomData[position]!!.id)
        intent.putExtra(EXTRA_ROOM_NAME, roomData[position]!!.name)
        startActivity(intent)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(RoomListViewModel::class.java)
        // TODO: Use the ViewModel
    }

}