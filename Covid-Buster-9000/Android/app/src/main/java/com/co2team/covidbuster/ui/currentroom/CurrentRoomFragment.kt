package com.co2team.covidbuster.ui.currentroom

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.co2team.covidbuster.R
import com.co2team.covidbuster.model.RoomCo2Data
import com.co2team.covidbuster.ui.history.HistoryActivity
import com.co2team.covidbuster.ui.roomlist.EXTRA_ROOM_ID
import com.co2team.covidbuster.ui.roomlist.EXTRA_ROOM_NAME
import com.co2team.covidbuster.util.Constants.DANGEROUS_CO2_THRESHOLD
import com.co2team.covidbuster.util.Constants.WARNING_CO2_THRESHOLD


class CurrentRoomFragment : Fragment() {

    companion object {
        fun newInstance() = CurrentRoomFragment()
    }

    private lateinit var historyButton: Button
    private lateinit var co2Label: TextView
    private lateinit var roomLabel: TextView
    private lateinit var ppmLabel: TextView
    private lateinit var numberOfDevicesLabel: TextView
    private lateinit var explanationLabel: TextView
    private lateinit var statusImg: ImageView
    private lateinit var activeCovidAppsCardView: CardView
    private lateinit var currentCO2CardView: CardView

    private val viewModel: CurrentRoomViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.current_room_fragment, container, false)
        historyButton = view.findViewById(R.id.history_fragment_button)
        co2Label = view.findViewById(R.id.labelCurrentCo2)
        ppmLabel = view.findViewById(R.id.labelppm)
        numberOfDevicesLabel = view.findViewById(R.id.numberOfCovidApps)
        roomLabel = view.findViewById(R.id.labelRoomName)
        explanationLabel = view.findViewById(R.id.labelExplanation)
        statusImg = view.findViewById(R.id.imgSafetyStatus)
        activeCovidAppsCardView = view.findViewById(R.id.active_covid_apps_cardview)
        currentCO2CardView = view.findViewById(R.id.current_co2_cardview)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setInitialState()

        viewModel.roomData.observe(viewLifecycleOwner, { roomData ->
            if (roomData != null) {
                updateRoomData(roomData)
            } else {
                setInitialState()
            }
        })

        viewModel.covidDevices.observe(viewLifecycleOwner, { numberOfDevices ->
            numberOfDevicesLabel.text = numberOfDevices.toString()
        })

        historyButton.setOnClickListener {
            val intent = Intent(requireActivity().applicationContext, HistoryActivity::class.java)
            intent.putExtra(EXTRA_ROOM_ID, viewModel.roomId)
            intent.putExtra(EXTRA_ROOM_NAME, viewModel.roomName)
            startActivity(intent)
        }
    }

    private fun setInitialState() {
        explanationLabel.text = getString(R.string.current_room_fragment_no_room_found)
        statusImg.setImageResource(R.drawable.ic_room_not_found)
        historyButton.visibility = View.GONE
        currentCO2CardView.visibility = View.GONE
        roomLabel.text = ""
    }

    private fun updateRoomData(roomData: RoomCo2Data) {
        ppmLabel.text = roomData.co2ppm.toString()
        roomLabel.text = viewModel.roomName
        currentCO2CardView.visibility = View.VISIBLE
        historyButton.visibility = View.VISIBLE

        when {
            roomData.co2ppm < WARNING_CO2_THRESHOLD -> {
                statusImg.setImageResource(R.drawable.ic_safety_status_safe)
                explanationLabel.text = getString(R.string.current_room_fragment_safe_to_stay_text)
            }
            roomData.co2ppm > DANGEROUS_CO2_THRESHOLD -> {
                statusImg.setImageResource(R.drawable.ic_safety_status_dangerous)
                explanationLabel.text = getString(R.string.current_room_fragment_danger_text)
            }
            else -> {
                statusImg.setImageResource(R.drawable.ic_safety_status_warning)
                explanationLabel.text = getString(R.string.current_room_fragment_warning_text)
            }
        }
    }
}