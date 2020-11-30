package com.co2team.covidbuster.service

import com.co2team.covidbuster.model.RoomCo2Data

interface OnDataReceivedCallback {
    fun onSuccess(roomCo2Data: List<RoomCo2Data>)

}