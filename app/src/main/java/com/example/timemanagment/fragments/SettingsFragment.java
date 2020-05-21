package com.example.timemanagment.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timemanagment.R;
import com.example.timemanagment.activities.AboutActivity;
import com.example.timemanagment.activities.AccountSettingsActivity;
import com.example.timemanagment.activities.AlarmSettingsActivity;
import com.example.timemanagment.activities.MainActivity;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * by: amal;
 * */
public class SettingsFragment extends Fragment {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    TextView account_txt, lang_txt, notification_txt, mood_txt,
            clock_pattern_txt, about_txt, feedback_txt, help_txt, share_txt;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.title);
        title.setText(R.string.settings);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).setTitle("");

        sharedPreferences = getContext().getSharedPreferences("settings", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //attaching views
        findViews();

        //enable click
        clickListeners();
        return view;
    }

    //for finding views
    public void findViews(){
        account_txt = view.findViewById(R.id.txt_account);
        lang_txt = view.findViewById(R.id.txt_lang);
        notification_txt = view.findViewById(R.id.txt_alarm_settings);
        mood_txt = view.findViewById(R.id.txt_mood);
        clock_pattern_txt = view.findViewById(R.id.txt_clock_patern);
        about_txt = view.findViewById(R.id.txt_about);
        feedback_txt = view.findViewById(R.id.txt_feedback);
        help_txt = view.findViewById(R.id.txt_help);
        share_txt = view.findViewById(R.id.txt_share);
    }

    public void clickListeners(){
        account_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AccountSettingsActivity.class);
                getContext().startActivity(intent);
            }
        });

        notification_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AlarmSettingsActivity.class);
                getContext().startActivity(intent);
            }
        });

        about_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AboutActivity.class);
                getContext().startActivity(intent);
            }
        });

        lang_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_app_lang);
                dialog.show();

                RadioButton btn_eng = dialog.findViewById(R.id.rb_en);
                RadioButton btn_ar = dialog.findViewById(R.id.rb_ar);

                if(sharedPreferences.getString("lang", "en").equals("en"))
                    btn_eng.setChecked(true);
                else
                    btn_ar.setChecked(true);

                btn_eng.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //chang to english lang operation
                        setLocale("en");

                        editor.putString("lang", "en");
                        editor.commit();

                        new Handler().postDelayed(new Runnable(){
                            @Override
                            public void run() {
                                /* Create an Intent that will start the Menu-Activity. */
                                setLocale("en");
                                dialog.dismiss();
                            }
                        }, 500);
                    }
                });

                btn_ar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //chang to english lang operation
                        setLocale("ar");

                        editor.putString("lang", "ar");
                        editor.commit();

                        new Handler().postDelayed(new Runnable(){
                            @Override
                            public void run() {
                                /* Create an Intent that will start the Menu-Activity. */
                                dialog.dismiss();
                            }
                        }, 500);
                    }
                });
            }
        });

        mood_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_app_mood);
                dialog.show();

                RadioButton lightMood = dialog.findViewById(R.id.rb_light);
                RadioButton darkMood = dialog.findViewById(R.id.rb_dark);

                if(sharedPreferences.getString("mood", "light").equals("light"))
                    lightMood.setChecked(true);
                else
                    darkMood.setChecked(true);

                lightMood.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //chang to light mood operation

                        editor.putString("mood", "light");
                        editor.commit();

                        new Handler().postDelayed(new Runnable(){
                            @Override
                            public void run() {
                                /* Create an Intent that will start the Menu-Activity. */
                                dialog.dismiss();
                            }
                        }, 500);
                    }
                });

                darkMood.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //chang to dark mood operation

                        editor.putString("mood", "dark");
                        editor.commit();

                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        Intent refresh = new Intent(getContext(), MainActivity.class);
                        startActivity(refresh);
                        getActivity().finish();

                        new Handler().postDelayed(new Runnable(){
                            @Override
                            public void run() {
                                /* Create an Intent that will start the Menu-Activity. */
                                dialog.dismiss();
                            }
                        }, 500);
                    }
                });
            }
        });

        clock_pattern_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_clock_pattern);
                dialog.show();

                RadioButton btn_12h = dialog.findViewById(R.id.rb_12h);
                RadioButton btn_24h = dialog.findViewById(R.id.rb_24h);

                if(!sharedPreferences.getBoolean("is24h", false))
                    btn_24h.setChecked(true);
                else
                    btn_12h.setChecked(true);

                btn_12h.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //change clock pattern operation

                        editor.putBoolean("is24h", false);
                        editor.commit();

                        new Handler().postDelayed(new Runnable(){
                            @Override
                            public void run() {
                                /* Create an Intent that will start the Menu-Activity. */
                                dialog.dismiss();
                            }
                        }, 500);
                    }
                });

                btn_24h.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //change clock pattern operation

                        editor.putBoolean("is24h", true);
                        editor.commit();

                        new Handler().postDelayed(new Runnable(){
                            @Override
                            public void run() {
                                /* Create an Intent that will start the Menu-Activity. */
                                dialog.dismiss();
                            }
                        }, 500);
                    }
                });
            }
        });

        feedback_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_feedback);
                dialog.show();

                EditText text = dialog.findViewById(R.id.et_feedback);
                Button btn_send = dialog.findViewById(R.id.btn_send);
                Button btn_cancel = dialog.findViewById(R.id.btn_cancel);

                btn_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!text.getText().toString().equals("")){
                            String[] TO = {"amal.alkhatib.99@gmail.com"};
                            Intent emailIntent = new Intent(Intent.ACTION_SEND);
                            emailIntent.setType( "message/rfc822");

                            emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Help");
                            emailIntent.putExtra(Intent.EXTRA_TEXT, text.getText().toString());
                            try {
                                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                            } catch (android.content.ActivityNotFoundException ex) {
                                Toast.makeText(getContext(),
                                        "There is no email client installed.", Toast.LENGTH_SHORT).show();
                            }
                        }else
                            Toast.makeText(getContext(), "the text field is empty!", Toast.LENGTH_LONG).show();
                    }
                });

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        help_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] TO = {"amal.alkhatib.99@gmail.com"};
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("message/rfc822");

                emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Help!");

                try {
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getContext(),
                            "There is no email client installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        share_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
                shareIntent.setType("text/plain");

                try {
                    startActivity(Intent.createChooser(shareIntent, "Share via..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getContext(),
                            "There is no email client installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(getContext(), MainActivity.class);
        startActivity(refresh);
        getActivity().finish();
    }
}
