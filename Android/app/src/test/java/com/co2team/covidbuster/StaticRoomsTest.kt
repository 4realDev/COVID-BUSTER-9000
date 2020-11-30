package com.co2team.covidbuster

import com.co2team.covidbuster.util.Utils
import org.junit.Assert.assertEquals
import org.junit.Test

class StaticRoomsTest {

    @Test
    fun static_rooms_have_name() {
        val roomName = Utils.getRoomName(3)

        assertEquals("ZL O6.16", roomName)
    }
}