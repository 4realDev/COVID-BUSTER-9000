package com.co2team.covidbuster.ui.currentroom

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.co2team.covidbuster.R
import com.co2team.covidbuster.ui.history.HistoryActivity



class CurrentRoomFragment : Fragment() {

    companion object {
        fun newInstance() = CurrentRoomFragment()
    }

    private lateinit var viewModel: CurrentRoomViewModel
    private lateinit var historyButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.current_room_fragment, container, false)
        historyButton = view.findViewById<Button>(R.id.history_fragment_button)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(CurrentRoomViewModel::class.java)
        // TODO: Use the ViewModel
        historyButton.setOnClickListener {
            val intent = Intent(context, HistoryActivity::class.java)
            startActivity(intent)
        }
    }
}