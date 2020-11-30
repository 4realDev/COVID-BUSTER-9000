package com.co2team.covidbuster.service

import android.util.Log
import okhttp3.*
import org.json.JSONException
import java.io.IOException

class BackendService {
    private val client = OkHttpClient()
    var jsonParser = ThingsSpeakResponseParseService()

    private val TAG = BackendService::class.java.simpleName

    companion object {
        private const val UPDATE_URL = "https://api.thingspeak.com/update?api_key=7G0T8JBRR8M6OAPD&field"
        private const val READ_URL = "https://api.thingspeak.com/channels/1224181/fields/"
    }

    fun uploadCo2Measurement(co2ppm: Int, roomId: Int) {
        val request = Request.Builder().url("${UPDATE_URL}$roomId=$co2ppm").build()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                Log.d(TAG, "Uploading successful!")
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.d(TAG, "Uploading value to backend failed")
            }
        })
    }

    fun readCo2MeasurementsForRoom(roomId: Int, done: OnDataReceivedCallback) {
        val request = Request.Builder().url("${READ_URL}$roomId.json?average=10&round=0&days=1").build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d(TAG, "Error reading!")
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                try {
                    response.body.use { responseBody ->
                        if (!response.isSuccessful || responseBody == null) {
                            Log.d(TAG, "Could not read room data! Response was: $response")
                        } else {
                            val roomCo2Data = jsonParser.parseJsonResponse(responseBody.string(), roomId)
                            done.onSuccess(roomCo2Data)
                        }
                    }
                } catch (e: JSONException) {
                    Log.d(TAG, "Error parsing JSON response!")
                    e.printStackTrace()
                }
            }
        })
    }


}