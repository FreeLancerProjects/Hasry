package com.hasry.activities_fragments.client.activity_product_details;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hasry.Animate.CircleAnimationUtil;
import com.hasry.R;
import com.hasry.activities_fragments.client.activity_cart.CartActivity;
import com.hasry.databinding.ActivityAboutAppBinding;
import com.hasry.databinding.ActivityProductDetailsBinding;
import com.hasry.interfaces.Listeners;
import com.hasry.language.Language;
import com.hasry.models.CreateOrderModel;
import com.hasry.models.ItemCartModel;
import com.hasry.models.MarketDataModel;
import com.hasry.models.OfferModel;
import com.hasry.preferences.Preferences;
import com.hasry.share.Common;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.Locale;

import io.paperdb.Paper;

public class ProductDetailsActivity extends AppCompatActivity implements Listeners.BackListener, Listeners.HomeListener {
    private ActivityProductDetailsBinding binding;
    private String lang;
    private MarketDataModel.Data.Market market;
    private OfferModel offerModel;
    private CreateOrderModel createOrderModel;
    private Preferences preferences;
    private boolean isDataAdded = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_product_details);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            market = (MarketDataModel.Data.Market) intent.getSerializableExtra("name");
            offerModel = (OfferModel) intent.getSerializableExtra("data");

        }
    }


    private void initView() {
        Paper.init(this);
        preferences = Preferences.getInstance();
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.setAction(this);

        binding.setMarketName(market.getName());
        binding.setModel(offerModel);
        createOrderModel = preferences.getCartData(this);
        if (createOrderModel == null) {
            createOrderModel = new CreateOrderModel();
            createOrderModel.setMarkter_id(market.getId());
        } else {
            binding.setCartCount(createOrderModel.getProducts().size());
        }
        binding.flAddToCart.setOnClickListener(v -> addToCart(offerModel));

    }

    @Override
    public void main() {

    }

    public void addToCart(OfferModel offerModel) {
        if (market.getMarkter_status().equals("open")) {
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
               // binding.setCartCount(createOrderModel.getProducts().size());
                makeFlyAnimation(binding.image,createOrderModel.getProducts().size());
                isDataAdded = true;
                //Toast.makeText(this, getString(R.string.added_suc), Toast.LENGTH_SHORT).show();
            } else {
                Common.CreateDialogAlert(this, getString(R.string.diff_market));
            }
        } else {
            Common.CreateDialogAlert(this, getString(R.string.market_not_available));

        }

    }
    public void makeFlyAnimation(RoundedImageView targetView, int quantity) {


        new CircleAnimationUtil().attachActivity(this).setTargetView(targetView, lang).setMoveDuration(1000).setDestView(binding.flCart).setAnimationListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //     addItemToCart();
                binding.setCartCount(createOrderModel.getProducts().size());

                //  Toast.makeText(homeActivity, "Continue Shopping...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).startAnimation();


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
        startActivityForResult(intent,100);
    }

    @Override
    public void more() {

    }


    @Override
    public void back() {
        if (isDataAdded){
            setResult(RESULT_OK);
        }
        finish();
    }


    @Override
    public void onBackPressed() {
        back();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100&&resultCode==RESULT_OK){
            createOrderModel = preferences.getCartData(this);
            if (createOrderModel==null){
                createOrderModel = new CreateOrderModel();
                createOrderModel.setMarkter_id(market.getId());
                binding.setCartCount(0);
                isDataAdded = true;

            }
            else {

                binding.setCartCount(createOrderModel.getProducts().size());
            }
        }
    }
}
