package com.hasryApp.activities_fragments.client.activity_search;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
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

import com.hasryApp.R;
import com.hasryApp.activities_fragments.client.activity_product_details.ProductDetailsActivity;
import com.hasryApp.adapters.SearchAdapter;
import com.hasryApp.databinding.ActivitySearchBinding;
import com.hasryApp.interfaces.Listeners;
import com.hasryApp.language.Language;
import com.hasryApp.models.CreateOrderModel;
import com.hasryApp.models.ItemCartModel;
import com.hasryApp.models.MarketDataModel;
import com.hasryApp.models.OfferModel;
import com.hasryApp.models.SearchDataModel;
import com.hasryApp.models.UserModel;
import com.hasryApp.preferences.Preferences;
import com.hasryApp.remote.Api;
import com.hasryApp.share.Common;
import com.hasryApp.tags.Tags;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity implements Listeners.BackListener{
    private ActivitySearchBinding binding;
    private String lang;
    private List<OfferModel> productList;
    private SearchAdapter adapter;
    private String query="";
    private UserModel userModel;
    private Preferences preferences;
    private LinearLayoutManager manager;
    private MarketDataModel.Data.Market market;
    private CreateOrderModel createOrderModel;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang","ar")));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        getDataFromIntent();
        initView();
    }



    private void initView()
    {
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);

        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        createOrderModel = preferences.getCartData(this);
        if (createOrderModel==null){
            createOrderModel = new CreateOrderModel();
            createOrderModel.setMarkter_id(market.getId());
        }


        productList = new ArrayList<>();
        manager = new GridLayoutManager(this,2);
        binding.recView.setLayoutManager(manager);
        adapter = new SearchAdapter(productList,this);
        binding.recView.setAdapter(adapter);





        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length()>0)
                {
                    query = editable.toString();

                    search();
                    binding.progBar.setVisibility(View.GONE);
                    binding.recView.setVisibility(View.VISIBLE);
                }else
                {
                    query ="";
                    productList.clear();
                    adapter.notifyDataSetChanged();


                }
            }
        });


    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("data")) {
            market = (MarketDataModel.Data.Market) intent.getSerializableExtra("data");

        }
    }

    private void search()
    {
        binding.progBar.setVisibility(View.VISIBLE);
        productList.clear();
        adapter.notifyDataSetChanged();

        Api.getService(Tags.base_url)
                .search(query)
                .enqueue(new Callback<SearchDataModel>() {
                    @Override
                    public void onResponse(Call<SearchDataModel> call, Response<SearchDataModel> response) {
                        binding.progBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            productList.addAll(response.body().getData().getProducts());

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
                                Toast.makeText(SearchActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SearchActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SearchDataModel> call, Throwable t) {
                        try {
                            binding.progBar.setVisibility(View.GONE);

                            if (t.getMessage() != null) {
                                Log.e("error_not_code", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(SearchActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SearchActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });
    }


    public void setItemData(OfferModel offerModel) {
        Intent intent = new Intent(this, ProductDetailsActivity.class);
        intent.putExtra("name",market);
        intent.putExtra("data",offerModel);
        startActivity(intent);
    }

    public void addToCart(OfferModel offerModel) {
        if (market.getId()==createOrderModel.getMarkter_id())
        {
            ItemCartModel model = new ItemCartModel(offerModel.getId(),offerModel.getId(),offerModel.getImage(),offerModel.getTitle(),1);
            model.setPrice_before_discount(Double.parseDouble(offerModel.getPrice()));

            if (offerModel.getOffer()==null){
                model.setPrice(Double.parseDouble(offerModel.getPrice()));
                model.setOffer_id(0);
            }else {

                if (offerModel.getOffer().getOffer_status().trim().equals("open"))
                {
                    if (offerModel.getOffer().getOffer_type().trim().equals("per")){
                        double price_before_discount = Double.parseDouble(offerModel.getPrice());
                        double price_after_discount = price_before_discount-(price_before_discount*(offerModel.getOffer().getOffer_value()/100));
                        model.setPrice(price_after_discount);
                        model.setOffer_id(offerModel.getOffer().getId());
                    }else {
                        double price_before_discount = Double.parseDouble(offerModel.getPrice());
                        double price_after_discount = price_before_discount-offerModel.getOffer().getOffer_value();
                        model.setPrice(price_after_discount);
                        model.setOffer_id(offerModel.getOffer().getId());
                    }
                }else {
                    model.setPrice(Double.parseDouble(offerModel.getPrice()));
                    model.setOffer_id(0);
                }
            }


            createOrderModel.addNewProduct(model);
            preferences.create_update_cart(this,createOrderModel);
            Toast.makeText(this, getString(R.string.added_suc), Toast.LENGTH_SHORT).show();
        }else {
            Common.CreateDialogAlert(this,getString(R.string.diff_market));
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
