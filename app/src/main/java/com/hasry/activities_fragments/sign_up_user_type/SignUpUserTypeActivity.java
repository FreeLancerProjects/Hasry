package com.hasry.activities_fragments.sign_up_user_type;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;

import com.hasry.R;
import com.hasry.language.Language;

import io.paperdb.Paper;

public class SignUpUserTypeActivity extends AppCompatActivity {


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang","ar")));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_user_type);
    }
}
