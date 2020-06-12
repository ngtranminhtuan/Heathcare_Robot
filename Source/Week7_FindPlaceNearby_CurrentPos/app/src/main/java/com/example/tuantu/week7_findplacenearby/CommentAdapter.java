package com.example.tuantu.week7_findplacenearby;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.example.tuantu.week7_findplacenearby.DirectionFinder;
import com.example.tuantu.week7_findplacenearby.DirectionFinderListener;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentAdapter extends ArrayAdapter<String> {

    private int resource;
    public static String titleName = "";
    ArrayList<String> _items = new ArrayList<String>();
    public static Bitmap[] temp;
    Context c;
    static ImageView avatar;
    static TextView userName;
    static TextView cmt;
    static RatingBar cmtRate;

    // public static List<HashMap<String, String>> result;

    public CommentAdapter (Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public CommentAdapter(Context context, int resource, ArrayList<String> items) {
        super(context, resource, items);
        this.resource = resource;
        c = context;
        Place_Detail.k = 0;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;
        if (v == null) {
            LayoutInflater vi;
            vi = ((Activity)c).getLayoutInflater();
            v = vi.inflate(resource, parent, false);
        }

         avatar = (ImageView) v.findViewById(R.id.imageAvatar);
         userName = (TextView) v.findViewById(R.id.textUser);
         cmt = (TextView) v.findViewById(R.id.textComment);
         cmtRate = (RatingBar) v.findViewById(R.id.commentRate);


        try {

            if (temp[position] == null) {
                avatar.setImageResource(R.drawable.p1);
            }
            else
                avatar.setImageBitmap(temp[position]);
            userName.setText(Comment.comments.getJSONObject(position).getString("author_name"));
            cmt.setText(Comment.comments.getJSONObject(position).getString("text"));
            cmtRate.setRating(Float.parseFloat(Comment.comments.getJSONObject(position).getJSONArray("aspects").getJSONObject(0).getString("rating")));
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return v;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public static class loadAvatar extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            try
            {
                URL newurl = new URL(strings[0]);
                HttpURLConnection connection = null;
                connection = (HttpURLConnection) newurl.openConnection();
                connection.connect();
                Bitmap mIcon_val = BitmapFactory.decodeStream(connection.getInputStream());
                return mIcon_val;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Bitmap a) {
            if (Place_Detail.k < Comment.comments.length() - 1) {
                try {
                    CommentAdapter.temp[Place_Detail.k] = a;
                    new loadAvatar().execute("http:" + Comment.comments.getJSONObject(++Place_Detail.k).getString("profile_photo_url"), String.valueOf(++Place_Detail.k));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

