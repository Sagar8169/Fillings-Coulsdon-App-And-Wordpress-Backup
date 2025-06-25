package com.example.stockmanager.network;

public class ProductResponse {
    private int stock_quantity;
    private boolean in_stock;
    private String stock_status;

    public ProductResponse(int stock_quantity, boolean in_stock) {
        this.stock_quantity = stock_quantity;
        this.in_stock = in_stock;
    }

    public ProductResponse(String stock_status) {
        this.stock_status = stock_status;
    }

    public int getStock_quantity() {
        return stock_quantity;
    }

    public boolean isIn_stock() {
        return in_stock;
    }

    public String getStock_status() {
        return stock_status;
    }
}
