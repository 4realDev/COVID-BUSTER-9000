package com.co2team.covidbuster.util

class Utils {

    companion object {
        fun getRoomName(id: Int): String {
            return Constants.allRooms.find{ it.id == id }!!.name
        }
    }
}