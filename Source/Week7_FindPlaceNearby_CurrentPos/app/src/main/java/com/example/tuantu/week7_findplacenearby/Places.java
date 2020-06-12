package com.example.tuantu.week7_findplacenearby;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Places {

    public List<HashMap<String, String>> parse(JSONObject jsonObject) {
        JSONArray jsonArray = null;
        JSONObject temp = null;
        try {
            if (PlacesDisplayTask.isDetails == 1)
            {
                jsonArray = new JSONArray();
                temp = jsonObject.getJSONObject("result");
                jsonArray.put(temp);
            }
            else jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return getPlaces(jsonArray);
    }

    private List<HashMap<String, String>> getPlaces(JSONArray jsonArray) {
        int placesCount = jsonArray.length();
        List<HashMap<String, String>> placesList = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> placeMap = null;

        for (int i = 0; i < placesCount; i++) {
            try {
                placeMap = getPlace((JSONObject) jsonArray.get(i));
                placesList.add(placeMap);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return placesList;
    }

    private HashMap<String, String> getPlace(JSONObject googlePlaceJson) {
        HashMap<String, String> googlePlaceMap = new HashMap<String, String>();
        String placeName = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longitude = "";
        String reference = "";
        String addr = "";
        String placeId = "";
        String open_now = "";
        String weekday_text = "";
        String rating = "";
        String phone_number = "";
        String url = "";



        if (PlacesDisplayTask.isDetails == 1)
        {
            Place_Detail.ob = googlePlaceJson;
        }

        try {
            if (!googlePlaceJson.isNull("name")) {
                placeName = googlePlaceJson.getString("name");
            }
            if (!googlePlaceJson.isNull("vicinity")) {
                vicinity = googlePlaceJson.getString("vicinity");
            }
            if (! googlePlaceJson.isNull("formatted_address"))
            {
                addr = googlePlaceJson.getString("formatted_address");
            }

            if (! googlePlaceJson.isNull("opening_hours") && PlacesDisplayTask.isDetails == 1)
            {
                open_now = googlePlaceJson.getJSONObject("opening_hours").getString("open_now");
               JSONArray temp = googlePlaceJson.getJSONObject("opening_hours").getJSONArray("weekday_text");


                for (int i = 0; i < temp.length(); i++)
                    weekday_text  += temp.getString(i)+"\n";
                    //String temp1 = temp.get(i).toString();


            }
            if (! googlePlaceJson.isNull("formatted_phone_number"))
            {
                phone_number = googlePlaceJson.getString("formatted_phone_number");
            }

            if (! googlePlaceJson.isNull("rating"))
            {
                rating = googlePlaceJson.getString("rating");
            }
            if (! googlePlaceJson.isNull("url"))
            {
                url = googlePlaceJson.getString("url");
            }

            latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");
            reference = googlePlaceJson.getString("reference");
            placeId = googlePlaceJson.getString("place_id");

            googlePlaceMap.put("place_name", placeName);
            googlePlaceMap.put("vicinity", vicinity);
            googlePlaceMap.put("address", addr);
            googlePlaceMap.put("lat", latitude);
            googlePlaceMap.put("place_id", placeId);
            googlePlaceMap.put("lng", longitude);
            googlePlaceMap.put("reference", reference);
            googlePlaceMap.put("open_now", open_now);
            googlePlaceMap.put("weekday_text", weekday_text);
            googlePlaceMap.put("rating", rating);
            googlePlaceMap.put("phone_number", phone_number);
            googlePlaceMap.put("url", url);
            Place_Detail.result = googlePlaceMap;
            if (!googlePlaceJson.isNull("reviews"))
            Comment.comments = googlePlaceJson.getJSONArray("reviews");


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return googlePlaceMap;
    }
}