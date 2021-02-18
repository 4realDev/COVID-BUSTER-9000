package com.co2team.covidbuster.ui.currentroom

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.co2team.covidbuster.model.RoomCo2Data
import java.time.LocalDateTime

class CurrentRoomViewModel : ViewModel() {
    private val currentRoomData = MutableLiveData<RoomCo2Data>()
    val roomData: MutableLiveData<RoomCo2Data> get() = currentRoomData

    private val covidDevicesData = MutableLiveData<Int>()
    val covidDevices: MutableLiveData<Int> get() = covidDevicesData

    var roomName = ""
    var roomId = 1

    fun setNumberOfCovidDevices(count: Int) {
        covidDevicesData.value = count
    }

    fun setRoomData(data: RoomCo2Data) {
        currentRoomData.value = data
    }

    fun getLastUpdated(): LocalDateTime {
        return currentRoomData.value!!.created
    }

    fun clearData() {
        currentRoomData.value = null
    }
}