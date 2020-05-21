package com.example.timemanagment.fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timemanagment.R;
import com.example.timemanagment.adapters.ListsAdapter;
import com.example.timemanagment.models.List;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.maltaisn.icondialog.Icon;
import com.maltaisn.icondialog.IconDialog;

import java.util.ArrayList;

import petrov.kristiyan.colorpicker.ColorPicker;

public class ListsFragment extends Fragment implements IconDialog.Callback{

    private Icon[] selectedIcons;

    ListsAdapter listAdapter;
    ArrayList<List> listsArray = new ArrayList<>();

    FloatingActionButton fab;
    RecyclerView rv_lists;
    View view ;
    EditText searchView_list ;

    int listColor ;

    //shared preferences
    SharedPreferences sharedPreferences;

    // firebase
    String userId;
    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_lists, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        TextView title = toolbar.findViewById(R.id.title);
        title.setText(R.string.list);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).setTitle("");

        //firebase
        db = FirebaseFirestore.getInstance();

        findViews();
        addListener();

        sharedPreferences = getActivity().getSharedPreferences("user info", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("user_id", "123");

        listAdapter = new ListsAdapter((ArrayList<List>) listsArray, getContext());

        return  view;
    }


    @Override
    public void onStart() {
        super.onStart();

        db.collection("lists").whereEqualTo("userId", userId)
        .get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    listsArray.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        List list = document.toObject(List.class);
                        listsArray.add(list);
                    }
                }

                if (listsArray.size() > 0){
                    listAdapter = new ListsAdapter((ArrayList<List>) listsArray, getContext());
                    rv_lists.setAdapter(listAdapter);
                }else
                    Toast.makeText(getContext(), "less than 1", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private  void  findViews(){
        fab = view.findViewById(R.id.fab);
        rv_lists = view.findViewById(R.id.rv_list);
        rv_lists.setLayoutManager(new GridLayoutManager(getContext(), 2));
        searchView_list = view.findViewById(R.id.searchView_list);
    }


    private void addListener(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.custom_add_list_dialog);
                TextView colorList = dialog.findViewById(R.id.et_listColor);
                EditText et_listName = dialog.findViewById(R.id.et_list_name);

                colorList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final ColorPicker colorPicker = new ColorPicker(getActivity());
                        colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                            @Override
                            public void onChooseColor(int position,int color) {
                            }

                            @Override
                            public void onCancel(){
                                // put code
                            }
                        })
                                .addListenerButton("ok", new ColorPicker.OnButtonListener() {
                                    @Override
                                    public void onClick(View v, int position, int color) {
                                        colorList.setText("List Color");
                                        colorList.setTextColor(color);
                                        colorPicker.dismissDialog();
                                        listColor = color;

                                    }
                                }).addListenerButton("cancel", new ColorPicker.OnButtonListener() {
                            @Override
                            public void onClick(View v, int position, int color) {
                                colorPicker.dismissDialog();
                            }
                        })
                                .disableDefaultButtons(true)

                                .setColumns(5)
                                .setTitle("Color List").setRoundColorButton(true)
                                .show();


                    }
                });
                //IconDialog iconDialog = new IconDialog();
                TextView iconList = dialog.findViewById(R.id.tv_iconList);
                iconList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        IconDialog iconDialog = new IconDialog();

//                        iconDialog.setSelectedIcons(selectedIcons);
                        iconDialog.setTargetFragment(ListsFragment.this, 0);
//                        iconDialog.getDi1alog().show();
//                        iconDialog.show(getFragmentManager(), "icon_dialog");

                        iconDialog.setSelectedIcons(selectedIcons);
                        iconDialog.show(getActivity().getSupportFragmentManager(), "icon_dialog");

                    }
                });


                //Cancel button in dialog
                Button cancel = dialog.findViewById(R.id.btn_cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });


                //Save Button on dialog

                Button btn_save = dialog.findViewById(R.id.btn_save);
                btn_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String listName = et_listName.getText().toString();
                        addList(listName,listColor , 1);
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        searchView_list.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });


    }

    @Override
    public void onIconDialogIconsSelected(Icon[] icons) {
        selectedIcons = icons;
    }

    private void addList(String listName, int listColor, int listIcon){

        DocumentReference ref = db.collection("lists").document();
        String listID = ref.getId();
        List list = new List(listID, listName, listColor, listIcon,userId);

        db.collection("lists")
                .document(listID)
                .set(list)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listsArray.add(list);
                        listAdapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void filter(String text) {
        ArrayList<List> filteredList = new ArrayList<>();

        for (List item : listsArray) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        listAdapter = new ListsAdapter(filteredList, getContext());
        rv_lists.setAdapter(listAdapter);
    }






}


