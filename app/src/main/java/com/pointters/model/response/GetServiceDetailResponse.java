package com.pointters.model.response;

import com.pointters.model.ServiceDetailModel;

/**
 * Created by prashantkumar on 14/11/17.
 */

public class GetServiceDetailResponse {

    private ServiceDetailModel result;

    public ServiceDetailModel getResult() {
        return result;
    }

    public void setResult(ServiceDetailModel result) {
        this.result = result;
    }
}
