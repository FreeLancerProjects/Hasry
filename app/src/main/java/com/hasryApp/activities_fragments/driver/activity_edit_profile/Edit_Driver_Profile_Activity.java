package com.hasryApp.activities_fragments.driver.activity_edit_profile;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.hasryApp.R;
import com.hasryApp.activities_fragments.client.activity_edit_profile.Edit_Profile_Activity;
import com.hasryApp.activities_fragments.driver.activity_signup.SignUpDriverActivity;
import com.hasryApp.adapters.CityAdapter;
import com.hasryApp.adapters.NeigborHoodAdapter;
import com.hasryApp.databinding.ActivityDriverEditProfileBinding;
import com.hasryApp.interfaces.Listeners;
import com.hasryApp.language.Language;
import com.hasryApp.models.CityDataModel;
import com.hasryApp.models.EditDriverprofileModel;
import com.hasryApp.models.EditprofileModel;
import com.hasryApp.models.NeighborHoodModel;
import com.hasryApp.models.UserModel;
import com.hasryApp.preferences.Preferences;
import com.hasryApp.remote.Api;
import com.hasryApp.share.Common;
import com.hasryApp.tags.Tags;
import com.mukesh.countrypicker.CountryPicker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Edit_Driver_Profile_Activity extends AppCompatActivity implements Listeners.EditprofileListener, Listeners.BackListener {
    private String current_language;
    private ActivityDriverEditProfileBinding binding;
    private CountryPicker countryPicker;
    private EditDriverprofileModel editprofileModel;
    private Preferences preferences;
    private List<String> cityList, neigborhoodlist;
    private CityAdapter cityAdapter;
    private NeigborHoodAdapter neigborHoodAdapter;
    private UserModel userModel;
    private final int IMG_REQ1 = 1, IMG_REQ2 = 2;
    private Uri imgUri1 = null;
    private final String READ_PERM = Manifest.permission.READ_EXTERNAL_STORAGE;
    private final String write_permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private final String camera_permission = Manifest.permission.CAMERA;
    private String city_id = "";

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_driver_edit_profile);

        initView();
        if (userModel != null) {
            updatedata(userModel);
        }
    }

    private void updatedata(UserModel userModel) {
        this.userModel = userModel;

        preferences.create_update_userdata(this, userModel);
        editprofileModel.setCity_id(this.userModel.getData().getCity() + "");
        editprofileModel.setNeigbourhood_id(userModel.getData().getDistrict());
        Log.e("lflflfll", userModel.getData().getDistrict());
        editprofileModel.setName(this.userModel.getData().getName());
        editprofileModel.setEmail(this.userModel.getData().getEmail());
        binding.setUserModel(userModel);
        binding.setViewModel(editprofileModel);
    }


    private void initView() {
        cityList = new ArrayList<>();
        cityList.add(getString(R.string.choose));
        neigborhoodlist = new ArrayList<>();
        neigborhoodlist.add(getResources().getString(R.string.ch_neigborhood));
        editprofileModel = new EditDriverprofileModel();
        Paper.init(this);
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        current_language = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setLang(current_language);
        binding.setViewModel(editprofileModel);
        binding.setBackListener(this);
        binding.setEditprofilelistner(this);
        binding.setUserModel(userModel);
        cityAdapter = new CityAdapter(cityList, this);
        binding.spinnerCity.setAdapter(cityAdapter);
        neigborHoodAdapter = new NeigborHoodAdapter(neigborhoodlist, this);
        binding.spinnerneigbourhood.setAdapter(neigborHoodAdapter);

        getCities();

        binding.spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    city_id = "";
                    editprofileModel.setCity_id(city_id);
                    binding.setViewModel(editprofileModel);
                } else {
                    city_id = String.valueOf(cityList.get(i));
                    editprofileModel.setCity_id(city_id);
                    binding.setViewModel(editprofileModel);
                    getNeigborhood();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.spinnerneigbourhood.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    editprofileModel.setNeigbourhood_id("");
                    binding.setViewModel(editprofileModel);
                } else {
                    editprofileModel.setNeigbourhood_id(String.valueOf(neigborhoodlist.get(i)));
                    binding.setViewModel(editprofileModel);
                 //   getNeigborhood();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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
                            updateCityAdapter();

                        } else {
                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (response.code() == 500) {
                                Toast.makeText(Edit_Driver_Profile_Activity.this, "Server Error", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(Edit_Driver_Profile_Activity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();


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
                                    Toast.makeText(Edit_Driver_Profile_Activity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(Edit_Driver_Profile_Activity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {
                        }
                    }
                });
    }

    private void updateCityAdapter() {


        if (userModel != null) {
            for (int i = 1; i < cityList.size(); i++) {
                if (cityList.get(i).equals(userModel.getData().getCity())) {
                    binding.spinnerCity.setSelection(i);
                    getNeigborhood();
                   // updateNeibourhoodAdapter();
                }
            }
        }

    }

    private void getNeigborhood() {
        neigborhoodlist.clear();
        neigborHoodAdapter.notifyDataSetChanged();
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.show();

        Api.getService(Tags.base_url)
                .getNeigborhood(editprofileModel.getCity_id())
                .enqueue(new Callback<NeighborHoodModel>() {
                    @Override
                    public void onResponse(Call<NeighborHoodModel> call, Response<NeighborHoodModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful() && response.body() != null) {

                            neigborhoodlist.clear();
                            neigborhoodlist.add(getString(R.string.ch_neigborhood));
                            neigborhoodlist.addAll(response.body().getData().getNeighborhoods());
                            runOnUiThread(() -> neigborHoodAdapter.notifyDataSetChanged());
                           updateNeibourhoodAdapter();

                        } else {
                            try {

                                Log.e("error", response.code() + "_" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (response.code() == 500) {
                                Toast.makeText(Edit_Driver_Profile_Activity.this, "Server Error", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(Edit_Driver_Profile_Activity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();


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
                                    Toast.makeText(Edit_Driver_Profile_Activity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(Edit_Driver_Profile_Activity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {
                        }
                    }
                });
    }

    private void updateNeibourhoodAdapter() {
        binding.spinnerneigbourhood.setSelection(0);
        if (userModel != null) {

            for (int i = 1; i < neigborhoodlist.size(); i++) {
                Log.e("ldldldll",neigborhoodlist.get(i));

                if (neigborhoodlist.get(i).equals(userModel.getData().getDistrict())&&city_id.equals(userModel.getData().getCity())) {
                    binding.spinnerneigbourhood.setSelection(i);

                }
            }
        }
      //  neigborHoodAdapter.notifyDataSetChanged();


    }


    @Override
    public void Editprofile() {

        binding.setViewModel(editprofileModel);
        if (editprofileModel.isDataValid(this)) {
            editprofile(editprofileModel);
        }
    }

    private void editprofile(EditDriverprofileModel editprofileModel) {
        try {
            ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
            dialog.setCancelable(false);
            dialog.show();
            Api.getService(Tags.base_url)
                    .editprofile("Bearer " + userModel.getData().getToken(), editprofileModel.getName(), editprofileModel.getEmail(), editprofileModel.getCity_id(), editprofileModel.getNeigbourhood_id(), userModel.getData().getId() + "")
                    .enqueue(new Callback<UserModel>() {
                        @Override
                        public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                            dialog.dismiss();
                            if (response.isSuccessful() && response.body() != null) {
                                Toast.makeText(Edit_Driver_Profile_Activity.this, getString(R.string.suc), Toast.LENGTH_SHORT).show();

                                preferences.create_update_userdata(Edit_Driver_Profile_Activity.this, response.body());
                                finish();

                            } else {
                                try {

                                    Log.e("error", response.code() + "_" + response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                Toast.makeText(Edit_Driver_Profile_Activity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();


                            }
                        }

                        @Override
                        public void onFailure(Call<UserModel> call, Throwable t) {
                            try {
                                dialog.dismiss();
                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(Edit_Driver_Profile_Activity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(Edit_Driver_Profile_Activity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {
        }
    }

    @Override
    public void back() {
        finish();
    }


}
