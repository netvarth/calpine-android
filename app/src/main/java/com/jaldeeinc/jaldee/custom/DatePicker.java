package com.jaldeeinc.jaldee.custom;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.jaldeeinc.jaldee.R;

import java.util.Calendar;

public class DatePicker extends DialogFragment {
	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
		Calendar mCalender = Calendar.getInstance();
		int year = mCalender.get(Calendar.YEAR);
		int month = mCalender.get(Calendar.MONTH);
		int dayOfMonth = mCalender.get(Calendar.DAY_OF_MONTH);
		DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.Base_Theme_AppCompat_Light_Dialog, (DatePickerDialog.OnDateSetListener)
				getActivity(), year, month, dayOfMonth);
		return datePickerDialog;
	}
}

