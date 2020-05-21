package com.example.timemanagment.adapters;

import android.content.Context;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.example.timemanagment.R;

import java.util.ArrayList;

public class RingtoneAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<String> ringtoneList;
    ViewHolder viewHolder;

    public RingtoneAdapter(Context context, ArrayList<String> ringtoneList) {
        this.context = context;
        this.ringtoneList = ringtoneList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_ringtone_list, viewGroup, false);
        viewHolder = new RingtoneAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        viewHolder.ringtoneName.setText(ringtoneList.get(i));
    }

    @Override
    public int getItemCount() {
        return ringtoneList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View rootView;
        RadioButton ringtoneName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView;
            ringtoneName = rootView.findViewById(R.id.ringtone_name);
        }
    }
}
