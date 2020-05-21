package com.example.timemanagment.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.example.timemanagment.R;
import com.example.timemanagment.adapters.TasksAdapter;
import com.example.timemanagment.models.DateModel;
import com.example.timemanagment.models.TaskDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Locale;

public class TasksActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    String userId;

    FloatingActionButton fab ;

    ArrayList<TaskDetails> tasks;
    RecyclerView rv_task;
    LinearLayoutManager linearLayoutManager ;
    TasksAdapter adapter ;

    String listId ;
    int listColor;
    String dateId;

    //firebase
    FirebaseFirestore db;

    //for saving settings
    SharedPreferences settingsPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settingsPreferences = getSharedPreferences("settings", MODE_PRIVATE);;
        if(settingsPreferences.getString("lang", "en").equals("ar"))
            setLocale(settingsPreferences.getString("lang", "en"));

        setContentView(R.layout.activity_tasks);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.title);
        title.setText(R.string.task);
        setTitle("");
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences("user info", MODE_PRIVATE);
        userId = sharedPreferences.getString("user_id", "123");

       listId = getIntent().getStringExtra("list_id");
       listColor =getIntent().getIntExtra("list_color",0);
       // databaseReference = FirebaseDatabase.getInstance().getReference("lists").child(listId);
        db = FirebaseFirestore.getInstance();

        tasks = new ArrayList<>();
        adapter = new TasksAdapter(TasksActivity.this, (ArrayList<TaskDetails>) tasks, listId , dateId);

        findViews();
        addListeners();

    }

    @Override
    protected void onStart() {
        super.onStart();

     //   listId = getIntent().getStringExtra("list_id");
        db.collection("lists").document(listId)
        .collection("date")
        .get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                tasks.clear();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        DateModel dateModel = document.toObject(DateModel.class);
                        dateId =dateModel.getId();

                        document.getReference().collection("tasks").whereEqualTo("day", dateModel.getDay())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        TaskDetails taskDetails = document.toObject(TaskDetails.class);
                                        tasks.add(taskDetails);
                                        adapter = new TasksAdapter(TasksActivity.this, (ArrayList<TaskDetails>) tasks, listId,dateId);
                                        rv_task.setAdapter(adapter);
                                    }
                                }
                            }
                        );
                    }
                }
            }
        });
    }

    private void findViews(){
        fab = findViewById(R.id.fab);
        rv_task = findViewById(R.id.rv_tasks);
        linearLayoutManager= new LinearLayoutManager(TasksActivity.this);
        rv_task.setLayoutManager(linearLayoutManager);
        tasks = new ArrayList<>();
    }

    private void addListeners(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TasksActivity.this, NewTaskActivity.class);
                intent.putExtra("list_id",listId);
                intent.putExtra("list_color",listColor);
                startActivity(intent);
            }
        });
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
