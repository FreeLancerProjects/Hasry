package com.hasryApp.activities_fragments.driver.activity_home_driver;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.firebase.iid.FirebaseInstanceId;
import com.hasryApp.R;
import com.hasryApp.activities_fragments.activity_about_app.AboutAppActivity;
import com.hasryApp.activities_fragments.activity_contact_us.ContactUsActivity;
import com.hasryApp.activities_fragments.driver.activity_home_driver.fragments.Fragment_Current_Order;
import com.hasryApp.activities_fragments.driver.activity_home_driver.fragments.Fragment_Previous_Order;
import com.hasryApp.activities_fragments.client.activity_login.LoginActivity;
import com.hasryApp.activities_fragments.driver.activity_notification.NotificationDriverActivity;
import com.hasryApp.activities_fragments.driver.activity_profile.ProfileActivity;
import com.hasryApp.adapters.ViewPagerOrderAdapter;
import com.hasryApp.databinding.ActivityDriverHomeBinding;
import com.hasryApp.language.Language;
import com.hasryApp.models.NotFireModel;
import com.hasryApp.models.NotificationCount;
import com.hasryApp.models.UserModel;
import com.hasryApp.preferences.Preferences;
import com.hasryApp.remote.Api;
import com.hasryApp.share.Common;
import com.hasryApp.tags.Tags;

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

public class HomeDriverActivity extends AppCompatActivity  {
    private ActivityDriverHomeBinding binding;
    private Preferences preferences;
    private FragmentManager fragmentManager;
    private UserModel userModel;
    private String lang;
    private ActionBarDrawerToggle toggle;
    private ViewPagerOrderAdapter adapter;
    private List<Fragment> fragmentList;
    private List<String> title;

    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_driver_home);
        initView();


    }

    private void initView() {
        fragmentManager = getSupportFragmentManager();
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        if(userModel!=null){
            binding.setUsermodel(userModel);
          //  Log.e("ldldldll",userModel.getData().getToken());
        }
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);

        toggle = new ActionBarDrawerToggle(this,binding.drawer,binding.toolbar,R.string.open,R.string.close);
        toggle.syncState();
        fragmentList = new ArrayList<>();
        title = new ArrayList<>();
        binding.tab.setupWithViewPager(binding.pager);
        binding.pager.setOffscreenPageLimit(2);

        fragmentList.add(Fragment_Current_Order.newInstance());
        fragmentList.add(Fragment_Previous_Order.newInstance());
        title.add(getString(R.string.current));
        title.add(getString(R.string.prevoius));
        adapter = new ViewPagerOrderAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragments(fragmentList);
        adapter.addTitles(title);
        binding.pager.setAdapter(adapter);
binding.lllogout.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Logout();
    }
});

binding.llNotification.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        binding.drawer.closeDrawer(GravityCompat.START);
        Intent intent = new Intent(HomeDriverActivity.this, NotificationDriverActivity.class);
        startActivity(intent);

    }
});
binding.llHome.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        binding.drawer.closeDrawer(GravityCompat.START);

        Intent intent = new Intent(HomeDriverActivity.this, HomeDriverActivity.class);
        startActivity(intent);
        finish();
    }
});
binding.llTerms.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        binding.drawer.closeDrawer(GravityCompat.START);

        Intent intent = new Intent(HomeDriverActivity.this, AboutAppActivity.class);
        intent.putExtra("type",1);

        startActivity(intent);
    }
});
binding.llAbout.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        binding.drawer.closeDrawer(GravityCompat.START);

        Intent intent = new Intent(HomeDriverActivity.this, AboutAppActivity.class);
        intent.putExtra("type",2);

        startActivity(intent);
    }
});
binding.llContact.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        binding.drawer.closeDrawer(GravityCompat.START);

        Intent intent = new Intent(HomeDriverActivity.this, ContactUsActivity.class);
        startActivity(intent);
    }
});
binding.llProfile.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        binding.drawer.closeDrawer(GravityCompat.START);

        Intent intent = new Intent(HomeDriverActivity.this, ProfileActivity.class);
        startActivity(intent);
    }
});
        binding.flNotification.setOnClickListener(view -> {


            if (userModel != null) {
                readNotificationCount();
                Intent intent = new Intent(this, NotificationDriverActivity.class);
                startActivity(intent);

            } else {
                Common.CreateDialogAlert(this, getString(R.string.please_sign_in_or_sign_up));
            }

        });





        if (userModel != null) {
            EventBus.getDefault().register(this);
            getNotificationCount();
            updateTokenFireBase();

        }


    }

    private void Logout() {
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
                                        preferences.clear(HomeDriverActivity.this);
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
                                            Toast.makeText(HomeDriverActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(HomeDriverActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
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
                                                Toast.makeText(HomeDriverActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(HomeDriverActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
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


    private void getNotificationCount() {
        Api.getService(Tags.base_url)
                .getUnreadNotificationCount("Bearer "+userModel.getData().getToken(),userModel.getData().getId())
                .enqueue(new Callback<NotificationCount>() {
                    @Override
                    public void onResponse(Call<NotificationCount> call, Response<NotificationCount> response) {
                        if (response.isSuccessful()) {
                          //  Log.e("ldlldl",response.body().getCount()+""+"Bearer "+userModel.getData().getToken()+userModel.getData().getId());
                            binding.setNotCount(response.body().getCount());
                        } else {
                            try {
                                Log.e("errorNotCode", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                                Toast.makeText(HomeDriverActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(HomeDriverActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<NotificationCount> call, Throwable t) {
                        try {
                            if (t.getMessage() != null) {
                                Log.e("error_not_code", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(HomeDriverActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(HomeDriverActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
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
                        } else {
                            try {
                                Log.e("error", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                                Toast.makeText(HomeDriverActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(HomeDriverActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        try {
                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(HomeDriverActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(HomeDriverActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });
    }


    private void updateTokenFireBase() {



        FirebaseInstanceId.getInstance()
                .getInstanceId().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult().getToken();

                try {

                    try {

                        Api.getService(Tags.base_url)
                                .updatePhoneToken("Bearer "+userModel.getData().getToken(),token,userModel.getData().getId(),1)
                                .enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        if (response.isSuccessful() && response.body() != null )
                                        {
                                            Log.e("token","updated successfully");
                                        } else {
                                            try {

                                                Log.e("error", response.code() + "_" + response.errorBody().string());
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        try {

                                            if (t.getMessage() != null) {
                                                Log.e("error", t.getMessage());
                                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                                    Toast.makeText(HomeDriverActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(HomeDriverActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                        } catch (Exception e) {
                                        }
                                    }
                                });
                    } catch (Exception e) {


                    }
                } catch (Exception e) {


                }

            }
        });
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


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        List<Fragment> fragmentList = fragmentManager.getFragments();
//        for (Fragment fragment :fragmentList)
//        {
//            fragment.onActivityResult(requestCode, resultCode, data);
//        }
//
//
//
//
//
//    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        List<Fragment> fragmentList = fragmentManager.getFragments();
//        for (Fragment fragment :fragmentList)
//        {
//            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }


}
