package com.example.tuantu.week7_findplacenearby;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by TUAN TU on 4/24/2016.
 */
public class Comment extends Activity {

    public static JSONArray comments;
    static TextView reviewsTitle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment);
        ListView listView = (ListView) findViewById(R.id.listView2);
        ArrayList<String> temp = new ArrayList<String>();
       for (int i = 0; i < comments.length(); i++)
           temp.add("");
        CommentAdapter adapter = new CommentAdapter(
                this,
                R.layout.custom_comment,
                temp
        );
        listView.setAdapter(adapter);
        reviewsTitle = (TextView) findViewById(R.id.reviews);
        reviewsTitle.setText("Đánh giá về " + CommentAdapter.titleName);

    }


}
