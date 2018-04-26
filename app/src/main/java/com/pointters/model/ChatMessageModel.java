package com.pointters.model;

/**
 * Created by mac on 1/5/18.
 */

public class ChatMessageModel {

    private ChatMsgModel message;
    private UserChatModel user;


    public ChatMsgModel getMessage() {
        return message;
    }

    public void setMessage(ChatMsgModel message) {
        this.message = message;
    }

    public UserChatModel getUser() {
        return user;
    }

    public void setUser(UserChatModel user) {
        this.user = user;
    }
}
