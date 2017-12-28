package com.pointters.model.response;

import com.pointters.model.SellJobsModel;

import java.util.List;

/**
 * Created by mac on 12/5/17.
 */

public class GetSellJobsResponse {
    private List<SellJobsModel> docs;
    private Integer total ;
    private Integer limit;
    private Integer page;
    private Integer pages;
    private String lastDocId;

    public List<SellJobsModel> getDocs() {
        return docs;
    }

    public void setDocs(List<SellJobsModel> docs) {
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

    public String getLastDocId() { return lastDocId; }

    public void setLastDocId(String lastDocId) { this.lastDocId = lastDocId; }
}
