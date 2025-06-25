package com.example.stockmanager.network;

public class CouponRequest {
    private String code;
    private String discount_type;
    private String amount;

    private String date_expires;

    public CouponRequest(String code, String discount_type, String amount) {
        this.code = code;
        this.discount_type = discount_type;
        this.amount = amount;
    }

public CouponRequest(String code, String discount_type, String amount, String date_expires) {
    this.code = code;
    this.discount_type = discount_type;
    this.amount = amount;
    this.date_expires = date_expires;
}
}
