package com.example.tuantu.week7_findplacenearby;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by TUAN TU on 5/30/2016.
 */
public class BookMark extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);

       /* ArrayList<String> itemStrings = new ArrayList<>();
        for (int i = 0; i < result.size(); i++)
            itemStrings.add("");
        ListView listView = (ListView) findViewById(R.id.listView);
        ListAdapter adapter = new ListAdapter(
                this,
                R.layout.custom_result,
                itemStrings
        );
        listView.setAdapter(adapter);*/
    }

}
