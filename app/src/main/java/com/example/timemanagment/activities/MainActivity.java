package com.example.timemanagment.activities;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.example.timemanagment.R;
import com.example.timemanagment.fragments.CalendarFragment;
import com.example.timemanagment.fragments.HistoryFragment;
import com.example.timemanagment.fragments.ListsFragment;
import com.example.timemanagment.fragments.SettingsFragment;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //for saving settings
    SharedPreferences settingsPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settingsPreferences = getSharedPreferences("settings", MODE_PRIVATE);;
        if(settingsPreferences.getString("lang", "en").equals("ar"))
            setLocale(settingsPreferences.getString("lang", "en"));

        setContentView(R.layout.activity_main);

        settingsPreferences = getSharedPreferences("settings", MODE_PRIVATE);

        //................for bottom navigation...............//
        AHBottomNavigation bottomNavigation = findViewById(R.id.bottom_navigation);

        AHBottomNavigationItem item1 =
                new AHBottomNavigationItem(getResources().getString(R.string.calendar), R.drawable.ic_user);

        bottomNavigation.addItem(item1);
        AHBottomNavigationItem item2 =
                new AHBottomNavigationItem(getResources().getString(R.string.task), R.drawable.ic_tasks);
        bottomNavigation.addItem(item2);

        AHBottomNavigationItem item3 =
                new AHBottomNavigationItem(getResources().getString(R.string.history), R.drawable.ic_history_clock_button);
        bottomNavigation.addItem(item3);

        AHBottomNavigationItem item4 =
                new AHBottomNavigationItem(getResources().getString(R.string.settings), R.drawable.ic_settings);
        bottomNavigation.addItem(item4);

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                if(position == 3){
                    addFragmentt(new SettingsFragment());
                    return true;
                }else if(position == 0){
                    addFragmentt(new CalendarFragment());
                    return true;
                }else if(position == 1){
                    addFragmentt(new ListsFragment());
                    return true;
                }else if(position == 2){
                    addFragmentt(new HistoryFragment());
                    return true;
                }
                return false;
            }
        });
        // Setting the very 1st item as home screen.
        bottomNavigation.setCurrentItem(0);

        bottomNavigation.setDefaultBackgroundColor(getResources().getColor(R.color.colorPrimary));
        bottomNavigation.setAccentColor(getResources().getColor(R.color.white));
        bottomNavigation.setInactiveColor(getResources().getColor(R.color.colorAccent));
        //..................................................//

        //configureSettings();
    }

    public void addFragmentt(Fragment frag){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, frag);
        fragmentTransaction.commit();
    }

    public void configureSettings(){
        if(settingsPreferences.contains("lang")){
            if(settingsPreferences.getString("lang", "en").equals("en")){
                setLocale("en");
            }else
                setLocale("ar");
        }
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
