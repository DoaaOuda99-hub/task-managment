package com.example.timemanagment.models;

public class HistoryTask {

    private String id, title, note, date, time;
    private boolean isChecked;

    public HistoryTask(String id, String title, String note, String date, String time, boolean isChecked) {
        this.id = id;
        this.title = title;
        this.note = note;
        this.date = date;
        this.time = time;
        this.isChecked = isChecked;
    }

    public HistoryTask() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
