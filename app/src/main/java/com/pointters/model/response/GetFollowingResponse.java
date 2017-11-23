package com.pointters.model.response;

import com.pointters.model.FollowingModel;

import java.util.List;

/**
 * Created by prashantkumar on 11/11/17.
 */

public class GetFollowingResponse {
    private List<FollowingModel> docs;
    private int total;
    private int limit;
    private int page;
    private int pages;

    public List<FollowingModel> getDocs() {
        return docs;
    }

    public void setDocs(List<FollowingModel> docs) {
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
}
