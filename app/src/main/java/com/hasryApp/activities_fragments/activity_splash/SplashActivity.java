package com.hasryApp.activities_fragments.activity_splash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.hasryApp.R;
import com.hasryApp.activities_fragments.client.activity_home.HomeActivity;
import com.hasryApp.activities_fragments.client.activity_login.LoginActivity;
import com.hasryApp.activities_fragments.driver.activity_home_driver.HomeDriverActivity;
import com.hasryApp.databinding.ActivitySplashBinding;
import com.hasryApp.language.Language;
import com.hasryApp.preferences.Preferences;
import com.hasryApp.tags.Tags;


import io.paperdb.Paper;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    private Preferences preferences;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang","ar")));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        preferences = Preferences.getInstance();

        new Handler().postDelayed(()->{

            String session = preferences.getSession(SplashActivity.this);
            if (session.equals(Tags.session_login)) {
                if(preferences.getUserData(this).getData().getUser_type().equals("client")){
                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(intent);}
                else {
                    Intent intent = new Intent(SplashActivity.this, HomeDriverActivity.class);
                    startActivity(intent);
                }
                finish();
            } else {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();


            }
        },3000);

    }
}
