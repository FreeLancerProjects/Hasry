package com.hasry.activities_fragments.activity_market_sign_up;

import androidx.appcompat.app.AppCompatActivity;

import android.nfc.Tag;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.hasry.R;
import com.hasry.tags.Tags;

public class MarketSignUpActivity extends AppCompatActivity {
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_sign_up);

        mWebView = findViewById(R.id.activity_main_webview);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.loadUrl(Tags.base_url+"admin/login");
    }
}
