package com.hasryApp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;

import com.hasryApp.R;
import com.hasryApp.databinding.SpinnerCityRowBinding;
import com.hasryApp.databinding.SpinnerNeighborhoodRowBinding;

import java.util.List;

public class NeigborHoodAdapter extends BaseAdapter {
    private List<String> list;
    private Context context;
    private LayoutInflater inflater;

    public NeigborHoodAdapter(List<String> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        @SuppressLint("ViewHolder") SpinnerNeighborhoodRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.spinner_neighborhood_row,parent,false);
        binding.setName(list.get(position));
        return binding.getRoot();
    }
}
