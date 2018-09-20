package com.pointters.rest;


import android.support.v4.content.ContextCompat;

import com.braintreepayments.api.models.ClientToken;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.google.gson.internal.LinkedTreeMap;
import com.pointters.model.ClientTokenModel;
import com.pointters.model.CustomOfferModels;
import com.pointters.model.ExploreJobsModel;
import com.pointters.model.GetserviceSearch;
import com.pointters.model.InviteSuggestedUserModel;
import com.pointters.model.LocationModel;
import com.pointters.model.Media;
import com.pointters.model.OrderModel;
import com.pointters.model.OrdersDetailModel;
import com.pointters.model.PaymentMethod;
import com.pointters.model.PaymentMethodNonceString;
import com.pointters.model.PaymentMethodResponse;
import com.pointters.model.ResetPasswordModel;
import com.pointters.model.ReviewOrderModel;
import com.pointters.model.SearchModel;
import com.pointters.model.Service;
import com.pointters.model.StoreLocationModel;
import com.pointters.model.TagServiceSellerModel;
import com.pointters.model.UserSettingsModel;
import com.pointters.model.request.AddServiceRequest;
import com.pointters.model.request.CancelOrderRequest;
import com.pointters.model.request.CommentRequest;
import com.pointters.model.request.DateModel;
import com.pointters.model.request.FlagInappropriateRequest;
import com.pointters.model.request.PostUpdateRequest;
import com.pointters.model.request.SendCustomOfferRequest;
import com.pointters.model.request.SendCustomOfferRequest1;
import com.pointters.model.request.SendServicesRequest;
import com.pointters.model.request.StoreLocationRequest;
import com.pointters.model.request.UserEmailLoginRequest;
import com.pointters.model.request.UserEmailSignUpRequest;
import com.pointters.model.request.UserFacebookLoginRequest;
import com.pointters.model.request.UserPutRequest;
import com.pointters.model.request.UserPutSettingsRequest;
import com.pointters.model.request.UserRegisterPutRequest;
import com.pointters.model.response.BaseResponse;
import com.pointters.model.response.CommentsResponse;
import com.pointters.model.response.FollowUnfollowResponse;
import com.pointters.model.response.GetBuyOrderResponse;
import com.pointters.model.response.GetCategoryResponse;
import com.pointters.model.response.GetChatHistoryResponse;
import com.pointters.model.response.GetConversationsResponse;
import com.pointters.model.response.CurrentUpdateModel;
import com.pointters.model.response.GetCustomOfferDetailsResponse;
import com.pointters.model.response.GetFollowersResponse;
import com.pointters.model.response.GetFollowingResponse;
import com.pointters.model.response.GetJobsExploreReponse;
import com.pointters.model.response.GetLiveOfferRequestsResponse;
import com.pointters.model.response.GetLiveOfferResponse;
import com.pointters.model.response.GetNotificationsResponse;
import com.pointters.model.response.GetReceivedOffersResponse;
import com.pointters.model.response.GetRelatedServicesResponse;
import com.pointters.model.response.GetSearchResponsel;
import com.pointters.model.response.GetSellJobsResponse;
import com.pointters.model.response.GetSellOrdersResponse;
import com.pointters.model.response.GetSellerEligibilityResponse;
import com.pointters.model.response.GetSendServicesResponse;
import com.pointters.model.response.GetSentOfferResponse;
import com.pointters.model.response.GetServiceByIdResponse;
import com.pointters.model.response.GetServiceDetailResponse;
import com.pointters.model.response.GetServiceInfoResponse;
import com.pointters.model.response.GetServicesExploreReponse;
import com.pointters.model.response.GetServicesReponse;
import com.pointters.model.response.GetStoreLocationResponse;
import com.pointters.model.response.GetTagServiceSellerResponse;
import com.pointters.model.response.GetTransactionHistoryResponse;
import com.pointters.model.response.GetUserMenuResponse;
import com.pointters.model.response.GetUserProfileResponse;
import com.pointters.model.response.LikeUnlikeResponse;
import com.pointters.model.response.LikesResponse;
import com.pointters.model.response.ResponsePutUser;
import com.pointters.model.response.SearchConversationsResponse;
import com.pointters.model.response.SearchSearviceResponse;
import com.pointters.model.response.SearchSendServicesResponse;
import com.pointters.model.response.ShareUnshareResponse;
import com.pointters.model.response.SharesResponse;
import com.pointters.model.response.UserEmailLoginResponse;
import com.pointters.model.response.UserEmailSignUpResponse;
import com.pointters.model.response.UserFacebookLoginResponse;
import com.pointters.model.response.WatchUnwatchResponse;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;


/**
 * Created by Anil Jha on 08-Aug-16.
 */

public interface ApiInterface {

    //********************************************************//
    //                     General APIs                       //
    //********************************************************//

    @POST("user/signup")
    Call<UserEmailSignUpResponse>
    userSignUpViaEmail(@Body UserEmailSignUpRequest params);

    @POST("user/login")
    Call<UserEmailLoginResponse>
    userLoginViaEmail(@Body UserEmailLoginRequest params);

    @POST("user/otp")
    Call<Void>
    sendOtp(@Body UserEmailLoginRequest params);

    @POST("user/reset/password")
    Call<UserEmailLoginResponse>
    resetPassword(@Body ResetPasswordModel params);

    @POST("user/facebook/token")
    Call<UserFacebookLoginResponse>
    userLoginViaFacebook(@Body UserFacebookLoginRequest params);

    @GET("user")
    Call<Object>
    getUserInformation(@Header("Authorization") String token);

    @GET("user/seller-eligibility")
    Call<GetSellerEligibilityResponse>
    getUserSellerEleigibility(@Header("Authorization") String token);

    @PUT("user")
    Call<ResponsePutUser>
    putUser(@Header("Authorization") String token,
            @Body UserPutRequest params);

    @PUT("user")
    Call<ResponsePutUser>
    putRegisterUser(@Header("Authorization") String token,
            @Body UserRegisterPutRequest params);


    @POST("user/logout")
    Call<Object>
    logOut(@Header("Authorization") String token);

    @PUT("user/setting")
    Call<Object>
    putUserSettings(@Header("Authorization") String token,
                    @Body UserPutSettingsRequest params);

    @GET("user/setting")
    Call<UserPutSettingsRequest>
    getUserSettings(@Header("Authorization") String token);


    @POST("service")
    Call<Service>
    postService(@Header("Authorization") String token,
                @Body AddServiceRequest params);

    @POST("post")
    Call<Object>
    postUpdate(@Header("Authorization") String token,
               @Body PostUpdateRequest params);

    @POST("send-service")
    Call<Object>
    sendService(@Header("Authorization") String token,
                @Body SendServicesRequest params);


    @GET("categories")
    Call<GetCategoryResponse>
    getCategories();

    //==================================================================================//
    //                              Store Locations                                     //
    //==================================================================================//
    @GET("stores")
    Call<GetStoreLocationResponse>
    getStores(@Header("Authorization") String token);

    @GET("store/{id}")
    Call<StoreLocationModel>
    getStoreDetail(@Header("Authorization") String token,
                   @Path("id") String id);

    @PUT("store/{id}")
    Call<StoreLocationModel>
    putStoreLocation(@Header("Authorization") String token,
                   @Path("id") String id,
                     @Body StoreLocationRequest request);

    @POST("store")
    Call<StoreLocationModel>
    postStore(@Header("Authorization") String token,
               @Body StoreLocationRequest id);

    @DELETE("store/{id}")
    Call<BaseResponse>
    deleteStore(@Header("Authorization") String token,
                @Path("id") String id);

    //==================================================================================//
    //                              Store Locations                                     //
    //==================================================================================//
    //==================================================================================//
    //                              Shipping Addresses                                  //
    //==================================================================================//
    @GET("shipment-addresses")
    Call<GetStoreLocationResponse>
    getShipmentAddresses(@Header("Authorization") String token);

    @GET("shipment-address/{id}")
    Call<StoreLocationModel>
    getShipmentAddressesDetail(@Header("Authorization") String token,
                               @Path("id") String id);

    @GET("shipment/{id}/rate")
    Call<JSONObject>
    getShipmentRate(@Header("Authorization") String token,
                               @Path("id") String id);

    @PUT("shipment-address/{id}")
    Call<StoreLocationModel>
    putShipmentAddresses(@Header("Authorization") String token,
                     @Path("id") String id,
                     @Body StoreLocationRequest request);

    @POST("shipment-address")
    Call<StoreLocationModel>
    postShipmentAddresses(@Header("Authorization") String token,
              @Body StoreLocationRequest id);

    @DELETE("shipment-address/{id}")
    Call<BaseResponse>
    deleteShipmentAddresses(@Header("Authorization") String token,
                @Path("id") String id);
    //==================================================================================//
    //                              Shipping Addresses                                  //
    //==================================================================================//

    //==================================================================================//
    //                              Notifications                                       //
    //==================================================================================//
    @GET("notifications")
    Call<GetNotificationsResponse>
    getNotifications(@Header("Authorization") String token,
                               @Query("lt_id") String lt_id);

    @PUT("notification/{id}/read")
    Call<BaseResponse>
    putNotificationRead(@Header("Authorization") String token,
                        @Path("id") String id);
    //==================================================================================//
    //                              Notifications                                       //
    //==================================================================================//

    //==================================================================================//
    //                              PostUpdate                                          //
    //==================================================================================//

    @GET("posts")
    Call<ArrayList<CurrentUpdateModel>>
    getCurrentUpdate(@Header("Authorization") String token,
                     @QueryMap Map<String, String> params);


    @GET("post/{id}/likes")
    Call<LikesResponse>
    getPostLikes(@Header("Authorization") String token,
                 @Path("id") String id,
                 @Query("lt_id") String lt_id);


    @GET("post/{id}/comments")
    Call<CommentsResponse>
    getPostComments(@Header("Authorization") String token,
                    @Path("id") String id,
                    @Query("lt_id") String lt_id);


    @GET("post/{id}/shares")
    Call<SharesResponse>
    getPostShares(@Header("Authorization") String token,
                  @Path("id") String id,
                  @Query("lt_id") String lt_id);

    @POST("post/{id}/shares")
    Call<BaseResponse>
    postShare(@Header("Authorization") String token,
              @Path("id") String id);

    @POST("post/{id}/like")
    Call<BaseResponse>
    postLike(@Header("Authorization") String token,
             @Path("id") String id);

    @DELETE("post/{id}/like")
    Call<BaseResponse>
    postUnlike(@Header("Authorization") String token,
             @Path("id") String id);

    @POST("post/{id}/comment")
    Call<BaseResponse>
    postComment(@Header("Authorization") String token,
             @Path("id") String id,
             @Body CommentRequest comment);


    //==================================================================================//
    //                              PostUpdate                                          //
    //==================================================================================//

    //==================================================================================//
    //                              Transaction History                                 //
    //==================================================================================//
    @GET("transaction-history")
    Call<GetTransactionHistoryResponse>
    getTransactionHisotry(@Header("Authorization") String token,
                          @QueryMap Map<String, String> params);

    //==================================================================================//
    //                              Transaction History                                 //
    //==================================================================================//
    //==================================================================================//
    //                              FulFillment                                 //
    //==================================================================================//

    @GET("order/{id}")
    Call<OrderModel>
    getOrderDetail(@Header("Authorization") String token,
                   @Path("id") String id);
    @PUT("order/{id}/request-location-change")
    Call<BaseResponse>
    putBuyerLocationChange(@Header("Authorization") String token,
                   @Path("id") String id,
                   @Body LocationModel buyerServiceLocation);
    @PUT("order/{id}/accept-location-change")
    Call<BaseResponse>
    putSellerAcceptLocationChange(@Header("Authorization") String token,
                                  @Path("id") String id);
    @PUT("order/{id}/request-schedule-change")
    Call<BaseResponse>
    putBuyerRequestScheduleChange(@Header("Authorization") String token,
                                  @Path("id") String id,
                                  @Body DateModel serviceScheduleDate);

    @PUT("order/{id}/accept-schedule-change")
    Call<BaseResponse>
    putSellerAcceptScheduleChange(@Header("Authorization") String token,
                                  @Path("id") String id);

    @PUT("order/{id}/request-cancel-order")
    Call<BaseResponse>
    putBuyerRequestCancelOrder(@Header("Authorization") String token,
                               @Path("id") String id,
                               @Body CancelOrderRequest request);

    @PUT("order/{id}/accept-cancel-order")
    Call<BaseResponse>
    putSellerAcceptCancelOrder(@Header("Authorization") String token,
                               @Path("id") String id);

    @PUT("order/{id}/start-service")
    Call<BaseResponse>
    putSellerStartService(@Header("Authorization") String token,
                          @Path("id") String id);

    @PUT("order/{id}/completed-service")
    Call<BaseResponse>
    putSellerCompletedtService(@Header("Authorization") String token,
                               @Path("id") String id);

    @PUT("order/{id}/seller-delivered-media")
    Call<BaseResponse>
    putSellerDeliveredMedia(@Header("Authorization") String token,
                            @Path("id") String id,
                            @Body ArrayList<Media> media);
    @PUT("order/{id}/completed-service")
    Call<BaseResponse>
    putBuyerAcceptCompletedService(@Header("Authorization") String token,
                                   @Path("id") String id);

    @GET("service/review/{id}")
    Call<BaseResponse>
    getBuyerReviewOrderAfterAcceptance(@Header("Authorization") String token,
                                       @Path("id") String id);

    @POST("service/{id}/review")
    Call<BaseResponse>
    postBuyerReviewOrder(@Header("Authorization") String token,
                                       @Path("id") String id,
                         @Body ReviewOrderModel request);


    //==================================================================================//
    //                              FulFillment                                 //
    //==================================================================================//

    //==================================================================================//
    //                              BackgroundCheck                                 //
    //==================================================================================//
    @POST("background-check")
    Call<BaseResponse>
    postBackgroundCheck(@Header("Authorization") String token);

    @PUT("background-check/{id}")
    Call<BaseResponse>
    putBackgroundCheck(@Header("Authorization") String token,
                       @Path("id") String id);

    @GET("background-check/{id}")
    Call<BaseResponse>
    getBackgroundCheck(@Header("Authorization") String token,
                       @Path("id") String id);

    @POST("background-check/pay")
    Call<BaseResponse>
    postBackgroundCheckPay(@Header("Authorization") String token);

    //==================================================================================//
    //                              BackgroundCheck                                 //
    //==================================================================================//

    //==================================================================================//
    //                              PaymentMethod                                 //
    //==================================================================================//

    @GET("braintree/client-token")
    Call<ClientTokenModel>
    getBrainTreeClientToken(@Header("Authorization") String token);

    @POST("braintree/payment-method")
    Call<PaymentMethodResponse>
    postBrainTreePaymentMethod(@Header("Authorization") String token,
                               @Body PaymentMethodNonceString paymentMethodNonce);

    //==================================================================================//
    //                              PaymentMethod                                 //
    //==================================================================================//


    //==================================================================================//
    //                              Search                                 //
    //==================================================================================//
    @GET("search-page")
    Call<GetSearchResponsel>
    getSearchPage(@Header("Authorization") String token);

    @POST("search")
    Call<ArrayList<SearchSearviceResponse>>
    postSearch(@Header("Authorization") String token,
               @Body SearchModel request);


    //==================================================================================//
    //                              Search                                 //
    //==================================================================================//

    //==================================================================================//
    //                              Hompage                                                                                          //
    //==================================================================================//
    @GET("homepage/mobile")
    Call<LinkedTreeMap>
    getHomepage();

    //==================================================================================//
    //                              Hompage                                                                                          //
    //==================================================================================//
    @GET("users/invite-suggested")
    Call<ArrayList<InviteSuggestedUserModel>>
    getInviteSuggested(@Header("Authorization") String token);



    @GET("user/{id}/follow")
    Call<FollowUnfollowResponse>
    getUserFollowStatus(@Header("Authorization") String token,
                        @Path("id") String id);

    @POST("user/{id}/follow")
    Call<Object>
    followUser(@Header("Authorization") String token,
               @Path("id") String id);

    @GET("user/followers")
    Call<GetFollowersResponse>
    userFollowers(@Header("Authorization") String token,
                  @Query("lt_id") String id);

    @GET("user/following")
    Call<GetFollowingResponse>
    userFollowing(@Header("Authorization") String token,
                  @Query("lt_id") String id);

    @DELETE("user/{id}/follow")
    Call<Object>
    unFollowUser(@Header("Authorization") String token,
                 @Path("id") String id);

    @GET("user/profile")
    Call<GetUserProfileResponse>
    getUserProfile(@Header("Authorization") String token,
                   @Query("userId") String id);

    @GET("services")
    Call<GetServicesReponse>
    getUserServices(@Header("Authorization") String token,
                    @QueryMap Map<String, String> params);

    @GET("services/search")
    Call<List<GetserviceSearch>>
    getServiceSearch(@Header("Authorization") String token,
                       @QueryMap Map<String, String> params);

    @GET("services/live-offer-suggested")
    Call<GetLiveOfferResponse>
    getLiveOffer(@Header("Authorization") String token,
                       @QueryMap Map<String, String> params);

    @GET("explore/jobs")
    Call<GetJobsExploreReponse>
    getJobsExplore(@Header("Authorization") String token,
                   @QueryMap Map<String, String> params);


    @GET("user/menu")
    Call<GetUserMenuResponse>
    getUserMenu(@Header("Authorization") String token);

    @GET("services/explore")
    Call<GetServicesExploreReponse>
    getServicesExplore(@Header("Authorization") String token,
                       @QueryMap Map<String, String> params);

    // ==============================================================================================

    @POST("request/{id}/offer")
    Call<GetCustomOfferDetailsResponse>
    sendCustomOffers(@Header("Authorization") String token,
                     @Path("id") String id,
                     @Body SendCustomOfferRequest params);

    @POST("request/{id}/offer")
    Call<GetCustomOfferDetailsResponse>
    sendCustomOffers1(@Header("Authorization") String token,
                     @Path("id") String id,
                     @Body SendCustomOfferRequest1 params);

    @POST("offer")
    Call<GetCustomOfferDetailsResponse>
    sendCustomOffers2(@Header("Authorization") String token,
                     @Body SendCustomOfferRequest params);

    @POST("offer")
    Call<GetCustomOfferDetailsResponse>
    sendCustomOffers3(@Header("Authorization") String token,
                      @Body SendCustomOfferRequest1 params);

    @PUT("request/offer/{id}")
    Call<ResponseBody>
    updateCustomOffers(@Header("Authorization") String token,
                       @Path("id") String id,
                       @Body SendCustomOfferRequest params);

    @PUT("offer/{id}")
    Call<ResponseBody>
    updateCustomOffers2(@Header("Authorization") String token,
                       @Path("id") String id,
                       @Body SendCustomOfferRequest1 params);

    @PUT("offer/{id}")
    Call<ResponseBody>
    updateCustomOffers3(@Header("Authorization") String token,
                       @Path("id") String id,
                       @Body SendCustomOfferRequest params);

    @PUT("request/offer/{id}")
    Call<ResponseBody>
    updateCustomOffers1(@Header("Authorization") String token,
                        @Path("id") String id,
                        @Body SendCustomOfferRequest1 params);

    @GET("request/offer/{id}")
    Call<CustomOfferModels>
    getCustomOfferDetails(@Header("Authorization") String token,
                          @Path("id") String id);

    @GET("offer/{id}")
    Call<GetCustomOfferDetailsResponse>
    getCustomOfferDetails1(@Header("Authorization") String token,
                          @Path("id") String id);

    @DELETE("request/offer/{id}")
    Call<Object>
    deleteOffer(@Header("Authorization") String token,
                  @Path("id") String id);

    @DELETE("offer/{id}")
    Call<Object>
    deleteOffer1(@Header("Authorization") String token,
                @Path("id") String id);

    @GET("service/{id}")
    Call<GetServiceByIdResponse>
    getServiceById(@Header("Authorization") String token,
                   @Path("id") String id);

    @GET("service/{id}")
    Call<GetServiceInfoResponse>
    getServiceInfo(@Header("Authorization") String token,
                   @Path("id") String id);

    @PUT("service/{id}")
    Call<Object>
    updateService(@Header("Authorization") String token,
                  @Body AddServiceRequest params,
                  @Path("id") String id);

    @DELETE("service/{id}")
    Call<Object>
    deleteService(@Header("Authorization") String token,
                  @Path("id") String id);

    @GET("conversations")
    Call<GetConversationsResponse>
    getConversations(@Header("Authorization") String token,
                     @Query("lt_id") String id);

    @GET("conversation/{id}/messages")
    Call<GetChatHistoryResponse>
    getChatHistory(@Header("Authorization") String token,
                   @Path("id") String id,
                   @Query("lt_id") String lt_id);



    @GET("services")
    Call<GetServicesReponse>
    getServicesByUserId(@Header("Authorization") String token,
                        @Query("userId") String id );

    @GET("services")
    Call<GetServicesReponse>
    getServices(@Header("Authorization") String token);

    @GET("orders/sell")
    Call<GetSellOrdersResponse>
    getSellOrder(@Header("Authorization") String token,
                 @Query("lt_id") String id);

    @GET("orders/buy")
    Call<GetBuyOrderResponse>
    getBuyOrder(@Header("Authorization") String token,
                @Query("lt_id") String id);

    @GET("offers/sent")
    Call<GetSentOfferResponse>
    getSentOffers(@Header("Authorization") String token,
                  @Query("lt_id") String id);

    @GET("offers/received")
    Call<GetReceivedOffersResponse>
    getReceivedOffer(@Header("Authorization") String token,
                     @Query("lt_id") String id);

    @GET("jobs")
    Call<GetSellJobsResponse>
    getSellJobs(@Header("Authorization") String token,
                @Query("lt_id") String id);

    @GET("requests")
    Call<GetLiveOfferRequestsResponse>
    getLiveOfferRequests(@Header("Authorization") String token,
                         @Query("lt_id") String id);

    @GET("request/{id}")
    Call<ExploreJobsModel>
    getJobsDetail(@Header("Authorization") String token,
                         @Path("id") String id);

    @GET("service/{id}/like")
    Call<LikeUnlikeResponse>
    getLikeService(@Header("Authorization") String token,
                   @Path("id") String id);

    @GET("service/{id}/watch")
    Call<WatchUnwatchResponse>
    getWatchService(@Header("Authorization") String token,
                    @Path("id") String id);

    @GET("service/{id}/share")
    Call<ShareUnshareResponse>
    getShareService(@Header("Authorization") String token,
                    @Path("id") String id);

    @POST("service/{id}/like")
    Call<Object>
    postLikeService(@Header("Authorization") String token,
                    @Path("id") String id);

    @DELETE("service/{id}/like")
    Call<Object>
    delLikeService(@Header("Authorization") String token,
                   @Path("id") String id);

    @POST("service/{id}/watch")
    Call<Object>
    postWatchService(@Header("Authorization") String token,
                     @Path("id") String id);

    @POST("service/{id}/flag-inappropriate")
    Call<Object>
    postInappropriate(@Header("Authorization") String token,
                     @Path("id") String id,
                      @Body FlagInappropriateRequest request);

    @DELETE("service/{id}/watch")
    Call<Object>
    delWatchService(@Header("Authorization") String token,
                    @Path("id") String id);

    @POST("service/{id}/share")
    Call<Object>
    postShareService(@Header("Authorization") String token,
                     @Path("id") String id);

    @DELETE("service/{id}/share")
    Call<Object>
    delShareService(@Header("Authorization") String token,
                    @Path("id") String id);

    @GET("services/watching")
    Call<GetServicesReponse>
    getWatchingServices(@Header("Authorization") String token,
                        @Query("lt_id") String id);

    @GET("services/liked")
    Call<GetServicesReponse>
    getLikedServices(@Header("Authorization") String token,
                     @Query("lt_id") String id);


    @GET("service/{id}/detail")
    Call<GetServiceDetailResponse>
    getServiceDetail(@Header("Authorization") String token,
                     @Path("id") String id);

    @GET("service/{id}/related")
    Call<GetRelatedServicesResponse>
    getRelatedService(@Header("Authorization") String token,
                      @Path("id") String id,
                      @QueryMap Map<String, Integer> params);

    @GET("services")
    Call<GetSendServicesResponse>
    getSendServices(@Header("Authorization") String token,
                    @Query("page") String page,
                    @Query("lt_id") String id);


    //********************************************************//
    //                      Search APIs                       //
    //********************************************************//

    @GET("users,services/_search")
    Call<GetTagServiceSellerResponse>
    getSearchTagServiceSeller(@QueryMap Map<String, String> params);

    @GET("post/tag/search")
    Call<List<TagServiceSellerModel>>
    getSearchTagService(@Header("Authorization") String token,
                        @QueryMap Map<String, String> params);

    @GET("conversations/search")
    Call<SearchConversationsResponse>
    getSearchConversations(@Header("Authorization") String token,
                           @QueryMap Map<String, String> params);

    @GET("services/_search")
    Call<SearchSendServicesResponse>
    getSearchSendServices(@QueryMap Map<String, String> params);
}

