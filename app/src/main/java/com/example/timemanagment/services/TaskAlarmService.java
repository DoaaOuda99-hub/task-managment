package com.example.timemanagment.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.timemanagment.R;
import com.example.timemanagment.models.HistoryTask;
import com.example.timemanagment.models.TaskDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class TaskAlarmService extends IntentService {

    public TaskAlarmService() {
        super("Task service");
    }

    FirebaseFirestore db;
    static  String taskTitle, note, time, date;
    static  String taskId ;
    static String list_id;
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        taskTitle = intent.getStringExtra("title");
        taskId = intent.getStringExtra("id");
        list_id = intent.getStringExtra("list_id");
        note = intent.getStringExtra("note");
        time = intent.getStringExtra("time");
        date = intent.getStringExtra("date");

        Intent closeButton = new Intent(this,addHistoryTask.class);
        closeButton.setAction("Add_task_to_history");
        closeButton.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        //This Intent will be called when Notification will be clicked by user.
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request
  code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

//This Intent will be called when Confirm button from notification will be
//clicked by user.
        PendingIntent pendingIntentConfirm = PendingIntent.getBroadcast(this, 0, closeButton, PendingIntent.FLAG_CANCEL_CURRENT);// PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(this, 0, closeButton, 0);

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_notification_layout);

        NotificationChannel mChannel = new NotificationChannel("1", "channel1", NotificationManager.IMPORTANCE_DEFAULT);

        Notification.Builder notification = new Notification.Builder(this, "1");
        notification.setAutoCancel(true);

        notification.setSmallIcon(R.drawable.ic_todo_list);
        notification.setCustomBigContentView(contentView);
        notification.setStyle(new Notification.DecoratedCustomViewStyle());
        notification.setAutoCancel(true);

        contentView.setOnClickPendingIntent(R.id.btn_done, pendingIntentConfirm);
        contentView.setTextViewText(R.id.txt_alarm_title, taskTitle);
        contentView.setTextViewText(R.id.txt_alarm_note, note);

        NotificationManager notificationManager
                = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(mChannel);
        notificationManager.notify(1, notification.build());

        notification.setAutoCancel(true);

        // Cancel the notification after its selected
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Ringtone ringTon = RingtoneManager.getRingtone(getApplicationContext(), alert);
        ringTon.play();

    }

    public static class addHistoryTask extends BroadcastReceiver {

        FirebaseFirestore db;
        Context context;

        TaskDetails emptyTask = new TaskDetails(); //if stills with empty data, then the date has no tasks anymore
        @Override
        public void onReceive(Context context, Intent intent) {
            this.context = context;
            System.out.println("Received Cancelled Event");
            db = FirebaseFirestore.getInstance();

            if (intent.getAction().equals("Add_task_to_history")){
                addTaskToHistory();
                deleteTask();
            }

        }

        public void addTaskToHistory(){
            DocumentReference ref = db.collection("history").document();
            String task = ref.getId();

            HistoryTask historyTask = new HistoryTask(task,TaskAlarmService.taskTitle, note, date, time, true);

            db.collection("history")
                    .document(task)
                    .set(historyTask)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //listsArray.add(list);
                            //listAdapter.notifyDataSetChanged();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }

        private void deleteTask(){
            String[] dateArr = TaskAlarmService.date.split(" ");
            int day = Integer.parseInt(dateArr[0]);
            String month = dateArr[1];
            int year = Integer.parseInt(dateArr[2]);

            String dateId;

            db.collection("lists").document(list_id)
                    .collection("date")
                    .whereEqualTo("day", day)
                    .whereEqualTo("monthName", month)
                    .whereEqualTo("year", year)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (QueryDocumentSnapshot document : task.getResult()){
                                document.getReference()
                                        .collection("tasks")
                                        .whereEqualTo("id", TaskAlarmService.taskId)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                for (QueryDocumentSnapshot document : task.getResult()){
                                                    document.getReference()
                                                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                Toast.makeText(context, "Keep it up!", Toast.LENGTH_SHORT).show();
                                                            }else{
                                                                Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                }
                                                }
                                        }
                                );
                            }
                        }
                    }
            );

//            //if the date now has no tasks at all, delete it then
//            db.collection("lists").document(list_id)
//                    .collection("date")
//                    .whereEqualTo("day", day)
//                    .whereEqualTo("monthName", month)
//                    .whereEqualTo("year", year)
//                    .get()
//                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            for (QueryDocumentSnapshot document : task.getResult()){
//                                DateModel _dateModel = document.toObject(DateModel.class);
//                                document.getReference()
//                                        .collection("tasks")
//                                        .whereEqualTo("id", TaskAlarmService.taskId)
//                                        .get()
//                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                           @Override
//                                           public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                               for(QueryDocumentSnapshot document : task.getResult()){
//                                                   if(task.isSuccessful()){
//                                                       emptyTask = document.toObject(TaskDetails.class);
//                                                       Toast.makeText(context, emptyTask.getTaskName(), Toast.LENGTH_SHORT).show();
//                                                   }
//                                               }
//                                           }
//                                       }
//                                );
//
//                                if(emptyTask.getDay() < 1)
//                                    db.collection("lists").document(list_id)
//                                            .collection("date").document(_dateModel.getId())
//                                            .delete()
//                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//                                                    if(task.isSuccessful())
//                                                        Toast.makeText(context, "deleted date", Toast.LENGTH_SHORT).show();
//                                                }
//                                            });
//                            }
//                        }
//                    });
        }
    }
}
