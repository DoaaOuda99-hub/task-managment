package com.example.timemanagment.activities;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.timemanagment.R;
import com.example.timemanagment.adapters.RingtoneAdapter;

import java.util.ArrayList;
import java.util.Locale;

public class AlarmSettingsActivity extends AppCompatActivity {

    RelativeLayout layout_ringtone;
    TextView txt_ringtone;

    RingtoneAdapter ringtoneAdapter;
    ArrayList<String> ringtoneList;

    //for saving settings
    SharedPreferences settingsPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settingsPreferences = getSharedPreferences("settings", MODE_PRIVATE);;
        if(settingsPreferences.getString("lang", "en").equals("ar"))
            setLocale(settingsPreferences.getString("lang", "en"));

        setContentView(R.layout.activity_alarm_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.title);
        title.setText(R.string.alarm_settings);
        setTitle("");
        setSupportActionBar(toolbar);

        findViews();
    }

    public void findViews(){
        layout_ringtone = findViewById(R.id.layout_ringtone);


        layout_ringtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(AlarmSettingsActivity.this);
                dialog.setContentView(R.layout.dialog_ringtone_list);
                dialog.show();
                RecyclerView rv_ringtoneList = dialog.findViewById(R.id.rv_ringtone);
                Button btn_save = dialog.findViewById(R.id.btn_save);
                Button btn_cacel = dialog.findViewById(R.id.btn_cancel);

                //to set ring tones into the list
                ringtoneList = listRingtones();
                ringtoneAdapter = new RingtoneAdapter(AlarmSettingsActivity.this, ringtoneList);
                rv_ringtoneList.setLayoutManager(new LinearLayoutManager(AlarmSettingsActivity.this));
                rv_ringtoneList.setAdapter(ringtoneAdapter);

                btn_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //change ringtone operation
                        dialog.dismiss();
                    }
                });

                btn_cacel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    public void chooseRingTone(View view){
        Dialog dialog = new Dialog(AlarmSettingsActivity.this);
        dialog.setContentView(R.layout.dialog_ringtone_list);
        dialog.show();
        RecyclerView rv_ringtoneList = dialog.findViewById(R.id.rv_ringtone);
        Button btn_save = dialog.findViewById(R.id.btn_save);
        Button btn_cacel = dialog.findViewById(R.id.btn_cancel);

        //to set ring tones into the list
        ringtoneList = listRingtones();
        ringtoneAdapter = new RingtoneAdapter(AlarmSettingsActivity.this, ringtoneList);
        rv_ringtoneList.setLayoutManager(new LinearLayoutManager(AlarmSettingsActivity.this));
        rv_ringtoneList.setAdapter(ringtoneAdapter);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change ringtone operation
                dialog.dismiss();
            }
        });

        btn_cacel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public ArrayList<String> listRingtones() {
        ArrayList<String> list = new ArrayList<>();

        RingtoneManager manager = new RingtoneManager(this);
        manager.setType(RingtoneManager.TYPE_RINGTONE);
        Cursor cursor = manager.getCursor();
        while (cursor.moveToNext()) {
            String title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            list.add(title);
            Uri ringtoneURI = manager.getRingtoneUri(cursor.getPosition());
            // Do something with the title and the URI of ringtone
        }

        return list;
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
