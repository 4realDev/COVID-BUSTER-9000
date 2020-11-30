package com.co2team.covidbuster.ui.currentroom

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.co2team.covidbuster.model.RoomCo2Data
import java.time.LocalDateTime

class CurrentRoomViewModel : ViewModel() {
    private val currentRoomData = MutableLiveData<RoomCo2Data>()
    val roomData: MutableLiveData<RoomCo2Data> get() = currentRoomData

    private val currentRoom = MutableLiveData<String>()
    val roomName: MutableLiveData<String> get() = currentRoom

    fun setRoomName(name: String) {
        roomName.value = name
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