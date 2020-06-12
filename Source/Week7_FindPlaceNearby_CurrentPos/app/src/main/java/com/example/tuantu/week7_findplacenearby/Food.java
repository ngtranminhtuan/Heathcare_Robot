package com.example.tuantu.week7_findplacenearby;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Vector;

public class Food extends AppCompatActivity {
    public Button b1, b2, b3;
    public Spinner sp;
    public LinearLayout li;
    public LinearLayout prev;
    public LinearLayout newLi;
    public int index = 0;
    public String res = "";
    public static Context context;
    ArrayList<EditText> arr = new ArrayList<EditText>();
    ArrayList<Spinner> arr3 = new ArrayList<Spinner>();
    ArrayList<LinearLayout> arr2 = new ArrayList<LinearLayout>();
   // ArrayList<p> arr1 = new ArrayList<p>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food);
        context = this;
        //s.split();
        li = (LinearLayout)findViewById(R.id.content);
        b1 = (Button)findViewById(R.id.add);
        b2 = (Button)findViewById(R.id.remove);
        b3 = (Button)findViewById(R.id.save);

        //Submit and show result
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index == 0)
                {
                    prev = (LinearLayout)findViewById(R.id.start);
                    newLi = new LinearLayout(getApplicationContext());
                    newLi.setOrientation(LinearLayout.HORIZONTAL);
                    newLi.setGravity(Gravity.CENTER);
                    newLi.setBaselineAligned(true);
                    li.addView(newLi);
                    arr2.add(prev);
                    arr2.add(newLi);
                    index++;
                }
                else
                {
                    try {
                        prev = arr2.get(arr2.size() - 1);
                        newLi = new LinearLayout(getApplicationContext());
                        newLi.setOrientation(LinearLayout.HORIZONTAL);
                        newLi.setGravity(Gravity.CENTER);
                        newLi.setBaselineAligned(true);
                        li.addView(newLi);
                        arr2.add(newLi);
                        index++;
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                param1.weight = 1;
                Spinner name = new Spinner(getApplicationContext());
                //name.setHint("Name");
                //name.setTextColor(Color.parseColor("#ffffff"));
                String[] ar = {"Cơm", "Thịt heo", "Thịt bò", "Thịt gà", "Tôm", "Mực", "Cá", "Rau và trái cây"};
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, ar);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                name.setAdapter(spinnerArrayAdapter);
                //name.setBackgroundColor(Color.parseColor("#0000ff"));
                name.setLayoutParams(param1);
                LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                param2.weight = 1;
                EditText value = new EditText(getApplicationContext());
                value.setHint("Hàm lượng (gam)");
                value.setTextColor(Color.parseColor("#ffffff"));
                value.setBackgroundColor(Color.parseColor("#545454"));
                value.setLayoutParams(param2);
                prev.removeAllViews();
                newLi.addView(b1);
                newLi.addView(b2);
                newLi.addView(b3);

                prev.addView(name);
                prev.addView(value);
                arr3.add(name);
                arr.add(value);
            }
        });
        b2.setOnClickListener(new View.OnClickListener(){
                                  @Override public void onClick(View v)
                                  {
                                   /*   TextView t = new TextView(getApplicationContext());
                                      for (int i = 0; i < arr.size();)
                                      {
                                          //res += arr.get(i).getText().toString() + ": " + arr.get(i + 1).getText().toString() + "\n";

                                          //i ++;
                                      }
                                      Parcel source = Parcel.obtain();
                                      if (sp.getSelectedItemId() == 0)
                                      {
                                         *//* p1 obj = p1.CREATOR.createFromParcel(source);
                                          obj.mData = res;
                                          arr1.add(obj);*//*
                                      }
                                      else if (sp.getSelectedItemId() == 1)
                                      {
                                        *//*  p2 obj = p2.CREATOR.createFromParcel(source);
                                          obj.mData = res;
                                          arr1.add(obj);*//*
                                      }

                                      Toast.makeText(getApplicationContext(), "Item has been saved", Toast.LENGTH_LONG).show();*/
                                      /*for (int i = 0; i < arr2.size() - 1; i++)
                                      {
                                          li.removeView(arr2.get(i));
                                      }*/
                                      //li.removeAllViews(arr2.get(arr2.size() - 1));

                                      //newLi = (LinearLayout)findViewById(R.id.start);
                                      //newLi.removeAllViews();
                                      //arr2.get(arr2.size() - 1).removeAllViews();//tranh trung id, ta phai remove truoc khi add lai
                                      if (arr2.size() > 2) {
                                          arr2.get(arr2.size() - 2).removeAllViews();
                                          arr2.get(arr2.size() - 1).removeAllViews();
                                          try {
                                              arr2.get(arr2.size() - 2).addView(b1);
                                              arr2.get(arr2.size() - 2).addView(b2);
                                              arr2.get(arr2.size() - 2).addView(b3);
                                          } catch (Exception e) {
                                              e.printStackTrace();
                                          }
                                          arr.remove(arr.size() - 1);
                                          arr3.remove(arr3.size() - 1);
                                          li.removeView(arr2.get(arr2.size() - 1));
                                          arr2.remove(arr2.size() - 1);
                                    /*  newLi.addView(b1);
                                      newLi.addView(b2);
                                      newLi.addView(b3);
                                      li.removeView(arr2.get(arr2.size() - 1));
                                      arr.removeAll(arr);
                                      arr3.removeAll(arr3);
                                      arr2.removeAll(arr2);*/
                                          index--;
                                          res = "";
                                      }
                                  }
                              }
        );

        b3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {


               /* values.put(FoodDb.FoodDes._ID, String.valueOf(stepCursor.getCount()));
                //stepNumber = 0;
                values.put(FoodDb.FoodDes.FOOD, String.valueOf(stepNumber));
                values.put(FoodDb.FoodDes.NUT, dateTime);*/
                //long rowID = stepDb.insert(
                //        FoodDb.FoodDes.TABLE_NAME,
                //        "",
                //        values);
                for (int i = 0; i < arr.size(); i++)
                {
                    try {
                        float t = Float.parseFloat(arr.get(i).getText().toString());
                    }catch (Exception e)
                    {
                        Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                for (int i = 0; i < arr3.size(); i++)
                {
                    if (arr3.get(i).getSelectedItem().toString().equals("Cơm"))
                    {
                        insert(i, 3);
                        insert(i, 1);
                    }
                    if (arr3.get(i).getSelectedItem().toString().equals("Thịt heo"))
                    {
                        insert(i, 0);
                    }
                    if (arr3.get(i).getSelectedItem().toString().equals("Thịt bò"))
                    {
                        insert(i, 2);
                    }
                    if (arr3.get(i).getSelectedItem().toString().equals("Thịt gà"))
                    {
                        insert(i, 2);
                    }
                    if (arr3.get(i).getSelectedItem().toString().equals("Tôm"))
                    {
                        insert(i, 2);
                    }
                    if (arr3.get(i).getSelectedItem().toString().equals("Mực"))
                    {
                        insert(i, 2);
                    }
                    if (arr3.get(i).getSelectedItem().toString().equals("Cá"))
                    {
                        insert(i, 2);
                    }
                    if (arr3.get(i).getSelectedItem().toString().equals("Rau và trái cây"))
                    {
                        insert(i, 4);
                    }
                }
                calcPercent();
                //-------------------------------


                Activity temp = (Activity) context;
                temp.finish();
            }
        });
    }

    public static void calcPercent()
    {
        FoodDb.FoodDbHelper foodDbHelper = new FoodDb.FoodDbHelper(Health.context);
        SQLiteDatabase foodDb = foodDbHelper.getWritableDatabase();
        String[] projection2 = {
                FoodDb.FoodDes._ID,
                FoodDb.FoodDes.FOOD,
                FoodDb.FoodDes.NUT};
        Cursor foodCursor = foodDb.query(
                FoodDb.FoodDes.TABLE_NAME,  // The table to query
                projection2,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                // The sort order
        );
        float total = 0f;
        foodCursor.moveToFirst();
        while (foodCursor.getPosition() < 5)
        {
            total += Float.parseFloat(foodCursor.getString(foodCursor.getColumnIndex(FoodDb.FoodDes.NUT)));
            foodCursor.moveToNext();
        }
        //float[] data = new float[5];
        foodCursor.moveToFirst();
        while (foodCursor.getPosition() < 5)
        {
            Health.yData[foodCursor.getPosition()] =  (Float.parseFloat(foodCursor.getString(foodCursor.getColumnIndex(FoodDb.FoodDes.NUT))) / total) * 100;
            foodCursor.moveToNext();
        }
        float min = Health.yData[0];
        float max = Health.yData[0];
        int idMax = 0;
        int idMin = 0;
        int goodFlag = 1;
        for (int i = 1; i < 5; i++)
        {


                if (Math.abs(Health.yData[i - 1] - Health.yData[i]) > 20) goodFlag = 0;


            if (Health.yData[i] >= max)
            {
                max = Health.yData[i];
                idMax = i;
            }
            if (Health.yData[i] <= min)
            {
                min = Health.yData[i];
                idMin = i;
            }
        }
        if (goodFlag == 1)  Health.advice.setText("Bạn làm rất tốt. Việc giữ cân bằng dinh dưỡng rất quan trọng đối với sức khỏe");
        else if (idMax == 0) Health.advice.setText("Không nên ăn quá nhiều chất béo có thể dẫn đến béo phì và bệnh tim mạch");
        else if (idMin == 2) Health.advice.setText("Nên ăn thêm cá tôm để bổ sung chất đạm, chất đạm cấu thành nên các tế bào miễn dịch giúp bảo vệ cơ thể");
        else if (idMin == 1 || idMin == 3)  Health.advice.setText("Chất bột đường là chất quan trọng nhất, giúp phát triển não bộ và hệ thần kinh, nên ăn bổ sung các loại khoai, củ");
        else if (idMin == 4) Health.advice.setText("Nên ăn thêm nhiều trái cây, trái cây giúp bổ sung vitamin, tốt cho mắt, da và giữ nhiều vai trò quan trọng khác");
        Health.drawChartFood();
    }
    private void insert(int index, int id)
    {
        FoodDb.FoodDbHelper foodDbDbHelper = new FoodDb.FoodDbHelper(getApplicationContext());
        SQLiteDatabase foodDb = foodDbDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FoodDb.FoodDes.NUT, arr.get(index).getText().toString());
        String[] selectionArgs = {String.valueOf(id)};
        String selection = FoodDb.FoodDes._ID + " LIKE ?";
        foodDb.update(
                FoodDb.FoodDes.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }
    public static final class FoodDb {
        private static final String TEXT_TYPE = " TEXT";
        private static final String COMMA_SEP = ",";
        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + FoodDes.TABLE_NAME + " (" +
                        FoodDes._ID + " INTEGER PRIMARY KEY," +
                        FoodDes.FOOD + TEXT_TYPE + COMMA_SEP +
                        FoodDes.NUT + TEXT_TYPE + ")";
        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + FoodDes.TABLE_NAME;
        // To prevent someone from accidentally instantiating the contract class,
        // give it an empty constructor.

        public FoodDb() {
        }

        /* Inner class that defines the table contents */
        public static abstract class FoodDes implements BaseColumns {
            public static final String TABLE_NAME = "foodnut";
            public static final String FOOD = "foodname";
            public static final String NUT = "nutrition";
        }

        public static class FoodDbHelper extends SQLiteOpenHelper {
            // If you change the database schema, you must increment the database version.
            public static final int DATABASE_VERSION = 1;
            public static final String DATABASE_NAME = "FoodNut.db";

            public FoodDbHelper(Context context) {
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
