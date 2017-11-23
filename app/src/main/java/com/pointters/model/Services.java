package com.pointters.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prashantkumar on 8/11/17.
 */

public class Services {
    private List<Service> docs;
    private int total;
    private int limit;
    private int page;

    public List<Service> getDocs() {
        return docs;
    }

    public void setDocs(List<Service> docs) {
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
