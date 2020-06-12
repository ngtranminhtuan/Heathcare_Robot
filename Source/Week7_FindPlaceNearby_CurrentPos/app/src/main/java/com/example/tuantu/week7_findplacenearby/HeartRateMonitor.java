package com.example.tuantu.week7_findplacenearby;

/**
 * Created by TUAN TU on 5/28/2016.
 */

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicBoolean;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;


/**
 * This class extends Activity to handle a picture preview, process the preview
 * for a red values and determine a heart beat.
 *
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class HeartRateMonitor extends Activity {

    private static final String TAG = "HeartRateMonitor";
    private static final AtomicBoolean processing = new AtomicBoolean(false);
    private static Context c= null;
    private static SurfaceView preview = null;
    private static SurfaceHolder previewHolder = null;
    private static Camera camera = null;
    private static View image = null;
    private static TextView text = null;
    public static int heartBeat = 0;
    private static WakeLock wakeLock = null;
    static int count = 0;
    private static int averageIndex = 0;
    private static final int averageArraySize = 4;
    private static final int[] averageArray = new int[averageArraySize];
    public static int tempCount = 0;
    public static enum TYPE {
        GREEN, RED
    };

    private static TYPE currentType = TYPE.GREEN;

    public static TYPE getCurrent() {
        return currentType;
    }

    private static int beatsIndex = 0;
    private static final int beatsArraySize = 3;
    private static final int[] beatsArray = new int[beatsArraySize];
    private static double beats = 0;
    private static long startTime = 0;
    static FeedReaderContract.FeedReaderDbHelper mDbHelper;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.heartbeat);
        c = this;
        tempCount = 0;
        count = 0;
        mDbHelper = new FeedReaderContract.FeedReaderDbHelper(getApplicationContext());

        preview = (SurfaceView) findViewById(R.id.preview);
        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        image = findViewById(R.id.image);
        text = (TextView) findViewById(R.id.text);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResume() {
        super.onResume();

        wakeLock.acquire();

        camera = Camera.open();

        startTime = System.currentTimeMillis();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPause() {
        super.onPause();

        wakeLock.release();

        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    private static PreviewCallback previewCallback = new PreviewCallback() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void onPreviewFrame(byte[] data, Camera cam) {
            if (data == null) throw new NullPointerException();
            Camera.Size size = cam.getParameters().getPreviewSize();
            if (size == null) throw new NullPointerException();

            if (!processing.compareAndSet(false, true)) return;

            int width = size.width;
            int height = size.height;

            int imgAvg = ImageProcessing.decodeYUV420SPtoRedAvg(data.clone(), height, width);
            // Log.i(TAG, "imgAvg="+imgAvg);
            if (imgAvg == 0 || imgAvg == 255) {
                processing.set(false);
                return;
            }

            int averageArrayAvg = 0;
            int averageArrayCnt = 0;
            for (int i = 0; i < averageArray.length; i++) {
                if (averageArray[i] > 0) {
                    averageArrayAvg += averageArray[i];
                    averageArrayCnt++;
                }
            }

            int rollingAverage = (averageArrayCnt > 0) ? (averageArrayAvg / averageArrayCnt) : 0;
            TYPE newType = currentType;
            if (imgAvg < rollingAverage) {
                newType = TYPE.RED;
                if (newType != currentType) {
                    beats++;
                    // Log.d(TAG, "BEAT!! beats="+beats);
                }
            } else if (imgAvg > rollingAverage) {
                newType = TYPE.GREEN;
            }

            if (averageIndex == averageArraySize) averageIndex = 0;
            averageArray[averageIndex] = imgAvg;
            averageIndex++;

            // Transitioned from one state to another to the same
            if (newType != currentType) {
                currentType = newType;
                image.postInvalidate();
            }

            long endTime = System.currentTimeMillis();
            double totalTimeInSecs = (endTime - startTime) / 1000d;
            if (totalTimeInSecs >= 10) {
                double bps = (beats / totalTimeInSecs);
                int dpm = (int) (bps * 60d);
                if (dpm < 30 || dpm > 180) {
                    startTime = System.currentTimeMillis();
                    beats = 0;
                    processing.set(false);
                    return;
                }

                // Log.d(TAG,
                // "totalTimeInSecs="+totalTimeInSecs+" beats="+beats);

                if (beatsIndex == beatsArraySize) beatsIndex = 0;
                beatsArray[beatsIndex] = dpm;
                beatsIndex++;

                int beatsArrayAvg = 0;
                int beatsArrayCnt = 0;
                for (int i = 0; i < beatsArray.length; i++) {
                    if (beatsArray[i] > 0) {
                        beatsArrayAvg += beatsArray[i];
                        beatsArrayCnt++;
                    }
                }
                int beatsAvg = (beatsArrayAvg / beatsArrayCnt);
                heartBeat = beatsAvg;
                text.setText(String.valueOf(beatsAvg));

                startTime = System.currentTimeMillis();
                beats = 0;

                final MediaPlayer player = MediaPlayer.create(c, R.raw.beep);
                new CountDownTimer(10000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        tempCount++;
                        if (tempCount % 2 == 0 && tempCount <= 10)
                        {

                            //MediaPlayer player = MediaPlayer.create(c, R.raw.beep);
                            player.start();

                        }
                    }

                    public void onFinish() {
                        //Intent intent = new Intent(c, Health.class);
                        //c.startActivity(intent);
                        //Save data

                        player.stop();
                        player.release();
                        //Activity a = (Activity) c;
                        //a.finish();
                        if (count == 0) {
                            count = 1;
                            Calendar calendar = Calendar.getInstance();
                            String time = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + "/" + String.valueOf(calendar.get(Calendar.MONTH) + 1);
                            SQLiteDatabase db = null;
                            try {
                                db = mDbHelper.getWritableDatabase();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            ContentValues values = new ContentValues();
                            values.put(FeedReaderContract.HeartBeat.HEART_BEAT, String.valueOf(heartBeat));
                            values.put(FeedReaderContract.HeartBeat.TIME, time);

                            db.insert(
                                    FeedReaderContract.HeartBeat.TABLE_NAME,
                                    "",
                                    values);

                            String[] projection = {
                                    FeedReaderContract.HeartBeat._ID,
                                    FeedReaderContract.HeartBeat.HEART_BEAT,
                                    FeedReaderContract.HeartBeat.TIME
                            };

                            Cursor cursor = db.query(
                                    FeedReaderContract.HeartBeat.TABLE_NAME,  // The table to query
                                    projection,                               // The columns to return
                                    null,                                // The columns for the WHERE clause
                                    null,                            // The values for the WHERE clause
                                    null,                                     // don't group the rows
                                    null,                                     // don't filter by row groups
                                    null                                // The sort order
                            );

                            cursor.moveToLast();
                            String heartbeat1 = cursor.getString(cursor.getColumnIndex(FeedReaderContract.HeartBeat.HEART_BEAT));
                            String time1 = cursor.getString(cursor.getColumnIndex(FeedReaderContract.HeartBeat.TIME));
                            try {
                                Entry entry = new Entry(Integer.parseInt(heartbeat1), Health.entries.size());
                                Health.entries.add(entry);
                                LineDataSet dataset = new LineDataSet(Health.entries, "# of Calls");
                                Health.labels.add(time1);
                                //LineDataSet dataset = new LineDataSet(entries, "# of Calls");
                                LineData data = new LineData(Health.labels, dataset);
                                dataset.setColors(ColorTemplate.COLORFUL_COLORS);
                                dataset.setDrawCubic(true);
                                dataset.setDrawFilled(true);
                                Health.lineChart.setData(data);
                                Health.lineChart.animateY(5000);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        //Health.entries.clear();
                        //Health.labels.clear();

                        Activity a = (Activity) c;
                        a.finish();
                    }
                }.start();
              /*  try {
                    new CountTime().execute("");
                }catch (Exception e)
                {
                    e.printStackTrace();
                }*/

            }
            processing.set(false);
        }
    };

    public static class CountTime extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            new CountDownTimer(10000, 1000) {
                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    //Intent intent = new Intent(c, Health.class);
                    //c.startActivity(intent);
                    Activity a = (Activity) c;
                    a.finish();
                }
            }.start();
            return "";
        }
        protected void onPostExecute(String res) {
            Activity a = (Activity) c;
            a.finish();
        }
    }

    private static SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera.setPreviewDisplay(previewHolder);
                camera.setPreviewCallback(previewCallback);
            } catch (Throwable t) {
                Log.e("PreviewDemo-surfaceCallback", "Exception in setPreviewDisplay()", t);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            Camera.Size size = getSmallestPreviewSize(width, height, parameters);
            if (size != null) {
                parameters.setPreviewSize(size.width, size.height);
                Log.d(TAG, "Using width=" + size.width + " height=" + size.height);
            }
            camera.setParameters(parameters);
            camera.startPreview();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // Ignore
        }
    };

    private static Camera.Size getSmallestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea < resultArea) result = size;
                }
            }
        }

        return result;
    }


    public static final class FeedReaderContract {
        private static final String TEXT_TYPE = " TEXT";
        private static final String COMMA_SEP = ",";
        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + HeartBeat.TABLE_NAME + " (" +
                        HeartBeat._ID + " INTEGER PRIMARY KEY," +
                        HeartBeat.HEART_BEAT + TEXT_TYPE + COMMA_SEP +
                        HeartBeat.TIME + TEXT_TYPE +")";
        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + HeartBeat.TABLE_NAME;
        // To prevent someone from accidentally instantiating the contract class,
        // give it an empty constructor.

        public FeedReaderContract() {
        }

        /* Inner class that defines the table contents */
        public static abstract class HeartBeat implements BaseColumns {
            public static final String TABLE_NAME = "heartbeat";
            public static final String HEART_BEAT = "heartbeat";
            public static final String TIME = "time";
        }

        public static class FeedReaderDbHelper extends SQLiteOpenHelper {
            // If you change the database schema, you must increment the database version.
            public static final int DATABASE_VERSION = 1;
            public static final String DATABASE_NAME = "FeedReader.db";

            public FeedReaderDbHelper(Context context) {
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
}

