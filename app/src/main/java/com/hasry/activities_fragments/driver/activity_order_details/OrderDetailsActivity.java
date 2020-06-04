package com.hasry.activities_fragments.driver.activity_order_details;

import android.Manifest;
import android.app.ProgressDialog;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hasry.R;
import com.hasry.activities_fragments.driver.activity_map_show_location.MapShowLocationActivity;
import com.hasry.activities_fragments.driver.activity_notification.NotificationDriverActivity;
import com.hasry.adapters.DriverProductAdapter;
import com.hasry.databinding.ActivityOrderDetailsBinding;
import com.hasry.interfaces.Listeners;
import com.hasry.language.Language;
import com.hasry.models.OrderDataModel;
import com.hasry.models.OrderModel;
import com.hasry.models.UserModel;
import com.hasry.preferences.Preferences;
import com.hasry.remote.Api;
import com.hasry.share.Common;
import com.hasry.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailsActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityOrderDetailsBinding binding;
    private String lang;
    private List<OrderModel.OrderDetails> orderDetailsList;
    private DriverProductAdapter driverProductAdapter;
    String order_id;
    Intent intent;
    private static final int REQUEST_PHONE_CALL = 1;
    private OrderModel orderModel;
    private UserModel userModel;
    private Preferences preferences;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
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
        if (type == 1) {
            intent.putExtra("lat", Double.parseDouble(orderModel.getLatitude()));
            intent.putExtra("lng", Double.parseDouble(orderModel.getLongitude()));
        } else {
            intent.putExtra("lat", Double.parseDouble(orderModel.getMarkter().getLatitude()));
            intent.putExtra("lng", Double.parseDouble(orderModel.getMarkter().getLongitude()));
        }
        startActivity(intent);
        //  finish();
    }

    private void initView() {
        orderDetailsList = new ArrayList<>();
        Paper.init(this);
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        binding.recView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        driverProductAdapter = new DriverProductAdapter(this, orderDetailsList);
        binding.recView.setAdapter(driverProductAdapter);
        binding.btCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  Log.e("kkkk",orderModel.getClient().getPhone());
                if (intent == null) {
                    if (orderModel != null) {
                        intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", orderModel.getClient().getPhone_code() + orderModel.getClient().getPhone(), null));
                    }
                }
                if (intent != null) {
                    Log.e("kkkk", orderModel.getClient().getPhone());
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
        binding.flUpdateOrderState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orderModel.getOrder_status().equals(Tags.driver_accept)) {
                    //binding.tvOrderNextState.setText(getResources().getString(R.string.delivery_in_progress));
                    deliverOrder(order_id);
                } else if (orderModel.getOrder_status().equals(Tags.delivery)) {
                    //binding.tvOrderNextState.setText(getResources().getString(R.string.end_oder));
                    EndOrder(order_id, "end");
                }
            }
        });
    }

    private void getOrder() {
        try {
            Api.getService(Tags.base_url)
                    .getOrderDetials(order_id, "Bearer " + userModel.getData().getToken())
                    .enqueue(new Callback<OrderDataModel>() {
                        @Override
                        public void onResponse(Call<OrderDataModel> call, Response<OrderDataModel> response) {
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
                        public void onFailure(Call<OrderDataModel> call, Throwable t) {
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

    private void UPDATEUI(OrderDataModel body) {
        orderDetailsList.clear();
        this.orderModel = body.getOrder_details();
        binding.setModel(body.getOrder_details());
        orderDetailsList.addAll(body.getOrder_details().getOrder_details());
        driverProductAdapter.notifyDataSetChanged();
        if (body.getOrder_details().getOrder_status().equals(Tags.driver_accept)) {
            binding.tvOrderNextState.setText(getResources().getString(R.string.delivery_in_progress));

        } else if (body.getOrder_details().getOrder_status().equals(Tags.delivery)) {
            binding.tvOrderNextState.setText(getResources().getString(R.string.end_oder));

        } else {
            binding.flUpdateOrderState.setVisibility(View.GONE);
        }
        if (body.getOrder_details().getClient().getPhone() != null) {
            Log.e("ldkfk", body.getOrder_details().getClient().getPhone());
            intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", body.getOrder_details().getClient().getPhone_code() + body.getOrder_details().getClient().getPhone(), null));
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
                } else {

                }
                return;
            }
        }
    }

    private void deliverOrder(String notification_id) {

        final ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        Api.getService(Tags.base_url)
                .Driverdeliverorder("Bearer " + userModel.getData().getToken(), notification_id)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {
                            getOrder();
                        } else {
                            dialog.dismiss();
                            Toast.makeText(OrderDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            try {
                                Log.e("error_code", response.code() + "" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Log.e("Error", t.getMessage());
                        } catch (Exception e) {
                        }
                    }
                });
    }

    private void EndOrder(String notification_id, String status) {

        final ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        Api.getService(Tags.base_url)
                .DriverEndOrder("Bearer " + userModel.getData().getToken(), notification_id, status)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()) {
                            getOrder();
                        } else {
                            dialog.dismiss();
                            Toast.makeText(OrderDetailsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            try {
                                Log.e("error_code", response.code() + "" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Log.e("Error", t.getMessage());
                        } catch (Exception e) {
                        }
                    }
                });
    }
}
