package com.example.timemanagment.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.timemanagment.R;
import com.example.timemanagment.models.TaskDetails;

import java.util.ArrayList;

public class DateTasksAdapter extends RecyclerView.Adapter<DateTasksAdapter.ViewHoler> {

    ArrayList<TaskDetails> tasks;
    Context context;


    public DateTasksAdapter(ArrayList<TaskDetails> tasks, Context context) {
        this.tasks = tasks;
        this.context = context;

    }

    @NonNull
    @Override
    public DateTasksAdapter.ViewHoler onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_task_day ,viewGroup  , false);
        return new DateTasksAdapter.ViewHoler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateTasksAdapter.ViewHoler viewHolder, int i) {
        viewHolder.taskName.setText(tasks.get(i).getTaskName());
        viewHolder.taskDateTime.setText(tasks.get(i).getTaskTime());

        GradientDrawable backgroundGradient = (GradientDrawable)viewHolder.linearLayout.getBackground();
        backgroundGradient.setColor(tasks.get(i).getListColor());

    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public class ViewHoler extends RecyclerView.ViewHolder{

        View rootView;
        TextView taskName, taskDateTime;
        LinearLayout linearLayout;

        public ViewHoler(@NonNull View itemView) {
            super(itemView);
            rootView = itemView;
            linearLayout=rootView.findViewById(R.id.ll_custom_task_day);
            taskName = itemView.findViewById(R.id.txt_task_name);
            taskDateTime = itemView.findViewById(R.id.txt_time_date);
        }
    }
}
