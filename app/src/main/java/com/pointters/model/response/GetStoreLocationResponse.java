package com.pointters.model.response;

import com.pointters.model.StoreLocationModel;

import java.util.ArrayList;

public class GetStoreLocationResponse {

    private ArrayList<StoreLocationModel> docs;
    private Integer total;
    private Integer limit;
    private Integer page;
    private Integer pages;
    private String lastDocId;

    public ArrayList<StoreLocationModel> getDocs() {
        return docs;
    }

    public void setDocs(ArrayList<StoreLocationModel> docs) {
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

    public String getLastDocId() {
        return lastDocId;
    }

    public void setLastDocId(String lastDocId) {
        this.lastDocId = lastDocId;
    }
}
