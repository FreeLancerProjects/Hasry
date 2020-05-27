package com.hasry.activities_fragments.client.activity_product_details;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;

import com.hasry.R;
import com.hasry.databinding.ActivityAboutAppBinding;
import com.hasry.databinding.ActivityProductDetailsBinding;
import com.hasry.interfaces.Listeners;
import com.hasry.language.Language;
import com.hasry.models.OfferModel;

import java.util.Locale;

import io.paperdb.Paper;

public class ProductDetailsActivity extends AppCompatActivity  implements Listeners.BackListener{
    private ActivityProductDetailsBinding binding;
    private String lang;
    private String marketName;
    private OfferModel offerModel;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang","ar")));
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
        if (intent!=null)
        {
            marketName = intent.getStringExtra("name");
            offerModel = (OfferModel) intent.getSerializableExtra("data");

        }
    }


    private void initView()
    {
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);

        binding.setMarketName(marketName);
        binding.setModel(offerModel);
    }




    @Override
    public void back() {
        finish();
    }

}
