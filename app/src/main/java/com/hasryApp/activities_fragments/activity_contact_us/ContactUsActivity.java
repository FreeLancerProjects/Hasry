package com.hasryApp.activities_fragments.activity_contact_us;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


import com.hasryApp.R;
import com.hasryApp.databinding.ActivityContactUsBinding;
import com.hasryApp.interfaces.Listeners;
import com.hasryApp.language.Language;
import com.hasryApp.models.ContactUsModel;
import com.hasryApp.models.SettingModel;
import com.hasryApp.remote.Api;
import com.hasryApp.share.Common;
import com.hasryApp.tags.Tags;

import java.io.IOException;
import java.util.Locale;

import io.paperdb.Paper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactUsActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityContactUsBinding binding;
    private String lang;
    private ContactUsModel contactUsModel;
    private SettingModel settingmodel;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", Locale.getDefault().getLanguage())));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contact_us);
        initView();
        getAppData();
    }


    private void initView() {
        contactUsModel = new ContactUsModel();
        binding.setContactUs(contactUsModel);
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setLang(lang);
        binding.setBackListener(this);
        binding.btnSend.setOnClickListener(v -> {
            sendContact();
        });
        binding.imagewhats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (settingmodel != null && settingmodel.getData().getWhatsapp() != null) {
                    ViewSocial("https://api.whatsapp.com/send?phone=" + settingmodel.getData().getWhatsapp());
                }
            }
        });
        binding.imageinstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (settingmodel.getData().getInstagram()!=null){

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(settingmodel.getData().getInstagram()));
                    startActivity(intent);
                }else
                {
                    Toast.makeText(ContactUsActivity.this, R.string.not_av, Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.imagesnap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://snapchat.com/add/" + settingmodel.getData().getSnapchat_ghost()));
                    intent.setPackage("com.snapchat.android");
                    startActivity(intent);
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://snapchat.com/add/" + settingmodel.getData().getSnapchat_ghost())));
                }
            }
        });
        binding.imagegmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (settingmodel != null && settingmodel.getData().getEmail1() != null) {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{settingmodel.getData().getEmail1()});
                    try {
                        startActivity(Intent.createChooser(i, "Send mail..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        // Toast.makeText(getContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void ViewSocial(String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(path));
        startActivity(intent);

    }

    @Override
    public void back() {
        finish();
    }

    public void sendContact() {


        if (contactUsModel.isDataValid(this)) {
            try {
                ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
                dialog.setCancelable(false);
                dialog.show();
                Api.getService(Tags.base_url)
                        .sendContact(contactUsModel.getName(), contactUsModel.getEmail(), contactUsModel.getSubject(), contactUsModel.getMessage())
                        .enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                dialog.dismiss();
                                if (response.isSuccessful()) {
                                    Toast.makeText(ContactUsActivity.this, getString(R.string.suc), Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    if (response.code() == 422) {
                                        Toast.makeText(ContactUsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                    } else if (response.code() == 500) {
                                        Toast.makeText(ContactUsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();

                                    } else {
                                        Toast.makeText(ContactUsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                        try {

                                            Log.e("error", response.code() + "_" + response.errorBody().string());
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                try {
                                    dialog.dismiss();
                                    if (t.getMessage() != null) {
                                        Log.e("error", t.getMessage());
                                        if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                            Toast.makeText(ContactUsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ContactUsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                } catch (Exception e) {
                                }
                            }
                        });
            } catch (Exception e) {
            }
        }

    }


    private void getAppData() {

        Api.getService(Tags.base_url)
                .getSetting()
                .enqueue(new Callback<SettingModel>() {
                    @Override
                    public void onResponse(Call<SettingModel> call, Response<SettingModel> response) {
                        if (response.isSuccessful() && response.body() != null) {

                            settingmodel = response.body();

                        } else {
                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (response.code() == 500) {

                            } else {


                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SettingModel> call, Throwable t) {
                        try {

                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                } else {
                                }
                            }

                        } catch (Exception e) {
                        }
                    }
                });

    }
}
