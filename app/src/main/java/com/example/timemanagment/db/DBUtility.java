package com.example.timemanagment.db;

// adapter description below
/*
* this class has all the processes and methods that will be  done on he DB
* operations are, insert, delete, update, and get(select)
* cursor acts as a pointer in getting the ows from the db*/
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.timemanagment.models.User;

import java.util.ArrayList;

public class DBUtility {

    private final String DATABASE_TABLE = "friends";

    private DBHelper dbHelper;
    private Context context;

    public DBUtility(Context context) {
        this.context = context;
        dbHelper = new DBHelper(context);
    }

    public long insertContact(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long result = 0;

        ContentValues contentValues = new ContentValues();

        contentValues.put("name", user.getName());
        contentValues.put("email", user.getEmail());
        contentValues.put("phone", user.getMobile());

        result = db.insert("user", null, contentValues);

        return result;
    }

}
