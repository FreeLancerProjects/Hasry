package com.hasry.activities_fragments.driver.activity_profile;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.hasry.R;
import com.hasry.databinding.ActivityOrderDetailsBinding;
import com.hasry.databinding.ActivityProfileBinding;
import com.hasry.interfaces.Listeners;
import com.hasry.language.Language;

import java.util.Locale;

import io.paperdb.Paper;

public class ProfileActivity extends AppCompatActivity implements Listeners.BackListener{
    private ActivityProfileBinding binding;
    private String lang;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang","ar")));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        initView();
    }



    private void initView()
    {
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setLang(lang);


    }


    @Override
    public void back() {
        finish();
    }


}
