package com.co2team.covidbuster

import com.co2team.covidbuster.service.ThingsSpeakResponseParseService
import org.junit.Assert.assertEquals
import org.junit.Test

class ThingsSpeakResponseParseServiceTest {

    private val testResponse = javaClass.getResource("test-response.json")!!.readText()

    @Test
    fun parsing_thingsspeak_response_works() {
        val service = ThingsSpeakResponseParseService()

        val result = service.parseJsonResponse(testResponse, 1)

        assertEquals(432, result[0].co2ppm)
        assertEquals(449, result[1].co2ppm)
    }
}