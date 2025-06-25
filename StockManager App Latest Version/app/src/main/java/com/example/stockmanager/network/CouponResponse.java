package com.example.stockmanager.network;

import com.google.gson.annotations.SerializedName;

public class CouponResponse {
    @SerializedName("id")
    private int id;

    @SerializedName("code")
    private String code;

    @SerializedName("amount")
    private String amount;

    @SerializedName("date_expires")
    private String dateExpires;  // ✅ Expiry date field

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getAmount() {
        return amount;
    }

    public String getDateExpires() {
        return dateExpires;
    }
}
