package com.example.tuantu.week7_findplacenearby;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.LineDataSet;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
/**
 * Created by TUAN TU on 5/28/2016.
 */
import android.graphics.Color;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class Health extends Activity  implements SensorEventListener{
    public static ArrayList<Entry> entries;
    public static ArrayList<String> labels;
    public static ArrayList<Entry> entries1;
    public static ArrayList<String> labels1;
    public static LineChart lineChart;
    public static LineChart stepChart;
    public static PieChart foodChart;
    private static TextView txtTemp;
    private static TextView txtCond;
    private static LinearLayout liWeather;
    private SensorManager sensorManager;
    private TextView step;
    private TextView food;
    private static TextView weatherAd;
    public static TextView advice;
    boolean isRun;
    boolean isFirst = true;
    public static Context context;
    private static int stepNumber = 0;
    private static long rowID;
    private static String temparature = "";
    private static String status = "";
    static float tempStep;
    //static b
    public static String[] xData = { "Béo", "Đường", "Đạm", "Bột", "Xơ và Khoáng chất" };
    public static float[] yData = { 40, 30, 15, 10, 5 };
    String url = "http://www.accuweather.com/en/vn/ho-chi-minh-city/353981/weather-forecast/353981";
    //String url = "http://www.accuweather.com/en/id/jakarta/208971/weather-forecast/208971";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.health);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lineChart = (LineChart) findViewById(R.id.chart);
        stepChart = (LineChart) findViewById(R.id.chartRun);
        foodChart = (PieChart) findViewById(R.id.chartFood);
        step = (TextView)findViewById(R.id.btnRun);
        food = (TextView)findViewById(R.id.btnFood);
        advice = (TextView)findViewById(R.id.food_advice);
        txtCond = (TextView)findViewById(R.id.cond);
        txtTemp = (TextView)findViewById(R.id.temp);
        liWeather = (LinearLayout)findViewById(R.id.weather);
        weatherAd = (TextView)findViewById(R.id.weatherAdvice);
        context = this;
        entries = new ArrayList<>();
        labels = new ArrayList<String>();
        entries1 = new ArrayList<>();
        labels1 = new ArrayList<String>();
        labels.clear();
        entries.clear();
        entries1.clear();
        labels1.clear();

        HeartRateMonitor.FeedReaderContract.FeedReaderDbHelper mDbHelper = new HeartRateMonitor.FeedReaderContract.FeedReaderDbHelper(getApplicationContext());


        String[] projection = {
                HeartRateMonitor.FeedReaderContract.HeartBeat._ID,
                HeartRateMonitor.FeedReaderContract.HeartBeat.HEART_BEAT,
                HeartRateMonitor.FeedReaderContract.HeartBeat.TIME
        };
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Cursor cursor = db.query(
                HeartRateMonitor.FeedReaderContract.HeartBeat.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                // The sort order
        );
        if (cursor.getCount() > 0) {

            cursor.moveToFirst();
            while (!(cursor.getPosition() == cursor.getCount())) {
                String heartbeat1 = cursor.getString(cursor.getColumnIndex(HeartRateMonitor.FeedReaderContract.HeartBeat.HEART_BEAT));
                String time1 = cursor.getString(cursor.getColumnIndex(HeartRateMonitor.FeedReaderContract.HeartBeat.TIME));
                Entry entry = new Entry(Integer.parseInt(heartbeat1), entries.size());
                entries.add(entry);
                labels.add(time1);
                cursor.moveToNext();
            }

            LineDataSet dataset = new LineDataSet(Health.entries, "# of Calls");
            LineData data = new LineData(Health.labels, dataset);
            dataset.setColors(ColorTemplate.COLORFUL_COLORS);
            dataset.setDrawCubic(true);
            dataset.setDrawFilled(true);
            lineChart.setData(data);
            lineChart.animateY(5000);
        }
        //Get Step
        StepCount.StepCountDbHelper stepCountDbHelper = new StepCount.StepCountDbHelper(getApplicationContext());
        SQLiteDatabase stepDb = stepCountDbHelper.getWritableDatabase();
        String[] projection1 = {
                StepCount.Step._ID,
                StepCount.Step.STEP,
                StepCount.Step.TIME};
        Cursor stepCursor = stepDb.query(
                StepCount.Step.TABLE_NAME,  // The table to query
                projection1,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                // The sort order
        );



        if (stepCursor.getCount() > 0) {

            stepCursor.moveToFirst();
            while (!(stepCursor.getPosition() == stepCursor.getCount())) {
                String step1 = stepCursor.getString(stepCursor.getColumnIndex(StepCount.Step.STEP));
                String time1 = stepCursor.getString(stepCursor.getColumnIndex(StepCount.Step.TIME));
                Entry entry = new Entry(Integer.parseInt(step1), entries1.size());
                entries1.add(entry);
                labels1.add(time1);
                stepCursor.moveToNext();
            }

            LineDataSet dataset = new LineDataSet(entries1, "# of Calls");
            LineData data = new LineData(labels1, dataset);
            dataset.setColors(ColorTemplate.COLORFUL_COLORS);
            dataset.setDrawCubic(true);
            dataset.setDrawFilled(true);
            stepChart.setData(data);
            stepChart.animateY(5000);
        }

        ContentValues values = new ContentValues();
        Calendar calendar = Calendar.getInstance();
        String dateTime = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + "/" + String.valueOf(calendar.get(Calendar.MONTH) + 1);

        stepCursor.moveToLast();
        if (stepCursor.getCount() > 0 && stepCursor.getString(stepCursor.getColumnIndex(StepCount.Step.TIME)).equals(dateTime))
        {
            //stepCursor.moveToLast();
            stepNumber = Integer.parseInt(stepCursor.getString(stepCursor.getColumnIndex(StepCount.Step.STEP)));
            if (stepNumber > 0) step.setText("Số bước " + stepCursor.getString(stepCursor.getColumnIndex(StepCount.Step.STEP)) + "/10000");
        }
        else
        {
            //String time = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + "/" + String.valueOf(calendar.get(Calendar.MONTH) + 1);
            values.put(StepCount.Step._ID, String.valueOf(stepCursor.getCount()));
            stepNumber = 0;
            values.put(StepCount.Step.STEP, String.valueOf(stepNumber));
            values.put(StepCount.Step.TIME, dateTime);
            rowID = stepDb.insert(
                    StepCount.Step.TABLE_NAME,
                    "",
                    values);
        }


       // FeedReaderContract ob = new FeedReaderContract();

        //ob.FeedReaderDbHelper h = new FeedReaderContract.FeedReaderDbHelper((getApplicationContext()));


       /* labels.add("January");
        labels.add("February");
        labels.add("March");
        labels.add("April");
        labels.add("May");
        labels.add("June");*/

       /* LineData data = new LineData(labels, dataset);
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);
        dataset.setDrawCubic(true);
        dataset.setDrawFilled(true);
        lineChart.setData(data);
        lineChart.animateY(5000);*/
        TextView btnHeart = (TextView) findViewById(R.id.btnHeart);

        btnHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HeartRateMonitor.class);
                startActivity(intent);
            }
        });
        food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Food.class);
                startActivity(intent);
            }
        });

        //Get Food

        Food.FoodDb.FoodDbHelper foodDbHelper = new Food.FoodDb.FoodDbHelper(getApplicationContext());
        SQLiteDatabase foodDb = foodDbHelper.getWritableDatabase();
        String[] projection2 = {
                Food.FoodDb.FoodDes._ID,
                Food.FoodDb.FoodDes.FOOD,
                Food.FoodDb.FoodDes.NUT};
        Cursor foodCursor = foodDb.query(
                Food.FoodDb.FoodDes.TABLE_NAME,  // The table to query
                projection2,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                // The sort order
        );
        //If database not exists
        if (foodCursor.getCount() <= 0)
        {
            for (int i = 0; i < 5; i++) {
                ContentValues values1 = new ContentValues();
                values1.put(Food.FoodDb.FoodDes._ID, String.valueOf(String.valueOf(i)));
                values1.put(Food.FoodDb.FoodDes.FOOD, "a");
                values1.put(Food.FoodDb.FoodDes.NUT, "20");
                long rowID = foodDb.insert(
                        Food.FoodDb.FoodDes.TABLE_NAME,
                        "",
                        values1);
            }
        }
        //drawChartFood();
        Food.calcPercent();
        if (isNetworkConnected() && isOnline())
        (new weatherLoad()).execute(new String[]{url});
    }
    public static class weatherLoad extends AsyncTask<String, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            ArrayList<String> buffer = new ArrayList<String>();
            try {

                org.jsoup.nodes.Document doc = Jsoup.connect(strings[0]).get();
                //Get all albums of the airtist
                Elements content = doc.select("li#feed-main strong.temp");
                //content.
                temparature = content.text();
                content = doc.select("li#feed-main span.cond");
                status = content.text();

                //String albumName = null;
                //String url1=null;
               /* if (container.contains("Album: "))
                {
                    String [] temp = container.split("Album: ");

                    albumName = temp[temp.length - 1];

                    for (int i = 0; i < albums.size(); i++)
                    {
                        if (albums.get(i).select("h3.title-item").text().equals(albumName)) {
                            url1 = albums.get(i).select("img").attr("src");
                            break;
                        }
                    }
                }*/
                buffer.add("");
                //MainActivity.urlNor(url);

            } catch (Exception t) {
                Toast.makeText(context, "Đường truyền không ổn định", Toast.LENGTH_LONG).show();
            }
            return buffer;
        }
        @Override
        protected void onPostExecute(ArrayList<String> s) {
            super.onPostExecute(s);

            txtCond.setText(status);
            txtTemp.setText(temparature);
            Calendar calendar = Calendar.getInstance();
            String time = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
            if ((status.contains("sun") || status.contains("lear") || status.contains("Sun")) && (Integer.parseInt(time) >= 6 && Integer.parseInt(time) < 18)) {
                liWeather.setBackgroundResource(R.drawable.sunny);
                weatherAd.setText("Trời trong, rất thích hợp cho một chuyến du lịch cùng bạn bè, gia đình và nhớ tránh nắng nóng");
            }
            else if ((status.contains("sun") || status.contains("lear") || status.contains("Sun")) && (Integer.parseInt(time) >= 18 || Integer.parseInt(time) < 6)) {
                liWeather.setBackgroundResource(R.drawable.clear);
                weatherAd.setText("Trời trong, bạn có thể đi dạo và tận hưởng bầu không khí trong lành vào buổi tối");
            }
            else if ((status.contains("artly cloudy") || status.contains("ome cloud")) && (Integer.parseInt(time) >= 18 || Integer.parseInt(time) < 6)) {
                liWeather.setBackgroundResource(R.drawable.partly_cloudy_night);
                weatherAd.setText("Thời tiết tốt, bạn có thể đi dạo và tập thể dục ngoài trời. Giúp cơ thể thư giãn và khỏe mạnh");
            }
            else if ((status.contains("artly cloudy") || status.contains("ome cloud")) && (Integer.parseInt(time) >= 6 && Integer.parseInt(time) < 18)) {
                liWeather.setBackgroundResource(R.drawable.partly_cloudy_morning);
                weatherAd.setText("Thời tiết tốt, bạn có thể đi dạo và tập thể dục ngoài trời. Giúp cơ thể thư giãn và khỏe mạnh");
            }
            else if (status.contains("loud")) {
                liWeather.setBackgroundResource(R.drawable.mostly_cloudy);
                weatherAd.setText("Trời nhiều mây, có thể mưa vào buổi chiều tối, nhớ mang ô trước khi ra khỏi phòng");
            }
            else if (status.contains("ain") || status.contains("shower")) {
                liWeather.setBackgroundResource(R.drawable.rain);
                weatherAd.setText("Mưa, nhớ mang theo áo mưa và nên hạn chế ra ngoài để không bị cảm lạnh");
            }
            else if (status.contains("storm")) {
                liWeather.setBackgroundResource(R.drawable.thunder_storm);
                weatherAd.setText("Cơn dông có thể đến vào chiều tối. Thời tiết nguy hiểm có thể kèm theo gió mạnh, nên hạn chế ra đường");
            }
            else
            {
                liWeather.setBackgroundResource(R.drawable.sunny);
                weatherAd.setText("");
            }
        }
    }
    private void weatherLoad()
    {

    }
    @Override
    protected void onResume() {
        super.onResume();
        isRun = true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);

        } else {
            step.setText("Điện thoại của bạn không hỗ trợ tính năng này");
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        /*StepCount.StepCountDbHelper stepCountDbHelper = new StepCount.StepCountDbHelper(getApplicationContext());
        SQLiteDatabase db = stepCountDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        Calendar calendar = Calendar.getInstance();
        String time = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + "/" + String.valueOf(calendar.get(Calendar.MONTH) + 1);
        values.put(StepCount.Step.STEP, String.valueOf(stepNumber));
        values.put(StepCount.Step.TIME, time);
        rowID = db.insert(
                StepCount.Step.TABLE_NAME,
                "",
                values);*/

    }
    @Override
    protected void onPause() {
        super.onPause();
        isRun = false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        StepCount.StepCountDbHelper stepCountDbHelper = new StepCount.StepCountDbHelper(getApplicationContext());
        SQLiteDatabase stepDb = stepCountDbHelper.getWritableDatabase();
        String[] projection1 = {
                StepCount.Step._ID,
                StepCount.Step.STEP,
                StepCount.Step.TIME};
        Cursor stepCursor = stepDb.query(
                StepCount.Step.TABLE_NAME,  // The table to query
                projection1,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                // The sort order
        );



        if (stepCursor.getCount() > 0 && isFirst == false && isRun == true) {

          /*  stepCursor.moveToFirst();
            while (!(stepCursor.getPosition() == stepCursor.getCount())) {
                String step1 = stepCursor.getString(stepCursor.getColumnIndex(StepCount.Step.STEP));
                String time1 = stepCursor.getString(stepCursor.getColumnIndex(StepCount.Step.TIME));
                Entry entry = new Entry(Integer.parseInt(step1), entries1.size());
                entries1.add(entry);
                labels1.add(time1);
                stepCursor.moveToNext();
            }
            LineDataSet dataset = new LineDataSet(entries1, "# of Calls");
            LineData data = new LineData(labels1, dataset);
            dataset.setColors(ColorTemplate.COLORFUL_COLORS);
            dataset.setDrawCubic(true);
            dataset.setDrawFilled(true);
            stepChart.setData(data);
            stepChart.animateY(5000);*/
        }
        Calendar calendar = Calendar.getInstance();
        String dateTime = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + "/" + String.valueOf(calendar.get(Calendar.MONTH) + 1);
        stepCursor.moveToLast();
        if (stepCursor.getCount() > 0 && !stepCursor.getString(stepCursor.getColumnIndex(StepCount.Step.TIME)).equals(dateTime))
        {
            ContentValues values = new ContentValues();
            values.put(StepCount.Step._ID, String.valueOf(stepCursor.getCount()));
            stepNumber = 0;
            values.put(StepCount.Step.STEP, String.valueOf(stepNumber));
            values.put(StepCount.Step.TIME, dateTime);
            rowID = stepDb.insert(
                    StepCount.Step.TABLE_NAME,
                    "",
                    values);
        }
        //----------------------------------------------------------------
        if (isFirst == false) {
            step.setText("Số bước " + (stepNumber + (int) (event.values[0] - tempStep)) + "/10000");
            //StepCount.StepCountDbHelper stepCountDbHelper1 = new StepCount.StepCountDbHelper(getApplicationContext());
            //SQLiteDatabase stepDb = stepCountDbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            //Calendar calendar = Calendar.getInstance();
            String time = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + "/" + String.valueOf(calendar.get(Calendar.MONTH) + 1);
            values.put(StepCount.Step.STEP, String.valueOf(stepNumber + (int) (event.values[0] - tempStep)));
            values.put(StepCount.Step.TIME, time);
            String[] selectionArgs = {String.valueOf(rowID)};
            String selection = StepCount.Step._ID + " LIKE ?";
            stepDb.update(
                    StepCount.Step.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);

        }
        if(isRun) {
            if (isFirst == true) tempStep = event.values[0];
            if (stepNumber > 0) step.setText("Số bước " + (stepNumber + (int)(event.values[0] - tempStep)) + "/10000");
            //if (isFirst == false) stepNumber++;
            isFirst = false;
            //StepCount.StepCountDbHelper stepCountDbHelper = new StepCount.StepCountDbHelper(getApplicationContext());
            //SQLiteDatabase db = stepCountDbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            //Calendar calendar = Calendar.getInstance();
            String time = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + "/" + String.valueOf(calendar.get(Calendar.MONTH) + 1);
            values.put(StepCount.Step.STEP, String.valueOf(stepNumber + (int)(event.values[0] - tempStep)));
            values.put(StepCount.Step.TIME, time);
            String[] selectionArgs = {String.valueOf(rowID)};
            String selection = StepCount.Step._ID + " LIKE ?";
            stepDb.update(
                    StepCount.Step.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public static final class StepCount {
        private static final String TEXT_TYPE = " TEXT";
        private static final String COMMA_SEP = ",";
        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + Step.TABLE_NAME + " (" +
                        Step._ID + " INTEGER PRIMARY KEY," +
                        Step.STEP + TEXT_TYPE + COMMA_SEP +
                        Step.TIME + TEXT_TYPE + ")";
        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + Step.TABLE_NAME;
        // To prevent someone from accidentally instantiating the contract class,
        // give it an empty constructor.

        public StepCount() {
        }

        /* Inner class that defines the table contents */
        public static abstract class Step implements BaseColumns {
            public static final String TABLE_NAME = "stepcount";
            public static final String STEP = "stepcount";
            public static final String TIME = "time";
        }

        public static class StepCountDbHelper extends SQLiteOpenHelper {
            // If you change the database schema, you must increment the database version.
            public static final int DATABASE_VERSION = 1;
            public static final String DATABASE_NAME = "StepCount.db";

            public StepCountDbHelper(Context context) {
                super(context, DATABASE_NAME, null, DATABASE_VERSION);
            }

            public void onCreate(SQLiteDatabase db) {
                db.execSQL(SQL_CREATE_ENTRIES);
            }

            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                // This database is only a cache for online data, so its upgrade policy is
                // to simply to discard the data and start over
                db.execSQL(SQL_DELETE_ENTRIES);
                onCreate(db);
            }

            public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                onUpgrade(db, oldVersion, newVersion);
            }
        }
    }
    public static void drawChartFood()
    {
        foodChart.setUsePercentValues(true);
        foodChart.setDescription("Phân phối dinh dưỡng");

        foodChart.setDrawHoleEnabled(true);
        foodChart.setHoleColorTransparent(true);
        foodChart.setHoleRadius(7);
        foodChart.setTransparentCircleRadius(0);


        foodChart.setRotationAngle(0);
        foodChart.setRotationEnabled(true);


        foodChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                // display msg when value selected
                if (e == null)
                    return;
                Toast.makeText(context, xData[e.getXIndex()] + " = " + e.getVal() + "%", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });
        addData();
        Legend l = foodChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7);
        l.setYEntrySpace(5);
    }
    public static void addData() {
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        for (int i = 0; i < yData.length; i++)
            yVals1.add(new Entry(yData[i], i));

        ArrayList<String> xVals = new ArrayList<String>();

        for (int i = 0; i < xData.length; i++)
            xVals.add(xData[i]);

        // create pie data set
        PieDataSet dataSet = new PieDataSet(yVals1, "");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);

        // add many colors
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);

        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.GRAY);

        foodChart.setData(data);

        foodChart.highlightValues(null);

        foodChart.invalidate();
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}