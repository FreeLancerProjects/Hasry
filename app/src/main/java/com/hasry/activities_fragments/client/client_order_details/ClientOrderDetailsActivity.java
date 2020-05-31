package com.hasry.activities_fragments.client.client_order_details;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hasry.R;
import com.hasry.activities_fragments.activity_about_app.AboutAppActivity;
import com.hasry.adapters.ProductDetailsAdapter;
import com.hasry.databinding.ActivityAboutAppBinding;
import com.hasry.databinding.ActivityClientOrderDetailsBinding;
import com.hasry.interfaces.Listeners;
import com.hasry.language.Language;
import com.hasry.models.OrderModel;
import com.hasry.models.SettingModel;
import com.hasry.remote.Api;
import com.hasry.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    }




    @Override
    public void back() {
        finish();
    }

}
