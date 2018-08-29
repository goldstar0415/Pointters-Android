package com.pointters.model.response;

import com.pointters.model.CommentsModel;

import java.util.ArrayList;

/**
 * Created by prashantkumar on 9/11/17.
 */

public class CommentsResponse {
    private Integer total;
    private Integer limit;
    private Integer page;
    private Integer pages;
    private String lastDocId;
    private ArrayList<CommentsModel> docs;

    public CommentsResponse() {
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

    public ArrayList<CommentsModel> getDocs() {
        return docs;
    }

    public void setDocs(ArrayList<CommentsModel> docs) {
        this.docs = docs;
    }
}
