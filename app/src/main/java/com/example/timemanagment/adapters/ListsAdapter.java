package com.example.timemanagment.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.timemanagment.R;
import com.example.timemanagment.activities.TasksActivity;
import com.example.timemanagment.models.List;

import java.util.ArrayList;

public class ListsAdapter extends RecyclerView.Adapter {

    ArrayList<List> lists;
    Context context;
    ViewHolder viewHolder;

    public ListsAdapter(ArrayList<List> lists, Context context) {
        this.lists = lists;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_list_view ,viewGroup  , false);
        viewHolder = new ListsAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        GradientDrawable backgroundGradient = (GradientDrawable)this.viewHolder.listIcon.getBackground();
        backgroundGradient.setColor(lists.get(i).getColorHexa());


      //  this.viewHolder.listIcon.setBackground(context.getDrawable(R.drawable.custom_circular_bg));

        this.viewHolder.listIcon.setImageResource(R.drawable.ic_clock);
        this.viewHolder.listName.setText(lists.get(i).getName());
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        View rootView;
        ImageButton listIcon;
        TextView listName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView;
            listIcon = rootView.findViewById(R.id.ic_list);
            listName = rootView.findViewById(R.id.txt_list_name);
            rootView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), TasksActivity.class);
            intent.putExtra("list_id" , lists.get(getAdapterPosition()).getId());
            intent.putExtra("list_color",lists.get(getAdapterPosition()).getColorHexa());
            view.getContext().startActivity(intent);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
    }
}
