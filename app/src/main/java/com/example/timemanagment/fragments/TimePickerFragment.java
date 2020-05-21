package com.example.timemanagment.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.view.ContextThemeWrapper;

import com.example.timemanagment.R;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    SharedPreferences sharedPreferences;

    TimePickerFragment.TimePickerListener mTimePicker;
    Calendar mCalender;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        sharedPreferences = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);

        //to set the tme type by settings configuration
        boolean is24h = false;
        if(sharedPreferences.getBoolean("is24h", false))
            is24h = true;

        mCalender = Calendar.getInstance();
        mCalender.setTimeInMillis(System.currentTimeMillis());
        int hour = mCalender.get(Calendar.HOUR_OF_DAY);
        int minute = mCalender.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog
                ( new ContextThemeWrapper(getActivity(), R.style.TimePicker),this, hour, minute, is24h);
        return timePickerDialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mTimePicker = (TimePickerFragment.TimePickerListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement setTime");
        }
    }

    public interface TimePickerListener{
        public void setTime(int hour, int miniute);
    }

    @Override
    public void onTimeSet(android.widget.TimePicker timePicker, int hour, int minute) {
        mTimePicker.setTime(hour, minute);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        dismiss();
    }
}
