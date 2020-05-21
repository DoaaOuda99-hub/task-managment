package com.example.timemanagment.models;

public class DateModel {
    int day , year , month ;
    String id ;String monthName;

    public DateModel() {
    }

    public DateModel(int day, int year, int month) {
        this.day = day;
        this.year = year;
        this.month = month;
    }

    public DateModel(String id,int day, int year, String monthName) {
        this.day = day;
        this.year = year;
        this.monthName = monthName;
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

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
