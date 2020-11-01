package com.hasryApp.activities_fragments.client.activity_verification_code;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.hasryApp.R;
import com.hasryApp.activities_fragments.client.activity_home.HomeActivity;
import com.hasryApp.activities_fragments.driver.activity_home_driver.HomeDriverActivity;
import com.hasryApp.activities_fragments.sign_up_user_type.SignUpUserTypeActivity;
import com.hasryApp.databinding.ActivityVerificationCodeBinding;
import com.hasryApp.language.Language;
import com.hasryApp.models.UserModel;
import com.hasryApp.preferences.Preferences;
import com.hasryApp.remote.Api;
import com.hasryApp.share.Common;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hasryApp.tags.Tags;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerificationCodeActivity extends AppCompatActivity {
    private ActivityVerificationCodeBinding binding;
    private String lang;
    private String phone_code;
    private String phone;
    private CountDownTimer timer;
    private FirebaseAuth mAuth;
    private String verificationId;
    private String smsCode ;
    private Preferences preferences;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_verification_code);
        getDataFromIntent();
        initView();

    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent!=null)
        {
            phone_code = intent.getStringExtra("phone_code");
            phone = intent.getStringExtra("phone");

        }
    }

    private void initView() {
        preferences = Preferences.getInstance();

        mAuth = FirebaseAuth.getInstance();
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        binding.tvResendCode.setOnClickListener(view ->sendSmsCode());

        binding.btnConfirm.setOnClickListener(view -> {
            String code = binding.edtCode.getText().toString().trim();
            if (!code.isEmpty())
            {
                binding.edtCode.setError(null);
                Common.CloseKeyBoard(this,binding.edtCode);
                checkValidCode(code);
            }else
                {
                   binding.edtCode.setError(getString(R.string.field_required));
                }

        });
        sendSmsCode();

    }

    private void sendSmsCode() {

        startTimer();

        mAuth.setLanguageCode(lang);
        PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                smsCode = phoneAuthCredential.getSmsCode();
                checkValidCode(smsCode);
            }

            @Override
            public void onCodeSent(@NonNull String verification_id, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verification_id, forceResendingToken);
                VerificationCodeActivity.this.verificationId = verification_id;
            }


            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                if (e.getMessage()!=null)
                {
                    Common.CreateDialogAlert(VerificationCodeActivity.this,e.getMessage());
                }else
                    {
                        Common.CreateDialogAlert(VerificationCodeActivity.this,getString(R.string.failed));

                    }
            }
        };
        PhoneAuthProvider.getInstance()
                .verifyPhoneNumber(
                        phone_code+phone,
                        60,
                        TimeUnit.SECONDS,
                        this,
                        mCallBack

                );


    }

    private void startTimer() {
        binding.tvResendCode.setEnabled(false);
        timer = new CountDownTimer(60*1000,  1000) {
            @Override
            public void onTick(long l) {
                SimpleDateFormat format = new SimpleDateFormat("mm:ss", Locale.ENGLISH);
                String time = format.format(new Date(l));
                binding.tvTimer.setText(time);
            }

            @Override
            public void onFinish() {
                binding.tvTimer.setText("00:00");
                binding.tvResendCode.setEnabled(true);
            }
        };

        timer.start();
    }


    private void checkValidCode(String code) {

        if (verificationId!=null)
        {
            Log.e("1","1");
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,code);
            mAuth.signInWithCredential(credential)
                    .addOnSuccessListener(authResult -> {
                        login();
                    }).addOnFailureListener(e -> {
                if (e.getMessage()!=null){
                    Common.CreateDialogAlert(this,e.getMessage());
                }else
                {
                    Toast.makeText(this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                }
            });
        }else
            {
                Log.e("2","2");
                login();
            }

    }

    private void login() {


        ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        Api.getService(Tags.base_url)
                .login(phone_code,phone)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()&&response.body()!=null)
                        {
                            preferences.create_update_userdata(VerificationCodeActivity.this,response.body());
                            if(response.body().getData().getUser_type().equals("client")){
                            navigateToHomeActivity();}
                            else {
                                navigateToHomeDriverActivity( );
                            }
                        }else
                        {

                            try {
                                Log.e("code",response.code()+"__"+response.errorBody().string());
                            } catch (Exception e) {
                              //  e.printStackTrace();
                            }
                            if (response.code()==500)
                            {
                                Toast.makeText(VerificationCodeActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            }else if (response.code()==401)
                            {
                                navigateToUserTypeActivity();
                            }else
                            {

                                Toast.makeText(VerificationCodeActivity.this,getString(R.string.failed), Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(VerificationCodeActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(VerificationCodeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }catch (Exception e)
                        {
                            Log.e("Error",e.getMessage()+"__");
                        }
                    }
                });

    }

    private void navigateToHomeDriverActivity() {
        Intent intent = new Intent(this, HomeDriverActivity.class);
        startActivity(intent);
        finish();
    }


    private void navigateToHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToUserTypeActivity() {
        Intent intent = new Intent(this, SignUpUserTypeActivity.class);
        intent.putExtra("phone",phone);
        intent.putExtra("phone_code",phone_code);
        startActivity(intent);
        finish();
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer!=null)
        {
            timer.cancel();
        }
    }
}
