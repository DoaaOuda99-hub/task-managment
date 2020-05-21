package com.example.timemanagment.activities;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.timemanagment.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {
    EditText et_email;
    Button btn_send;

    SharedPreferences preferences ;
    String email ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.forget_password);
        ImageView toolbarBackArrow = toolbar.findViewById(R.id.Icon_back_arrow);

        toolbarBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setSupportActionBar(toolbar);

        preferences = getSharedPreferences("email", MODE_PRIVATE);

        findViews();
        clickListeners();

    }


    private void findViews(){
        et_email = findViewById(R.id.et_email);
        btn_send = findViewById(R.id.btn_send);
    }


    private void clickListeners(){
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = et_email.getText().toString();
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ForgetPasswordActivity.this, "successfully send reset email", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
