package com.example.tuantu.week7_findplacenearby;


import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.provider.SyncStateContract;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

/**
 * Created by TUAN TU on 5/30/2016.
 */
public class PowerService extends Service {
    public static int count1  = 0;
    public static int count2 = 0;
    public Handler handler1;
    public Handler handler2;
    boolean flag1 = false;
    boolean flag2= false;
    @Override
    public void onCreate() {
        super.onCreate();
        // register receiver that handles screen on and screen off logic
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver = new Receiver();
        registerReceiver(mReceiver, filter);
    }
    @Override
    public void onStart(Intent intent, int startId) {


        final Setting.SettingDb.SettingDbHelper settingDbHelper = new Setting.SettingDb.SettingDbHelper(getApplicationContext());
        final SQLiteDatabase settingDb = settingDbHelper.getWritableDatabase();
        String[] projection = {
                Setting.SettingDb.SettingDes.ISMESS,
                Setting.SettingDb.SettingDes.MESSCONTENT,
                Setting.SettingDb.SettingDes.MESSCONTACT,
                Setting.SettingDb.SettingDes.MESSNUM,
                Setting.SettingDb.SettingDes.ISCALL,
                Setting.SettingDb.SettingDes.CALLCONTACT,
                Setting.SettingDb.SettingDes.CALLNUM
        };

        Cursor settingCursor = settingDb.query(
                Setting.SettingDb.SettingDes.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                // The sort order
        );

        settingCursor.moveToFirst();
        if (settingCursor.getCount() > 0) {
            //_isMess = Integer.parseInt(settingCursor.getString(settingCursor.getColumnIndex(SettingDb.SettingDes.ISMESS)));
            Setting._isMess = settingCursor.getString(settingCursor.getColumnIndex(Setting.SettingDb.SettingDes.ISMESS));
            Setting._messageContent = settingCursor.getString(settingCursor.getColumnIndex(Setting.SettingDb.SettingDes.MESSCONTENT));
        Setting._messageContact = settingCursor.getString(settingCursor.getColumnIndex(Setting.SettingDb.SettingDes.MESSCONTACT));
        Setting._numMess = Integer.parseInt(settingCursor.getString(settingCursor.getColumnIndex(Setting.SettingDb.SettingDes.MESSNUM)));
        Setting._isCall = settingCursor.getString(settingCursor.getColumnIndex(Setting.SettingDb.SettingDes.ISCALL));
        Setting._callContact = settingCursor.getString(settingCursor.getColumnIndex(Setting.SettingDb.SettingDes.CALLCONTACT));
        Setting._numCall = Integer.parseInt(settingCursor.getString(settingCursor.getColumnIndex(Setting.SettingDb.SettingDes.CALLNUM)));
    }

        final boolean screenOn = intent.getBooleanExtra("screen_state", false);
        if (!screenOn) {
            count1++;
            count2++;
            //Toast.makeText(getApplicationContext(), "Power on", Toast.LENGTH_LONG).show();
            if (count1 == 1 || count2 == 1) {
                handler1 = new Handler();
                Runnable runnable = new Runnable() {
                    public void run() {
                        new CountDownTimer(3000, 1) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                if (count1 >= Setting._numMess && Setting._isMess.equals("true"))
                                    {
                                        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                                        Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                        if (loc == null) {
                                            loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                                        }
                                        if (loc != null) {

                                            MapsActivity.latitude = loc.getLatitude();
                                            MapsActivity.longitude = loc.getLongitude();

                                        }


                                        Toast.makeText(getApplicationContext(), "Emergency mode!", Toast.LENGTH_LONG).show();
                                        count1 = 0;
                                        //String number = "12346556";  // The number on which you want to send SMS
                                        //startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null)));
                                        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                                        sendIntent.putExtra("address"  , Setting._messageContact);
                                        sendIntent.putExtra("sms_body", Setting._messageContent + " My latest position: https://www.google.com/maps/place/"+ MapsActivity.latitude + "," + MapsActivity.longitude);
                                        sendIntent.setData(Uri.parse("smsto:" + Setting._messageContact));
                                        //sendIntent.setType("vnd.android-dir/mms-sms");

                                        try {
                                            sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(sendIntent);
                                        }catch (Exception e)
                                        {
                                            e.printStackTrace();
                                        }

                                    }
                                if (count2 >= Setting._numCall && Setting._isCall.equals("true"))
                                {
                                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                                    callIntent.setData(Uri.parse("tel:"+Setting._callContact));
                                    callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(callIntent);
                                    count2 = 0;
                                }
                            }
                            @Override
                            public void onFinish() {

                                count1 = 0;
                                count2 = 0;
                            }
                        }.start();
                        handler1.postDelayed(this, 4000);
                    }
                };
                runnable.run();
            }
        } else {
            //Toast.makeText(getApplicationContext(), "Power off", Toast.LENGTH_LONG).show();
            count1++;
            count2++;
           /* if (count >= 3)
            {
                Toast.makeText(getApplicationContext(), "Emergency mode!", Toast.LENGTH_LONG).show();
                count = 0;
            }
            else {
                handler2 = new Handler();
                Runnable runnable = new Runnable() {
                    public void run() {
                        CountDownTimer timer = new CountDownTimer(1000, 10) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                if (!screenOn)
                                {
                                    count++;
                                    flag2 = true;
                                }
                            }

                            @Override
                            public void onFinish() {
                                if (flag2 == false)
                                {
                                    count = 0;
                                }
                                flag2 = false;
                            }
                        };
                        handler2.postDelayed(this, 1100);
                    }
                };
                runnable.run();
            }*/
            //Toast.makeText(getApplicationContext(), "Power off", Toast.LENGTH_LONG).show();
        }
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Truiton Music Player")
                .setTicker("Truiton Music Player")
                .setContentText("My Music")
                .build();
        startForeground(101, notification);
    }
    @Override
    public IBinder onBind(Intent intent)
    {
        return mBinder;
    }
    public class LocalBinder extends Binder {
        PowerService getService() {
            return PowerService.this;
        }
    }
    private final IBinder mBinder = new LocalBinder();

}
