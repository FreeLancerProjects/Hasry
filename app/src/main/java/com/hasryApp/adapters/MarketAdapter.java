package com.hasryApp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.hasryApp.R;
import com.hasryApp.activities_fragments.client.activity_markets.MarketsActivity;
import com.hasryApp.databinding.MainCategoryRowBinding;
import com.hasryApp.databinding.MarketRowBinding;
import com.hasryApp.models.MarketDataModel;

import java.util.List;

public class MarketAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MarketDataModel.Data.Market> list;
    private Context context;
    private LayoutInflater inflater;
    private MarketsActivity activity;

    public MarketAdapter(List<MarketDataModel.Data.Market> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        activity = (MarketsActivity) context;


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        MarketRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.market_row, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;
        MarketDataModel.Data.Market  model = list.get(position);

        myHolder.binding.setModel(model);

        myHolder.itemView.setOnClickListener(view -> {
            MarketDataModel.Data.Market  model2 = list.get(holder.getAdapterPosition());

            activity.setItemData(model2);
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public MarketRowBinding binding;

        public MyHolder(@NonNull MarketRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


}
