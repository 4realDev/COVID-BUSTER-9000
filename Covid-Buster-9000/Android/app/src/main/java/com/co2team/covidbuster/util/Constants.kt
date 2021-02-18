package com.co2team.covidbuster.util

import com.co2team.covidbuster.model.RoomData

object Constants {

    const val SWISS_COVID_APP_ID = "0000fd6f-0000-1000-8000-00805f9b34fb"

    const val SERVICE_NAME_COVID_BUSTER_PERIPHERAL = "COVID BUSTER PERIPHERAL"

    const val NEED_TO_VENTILATE_NOTIFICATION_ID = 0

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