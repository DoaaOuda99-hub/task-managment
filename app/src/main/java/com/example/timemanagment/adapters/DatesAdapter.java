package com.example.timemanagment.adapters;

import android.content.Context;
import android.content.SharedPreferences;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.timemanagment.R;
import com.example.timemanagment.models.DateModel;
import com.example.timemanagment.models.TaskDetails;
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

public class DatesAdapter extends RecyclerView.Adapter<DatesAdapter.ViewHolder> {

    Context mContext ;
    ArrayList<DateModel> dates;
    DatesAdapter.ViewHolder viewHolder;

    FirebaseFirestore db;

    public DatesAdapter(Context mContext, ArrayList<DateModel> dates){
        this.mContext = mContext ;
        this.dates = dates;

        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public DatesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_date_day ,viewGroup  , false);
        viewHolder = new DatesAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DatesAdapter.ViewHolder viewHolder, int i) {
        viewHolder.date.setText(dates.get(i).getDay()+"");

        //to convert day, month and year into date frormat
        int month = 0;
        Date _date = new Date();
        try {
            _date = new SimpleDateFormat("MMMM").parse(dates.get(i).getMonthName());
            Calendar cal = Calendar.getInstance();
            cal.setTime(_date);
            month = cal.get(Calendar.MONTH);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String strDate = dates.get(i).getYear()+"/"+dates.get(i).getMonth()+ month;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date d = new Date();
        try {
            d = formatter.parse(strDate);//catch exception

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        SimpleDateFormat dayOfTheWeek = new SimpleDateFormat("E"); // the day of the week abbreviated
        viewHolder.day.setText(dayOfTheWeek.format(d));

        getTasks(viewHolder.rv_tasks, i);
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView date, day;
        RecyclerView rv_tasks;
        DateTasksAdapter dateTasksAdapter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.txt_date);
            day = itemView.findViewById(R.id.txt_day);
            rv_tasks = itemView.findViewById(R.id.rv_tasks);
            rv_tasks.setLayoutManager(new LinearLayoutManager(mContext));
        }
    }

    public void getTasks(RecyclerView rv_tasks, int position){
        ArrayList<TaskDetails> tasks = new ArrayList<>();
        SharedPreferences preferences = mContext.getSharedPreferences("user info", Context.MODE_PRIVATE);
        String userId = preferences.getString("user_id", "123");

        db.collection("lists").whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            document.getReference().collection("date")
                                    .whereEqualTo("monthName", dates.get(0).getMonthName())
                                    .whereEqualTo("year", dates.get(0).getYear())
                                    .whereEqualTo("day", dates.get(position).getDay())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                DateModel dateModel = document.toObject(DateModel.class);
                                                tasks.clear();
                                                document.getReference().collection("tasks")
                                                        .whereEqualTo("day", dates.get(position).getDay())
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                for (QueryDocumentSnapshot document : task.getResult()){
                                                                    TaskDetails taskDetails = document.toObject(TaskDetails.class);
                                                                    tasks.add(taskDetails);
                                                                }
                                                                if(tasks.size() > 0){
                                                                    DateTasksAdapter dateTasksAdapter = new DateTasksAdapter(tasks, mContext);
                                                                    rv_tasks.setAdapter(dateTasksAdapter);
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });

    }
}
