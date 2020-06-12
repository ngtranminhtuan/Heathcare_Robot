package com.example.tuantu.week7_findplacenearby;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by TUAN TU on 4/23/2016.
 */
public class placeResult extends Activity{

    public static List<HashMap<String, String>> result;
    ImageButton findPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);

        ArrayList<String> itemStrings = new ArrayList<>();
        if (ListAdapter.flag == 0) {
            for (int i = 0; i < result.size(); i++)
                itemStrings.add("");
        }
        else
        {
            for (int i = 0; i < Life.result.size(); i++)
                itemStrings.add("");
        }
        ListView listView = (ListView) findViewById(R.id.listView);
        ListAdapter adapter = new ListAdapter(
                this,
                R.layout.custom_result,
                itemStrings
        );
        listView.setAdapter(adapter);

    }
}
