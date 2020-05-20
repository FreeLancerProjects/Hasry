package com.hasry.activities_fragments.client.activity_order;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import com.hasry.R;
import com.hasry.activities_fragments.client.activity_order.fragments.Fragment_Current_Order;
import com.hasry.activities_fragments.client.activity_order.fragments.Fragment_Previous_Order;
import com.hasry.adapters.ViewPagerOrderAdapter;
import com.hasry.databinding.ActivityOrderBinding;
import com.hasry.interfaces.Listeners;
import com.hasry.language.Language;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class OrderActivity extends AppCompatActivity implements Listeners.BackListener{
    private ActivityOrderBinding binding;
    private String lang;
    private ViewPagerOrderAdapter adapter;
    private List<Fragment> fragmentList;
    private List<String> title;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang","ar")));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order);
        initView();
    }




    private void initView()
    {
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);

        fragmentList = new ArrayList<>();
        title = new ArrayList<>();
        binding.tab.setupWithViewPager(binding.pager);
        binding.pager.setOffscreenPageLimit(2);

        fragmentList.add(Fragment_Current_Order.newInstance());
        fragmentList.add(Fragment_Previous_Order.newInstance());
        title.add(getString(R.string.current));
        title.add(getString(R.string.prevoius));
        adapter = new ViewPagerOrderAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragments(fragmentList);
        adapter.addTitles(title);
        binding.pager.setAdapter(adapter);




    }


    @Override
    public void back() {
        finish();
    }

}
