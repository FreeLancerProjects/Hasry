package com.hasry.services;


import com.hasry.models.CityDataModel;
import com.hasry.models.CreateOrderModel;
import com.hasry.models.MainCategoryDataModel;
import com.hasry.models.MarketDataModel;
import com.hasry.models.MostSellerDataModel;
import com.hasry.models.NeighborHoodModel;
import com.hasry.models.NotificationDataModel;
import com.hasry.models.NotificationCount;
import com.hasry.models.NotificationDataModel;
import com.hasry.models.OfferDataModel;
import com.hasry.models.OrderDataModel;
import com.hasry.models.OrderModel;
import com.hasry.models.OrderResponseModel;
import com.hasry.models.PlaceGeocodeData;
import com.hasry.models.PlaceMapDetailsData;
import com.hasry.models.SearchDataModel;
import com.hasry.models.SettingModel;
import com.hasry.models.UserModel;

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
    Call<NeighborHoodModel> getNeigborhood();


    @FormUrlEncoded
    @POST("api/logout")
    Call<ResponseBody> logout(@Header("Authorization") String user_token,
                              @Field("user_token") String firebase_token


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
}