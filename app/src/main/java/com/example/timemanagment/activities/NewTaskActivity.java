package com.example.timemanagment.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timemanagment.R;
import com.example.timemanagment.fragments.DatePickerFragment;
import com.example.timemanagment.fragments.TimePickerFragment;
import com.example.timemanagment.models.DateModel;
import com.example.timemanagment.models.TaskDetails;
import com.example.timemanagment.models.TimeModel;
import com.example.timemanagment.services.TaskAlarmService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class NewTaskActivity extends AppCompatActivity
        implements TimePickerFragment.TimePickerListener, DatePickerFragment.DatePickerListener {


    //for services
    AlarmManager alarmManager ;
    PendingIntent pendingIntent;
    Intent intent ;

    //for prev alarm service
    AlarmManager prevAlarmManager;
    PendingIntent prevPendingIntent;
    Intent prevIntent;

    //global variables to store the entered time in the alarm set
    int _hour, _minute; String monthName;
    int _year, _month, _day;

    //for saving settings
    SharedPreferences settingsPreferences;

    //taskId
    String dateId, taskId;

    boolean stored = false;

    //views
    EditText et_note, et_time, et_date, et_task_name;
    BetterSpinner spinner_repeat, spinner_priority;
    SwitchCompat switch_active;
    Button btn_save, btn_cancel;


    //strings for update
    String taskName, taskTime, taskDate, taskPriority, taskRepeat, note;
    int day, month,year;
    boolean isActive;


    TimePickerFragment timePicker; DatePickerFragment datePicker;

    //firebase
    //DatabaseReference databaseReference;
    FirebaseFirestore db;

    String listId;
    int listColor ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settingsPreferences = getSharedPreferences("settings", MODE_PRIVATE);;
        if(settingsPreferences.getString("lang", "en").equals("ar"))
            setLocale(settingsPreferences.getString("lang", "en"));

        setContentView(R.layout.activity_new_task);
        Toolbar toolbar = findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.title);
        title.setText(R.string.new_list);
        setTitle("");
        setSupportActionBar(toolbar);

        timePicker = new TimePickerFragment();
        datePicker = new DatePickerFragment();

        findViews();
        listId = getIntent().getStringExtra("list_id");
        listColor= getIntent().getIntExtra("list_color",0);


        //***********alarm service****************//
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        intent = new Intent(NewTaskActivity.this,TaskAlarmService.class);
       //***************************************//


        //......................UPDATE INTENT.......................//
        if (getIntent().getAction()!= null){
            btn_save.setText("Update");
            setValue();
            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getValue();
                    db.collection("lists").document(listId).collection("date").document(dateId)
                            .update("day",day,"monthName",monthName,"year",year).
                            addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(NewTaskActivity.this, "successfully date updated", Toast.LENGTH_SHORT).show();
                                }
                            });

                    DocumentReference docRef = db.collection("lists").document(listId).
                            collection("date").document(dateId).collection("tasks").document(taskId);



                    docRef.update("note",note, "priority",taskPriority,"repeat",taskRepeat
                            , "day",day,"month",monthName,"year",year,"taskName",taskName ,"taskTime",taskTime).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(NewTaskActivity.this, "successfully update task", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(NewTaskActivity.this, TasksActivity.class);
                                intent.putExtra("list_id",listId);
                                startActivity(intent);
                            }


                        }
                    });


                }
            });

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(NewTaskActivity.this, TasksActivity.class);
                    intent.putExtra("list_id" ,listId );
                    startActivity(intent);
                }
            });



        }



        //............prev alarm service............//
       // prevAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
       // prevIntent = new Intent(NewTaskActivity.this, PrevTaskAlarmService.class);

       // prevPendingIntent = PendingIntent.getService(NewTaskActivity.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        //.......................................//

        db = FirebaseFirestore.getInstance();
        clickListners();

    }

    //to find the views
    public void findViews(){
        et_task_name = findViewById(R.id.et_task_name);
        et_date = findViewById(R.id.et_date);
        et_time = findViewById(R.id.et_time);

        et_date.setClickable(true); et_time.setClickable(true);

        et_note = findViewById(R.id.et_note);
        spinner_priority = findViewById(R.id.spinner_priority);
        spinner_repeat = findViewById(R.id.spinner_repeat);
        switch_active = findViewById(R.id.switch_active);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_save = findViewById(R.id.btn_save);

        String [] repeating = {"Daily" , "Monthly" , "Yearly", "None"};
        ArrayAdapter<String> repeatingAdapter = new ArrayAdapter(NewTaskActivity.this, android.R.layout.simple_spinner_dropdown_item, repeating);
        spinner_repeat.setAdapter(repeatingAdapter);

        String [] priority = {"High" , "Medium" , "Low", "None"};
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter(NewTaskActivity.this, android.R.layout.simple_spinner_dropdown_item, priority);
        spinner_priority.setAdapter(priorityAdapter);
    }

    public void clickListners(){
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(et_task_name.getText().toString().equals(""))
                    et_task_name.setError("write down the task name");
                else if(et_date.getText().toString().equals("Date") && et_time.getText().toString().equals("Time")){
                    et_date.setError("specify the date!");
                    et_time.setError("specify the time!");
                }
                else if(et_date.getText().toString().equals("Date"))
                    et_date.setError("specify the date!");
                else if(et_time.getText().toString().equals("Time"))
                    et_time.setError("specify the time!");

                //String taskId = databaseReference.push().getKey();
                String taskName = et_task_name.getText().toString();
                String taskTime = et_time.getText().toString();

                String[] taskDate = et_date.getText().toString().split(" ");
                int day = Integer.parseInt(taskDate[0]);
                String month = taskDate[1];
                int year = Integer.parseInt(taskDate[2]);

                String taskRepeat = spinner_repeat.getText().toString();
                String taskPriority = spinner_priority.getText().toString();
                String taskNote = "";
                if(!et_note.getText().toString().equals(""))
                    taskNote = et_note.getText().toString();

                DocumentReference dateRef = db.collection("lists").document(listId).collection("date").document();
                dateId = dateRef.getId();

                DocumentReference taskRef = db.collection("lists").document(listId).collection("date").document(dateId).collection("tasks").document();
                taskId = taskRef.getId();

                TaskDetails taskDetails = new TaskDetails(taskId, taskName, taskTime, taskRepeat, taskPriority, taskNote, true, day, monthName, year, listColor);
                DateModel dateModel = new DateModel(dateId, day, year, monthName);

                addTask(dateModel, taskDetails);

                if(switch_active.isChecked()){
                    intent.putExtra("title", et_task_name.getText().toString());
                    intent.putExtra("note", et_note.getText().toString());
                    intent.putExtra("id", dateId);
                    intent.putExtra("list_id", listId);
                    intent.putExtra("time", et_time.getText().toString());
                    intent.putExtra("date", et_date.getText().toString());

                    pendingIntent = PendingIntent.getService(NewTaskActivity.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                    schedualService(new TimeModel(_hour , _minute) , new DateModel(_day,_year,_month));
         //           schedualPrevAlarm(new TimeModel(_hour , _minute) , new DateModel(_day,_year,_month));
                }

            }

        });

        et_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker.show(getSupportFragmentManager(), "Date picker");
            }
        });

        et_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker.show(getSupportFragmentManager(), "Time picker");
            }
        });

        switch_active.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!switch_active.isChecked()){
                    alarmManager.cancel(pendingIntent);
                    Toast.makeText(NewTaskActivity.this, "alarm is canceled", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void dateClick(View view){
    }

    @Override
    public void setDate(int year, int month, int day) {
        _month = month;
        _year = year;
        _day = day;
        monthName = new DateFormatSymbols().getMonths()[month-1];
        et_date.setText(day+ " "+ monthName+ " "+ year);
    }

    @Override
    public void setTime(int hour, int minute) {
        this._hour = hour;
        this._minute = minute;

        et_time.setText(hour + ":" + minute);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void schedualService(TimeModel timeModel, DateModel dateModel){
        Calendar calender = Calendar.getInstance();
        //to set the selected time and date on the calender
        calender.setTimeInMillis(System.currentTimeMillis());
        calender.set(Calendar.HOUR_OF_DAY, timeModel.getHour());
        calender.set(Calendar.MINUTE, timeModel.getMinute());
        calender.set(Calendar.DAY_OF_MONTH, dateModel.getDay());
        calender.set(Calendar.MONTH, dateModel.getMonth() );
        calender.set(Calendar.YEAR, dateModel.getYear());

        Toast.makeText(NewTaskActivity.this, "alarm is set", Toast.LENGTH_LONG).show();
        //AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calender.getTimeInMillis() , pendingIntent);
    }

    public void schedualPrevAlarm(TimeModel timeModel, DateModel dateModel){
        Calendar calender = Calendar.getInstance();
        //to set the selected time and date on the calender
        int minute = timeModel.getMinute()-2;
        calender.setTimeInMillis(System.currentTimeMillis());
        calender.set(Calendar.HOUR_OF_DAY, timeModel.getHour());
        calender.set(Calendar.MINUTE, minute);
        calender.set(Calendar.DAY_OF_MONTH, dateModel.getDay());
        calender.set(Calendar.MONTH, dateModel.getMonth() );
        calender.set(Calendar.YEAR, dateModel.getYear());

        Toast.makeText(NewTaskActivity.this, "alarm is set", Toast.LENGTH_LONG).show();

    }
    DateModel _dateMode1 = null;

    private void addTask(DateModel dateModel, TaskDetails taskDetails){
        db.collection("lists").document(listId).collection("date")
                .whereEqualTo("monthName", monthName)
                .whereEqualTo("year", dateModel.getYear())
                .whereEqualTo("day", dateModel.getDay())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()){
                            if(task.isSuccessful()){
                                document.getReference().collection("tasks").document(taskId)
                                        .set(taskDetails)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                if (task.isSuccessful()){
                                                    _dateMode1 = new DateModel(1, 1, 1);
                                                    Toast.makeText(NewTaskActivity.this, "not null", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(NewTaskActivity.this, TasksActivity.class);
                                                    intent.putExtra("list_id", listId);
                                                    startActivity(intent);
                                                    finish();
                                                }else
                                                    addNewDate(dateModel, taskDetails);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                addNewDate(dateModel, taskDetails);
                                            }
                                        }
                                );
                            }else{
                                addNewDate(dateModel, taskDetails);
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                addNewDate(dateModel, taskDetails);
            }
        });
    }

    public void addNewDate(DateModel dateModel, TaskDetails taskDetails){
        db.collection("lists").document(listId).collection("date").document(dateId)
                .set(dateModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        db.collection("lists").document(listId)
                                .collection("date").document(dateId)
                                .collection("tasks").document(taskId)
                                .set(taskDetails)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Intent intent = new Intent(NewTaskActivity.this, TasksActivity.class);
                                        intent.putExtra("list_id", listId);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NewTaskActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setValue(){
        taskId=getIntent().getStringExtra("taskId");
        taskName =getIntent().getStringExtra("taskTitle");
        taskTime= getIntent().getStringExtra("taskTime");
        day= getIntent().getIntExtra("day",0);
        month= getIntent().getIntExtra("month",1);
        year = getIntent().getIntExtra("year",0);
        taskPriority= getIntent().getStringExtra("taskPriority");
        taskRepeat= getIntent().getStringExtra("taskRepeat");
        note= getIntent().getStringExtra("taskNote");
        listId = getIntent().getStringExtra("list_id");
        dateId = getIntent().getStringExtra("date_id");
        isActive = getIntent().getBooleanExtra("isActive",true);

        et_task_name.setText(taskName);
        et_time.setText(taskTime);
        et_date.setText(day+"/"+new DateFormatSymbols().getMonths()[month+1]+"/"+year);
        et_note.setText(note);

        spinner_repeat.setText(taskRepeat);
        spinner_priority.setText(taskPriority);
        switch_active.setChecked(isActive);


    }

    private void getValue(){
        taskName = et_task_name.getText().toString();
        taskTime = et_time.getText().toString();
        taskDate = et_date.getText().toString();
        taskRepeat = spinner_repeat.getText().toString();
        taskPriority = spinner_priority.getText().toString();
        isActive = switch_active.isChecked();
        note = "";
        if(!et_note.getText().toString().equals(""))
            note = et_note.getText().toString();
    }

    //for language configuration
    public void setLocale(String lang){
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

}
