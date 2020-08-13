package com.hasryApp.services;


import com.hasryApp.models.CategoryDataModel;
import com.hasryApp.models.CityDataModel;
import com.hasryApp.models.CreateOrderModel;
import com.hasryApp.models.DepartmentPRoductDataModel;
import com.hasryApp.models.MainCategoryDataModel;
import com.hasryApp.models.MarketDataModel;
import com.hasryApp.models.MostSellerDataModel;
import com.hasryApp.models.NeighborHoodModel;
import com.hasryApp.models.NotificationDataModel;
import com.hasryApp.models.NotificationCount;
import com.hasryApp.models.OfferDataModel;
import com.hasryApp.models.OrderDataModel;
import com.hasryApp.models.OrderResponseModel;
import com.hasryApp.models.PlaceDirectionModel;
import com.hasryApp.models.PlaceGeocodeData;
import com.hasryApp.models.PlaceMapDetailsData;
import com.hasryApp.models.SearchDataModel;
import com.hasryApp.models.SettingModel;
import com.hasryApp.models.UserModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Part;
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


    @FormUrlEncoded
    @POST("api/register")
    Call<UserModel> signUpWithoutImage(@Field("name") String name,
                                       @Field("email") String email,
                                       @Field("phone_code") String phone_code,
                                       @Field("phone") String phone,
                                       @Field("city") String city,
                                       @Field("user_type") String user_type,
                                       @Field("software_type") String software_type


    );

    @Multipart
    @POST("api/register")
    Call<UserModel> signUpWithImage(@Part("name") RequestBody name,
                                    @Part("email") RequestBody email,
                                    @Part("phone_code") RequestBody phone_code,
                                    @Part("phone") RequestBody phone,
                                    @Part("city") RequestBody city,
                                    @Part("user_type") RequestBody user_type,
                                    @Part("software_type") RequestBody software_type,
                                    @Part MultipartBody.Part image
    );


    @Multipart
    @POST("api/register")
    Call<UserModel> registerDelegatewithimage(@Part("name") RequestBody name,
                                              @Part("email") RequestBody email,
                                              @Part("phone_code") RequestBody phone_code,
                                              @Part("phone") RequestBody phone,
                                              @Part("city") RequestBody city,
                                              @Part("district") RequestBody district,
                                              @Part("user_type") RequestBody user_type,
                                              @Part("software_type") RequestBody software_type,
                                              @Part MultipartBody.Part logo,
                                              @Part MultipartBody.Part car_image,
                                              @Part MultipartBody.Part car_documentation_image,
                                              @Part MultipartBody.Part drive_documentation_image

    );

    @Multipart
    @POST("api/register")
    Call<UserModel> registerDelegatewithoutimage(@Part("name") RequestBody name,
                                                 @Part("email") RequestBody email,
                                                 @Part("phone_code") RequestBody phone_code,
                                                 @Part("phone") RequestBody phone,
                                                 @Part("city") RequestBody city,
                                                 @Part("district") RequestBody district,
                                                 @Part("user_type") RequestBody user_type,
                                                 @Part("software_type") RequestBody software_type,
                                                 @Part MultipartBody.Part car_image,
                                                 @Part MultipartBody.Part car_documentation_image,
                                                 @Part MultipartBody.Part drive_documentation_image

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


    @GET("api/orders")
    Call<OrderDataModel> getOrders(@Header("Authorization") String user_token,
                                   @Query("order_status") String order_status,
                                   @Query("client_or_driver_id") int client_or_driver_id,
                                   @Query("user_type") String user_type

    );


    @GET("api/orders")
    Call<OrderDataModel> getDriverOrders(@Header("Authorization") String user_token,
                                         @Query("order_status") String order_status,
                                         @Query("client_or_driver_id") int client_or_driver_id,
                                         @Query("user_type") String user_type,
                                         @Query("page") int page,
                                         @Query("latituide") String latituide,
                                         @Query("longituide") String longituide

    );

    @GET("api/Get-Notifications")
    Call<NotificationDataModel> getNotification(
            @Query("client_or_driver_id") int client_or_driver_id
            , @Header("Authorization") String user_token


    );

    @GET("api/order-details")
    Call<OrderDataModel> getOrderDetials(@Query("order_id") String order_id
            , @Header("Authorization") String user_token


    );

    @GET("api/show-setting")
    Call<SettingModel> getSetting();

    @GET("api/cities")
    Call<CityDataModel> getCity();

    @GET("api/neighborhoods")
    Call<NeighborHoodModel> getNeigborhood(@Query("city_name") String city_name);


    @FormUrlEncoded
    @POST("api/logout")
    Call<ResponseBody> logout(@Header("Authorization") String user_token,
                              @Field("token") String firebase_token


    );

    @POST("api/send-order")
    Call<OrderResponseModel> createOrder(@Header("Authorization") String user_token,
                                         @Body CreateOrderModel model
    );


    @GET("api/search-product")
    Call<SearchDataModel> search(@Query("key") String query
    );

    @FormUrlEncoded
    @POST("api/notification-is-read")
    Call<ResponseBody> readNotification(@Header("Authorization") String user_token,
                                        @Field("user_id") int user_id
    );


    @FormUrlEncoded
    @POST("api/notification-count")
    Call<NotificationCount> getUnreadNotificationCount(@Header("Authorization") String user_token,
                                                       @Field("user_id") int user_id
    );


    @GET("api/Get-Notifications")
    Call<NotificationDataModel> getNotification(@Header("Authorization") String user_token,
                                                @Query("client_or_driver_id") int client_or_driver_id
    );


    @FormUrlEncoded
    @POST("api/delete_notifications")
    Call<ResponseBody> deleteNotification(@Header("Authorization") String user_token,
                                          @Field("id") int id,
                                          @Field("user_id") int user_id
    );

    @FormUrlEncoded
    @POST("api/Driver-action-Order")
    Call<ResponseBody> DriverAcceptRefuse(
            @Header("Authorization") String user_token,
            @Field("notification_id") String notification_id,
            @Field("type") String type

    );

    @FormUrlEncoded
    @POST("api/Driver-delivery-Order")
    Call<ResponseBody> Driverdeliverorder(
            @Header("Authorization") String user_token,
            @Field("order_id") String notification_id

    );

    @FormUrlEncoded
    @POST("api/Driver-end-Order")
    Call<ResponseBody> DriverEndOrder(
            @Header("Authorization") String user_token,
            @Field("order_id") String notification_id,
            @Field("type") String type

    );


    @FormUrlEncoded
    @POST("api/contact")
    Call<ResponseBody> sendContact(@Field("name") String name,
                                   @Field("email") String email,
                                   @Field("subject") String subject,
                                   @Field("message") String message

    );

    @FormUrlEncoded
    @POST("api/firebase-tokens")
    Call<ResponseBody> updatePhoneToken(@Header("Authorization") String user_token,
                                        @Field("phone_token") String phone_token,
                                        @Field("user_id") int user_id,
                                        @Field("software_type") int software_type
    );

    @GET("directions/json")
    Call<PlaceDirectionModel> getDirection(@Query("origin") String origin,
                                           @Query("destination") String destination,
                                           @Query("transit_mode") String transit_mode,
                                           @Query("key") String key
    );

    @GET("api/market-dep")
    Call<CategoryDataModel> getAllCategories(
            @Query("pagination_status") String pagination_status,
            @Query("markter_id") String market_id);

    @GET("api/prouducts-by-market-dep")
    Call<DepartmentPRoductDataModel> getAds(
            @Query("pagination_status") String pagination_status,
            @Query("dep_id") String dep_id);

    @FormUrlEncoded
    @POST("api/update-notification-status")
    Call<UserModel> UpdatereadNotificationStaus(
            @Field("notification_status") String notification_status,
            @Field("user_id") int user_id
    );

    @Multipart
    @POST("api/update-profile")
    Call<UserModel> editClientProfileWithImage(
            @Part("user_id") RequestBody user_id,
            @Header("Authorization") String Authorization,
            @Part MultipartBody.Part logo

    );

    @FormUrlEncoded
    @POST("api/user_profile_update")
    Call<UserModel> editprofile(
            @Header("Authorization") String Authorization,
            @Field("name") String name,
            @Field("email") String email,
            @Part("city") String city,
            @Part("district") String district,
            @Field("user_id") String user_id
    );
    @FormUrlEncoded
    @POST("api/user_profile_update")
    Call<UserModel> editprofile(
            @Header("Authorization") String Authorization,
            @Field("name") String name,
            @Field("email") String email,
            @Part("city") String city,
            @Field("user_id") String user_id
    );
}