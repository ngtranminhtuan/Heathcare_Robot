package com.example.tuantu.week7_findplacenearby;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by TUAN TU on 5/30/2016.
 */
public class Setting extends Activity {
    static Spinner message_num;
    static Spinner call_num;
    static Button btnSave;
    static Button btnCancel;
    static EditText txtMessContent;
    static EditText txtMessContact;
    static CheckBox isMess;

    static EditText txtCallContact;
    static CheckBox isCall;

    static SQLiteDatabase settingDb;
    public static String _messageContent;
    public static String _messageContact;
    public static String _isCall;
    public static String _isMess;
    public static String _callContact;
    public static int _numCall;
    public static int _numMess;
    static ArrayAdapter<String> spinnerArrayAdapter;
    static Context c;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        message_num = (Spinner)findViewById(R.id.number_mess);
        call_num = (Spinner)findViewById(R.id.number_call);
        btnSave = (Button)findViewById(R.id.btn_save);
        btnCancel = (Button)findViewById(R.id.btn_cancel);
        txtMessContent = (EditText)findViewById(R.id.mess_content);
        txtMessContact = (EditText)findViewById(R.id.mess_contact);
        isMess = (CheckBox)findViewById(R.id.is_mess);
        isCall = (CheckBox)findViewById(R.id.is_call);
        txtCallContact = (EditText)findViewById(R.id.call_number);

        c = this;

        String[] ar = {"2","3", "4", "5", "6"};
        spinnerArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, ar);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        message_num.setAdapter(spinnerArrayAdapter);
        call_num.setAdapter(spinnerArrayAdapter);

        databseProcess();
        txtMessContent.setText(_messageContent);
        txtMessContact.setText(_messageContact);
        txtCallContact.setText(_callContact);

        if (_isCall.equals("true"))
            isCall.setChecked(true);
        else  isCall.setChecked(false);

        if (_isMess.equals("true"))
            isMess.setChecked(true);
        else isMess.setChecked(false);

        int spinnerPosition = spinnerArrayAdapter.getPosition(String.valueOf(_numMess));
        message_num.setSelection(spinnerPosition);

        spinnerPosition = spinnerArrayAdapter.getPosition(String.valueOf(_numCall));
        call_num.setSelection(spinnerPosition);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try {
                    ContentValues values = new ContentValues();

                    values.put(SettingDb.SettingDes.ISMESS, String.valueOf(isMess.isChecked()));
                    values.put(SettingDb.SettingDes.MESSCONTENT, txtMessContent.getText().toString());
                    values.put(SettingDb.SettingDes.MESSCONTACT, txtMessContact.getText().toString());
                    values.put(SettingDb.SettingDes.MESSNUM, message_num.getSelectedItem().toString());
                    values.put(SettingDb.SettingDes.ISCALL, String.valueOf(isCall.isChecked()));
                    values.put(SettingDb.SettingDes.CALLCONTACT, txtCallContact.getText().toString());
                    values.put(SettingDb.SettingDes.CALLNUM, call_num.getSelectedItem().toString());
                    settingDb.update(SettingDb.SettingDes.TABLE_NAME,
                            values,
                            null,
                            null);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

                Toast.makeText(c, "Đã lưu", Toast.LENGTH_LONG).show();
                Activity a = (Activity)c;
                a.finish();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity a = (Activity)c;
                a.finish();
            }
        });
    }
    public static void databseProcess()
    {
        final SettingDb.SettingDbHelper settingDbHelper = new SettingDb.SettingDbHelper(MapsActivity._context);
        settingDb = settingDbHelper.getWritableDatabase();
        String[] projection = {
                SettingDb.SettingDes.ISMESS,
                SettingDb.SettingDes.MESSCONTENT,
                SettingDb.SettingDes.MESSCONTACT,
                SettingDb.SettingDes.MESSNUM,
                SettingDb.SettingDes.ISCALL,
                SettingDb.SettingDes.CALLCONTACT,
                SettingDb.SettingDes.CALLNUM
        };

        Cursor settingCursor = settingDb.query(
                SettingDb.SettingDes.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                // The sort order
        );

        settingCursor.moveToFirst();
        if (settingCursor.getCount() > 0)
        {
            //_isMess = Integer.parseInt(settingCursor.getString(settingCursor.getColumnIndex(SettingDb.SettingDes.ISMESS)));
            _isMess = settingCursor.getString(settingCursor.getColumnIndex(SettingDb.SettingDes.ISMESS));
            _messageContent = settingCursor.getString(settingCursor.getColumnIndex(SettingDb.SettingDes.MESSCONTENT));
            _messageContact = settingCursor.getString(settingCursor.getColumnIndex(SettingDb.SettingDes.MESSCONTACT));
            _numMess = Integer.parseInt(settingCursor.getString(settingCursor.getColumnIndex(SettingDb.SettingDes.MESSNUM)));
            _isCall = settingCursor.getString(settingCursor.getColumnIndex(SettingDb.SettingDes.ISCALL));
            _callContact = settingCursor.getString(settingCursor.getColumnIndex(SettingDb.SettingDes.CALLCONTACT));
            _numCall = Integer.parseInt(settingCursor.getString(settingCursor.getColumnIndex(SettingDb.SettingDes.CALLNUM)));


        }
        else
        {
            ContentValues values = new ContentValues();

            values.put(SettingDb.SettingDes.ISMESS, "false");
            values.put(SettingDb.SettingDes.MESSCONTENT, "");
            values.put(SettingDb.SettingDes.MESSCONTACT, "");
            values.put(SettingDb.SettingDes.MESSNUM, "2");
            values.put(SettingDb.SettingDes.ISCALL, "false");
            values.put(SettingDb.SettingDes.CALLCONTACT, "");
            values.put(SettingDb.SettingDes.CALLNUM, "2");
            settingDb.insert(SettingDb.SettingDes.TABLE_NAME,
                    "",
                    values);
        }
    }
    public static final class SettingDb {
        private static final String TEXT_TYPE = " TEXT";
        private static final String COMMA_SEP = ",";
        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + SettingDes.TABLE_NAME + " (" +
                        SettingDes._ID + " INTEGER PRIMARY KEY," +
                        SettingDes.ISMESS + TEXT_TYPE + COMMA_SEP +
                        SettingDes.MESSCONTENT + TEXT_TYPE + COMMA_SEP +
                        SettingDes.MESSCONTACT + TEXT_TYPE + COMMA_SEP +
                        SettingDes.MESSNUM + TEXT_TYPE + COMMA_SEP +
                        SettingDes.ISCALL + TEXT_TYPE + COMMA_SEP +
                        SettingDes.CALLCONTACT + TEXT_TYPE + COMMA_SEP +
                        SettingDes.CALLNUM + TEXT_TYPE + ")";
        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + SettingDes.TABLE_NAME;
        // To prevent someone from accidentally instantiating the contract class,
        // give it an empty constructor.

        public SettingDb() {

        }

        /* Inner class that defines the table contents */
        public static abstract class SettingDes implements BaseColumns {
            public static final String TABLE_NAME = "Settingnut";
            public static final String ISMESS = "isMess";
            public static final String MESSCONTENT = "MessageContent";
            public static final String MESSCONTACT = "MessageContact";
            public static final String MESSNUM = "MessageNum";
            public static final String ISCALL = "isCall";
            public static final String CALLCONTACT = "CallContact";
            public static final String CALLNUM = "CallNum";
        }

        public static class SettingDbHelper extends SQLiteOpenHelper {
            // If you change the database schema, you must increment the database version.
            public static final int DATABASE_VERSION = 1;
            public static final String DATABASE_NAME = "Setting.db";

            public SettingDbHelper(Context context) {
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
