package com.hasry.activities_fragments.driver.activity_profile;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.hasry.R;
import com.hasry.activities_fragments.client.activity_verification_code.VerificationCodeActivity;
import com.hasry.databinding.ActivityOrderDetailsBinding;
import com.hasry.databinding.ActivityProfileBinding;
import com.hasry.interfaces.Listeners;
import com.hasry.language.Language;
import com.hasry.models.UserModel;
import com.hasry.preferences.Preferences;
import com.hasry.remote.Api;
import com.hasry.share.Common;
import com.hasry.tags.Tags;

import java.io.IOException;
import java.util.Locale;
import java.util.Random;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityProfileBinding binding;
    private String lang;
    private UserModel userModel;
    private Preferences preferences;
    String notification_status = "";

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        initView();
    }


    private void initView() {
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setLang(lang);
        binding.setBackListener(this);
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        Random random = new Random();
        binding.setRandom(random);
        binding.setModel(userModel);
        binding.tvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.tvShare.setSelectAllOnFocus(true);
                binding.tvShare.setSelected(true);
            }
        });
        binding.tvCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.tvShare.setSelectAllOnFocus(true);
                copyToClipboard(binding.tvShare.getText().toString());
            }
        });
        if (userModel.getData().getNotification_status() != null) {
            if (userModel.getData().getNotification_status().equals("enable")) {
                notification_status = "disable";
            } else {
                notification_status = "enable";
            }
        } else {
            notification_status = "enable";
        }
    }


    @Override
    public void back() {
        finish();
    }

    public void copyToClipboard(String copyText) {
        ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("url", copyText);
        clipboard.setPrimaryClip(clip);
        Toast toast = Toast.makeText(this, getResources().getString(R.string.link_is_copied), Toast.LENGTH_SHORT);
        toast.show();
    }

    public void UpdateNotificationStaus2(View view) {
Log.e(";lflfll",notification_status);

        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        Api.getService(Tags.base_url)
                .UpdatereadNotificationStaus( notification_status, userModel.getData().getId())
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null) {
                          update(response.body());

                        } else {

                            try {
                                Log.e("code", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (response.code() == 500) {
                                Toast.makeText(ProfileActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            } else {

                                Toast.makeText(ProfileActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            if (t.getMessage() != null) {
                                Log.e("msg_category_error", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(ProfileActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ProfileActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });

    }

    private void update(UserModel body) {
        preferences.create_update_userdata(ProfileActivity.this, body);
        body.getData().setToken(userModel.getData().getToken());
        userModel=body;

        if (userModel.getData().getNotification_status() != null) {
            if (userModel.getData().getNotification_status().equals("enable")) {
                notification_status = "disable";
            } else {
                notification_status = "enable";
            }
        } else {
            notification_status = "enable";
        }
        Log.e("sssssssss", notification_status);        binding.setModel(body);
    }

}
