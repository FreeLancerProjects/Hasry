package com.hasryApp.activities_fragments.driver.activity_signup;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.hasryApp.R;
import com.hasryApp.activities_fragments.activity_about_app.AboutAppActivity;
import com.hasryApp.activities_fragments.driver.activity_home_driver.HomeDriverActivity;
import com.hasryApp.adapters.CityAdapter;
import com.hasryApp.adapters.NeigborHoodAdapter;
import com.hasryApp.databinding.ActivitySignUpAsDriverBinding;
import com.hasryApp.interfaces.Listeners;
import com.hasryApp.language.Language;
import com.hasryApp.models.CityDataModel;
import com.hasryApp.models.NeighborHoodModel;
import com.hasryApp.models.SignUpDriverModel;
import com.hasryApp.models.UserModel;
import com.hasryApp.preferences.Preferences;
import com.hasryApp.remote.Api;
import com.hasryApp.share.Common;
import com.hasryApp.tags.Tags;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpDriverActivity extends AppCompatActivity implements Listeners.SignUpListener {
    private ActivitySignUpAsDriverBinding binding;
    private final String READ_PERM = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final String write_permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final String camera_permission = Manifest.permission.CAMERA;
    private final int READ_REQ = 1, CAMERA_REQ = 2;
    private Uri uri = null;
    private SignUpDriverModel signUpModel;
    private Preferences preferences;
    private String phone;
    private String phone_code;
    private final int IMG_REQ1 = 3, IMG_REQ2 = 4, IMG_REQ3 = 5;
    private Uri imgUri1 = null, imgUri2 = null, imgUri3 = null;
    private int selectedType;
    private List<String> cityList, neigborhoodlist;
    private CityAdapter cityAdapter;
    private NeigborHoodAdapter neigborHoodAdapter;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up_as_driver);
        getDataFromIntent();
        initView();

    }

    private void initView() {
        cityList = new ArrayList<>();
        neigborhoodlist = new ArrayList<>();
        cityList.add(getString(R.string.choose));
        neigborhoodlist.add(getResources().getString(R.string.ch_neigborhood));
        preferences = Preferences.getInstance();
        signUpModel = new SignUpDriverModel();
        binding.setListener(this);
        signUpModel.setPhone_code(phone_code);
        signUpModel.setPhone(phone);
        binding.setModel(signUpModel);

        binding.checkbox.setOnClickListener(v -> {
            if (binding.checkbox.isChecked()) {
                signUpModel.setTermsAccepted(true);
                navigateToAboutAppActivity();
            } else {
                signUpModel.setTermsAccepted(false);
            }

            binding.setModel(signUpModel);

        });

        cityAdapter = new CityAdapter(cityList, this);
        neigborHoodAdapter = new NeigborHoodAdapter(neigborhoodlist, this);
        binding.spinnerCity.setAdapter(cityAdapter);
        binding.spinnerneibhourhod.setAdapter(neigborHoodAdapter);
        binding.flDocImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateImageAlertDialog(IMG_REQ1);
            }
        });
        binding.flIdImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateImageAlertDialog(IMG_REQ2);
            }
        });
        binding.flLecience.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateImageAlertDialog(IMG_REQ3);
            }
        });
        binding.spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    signUpModel.setCity("");
                } else {
                    signUpModel.setCity(cityList.get(position));

                }

                binding.setModel(signUpModel);
                Log.e("cccccc",cityList.get(position));
                getNeigborhood();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.spinnerneibhourhod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    signUpModel.setNeigborhood("");
                } else {
                    signUpModel.setNeigborhood(cityList.get(position));

                }

                binding.setModel(signUpModel);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getCities();
    }

    private void getCities() {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.show();

        Api.getService(Tags.base_url)
                .getCity()
                .enqueue(new Callback<CityDataModel>() {
                    @Override
                    public void onResponse(Call<CityDataModel> call, Response<CityDataModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null) {

                            cityList.clear();
                            cityList.add(getString(R.string.choose));
                            cityList.addAll(response.body().getData().getCities());
                            runOnUiThread(() -> cityAdapter.notifyDataSetChanged());

                        } else {
                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (response.code() == 500) {
                                Toast.makeText(SignUpDriverActivity.this, "Server Error", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(SignUpDriverActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();


                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<CityDataModel> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(SignUpDriverActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SignUpDriverActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {
                        }
                    }
                });
    }

    private void getNeigborhood() {
        neigborhoodlist.clear();
        neigborHoodAdapter.notifyDataSetChanged();
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.show();

        Log.e("mmmmmmm",signUpModel.getCity());
        Api.getService(Tags.base_url)
                .getNeigborhood(signUpModel.getCity())
                .enqueue(new Callback<NeighborHoodModel>() {
                    @Override
                    public void onResponse(Call<NeighborHoodModel> call, Response<NeighborHoodModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null) {

                            neigborhoodlist.clear();
                            neigborhoodlist.add(getString(R.string.ch_neigborhood));
                            neigborhoodlist.addAll(response.body().getData().getNeighborhoods());
                            runOnUiThread(() -> neigborHoodAdapter.notifyDataSetChanged());

                        } else {
                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (response.code() == 500) {
                                Toast.makeText(SignUpDriverActivity.this, "Server Error", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(SignUpDriverActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();


                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<NeighborHoodModel> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(SignUpDriverActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SignUpDriverActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {
                        }
                    }
                });
    }

    private void navigateToAboutAppActivity() {

        Intent intent = new Intent(this, AboutAppActivity.class);
        intent.putExtra("type", 1);
        startActivity(intent);
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            phone_code = intent.getStringExtra("phone_code");
            phone = intent.getStringExtra("phone");

        }
    }

    @Override
    public void openSheet() {
        binding.expandLayout.setExpanded(true, true);
    }

    @Override
    public void closeSheet() {
        binding.expandLayout.collapse(true);

    }


    @Override
    public void checkDataValid() {


        if (signUpModel.isDataValid(this)) {
            Common.CloseKeyBoard(this, binding.edtName);
            signUp();
        }

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
        } else if (requestCode == IMG_REQ1) {

            if (selectedType == 1) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SelectImage(selectedType, IMG_REQ1);
                } else {
                    Toast.makeText(this, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
                }
            } else {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    SelectImage(selectedType, IMG_REQ1);
                } else {
                    Toast.makeText(this, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == IMG_REQ2) {
            if (selectedType == 1) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SelectImage(selectedType, IMG_REQ2);
                } else {
                    Toast.makeText(this, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
                }
            } else {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    SelectImage(selectedType, IMG_REQ2);
                } else {
                    Toast.makeText(this, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == IMG_REQ3) {

            if (selectedType == 1) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SelectImage(selectedType, IMG_REQ3);
                } else {
                    Toast.makeText(this, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
                }
            } else {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    SelectImage(selectedType, IMG_REQ3);
                } else {
                    Toast.makeText(this, getString(R.string.perm_image_denied), Toast.LENGTH_SHORT).show();
                }
            }


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == READ_REQ && resultCode == Activity.RESULT_OK && data != null) {

            uri = data.getData();
            File file = new File(Common.getImagePath(this, uri));
            Picasso.get().load(file).fit().into(binding.image);
            binding.icon.setVisibility(View.GONE);


        } else if (requestCode == CAMERA_REQ && resultCode == Activity.RESULT_OK && data != null) {

            binding.icon.setVisibility(View.GONE);
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            uri = getUriFromBitmap(bitmap);
            if (uri != null) {
                String path = Common.getImagePath(this, uri);

                if (path != null) {
                    Picasso.get().load(new File(path)).fit().into(binding.image);

                } else {
                    Picasso.get().load(uri).fit().into(binding.image);

                }
            }


        } else if (requestCode == IMG_REQ1 && resultCode == Activity.RESULT_OK && data != null) {
            if (selectedType == 1) {
                imgUri1 = data.getData();
                binding.l.setVisibility(View.GONE);
                File file = new File(Common.getImagePath(this, imgUri1));
                Picasso.get().load(file).fit().into(binding.imageDoc);
            } else if (selectedType == 2) {
                binding.l.setVisibility(View.GONE);

                Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                imgUri1 = getUriFromBitmap(bitmap);
                if (imgUri1 != null) {
                    String path = Common.getImagePath(this, imgUri1);

                    if (path != null) {
                        Picasso.get().load(new File(path)).fit().into(binding.imageDoc);

                    } else {
                        Picasso.get().load(imgUri1).fit().into(binding.imageDoc);

                    }
                }
            }


        } else if (requestCode == IMG_REQ2 && resultCode == Activity.RESULT_OK && data != null) {

            if (selectedType == 1) {
                imgUri2 = data.getData();
                binding.ll.setVisibility(View.GONE);
                File file = new File(Common.getImagePath(this, imgUri2));

                Picasso.get().load(file).fit().into(binding.imageId);
            } else if (selectedType == 2) {

                binding.ll.setVisibility(View.GONE);

                Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                imgUri2 = getUriFromBitmap(bitmap);
                if (imgUri2 != null) {
                    String path = Common.getImagePath(this, imgUri2);

                    if (path != null) {
                        Picasso.get().load(new File(path)).fit().into(binding.imageId);

                    } else {
                        Picasso.get().load(imgUri2).fit().into(binding.imageId);

                    }
                }
            }


        } else if (requestCode == IMG_REQ3 && resultCode == Activity.RESULT_OK && data != null) {

            if (selectedType == 1) {
                imgUri3 = data.getData();
                binding.lll.setVisibility(View.GONE);
                File file = new File(Common.getImagePath(this, imgUri3));

                Picasso.get().load(file).fit().into(binding.imageLecience);
            } else if (selectedType == 2) {

                binding.lll.setVisibility(View.GONE);

                Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                imgUri3 = getUriFromBitmap(bitmap);
                if (imgUri3 != null) {
                    String path = Common.getImagePath(this, imgUri3);

                    if (path != null) {
                        Picasso.get().load(new File(path)).fit().into(binding.imageLecience);

                    } else {
                        Picasso.get().load(imgUri3).fit().into(binding.imageLecience);

                    }
                }


            }


        }
    }


    private Uri getUriFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        return Uri.parse(MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "", ""));
    }

    private void signUp() {


        if (uri == null) {
            if (
                    imgUri1 != null &&
                            imgUri2 != null &&
                            imgUri3 != null

            ) {
                signUpWithoutImage();
            } else {
                if (imgUri2 == null) {
                    Toast.makeText(this, R.string.choose_identity_card_image, Toast.LENGTH_SHORT).show();
                }

                if (imgUri3 == null) {
                    Toast.makeText(this, R.string.choose_license_image, Toast.LENGTH_SHORT).show();
                }

                if (imgUri1 == null) {
                    Toast.makeText(this, R.string.ch_img_car_form, Toast.LENGTH_SHORT).show();
                }


            }
        } else {
            if (
                    imgUri1 != null &&
                            imgUri2 != null &&
                            imgUri3 != null

            ) {
                signUpWithImage();
            } else {
                if (imgUri2 == null) {
                    Toast.makeText(this, R.string.choose_identity_card_image, Toast.LENGTH_SHORT).show();
                }

                if (imgUri3 == null) {
                    Toast.makeText(this, R.string.choose_license_image, Toast.LENGTH_SHORT).show();
                }

                if (imgUri1 == null) {
                    Toast.makeText(this, R.string.ch_img_car_form, Toast.LENGTH_SHORT).show();
                }


            }
        }
    }

    private void signUpWithoutImage() {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        RequestBody name_part = Common.getRequestBodyText(signUpModel.getName());
        RequestBody phone_code_part = Common.getRequestBodyText(signUpModel.getPhone_code());
        RequestBody phone_part = Common.getRequestBodyText(signUpModel.getPhone());
        RequestBody email_part = Common.getRequestBodyText(signUpModel.getEmail());

        RequestBody city_part = Common.getRequestBodyText(signUpModel.getCity());
        RequestBody neig_part = Common.getRequestBodyText(signUpModel.getNeigborhood());
        RequestBody type_part = Common.getRequestBodyText("driver");
        RequestBody software_part = Common.getRequestBodyText("1");

        MultipartBody.Part image_national_id_part = Common.getMultiPart(this, imgUri2, "car_documentation_image");
        MultipartBody.Part image_license_part = Common.getMultiPart(this, imgUri3, "drive_documentation_image");

        MultipartBody.Part image_form_part = Common.getMultiPart(this, imgUri1, "car_image");


        Api.getService(Tags.base_url)
                .registerDelegatewithoutimage(name_part, email_part, phone_code_part, phone_part, city_part, neig_part, type_part, software_part, image_form_part, image_national_id_part, image_license_part)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null) {
                            preferences.create_update_userdata(SignUpDriverActivity.this, response.body());
                            navigateToHomeActivity();
                        } else {
                            if (response.code() == 500) {
                                Toast.makeText(SignUpDriverActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            } else if (response.code() == 422) {
                                Toast.makeText(SignUpDriverActivity.this, R.string.user_found, Toast.LENGTH_SHORT).show();

                            } else {
                                try {
                                    Log.e("jdjjdjjd", response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(SignUpDriverActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(SignUpDriverActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SignUpDriverActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });
    }

    private void signUpWithImage() {

        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
        RequestBody name_part = Common.getRequestBodyText(signUpModel.getName());
        RequestBody phone_code_part = Common.getRequestBodyText(signUpModel.getPhone_code());
        RequestBody phone_part = Common.getRequestBodyText(signUpModel.getPhone());
        RequestBody email_part = Common.getRequestBodyText(signUpModel.getEmail());

        RequestBody city_part = Common.getRequestBodyText(signUpModel.getCity());
        RequestBody neig_part = Common.getRequestBodyText(signUpModel.getNeigborhood());
        RequestBody type_part = Common.getRequestBodyText("driver");
        RequestBody software_part = Common.getRequestBodyText("1");

        MultipartBody.Part image_national_id_part = Common.getMultiPart(this, imgUri2, "car_documentation_image");
        MultipartBody.Part image_license_part = Common.getMultiPart(this, imgUri3, "drive_documentation_image");

        MultipartBody.Part image_form_part = Common.getMultiPart(this, imgUri1, "car_image");
        MultipartBody.Part image = Common.getMultiPart(this, uri, "logo");


        Api.getService(Tags.base_url)
                .registerDelegatewithimage(name_part, email_part, phone_code_part, phone_part, city_part, neig_part, type_part, software_part, image, image_form_part, image_national_id_part, image_license_part)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null) {
                            preferences.create_update_userdata(SignUpDriverActivity.this, response.body());
                            navigateToHomeActivity();
                        } else {
                            if (response.code() == 500) {
                                Toast.makeText(SignUpDriverActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            } else if (response.code() == 422) {
                                Toast.makeText(SignUpDriverActivity.this, R.string.user_found, Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(SignUpDriverActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(SignUpDriverActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SignUpDriverActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });

    }

    private void CreateImageAlertDialog(final int img_req) {

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(true)
                .create();


        View view = LayoutInflater.from(this).inflate(R.layout.dialog_select_image, null);
        Button btn_camera = view.findViewById(R.id.btn_camera);
        Button btn_gallery = view.findViewById(R.id.btn_gallery);
        Button btn_cancel = view.findViewById(R.id.btn_cancel);


        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                selectedType = 2;
                Check_CameraPermission(img_req);

            }
        });

        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                selectedType = 1;
                CheckReadPermission(img_req);


            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.setCanceledOnTouchOutside(false);
        dialog.setView(view);
        dialog.show();
    }

    private void CheckReadPermission(int img_req) {
        if (ActivityCompat.checkSelfPermission(this, READ_PERM) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{READ_PERM}, img_req);
        } else {
            SelectImage(1, img_req);
        }
    }

    private void Check_CameraPermission(int img_req) {
        if (ContextCompat.checkSelfPermission(this, camera_permission) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, write_permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{camera_permission, write_permission}, img_req);
        } else {
            SelectImage(2, img_req);

        }

    }

    private void SelectImage(int type, int img_req) {

        Intent intent = new Intent();

        if (type == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            } else {
                intent.setAction(Intent.ACTION_GET_CONTENT);

            }

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("image/*");
            startActivityForResult(intent, img_req);

        } else if (type == 2) {
            try {
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, img_req);
            } catch (SecurityException e) {
                Toast.makeText(this, R.string.perm_image_denied, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, R.string.perm_image_denied, Toast.LENGTH_SHORT).show();

            }


        }
    }

    private void navigateToHomeActivity() {
        Intent intent = new Intent(this, HomeDriverActivity.class);
        startActivity(intent);
        finish();
    }
}
