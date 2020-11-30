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
import com.co2team.covidbuster.Constants.DANGEROUS_CO2_THRESHOLD
import com.co2team.covidbuster.Constants.WARNING_CO2_THRESHOLD
import com.co2team.covidbuster.R
import com.co2team.covidbuster.ui.history.HistoryActivity


class CurrentRoomFragment : Fragment() {

    companion object {
        fun newInstance() = CurrentRoomFragment()
    }

    private lateinit var historyButton: Button
    private lateinit var co2Label: TextView
    private lateinit var statusLabel: TextView
    private lateinit var explanationLabel: TextView
    private lateinit var statusImg: ImageView

    private val viewModel: CurrentRoomViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.current_room_fragment, container, false)
        historyButton = view.findViewById(R.id.history_fragment_button)
        co2Label = view.findViewById(R.id.labelCurrentCo2)
        statusLabel = view.findViewById(R.id.labelStatus)
        explanationLabel = view.findViewById(R.id.labelExplanation)
        statusImg = view.findViewById(R.id.imgSafetyStatus)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        statusLabel.text = getString(R.string.current_room_fragment_no_room_found)
        statusImg.setImageResource(R.drawable.ic_not_found)

        viewModel.roomData.observe(viewLifecycleOwner, { roomData ->
            co2Label.text = getString(R.string.current_room_fragment_co2_level_label,roomData.co2ppm.toString())
            when {
                roomData.co2ppm < WARNING_CO2_THRESHOLD -> {
                    statusImg.setImageResource(R.drawable.safe)
                    statusLabel.text = getString(R.string.current_room_fragment_safe)
                    explanationLabel.text = "ℹ️ This room is safe to stay in :)"
                }
                roomData.co2ppm > DANGEROUS_CO2_THRESHOLD -> {
                    statusImg.setImageResource(R.drawable.danger)
                    statusLabel.text = getString(R.string.current_room_fragment_dangerous)
                    explanationLabel.text = "ℹ️ Please ventilate this room immediately! The measured co2 levels indicate, that there was no exchange of fresh air inside this room."
                }
                else -> {
                    statusImg.setImageResource(R.drawable.warning)
                    statusLabel.text = getString(R.string.current_room_fragment_warning)
                    explanationLabel.text = "ℹ️ The air is getting thick in here. Consider ventilating this room to keep the infection rate low."
                }
            }
        })

        historyButton.setOnClickListener {
            val intent = Intent(context, HistoryActivity::class.java)
            startActivity(intent)
        }
    }
}