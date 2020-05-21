package com.example.timemanagment.adapters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timemanagment.R;
import com.example.timemanagment.activities.NewTaskActivity;
import com.example.timemanagment.models.DateModel;
import com.example.timemanagment.models.TaskDetails;
import com.example.timemanagment.models.TimeModel;
import com.example.timemanagment.services.TaskAlarmService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.ALARM_SERVICE;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder>{

    //for services
    AlarmManager alarmManager ;
    PendingIntent pendingIntent;
    Intent intent ;

    Context mContext;
    ArrayList<TaskDetails> tasks;
    String listID, dateId;

    FirebaseFirestore db;

    public TasksAdapter(Context mContext, ArrayList<TaskDetails> tasks, String listID , String dateId) {
        this.mContext = mContext;
        this.tasks = tasks;
        this.listID = listID;
        this.dateId=dateId;

        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_task,viewGroup  , false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String dateString = tasks.get(i).getDay()+" "+tasks.get(i).getMonth()+" "+tasks.get(i).getYear();
        TaskDetails task = tasks.get(i);
        viewHolder.taskName.setText(task.getTaskName());
        viewHolder.taskDate.setText(task.getTaskTime()+", "+ dateString);
        if(tasks.get(i).isActive())
            viewHolder.isActive_task.setChecked(true);
        else
            viewHolder.isActive_task.setChecked(false);

        alarmManager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
        intent = new Intent(mContext, TaskAlarmService.class);

        intent.putExtra("title", tasks.get(i).getTaskName());
        intent.putExtra("note", tasks.get(i).getNote());
        intent.putExtra("time", tasks.get(i).getTaskTime());
        intent.putExtra("date", dateString);
        intent.putExtra("id", tasks.get(i).getId());
        intent.putExtra("list_id", listID);
        pendingIntent = PendingIntent.getService(mContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);



        viewHolder.isActive_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] timeDate = viewHolder.taskDate.getText().toString().split(", ");
                String[] time = timeDate[0].split(":");
                int hour = Integer.parseInt(time[0]), minute = Integer.parseInt(time[1]);
                TimeModel timeModel = new TimeModel(hour, minute);

                int month = 0;
                //to convert month name to number
                Date _date = null;
                try {
                    _date = new SimpleDateFormat("MMMM").parse(tasks.get(i).getMonth());
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(_date);
                    month = cal.get(Calendar.MONTH);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                DateModel dateModel = new DateModel(tasks.get(i).getDay(), tasks.get(i).getYear(), month);

                if(viewHolder.isActive_task.isChecked()){
                    reActiveService(timeModel, dateModel);
                    Toast.makeText(mContext, "alarm activated for this task", Toast.LENGTH_SHORT).show();
                    updateTaskState(viewHolder.isActive_task.isChecked(), i);
                }else{
                    cancelService(timeModel, dateModel);
                    Toast.makeText(mContext, "alarm canceled for this task", Toast.LENGTH_SHORT).show();
                    updateTaskState(viewHolder.isActive_task.isChecked(), i);
                }
            }
        });

        // task edit
        viewHolder.taskShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, task.getTaskName());
                shareIntent.setType("text/plain");

                try {
                    view.getContext().startActivity(Intent.createChooser(shareIntent, "Share via..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(view.getContext(),
                            "There is no client installed.", Toast.LENGTH_SHORT).show();
                }
            }

        });

        viewHolder.taskEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(mContext, NewTaskActivity.class);
                intent.setAction("updateTask");
                intent.putExtra("taskId",task.getId());
                intent.putExtra("taskTitle", task.getTaskName());
                intent.putExtra("taskTime",task.getTaskTime());
                intent.putExtra("day",task.getDay());
                intent.putExtra("month",task.getMonth());
                intent.putExtra("year",task.getYear());
                intent.putExtra("taskPriority",task.getPriority());
                intent.putExtra("taskRepeat",task.getRepeat());
                intent.putExtra("taskNote",task.getNote());
                intent.putExtra("list_id", listID);
                intent.putExtra("isActive",task.isActive());
                intent.putExtra("date_id", dateId);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        View rootView ;
        TextView taskName ;
        TextView taskDate ;
        TextView taskTime;
        ImageView taskEdit ;
        ImageView taskShare ;
        Switch isActive_task ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView;
            taskName = rootView.findViewById(R.id.tv_task_name);
            taskDate = rootView.findViewById(R.id.tv_task_time_date);

            //taskTime = rootView.findViewById(R.id.tv_task_time);
            taskEdit = rootView.findViewById(R.id.iv_task_edit);
            isActive_task = rootView.findViewById(R.id.switch_active);
            taskShare = rootView.findViewById(R.id.iv_task_share);
        }
    }

    public void cancelService(TimeModel timeModel, DateModel dateModel){
        Calendar calender = Calendar.getInstance();
        //to set the selected time and date on the calender
        calender.setTimeInMillis(System.currentTimeMillis());
        calender.set(Calendar.HOUR_OF_DAY, timeModel.getHour());
        calender.set(Calendar.MINUTE, timeModel.getMinute());
        calender.set(Calendar.DAY_OF_MONTH, dateModel.getDay());
        calender.set(Calendar.MONTH, dateModel.getMonth() );
        calender.set(Calendar.YEAR, dateModel.getYear());

        alarmManager.cancel(pendingIntent);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void reActiveService(TimeModel timeModel, DateModel dateModel){
        Calendar calender = Calendar.getInstance();
        //to set the selected time and date on the calender
        calender.setTimeInMillis(System.currentTimeMillis());
        calender.set(Calendar.HOUR_OF_DAY, timeModel.getHour());
        calender.set(Calendar.MINUTE, timeModel.getMinute());
        calender.set(Calendar.DAY_OF_MONTH, dateModel.getDay());
        calender.set(Calendar.MONTH, dateModel.getMonth() );
        calender.set(Calendar.YEAR, dateModel.getYear());

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calender.getTimeInMillis() , pendingIntent);

    }

    public void updateTaskState(boolean isChecked, int position){
        db.collection("lists").document(listID)
                .collection("date").whereEqualTo("day", tasks.get(position).getDay())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()){
                            document.getReference().collection("tasks").document(tasks.get(position).getId())
                                    .update("active", isChecked);
                        }

                    }
                });

    }
}
