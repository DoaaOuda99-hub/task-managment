package com.example.timemanagment.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.view.ContextThemeWrapper;


import com.example.timemanagment.R;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    DatePickerFragment.DatePickerListener mDatePicker;
    static Calendar mCalender;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mCalender = Calendar.getInstance();
        mCalender.setTimeInMillis(System.currentTimeMillis());
        int year = mCalender.get(Calendar.YEAR);
        int day = mCalender.get(Calendar.DAY_OF_MONTH);

        int month = mCalender.get(Calendar.MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog( new ContextThemeWrapper(getActivity(), R.style.TimePicker), this, year, month, day);

        return datePickerDialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mDatePicker = (DatePickerFragment.DatePickerListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement setTime");
        }
    }

    @Override
    public void onDateSet(android.widget.DatePicker datePicker, int year, int month, int day) {
        mDatePicker.setDate(year, month, day);
    }

    public interface DatePickerListener{
        public void setDate(int year ,int month, int day);
    }
}
