package com.hasry.activities_fragments.client.activity_client_profile;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.hasry.R;
import com.hasry.databinding.ActivityClientProfileBinding;
import com.hasry.databinding.ActivityProfileBinding;
import com.hasry.interfaces.Listeners;
import com.hasry.language.Language;
import com.hasry.models.UserModel;
import com.hasry.preferences.Preferences;

import java.util.Locale;
import java.util.Random;

import io.paperdb.Paper;

public class ClientProfileActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityClientProfileBinding binding;
    private String lang;
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


}
