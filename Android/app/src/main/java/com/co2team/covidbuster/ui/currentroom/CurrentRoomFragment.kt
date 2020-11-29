package com.co2team.covidbuster.ui.currentroom

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.co2team.covidbuster.R
import com.co2team.covidbuster.ui.history.HistoryActivity


class CurrentRoomFragment : Fragment() {

    companion object {
        fun newInstance() = CurrentRoomFragment()
    }

    private lateinit var historyButton: Button
    private lateinit var co2Label: TextView
    private lateinit var statusImg: ImageView

    private val viewModel: CurrentRoomViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.current_room_fragment, container, false)
        historyButton = view.findViewById(R.id.history_fragment_button)
        co2Label = view.findViewById(R.id.labelCurrentCo2)
        statusImg = view.findViewById(R.id.imgSafetyStatus)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.roomData.observe(viewLifecycleOwner, { roomData ->
            co2Label.text = getString(R.string.current_room_fragment_co2_level_label,roomData.co2ppm.toString())
            when {
                roomData.co2ppm < 350 -> {
                    statusImg.setImageResource(R.drawable.safe)
                }
                roomData.co2ppm > 500 -> {
                    statusImg.setImageResource(R.drawable.danger)
                }
                else -> {
                    statusImg.setImageResource(R.drawable.warning)
                }
            }
        })

        historyButton.setOnClickListener {
            val intent = Intent(context, HistoryActivity::class.java)
            startActivity(intent)
        }
    }
}