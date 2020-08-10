package com.hasryApp.activities_fragments.client.activity_more;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.hasryApp.BuildConfig;
import com.hasryApp.R;
import com.hasryApp.activities_fragments.activity_about_app.AboutAppActivity;
import com.hasryApp.activities_fragments.activity_contact_us.ContactUsActivity;
import com.hasryApp.databinding.ActivityAboutAppBinding;
import com.hasryApp.databinding.ActivityMoreBinding;
import com.hasryApp.interfaces.Listeners;
import com.hasryApp.language.Language;

import java.util.Locale;

import io.paperdb.Paper;

public class MoreActivity extends AppCompatActivity implements Listeners.BackListener, Listeners.SettingActions {
    private ActivityMoreBinding binding;
    private String lang;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang","ar")));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_more);
        initView();
    }




    private void initView()
    {
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.setAction(this);

        binding.tvVersion.setText(String.format("%s%s","Version : ", BuildConfig.VERSION_NAME) );

        if (lang.equals("ar"))
        {
            binding.btnAr.setBackgroundResource(R.drawable.small_rounded_btn_second);
            binding.btnEn.setBackgroundResource(R.drawable.small_rounded_btn_primary);
        }else {
            binding.btnAr.setBackgroundResource(R.drawable.small_rounded_btn_primary);
            binding.btnEn.setBackgroundResource(R.drawable.small_rounded_btn_second);
        }



    }




    @Override
    public void back() {
        finish();
    }

    @Override
    public void contactUs() {
        Intent intent = new Intent(this, ContactUsActivity.class);
        startActivity(intent);
    }

    @Override
    public void terms() {
        Intent intent = new Intent(this, AboutAppActivity.class);
        intent.putExtra("type",1);
        startActivity(intent);
    }

    @Override
    public void aboutApp() {
        Intent intent = new Intent(this, AboutAppActivity.class);
        intent.putExtra("type",2);
        startActivity(intent);
    }

    @Override
    public void logout() {
        Intent intent = getIntent();
        intent.putExtra("type",1);
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public void share() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,"https://play.google.com/store/apps/details?id="+getPackageName());
        startActivity(intent);
    }

    @Override
    public void rateApp() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+getPackageName())));
        } catch (ActivityNotFoundException e1) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="+getPackageName())));
            } catch (ActivityNotFoundException e2) {
                Toast.makeText(this, "You don't have any app that can open this link", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void arLang() {
        if (!lang.equals("ar"))
        {
            refreshActivity("ar");

            binding.btnAr.setBackgroundResource(R.drawable.small_rounded_btn_second);
            binding.btnEn.setBackgroundResource(R.drawable.small_rounded_btn_primary);
        }
    }

    @Override
    public void enLang() {
        if (!lang.equals("en"))
        {
            refreshActivity("en");
            binding.btnAr.setBackgroundResource(R.drawable.small_rounded_btn_primary);
            binding.btnEn.setBackgroundResource(R.drawable.small_rounded_btn_second);
        }
    }


    public void refreshActivity(String lang) {
        Paper.book().write("lang", lang);
        Language.setNewLocale(this, lang);
        Intent intent = getIntent();
        intent.putExtra("type",2);
        setResult(RESULT_OK,intent);
        finish();


    }
}
