package com.example.stockmanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stockmanager.network.WooCommerceApi;
import com.example.stockmanager.network.RetrofitClient;
import com.example.stockmanager.network.CouponRequest;
import com.example.stockmanager.network.CouponResponse;
import com.example.stockmanager.adapters.CouponAdapter;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CouponsActivity extends AppCompatActivity {
    private EditText couponCode, discountValue, expiryDate;
    private Spinner discountType;
    private Button createCouponButton;
    private RecyclerView couponRecyclerView;
    private CouponAdapter couponAdapter;
    private WooCommerceApi api;

    private CouponResponse selectedCoupon = null; // ✅ Edit mode ke liye selected coupon
    private final String consumerKey = "ck_ff40981ff997580145f8954ad517ae9664a9cca1";
    private final String consumerSecret = "cs_3e744ecb6cb3e32ad89905f021829e2e3c30a729";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupons);

        api = RetrofitClient.getInstance().create(WooCommerceApi.class);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        couponCode = findViewById(R.id.couponCode);
        discountValue = findViewById(R.id.discountValue);
        expiryDate = findViewById(R.id.expiryDate);
        discountType = findViewById(R.id.discountType);
        createCouponButton = findViewById(R.id.createCouponButton);
        couponRecyclerView = findViewById(R.id.couponRecyclerView);

        couponRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        String[] types = {"percent", "fixed_cart"}; // ✅ Correct WooCommerce format
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, types);
        discountType.setAdapter(adapter);

        expiryDate.setOnClickListener(v -> showDatePicker());

        createCouponButton.setOnClickListener(v -> {
            String code = couponCode.getText().toString().trim();
            String type = discountType.getSelectedItem().toString();
            String value = discountValue.getText().toString().trim();
            String expiry = expiryDate.getText().toString().trim();

            if (code.isEmpty() || value.isEmpty() || expiry.isEmpty()) {
                Toast.makeText(CouponsActivity.this, "Enter all details", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedCoupon == null) {
                createCoupon(code, type, value, expiry);
            } else {
                updateCoupon(selectedCoupon.getId(), code, type, value, expiry);
            }
        });

        loadCoupons();
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, (view, year1, month1, dayOfMonth) -> {
            String selectedDate = year1 + "-" + (month1 + 1) + "-" + dayOfMonth;
            expiryDate.setText(selectedDate);
        }, year, month, day);
        datePickerDialog.show();
    }

    private void createCoupon(String code, String type, String amount, String expiry) {
        CouponRequest couponRequest = new CouponRequest(code, type, amount, expiry);
        api.createCoupon(couponRequest, consumerKey, consumerSecret)
                .enqueue(new Callback<CouponResponse>() {
                    @Override
                    public void onResponse(Call<CouponResponse> call, Response<CouponResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(CouponsActivity.this, "Coupon Created", Toast.LENGTH_LONG).show();
                            selectedCoupon = null; // ✅ Reset edit mode
                            loadCoupons();
                        } else {
                            Toast.makeText(CouponsActivity.this, "Failed to create coupon", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<CouponResponse> call, Throwable t) {
                        Toast.makeText(CouponsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateCoupon(int couponId, String code, String type, String amount, String expiry) {
        CouponRequest couponRequest = new CouponRequest(code, type, amount, expiry);
        api.updateCoupon(couponId, couponRequest, consumerKey, consumerSecret)
                .enqueue(new Callback<CouponResponse>() {
                    @Override
                    public void onResponse(Call<CouponResponse> call, Response<CouponResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(CouponsActivity.this, "Coupon Updated", Toast.LENGTH_LONG).show();
                            selectedCoupon = null; // ✅ Reset edit mode
                            createCouponButton.setText("Create Coupon"); // ✅ Reset button text
                            loadCoupons();
                        } else {
                            Toast.makeText(CouponsActivity.this, "Failed to update coupon", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<CouponResponse> call, Throwable t) {
                        Toast.makeText(CouponsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadCoupons() {
        api.getCoupons(consumerKey, consumerSecret).enqueue(new Callback<List<CouponResponse>>() {
            @Override
            public void onResponse(Call<List<CouponResponse>> call, Response<List<CouponResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    couponAdapter = new CouponAdapter(CouponsActivity.this, response.body(), new CouponAdapter.OnCouponActionListener() {
                        @Override
                        public void onDelete(int couponId) {
                            deleteCoupon(couponId);
                        }

                        @Override
                        public void onEdit(CouponResponse coupon) {
                            selectedCoupon = coupon;
                            couponCode.setText(coupon.getCode());
                            discountValue.setText(coupon.getAmount());
                            expiryDate.setText(coupon.getDateExpires());
                            createCouponButton.setText("Update Coupon"); // ✅ Change button text
                        }
                    });
                    couponRecyclerView.setAdapter(couponAdapter);
                } else {
                    Toast.makeText(CouponsActivity.this, "Failed to load coupons", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CouponResponse>> call, Throwable t) {
                Toast.makeText(CouponsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteCoupon(int couponId) {
        api.deleteCoupon(couponId, consumerKey, consumerSecret).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CouponsActivity.this, "Coupon Deleted", Toast.LENGTH_SHORT).show();
                    loadCoupons();
                } else {
                    Toast.makeText(CouponsActivity.this, "Failed to delete coupon", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(CouponsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
