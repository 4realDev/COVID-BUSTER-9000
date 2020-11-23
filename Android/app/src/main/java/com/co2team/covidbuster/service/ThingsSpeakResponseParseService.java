package com.co2team.covidbuster.service;

import android.annotation.SuppressLint;
import android.util.Log;

import com.co2team.covidbuster.model.RoomCo2Data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ThingsSpeakResponseParseService {

    private static final String TAG = BackendService.class.getSimpleName();

    public List<RoomCo2Data> parseJsonResponse(String jsonResponse, int roomId) throws JSONException {
        List<RoomCo2Data> roomData = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(jsonResponse);

        JSONArray feeds = (JSONArray) jsonObject.get("feeds");
        for (int i = 0; i < feeds.length(); i++) {
            String createdString = (String) feeds.getJSONObject(i).get("created_at");
            String co2ppmString = (String) feeds.getJSONObject(i).get("field" + roomId);
            int co2ppm = Integer.parseInt(co2ppmString);

            @SuppressLint("SimpleDateFormat")
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

            try {
                Date createdDate = dateFormat.parse(createdString);
                roomData.add(new RoomCo2Data(co2ppm, createdDate));
            } catch (ParseException e) {
                Log.d(TAG, "Unexpected error while parsing this string to date: " + createdString);
                e.printStackTrace();
            }

        }

        return roomData;
    }
}
