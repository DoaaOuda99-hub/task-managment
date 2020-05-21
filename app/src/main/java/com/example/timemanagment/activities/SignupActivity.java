package com.example.timemanagment.activities;

import android.content.Intent;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {

    EditText et_username, et_name, et_email, et_mobile, et_pass, et_passConfirm;
    Button btn_signUp, btn_signUpGoogle;

    //firebase
    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar = findViewById(R.id.toolbar);

        TextView toolbarTitle = toolbar.findViewById(R.id.title);
        toolbarTitle.setText(R.string.sign_up);
        ImageView toolbarBackArrow = toolbar.findViewById(R.id.Icon_back_arrow);
        setSupportActionBar(toolbar);

        findViews();

        //firebase init
        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();

    }

    public void findViews() {
        et_username = findViewById(R.id.et_username);
        et_name = findViewById(R.id.et_name);
        et_email = findViewById(R.id.et_email);
        et_mobile = findViewById(R.id.et_mobile_number);
        et_pass = findViewById(R.id.et_password);
        et_passConfirm = findViewById(R.id.et_confirm_pass);
        btn_signUp = findViewById(R.id.btn_sign_up);
        btn_signUpGoogle = findViewById(R.id.btn_sign_up_with_google);

    }

    public void signUp(View view){
        if(et_username.getText().toString().equals("") || et_name.getText().toString().equals("") ||
                et_email.getText().toString().equals("") || et_pass.getText().toString().equals("") ||
                et_passConfirm.getText().toString().equals("") || et_mobile.getText().toString().equals("")){
            Toast.makeText(this, "All fields must be filled", Toast.LENGTH_SHORT).show();
        }else if(!(et_pass.getText().toString().equals(et_passConfirm.getText().toString()))){
            et_passConfirm.setError("doesn't match");
        }else{
            auth.createUserWithEmailAndPassword(et_email.getText().toString(), et_pass.getText().toString()).
                    addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        assert firebaseUser != null;
                        String userID = firebaseUser.getUid();

                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("id", userID);
                        hashMap.put("username", et_username.getText().toString());
                        hashMap.put("name", et_name.getText().toString());
                        hashMap.put("mobileNumber", et_mobile.getText().toString());
                        hashMap.put("profileImg", "default");

                        db.collection("users")
                                .document(userID)
                                .set(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(SignupActivity.this, "success", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                        });

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignupActivity.this , e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void login(View view){
        Intent intent = new Intent(SignupActivity.this , LoginActivity.class);
        startActivity(intent);
    }
}