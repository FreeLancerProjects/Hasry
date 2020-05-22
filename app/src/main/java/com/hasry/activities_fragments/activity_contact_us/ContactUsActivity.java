package com.hasry.activities_fragments.activity_contact_us;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


import com.hasry.R;
import com.hasry.databinding.ActivityContactUsBinding;
import com.hasry.interfaces.Listeners;
import com.hasry.language.Language;

import java.util.Locale;

import io.paperdb.Paper;

public class ContactUsActivity extends AppCompatActivity implements Listeners.BackListener  {
    private ActivityContactUsBinding binding;
    private String lang;
   // private ContactUsModel contactUsModel;

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

    }


    private void initView() {
//        contactUsModel = new ContactUsModel();
//        binding.setContactUs(contactUsModel);
//        binding.setContactListener(this);
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setLang(lang);
        binding.setBackListener(this);


    }


    @Override
    public void back() {
        finish();
    }

//    @Override
//    public void sendContact(ContactUsModel contactUsModel)
//    {
//
//
//        if (contactUsModel.isDataValid(this))
//        {
//            /*try {
//                ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
//                dialog.setCancelable(false);
//                dialog.show();
//                Api.getService(Tags.base_url)
//                        .sendContact(contactUsModel.getName(),contactUsModel.getEmail(),contactUsModel.getSubject(),contactUsModel.getMessage())
//                        .enqueue(new Callback<ResponseBody>() {
//                            @Override
//                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                                dialog.dismiss();
//                                if (response.isSuccessful() ) {
//                                    Toast.makeText(ContactUsActivity.this, getString(R.string.suc), Toast.LENGTH_SHORT).show();
//                                    finish();
//                                } else {
//                                    if (response.code() == 422) {
//                                        Toast.makeText(ContactUsActivity.this,getString(R.string.failed), Toast.LENGTH_SHORT).show();
//                                    } else if (response.code() == 500) {
//                                        Toast.makeText(ContactUsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
//
//                                    }else
//                                    {
//                                        Toast.makeText(ContactUsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
//
//                                        try {
//
//                                            Log.e("error",response.code()+"_"+response.errorBody().string());
//                                        } catch (IOException e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                                try {
//                                    dialog.dismiss();
//                                    if (t.getMessage() != null) {
//                                        Log.e("error", t.getMessage());
//                                        if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
//                                            Toast.makeText(ContactUsActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
//                                        } else {
//                                            Toast.makeText(ContactUsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//
//                                } catch (Exception e) {
//                                }
//                            }
//                        });
//            } catch (Exception e) {
//            }*/
//        }
//
//    }
}
