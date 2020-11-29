package com.co2team.covidbuster.service;

import com.co2team.covidbuster.model.RoomCo2Data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ThingsSpeakResponseParseService {

    private static final String TAG = BackendService.class.getSimpleName();

    public List<RoomCo2Data> parseJsonResponse(String jsonResponse, int roomId) throws JSONException {
        List<RoomCo2Data> roomData = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(jsonResponse);

        JSONArray feeds = (JSONArray) jsonObject.get("feeds");
        for (int i = 0; i < feeds.length(); i++) {
            String createdString = (String) feeds.getJSONObject(i).get("created_at");
            String co2ppmString = feeds.getJSONObject(i).get("field" + roomId).toString();
            if(co2ppmString.equals("null")) {
                // This means we have no value for this field and can ignore this entry
                continue;
            }
            int co2ppm = Integer.parseInt(co2ppmString);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
            LocalDateTime createdDate = LocalDateTime.parse(createdString, formatter);
            roomData.add(new RoomCo2Data(co2ppm, createdDate));

        }

        return roomData;
    }
}
