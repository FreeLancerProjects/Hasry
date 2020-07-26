package com.hasry.activities_fragments.client.activity_notification;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hasry.R;
import com.hasry.activities_fragments.client.activity_home.HomeActivity;
import com.hasry.adapters.NotificationAdapter;
import com.hasry.databinding.ActivityNotificationBinding;
import com.hasry.databinding.DialogAlertBinding;
import com.hasry.databinding.DialogImageBinding;
import com.hasry.interfaces.Listeners;
import com.hasry.language.Language;
import com.hasry.models.NotificationDataModel;
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
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity implements Listeners.BackListener{
    private ActivityNotificationBinding binding;
    private String lang;
    private List<NotificationDataModel.NotificationModel> notificationModelList;
    private NotificationAdapter adapter;
    private Preferences preferences;
    private UserModel userModel;
    private int current_page=1;
    private boolean isLoading=false;
    private boolean isFromFirebase = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang","ar")));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra("not")){
            isFromFirebase = true;
        }
    }


    private void initView()
    {
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        notificationModelList = new ArrayList<>();

        binding.recView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter(notificationModelList,this);
        binding.recView.setAdapter(adapter);


       /* binding.recView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy>0)
                {
                    int total_item = binding.recView.getAdapter().getItemCount();
                    int last_visible_item = ((LinearLayoutManager)binding.recView.getLayoutManager()).findLastCompletelyVisibleItemPosition();

                    if (total_item>=20&&(total_item-last_visible_item)==5&&!isLoading)
                    {

                        isLoading = true;
                        int page = current_page+1;
                        notificationModelList.add(null);
                        adapter.notifyItemInserted(notificationModelList.size()-1);

                        loadMore(page);
                    }
                }
            }
        });*/
        getNotification();

    }

    private void getNotification()
    {
        try {
            Api.getService(Tags.base_url)
                    .getNotification("Bearer "+userModel.getData().getToken(),userModel.getData().getId())
                    .enqueue(new Callback<NotificationDataModel>() {
                        @Override
                        public void onResponse(Call<NotificationDataModel> call, Response<NotificationDataModel> response) {
                            binding.progBar.setVisibility(View.GONE);
                            if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                                notificationModelList.clear();
                                notificationModelList.addAll(response.body().getData());
                                if (notificationModelList.size() > 0) {

                                    adapter.notifyDataSetChanged();

                                    binding.tvNoData.setVisibility(View.GONE);
                                } else {
                                    binding.tvNoData.setVisibility(View.VISIBLE);

                                }
                            } else {
                                if (response.code() == 500) {
                                    Toast.makeText(NotificationActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(NotificationActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<NotificationDataModel> call, Throwable t) {
                            try {
                                binding.progBar.setVisibility(View.GONE);

                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(NotificationActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(NotificationActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }
    }

    private void loadMore(int page)
    {
        /*try {

            Api.getService(Tags.base_url)
                    .getNotification(userModel.getUser().getToken(),page,"on",20)
                    .enqueue(new Callback<NotificationDataModel>() {
                        @Override
                        public void onResponse(Call<NotificationDataModel> call, Response<NotificationDataModel> response) {
                            isLoading = false;
                            notificationModelList.remove(notificationModelList.size() - 1);
                            adapter.notifyItemRemoved(notificationModelList.size() - 1);


                            if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {

                                int oldPos = notificationModelList.size()-1;

                                notificationModelList.addAll(response.body().getData());

                                if (response.body().getData().size() > 0) {
                                    current_page = response.body().getMeta().getCurrent_page();
                                    adapter.notifyItemRangeChanged(oldPos,notificationModelList.size()-1);

                                }
                            } else {
                                if (response.code() == 500) {
                                    Toast.makeText(NotificationActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(NotificationActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

                                    try {

                                        Log.e("error", response.code() + "_" + response.errorBody().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<NotificationDataModel> call, Throwable t) {
                            try {

                                if (notificationModelList.get(notificationModelList.size() - 1) == null) {
                                    isLoading = false;
                                    notificationModelList.remove(notificationModelList.size() - 1);
                                    adapter.notifyItemRemoved(notificationModelList.size() - 1);

                                }

                                if (t.getMessage() != null) {
                                    Log.e("error", t.getMessage());
                                    if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                                        Toast.makeText(NotificationActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(NotificationActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }*/
    }

    public void setItemData(NotificationDataModel.NotificationModel notificationModel, int adapterPosition) {


        try {
            ProgressDialog dialog = Common.createProgressDialog(this,getString(R.string.wait));
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            Api.getService(Tags.base_url)
                    .deleteNotification("Bearer "+userModel.getData().getToken(),notificationModel.getId(),userModel.getData().getId())
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            dialog.dismiss();
                            if (response.isSuccessful()) {
                                notificationModelList.remove(adapterPosition);
                                adapter.notifyItemRemoved(adapterPosition);

                                if (notificationModelList.size() > 0) {

                                    binding.tvNoData.setVisibility(View.GONE);
                                } else {
                                    binding.tvNoData.setVisibility(View.VISIBLE);

                                }
                            } else {
                                dialog.dismiss();
                                if (response.code() == 500) {
                                    Toast.makeText(NotificationActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                                } else {
                                    Toast.makeText(NotificationActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();

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
                                        Toast.makeText(NotificationActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(NotificationActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (Exception e) {
                            }
                        }
                    });
        } catch (Exception e) {

        }
    }

    @Override
    public void back() {
        if (isFromFirebase){
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
        finish();
    }


    public void CreateDialogimage(Context context, int pos) {
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .create();

        DialogImageBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_image, null, false);



       binding.setModel(notificationModelList.get(pos));
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_congratulation_animation;
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_window_bg);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setView(binding.getRoot());
        dialog.show();
    }

}
