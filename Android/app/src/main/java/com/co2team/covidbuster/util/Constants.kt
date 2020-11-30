package com.co2team.covidbuster.util

import com.co2team.covidbuster.model.RoomData

object Constants {

    /** any ppm below this threshold is safe  */
    const val WARNING_CO2_THRESHOLD = 1000

    /** any ppm above this threshold is dangerous  */
    const val DANGEROUS_CO2_THRESHOLD = 2000

    /** static definition of all room. to be improved in future version */
    val allRooms = arrayOf(
            RoomData("ZL O6.12", 1),
            RoomData("Sihlhof 208", 2),
            RoomData("ZL O6.16", 3))
}