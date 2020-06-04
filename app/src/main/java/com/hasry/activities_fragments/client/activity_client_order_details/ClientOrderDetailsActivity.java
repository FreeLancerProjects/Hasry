package com.hasry.activities_fragments.client.activity_client_order_details;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.hasry.R;
import com.hasry.activities_fragments.client.activity_follow_order.FollowOrderActivity;
import com.hasry.adapters.ProductDetailsAdapter;
import com.hasry.databinding.ActivityClientOrderDetailsBinding;
import com.hasry.interfaces.Listeners;
import com.hasry.language.Language;
import com.hasry.models.OrderModel;
import com.hasry.share.Common;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class ClientOrderDetailsActivity extends AppCompatActivity implements Listeners.BackListener{
    private ActivityClientOrderDetailsBinding binding;
    private String lang;
    private OrderModel orderModel;
    private List<OrderModel.OrderDetails> orderDetailsList;
    private ProductDetailsAdapter adapter;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang","ar")));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_client_order_details);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent!=null)
        {
            orderModel = (OrderModel) intent.getSerializableExtra("data");

        }
    }


    private void initView()
    {
        orderDetailsList = new ArrayList<>();
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.setModel(orderModel);
        orderDetailsList.addAll(orderModel.getOrder_details());
        adapter = new ProductDetailsAdapter(orderDetailsList,this);
        binding.recView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        binding.recView.setAdapter(adapter);

        binding.flFollowDriver.setOnClickListener(v -> {
            if (!orderModel.getOrder_status().equals("new_order")){
                Intent intent = new Intent(this, FollowOrderActivity.class);
                intent.putExtra("data",orderModel);
                startActivity(intent);
            }else {
                Common.CreateDialogAlert(this,getString(R.string.new_order));
            }
        });
    }




    @Override
    public void back() {
        finish();
    }

}
