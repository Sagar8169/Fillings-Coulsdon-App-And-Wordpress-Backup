package com.example.stockmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.PopupMenu;


import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.stockmanager.network.Product;
import com.example.stockmanager.network.StockStatus;
import com.example.stockmanager.network.WooCommerceApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private LinearLayout productContainer;
    private WooCommerceApi apiService;
    private SharedPreferences sharedPreferences;
    private Switch storeToggle;
    private RequestQueue queue;
    private String apiUrl = "https://fillingscoulsdon.co.uk/wp-json/store/v1/status";
    private int[] productIds = {
            8818, 8799, 8781, 8780, 8779, 8778, 8719, 8718, 8716, 8715, 8714, 8713, 8712, 8711,
            8584, 8582, 8580, 7824, 7816, 7814, 7732, 7730, 7727, 7725, 7723, 7721, 7719, 7711,
            7301, 7296, 7293, 7291, 7289, 7287, 7285, 7283, 7281, 7279, 7277, 7273, 7275, 7267,
            7265, 7261, 7256, 6967, 3656, 3249, 2816, 2812, 2089
    };

    private String[] productNames = {
            "Peri Peri Chicken & Rice Box", "Poppadam’s", "Kids Falafel Wrap", "Kids Halloumi Wrap",
            "Kids Fresh Veggie Wrap", "Kids Butter Chicken & Pilav Rice", "Peri Peri Sauce Pot",
            "Green Garlic Mayo Pot", "Hot Chilli Siracha Sauce Pot", "Tamarind Sauce Pot",
            "Classic Light Mayo Sauce Pot", "Signature Sauce Pot", "Mango Chutney Sauce Pot",
            "Mint Yogurt Sauce Pot (Raita)", "Cheesy Kebab Roll (Lamb)", "Cheesy Kebab Roll (Chicken)",
            "Mixed Salad", "Channa (250ml) + Naan", "Aloo Paratha Wrap", "Keema Paratha Wrap (Breakfast)",
            "Still Water", "Mango Lassi", "Diet Coke", "Sprite", "Fanta Orange", "Coke", "Coke Zero",
            "Fanta Lemon", "Gulab Jamun (2pcs)", "Plain Fries", "Peri-Salted Fries", "Loaded fries",
            "Tandoori Chicken Tikka", "Chicken Kebab", "Naan Bread", "Paratha Bread", "Samosa Chaat",
            "Lamb Kebab", "Lamb Samosa", "Veg Samosa", "Spiced Potatoes", "Chicken Samosa", "Salad Box",
            "Hot Box", "Toasted Wraps", "Paratha", "Pilau Rice", "Naan", "Chicken Thali",
            "Lamb Thali", "Vegetable Thali"
    };

    private int[] productImages = {
            R.drawable.product1, R.drawable.product2, R.drawable.product3, R.drawable.product4,
            R.drawable.product5, R.drawable.product6, R.drawable.product7, R.drawable.product8,
            R.drawable.product9, R.drawable.product10, R.drawable.product11, R.drawable.product12,
            R.drawable.product13, R.drawable.product14, R.drawable.product15, R.drawable.product16,
            R.drawable.product17, R.drawable.product18, R.drawable.product19, R.drawable.product20,
            R.drawable.product21, R.drawable.product22, R.drawable.product23, R.drawable.product24,
            R.drawable.product25, R.drawable.product26, R.drawable.product27, R.drawable.product28,
            R.drawable.product29, R.drawable.product30, R.drawable.product31, R.drawable.product32,
            R.drawable.product33, R.drawable.product34, R.drawable.product35, R.drawable.product36,
            R.drawable.product37, R.drawable.product38, R.drawable.product39, R.drawable.product40,
            R.drawable.product41, R.drawable.product42, R.drawable.product43, R.drawable.product44,
            R.drawable.product45, R.drawable.product46,
            R.drawable.product47, R.drawable.product57, R.drawable.product58,
            R.drawable.product59, R.drawable.product60
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView menuButton = findViewById(R.id.menuButton);


        menuButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(MainActivity.this, v);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.menu, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.contactDeveloper) {
                    // Open email intent
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setData(Uri.parse("mailto:sagarmiishra8169@gmail.com")); // Replace with your email
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Support Request");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello Developer,");
                    startActivity(Intent.createChooser(emailIntent, "Send Email"));
                    return true;
                } else if (item.getItemId() == R.id.checkWebsite) {
                    // Open website
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://fillingscoulsdon.co.uk"));
                    startActivity(browserIntent);
                    return true;
                }
                return false;
            });

            popup.show();
        });


        productContainer = findViewById(R.id.productContainer);
        storeToggle = findViewById(R.id.storeToggle);
        sharedPreferences = getSharedPreferences("StockPrefs", Context.MODE_PRIVATE);
        queue = Volley.newRequestQueue(this);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://fillingscoulsdon.co.uk/wp-json/wc/v3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(WooCommerceApi.class);

        // ✅ Store Toggle Logic
        storeToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String status = isChecked ? "open" : "closed";
            storeToggle.setText(isChecked ? "Store Open" : "Store Closed");
            updateStoreStatus(status);
        });

        // ✅ Button to open Coupons Activity
        Button createCouponButton = findViewById(R.id.createCouponButton);
        createCouponButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CouponsActivity.class);
            startActivity(intent);
        });


        Button openCloseButton = findViewById(R.id.OpenCloseButton);
        openCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OpenClose.class);
                startActivity(intent);
            }
        });


        Button variableViewBtn = findViewById(R.id.variableViewBtn);
        variableViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VariableStock.class);
                startActivity(intent);
            }
        });



        // ✅ Load Products
        for (int i = 0; i < productIds.length; i++) {
            addProduct(productIds[i], productNames[i], productImages[i]);
        }

        getStoreStatus();
    }




    private void updateStoreStatus(String status) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("status", status);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, apiUrl, requestBody,
                    response -> Toast.makeText(this, "Store status updated to " + status, Toast.LENGTH_SHORT).show(),
                    error -> Toast.makeText(this, "Failed to update store status!", Toast.LENGTH_SHORT).show()) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            queue.add(request);
        } catch (JSONException e) {
            Toast.makeText(this, "Error creating JSON request!", Toast.LENGTH_SHORT).show();
        }
    }

    private void addProduct(int productId, String productName, int productImageResId) {
        LinearLayout productLayout = new LinearLayout(this);
        productLayout.setOrientation(LinearLayout.HORIZONTAL);
        productLayout.setPadding(16, 16, 16, 16);
        productLayout.setBackgroundResource(R.drawable.card_background);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, 20);
        productLayout.setLayoutParams(layoutParams);

        ImageView productImage = new ImageView(this);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(250, 250);
        imageParams.setMargins(0, 0, 16, 0);
        productImage.setLayoutParams(imageParams);
        Glide.with(this).load(productImageResId).into(productImage);

        LinearLayout textAndToggleLayout = new LinearLayout(this);
        textAndToggleLayout.setOrientation(LinearLayout.HORIZONTAL);
        textAndToggleLayout.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView productNameText = new TextView(this);
        productNameText.setText(productName);
        productNameText.setTextSize(15);
        productNameText.setPadding(5, 80, 0, 16);
        productNameText.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        Switch stockStatusSwitch = new Switch(this);
        stockStatusSwitch.setText("In Stock");
        stockStatusSwitch.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        boolean lastStatus = sharedPreferences.getBoolean("product_" + productId, true);
        stockStatusSwitch.setChecked(lastStatus);
        updateToggleColor(stockStatusSwitch); // 👈 App open hone par color set karo



        stockStatusSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("product_" + productId, isChecked).apply();
            String status = isChecked ? "instock" : "outofstock";

            // ✅ Change Switch Color based on Status
            if (isChecked) {
                stockStatusSwitch.setText("In Stock");
                stockStatusSwitch.getThumbDrawable().setTint(getResources().getColor(R.color.black, null));
                stockStatusSwitch.getTrackDrawable().setTint(getResources().getColor(R.color.light_text, null));
            } else {
                stockStatusSwitch.setText("Out of Stock");
                stockStatusSwitch.getThumbDrawable().setTint(getResources().getColor(R.color.red, null));
                stockStatusSwitch.getTrackDrawable().setTint(getResources().getColor(R.color.light_red, null));
            }

            updateStockStatus(productId, status);
            updateToggleColor(stockStatusSwitch); // 👈 Color update function call karo

        });


        textAndToggleLayout.addView(productNameText);
        textAndToggleLayout.addView(stockStatusSwitch);

        productLayout.addView(productImage);
        productLayout.addView(textAndToggleLayout);

        productContainer.addView(productLayout);
    }


    private void updateToggleColor(Switch toggle) {
        if (toggle.isChecked()) {
            toggle.setText("In Stock");
            toggle.getThumbDrawable().setTint(getResources().getColor(R.color.black, null));
            toggle.getTrackDrawable().setTint(getResources().getColor(R.color.light_text, null));
        } else {
            toggle.setText("Out of Stock");
            toggle.getThumbDrawable().setTint(getResources().getColor(R.color.red, null));
            toggle.getTrackDrawable().setTint(getResources().getColor(R.color.light_red, null));
        }
    }



    private void updateStockStatus(int productId, String status) {
        String consumerKey = "ck_ff40981ff997580145f8954ad517ae9664a9cca1";
        String consumerSecret = "cs_3e744ecb6cb3e32ad89905f021829e2e3c30a729";

        StockStatus stockStatus = new StockStatus(status);

        apiService.updateProductStock(productId, stockStatus, consumerKey, consumerSecret)
                .enqueue(new Callback<Product>() {
                    @Override
                    public void onResponse(Call<Product> call, Response<Product> response) {
                        if (response.isSuccessful()) {
                            Product product = response.body();
                            if (product != null && "variable".equals(product.getType())) {
                                fetchAndUpdateVariations(product.getId(), status);
                            } else {
                                Toast.makeText(MainActivity.this, "Stock Status Updated", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to update stock status", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Product> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void fetchAndUpdateVariations(int parentProductId, String status) {
        String consumerKey = "ck_ff40981ff997580145f8954ad517ae9664a9cca1";
        String consumerSecret = "cs_3e744ecb6cb3e32ad89905f021829e2e3c30a729";

        apiService.getProductVariations(parentProductId, consumerKey, consumerSecret)
                .enqueue(new Callback<List<Product>>() {
                    @Override
                    public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                        if (response.isSuccessful()) {
                            List<Product> variations = response.body();
                            if (variations != null) {
                                for (Product variation : variations) {
                                    updateStockStatus(variation.getId(), status);
                                }
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Failed to fetch variations", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Product>> call, Throwable t) {
                        Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }






    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.contactDeveloper) {
            // Open Email App
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"sagarmiishra8169@gmail.com"});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Support Request");

            if (emailIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(Intent.createChooser(emailIntent, "Send Email"));
            } else {
                Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show();
            }
            return true;
        }


        else if (id == R.id.checkWebsite) {
            // Open Website
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://fillingscoulsdon.co.uk"));

            if (browserIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(browserIntent);
            } else {
                Toast.makeText(this, "No browser found", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getStoreStatus() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, apiUrl, null,
                response -> {
                    try {
                        String status = response.getString("status");
                        storeToggle.setChecked(status.equals("open"));
                        storeToggle.setText(status.equals("open") ? "Store Open" : "Store Closed");
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error parsing store status!", Toast.LENGTH_SHORT).show();
                    }
                }, error -> Toast.makeText(this, "Failed to fetch store status!", Toast.LENGTH_SHORT).show());

        queue.add(request);
    }
}