package com.pointters.model.response;

import com.pointters.model.ServiceDetailModel;

import java.util.List;

/**
 * Created by prashantkumar on 15/11/17.
 */

public class GetRelatedServicesResponse {
    private List<ServiceDetailModel> docs;

    public List<ServiceDetailModel> getDocs() {
        return docs;
    }

    public void setDocs(List<ServiceDetailModel> docs) {
        this.docs = docs;
    }
}
