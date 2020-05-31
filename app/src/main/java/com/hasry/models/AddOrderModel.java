package com.hasry.models;

import android.content.Context;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.hasry.BR;
import com.hasry.R;


import java.io.Serializable;

public class AddOrderModel extends BaseObservable implements Serializable {

    private String address;
    private double lat;
    private double lng;
    private String date;
    private String time;
    public ObservableField<String> error_address = new ObservableField<>();



    public AddOrderModel() {
        setAddress("");
        setLat(0.0);
        setLat(0.0);
        setDate("");
        setTime("");

    }

    public boolean isStep1Valid(Context context)
    {
        if (!address.trim().isEmpty())
        {
            error_address.set(null);
            return true;
        }else {
            error_address.set(context.getString(R.string.field_required));
            return false;
        }
    }

    public boolean isStep2Valid(Context context)
    {
        if (!date.isEmpty()&&!time.isEmpty())
        {
            return true;
        }else {

            if (date.isEmpty())
            {
                Toast.makeText(context, R.string.ch_date, Toast.LENGTH_SHORT).show();
            }

            if (time.isEmpty())
            {
                Toast.makeText(context, R.string.ch_time, Toast.LENGTH_SHORT).show();

            }
            return false;
        }
    }


    @Bindable
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        notifyPropertyChanged(BR.address);
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }




    public String  getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


}
