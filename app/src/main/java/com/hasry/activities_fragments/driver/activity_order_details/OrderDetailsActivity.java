package com.hasry.activities_fragments.driver.activity_order_details;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.hasry.R;
import com.hasry.activities_fragments.driver.activity_map.MapShowLocationActivity;
import com.hasry.activities_fragments.driver.activity_signup.SignUpDriverActivity;
import com.hasry.adapters.MarketAdapter;
import com.hasry.adapters.ProductAdapter;
import com.hasry.databinding.ActivityOrderDetailsBinding;
import com.hasry.databinding.ActivityShowlocationMapBinding;
import com.hasry.interfaces.Listeners;
import com.hasry.language.Language;
import com.hasry.models.MarketDataModel;
import com.hasry.models.OrderDataModel;
import com.hasry.models.OrderModel;
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

public class OrderDetailsActivity extends AppCompatActivity implements Listeners.BackListener{
    private ActivityOrderDetailsBinding binding;
    private String lang;
private List<OrderModel.OrderDetails> orderDetailsList;
    private ProductAdapter productAdapter;
    String order_id;
    Intent intent ;
    private static final int REQUEST_PHONE_CALL = 1;
    private OrderModel orderModel;
    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang","ar")));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_details);
        getDataFromIntent();
        initView();
        getOrder();

    }


    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            order_id = intent.getStringExtra("order_id");

        }
    }
    private void navigateToSMapActivity(int type) {
        Intent intent = new Intent(this, MapShowLocationActivity.class);
if(type==1){
        intent.putExtra("lat",Double.parseDouble(orderModel.getLatitude()));
        intent.putExtra("lng",Double.parseDouble(orderModel.getLongitude()));}
else {
//    intent.putExtra("lat",Double.parseDouble(orderModel.getMarkter().getLatitude()));
//    intent.putExtra("lng",Double.parseDouble(orderModel.getMarkter().getLongitude()));
}
        startActivity(intent);
        finish();
    }
    private void initView()
    {
        orderDetailsList=new ArrayList<>();
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        binding.recView.setLayoutManager(new GridLayoutManager(this,3));
        productAdapter = new ProductAdapter(this,orderDetailsList);
        binding.recView.setAdapter(productAdapter);
        binding.btCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(intent!=null){
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(OrderDetailsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(OrderDetailsActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                        } else {
                            startActivity(intent);
                        }
                    } else {
                        startActivity(intent);
                    }
                }
            }
        });
        binding.btLocationclient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToSMapActivity(1);
            }
        });
        binding.btLocationmarket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToSMapActivity(2);
            }
        });
    }
    private void getOrder()
    {
        try {
            Api.getService(Tags.base_url)
                    .getOrderDetials(order_id)
                    .enqueue(new Callback<OrderModel>() {
                        @Override
                        public void onResponse(Call<OrderModel> call, Response<OrderModel> response) {
                            binding.progBar.setVisibility(View.GONE);
                            if (response.isSuccessful() && response.body() != null) {
UPDATEUI(response.body());
                            } else {
                                if (response.code() == 500) {
                                    Toast.makeText(OrderDetailsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(OrderDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<OrderModel> call, Throwable t) {
                            try {
                                binding.progBar.setVisibility(View.GONE);

                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(OrderDetailsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(OrderDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }
    }

    private void UPDATEUI(OrderModel body) {
        this.orderModel=body;
        binding.setModel(body);
        orderDetailsList.addAll(body.getOrder_details());
        productAdapter.notifyDataSetChanged();
        if(body.getClient().getPhone()!=null) {
            intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", body.getClient().getPhone_code()+body.getClient().getPhone(), null));
        }
    }

    @Override
    public void back() {
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PHONE_CALL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    Activity#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for Activity#requestPermissions for more details.
                            return;
                        }
                    }
                    startActivity(intent);
                }
                else {

                }
                return;
            }
        }
    }
}
