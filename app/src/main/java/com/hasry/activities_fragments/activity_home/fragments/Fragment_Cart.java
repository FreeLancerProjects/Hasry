package com.hasry.activities_fragments.activity_home.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hasry.R;
import com.hasry.activities_fragments.activity_home.HomeActivity;
import com.hasry.databinding.FragmentCartBinding;
import com.hasry.models.UserModel;
import com.hasry.preferences.Preferences;


import java.util.Locale;

import io.paperdb.Paper;

public class Fragment_Cart extends Fragment{

    private HomeActivity activity;
    private FragmentCartBinding binding;
    private Preferences preferences;
    private UserModel userModel;
    private String lang;
    private LinearLayoutManager manager;
    private double total = 0.0;

    public static Fragment_Cart newInstance() {
        return new Fragment_Cart();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cart, container, false);
        initView();
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void initView() {
        activity = (HomeActivity) getActivity();
        Paper.init(activity);
        lang = Paper.book().read("lang", "ar");
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(activity);

        manager = new LinearLayoutManager(activity);
        binding.recView.setLayoutManager(manager);

        binding.btnShop.setOnClickListener(view -> activity.displayFragmentMain());


        updateUI();

        binding.btnCheckout.setOnClickListener(view -> navigateToCheckoutActivity());

    }


    private void navigateToCheckoutActivity() {
        /*Intent intent = new Intent(activity, CheckoutActivity.class);
        intent.putExtra("total_cost",total);
        startActivityForResult(intent,100);*/
    }


    public void updateUI() {



        /*if (itemCartModelList.size()==0)
        {
            binding.btnCheckout.setVisibility(View.GONE);
            binding.llEmptyCart.setVisibility(View.VISIBLE);
            binding.llTotal.setVisibility(View.GONE);
        }else {
            binding.btnCheckout.setVisibility(View.VISIBLE);
            binding.llEmptyCart.setVisibility(View.GONE);
            binding.llTotal.setVisibility(View.VISIBLE);

        }*/
        calculateTotal();
    }

    public void calculateTotal()
    {
        total = 0;
       /* for (ItemCartModel model :itemCartModelList)
        {
            total += model.getCost()*model.getAmount();
        }*/

        binding.tvTotal.setText(String.format(Locale.ENGLISH,"%s %s",String.valueOf(total),getString(R.string.sar)));
    }






   /* public void increase_decrease(ItemCartModel model2, int adapterPosition) {
        itemCartModelList.set(adapterPosition,model2);
        calculateTotal();
    }*/


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100&&resultCode== Activity.RESULT_OK&&data!=null)
        {



        }
    }


}
