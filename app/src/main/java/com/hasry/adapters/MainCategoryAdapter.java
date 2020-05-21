package com.hasry.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.hasry.R;
import com.hasry.activities_fragments.client.activity_home.HomeActivity;
import com.hasry.databinding.MainCategoryRowBinding;
import com.hasry.models.MainCategoryDataModel;


import java.util.List;

public class MainCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MainCategoryDataModel.Data.MainDepartments> list;
    private Context context;
    private LayoutInflater inflater;
    private HomeActivity activity;

    public MainCategoryAdapter(List<MainCategoryDataModel.Data.MainDepartments> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        activity = (HomeActivity) context;


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        MainCategoryRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.main_category_row, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;
        MainCategoryDataModel.Data.MainDepartments mainDepartments = list.get(position);

        myHolder.binding.cardView.setCardBackgroundColor(Color.parseColor(mainDepartments.getBackground()));
        myHolder.binding.setModel(mainDepartments);

        myHolder.itemView.setOnClickListener(view -> {
            MainCategoryDataModel.Data.MainDepartments model = list.get(holder.getAdapterPosition());

            activity.setItemData(model);
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public MainCategoryRowBinding binding;

        public MyHolder(@NonNull MainCategoryRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


}
