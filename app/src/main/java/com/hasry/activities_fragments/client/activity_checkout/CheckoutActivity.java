package com.hasry.activities_fragments.client.activity_checkout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.hasry.R;
import com.hasry.activities_fragments.client.activity_checkout.fragments.Fragment_Address;
import com.hasry.activities_fragments.client.activity_checkout.fragments.Fragment_Date;
import com.hasry.databinding.ActivityCheckoutBinding;
import com.hasry.interfaces.Listeners;
import com.hasry.language.Language;
import com.hasry.models.AddOrderModel;
import com.hasry.models.CreateOrderModel;
import com.hasry.models.OrderModel;
import com.hasry.models.OrderResponseModel;
import com.hasry.models.UserModel;
import com.hasry.preferences.Preferences;
import com.hasry.remote.Api;
import com.hasry.share.Common;
import com.hasry.tags.Tags;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity implements Listeners.BackListener{
    private ActivityCheckoutBinding binding;
    private String lang;
    private FragmentManager fragmentManager;
    private Fragment_Address fragment_address;
    private Fragment_Date fragment_date;
    private AddOrderModel addOrderModel;
    private CreateOrderModel createOrderModel;
    private UserModel userModel;
    private Preferences preferences;
    private OrderModel orderModel;



    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_checkout);
        fragmentManager = getSupportFragmentManager();
        initView();
        if (savedInstanceState == null) {
            displayFragmentAddress();
        }
    }




    private void initView()
    {
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        createOrderModel = preferences.getCartData(this);
        addOrderModel = new AddOrderModel();
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);



    }


    public void displayFragmentAddress()
    {
        try {
            if (fragment_address == null) {
                fragment_address = Fragment_Address.newInstance(addOrderModel);
            }else {
                fragment_address.setModel(addOrderModel);
            }

            if (fragment_date != null && fragment_date.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_date).commit();
            }


            if (fragment_address.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_address).commit();

            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_container, fragment_address, "fragment_address").addToBackStack("fragment_address").commit();

            }

            binding.tvAddress.setTextColor(ContextCompat.getColor(this,R.color.white));
            binding.tvAddress.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));

/*

            binding.tvDate.setTextColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
            binding.tvDate.setBackgroundColor(ContextCompat.getColor(this,R.color.transparent));
*/



        } catch (Exception e) {
        }

    }
    public void displayFragmentDate()
    {
        try {
            if (fragment_date == null) {
                fragment_date = Fragment_Date.newInstance(addOrderModel);
            }else {
                fragment_date.setModel(addOrderModel);
            }

            if (fragment_address != null && fragment_address.isAdded()) {
                fragmentManager.beginTransaction().hide(fragment_address).commit();
            }


            if (fragment_date.isAdded()) {
                fragmentManager.beginTransaction().show(fragment_date).commit();

            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_container, fragment_date, "fragment_date").addToBackStack("fragment_date").commit();

            }

      /*      binding.tvDate.setTextColor(ContextCompat.getColor(this,R.color.white));
            binding.tvDate.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
*/

            binding.tvAddress.setTextColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));
            binding.tvAddress.setBackgroundColor(ContextCompat.getColor(this,R.color.transparent));





        } catch (Exception e) {
        }

    }



    public void updateModel(AddOrderModel addOrderModel)
    {
        this.addOrderModel = addOrderModel;
    }


    public void createOrder() {
        try {
            ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            createOrderModel.setUser_id(userModel.getData().getId());
            createOrderModel.setAddress(addOrderModel.getAddress());
            createOrderModel.setLatitude(String.valueOf(addOrderModel.getLat()));
            createOrderModel.setLongitude(String.valueOf(addOrderModel.getLng()));
     /*       createOrderModel.setOrder_date(addOrderModel.getDate());
            createOrderModel.setOrder_time(addOrderModel.getTime());*/

            Api.getService(Tags.base_url)
                    .createOrder("Bearer "+userModel.getData().getToken(),createOrderModel)
                    .enqueue(new Callback<OrderResponseModel>() {
                        @Override
                        public void onResponse(Call<OrderResponseModel> call, Response<OrderResponseModel> response) {
                            dialog.dismiss();
                            if (response.isSuccessful() ) {


                                if (response.body() != null && response.body().getData() != null) {

                                    orderModel = response.body().getData();
                                    Intent intent = getIntent();
                                    intent.putExtra("data", orderModel);
                                    setResult(RESULT_OK,intent);
                                    finish();
                                }

                            } else {
                                dialog.dismiss();
                                try {
                                    Log.e("error_code",response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                if (response.code() == 500) {
                                    Toast.makeText(CheckoutActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(CheckoutActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<OrderResponseModel> call, Throwable t) {
                            try {
                                dialog.dismiss();
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(CheckoutActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(CheckoutActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }
    }


    @Override
    public void onBackPressed() {
        back();
    }

    @Override
    public void back() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragmentList = fragmentManager.getFragments();
        for (Fragment fragment :fragmentList)
        {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragmentList = fragmentManager.getFragments();
        for (Fragment fragment :fragmentList)
        {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


}
