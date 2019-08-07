package com.example.nao_control;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.graphics.Color;
import android.icu.util.TimeZone;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.CalendarContract.Instances;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Calendars;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private Context contex;

    private TextView txvResult;
    private TextView txvResult2;
    private TextView txvResult3;
    private String sever_ip = "";
    receive_socket receive = new receive_socket();
    public static final String EXTRA_MESSAGE = "com.example.Nao_control.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contex = getApplicationContext();
        setContentView(R.layout.activity_main);
        txvResult = (TextView) findViewById(R.id.txvResult);
        txvResult2 = (TextView) findViewById(R.id.textView2);
        txvResult3 = (TextView) findViewById(R.id.textView3);
        receive.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void getSpeechInput(View view) {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());


        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        ArrayList<String> result;

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txvResult.setText(result.get(0));
                    user_Sorket socket = new user_Sorket();
                    socket.setMessage(result.get(0));
                    socket.setIp(this.sever_ip);
                    socket.execute();
                }
                break;
        }
    }

    public void Button_click(View v) {
        EditText editIp = (EditText) findViewById(R.id.editText);
        this.sever_ip = editIp.getText().toString();
        txvResult.setText(this.sever_ip);

        txvResult.setText(receive.get_json());

    }

    public void Button2_click(View v) {
        JSONObject jsonObject;
        Intent intent = null;
        String Thekey = "";
        String Thevalue = "";

        Intent intent_text = new Intent(this, text_present.class);
        Intent intent_speech = new Intent(this, testToSpeech.class);
        //Intent intent_voice = new Intent(this, text_to_voice.class);
        //Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();

        try {

            String json = receive.get_json();
            jsonObject = new JSONObject(json);
            txvResult.setText(json);

            //Toast.makeText(this, jsonObject.getString(jsonObject.names().get(0).toString()), Toast.LENGTH_SHORT).show();

            if (jsonObject.names().get(0).toString().equals("url") && jsonObject.getString(jsonObject.names().get(0).toString()).substring(0, 4).equals("http")) {
                Toast.makeText(this, "do not suuport", Toast.LENGTH_SHORT).show();

                Thevalue = jsonObject.getString(jsonObject.names().get(0).toString());
                Uri uri = Uri.parse(Thevalue);

                startActivity(new Intent(Intent.ACTION_VIEW, uri));


            } if (jsonObject.names().get(2).toString().equals("speech") && !jsonObject.getString(jsonObject.names().get(2).toString()).equals("")) {
                Toast.makeText(this, jsonObject.getString(jsonObject.names().get(2).toString()), Toast.LENGTH_SHORT).show();

                Thevalue = jsonObject.getString(jsonObject.names().get(2).toString());
                intent_speech.putExtra(EXTRA_MESSAGE, Thevalue);
                startActivity(intent_speech);
            } if (jsonObject.names().get(1).toString().equals("text") && !jsonObject.getString(jsonObject.names().get(1).toString()).equals("")) {

                Thevalue ="Anna say: "+ jsonObject.getString(jsonObject.names().get(1).toString());
                intent_text.putExtra(EXTRA_MESSAGE, Thevalue);
                txvResult2.setText(Thevalue);
                //startActivity(intent_text);

            } if (jsonObject.names().get(4).toString().equals("set_calendar") && !jsonObject.getString(jsonObject.names().get(4).toString()).equals("")) {
                String event = jsonObject.getString(jsonObject.names().get(4).toString());


                String[] event_array = event.split("\\|");

                CalendarReminderUtils my_calendar = new CalendarReminderUtils();
                Calendar cal = Calendar.getInstance();

                //long t_start = cal.getTime().getTime(); // Long.parseLong("String")
                //long t_end = cal.getTime().getTime()+10*60; //Long.parseLong("String")
                txvResult3.setText(event_array[0]);
                long t_start =  Long.parseLong(event_array[0]);
                long t_end =  Long.parseLong(event_array[1]);

                String title = "Ana sleep"; //
                String description = "I want to sleep";
                my_calendar.addCalendarEvent(this, title, description, t_start, t_end, t_start+10*60, 1);

                //CalendarContentResolver my2_calender = CalendarContentResolver(contex);

                //saveCalender(v);
                //get_cal_event();

            }
            if (jsonObject.names().get(3).toString().equals("get_calendar") && !jsonObject.getString(jsonObject.names().get(3).toString()).equals("")) {

                Thevalue = jsonObject.getString(jsonObject.names().get(3).toString());

                calendar my2_cal = new calendar();
                long start_time=0;
                long end_time=0;

                JSONArray json_event = my2_cal.getcalendar(this,start_time,end_time);


                user_Sorket socket2 = new user_Sorket();
                socket2.setJsonArray(json_event);
                //socket.setIp(this.sever_ip);
                socket2.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void saveCalender(View view) {
        Intent calendarIntent = new Intent(Intent.ACTION_INSERT, CalendarContract.Events.CONTENT_URI);
        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, 8, 1, 7, 30);
        calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calendar.getTimeInMillis());
        calendar.set(2019, 9, 1, 10, 30);
        calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, calendar.getTimeInMillis());
        calendarIntent.putExtra(CalendarContract.Events.TITLE, "上课");
        calendarIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, "CCIS 1-140");
        calendarIntent.putExtra(CalendarContract.Events.DESCRIPTION, "Osmar's course, do not miss");
        calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, CalendarContract.EXTRA_EVENT_ALL_DAY);
        startActivity(calendarIntent);
    }

    public void search_cal_event(){
        Calendar c_start= Calendar.getInstance();
        c_start.set(2019,7,24,0,0); //Note that months start from 0 (January)
        Calendar c_end= Calendar.getInstance();
        c_end.set(2013,7,25,0,0); //Note that months start from 0 (January)
        String selection = "((dtstart >= "+c_start.getTimeInMillis()+") AND (dtend <= "+c_end.getTimeInMillis()+"))";
        txvResult.setText(selection);
    }
}


/*
    public void get_calendar(){
        // Run query
        Cursor cur = null;
        ContentResolver cr = getContentResolver();
        Uri uri = Calendars.CONTENT_URI;
        String selection = "((" + Calendars.ACCOUNT_NAME + " = ?) AND ("
                + Calendars.ACCOUNT_TYPE + " = ?) AND ("
                + Calendars.OWNER_ACCOUNT + " = ?))";
        String[] selectionArgs = new String[] {"jiabin@ualberta.ca", "com.google",
                "jiabin@ualberta.ca"};
        // Submit the query and get a Cursor object back.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR}, MY_CAL_REQ);
            Toast.makeText(this, "you dont have permission to read calendar", Toast.LENGTH_SHORT).show();
        }
        cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);

        // Use the cursor to step through the returned records
        while (cur.moveToNext()) {
            String displayName = null;
            String accountName = null;
            String ownerName = null;

            // Get the field values
            long calID = cur.getLong(PROJECTION_ID_INDEX);
            displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
            accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);

            // Do something with the values...

        }
    }
*/
/*
    public void search_Calendar(){
        // ACTION_INSERT does not work on all phones
        // use  Intent.ACTION_EDIT in this case
        Cursor cur = null;
        ContentResolver cr = getContentResolver();

        String[] mProjection =
                {
                        CalendarContract.Calendars.ALLOWED_ATTENDEE_TYPES,
                        CalendarContract.Calendars.ACCOUNT_NAME,
                        CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                        CalendarContract.Calendars.CALENDAR_LOCATION,
                        CalendarContract.Calendars.CALENDAR_TIME_ZONE
                };

        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?) AND ("
                + CalendarContract.Calendars.OWNER_ACCOUNT + " = ?))";
        String[] selectionArgs = new String[]{"cal@zoftino.com", "cal.zoftino.com",
                "cal@zoftino.com"};

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR}, MY_CAL_REQ);
            Toast.makeText(this, "you dont have permission to read calendar", Toast.LENGTH_SHORT).show();

        }
        cur = cr.query(uri, mProjection, selection, selectionArgs, null);

        while (cur.moveToNext()) {
            String displayName = cur.getString(cur.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME));
            String accountName = cur.getString(cur.getColumnIndex(CalendarContract.Calendars.ACCOUNT_NAME));

            txvResult.setText(displayName);
            //cont.addView(tv1);
        }
    }
    */


/*
    public void getCalendarEvent() {

        String startTime = "";
        String endTime = "";
        String eventTitle = "";
        String description = "";
        String location = "";

        long startEventTime;
        long currentTime;
        List<Long> listTime;
        listTime = new ArrayList<Long>();
        Map map = new HashMap();

        Cursor eventCursor = getContentResolver().query(Uri.parse(CALENDER_EVENT_URL), null, null, null, null);

        eventCursor.moveToNext();
        while (eventCursor.moveToNext()) {
            Log.i(TAG, "·········································· ");
            eventTitle = eventCursor.getString(eventCursor.getColumnIndex("title"));    //获取日历事件 的标题
            description = eventCursor.getString(eventCursor.getColumnIndex("description"));  //获取日历事件 的描述
            location = eventCursor.getString(eventCursor.getColumnIndex("eventLocation"));  //获取日历事件 的地点
            startEventTime = Long.parseLong(eventCursor.getString(eventCursor.getColumnIndex("dtstart"))); //获取 日程 开始的时间
            Log.i(TAG, "startEventTime：  " + startEventTime);
            currentTime = Calendar.getInstance().getTimeInMillis();  //获取当前时间
            Log.i(TAG, "currentTime：  " + currentTime);
            if (startEventTime > currentTime) {  //当日历设定时间大于当前时间
                listTime.add(startEventTime);
                map.put(startEventTime, description);//存储键值
            }
            startTime = timeStamp2Date(startEventTime);
            endTime = timeStamp2Date(Long.parseLong(eventCursor.getString(eventCursor.getColumnIndex("dtend")))); //获取日程结束的时间
            Log.i(TAG, "eventTitle: " + eventTitle + "\n" +
                    "description: " + description + "\n" +
                    "location: " + location + "\n" +
                    "startTime: " + startTime + "\n" +
                    "endTime: " + endTime + "\n"
            );
        }

        txvResult.setText(eventTitle);
        Collections.sort(listTime); //将list 从小到大排序 根据时间大小 获取最近事件
        Object value = map.get(listTime.get(0)); //获取键所对应的值
        Log.i(TAG,"获取最近一次事件：" + value);


    }
    private static String timeStamp2Date(long time) {
        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(time));
    }


    protected  void  onDestroy() {
        unregisterObserver();
        super.onDestroy();
    }
    private synchronized void unregisterObserver() {
        try {
            if (newCalendarContentObserver != null) {
                getContentResolver().unregisterContentObserver(newCalendarContentObserver);
            }
        } catch (Exception e) {
            Log.e(TAG, "unregisterObserver fail");
        }

    }
    private ContentObserver newCalendarContentObserver = new ContentObserver(new Handler()) {
        public void onChange(boolean selfChange) {
            System.out.println("接收到的日历事件监听");
            getCalendarEvent();
        }

    };
*/


