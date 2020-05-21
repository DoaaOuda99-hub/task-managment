package com.example.timemanagment.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timemanagment.R;
import com.example.timemanagment.db.DBHelper;
import com.example.timemanagment.db.DBUtility;
import com.example.timemanagment.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.Map;

import petrov.kristiyan.colorpicker.ColorPicker;

public class LoginActivity extends AppCompatActivity {
    EditText et_email ;
    EditText et_password;
    Button btn_login;

    String userId;
    SharedPreferences.Editor editor ;

    //firebase
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        TextView toolbarTitle = toolbar.findViewById(R.id.tv_title);
        toolbarTitle.setText(R.string.login);
        ImageView toolbarBackArrow = toolbar.findViewById(R.id.Icon_back_arrow);
        toolbarBackArrow.setImageBitmap(null);
        setSupportActionBar(toolbar);

        findViews();

        //firebase init
        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();

    }


    public void findViews(){
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        btn_login = findViewById(R.id.btn_login);

    }
    public void signUp(View view) {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);

    }

    public void forgetPass(View view) {
        Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
        startActivity(intent);

    }

    public void login(View view ){

        if(et_email.getText().toString().equals("") || et_password.getText().toString().equals("")){
            Toast.makeText(LoginActivity.this, "Both fields are required", Toast.LENGTH_LONG).show();
        }else if(et_password.getText().toString().length() < 6){
            Toast.makeText(LoginActivity.this, "password must be +6 chars", Toast.LENGTH_LONG).show();
        }else{
            auth.signInWithEmailAndPassword(et_email.getText().toString(),
                    et_password.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                userId = task.getResult().getUser().getUid();
                                //save user_id in shared preferences
                                editor = getSharedPreferences("user info", MODE_PRIVATE).edit();
                                editor.putString("email", et_email.getText().toString());
                                editor.putString("password", et_password.getText().toString());
                                editor.putString("user_id" , userId);
                                editor.apply();

                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                DocumentReference doc = db.collection("users")
                                        .document(userId);
                                doc.get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                DocumentSnapshot document = task.getResult();
                                                Map<String, Object> map = document.getData();

                                                //saving user info in shared preference
                                                editor.putString("username", map.get("username").toString());
                                                editor.putString("name", map.get("name").toString());
                                                editor.putString("mobileNumber", map.get("mobileNumber").toString());
                                                editor.putString("profileImg", map.get("profileImg").toString());
                                                editor.commit();

                                                //moving to user profile
                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                                                DBUtility dbUtility = new DBUtility(LoginActivity.this);
                                                dbUtility.insertContact
                                                        (new User( map.get("name").toString(),
                                                                map.get("email").toString(),
                                                                map.get("mobileNumber").toString()));

                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                            }
                                        });
                            }else
                                Toast.makeText(LoginActivity.this, "not registered", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(LoginActivity.this, e.getMessage() , Toast.LENGTH_LONG).show();
                }
            });
        }
    }

}

