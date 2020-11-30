package com.co2team.covidbuster.service

import com.co2team.covidbuster.model.RoomCo2Data
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class ThingsSpeakResponseParseService {

    @Throws(JSONException::class)
    fun parseJsonResponse(jsonResponse: String, roomId: Int): List<RoomCo2Data> {
        val roomData: MutableList<RoomCo2Data> = ArrayList()
        val jsonObject = JSONObject(jsonResponse)

        val feeds = jsonObject["feeds"] as JSONArray
        for (i in 0 until feeds.length()) {
            val createdString = feeds.getJSONObject(i)["created_at"] as String
            val co2ppmString = feeds.getJSONObject(i)["field$roomId"].toString()
            if (co2ppmString == "null") {
                // This means we have no value for this field and can ignore this entry
                continue
            }
            val co2ppm = co2ppmString.toInt()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
            val createdDate = LocalDateTime.parse(createdString, formatter)
            val createdDateLocalTime = createdDate.atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
            roomData.add(RoomCo2Data(co2ppm, createdDateLocalTime))
        }
        return roomData
    }

}