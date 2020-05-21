package com.hasry.activities_fragments.client.activity_markets;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hasry.R;
import com.hasry.activities_fragments.client.activity_home.HomeActivity;
import com.hasry.activities_fragments.client.activity_market_data.MarketDataActivity;
import com.hasry.adapters.MarketAdapter;
import com.hasry.databinding.ActivityAboutAppBinding;
import com.hasry.databinding.ActivityMarketsBinding;
import com.hasry.databinding.ActivitySplashBinding;
import com.hasry.interfaces.Listeners;
import com.hasry.language.Language;
import com.hasry.models.MainCategoryDataModel;
import com.hasry.models.MarketDataModel;
import com.hasry.preferences.Preferences;
import com.hasry.remote.Api;
import com.hasry.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MarketsActivity extends AppCompatActivity implements Listeners.BackListener{
    private ActivityMarketsBinding binding;
    private String lang;
    private MainCategoryDataModel.Data.MainDepartments mainDepartments;
    private List<MarketDataModel.Data.Market> marketList;
    private MarketAdapter adapter;



    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang","ar")));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_markets);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent!=null)
        {
            mainDepartments = (MainCategoryDataModel.Data.MainDepartments) intent.getSerializableExtra("data");
        }
    }


    private void initView()
    {
        marketList = new ArrayList<>();
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        binding.recView.setLayoutManager(new GridLayoutManager(this,3));
        adapter = new MarketAdapter(marketList,this);
        binding.recView.setAdapter(adapter);
        getMarkets();

    }

    private void getMarkets() {

        Api.getService(Tags.base_url)
                .getMarkets(mainDepartments.getId())
                .enqueue(new Callback<MarketDataModel>() {
                    @Override
                    public void onResponse(Call<MarketDataModel> call, Response<MarketDataModel> response) {
                        binding.progBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            marketList.clear();
                            marketList.addAll(response.body().getData().getMarkets());

                            if (marketList.size()>0)
                            {
                                adapter.notifyDataSetChanged();
                                binding.tvNoData.setVisibility(View.GONE);
                            }else {
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
                                Toast.makeText(MarketsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MarketsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MarketDataModel> call, Throwable t) {
                        try {
                            binding.progBar.setVisibility(View.GONE);

                            if (t.getMessage() != null) {
                                Log.e("error_not_code", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(MarketsActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MarketsActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });

    }

    public void setItemData(MarketDataModel.Data.Market model) {
        Intent intent = new Intent(this, MarketDataActivity.class);
        intent.putExtra("data",model);
        startActivity(intent);

    }

    @Override
    public void back() {
        finish();
    }


}
