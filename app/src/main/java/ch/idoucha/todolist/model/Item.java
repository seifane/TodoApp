package ch.idoucha.todolist.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.github.thunder413.datetimeutils.DateTimeStyle;
import com.github.thunder413.datetimeutils.DateTimeUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import ch.idoucha.todolist.notification.NotificationBroadcastReceiver;
import ninja.sakib.pultusorm.annotations.AutoIncrement;
import ninja.sakib.pultusorm.annotations.PrimaryKey;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Seïfane Idouchach on 1/29/2018.
 */

public class Item {

    public static final int STATE_ACTIVE = 0;
    public static final int STATE_DONE = 1;

    @PrimaryKey
    @AutoIncrement
    public int id;
    public String title;
    public String content;
    public int status;
    public long date;

    public Item(String title, String content) {
        this.title = title;
        this.content = content;
        this.status = STATE_ACTIVE;
        this.date = 0;
    }

    public Item(String title, String content, long date) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.status = STATE_ACTIVE;
    }

    public Item() {
        this.title = "";
        this.content = "";
        this.date = 0;
        this.status = STATE_ACTIVE;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDateString() {
        if (date == 0)
            return "";
        return DateTimeUtils.formatWithStyle(new Date(date), DateTimeStyle.MEDIUM);
    }

    public String getTimeString() {
        if (date == 0)
            return "";
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(date));
        cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + 1);
        return DateTimeUtils.formatWithPattern(cal.getTime(), "HH:mm");
    }

    public String getDateTimeString() {
        return getDateString() + " " + getTimeString();
    }

    public Date getDateAsDate() {
        if (date == 0)
            return null;
        return new Date(date);
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void schedule(Context context) {
        if (date == 0)
            return;
        Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
        intent.putExtra("ID", this.id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, new Random().nextInt(), intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        Log.d("DEBUG", "current = " + System.currentTimeMillis() + " date = " + date );
        alarmManager.set(AlarmManager.RTC_WAKEUP, date, pendingIntent);
    }
}
