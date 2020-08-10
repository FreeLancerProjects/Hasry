package com.hasryApp.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.hasryApp.R;
import com.hasryApp.activities_fragments.client.activity_cart.CartActivity;
import com.hasryApp.databinding.CartRowBinding;
import com.hasryApp.databinding.MainCategoryRowBinding;
import com.hasryApp.models.ItemCartModel;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ItemCartModel> list;
    private Context context;
    private LayoutInflater inflater;
    private CartActivity activity;

    public CartAdapter(List<ItemCartModel> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        activity = (CartActivity) context;


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        CartRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.cart_row, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;
        ItemCartModel model = list.get(position);

        myHolder.binding.setModel(model);
        myHolder.binding.tvPriceBeforeDiscount.setPaintFlags(myHolder.binding.tvPriceBeforeDiscount.getPaintFlags()|Paint.STRIKE_THRU_TEXT_FLAG);

        myHolder.binding.imageIncrease.setOnClickListener(v -> {
            ItemCartModel model2 = list.get(myHolder.getAdapterPosition());

            int amount = model2.getAmount() + 1;
            model2.setAmount(amount);
            activity.increase_decrease(model2, myHolder.getAdapterPosition());
        });

        myHolder.binding.imageDecrease.setOnClickListener(v -> {
            ItemCartModel model2 = list.get(myHolder.getAdapterPosition());

            if (model2.getAmount() > 1) {
                int amount = model2.getAmount() - 1;
                model2.setAmount(amount);
                activity.increase_decrease(model2, myHolder.getAdapterPosition());
            }

        });

        myHolder.binding.imageDelete.setOnClickListener(v -> {
            ItemCartModel model2 = list.get(myHolder.getAdapterPosition());
            activity.deleteItem(model2, myHolder.getAdapterPosition());
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public CartRowBinding binding;

        public MyHolder(@NonNull CartRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }


}
