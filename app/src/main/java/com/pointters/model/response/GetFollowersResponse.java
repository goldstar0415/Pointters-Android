package com.pointters.model.response;

import com.pointters.model.FollowersModel;

import java.util.List;

/**
 * Created by prashantkumar on 11/11/17.
 */

public class GetFollowersResponse {
    private List<FollowersModel> docs;
    private int total;
    private int limit;
    private int page;
    private int pages;
    private String lastDocId;

    public List<FollowersModel> getDocs() {
        return docs;
    }

    public void setDocs(List<FollowersModel> docs) {
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

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public String getLastDocId() { return lastDocId; }

    public void setLastDocId(String lastDocId) { this.lastDocId = lastDocId; }
}
