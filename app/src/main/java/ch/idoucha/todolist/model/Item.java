package ch.idoucha.todolist.model;

import com.github.thunder413.datetimeutils.DateTimeStyle;
import com.github.thunder413.datetimeutils.DateTimeUtils;

import java.util.Date;

import ninja.sakib.pultusorm.annotations.AutoIncrement;
import ninja.sakib.pultusorm.annotations.PrimaryKey;

/**
 * Created by Seïfane Idouchach on 1/29/2018.
 */

public class Item {
    @PrimaryKey
    @AutoIncrement
    public int id;
    public String title;
    public String content;
    public boolean isDone;
    public long date;

    public Item(String title, String content) {
        this.title = title;
        this.content = content;
        this.isDone = false;
        this.date = 0;
    }

    public Item(String title, String content, long date) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.isDone = false;
    }

    public Item() {
        this.title = "";
        this.content = "";
        this.date = 0;
        this.isDone = false;
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

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public String getDateString() {
        if (date == 0)
            return "";
        return DateTimeUtils.formatWithStyle(new Date(date), DateTimeStyle.MEDIUM);
    }

    public String getTimeString() {
        if (date == 0)
            return "";
        return DateTimeUtils.formatTime(new Date(), true);
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
}
