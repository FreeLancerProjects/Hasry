package com.hasryApp.activities_fragments.client.activity_client_profile;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.hasryApp.R;
import com.hasryApp.databinding.ActivityClientProfileBinding;
import com.hasryApp.databinding.ActivityProfileBinding;
import com.hasryApp.interfaces.Listeners;
import com.hasryApp.language.Language;
import com.hasryApp.models.UserModel;
import com.hasryApp.preferences.Preferences;
import com.hasryApp.remote.Api;
import com.hasryApp.share.Common;
import com.hasryApp.tags.Tags;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Random;

import io.paperdb.Paper;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClientProfileActivity extends AppCompatActivity implements Listeners.BackListener, Listeners.SignUpListener  {
    private ActivityClientProfileBinding binding;
    private String lang;
    private Preferences preferences;
    private UserModel userModel;
    String notification_status = "";
    private final String READ_PERM = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final String write_permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final String camera_permission = Manifest.permission.CAMERA;
    private final int READ_REQ = 1, CAMERA_REQ = 2;
    private Uri uri = null;
    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_client_profile);
        initView();
    }


    private void initView() {
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setLang(lang);
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        Random random = new Random();

        binding.setRandom(random);
        binding.setBackListener(this);
        binding.setListener(this);

        binding.setModel(userModel.getData());

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

    public void UpdateNotificationStaus(View view) {
        Log.e(";lflfll", notification_status);

        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        Api.getService(Tags.base_url)
                .UpdatereadNotificationStaus(notification_status, userModel.getData().getId())
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
                                Toast.makeText(ClientProfileActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            } else {

                                Toast.makeText(ClientProfileActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(ClientProfileActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ClientProfileActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });

    }

    private void update(UserModel body) {
        preferences.create_update_userdata(ClientProfileActivity.this, body);
        body.getData().setToken(userModel.getData().getToken());
        userModel = body;
        if (userModel.getData().getNotification_status() != null) {
            if (userModel.getData().getNotification_status().equals("enable")) {
                notification_status = "disable";
            } else {
                notification_status = "enable";
            }
        } else {
            notification_status = "enable";
        }
        Log.e("sssssssss", notification_status);

        // Log.e("ssss", userModel.getData().getNotification_status() + "" + body.getData().getNotification_status());
        binding.setModel(body.getData()
        );


    }
    @Override
    public void openSheet()
    {
            binding.expandLayout.setExpanded(true, true);
    }

    @Override
    public void closeSheet() {
         binding.expandLayout.collapse(true);

    }


    @Override
    public void checkDataValid() {




    }

    @Override
    public void checkReadPermission() {
        closeSheet();
        if (ActivityCompat.checkSelfPermission(this, READ_PERM) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{READ_PERM}, READ_REQ);
        } else {
            SelectImage(READ_REQ);
        }
    }

    @Override
    public void checkCameraPermission() {

        closeSheet();

        if (ContextCompat.checkSelfPermission(this, write_permission) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, camera_permission) == PackageManager.PERMISSION_GRANTED
        ) {
            SelectImage(CAMERA_REQ);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{camera_permission, write_permission}, CAMERA_REQ);
        }
    }

    private void SelectImage(int req) {

        Intent intent = new Intent();

        if (req == READ_REQ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            } else {
                intent.setAction(Intent.ACTION_GET_CONTENT);

            }

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("image/*");
            startActivityForResult(intent, req);

        } else if (req == CAMERA_REQ) {
            try {
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, req);
            } catch (SecurityException e) {
                Toast.makeText(this, R.string.perm_image_denied, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, R.string.perm_image_denied, Toast.LENGTH_SHORT).show();

            }


        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_REQ) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SelectImage(requestCode);
            } else {
                Toast.makeText(this, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == CAMERA_REQ) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                SelectImage(requestCode);
            } else {
                Toast.makeText(this, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == READ_REQ && resultCode == Activity.RESULT_OK && data != null) {

            uri = data.getData();
            File file = new File(Common.getImagePath(this, uri));
          //  Picasso.get().load(file).fit().into(binding.image);

            editImageProfile( uri.toString());

        } else if (requestCode == CAMERA_REQ && resultCode == Activity.RESULT_OK && data != null) {

            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            uri = getUriFromBitmap(bitmap);
            if (uri != null) {
                String path = Common.getImagePath(this, uri);

                if (path != null) {
                    editImageProfile( uri.toString());

                    //  Picasso.get().load(new File(path)).fit().into(binding.image);

                } else {
                    editImageProfile( uri.toString());

                    //  Picasso.get().load(uri).fit().into(binding.image);

                }
            }





        }
    }

    private void editImageProfile(String image) {
        ProgressDialog dialog = Common.createProgressDialog(this, getResources().getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();

        RequestBody id_part = Common.getRequestBodyText(String.valueOf(userModel.getData().getId()+""));

        MultipartBody.Part image_part = Common.getMultiPart(this, Uri.parse(image), "logo");

        Api.getService(Tags.base_url)
                .editClientProfileWithImage(id_part,"Bearer "+userModel.getData().getToken(), image_part)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null) {
                            //listener.onSuccess(response.body());

                            Toast.makeText(ClientProfileActivity.this, getString(R.string.suc), Toast.LENGTH_SHORT).show();
                            update(response.body());

                        } else {
                            Log.e("codeimage", response.code() + "_");
                            try {
                                Toast.makeText(ClientProfileActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                Log.e("respons", response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            // listener.onFailed(response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Toast.makeText(ClientProfileActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                        }
                    }
                });
    }

    private Uri getUriFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        return Uri.parse(MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "", ""));
    }
}


