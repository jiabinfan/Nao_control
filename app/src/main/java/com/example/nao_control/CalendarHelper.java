package com.example.nao_control;


import java.util.Calendar;
import java.util.TimeZone;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract.Reminders;

//import com.example.testshellcmd.SharedApplication;

public class CalendarHelper {
    /**
     * 后台插入一条日历事件
     * @param title 时间标题
     * @param descrip 描述
     * @param startTime 事件开始时间，例2015年11月22号10点
     * 			Calendar mCalendar = Calendar.getInstance();
     *			mCalendar.set(2015, 10, 22, 10, 0);
     * @param startTime 事件结束时间
     */
    public static void insertCalendar(String title, String descrip,String location,
                                      Calendar startTime, Calendar endTime, Context context) {
        String calanderURL;
        String calanderEventURL;
        String calanderRemiderURL;
        if (Integer.parseInt(Build.VERSION.SDK) >= 8) {
            calanderURL = "content://com.android.calendar/calendars";
            calanderEventURL = "content://com.android.calendar/events";
            calanderRemiderURL = "content://com.android.calendar/reminders";
        } else {
            calanderURL = "content://calendar/calendars";
            calanderEventURL = "content://calendar/events";
            calanderRemiderURL = "content://calendar/reminders";
        }
        // 获取要出入的gmail账户的id
        String calId = "";
        Cursor userCursor = context.getContentResolver()
                .query(Uri.parse(calanderURL), null, null, null, null);
        if (userCursor.getCount() > 0) {
            userCursor.moveToFirst();
            calId = userCursor.getString(userCursor.getColumnIndex("_id"));
            android.util.Log.e("cxq", calId);

        }
        ContentValues event = new ContentValues();
        event.put("title", title);
        event.put("description", descrip);
        event.put("calendar_id", calId);
        event.put("eventLocation", location);

        // 全天事件
        // event.put("allDay", true);

        long start = startTime.getTime().getTime();
        long end = endTime.getTime().getTime();
        event.put("dtstart", start);
        event.put("dtend", end);

        event.put("hasAlarm", 1);
        // 设置时区
        event.put("eventTimezone", TimeZone.getDefault().getID().toString());

        Uri newEvent = context.getContentResolver().insert(
                Uri.parse(calanderEventURL), event);
        long id = Long.parseLong(newEvent.getLastPathSegment());
        ContentValues values = new ContentValues();
        values.put("event_id", id);
        // 提前一天提醒
        values.put(Reminders.MINUTES, 1 * 60 * 24);
        values.put(Reminders.EVENT_ID, id);
        values.put(Reminders.METHOD, Reminders.METHOD_ALERT);
        ContentResolver cr1 = context.getContentResolver(); // 为刚才新添加的event添加reminder
        cr1.insert(Reminders.CONTENT_URI, values);
    }

    /**
     * 带图形界面
     * @author meitu
     * @time 2015-10-21下午2:28:10
     */
    public static void insertWithActivity(Context context) {
        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", cal.getTimeInMillis());
        intent.putExtra("allDay", true);
        intent.putExtra("rrule", "FREQ=YEARLY");
        intent.putExtra("endTime", cal.getTimeInMillis() + 60 * 60 * 2000);
        intent.putExtra("title", "Test Event");
        intent.putExtra("description", "This is a sample description");
        context.startActivity(intent);
    }

}
