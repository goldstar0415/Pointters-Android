package com.pointters.model.response;

import com.pointters.model.LiveOfferRequestsModel;

import java.util.List;

/**
 * Created by prashant on 21-11-2017.
 */

public class GetLiveOfferRequestsResponse {
    private List<LiveOfferRequestsModel> docs;
    private Integer total ;
    private Integer limit;
    private Integer page;
    private Integer pages;

    public List<LiveOfferRequestsModel> getDocs() {
        return docs;
    }

    public void setDocs(List<LiveOfferRequestsModel> docs) {
        this.docs = docs;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }
}
