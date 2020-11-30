package com.co2team.covidbuster.ui.roomlist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.ListFragment
import androidx.lifecycle.ViewModelProvider
import com.co2team.covidbuster.util.Constants
import com.co2team.covidbuster.R
import com.co2team.covidbuster.ui.history.HistoryActivity

const val EXTRA_ROOM_ID = "com.co2team.covidbuster.ui.roomlist.ROOMID"
const val EXTRA_ROOM_NAME = "com.co2team.covidbuster.ui.roomlist.ROOMNAME"

class RoomListFragment : ListFragment() {

    companion object {
        fun newInstance() = RoomListFragment()
    }

    private lateinit var viewModel: RoomListViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        listAdapter = RoomListArrayAdapter(this.requireContext(), Constants.allRooms)

        return inflater.inflate(R.layout.room_list_fragment, container, false)
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)
        val intent = Intent(requireActivity().applicationContext, HistoryActivity::class.java)
        intent.putExtra(EXTRA_ROOM_ID, Constants.allRooms[position].id)
        intent.putExtra(EXTRA_ROOM_NAME, roomData[position]!!.name)
        startActivity(intent)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(RoomListViewModel::class.java)
        // TODO: Use the ViewModel
    }

}