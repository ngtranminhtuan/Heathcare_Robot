package com.example.tuantu.week7_findplacenearby;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.media.Rating;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

//import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class Place_Detail extends AppCompatActivity {
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    static Context c = null;
    static Timer timer = null;
    static int k = 0;
    public static int isThread = 0;
    public static String placeID = ""; //retrieve place id
    public static JSONObject ob;
    //private static final Integer[] IMAGES= {R.drawable.pic1,R.drawable.pic2,R.drawable.pic3};
    private static ArrayList<String> photoRefer = new ArrayList<String>();
    public static HashMap<String, String> result;
    static TextView placeName = null;
    static TextView rating = null;
    static TextView commentCount = null;
    static TextView address = null;
    static TextView phone_number = null;
    static TextView open_time = null;
    static TextView calendar = null;
    static RatingBar rateBar = null;
    LinearLayout comment = null;
    LinearLayout phoneNumber = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.detail_place);

       setContentView(R.layout.detail_place);
        //GetPlaceDetails to get Photo
        isThread = 0;
        c = this;
        placeName = (TextView)findViewById(R.id.placeName);
        rating = (TextView)findViewById(R.id.rating_number);
        address = (TextView)findViewById(R.id.textAddr);
        phone_number = (TextView)findViewById(R.id.textPhoneNumber);
        open_time = (TextView)findViewById(R.id.textOpen);
        rateBar = (RatingBar)findViewById(R.id.ratingBar);
        calendar = (TextView)findViewById(R.id.textCalendar);
        comment = (LinearLayout)findViewById(R.id.comment);
        commentCount = (TextView)findViewById(R.id.reviews_number);
        phoneNumber = (LinearLayout)findViewById(R.id.phonenumber);
        k = 0;

        LayerDrawable stars = (LayerDrawable) rateBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);

        new DownloadData().execute("");


        currentPage = 0;
        if (timer != null)
        {
            timer.cancel();
        }

        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(getApplicationContext(), Comment.class);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        phoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri number = Uri.parse("tel:" + phone_number.getText());
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(callIntent);
            }
        });
        mPager = (ViewPager) findViewById(R.id.pager);
    }

       private static void init() {

           //mPager.setCurrentItem(1, true);
           try
           {
               mPager.setAdapter(new SlidingImage_Adapter(c, photoRefer));
           }catch (Exception e)
           {

           }
        NUM_PAGES = photoRefer.size();
        // Auto start of
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };

           timer = new Timer();
           timer.schedule(new TimerTask() {
               @Override
               public void run() {
                   handler.post(Update);
               }
           }, 3000, 3000);

    }
    public static class DownloadData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                photoRefer.clear();
                StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
                googlePlacesUrl.append("placeid=" + placeID);
                googlePlacesUrl.append("&language=vi");
                googlePlacesUrl.append("&key=" + MapsActivity.GOOGLE_API_KEY);
                //String googlePlacesUrl = "https://maps.googleapis.com/maps/api/place/details/json?placeid=ChIJ5QCuTRkvdTERww6Hv1euj74&key=AIzaSyBHfDnaWtEfXJVVp-okrCzU0kv7xGZzkVc";
                PlacesDisplayTask.isDetails = 1;
                GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask();
                Object[] toPass = new Object[2];
                toPass[0] = MapsActivity.mMap;

                toPass[1] = googlePlacesUrl.toString();
                Http http = new Http();
                String googlePlacesData = http.read(googlePlacesUrl.toString());
                JSONObject googlePlacesJson = new JSONObject(googlePlacesData);
                Places placeJsonParser = new Places();
                //ob = googlePlacesJson;
                placeJsonParser.parse(googlePlacesJson);

            }catch (Exception e)
            {
                e.printStackTrace();
            }
            //process(googlePlacesReadTask, toPass);
            return "";
        }

        protected void onPostExecute(String res) {
           if (isThread == 0) {
                try {
                    if (!ob.isNull("photos")) {
                        JSONArray obs = ob.getJSONArray("photos");

                        for (int i = 0; i < obs.length(); i++)
                            photoRefer.add(obs.getJSONObject(i).getString("photo_reference"));
                    }
                    init();
                    placeName.setText(result.get("place_name"));
                    CommentAdapter.titleName = result.get("place_name");
                    rating.setText(result.get("rating"));
                    if (!result.get("address").equals(""))
                        address.setText(result.get("address"));
                    else address.setText("Không có thông tin");
                    if (!result.get("phone_number").equals(""))
                        phone_number.setText(result.get("phone_number"));
                    else phone_number.setText("Không có thông tin");
                    if (!result.get("open_now").equals("")) {
                        if (result.get("open_now").equals("true"))
                            open_time.setText("Đang mở cửa");
                        else open_time.setText("Đã đóng cửa");
                    } else open_time.setText("Không có thông tin");
                    if (result.get("rating") != "")
                        rateBar.setRating(Float.parseFloat(result.get("rating")));
                    if (!result.get("weekday_text").equals(""))
                        calendar.setText(result.get("weekday_text"));
                    else calendar.setText("Không có thông tin");
                    commentCount.setText(String.valueOf(Comment.comments.length()) + " đánh giá");
                    CommentAdapter.temp = new Bitmap[Comment.comments.length()];
                    new CommentAdapter.loadAvatar().execute("http:" + Comment.comments.getJSONObject(0).getString("profile_photo_url"), String.valueOf(0));
                } catch (Exception e) {
                    e.printStackTrace();
                }
           }
        }
    }
    @Override
    public void onResume()
    {
        super.onResume();
    }
}