package com.hasry.services;


import com.hasry.models.MainCategoryDataModel;
import com.hasry.models.MarketDataModel;
import com.hasry.models.MostSellerDataModel;
import com.hasry.models.OfferDataModel;
import com.hasry.models.PlaceGeocodeData;
import com.hasry.models.PlaceMapDetailsData;
import com.hasry.models.UserModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Service {


    @GET("place/findplacefromtext/json")
    Call<PlaceMapDetailsData> searchOnMap(@Query(value = "inputtype") String inputtype,
                                          @Query(value = "input") String input,
                                          @Query(value = "fields") String fields,
                                          @Query(value = "language") String language,
                                          @Query(value = "key") String key
    );

    @GET("geocode/json")
    Call<PlaceGeocodeData> getGeoData(@Query(value = "latlng") String latlng,
                                      @Query(value = "language") String language,
                                      @Query(value = "key") String key);


    @FormUrlEncoded
    @POST("api/login")
    Call<UserModel> login(@Field("phone_code") String phone_code,
                          @Field("phone") String phone

    );


    @GET("api/main-category")
    Call<MainCategoryDataModel> getMainCategory();

    @GET("api/main-market-byCategoryId")
    Call<MarketDataModel> getMarkets(@Query("department_id") int department_id);

    @GET("api/main-offer-byMarketId")
    Call<OfferDataModel> getOffer(@Query("pagination_status") String pagination_status,
                                  @Query("per_link_") int per_link,
                                  @Query("page") int page,
                                  @Query("markter_id") int market_id
    );

    @GET("api/main-options-byMarketId")
    Call<MostSellerDataModel> getMostSeller(@Query("pagination_status") String pagination_status,
                                            @Query("per_link_") int per_link,
                                            @Query("page") int page,
                                            @Query("markter_id") int market_id,
                                            @Query("option") String option

    );

}