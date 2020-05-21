package com.example.timemanagment.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timemanagment.R;
import com.example.timemanagment.models.HistoryTask;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    Context mContext ;
    ArrayList<HistoryTask> historyTasks;
    ViewHolder viewHolder;

    ArrayList<HistoryTask> tasksToDelete = new ArrayList<>();

    FloatingActionButton fab_delete;

    FirebaseFirestore db;

    public HistoryAdapter(Context mContext, ArrayList<HistoryTask> historyTasks, FloatingActionButton fab_delete){
        this.mContext = mContext ;
        this.historyTasks = historyTasks;
        this.fab_delete = fab_delete;

        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_histoy_list ,viewGroup  , false);
        viewHolder = new HistoryAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder viewHolder, int i) {
        HistoryTask task = historyTasks.get(i);
        this.viewHolder.taskName.setText(task.getTitle());
        this.viewHolder.taskTime.setText(task.getTime() + ", " + task.getDate());

        this.viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(viewHolder.checkBox.isChecked()){
                    fab_delete.setVisibility(LinearLayout.VISIBLE);
                    historyTasks.get(i).setChecked(true);
                }
            }
        });

        fab_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i=0; i<historyTasks.size(); i++){
                    if(historyTasks.get(i).isChecked()){
                        tasksToDelete.add(historyTasks.get(i));
                        historyTasks.remove(i);
                    }
                }

                deleteTask(tasksToDelete);

                notifyDataSetChanged();

            }
        });
    }

    @Override
    public int getItemCount() {
        return historyTasks.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        View rootView ;
        TextView taskName ;
        TextView taskTime;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView;
            taskName = rootView.findViewById(R.id.tv_task_name);
            taskTime = rootView.findViewById(R.id.tv_task_time_date);
            checkBox = rootView.findViewById(R.id.checkbox);
        }
    }

    public void deleteTask(ArrayList<HistoryTask> tasksToDelete){

        for(int i=0; i<tasksToDelete.size(); i++){
            db.collection("history").document(tasksToDelete.get(i).getId())
                    .delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(mContext, "successfully removed", Toast.LENGTH_SHORT).show();
                        }
            });
        }
    }

}
