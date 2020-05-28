package com.hasry.activities_fragments.client.activity_client_profile;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.hasry.R;
import com.hasry.databinding.ActivityClientProfileBinding;
import com.hasry.databinding.ActivityProfileBinding;
import com.hasry.interfaces.Listeners;
import com.hasry.language.Language;
import com.hasry.models.UserModel;
import com.hasry.preferences.Preferences;

import java.util.Locale;

import io.paperdb.Paper;

public class ClientProfileActivity extends AppCompatActivity implements Listeners.BackListener{
    private ActivityClientProfileBinding binding;
    private String lang;
    private Preferences preferences;
    private UserModel userModel;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang","ar")));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_client_profile);
        initView();
    }



    private void initView()
    {
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setLang(lang);
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        binding.setModel(userModel.getData());

    }


    @Override
    public void back() {
        finish();
    }


}
