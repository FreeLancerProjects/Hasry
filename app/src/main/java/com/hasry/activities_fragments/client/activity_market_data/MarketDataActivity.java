package com.hasry.activities_fragments.client.activity_market_data;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hasry.R;
import com.hasry.activities_fragments.activity_map.MapActivity;
import com.hasry.activities_fragments.client.activity_cart.CartActivity;
import com.hasry.activities_fragments.client.activity_home.HomeActivity;
import com.hasry.activities_fragments.client.activity_product_details.ProductDetailsActivity;
import com.hasry.activities_fragments.client.activity_search.SearchActivity;
import com.hasry.adapters.OfferAdapter;
import com.hasry.databinding.ActivityAboutAppBinding;
import com.hasry.databinding.ActivityMarketDataBinding;
import com.hasry.databinding.ActivityMarketsBinding;
import com.hasry.interfaces.Listeners;
import com.hasry.language.Language;
import com.hasry.models.CreateOrderModel;
import com.hasry.models.ItemCartModel;
import com.hasry.models.MainCategoryDataModel;
import com.hasry.models.MarketDataModel;
import com.hasry.models.MostSellerDataModel;
import com.hasry.models.OfferDataModel;
import com.hasry.models.OfferModel;
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

public class MarketDataActivity extends AppCompatActivity implements Listeners.BackListener, Listeners.HomeListener {
    private ActivityMarketDataBinding binding;
    private String lang;
    private MarketDataModel.Data.Market market;
    private List<OfferModel> offerList, mostSellerList,mostUsedList;
    private OfferAdapter offerAdapter, mostSellerAdapter,mostUsedAdapter;
    private int offer_current_page = 1;
    private boolean offer_is_loading = false;
    private int most_seller_current_page = 1;
    private boolean most_seller_is_loading = false;
    private int most_used_current_page = 1;
    private boolean most_used_is_loading = false;
    private CreateOrderModel createOrderModel;
    private Preferences preferences;
    private UserModel userModel;



    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_market_data);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("data")) {
            market = (MarketDataModel.Data.Market) intent.getSerializableExtra("data");

        }
    }


    private void initView() {
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        createOrderModel = preferences.getCartData(this);
        if (createOrderModel==null){
            createOrderModel = new CreateOrderModel();
            createOrderModel.setMarkter_id(market.getId());
        }
        binding.setAction(this);

        offerList = new ArrayList<>();
        mostUsedList = new ArrayList<>();
        mostSellerList = new ArrayList<>();

        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.setModel(market);
        binding.progBarOffer.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        binding.progBarMostSeller.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        binding.progBarMostUsed.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        offerAdapter = new OfferAdapter(offerList, this);
        binding.recViewOffer.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recViewOffer.setAdapter(offerAdapter);
        binding.recViewOffer.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int last_item = ((LinearLayoutManager) binding.recViewOffer.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                if (last_item > 18 && last_item >= offerList.size() - 2 && !offer_is_loading) {
                    offer_is_loading = true;
                    offerList.add(null);
                    offerAdapter.notifyItemInserted(offerList.size() - 1);
                    int page = offer_current_page + 1;
                    loadMoreOffer(page);
                }

            }
        });

        mostSellerAdapter = new OfferAdapter(mostSellerList, this);

        binding.recViewMostSeller.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recViewMostSeller.setAdapter(mostSellerAdapter);
        binding.recViewMostSeller.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int last_item = ((LinearLayoutManager) binding.recViewMostSeller.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                if (last_item > 18 && last_item >= mostSellerList.size() - 2 && !most_seller_is_loading) {
                    most_seller_is_loading = true;
                    mostSellerList.add(null);
                    mostSellerAdapter.notifyItemInserted(mostSellerList.size() - 1);
                    int page = most_seller_current_page + 1;
                    loadMoreMostSeller(page);
                }

            }
        });



        mostUsedAdapter = new OfferAdapter(mostUsedList, this);

        binding.recViewMostUsed.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recViewMostUsed.setAdapter(mostUsedAdapter);
        binding.recViewMostUsed.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int last_item = ((LinearLayoutManager) binding.recViewMostUsed.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                if (last_item > 18 && last_item >= mostUsedList.size() - 2 && !most_used_is_loading) {
                    most_used_is_loading = true;
                    mostUsedList.add(null);
                    mostUsedAdapter.notifyItemInserted(mostUsedList.size() - 1);
                    int page = most_used_current_page + 1;
                    loadMoreMostUsed(page);
                }

            }
        });

        binding.llLocation.setOnClickListener(v -> {
            Intent intent = new Intent(this, MapActivity.class);
            intent.putExtra("lat",Double.parseDouble(market.getLatitude()));
            intent.putExtra("lng",Double.parseDouble(market.getLongitude()));
            intent.putExtra("address",market.getAddress());
            startActivity(intent);

        });

        binding.flSearch.setOnClickListener(v -> {
            Intent intent = new Intent(this, SearchActivity.class);
            intent.putExtra("data",market);
            startActivity(intent);
        });

        getOffers();
        getMostSeller();
        getMostUsed();

    }

    private void getOffers() {

        Api.getService(Tags.base_url)
                .getOffer("on", 20, 1, market.getId())
                .enqueue(new Callback<OfferDataModel>() {
                    @Override
                    public void onResponse(Call<OfferDataModel> call, Response<OfferDataModel> response) {
                        binding.progBarOffer.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            offerList.clear();
                            offerList.addAll(response.body().getData().getOffers().getData());

                            if (offerList.size() > 0) {
                                offerAdapter.notifyDataSetChanged();
                                binding.flOffer.setVisibility(View.VISIBLE);
                            } else {
                                binding.flOffer.setVisibility(View.GONE);

                            }


                        } else {
                            binding.progBarOffer.setVisibility(View.GONE);

                            try {
                                Log.e("errorNotCode", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                                Toast.makeText(MarketDataActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MarketDataActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<OfferDataModel> call, Throwable t) {
                        try {
                            binding.progBarOffer.setVisibility(View.GONE);

                            if (t.getMessage() != null) {
                                Log.e("error_not_code", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(MarketDataActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MarketDataActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });


    }


    private void loadMoreOffer(int page) {

        Api.getService(Tags.base_url)
                .getOffer("on", 20, page, market.getId())
                .enqueue(new Callback<OfferDataModel>() {
                    @Override
                    public void onResponse(Call<OfferDataModel> call, Response<OfferDataModel> response) {

                        offer_is_loading = false;
                        offerList.remove(offerList.size() - 1);
                        offerAdapter.notifyItemRemoved(offerList.size() - 1);
                        binding.progBarOffer.setVisibility(View.GONE);

                        if (response.isSuccessful()) {


                            if (response.body().getData().getOffers().getData().size() > 0) {
                                int old_pos = offerList.size() - 1;
                                offer_current_page = response.body().getData().getOffers().getCurrent_page();
                                offerList.addAll(response.body().getData().getOffers().getData());
                                int new_pos = response.body().getData().getOffers().getData().size();
                                offerAdapter.notifyItemRangeChanged(old_pos, new_pos);

                            }


                        } else {
                            offer_is_loading = false;
                            binding.progBarOffer.setVisibility(View.GONE);

                            try {
                                Log.e("errorNotCode", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                                Toast.makeText(MarketDataActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MarketDataActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<OfferDataModel> call, Throwable t) {
                        try {
                            if (offerList.get(offerList.size() - 1) == null) {
                                offerList.remove(offerList.size() - 1);
                                offerAdapter.notifyItemRemoved(offerList.size() - 1);
                            }
                            offer_is_loading = false;
                            binding.progBarOffer.setVisibility(View.GONE);

                            if (t.getMessage() != null) {
                                Log.e("error_not_code", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(MarketDataActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MarketDataActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });


    }


    private void getMostSeller() {

        Api.getService(Tags.base_url)
                .getMostSeller("on", 20, 1, market.getId(), "best_seller")
                .enqueue(new Callback<MostSellerDataModel>() {
                    @Override
                    public void onResponse(Call<MostSellerDataModel> call, Response<MostSellerDataModel> response) {
                        binding.progBarMostSeller.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            mostSellerList.clear();
                            mostSellerList.addAll(response.body().getData().getProducts().getData());

                            if (mostSellerList.size() > 0) {
                                mostSellerAdapter.notifyDataSetChanged();
                                binding.flMostSeller.setVisibility(View.VISIBLE);
                            } else {
                                binding.flMostSeller.setVisibility(View.GONE);

                            }


                        } else {
                            binding.progBarMostSeller.setVisibility(View.GONE);

                            try {
                                Log.e("errorNotCode", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                                Toast.makeText(MarketDataActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MarketDataActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MostSellerDataModel> call, Throwable t) {
                        try {
                            binding.progBarMostSeller.setVisibility(View.GONE);

                            if (t.getMessage() != null) {
                                Log.e("error_not_code", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(MarketDataActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MarketDataActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });


    }


    private void loadMoreMostSeller(int page) {

        Api.getService(Tags.base_url)
                .getMostSeller("on", 20, page, market.getId(), "best_seller")
                .enqueue(new Callback<MostSellerDataModel>() {
                    @Override
                    public void onResponse(Call<MostSellerDataModel> call, Response<MostSellerDataModel> response) {

                        most_seller_is_loading = false;
                        mostSellerList.remove(mostSellerList.size() - 1);
                        mostSellerAdapter.notifyItemRemoved(mostSellerList.size() - 1);
                        binding.progBarMostSeller.setVisibility(View.GONE);

                        if (response.isSuccessful()) {


                            if (response.body().getData().getProducts().getData().size() > 0) {
                                int old_pos = mostSellerList.size() - 1;
                                most_seller_current_page = response.body().getData().getProducts().getCurrent_page();
                                mostSellerList.addAll(response.body().getData().getProducts().getData());
                                int new_pos = response.body().getData().getProducts().getData().size();
                                mostSellerAdapter.notifyItemRangeChanged(old_pos, new_pos);

                            }


                        } else {
                            most_seller_is_loading = false;
                            binding.progBarMostSeller.setVisibility(View.GONE);

                            try {
                                Log.e("errorNotCode", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                                Toast.makeText(MarketDataActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MarketDataActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MostSellerDataModel> call, Throwable t) {
                        try {
                            if (mostSellerList.get(mostSellerList.size() - 1) == null) {
                                mostSellerList.remove(mostSellerList.size() - 1);
                                mostSellerAdapter.notifyItemRemoved(mostSellerList.size() - 1);
                            }
                            most_seller_is_loading = false;
                            binding.progBarMostSeller.setVisibility(View.GONE);

                            if (t.getMessage() != null) {
                                Log.e("error_not_code", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(MarketDataActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MarketDataActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });


    }



    private void getMostUsed() {

        Api.getService(Tags.base_url)
                .getMostSeller("on", 20, 1, market.getId(), "most_used")
                .enqueue(new Callback<MostSellerDataModel>() {
                    @Override
                    public void onResponse(Call<MostSellerDataModel> call, Response<MostSellerDataModel> response) {
                        binding.progBarMostUsed.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            mostUsedList.clear();
                            mostUsedList.addAll(response.body().getData().getProducts().getData());

                            if (mostUsedList.size() > 0) {
                                mostUsedAdapter.notifyDataSetChanged();
                                binding.flMostUsed.setVisibility(View.VISIBLE);
                            } else {
                                binding.flMostUsed.setVisibility(View.GONE);

                            }


                        } else {
                            binding.progBarMostUsed.setVisibility(View.GONE);

                            try {
                                Log.e("errorNotCode", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                                Toast.makeText(MarketDataActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MarketDataActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MostSellerDataModel> call, Throwable t) {
                        try {
                            binding.progBarMostUsed.setVisibility(View.GONE);

                            if (t.getMessage() != null) {
                                Log.e("error_not_code", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(MarketDataActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MarketDataActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });


    }


    private void loadMoreMostUsed(int page) {

        Api.getService(Tags.base_url)
                .getMostSeller("on", 20, page, market.getId(), "most_used")
                .enqueue(new Callback<MostSellerDataModel>() {
                    @Override
                    public void onResponse(Call<MostSellerDataModel> call, Response<MostSellerDataModel> response) {

                        most_used_is_loading = false;
                        mostUsedList.remove(mostUsedList.size() - 1);
                        mostUsedAdapter.notifyItemRemoved(mostUsedList.size() - 1);
                        binding.progBarMostUsed.setVisibility(View.GONE);

                        if (response.isSuccessful()) {


                            if (response.body().getData().getProducts().getData().size() > 0) {
                                int old_pos = mostUsedList.size() - 1;
                                most_used_current_page = response.body().getData().getProducts().getCurrent_page();
                                mostUsedList.addAll(response.body().getData().getProducts().getData());
                                int new_pos = response.body().getData().getProducts().getData().size();
                                mostUsedAdapter.notifyItemRangeChanged(old_pos, new_pos);

                            }


                        } else {
                            most_used_is_loading = false;
                            binding.progBarMostUsed.setVisibility(View.GONE);

                            try {
                                Log.e("errorNotCode", response.code() + "__" + response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (response.code() == 500) {
                                Toast.makeText(MarketDataActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MarketDataActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MostSellerDataModel> call, Throwable t) {
                        try {
                            if (mostUsedList.get(mostUsedList.size() - 1) == null) {
                                mostUsedList.remove(mostUsedList.size() - 1);
                                mostUsedAdapter.notifyItemRemoved(mostUsedList.size() - 1);
                            }
                            most_used_is_loading = false;
                            binding.progBarMostUsed.setVisibility(View.GONE);

                            if (t.getMessage() != null) {
                                Log.e("error_not_code", t.getMessage() + "__");

                                if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                    Toast.makeText(MarketDataActivity.this, getString(R.string.something), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MarketDataActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage() + "__");
                        }
                    }
                });


    }


    @Override
    public void back() {
        finish();
    }

    public void setItemData(OfferModel offerModel) {
        Intent intent = new Intent(this, ProductDetailsActivity.class);
        intent.putExtra("name",market);
        intent.putExtra("data",offerModel);
        startActivity(intent);
    }

    public void addToCart(OfferModel offerModel) {
        if (market.getMarkter_status().equals("open")){
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
        }else {
            Common.CreateDialogAlert(this,getString(R.string.market_not_available));

        }

    }

    @Override
    public void main() {

    }

    @Override
    public void profile() {

    }

    @Override
    public void myOrder() {

    }

    @Override
    public void notification() {

    }

    @Override
    public void cart() {
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
    }

    @Override
    public void more() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        createOrderModel = preferences.getCartData(this);
        if (createOrderModel==null){
            binding.setCartCount(0);
        }else {
            binding.setCartCount(createOrderModel.getProducts().size());
        }
    }
}
