package com.pointters.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;

/**
 * Created by mac on 1/5/18.
 */

public class SocketManager {

    private static SocketManager mInstance = null;
    private io.socket.client.Socket mSocket;


    public static SocketManager getInstance() {
        if (mInstance == null) {
            Class clazz = SocketManager.class;
            synchronized (clazz) {
                mInstance = new SocketManager();
            }
        }
        return mInstance;
    }

    public void init(String token) {
        try {
            if (token == null)
                return;
            IO.Options options = new IO.Options();
            options.forceNew = false;
            options.reconnection = true;
            options.query = "token=" + token;
            String strUrl = "http://pointters-api-dev3.us-east-1.elasticbeanstalk.com:9000";
            mSocket = IO.socket(strUrl, options);
            mSocket.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public io.socket.client.Socket getSocket() {
        return mSocket;
    }

    public boolean isConnected() {
        if (mSocket == null)
            return false;
        return mSocket.connected();
    }

    public void close() {
        if (mSocket == null)
            return;
        mSocket.disconnect();
        mSocket.close();
    }

    public void setStartConversation(String convId, JSONArray users) {
        if (!mSocket.connected())
            return;

        JSONObject object = new JSONObject();
        try {
            if (!convId.isEmpty()) {
                object.put("conversationId", convId);
            }
            object.put("users", users);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSocket.emit("start_conversation", object);
    }

    public void setJoinLiveOfferRoom(String requestId) {
        if (!mSocket.connected())
            return;

        JSONObject object = new JSONObject();
        try {
            if (!requestId.isEmpty()) {
                object.put("requestId", requestId);
            }
//            object.put("users", users);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSocket.emit("join_live_offer_room", object);
    }

    public void sendMessage(JSONObject data) {
        mSocket.emit("message", data);
    }

    public void disconnectSocket() {
        if (!mSocket.connected()) {
            return;
        }

        mSocket.off();
        mSocket.disconnect();
    }
}
