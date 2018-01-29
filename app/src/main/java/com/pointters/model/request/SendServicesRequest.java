package com.pointters.model.request;

import java.util.List;

/**
 * Created by mac on 12/22/17.
 */

public class SendServicesRequest {

    private String fromUserId;
    private List<String> toUserId;
    private String serviceId;

    public SendServicesRequest(String fromId, List<String> toId, String serviceId) {
        this.fromUserId = fromId;
        this.toUserId = toId;
        this.serviceId = serviceId;
    }
}
