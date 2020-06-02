package com.hasry.activities_fragments.client.activity_home;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.firebase.iid.FirebaseInstanceId;
import com.hasry.R;
import com.hasry.activities_fragments.client.activity_cart.CartActivity;
import com.hasry.activities_fragments.client.activity_client_profile.ClientProfileActivity;
import com.hasry.activities_fragments.client.activity_login.LoginActivity;
import com.hasry.activities_fragments.client.activity_markets.MarketsActivity;
import com.hasry.activities_fragments.client.activity_more.MoreActivity;
import com.hasry.activities_fragments.client.activity_notification.NotificationActivity;
import com.hasry.activities_fragments.client.activity_order.OrderActivity;
import com.hasry.activities_fragments.client.activity_search.SearchActivity;
import com.hasry.adapters.MainCategoryAdapter;
import com.hasry.databinding.ActivityHomeBinding;
import com.hasry.interfaces.Listeners;
import com.hasry.language.Language;
import com.hasry.models.CreateOrderModel;
import com.hasry.models.MainCategoryDataModel;
import com.hasry.models.NotFireModel;
import com.hasry.models.NotificationCount;
import com.hasry.models.UserModel;
import com.hasry.preferences.Preferences;

import com.hasry.remote.Api;
import com.hasry.share.Common;
import com.hasry.tags.Tags;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements Listeners.HomeListener {
    private ActivityHomeBinding binding;
    private Preferences preferences;
    private FragmentManager fragmentManager;
    private UserModel userModel;
    private String lang;
    private ActionBarDrawerToggle toggle;
    private MainCategoryAdapter adapter;
    private List<MainCategoryDataModel.Data.MainDepartments> mainDepartmentsList;
    private CreateOrderModel createOrderModel;


    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        initView();

    }

    private void initView() {
        mainDepartmentsList = new ArrayList<>();
        fragmentManager = getSupportFragmentManager();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);

        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        binding.setAction(this);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        toggle = new ActionBarDrawerToggle(this,binding.drawer,binding.toolbar,R.string.open,R.string.close);
        toggle.syncState();

        binding.recView.setLayoutManager(new GridLayoutManager(this,1));
        adapter = new MainCategoryAdapter(mainDepartmentsList,this);
        binding.recView.setAdapter(adapter);




        if (userModel != null) {
            EventBus.getDefault().register(this);
            getNotificationCount();
            updateTokenFireBase();

        }

        getMainCategory();

    }

    @Override
    protected void onResume() {
        super.onResume();
        createOrderModel = preferences.getCartData(this);
        if (createOrderModel==null){
            binding.setCartCount(0);
        }else {
            binding.setCartCount(createOrderModel.getProducts().size());
        }
    }

    private void getMainCategory() {

         Api.getService(Tags.base_url)
                .getMainCategory()
                .enqueue(new Callback<MainCategoryDataModel>() {
                    @Override
                    public void onResponse(Call<MainCategoryDataModel> call, Response<MainCategoryDataModel> response) {
                        binding.progBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            mainDepartmentsList.clear();
                            mainDepartmentsList.addAll(response.body().getData().getMain_departments());

                            if (mainDepartmentsList.size()>0)
                            {
                                adapter.notifyDataSetChanged();
                                binding.tvNoData.setVisibility(View.GONE);
                            }else {
                                binding.tvNoData.setVisibility(View.VISIBLE);

                            }


                        } else {
                            binding.progBar.setVisibility(View.GONE);

                            try {
                                Log.e("errorNotCode", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                                Toast.makeText(HomeActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MainCategoryDataModel> call, Throwable t) {
                        try {
                            binding.progBar.setVisibility(View.GONE);

                            if (t.getMessage() != null) {
                                Log.e("error_not_code", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(HomeActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });
    }


    private void getNotificationCount() {
        Api.getService(Tags.base_url)
                .getUnreadNotificationCount("Bearer "+userModel.getData().getToken(),userModel.getData().getId())
                .enqueue(new Callback<NotificationCount>() {
                    @Override
                    public void onResponse(Call<NotificationCount> call, Response<NotificationCount> response) {
                        if (response.isSuccessful()) {
                            Log.e("count",response.body().getCount()+"_");
                            binding.setNotCount(response.body().getCount());
                        } else {
                            try {
                                Log.e("errorNotCode", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                                Toast.makeText(HomeActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<NotificationCount> call, Throwable t) {
                        try {
                            if (t.getMessage() != null) {
                                Log.e("error_not_code", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(HomeActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });
    }

    private void readNotificationCount() {
        Api.getService(Tags.base_url)
                .readNotification("Bearer "+userModel.getData().getToken(),userModel.getData().getId())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            binding.setNotCount(0);
                            Log.e("bb","bb");
                        } else {
                            try {
                                Log.e("error", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                                Toast.makeText(HomeActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        try {
                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(HomeActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });
    }





    private void updateTokenFireBase() {

        /*FirebaseInstanceId.getInstance()
                .getInstanceId().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                token = task.getResult().getToken();

                try {

                    Api.getService(Tags.base_url)
                            .updateToken(userModel.getUser().getToken(), token, "android")
                            .enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        Log.e("token", "updated successfully");
                                    } else {
                                        try {

                                            Log.e("errorToken", response.code() + "_" + response.errorBody().string());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    try {

                                        if (t.getMessage() != null) {
                                            Log.e("errorToken2", t.getMessage());
                                            if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                                Toast.makeText(HomeActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                    } catch (Exception e) {
                                    }
                                }
                            });
                } catch (Exception e) {


                }

            }
        });*/
    }


    public void logout() {
        if (userModel != null) {


            ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
            dialog.show();


            FirebaseInstanceId.getInstance()
                    .getInstanceId().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String token = task.getResult().getToken();

                    Api.getService(Tags.base_url)
                            .logout("Bearer " + userModel.getData().getToken(), token)
                            .enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    dialog.dismiss();
                                    if (response.isSuccessful()) {
                                        preferences.clear(HomeActivity.this);
                                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                        if (manager != null) {
                                            manager.cancel(Tags.not_tag, Tags.not_id);
                                        }
                                        navigateToSignInActivity();


                                    } else {
                                        dialog.dismiss();
                                        try {
                                            Log.e("error", response.code() + "__" + response.errorBody().string());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }

                                        if (response.code() == 500) {
                                            Toast.makeText(HomeActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    try {
                                        dialog.dismiss();
                                        if (t.getMessage() != null) {
                                            Log.e("error", t.getMessage() + "__");

                                            if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                                Toast.makeText(HomeActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    } catch (Exception e) {
                                        Log.e("Error", e.getMessage() + "__");
                                    }
                                }
                            });

                }
            });


        } else {
            navigateToSignInActivity();
        }

    }



    public void refreshActivity(String lang) {
        Paper.book().write("lang", lang);
        Language.setNewLocale(this, lang);
        new Handler()
                .postDelayed(() -> {

                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }, 1050);


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void listenToNotifications(NotFireModel notFireModel) {
        if (userModel != null) {
            getNotificationCount();

        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    private void navigateToSignInActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragmentList = fragmentManager.getFragments();
        for (Fragment fragment :fragmentList)
        {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

        if (requestCode==100&&resultCode==RESULT_OK&&data!=null){

            int type = data.getIntExtra("type",1);

            if (type==1){
                logout();

            }else if (type ==2){
                new Handler()
                        .postDelayed(() -> {

                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }, 500);
            }


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

    public void setItemData(MainCategoryDataModel.Data.MainDepartments modle) {
        Intent intent = new Intent(this, MarketsActivity.class);
        intent.putExtra("data",modle);
        startActivity(intent);
    }

    @Override
    public void main() {
        if (binding.drawer.isDrawerOpen(GravityCompat.START)){
            binding.drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public void profile() {
        if (userModel!=null){

            Intent intent = new Intent(this, ClientProfileActivity.class);
            startActivity(intent);

        }else {
            Common.CreateDialogAlert(this,getString(R.string.please_sign_in_or_sign_up));
        }


    }

    @Override
    public void myOrder() {
        if (userModel!=null){
            Intent intent = new Intent(this, OrderActivity.class);
            startActivity(intent);
        }else {
            Common.CreateDialogAlert(this,getString(R.string.please_sign_in_or_sign_up));
        }

    }

    @Override
    public void notification() {
        if (userModel != null) {
            binding.setNotCount(0);
            readNotificationCount();
            Intent intent = new Intent(this, NotificationActivity.class);
            startActivity(intent);

        } else {
            Common.CreateDialogAlert(this, getString(R.string.please_sign_in_or_sign_up));
        }
    }

    @Override
    public void cart() {
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
    }

    @Override
    public void more() {
        Intent intent = new Intent(this, MoreActivity.class);
        startActivityForResult(intent,100);
    }

}
