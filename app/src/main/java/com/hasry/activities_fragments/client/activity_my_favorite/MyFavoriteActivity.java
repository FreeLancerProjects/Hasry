package com.hasry.activities_fragments.client.activity_my_favorite;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hasry.R;
import com.hasry.databinding.ActivityMyFavoriteBinding;
import com.hasry.interfaces.Listeners;
import com.hasry.language.Language;
import com.hasry.models.UserModel;
import com.hasry.preferences.Preferences;

import java.util.Locale;

import io.paperdb.Paper;

public class MyFavoriteActivity extends AppCompatActivity implements Listeners.BackListener {
    private ActivityMyFavoriteBinding binding;
    private String lang;
    private boolean isLoading = false;
    private int current_page = 1;

    private LinearLayoutManager manager;
    private UserModel userModel;
    private Preferences preferences;
    private int selected_pos = -1;
    private boolean isFavoriteChange = false;
    private boolean isItemAdded = false;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_favorite);
        initView();
    }


    private void initView() {

        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);


        manager = new GridLayoutManager(this,1);
        binding.recView.setLayoutManager(manager);
        /*adapter = new MyFavoriteAdapter(this, productModelList, this);
        binding.recView.setAdapter(adapter);
        binding.recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    int total = binding.recView.getAdapter().getItemCount();

                    int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();


                    if (total > 6 && (total - lastVisibleItem) == 2 && !isLoading) {
                        isLoading = true;
                        int page = current_page + 1;
                        productModelList.add(null);
                        adapter.notifyDataSetChanged();
                        loadMore(page);
                    }
                }
            }
        });*/
        getData();
    }


    public void getData() {

       /* try {

            String token;
            if (userModel == null) {
                token = "";
            } else {
                token = userModel.getUser().getToken();
            }
            Api.getService(Tags.base_url)
                    .getMyFavoriteProducts(lang, token, "on", 1)
                    .enqueue(new Callback<ProductDataModel>() {
                        @Override
                        public void onResponse(Call<ProductDataModel> call, Response<ProductDataModel> response) {
                            binding.progBar.setVisibility(View.GONE);
                            if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                                productModelList.clear();
                                productModelList.addAll(response.body().getData());
                                if (productModelList.size() > 0) {
                                    adapter.notifyDataSetChanged();
                                    binding.tvNoData.setVisibility(View.GONE);
                                } else {
                                    binding.tvNoData.setVisibility(View.VISIBLE);

                                }
                            } else {
                                if (response.code() == 500) {
                                    Toast.makeText(MyFavoriteActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(MyFavoriteActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ProductDataModel> call, Throwable t) {
                            try {
                                binding.progBar.setVisibility(View.GONE);

                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(MyFavoriteActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MyFavoriteActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }*/
    }

    private void loadMore(int page) {
        /*try {
            String token;
            if (userModel == null) {
                token = "";
            } else {
                token = userModel.getUser().getToken();
            }

            Api.getService(Tags.base_url)
                    .getMyFavoriteProducts(lang, token, "on", page)
                    .enqueue(new Callback<ProductDataModel>() {
                        @Override
                        public void onResponse(Call<ProductDataModel> call, Response<ProductDataModel> response) {
                            isLoading = false;
                            productModelList.remove(productModelList.size() - 1);
                            adapter.notifyItemRemoved(productModelList.size() - 1);

                            if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                                productModelList.addAll(response.body().getData());
                                adapter.notifyDataSetChanged();
                                if (response.body().getData().size() > 0) {
                                    current_page = response.body().getMeta().getCurrent_page();

                                }
                            } else {
                                if (response.code() == 500) {
                                    Toast.makeText(MyFavoriteActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(MyFavoriteActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ProductDataModel> call, Throwable t) {
                            try {
                                if (productModelList.get(productModelList.size() - 1) == null) {
                                    isLoading = false;
                                    productModelList.remove(productModelList.size() - 1);
                                    adapter.notifyItemRemoved(productModelList.size() - 1);

                                }
                                binding.progBar.setVisibility(View.GONE);

                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(MyFavoriteActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MyFavoriteActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }*/
    }


   /* public void setItemData(ProductDataModel.Data model, int pos) {
        selected_pos = pos;
        Intent intent = new Intent(this, ProductDetailsActivity.class);
        intent.putExtra("data", model);
        startActivityForResult(intent, 100);
    }*/


/*
    public void dislike(ProductDataModel.Data productModel, int pos) {
        ProgressDialog dialog = Common.createProgressDialog(this, getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        try {
            Api.getService(Tags.base_url)
                    .addFavoriteProduct(lang, userModel.getUser().getToken(), "unfavourite", productModel.getId())
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            dialog.dismiss();
                            if (response.isSuccessful()) {
                                isFavoriteChange = true;
                                productModelList.remove(pos);
                                adapter.notifyItemRemoved(pos);

                                if (productModelList.size() > 0) {
                                    binding.tvNoData.setVisibility(View.GONE);
                                } else {
                                    binding.tvNoData.setVisibility(View.VISIBLE);

                                }

                            } else {

                                dialog.dismiss();


                                if (response.code() == 500) {
                                    Toast.makeText(MyFavoriteActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(MyFavoriteActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            try {
                                dialog.dismiss();

                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(MyFavoriteActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MyFavoriteActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }
    }
*/

    public void addItemToCart() {
        isItemAdded = true;


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            if (selected_pos != -1) {
                isFavoriteChange = true;
                /*productModelList.remove(selected_pos);
                adapter.notifyItemRemoved(selected_pos);

                if (productModelList.size() > 0) {
                    binding.tvNoData.setVisibility(View.GONE);
                } else {
                    binding.tvNoData.setVisibility(View.VISIBLE);

                }*/

                selected_pos = -1;
            }

        }
    }

    @Override
    public void back() {
        if (isFavoriteChange && isItemAdded) {
            Intent intent = getIntent();
            intent.putExtra("action", 1);
            setResult(RESULT_OK, intent);

        } else if (isFavoriteChange) {
            setResult(RESULT_OK);

        }else if (isItemAdded)
        {
            Intent intent = getIntent();
            intent.putExtra("action", 1);
            setResult(RESULT_OK, intent);
        }

        finish();
    }


    @Override
    public void onBackPressed() {
        back();
    }
}
