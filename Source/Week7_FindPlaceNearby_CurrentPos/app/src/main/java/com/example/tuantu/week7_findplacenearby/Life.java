package com.example.tuantu.week7_findplacenearby;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by TUAN TU on 5/29/2016.
 */
public class Life extends AppCompatActivity {
    ImageButton hospital;
    ImageButton atm;
    ImageButton bus;
    ImageButton restaurant;
    ImageButton bookmark;
    ImageButton trick;
    public static List<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.life);
        //result = new List<HashMap<String, String>>();
        hospital = (ImageButton)findViewById(R.id.hospital);
        atm = (ImageButton)findViewById(R.id.atm);
        bus = (ImageButton)findViewById(R.id.bus);
        restaurant = (ImageButton)findViewById(R.id.restaurant);
        bookmark = (ImageButton)findViewById(R.id.bookmark);
        trick = (ImageButton)findViewById(R.id.trick);

        hospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapsActivity.placeType = "hospital";
                MapsActivity.flag = 1;
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);

            }
        });

        atm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapsActivity.placeType = "atm";
                MapsActivity.flag = 1;
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);

            }
        });

        bus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapsActivity.placeType = "bus_station";
                MapsActivity.flag = 1;
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);

            }
        });

        restaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapsActivity.placeType = "restaurant";
                MapsActivity.flag = 1;
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);

            }
        });

        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListAdapter.flag = 1;
                MapsActivity.PlaceDb.PlaceDbHelper PlaceDbHelper = new  MapsActivity.PlaceDb.PlaceDbHelper(getApplicationContext());
                SQLiteDatabase placeDb = PlaceDbHelper.getWritableDatabase();
                String[] projection2 = {
                        MapsActivity.PlaceDb.PlaceDes._ID,
                        MapsActivity.PlaceDb.PlaceDes.NAME,
                        MapsActivity.PlaceDb.PlaceDes.ADDR,
                        MapsActivity.PlaceDb.PlaceDes.VICINITY,
                        MapsActivity.PlaceDb.PlaceDes.LONGITUDE,
                        MapsActivity.PlaceDb.PlaceDes.LATITUDE,
                        MapsActivity.PlaceDb.PlaceDes.PLACEID};

                Cursor placeCursor = placeDb.query(
                        MapsActivity.PlaceDb.PlaceDes.TABLE_NAME,  // The table to query
                        projection2,                               // The columns to return
                        null,                                // The columns for the WHERE clause
                        null,                            // The values for the WHERE clause
                        null,                                     // don't group the rows
                        null,                                     // don't filter by row groups
                        null                                // The sort order
                );

                placeCursor.moveToFirst();
                for (int i = 0; i < placeCursor.getCount(); i++)
                {
                    HashMap<String, String> temp = new HashMap<String, String>();
                    try {
                        //int ts = placeCursor.getColumnIndex(MapsActivity.PlaceDb.PlaceDes.ADDR);
                        //temp.put("address", placeCursor.getString(placeCursor.getColumnIndex(MapsActivity.PlaceDb.PlaceDes.ADDR)));
                        temp.put("place_name", placeCursor.getString(placeCursor.getColumnIndex(MapsActivity.PlaceDb.PlaceDes.NAME)));
                        temp.put("address", placeCursor.getString(placeCursor.getColumnIndex(MapsActivity.PlaceDb.PlaceDes.ADDR)));
                        temp.put("vinicity", placeCursor.getString(placeCursor.getColumnIndex(MapsActivity.PlaceDb.PlaceDes.VICINITY)));
                        temp.put("lat", placeCursor.getString(placeCursor.getColumnIndex(MapsActivity.PlaceDb.PlaceDes.LATITUDE)));
                        temp.put("lng", placeCursor.getString(placeCursor.getColumnIndex(MapsActivity.PlaceDb.PlaceDes.LONGITUDE)));
                        temp.put("place_id", placeCursor.getString(placeCursor.getColumnIndex(MapsActivity.PlaceDb.PlaceDes.PLACEID)));
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    result.add(temp);
                    placeCursor.moveToNext();
                }
                    //placeResult.result.get(i)
                    Intent intent = new Intent(getApplicationContext(), placeResult.class);
                    startActivity(intent);
            }
        });

        trick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
    @Override
    protected  void onResume()
    {
        super.onResume();
        ListAdapter.flag = 0;
        MapsActivity.flag = 0;
        Life.result.clear();
        MapsActivity.placeType = "restaurant";
    }

}
