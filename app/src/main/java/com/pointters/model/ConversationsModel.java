package com.pointters.model;

import java.util.List;

/**
 * Created by mac on 12/21/17.
 */

public class ConversationsModel {

    private String _id;
    private String conversationTitle;
    private Integer countNewMessages;
    private MessageModel lastMessage;
    private List<UserChatModel> users;


    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getConversationTitle() {
        return conversationTitle;
    }

    public void setConversationTitle(String conversationTitle) {
        this.conversationTitle = conversationTitle;
    }

    public Integer getCountNewMessages() {
        return countNewMessages;
    }

    public void setCountNewMessages(Integer countNewMessages) {
        this.countNewMessages = countNewMessages;
    }

    public MessageModel getLastMessage() { return lastMessage; }

    public void setLastMessage(MessageModel lastMessage) {
        this.lastMessage = lastMessage;
    }

    public List<UserChatModel> getUsers() {
        return users;
    }

    public void setUsers(List<UserChatModel> users) {
        this.users = users;
    }
}
