package com.example.timemanagment.activities;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.example.timemanagment.R;

public class ResetPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.reset_pass);
        ImageView toolbarBackArrow = toolbar.findViewById(R.id.Icon_back_arrow);

        toolbarBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setSupportActionBar(toolbar);

    }
}
