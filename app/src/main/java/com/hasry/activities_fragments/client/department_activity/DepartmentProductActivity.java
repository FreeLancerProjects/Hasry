package com.hasry.activities_fragments.client.department_activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.tabs.TabLayout;
import com.hasry.R;
import com.hasry.activities_fragments.client.activity_product_details.ProductDetailsActivity;
import com.hasry.adapters.SearchAdapter;
import com.hasry.databinding.ActivityDepartmentProductBinding;
import com.hasry.databinding.ActivitySearchBinding;
import com.hasry.interfaces.Listeners;
import com.hasry.language.Language;
import com.hasry.models.CategoryDataModel;
import com.hasry.models.CreateOrderModel;
import com.hasry.models.DepartmentPRoductDataModel;
import com.hasry.models.ItemCartModel;
import com.hasry.models.MarketDataModel;
import com.hasry.models.OfferModel;
import com.hasry.models.SearchDataModel;
import com.hasry.models.UserModel;
import com.hasry.preferences.Preferences;
import com.hasry.remote.Api;
import com.hasry.share.Common;
import com.hasry.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DepartmentProductActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityDepartmentProductBinding binding;
    private String lang;
    private List<OfferModel> productList;
    private SearchAdapter adapter;
    private UserModel userModel;
    private Preferences preferences;
    private LinearLayoutManager manager;
    private MarketDataModel.Data.Market market;
    private CreateOrderModel createOrderModel;
    private List<CategoryDataModel.CategoryModel> categoryModelList;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_department_product);
        getDataFromIntent();
        initView();
        getAllCategories();
    }


    private void initView() {
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);

        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        createOrderModel = preferences.getCartData(this);
        if (createOrderModel == null) {
            createOrderModel = new CreateOrderModel();
            createOrderModel.setMarkter_id(market.getId());
        }


        productList = new ArrayList<>();
        categoryModelList = new ArrayList<>();
        manager = new GridLayoutManager(this, 2);
        binding.recView.setLayoutManager(manager);
        adapter = new SearchAdapter(productList, this);
        binding.recView.setAdapter(adapter);


    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("data")) {
            market = (MarketDataModel.Data.Market) intent.getSerializableExtra("data");

        }
    }

    private void getAds(String department) {
        binding.progBar.setVisibility(View.VISIBLE);
        productList.clear();
        adapter.notifyDataSetChanged();

        Api.getService(Tags.base_url)
                .getAds("off", department)
                .enqueue(new Callback<DepartmentPRoductDataModel>() {
                    @Override
                    public void onResponse(Call<DepartmentPRoductDataModel> call, Response<DepartmentPRoductDataModel> response) {
                        binding.progBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            productList.addAll(response.body().getData());

                            if (productList.size() > 0) {
                                adapter.notifyDataSetChanged();
                                binding.tvNoData.setVisibility(View.GONE);
                            } else {
                                binding.tvNoData.setVisibility(View.VISIBLE);

                            }


                        } else {
                            binding.progBar.setVisibility(View.GONE);

                            try {
                                Log.e("errorNotCode", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                                Toast.makeText(DepartmentProductActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(DepartmentProductActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<DepartmentPRoductDataModel> call, Throwable t) {
                        try {
                            binding.progBar.setVisibility(View.GONE);

                            if (t.getMessage() != null) {
                                Log.e("error_not_code", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(DepartmentProductActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(DepartmentProductActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });
    }

    private void getAllCategories() {

        Api.getService(Tags.base_url)
                .getAllCategories("off", market.getId() + "")
                .enqueue(new Callback<CategoryDataModel>() {
                    @Override
                    public void onResponse(Call<CategoryDataModel> call, Response<CategoryDataModel> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {

                            updateTabUI(response.body().getData());
                        }
                    }

                    @Override
                    public void onFailure(Call<CategoryDataModel> call, Throwable t) {

                        try {
                            if (t.getMessage() != null) {
                                Log.e("error", t.getMessage());
                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(DepartmentProductActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                } else {
                                    //   Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }

                        } catch (Exception e) {
                        }
                    }
                });


    }

    private void updateTabUI(List<CategoryDataModel.CategoryModel> data) {
//        CategoryDataModel.CategoryModel categoryModel = new CategoryDataModel.CategoryModel(0,getString(R.string.show_all));
//       data.add(0,categoryModel);
        categoryModelList.clear();
        categoryModelList.addAll(data);
        for (CategoryDataModel.CategoryModel categoryModel1 : data) {

            binding.tab.addTab(binding.tab.newTab().setText(categoryModel1.getTitle()));


        }

        new Handler().postDelayed(
                () -> binding.tab.getTabAt(0).select(), 100);


    }

    public void setItemData(OfferModel offerModel) {
        Intent intent = new Intent(this, ProductDetailsActivity.class);
        intent.putExtra("name", market);
        intent.putExtra("data", offerModel);
        startActivity(intent);
    }

    public void addToCart(OfferModel offerModel) {
        if (market.getId() == createOrderModel.getMarkter_id()) {
            ItemCartModel model = new ItemCartModel(offerModel.getId(), offerModel.getId(), offerModel.getImage(), offerModel.getTitle(), 1);
            model.setPrice_before_discount(Double.parseDouble(offerModel.getPrice()));

            if (offerModel.getOffer() == null) {
                model.setPrice(Double.parseDouble(offerModel.getPrice()));
                model.setOffer_id(0);
            } else {

                if (offerModel.getOffer().getOffer_status().trim().equals("open")) {
                    if (offerModel.getOffer().getOffer_type().trim().equals("per")) {
                        double price_before_discount = Double.parseDouble(offerModel.getPrice());
                        double price_after_discount = price_before_discount - (price_before_discount * (offerModel.getOffer().getOffer_value() / 100));
                        model.setPrice(price_after_discount);
                        model.setOffer_id(offerModel.getOffer().getId());
                    } else {
                        double price_before_discount = Double.parseDouble(offerModel.getPrice());
                        double price_after_discount = price_before_discount - offerModel.getOffer().getOffer_value();
                        model.setPrice(price_after_discount);
                        model.setOffer_id(offerModel.getOffer().getId());
                    }
                } else {
                    model.setPrice(Double.parseDouble(offerModel.getPrice()));
                    model.setOffer_id(0);
                }
            }


            createOrderModel.addNewProduct(model);
            preferences.create_update_cart(this, createOrderModel);
            Toast.makeText(this, getString(R.string.added_suc), Toast.LENGTH_SHORT).show();
        } else {
            Common.CreateDialogAlert(this, getString(R.string.diff_market));
        }
    }

    @Override
    public void back() {

        finish();
    }


    @Override
    public void onBackPressed() {
        back();
    }
}
