package com.example.tuantu.week7_findplacenearby;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SlidingImage_Adapter extends PagerAdapter {
    private static ArrayList<String> IMAGES;
    static Bitmap[] temp;
    private LayoutInflater inflater;
    private Context context;
    static ImageView imageView = null;

    public SlidingImage_Adapter(Context context,ArrayList<String> IMAGES) {
        this.context = context;
        this.IMAGES=IMAGES;
        inflater = LayoutInflater.from(context);
        temp = new Bitmap[IMAGES.size()];
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return IMAGES.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
       // View imageLayout = inflater.inflate(R.layout.slidingimages_layout, view, false);

        View imageLayout = inflater.inflate(R.layout.slidingimages_layout, view, false);

        assert imageLayout != null;
        imageView = (ImageView) imageLayout
                .findViewById(R.id.image);

        if (temp[position] == null)
        {
            imageView.setImageResource(R.drawable.h1);
            new ParseURL().execute(String.valueOf(position));
        }
        else imageView.setImageBitmap(temp[position]);
        view.addView(imageLayout, 0);
        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
    public static class ParseURL extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL newurl = new URL("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + IMAGES.get(Integer.parseInt(strings[0])) + "&key=" + MapsActivity.GOOGLE_API_KEY);
                HttpURLConnection connection = null;
                connection = (HttpURLConnection) newurl.openConnection();
                connection.connect();
                Bitmap mIcon_val = BitmapFactory.decodeStream(connection.getInputStream());
                temp[Integer.parseInt(strings[0])] = mIcon_val;
                return mIcon_val;
            } catch (Exception e) {
                e.printStackTrace();
            }
           return null;
        }
        @Override
        protected void onPostExecute(Bitmap a) {
            setImage(a);
        }
    }
    public static void setImage(Bitmap b)
    {
        try {
            imageView.setImageBitmap(b);

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}