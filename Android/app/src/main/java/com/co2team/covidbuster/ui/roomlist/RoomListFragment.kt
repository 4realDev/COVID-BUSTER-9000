package com.co2team.covidbuster.ui.roomlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.co2team.covidbuster.R

class RoomListFragment : Fragment() {

    companion object {
        fun newInstance() = RoomListFragment()
    }

    private lateinit var viewModel: RoomListViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.room_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(RoomListViewModel::class.java)
        // TODO: Use the ViewModel
    }

}