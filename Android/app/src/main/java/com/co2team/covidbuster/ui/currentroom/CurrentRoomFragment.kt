package com.co2team.covidbuster.ui.currentroom

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.co2team.covidbuster.R

class CurrentRoomFragment : Fragment() {

    companion object {
        fun newInstance() = CurrentRoomFragment()
    }

    private lateinit var viewModel: CurrentRoomViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.current_room_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(CurrentRoomViewModel::class.java)
        // TODO: Use the ViewModel
    }

}