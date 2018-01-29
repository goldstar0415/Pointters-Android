package com.pointters.utils;

import android.graphics.Color;

public interface ConstantUtils {

    String BASE_URL = "http://pointters-api-dev3.us-east-1.elasticbeanstalk.com:9000/";
    String SEARCH_URL = "https://search-pointters-es-dev-cn37tjlkgx574lzojxb7esinzm.us-east-1.es.amazonaws.com/";

    String API_KEY_FOR_GOOGLE_PLACES="AIzaSyDqUe8tS5yOcv53S6B3K_sYngMAv2Dtk5c";
    String APP_PREF = "appPref";
    String TOKEN_PREFIX = "Bearer ";

    int ACTION_CROP_IMAGE = 1010;
    int CROP_ASPECT_X = 1;
    int CROP_ASPECT_Y = 1;
    int CROP_IMAGE_DIMEN_X = 300;
    int CROP_IMAGE_DIMEN_Y = 300;

    int[] rectColors = {
            Color.rgb(102, 255, 178),  // light green
            Color.rgb(255, 153, 51),   // orange
            Color.rgb(178, 102, 255),  // purple
            Color.rgb(255, 102, 102),  // blood
            Color.rgb(255, 255, 102),  // yellow
            Color.rgb(255, 153, 255),  // light pink
            Color.rgb(0, 255, 0),      // dark green
            Color.rgb(255, 0, 127),    // red
            Color.rgb(102, 102, 255),  // blue
            Color.rgb(204, 0, 204)     // dark pink
    };

    String PREF_IS_LOGIN = "isLogin";
    String IS_REGISTRATION_COMPLETED="isRegistrationCompleted";
    String IS_ON_REGISTRATION_SCREEN="isOnRegistrationScreen";
    String USER_DATA="userData";
    String NOTIFICATION_DATA="notificationData";
    String FIRST_NAME="firstName";
    String LAST_NAME="lastName";
    String COMPANY_NAME="compmayName";
    String LOCATION="location";
    String PHONE_NUMBER="phone_no";
    String ABOUT_YOU="about_you";

    String PREF_TOKEN = "token";
    String PREF_USER_ID = "userId";
    String PREF_ID="prefId";
    String PREF_USER_EMAIL = "userEmail";
    String PREF_USER_FB_AUTH_TOKEN = "userFbAuthToken";
    String PREF_USER_PASSWORD = "userPassword";
    String PREF_IS_EMAIL_LOGIN = "isEmailLogin";
    String PREF_USER_DATA_MODEL = "userModel";
    String MY_BUCKET = "pointters_dev/dev";
    String AWS_ACCESS_KEY="AKIAIGALHANVPEWURBJA";
    String AWS_SECRATE_KEY="t7QqrZAe87TsZa2AW8LUWkGpxnfcXFg5Fvb85UrT";
    String SOURCE="source";
    String MENU_SCREEN="menuSceen";
    String SELECTED_TAB="selected_tab";
    String LAST_SELECTED_TAB="last_selected_tab";

    String PRICE="price";
    String DELETE="delete";
    String POSITION="position";
    String SERVICE_ID="serviceID";
    String INTENT_BUNDLE_IMAGE_VIDEO="bundleImageVideo";
    String CATEGORY_MODEL="categoryModel";
    String GROUP_POSITION="groupPosition";
    String CHILD_POSITION="childPosition";
    String CATEGORY_LIST_HEADRES="categoryListHeader";
    String CATEGORY_LIST_Child="categoryListChild";
    String CHOOSED_CATEGORY="choosed_category";

    String CHOOSE_TAG_ID = "choose_tag_id";
    String CHOOSE_TAG_TYPE = "choose_tag_type";
    String CHOOSE_TAG_NAME = "choose_tag_name";
    String CHOOSE_TAG_POS = "choose_tag_pos";
    String CHOOSE_TAG_PIC = "choose_tag_pic";

    String FOLLOW_TYPE="follow_type";
    String USER_LATITUDE="user_latitude";
    String USER_LONGITUDE="user_longitude";
    String WATCHING_LIKED_TYPE="watching_liked_type";

    String SERVICE_TYPE="service_type";
    String USER_VERIFIED="user_verified";
    String CHAT_USER_ID = "chat_user_id";
    String CHAT_USER_NAME = "chat_user_name";
    String CHAT_USER_PIC = "chat_user_pic";
    String CHAT_CONVERSATION_ID = "chat_conversation_id";

    String CHAT_SEND_SERVICE = "chat_send_service";
    String CHAT_CUSTOM_OFFER_ID = "chat_custom_offer_id";
    String CHAT_CUSTOM_OFFER_INFO = "chat_custom_offer_info";
    String CHAT_CUSTOM_OFFER_SERVICE = "chat_custom_offer_service";
    String CHAT_CUSTOM_OFFER_LINKED = "chat_custom_offer_linked";
    String CHAT_OFFER_DIRECTION = "chat_offer_direction";

    String CHAT_TAP_PHOTO = "chat_tap_photo";
    String CHAT_TAP_VIDEO = "chat_tap_video";

    String LINK_SERVICE_ID = "link_service_id";
    String LINK_SERVICE_DESC = "link_service_desc";
    String LINK_SERVICE_PIC = "link_service_pic";
    String LINK_SERVICE_INFO = "link_service_info";

    String SELECT_OFFER_ID = "select_offer_id";
    String CUSTOM_OFFER_UPDATE = "custom_offer_update";

    String PROFILE_LOGINUSER = "profile_loginuser";
    String PROFILE_USERID = "profile_userid";
}
