package com.example.timemanagment.fragments;

import android.content.Context;
import android.content.SharedPreferences;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.timemanagment.R;
import com.example.timemanagment.adapters.DatesAdapter;
import com.example.timemanagment.models.DateModel;
import com.example.timemanagment.models.List;
import com.example.timemanagment.models.TaskDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import java.util.ArrayList;

public class CalendarFragment extends Fragment {

    SharedPreferences sharedPreferences;

    View view;
    TextView txt_username, txt_email, txt_year;
    BetterSpinner spinner_months;
    RecyclerView rv_dates;
    String months[] = {"January", "February", "March", "April", "May", "June", "July"};

    String user_id;

    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_calendar, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.title);
        title.setText(R.string.new_list);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).setTitle("");

        db = FirebaseFirestore.getInstance();

        sharedPreferences = getContext().getSharedPreferences("user info", Context.MODE_PRIVATE);
        user_id = sharedPreferences.getString("user_id",null);

        findViews();
        appendViews();

        getTasks();

        return view;
    }

    public void findViews(){
        spinner_months =view.findViewById(R.id.spinner_months);
        txt_username = view.findViewById(R.id.txt_username);
        txt_email = view.findViewById(R.id.txt_email);
        txt_year = view.findViewById(R.id.txt_year);
        rv_dates = view.findViewById(R.id.rv_dates);
        rv_dates.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void appendViews(){
        ArrayAdapter<String> monthsAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, months);
        spinner_months.setAdapter(monthsAdapter);

        txt_username.setText(sharedPreferences.getString("username", "Username"));
        txt_email.setText(sharedPreferences.getString("email", "Email"));
    }

    public void getTasks(){
        ArrayList<DateModel> dates = new ArrayList<>();

        db.collection("lists")
                .whereEqualTo("userId",user_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            List list = document.toObject(List.class);
                            document.getReference().collection("date")
                                .whereEqualTo("monthName", "June")
                                .whereEqualTo("year", 2019)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                   @Override
                                   public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                       if(task.isSuccessful()) {
                                           for (QueryDocumentSnapshot document : task.getResult()) {
                                               DateModel dateModel = document.toObject(DateModel.class);
                                               if(dates.size() > 0) {
                                                   for (int i = 0; i < dates.size(); i++) {
                                                       if (dates.get(i).getDay() != dateModel.getDay()){
                                                           nonEmptyDates(document, dates, dateModel);
                                                           break;
                                                       }
                                                   }
                                               }nonEmptyDates(document, dates, dateModel);

                                           }if(dates.size() > 0){
                                               DatesAdapter dateTasksAdapter = new DatesAdapter(getContext(), dates);
                                               rv_dates.setAdapter(dateTasksAdapter);
                                           }
                                       }
                                   }
                               }
                            );
                        }
                    }
                });
        //Toast.makeText(getContext(), queryDocumentSnapshots.toString(), Toast.LENGTH_SHORT).show();
    }


    //to add only dates that has tasks
    public void nonEmptyDates(QueryDocumentSnapshot document, ArrayList<DateModel> dates, DateModel dateModel){
        document.getReference()
                .collection("tasks")
                .whereEqualTo("day", dateModel.getDay())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                if(document.toObject(TaskDetails.class) != null)
                                    dates.add(dateModel);
                            }
                            DatesAdapter dateTasksAdapter = new DatesAdapter(getContext(), dates);
                            rv_dates.setAdapter(dateTasksAdapter);
                        }
                    }
                });
    }
}