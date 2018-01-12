package com.pointters.rest;


import com.pointters.model.ResetPasswordModel;
import com.pointters.model.request.AddServiceRequest;
import com.pointters.model.request.PostUpdateRequest;
import com.pointters.model.request.SendCustomOfferRequest;
import com.pointters.model.request.SendServicesRequest;
import com.pointters.model.request.UserEmailLoginRequest;
import com.pointters.model.request.UserEmailSignUpRequest;
import com.pointters.model.request.UserFacebookLoginRequest;
import com.pointters.model.request.UserPutRequest;
import com.pointters.model.request.UserPutSettingsRequest;
import com.pointters.model.response.FollowUnfollowResponse;
import com.pointters.model.response.GetBuyOrderResponse;
import com.pointters.model.response.GetCategoryResponse;
import com.pointters.model.response.GetChatHistoryResponse;
import com.pointters.model.response.GetConversationsResponse;
import com.pointters.model.response.GetCustomOfferDetailsResponse;
import com.pointters.model.response.GetFollowersResponse;
import com.pointters.model.response.GetFollowingResponse;
import com.pointters.model.response.GetLiveOfferRequestsResponse;
import com.pointters.model.response.GetReceivedOffersResponse;
import com.pointters.model.response.GetRelatedServicesResponse;
import com.pointters.model.response.GetSellJobsResponse;
import com.pointters.model.response.GetSellOrdersResponse;
import com.pointters.model.response.GetSendServicesResponse;
import com.pointters.model.response.GetSentOfferResponse;
import com.pointters.model.response.GetServiceByIdResponse;
import com.pointters.model.response.GetServiceDeatilResponse;
import com.pointters.model.response.GetServicesReponse;
import com.pointters.model.response.GetTagServiceSellerResponse;
import com.pointters.model.response.ResponsePutUser;
import com.pointters.model.response.SearchConversationsResponse;
import com.pointters.model.response.SearchSendServicesResponse;
import com.pointters.model.response.UserEmailLoginResponse;
import com.pointters.model.response.UserEmailSignUpResponse;
import com.pointters.model.response.UserFacebookLoginResponse;

import java.util.Map;

import retrofit2.Call;
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

    @PUT("user")
    Call<ResponsePutUser>
    putUser(@Header("Authorization") String token,
            @Body UserPutRequest params);


    @POST("user/logout")
    Call<Object>
    logOut(@Header("Authorization") String token);

    @PUT("user/setting")
    Call<Object>
    putUserSettings(@Header("Authorization") String token,
                    @Body UserPutSettingsRequest params);


    @POST("service")
    Call<Object>
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

    @GET("user/{id}/follow")
    Call<FollowUnfollowResponse>
    getfollowedUser(@Header("Authorization") String token,
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




    @POST("offer")
    Call<GetCustomOfferDetailsResponse>
    sendCustomOffers(@Header("Authorization") String token,
                     @Body SendCustomOfferRequest params);

    @PUT("offer/{id}")
    Call<Object>
    updateCustomOffers(@Header("Authorization") String token,
                       @Path("id") String id,
                       @Body SendCustomOfferRequest params);

    @GET("offer/{id}")
    Call<GetCustomOfferDetailsResponse>
    getCustomOfferDetails(@Header("Authorization") String token,
                          @Path("id") String id);

    @GET("service/{id}")
    Call<GetServiceByIdResponse>
    getServiceById(@Header("Authorization") String token,
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



    @GET("service/{id}/like")
    Call<Object>
    GetLikeService(@Header("Authorization") String token,
                @Path("id") String id);

    @GET("service/{id}/watch")
    Call<Object>
    GetWatchService(@Header("Authorization") String token,
                   @Path("id") String id);

    @POST("service/{id}/like")
    Call<Object>
    likeService(@Header("Authorization") String token,
               @Path("id") String id);

    @DELETE("service/{id}/like")
    Call<Object>
    UnlikeService(@Header("Authorization") String token,
                  @Path("id") String id);

    @DELETE("service/{id}/watch")
    Call<Object>
    watchService(@Header("Authorization") String token,
                 @Path("id") String id);

    @DELETE("service/{id}/watch")
    Call<Object>
    UnwatchService(@Header("Authorization") String token,
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
    Call<GetServiceDeatilResponse>
    getServiceDetail(@Header("Authorization") String token,
                     @Path("id") String id);

    @GET("service/{id}/related")
    Call<GetRelatedServicesResponse>
    getRelatedService(@Header("Authorization") String token,
                      @Path("id") String id);

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

    @GET("conversations/search")
    Call<SearchConversationsResponse>
    getSearchConversations(@Header("Authorization") String token,
                           @QueryMap Map<String, String> params);

    @GET("services/_search")
    Call<SearchSendServicesResponse>
    getSearchSendServices(@QueryMap Map<String, String> params);
}

