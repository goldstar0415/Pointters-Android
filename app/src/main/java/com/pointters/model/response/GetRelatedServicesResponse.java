package com.pointters.model.response;

import com.pointters.model.ServiceDetailModel;

import java.util.List;

/**
 * Created by prashantkumar on 15/11/17.
 */

public class GetRelatedServicesResponse {

    private List<ServiceDetailModel> docs;
    private int total;
    private int limit;
    private int page;

    public List<ServiceDetailModel> getDocs() {
        return docs;
    }

    public void setDocs(List<ServiceDetailModel> docs) {
        this.docs = docs;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
