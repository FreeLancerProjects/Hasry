package com.hasryApp.activities_fragments.client.activity_checkout.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.hasryApp.R;
import com.hasryApp.activities_fragments.client.activity_checkout.CheckoutActivity;
import com.hasryApp.databinding.FragmentDateBinding;
import com.hasryApp.interfaces.Listeners;
import com.hasryApp.models.AddOrderModel;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Fragment_Date extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, Listeners.NextPreviousAction {
    private static final String TAG = "data";
    private CheckoutActivity activity;
    private FragmentDateBinding binding;
    private TimePickerDialog timePickerDialog;
    private DatePickerDialog datePickerDialog;
    private AddOrderModel addOrderModel;


    public static Fragment_Date newInstance(AddOrderModel addOrderModel)
    {
        Bundle bundle = new Bundle();
        bundle.putSerializable(TAG,addOrderModel);
        Fragment_Date fragment_date  = new Fragment_Date();
        fragment_date.setArguments(bundle);
        return fragment_date;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_date, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();

    }


    private void initView() {
        activity = (CheckoutActivity) getActivity();
        binding.setAction(this);

        Bundle bundle = getArguments();
        if (bundle!=null)
        {
            addOrderModel = (AddOrderModel) bundle.getSerializable(TAG);
        }

        CreateDatePickerDialog();
        createTimePickerDialog();
        binding.llDate.setOnClickListener(view -> {
            try {
                datePickerDialog.show(activity.getFragmentManager(), "");
            } catch (Exception e) {
            }

        });
        binding.llTime.setOnClickListener(view -> {
            try {
                timePickerDialog.show(activity.getFragmentManager(), "");

            } catch (Exception e) {
            }

        });

    }


    public void setModel(AddOrderModel model)
    {
        this.addOrderModel =model;
    }

    private void CreateDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);

        datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.dismissOnPause(true);
        datePickerDialog.setAccentColor(ActivityCompat.getColor(activity, R.color.colorPrimary));
        datePickerDialog.setCancelColor(ActivityCompat.getColor(activity, R.color.gray4));
        datePickerDialog.setOkColor(ActivityCompat.getColor(activity, R.color.colorPrimary));
        datePickerDialog.setOkText(getString(R.string.select));
        datePickerDialog.setCancelText(getString(R.string.cancel));
        datePickerDialog.setLocale(Locale.ENGLISH);
        datePickerDialog.setVersion(DatePickerDialog.Version.VERSION_2);
        datePickerDialog.setMinDate(calendar);

    }

    private void createTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY)+1, calendar.get(Calendar.MINUTE), false);
        timePickerDialog.dismissOnPause(true);
        timePickerDialog.setAccentColor(ActivityCompat.getColor(activity, R.color.colorPrimary));
        timePickerDialog.setCancelColor(ActivityCompat.getColor(activity, R.color.gray4));
        timePickerDialog.setOkColor(ActivityCompat.getColor(activity, R.color.colorPrimary));
        timePickerDialog.setOkText(getString(R.string.select));
        timePickerDialog.setCancelText(getString(R.string.cancel));
        timePickerDialog.setLocale(Locale.ENGLISH);
        timePickerDialog.setVersion(TimePickerDialog.Version.VERSION_2);


    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy", Locale.ENGLISH);
        String d = dateFormat.format(new Date(calendar.getTimeInMillis()));
        binding.tvDate.setText(d);


        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        String date = dateFormat2.format(new Date(calendar.getTimeInMillis()));

    //    addOrderModel.setDate(date);

    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
        String t = dateFormat.format(new Date(calendar.getTimeInMillis()));
        binding.tvTime.setText(t);
        String time =dateFormat.format(new Date(calendar.getTimeInMillis()));
      //  addOrderModel.setTime(time);
    }


    @Override
    public void onNext() {
       /* if (addOrderModel.isStep2Valid(activity))
        {
            activity.updateModel(addOrderModel);
            activity.createOrder();
        }*/
    }

    @Override
    public void onPrevious() {
        activity.displayFragmentAddress();
    }
}
