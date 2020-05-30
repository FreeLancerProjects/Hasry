package com.hasry.activities_fragments.sign_up_user_type;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.hasry.R;
import com.hasry.activities_fragments.client.activity_signup.SignUpActivity;
import com.hasry.activities_fragments.driver.activity_signup.SignUpDriverActivity;
import com.hasry.databinding.ActivitySignUpUserTypeBinding;
import com.hasry.language.Language;

import io.paperdb.Paper;

public class SignUpUserTypeActivity extends AppCompatActivity {
ActivitySignUpUserTypeBinding binding;

    private String phone_code,phone;
private int selected_type=1;
    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang","ar")));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up_user_type);
        getDataFromIntent();
initView();
    }
    private void navigateToSignUpActivity() {
        Intent intent = new Intent(this, SignUpActivity.class);
        intent.putExtra("phone",phone);
        intent.putExtra("phone_code",phone_code);
        startActivity(intent);
        finish();
    }
    private void navigateToSignUpDriverActivity() {
        Intent intent = new Intent(this, SignUpDriverActivity.class);
        intent.putExtra("phone",phone);
        intent.putExtra("phone_code",phone_code);
        startActivity(intent);
        finish();
    }
    private void initView() {
        binding.rbDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_type = 2;
            }
        });

        binding.rbBuyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selected_type = 1;



            }
        });
        binding.rbBuyer.setSelected(true);
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selected_type == 1)
                {
                navigateToSignUpActivity();
                }else if(selected_type==2)
                {
                   navigateToSignUpDriverActivity();
                }

            }
        });
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            phone_code = intent.getStringExtra("phone_code");
            phone = intent.getStringExtra("phone");

        }
    }
}
