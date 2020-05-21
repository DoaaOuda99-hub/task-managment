package com.example.timemanagment.activities;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.widget.TextView;

import com.example.timemanagment.R;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class AboutActivity extends AppCompatActivity {

    SharedPreferences settingsPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settingsPreferences = getSharedPreferences("settings", MODE_PRIVATE);;
        if(settingsPreferences.getString("lang", "en").equals("ar"))
            setLocale(settingsPreferences.getString("lang", "en"));

        setContentView(R.layout.activity_about);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.title);
        title.setText(R.string.about);
        setTitle("");
        setSupportActionBar(toolbar);
    }

    //for language configuration
    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }
}
