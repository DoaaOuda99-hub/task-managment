package com.example.timemanagment.models;

public class TaskDetails {

    private String id;
    private String taskName;
    private String taskTime;
    private String repeat;
    private String priority;
    private String note;
    private boolean isActive;
    int day, year;
    String month;
    int listColor;


    public TaskDetails() {
    }



    public TaskDetails(String id, String taskName, String taskTime, String repeat, String priority, String note, boolean isActive, int day, String month, int year, int listColor) {
        this.id = id;
        this.taskName = taskName;
        this.taskTime = taskTime;
        this.repeat = repeat;
        this.priority = priority;
        this.note = note;
        this.isActive = isActive;
        this.day = day;
        this.year = year;
        this.month = month;
        this.listColor= listColor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(String taskTime) {
        this.taskTime = taskTime;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getListColor() {
        return listColor;
    }

    public void setListColor(int listColor) {
        this.listColor = listColor;
    }
}

