package com.example.stockmanager;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.stockmanager.network.RetrofitClient;
import com.google.gson.JsonObject;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VariableStock extends AppCompatActivity {

    private void setupToggle(Switch toggle, int productId, String productName) {
        // Temporarily remove listener
        toggle.setOnCheckedChangeListener(null);

        // Fetch real-time stock status
        RetrofitClient.getApiService().getStockStatus(productId).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean inStock = response.body().get("in_stock").getAsBoolean();

                    toggle.setChecked(inStock);
                    if (inStock) {
                        toggle.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.stock_in)));
                        toggle.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.stock_track_in)));
                    } else {
                        toggle.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.stock_out)));
                        toggle.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.stock_track_out)));
                    }

                    // Now set listener
                    toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (isChecked) {
                            toggle.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.stock_in)));
                            toggle.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.stock_track_in)));
                        } else {
                            toggle.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.stock_out)));
                            toggle.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.stock_track_out)));
                        }

                        handleToggle(productId, productName, isChecked, toggle);
                    });
                } else {
                    Toast.makeText(VariableStock.this, "❌ Failed to load stock status for " + productName, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(VariableStock.this, "⚠️ Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void handleToggle(int productId, String productName, boolean inStock, Switch toggle) {
        // Set color again (in case toggle is manually flipped)
        if (inStock) {
            toggle.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.stock_in)));
            toggle.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.stock_track_in)));
        } else {
            toggle.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.stock_out)));
            toggle.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.stock_track_out)));
        }

        Call<ResponseBody> call = inStock
                ? RetrofitClient.getApiService().markInStock(productId)
                : RetrofitClient.getApiService().markOutOfStock(productId);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String msg = inStock
                            ? productName + " is in stock ✅"
                            : productName + " is out of stock ❌";
                    Toast.makeText(VariableStock.this, msg, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(VariableStock.this,
                            "❌ Failed (" + response.code() + "): " + productName,
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(VariableStock.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_variable_stock);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        setupToggle(findViewById(R.id.toggle_7200), 7200, "Veg Biryani");
        setupToggle(findViewById(R.id.toggle_7196), 7196, "Lamb Biryani");
        setupToggle(findViewById(R.id.toggle_7195), 7195, "Chicken Dum Biryani");
        setupToggle(findViewById(R.id.toggle_3662), 3662, "Butter Chicken");
        setupToggle(findViewById(R.id.toggle_3660), 3660, "Channa Masala");
        setupToggle(findViewById(R.id.toggle_3654), 3654, "Shahi Paneer");
        setupToggle(findViewById(R.id.toggle_3652), 3652, "Karahi Chicken");
        setupToggle(findViewById(R.id.toggle_3650), 3650, "Bhuna Ghosht");
        setupToggle(findViewById(R.id.toggle_3571), 3571, "Daal Tadka");
    }
}
