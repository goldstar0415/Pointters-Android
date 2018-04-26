package com.pointters.model.response;

import com.pointters.model.TransactionModel;

import java.util.ArrayList;

public class GetTransactionHistoryResponse {

    private String currencyCode;
    private String currencySymbol;
    private String totalPurchases;
    private String totalActiveOrderPurchases;
    private String totalCompletedOrderPurchases;
    private String totalPersonalBalance;
    private String totalSalesEarning;
    private String lastDocId;
    private Integer total;
    private Integer limit;
    private Integer page;
    private Integer pages;
    private ArrayList<TransactionModel> docs;


    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getTotalPurchases() {
        return totalPurchases;
    }

    public void setTotalPurchases(String totalPurchases) {
        this.totalPurchases = totalPurchases;
    }

    public String getTotalActiveOrderPurchases() {
        return totalActiveOrderPurchases;
    }

    public void setTotalActiveOrderPurchases(String totalActiveOrderPurchases) {
        this.totalActiveOrderPurchases = totalActiveOrderPurchases;
    }

    public String getTotalCompletedOrderPurchases() {
        return totalCompletedOrderPurchases;
    }

    public void setTotalCompletedOrderPurchases(String totalCompletedOrderPurchases) {
        this.totalCompletedOrderPurchases = totalCompletedOrderPurchases;
    }

    public String getTotalPersonalBalance() {
        return totalPersonalBalance;
    }

    public void setTotalPersonalBalance(String totalPersonalBalance) {
        this.totalPersonalBalance = totalPersonalBalance;
    }

    public String getTotalSalesEarning() {
        return totalSalesEarning;
    }

    public void setTotalSalesEarning(String totalSalesEarning) {
        this.totalSalesEarning = totalSalesEarning;
    }

    public String getLastDocId() {
        return lastDocId;
    }

    public void setLastDocId(String lastDocId) {
        this.lastDocId = lastDocId;
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

    public ArrayList<TransactionModel> getDocs() {
        return docs;
    }

    public void setDocs(ArrayList<TransactionModel> docs) {
        this.docs = docs;
    }
}
