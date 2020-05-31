package com.hasry.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.hasry.R;
import com.hasry.activities_fragments.client.activity_market_data.MarketDataActivity;
import com.hasry.activities_fragments.client.activity_search.SearchActivity;
import com.hasry.databinding.LoadmoreRow1Binding;
import com.hasry.databinding.OfferRowBinding;
import com.hasry.databinding.SearchRowBinding;
import com.hasry.models.OfferModel;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int DATA_ROW =1;
    private final int LOAD_ROW =2;

    private List<OfferModel> list;
    private Context context;
    private LayoutInflater inflater;
    private AppCompatActivity activity;

    public SearchAdapter(List<OfferModel> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        activity = (AppCompatActivity) context;


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == DATA_ROW){
            SearchRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.search_row, parent, false);
            return new MyHolder(binding);
        }else {
            LoadmoreRow1Binding binding = DataBindingUtil.inflate(inflater, R.layout.loadmore_row1, parent, false);
            return new LoadMoreHolder(binding);
        }




    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof MyHolder){
            MyHolder myHolder = (MyHolder) holder;

            myHolder.binding.setModel(list.get(position));

            myHolder.itemView.setOnClickListener(v -> {
                if (activity instanceof SearchActivity){
                    SearchActivity searchActivity = (SearchActivity) activity;
                    searchActivity.setItemData(list.get(myHolder.getAdapterPosition()));
                }
            });

            myHolder.binding.imageAddToCart.setOnClickListener(v -> {
                if (activity instanceof SearchActivity){
                    SearchActivity searchActivity = (SearchActivity) activity;
                    searchActivity.addToCart(list.get(myHolder.getAdapterPosition()));

                }
            });

        }else {
            LoadMoreHolder loadMoreHolder = (LoadMoreHolder) holder;
            loadMoreHolder.binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(context,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
            loadMoreHolder.binding.progBar.setIndeterminate(true);
        }





    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public SearchRowBinding binding;

        public MyHolder(@NonNull SearchRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;



        }
    }

    public class LoadMoreHolder extends RecyclerView.ViewHolder {
        public LoadmoreRow1Binding binding;

        public LoadMoreHolder(@NonNull LoadmoreRow1Binding binding) {
            super(binding.getRoot());
            this.binding = binding;



        }
    }


    @Override
    public int getItemViewType(int position) {
        OfferModel model = list.get(position);
        if (model ==null){
            return LOAD_ROW;
        }else {
            return DATA_ROW;
        }
    }
}
