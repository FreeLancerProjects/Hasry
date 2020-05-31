package com.hasry.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.hasry.R;

import com.hasry.activities_fragments.driver.activity_home_driver.fragments.Fragment_Current_Order;
import com.hasry.activities_fragments.driver.activity_home_driver.fragments.Fragment_Previous_Order;
import com.hasry.databinding.DriverOrderRowBinding;
import com.hasry.databinding.LoadMoreBinding;
import com.hasry.models.OrderModel;

import java.util.List;

import io.paperdb.Paper;

public class DriverOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int DATA = 1;
    private final int LOAD = 2;

    private Context context;
    private List<OrderModel>  list;
    private Fragment fragment;
    private String lang;
    public DriverOrderAdapter(Context context, List<OrderModel>  list, Fragment fragment) {
        this.context = context;
        this.list = list;
        this.fragment = fragment;
        Paper.init(context);
        lang = Paper.book().read("lang","ar");

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType==DATA) {
            DriverOrderRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.driver_order_row, parent, false);
            return new Holder1(binding);


        }else
            {
                LoadMoreBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.load_more,parent,false);
                return new LoadHolder(binding);
            }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        OrderModel model = list.get(position);

        if (holder instanceof Holder1)
        {
            Holder1 holder1 = (Holder1) holder;

            holder1.binding.setModel(model);


            holder1.itemView.setOnClickListener(view ->
            {
                OrderModel model2 =list.get(holder.getAdapterPosition());

                if (fragment instanceof Fragment_Current_Order)
                {
                    Fragment_Current_Order fragmentCurrentOrder = (Fragment_Current_Order) fragment;
                    fragmentCurrentOrder.setItemData(list.get(holder.getLayoutPosition()).getId());
                }else if (fragment instanceof Fragment_Previous_Order)
                {
                    Fragment_Previous_Order fragmentPreviousOrder = (Fragment_Previous_Order) fragment;
                    fragmentPreviousOrder.setItemData(list.get(holder.getLayoutPosition()).getId());

                }
            });
        }else if (holder instanceof LoadHolder) {
            LoadHolder loadHolder = (LoadHolder) holder;
            loadHolder.binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
            loadHolder.binding.progBar.setIndeterminate(true);
        }




    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Holder1 extends RecyclerView.ViewHolder {
        private DriverOrderRowBinding binding;

        public Holder1(@NonNull DriverOrderRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


    public class LoadHolder extends RecyclerView.ViewHolder {
        private LoadMoreBinding binding;

        public LoadHolder(@NonNull LoadMoreBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


    @Override
    public int getItemViewType(int position) {

        if (list.get(position)==null)
        {
            return LOAD;
        }else
        {
            return DATA;
        }
    }
}
