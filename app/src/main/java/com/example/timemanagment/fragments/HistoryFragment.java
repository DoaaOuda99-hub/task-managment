package com.example.timemanagment.fragments;

import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timemanagment.R;
import com.example.timemanagment.adapters.HistoryAdapter;
import com.example.timemanagment.models.HistoryTask;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {

    FloatingActionButton fab_delete;

    View view;
    RecyclerView rv_history;
    HistoryAdapter historyAdapter;
    ArrayList<HistoryTask> historyTasks = new ArrayList<>();

    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_history, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.title);
        title.setText(R.string.history);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).setTitle("");

        db = FirebaseFirestore.getInstance();

        fab_delete = view.findViewById(R.id.fab_delete);
        rv_history = view.findViewById(R.id.rv_history_list);
        rv_history.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        db.collection("history")
        .get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    historyTasks.clear();
                    for (DocumentSnapshot document : task.getResult()) {
                        HistoryTask taskDetails = document.toObject(HistoryTask.class);
                        historyTasks.add(taskDetails);
                    }
                }

                if (historyTasks.size() > 0){
                    historyAdapter = new HistoryAdapter(getContext(), historyTasks, fab_delete);
                    rv_history.setAdapter(historyAdapter);
                }else
                    Toast.makeText(getContext(), "less than 1", Toast.LENGTH_SHORT).show();
            }
        });
    }

}